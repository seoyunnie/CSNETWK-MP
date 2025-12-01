package seoyunnie.pokeprotocol.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;

import seoyunnie.pokeprotocol.exception.IncompatiblePeerException;
import seoyunnie.pokeprotocol.gui.GamePanel;
import seoyunnie.pokeprotocol.gui.battle.HUDPanel;
import seoyunnie.pokeprotocol.gui.battle.HUDTurnPanel;
import seoyunnie.pokeprotocol.move.GameMoves;
import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.network.GameClient;
import seoyunnie.pokeprotocol.network.GameHostClient;
import seoyunnie.pokeprotocol.network.message.AttackAnnounce;
import seoyunnie.pokeprotocol.network.message.CalculationReport;
import seoyunnie.pokeprotocol.network.message.GameOver;
import seoyunnie.pokeprotocol.network.message.Message;
import seoyunnie.pokeprotocol.network.message.ResolutionRequest;
import seoyunnie.pokeprotocol.pokemon.GamePokemon;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.type.GameTypes;
import seoyunnie.pokeprotocol.type.Type;
import seoyunnie.pokeprotocol.type.TypeEffectiveness;

public class BattleManager implements Runnable {
    private final GameClient client;

    private final BattlePokemon ownPokemon;
    private final StatBoosts ownStatBoosts;

    private final BattlePokemon enemyPokemon;
    private final StatBoosts enemyStatBoosts;

    private boolean isOwnTurn;

    private boolean isOver = false;
    private String winner;
    private String loser;

    private CountDownLatch latch = new CountDownLatch(1);
    private Move selectedMove;
    private TypeEffectiveness selectedMoveEffectiveness;

    private final HUDPanel hudPanel;
    private final HUDTurnPanel hudTurnPanel;
    private final GamePanel gamePanel;

    public BattleManager(GameClient client, boolean isSpectating, Pokemon ownPokemon, StatBoosts ownStatBoosts,
            Pokemon enemyPokemon, StatBoosts enemyStatBoosts) {
        this.client = client;

        this.ownPokemon = new BattlePokemon(ownPokemon);
        this.ownStatBoosts = ownStatBoosts;

        this.enemyPokemon = new BattlePokemon(enemyPokemon);
        this.enemyStatBoosts = enemyStatBoosts;

        this.isOwnTurn = client instanceof GameHostClient;

        this.hudPanel = new HUDPanel();
        this.hudTurnPanel = new HUDTurnPanel(this.ownPokemon.getName(), getMoveEffectivenessMap());
        this.gamePanel = new GamePanel(this.ownPokemon, this.enemyPokemon,
                isSpectating ? hudPanel : isOwnTurn ? hudTurnPanel : hudPanel);

        hudTurnPanel.setMoveButtonListener((move, effectiveness) -> {
            this.selectedMove = move;
            this.selectedMoveEffectiveness = effectiveness;

            latch.countDown();
        });
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    private TypeEffectiveness getMoveEffectiveness(Move move) {
        Map<Type, Float> effectivenessMap = GameTypes.EFFECTIVENESS_MAP.get(move.type());

        float effectiveness = 1;

        if (enemyPokemon.isDualTyped()) {
            if (effectivenessMap.containsKey(enemyPokemon.getFirstType())) {
                effectiveness = effectivenessMap.get(enemyPokemon.getFirstType()).floatValue();
            }

            if (effectivenessMap.containsKey(enemyPokemon.getSecondType()) && effectiveness != 0f) {
                effectiveness *= effectivenessMap.get(enemyPokemon.getSecondType()).floatValue();
            }
        } else if (effectivenessMap.containsKey(enemyPokemon.getFirstType())) {
            effectiveness = effectivenessMap.get(enemyPokemon.getFirstType()).floatValue();
        }

        if (effectiveness == 0f) {
            return TypeEffectiveness.NO_EFFECT;
        } else if (effectiveness <= 0.5f) {
            return TypeEffectiveness.NOT_VERY_EFFECTIVE;
        } else if (effectiveness >= 2f) {
            return TypeEffectiveness.SUPER_EFFECTIVE;
        }

        return TypeEffectiveness.NORMAL;
    }

    private Map<Move, TypeEffectiveness> getMoveEffectivenessMap() {
        var effectivenessByMove = new HashMap<Move, TypeEffectiveness>();

        for (Move move : ownPokemon.getMoves()) {
            effectivenessByMove.put(move, getMoveEffectiveness(move));
        }

        return effectivenessByMove;
    }

    private int calculateMoveDamage(Move move, Pokemon attackingPokemon, Pokemon defendingPokemon) {
        Map<Type, Float> effectivenessMap = GameTypes.EFFECTIVENESS_MAP.get(move.type());

        return (int) (move.power()
                * (move.isSpecial() ? attackingPokemon.stats().specialAttack() : attackingPokemon.stats().attack())
                * effectivenessMap.getOrDefault(defendingPokemon.firstType(), 1f).floatValue()
                * (defendingPokemon.secondType() != null
                        ? effectivenessMap.getOrDefault(defendingPokemon.secondType(), 1f).floatValue()
                        : 1))
                / (move.isSpecial() ? defendingPokemon.stats().specialAttack() : defendingPokemon.stats().defense());
    }

    private void attack() throws IOException, IllegalStateException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!client.announceAttack(selectedMove)) {
            throw new IOException();
        }

