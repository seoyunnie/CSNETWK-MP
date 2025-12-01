package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Optional;

import seoyunnie.pokeprotocol.game.BattlePokemon;
import seoyunnie.pokeprotocol.game.StatBoosts;
import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.network.message.ACK;
import seoyunnie.pokeprotocol.network.message.AttackAnnounce;
import seoyunnie.pokeprotocol.network.message.BattleSetup;
import seoyunnie.pokeprotocol.network.message.CalculationConfirm;
import seoyunnie.pokeprotocol.network.message.CalculationReport;
import seoyunnie.pokeprotocol.network.message.DefenseAnnounce;
import seoyunnie.pokeprotocol.network.message.GameOver;
import seoyunnie.pokeprotocol.network.message.Message;
import seoyunnie.pokeprotocol.network.message.ResolutionRequest;
import seoyunnie.pokeprotocol.pokemon.Pokemon;

public abstract class GameClient extends Client {
    protected final CommunicationMode communicationMode;

    protected GameClient(int port, boolean isBroadcasting) throws SocketException {
        super(port, isBroadcasting);

        this.communicationMode = isBroadcasting ? CommunicationMode.BROADCAST : CommunicationMode.P2P;
    }

    public int getPort() {
        return socket.getPort();
    }

    protected abstract void sendMessage(Message msg) throws IOException;

    protected abstract boolean sendReliableMessage(Message msg) throws IOException;

    protected abstract void sendACK(int seqNum) throws IOException;

    public void sendBattleSetup(Pokemon pokemon, StatBoosts statBoosts) throws IOException {
        sendMessage(new BattleSetup(communicationMode, pokemon.name(), statBoosts));
    }

    public Optional<BattleSetup> receiveBattleSetup() throws IOException {
        return BattleSetup.decode(receiveBlockingPacket());
    }

    public boolean announceAttack(Move move) throws IOException {
        return sendReliableMessage(new AttackAnnounce(move.name(), sequenceNumber.getAndIncrement()));
    }

    public Optional<AttackAnnounce> receiveAttackAnnouncement() throws IOException {
        var attackAnnouncement = AttackAnnounce.decode(receiveBlockingPacket()).orElse(null);

        if (attackAnnouncement == null) {
            return Optional.empty();
        }

        sendACK(attackAnnouncement.sequenceNumber());

        return Optional.of(attackAnnouncement);
    }

    public boolean announceDefense() throws IOException {
        return sendReliableMessage(new DefenseAnnounce(sequenceNumber.getAndIncrement()));
    }

    public boolean receiveDefenseAnnouncement() throws IOException {
        var defenseAnnouncement = DefenseAnnounce.decode(receiveBlockingPacket()).orElse(null);

        if (defenseAnnouncement == null) {
            return false;
        }

        sendACK(defenseAnnouncement.sequenceNumber());

        return true;
    }

    public boolean sendCalculationReport(BattlePokemon pokemon, Move moveUsed, int damageDealt, int defenderHP,
            String msg) throws IOException {
        return sendReliableMessage(new CalculationReport(pokemon.getName(), moveUsed.name(), pokemon.getCurrentHP(),
                damageDealt, defenderHP, msg, sequenceNumber.getAndIncrement()));
    }

    public Optional<CalculationReport> receiveCalculationReport() throws IOException {
        var calculationReport = CalculationReport.decode(receiveBlockingPacket()).orElse(null);

        if (calculationReport == null) {
            return Optional.empty();
        }

        sendACK(calculationReport.sequenceNumber());

        return Optional.of(calculationReport);
    }

    public boolean sendCalculationConfirmation() throws IOException {
        return sendReliableMessage(new CalculationConfirm(sequenceNumber.getAndIncrement()));
    }

    public void sendResolutionRequest(BattlePokemon pokemon, Move moveUsed, int damageDealt, int defenderHP)
            throws IOException {
        sendMessage(new ResolutionRequest(pokemon.getName(), moveUsed.name(), damageDealt, defenderHP,
                sequenceNumber.getAndIncrement()));
    }

    public Optional<Message> receiveCalculationConfirmation() throws IOException {
        DatagramPacket packet = receiveBlockingPacket();

        var calculationConfirmation = CalculationConfirm.decode(packet).orElse(null);

        if (calculationConfirmation != null) {
            sendACK(calculationConfirmation.sequenceNumber());

            return Optional.of(calculationConfirmation);
        }

        var resolutionReq = ResolutionRequest.decode(packet).orElse(null);

        if (resolutionReq != null) {
            return Optional.of(resolutionReq);
        }

        return Optional.empty();
    }

    public void sendResolutionConfirmation(ResolutionRequest resolutionReq) throws IOException {
        sendACK(resolutionReq.sequenceNumber());
    }

    public boolean receiveResolutionConfirmation() throws IOException {
        return ACK.decode(receiveBlockingPacket()).isPresent();
    }

    public boolean sendGameOver(BattlePokemon winningPokemon, BattlePokemon losingPokemon) throws IOException {
        return sendReliableMessage(
                new GameOver(winningPokemon.getName(), losingPokemon.getName(), sequenceNumber.getAndIncrement()));
    }

    public Optional<GameOver> receiveGameOver() throws IOException {
        var gameOver = GameOver.decode(receiveBlockingPacket()).orElse(null);

        if (gameOver == null) {
            return Optional.empty();
        }

        sendACK(gameOver.sequenceNumber());

        return Optional.of(gameOver);
    }
}
