package seoyunnie.pokeprotocol.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;

import seoyunnie.pokeprotocol.gui.BattleFrame;
import seoyunnie.pokeprotocol.gui.battle.HUDPanel;
import seoyunnie.pokeprotocol.gui.battle.HUDTurnPanel;
import seoyunnie.pokeprotocol.move.GameMoves;
import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.network.GameClient;
import seoyunnie.pokeprotocol.network.GameHostClient;
import seoyunnie.pokeprotocol.network.message.AttackAnnounce;
import seoyunnie.pokeprotocol.network.message.CalculationReport;
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

    private CountDownLatch latch = new CountDownLatch(1);
    private Move selectedMove;
    private TypeEffectiveness selectedMoveEffectiveness;

    private final HUDPanel hudPanel;
    private final HUDTurnPanel hudTurnPanel;
    private final BattleFrame frame;

    public BattleManager(GameClient client, Pokemon ownPokemon, StatBoosts ownStatBoosts, Pokemon enemyPokemon,
            StatBoosts enemyStatBoosts) {
        this.client = client;

        this.ownPokemon = new BattlePokemon(ownPokemon);
        this.ownStatBoosts = ownStatBoosts;

        this.enemyPokemon = new BattlePokemon(enemyPokemon);
        this.enemyStatBoosts = enemyStatBoosts;

        this.isOwnTurn = client instanceof GameHostClient;

        this.hudPanel = new HUDPanel();
        this.hudTurnPanel = new HUDTurnPanel(this.ownPokemon.getName(), getMoveEffectivenessMap());
        this.frame = new BattleFrame(this.ownPokemon, this.enemyPokemon, isOwnTurn ? hudTurnPanel : hudPanel);

        hudTurnPanel.addMoveButtonListener((move, effectiveness) -> {
            this.selectedMove = move;
            this.selectedMoveEffectiveness = effectiveness;

            latch.countDown();
        });
    }

    public BattleFrame getFrame() {
        return frame;
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
                * (move.isSpecial()
                        ? attackingPokemon.stats().specialAttack()
                        : attackingPokemon.stats().attack())
                * effectivenessMap.getOrDefault(defendingPokemon.firstType(), 1f).floatValue()
                * (defendingPokemon.secondType() != null
                        ? effectivenessMap.getOrDefault(defendingPokemon.secondType(), 1f).floatValue()
                        : 1))
                / (move.isSpecial()
                        ? defendingPokemon.stats().specialAttack()
                        : defendingPokemon.stats().defense());
    }

    private void attack() throws IOException {
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
        boolean isKOd = enemyCurrHP <= 0;

        String ownMsg = enemyPokemon.getName() + " used " + selectedMove.name() + "!";
        String enemyMsg = "The opposing " + enemyPokemon.getName() + " used " + selectedMove.name() + "!";
        String effectivenessMsg = switch (selectedMoveEffectiveness) {
            case TypeEffectiveness.NO_EFFECT -> "  It doesn't affect " + enemyPokemon.getName() + "!";
            case TypeEffectiveness.NOT_VERY_EFFECTIVE -> "  It's not very effective...";
            case TypeEffectiveness.NORMAL -> "";
            case TypeEffectiveness.SUPER_EFFECTIVE -> "  It's super effective!";
        };

        client.sendCalculationReport(
                ownPokemon, selectedMove,
                damageDealt, isKOd ? 0 : enemyCurrHP,
                enemyMsg + effectivenessMsg);

        enemyPokemon.decreaseHP(isKOd ? enemyPokemon.getCurrentHP() : damageDealt);

        frame.repaint();

        frame.setHUDPanel(hudPanel);
        hudPanel.setMessage(ownMsg + effectivenessMsg);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        selectedMove = null;

        this.latch = new CountDownLatch(1);
    }

    private void defend() throws IOException {
        hudPanel.setMessage("");

        AttackAnnounce attackAnnouncement = client.receiveAttackAnnouncement().orElseThrow(
                () -> new IOException());

        if (!client.announceDefense()) {
            throw new IOException();
        }

        Move enemyMove = GameMoves.getByName(attackAnnouncement.moveName()).get();

        int damageDealt = calculateMoveDamage(enemyMove, enemyPokemon.getBasePokemon(), ownPokemon.getBasePokemon());

        int ownCurrHP = ownPokemon.getCurrentHP() - damageDealt;
        boolean isKOd = ownCurrHP <= 0;

        CalculationReport calculationReport = client.receiveCalculationReport().orElseThrow(() -> new IOException());

        if (calculationReport.damageDealt() != damageDealt) {
            client.sendResolutionRequest(enemyPokemon, enemyMove, damageDealt, isKOd ? 0 : ownCurrHP);
        }

        ownPokemon.decreaseHP(isKOd ? ownPokemon.getCurrentHP() : damageDealt);

        frame.repaint();

        // if (!client.sendCalculationConfirmation()) {
        // throw new IOException();
        // }

        hudPanel.setMessage(calculationReport.statusMessage());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        frame.setHUDPanel(hudTurnPanel);
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
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "You, or your, opponent had been disconnected.", "Disconnected",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "The peer is using an application with a different implementation.", "Invalid Peer",
                    JOptionPane.ERROR_MESSAGE);
        }

        frame.dispose();

        client.close();
    }
}