        if (!client.receiveDefenseAnnouncement()) {
            throw new IOException();
        }

        int damageDealt = calculateMoveDamage(selectedMove, ownPokemon.getBasePokemon(), enemyPokemon.getBasePokemon());

        int enemyCurrHP = enemyPokemon.getCurrentHP() - damageDealt;
        boolean isKnockedOut = enemyCurrHP <= 0;

        String ownMsg = ownPokemon.getName() + " used " + selectedMove.name() + "!";
        String enemyMsg = "The opposing " + ownPokemon.getName() + " used " + selectedMove.name() + "!";
        String effectivenessMsg = switch (selectedMoveEffectiveness) {
            case TypeEffectiveness.NO_EFFECT -> "  It doesn't affect " + enemyPokemon.getName() + "!";
            case TypeEffectiveness.NOT_VERY_EFFECTIVE -> "  It's not very effective...";
            case TypeEffectiveness.NORMAL -> "";
            case TypeEffectiveness.SUPER_EFFECTIVE -> "  It's super effective!";
        };

        if (!client.sendCalculationReport(ownPokemon, selectedMove, damageDealt, isKnockedOut ? 0 : enemyCurrHP,
                enemyMsg + effectivenessMsg)) {
            throw new IOException();
        }

        Message calculationConfirmation = client.receiveCalculationConfirmation().orElse(null);

        if (calculationConfirmation == null) {
            throw new IOException();
        } else if (calculationConfirmation instanceof ResolutionRequest resolutionReq) {
            damageDealt = calculateMoveDamage(selectedMove, ownPokemon.getBasePokemon(), enemyPokemon.getBasePokemon());

            enemyCurrHP = enemyPokemon.getCurrentHP() - damageDealt;
            isKnockedOut = enemyCurrHP <= 0;

            if (resolutionReq.damageDealt() != damageDealt
                    || resolutionReq.defenderHPRemaining() != (isKnockedOut ? 0 : enemyCurrHP)) {
                throw new IllegalStateException();
            }

            client.sendResolutionConfirmation(resolutionReq);
        }

        enemyPokemon.decreaseHP(isKnockedOut ? enemyPokemon.getCurrentHP() : damageDealt);

        gamePanel.repaint();

        gamePanel.setHUDPanel(hudPanel);
        hudPanel.setMessage(ownMsg + effectivenessMsg);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (enemyPokemon.getCurrentHP() == 0) {
            hudPanel.setMessage("The opposing " + enemyPokemon.getName() + " fainted!");

            if (!client.sendGameOver(ownPokemon, enemyPokemon)) {
                throw new IOException();
            }

            this.isOver = true;
            this.winner = ownPokemon.getName();
            this.loser = enemyPokemon.getName();

            return;
        }

        selectedMove = null;

