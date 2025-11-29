package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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
import seoyunnie.pokeprotocol.network.message.ResolutionRequest;
import seoyunnie.pokeprotocol.pokemon.Pokemon;

public abstract class GameClient {
    protected static final int TIMEOUT_MS = 500;
    protected static final int MAX_RETRIES = 3;

    protected static final int BUFFER_SIZE = 4096;

    protected final CommunicationMode communicationMode;
    protected final DatagramSocket socket;

    protected final AtomicInteger sequenceNumber = new AtomicInteger(1);

    protected GameClient(boolean isBroadcasting, int port) throws SocketException {
        this.communicationMode = isBroadcasting ? CommunicationMode.BROADCAST : CommunicationMode.P2P;
        this.socket = new DatagramSocket(port);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.setReuseAddress(isBroadcasting);
        socket.setBroadcast(isBroadcasting);
    }

    protected GameClient(boolean isBroadcasting) throws SocketException {
        this.communicationMode = isBroadcasting ? CommunicationMode.BROADCAST : CommunicationMode.P2P;
        this.socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);
        socket.setReuseAddress(isBroadcasting);
        socket.setBroadcast(isBroadcasting);
    }

    protected void sendMessage(Object msg, InetAddress addr, int port) throws IOException {
        byte[] buff = msg.toString().getBytes();

        socket.send(new DatagramPacket(buff, buff.length, addr, port));
    }

    protected abstract void sendMessage(Object msg) throws IOException;

    protected boolean sendTimedMessage(Object msg, InetAddress addr, int port) throws IOException {
        byte[] buff = msg.toString().getBytes();
        var packet = new DatagramPacket(buff, buff.length, addr, port);

        int attempts;

        for (attempts = 0; attempts < MAX_RETRIES; attempts++) {
            socket.send(packet);

            try {
                if (ACK.fromPacket(receivePacket()).isPresent()) {
                    break;
                }
            } catch (SocketTimeoutException e) {
            }
        }

        return attempts <= MAX_RETRIES;
    }

    protected abstract boolean sendTimedMessage(Object msg) throws IOException;

    protected void sendACK(int seqNum, InetAddress addr, int port) throws IOException {
        sendMessage(new ACK(seqNum), addr, port);
    }

    protected abstract void sendACK(int seqNum) throws IOException;

    protected DatagramPacket receivePacket() throws IOException {
        var buff = new byte[BUFFER_SIZE];
        var packet = new DatagramPacket(buff, buff.length);

        socket.receive(packet);

        return packet;
    }

    protected DatagramPacket receiveBlockingPacket() throws IOException {
        var buff = new byte[BUFFER_SIZE];
        var packet = new DatagramPacket(buff, buff.length);

        socket.setSoTimeout(0);

        socket.receive(packet);

        socket.setSoTimeout(TIMEOUT_MS);

        return packet;
    }

    public void sendBattleSetup(Pokemon pokemon, StatBoosts statBoosts) throws IOException {
        sendMessage(new BattleSetup(CommunicationMode.P2P, pokemon.name(), statBoosts));
    }

    public Optional<BattleSetup> receiveBattleSetup() throws IOException {
        return BattleSetup.fromPacket(receiveBlockingPacket());
    }

    public boolean announceAttack(Move move) throws IOException {
        return sendTimedMessage(new AttackAnnounce(move.name(), sequenceNumber.getAndIncrement()));
    }

    public Optional<AttackAnnounce> receiveAttackAnnouncement() throws IOException {
        var attackAnnouncement = AttackAnnounce.fromPacket(receiveBlockingPacket()).orElse(null);

        if (attackAnnouncement == null) {
            return Optional.empty();
        }

        sendACK(attackAnnouncement.sequenceNumber());

        return Optional.of(attackAnnouncement);
    }

    public boolean announceDefense() throws IOException {
        return sendTimedMessage(new DefenseAnnounce(sequenceNumber.getAndIncrement()));
    }

    public boolean receiveDefenseAnnouncement() throws IOException {
        var defenseAnnouncement = DefenseAnnounce.fromPacket(receiveBlockingPacket()).orElse(null);

        if (defenseAnnouncement == null) {
            return false;
        }

        sendACK(defenseAnnouncement.sequenceNumber());

        return true;
    }

    public boolean sendCalculationReport(BattlePokemon pokemon, Move moveUsed, int damageDealt, int defenderHP,
            String msg) throws IOException {
        return sendTimedMessage(new CalculationReport(pokemon.getName(), moveUsed.name(), pokemon.getCurrentHP(),
                damageDealt, defenderHP, msg, sequenceNumber.getAndIncrement()));
    }

    public Optional<CalculationReport> receiveCalculationReport() throws IOException {
        var calculationReport = CalculationReport.fromPacket(receiveBlockingPacket()).orElse(null);

        if (calculationReport == null) {
            return Optional.empty();
        }

        sendACK(calculationReport.sequenceNumber());

        return Optional.of(calculationReport);
    }

    public boolean sendCalculationConfirmation() throws IOException {
        return sendTimedMessage(new CalculationConfirm(sequenceNumber.getAndIncrement()));
    }

    public void sendResolutionRequest(BattlePokemon pokemon, Move moveUsed, int damageDealt, int defenderHP)
            throws IOException {
        sendMessage(new ResolutionRequest(pokemon.getName(), moveUsed.name(), damageDealt, defenderHP,
                sequenceNumber.getAndIncrement()));
    }

    public Optional<Object> receiveCalculationConfirmation() throws IOException {
        DatagramPacket packet = receiveBlockingPacket();

        var calculationConfirmation = CalculationConfirm.fromPacket(packet).orElse(null);

        if (calculationConfirmation != null) {
            sendACK(calculationConfirmation.sequenceNumber());

            return Optional.of(calculationConfirmation);
        }

        var resolutionReq = ResolutionRequest.fromPacket(packet).orElse(null);

        if (resolutionReq != null) {
            return Optional.of(resolutionReq);
        }

        return Optional.empty();
    }

    public void sendResolutionConfirmation(ResolutionRequest resolutionReq) throws IOException {
        sendACK(resolutionReq.sequenceNumber());
    }

    public boolean receiveResolutionConfirmation() throws IOException {
        return ACK.fromPacket(receiveBlockingPacket()).isPresent();
    }

    public boolean sendGameOver(BattlePokemon winningPokemon, BattlePokemon losingPokemon) throws IOException {
        return sendTimedMessage(
                new GameOver(winningPokemon.getName(), losingPokemon.getName(), sequenceNumber.getAndIncrement()));
    }

    public Optional<GameOver> receiveGameOver() throws IOException {
        var gameOver = GameOver.fromPacket(receiveBlockingPacket()).orElse(null);

        if (gameOver == null) {
            return Optional.empty();
        }

        sendACK(gameOver.sequenceNumber());

        return Optional.of(gameOver);
    }

    public void close() {
        socket.close();
    }
}