        this.latch = new CountDownLatch(1);
    }

    private void defend() throws IOException, IncompatiblePeerException, IllegalStateException {
        hudPanel.setMessage("Waiting for opponent...");

        AttackAnnounce attackAnnouncement = client.receiveAttackAnnouncement().orElseThrow(
                () -> new IOException());

        if (!client.announceDefense()) {
            throw new IOException();
        }

        Move enemyMove = GameMoves.getByName(attackAnnouncement.moveName()).orElseThrow(
                () -> new IncompatiblePeerException("'" + attackAnnouncement.moveName() + "' is not implemented"));

        int damageDealt = calculateMoveDamage(enemyMove, enemyPokemon.getBasePokemon(), ownPokemon.getBasePokemon());

        int ownCurrHP = ownPokemon.getCurrentHP() - damageDealt;
        boolean isKnockedOut = ownCurrHP <= 0;

        CalculationReport calculationReport = client.receiveCalculationReport().orElseThrow(() -> new IOException());

        if (calculationReport.damageDealt() != damageDealt) {
            client.sendResolutionRequest(enemyPokemon, enemyMove, damageDealt, isKnockedOut ? 0 : ownCurrHP);

            if (!client.receiveResolutionConfirmation()) {
                throw new IllegalStateException();
            }
        }

        if (!client.sendCalculationConfirmation()) {
            throw new IOException();
        }

        ownPokemon.decreaseHP(isKnockedOut ? ownPokemon.getCurrentHP() : damageDealt);

        gamePanel.repaint();

        hudPanel.setMessage(calculationReport.statusMessage());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ownPokemon.getCurrentHP() == 0) {
            GameOver gameOver = client.receiveGameOver().orElseThrow(() -> new IOException());

            hudPanel.setMessage(gameOver.loser() + " fainted!");

            this.isOver = true;
            this.winner = gameOver.winner();
            this.loser = gameOver.loser();

            return;
        }

        gamePanel.setHUDPanel(hudTurnPanel);
    }

    @Override
    public void run() {
        try {
            while (!isOver) {
                if (isOwnTurn) {
                    attack();
                } else {
                    defend();
                }

                isOwnTurn = !isOwnTurn;
            }

            JOptionPane.showConfirmDialog(gamePanel, winner + " defeated " + loser + "!", "Match Over",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A client had been disconnected.", "Clients Disconnected",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IncompatiblePeerException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A peer is using an application with a different implementation.",
                    "Invalid Peer Client", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A peer's client has gotten out of sync.", "Clients Out of Sync",
                    JOptionPane.ERROR_MESSAGE);
        }

        client.close();
    }

    public void spectate() {
        try {
            boolean isFirstRun = true;

            BattlePokemon hostPokemon = null;
            BattlePokemon joinerPokemon = null;

            while (!isOver) {
                hudPanel.setMessage("Waiting...");

                AttackAnnounce attackAnnouncement = client.receiveAttackAnnouncement()
                        .orElseThrow(() -> new IOException());

                GameMoves.getByName(attackAnnouncement.moveName()).orElseThrow(() -> new IncompatiblePeerException(
                        "'" + attackAnnouncement.moveName() + "' is not implemented"));

                if (!client.receiveDefenseAnnouncement()) {
                    throw new IOException();
                }

                CalculationReport calculationReport = client.receiveCalculationReport()
                        .orElseThrow(() -> new IOException());

                Message calculationConfirmation = client.receiveCalculationConfirmation().orElse(null);

                if (calculationConfirmation == null) {
                    throw new IOException();
                } else if (calculationConfirmation instanceof ResolutionRequest
                        && !client.receiveResolutionConfirmation()) {
                    throw new IllegalStateException();
                }

                if (isFirstRun) {
                    for (Pokemon pokemon : GamePokemon.values()) {
                        if (pokemon.stats().hp() - calculationReport.damageDealt() == calculationReport
                                .defenderHPRemaining()) {
                            if (ownPokemon.getBasePokemon().equals(pokemon)) {
                                joinerPokemon = ownPokemon;
                                hostPokemon = enemyPokemon;
                            } else {
                                joinerPokemon = enemyPokemon;
                                hostPokemon = ownPokemon;
                            }

                            break;
                        }
                    }

                    if (hostPokemon == null || joinerPokemon == null) {
                        throw new IllegalStateException();
                    }

                    isFirstRun = false;
                }

                int enemyCurrHP = (isOwnTurn ? hostPokemon : joinerPokemon).getCurrentHP()
                        - calculationReport.damageDealt();
                boolean isKnockedOut = enemyCurrHP <= 0;

                (isOwnTurn ? hostPokemon : joinerPokemon)
                        .decreaseHP(isKnockedOut ? (isOwnTurn ? hostPokemon : joinerPokemon).getCurrentHP()
                                : calculationReport.damageDealt());

                gamePanel.repaint();

                hudPanel.setMessage(calculationReport.statusMessage().replace("The opposing ", ""));

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (hostPokemon.getCurrentHP() == 0 || joinerPokemon.getCurrentHP() == 0) {
                    GameOver gameOver = client.receiveGameOver().orElseThrow(() -> new IOException());

                    hudPanel.setMessage(gameOver.loser() + " fainted!");

                    this.isOver = true;
                    this.winner = gameOver.winner();
                    this.loser = gameOver.loser();
                }

                isOwnTurn = !isOwnTurn;
            }

            JOptionPane.showConfirmDialog(gamePanel, winner + " defeated " + loser + "!", "Match Over",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A client had been disconnected.", "Clients Disconnected",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IncompatiblePeerException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A peer is using an application with a different implementation.",
                    "Invalid Peer Client", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A peer's client has gotten out of sync.", "Clients Out of Sync",
                    JOptionPane.ERROR_MESSAGE);
        }

        client.close();
    }
}
