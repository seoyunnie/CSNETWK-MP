package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seoyunnie.pokeprotocol.game.StatBoosts;
import seoyunnie.pokeprotocol.network.message.ACK;
import seoyunnie.pokeprotocol.network.message.AttackAnnounce;
import seoyunnie.pokeprotocol.network.message.BattleSetup;
import seoyunnie.pokeprotocol.network.message.CalculationConfirm;
import seoyunnie.pokeprotocol.network.message.CalculationReport;
import seoyunnie.pokeprotocol.network.message.DefenseAnnounce;
import seoyunnie.pokeprotocol.network.message.GameOver;
import seoyunnie.pokeprotocol.network.message.HandshakeRequest;
import seoyunnie.pokeprotocol.network.message.HandshakeResponse;
import seoyunnie.pokeprotocol.network.message.Message;
import seoyunnie.pokeprotocol.network.message.ResolutionRequest;
import seoyunnie.pokeprotocol.network.message.SpectatorRequest;
import seoyunnie.pokeprotocol.pokemon.Pokemon;

public class GameHostClient extends GameClient {
    public static final int PORT = 8081;

    private boolean isListening;
    private Thread requestListenerThread;

    private Peer peer;
    private final Set<Peer> spectators = new HashSet<>();

    public GameHostClient(boolean isBroadcasting) throws SocketException {
        super(PORT, isBroadcasting);
    }

    public GameHostClient() throws SocketException {
        super(PORT, false);
    }

    public Peer getPeer() {
        return peer;
    }

    public Set<Peer> getSpectators() {
        return spectators;
    }

    @Override
    protected void sendMessage(Message msg) throws IOException {
        sendMessage(msg, peer.address(), peer.port());
    }

    @Override
    protected boolean sendReliableMessage(Message msg) throws IOException {
        return sendReliableMessage(msg, peer.address(), peer.port());
    }

    @Override
    protected void sendACK(int seqNum) throws IOException {
        sendACK(seqNum, peer);
    }

    public void startHandshake() throws IOException {
        isListening = true;
        requestListenerThread = new Thread(() -> {
            while (isListening) {
                try {
                    DatagramPacket packet = receiveBlockingPacket();

                    var peer = new Peer(packet.getAddress(), packet.getPort());

                    HandshakeRequest.decode(packet).ifPresentOrElse((r) -> {
                        try {
                            sendMessage(new HandshakeResponse(), peer.address(), peer.port());

                            this.peer = peer;

                            this.isListening = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, () -> SpectatorRequest.decode(packet).ifPresent((r) -> {
                        try {
                            sendMessage(new HandshakeResponse(), peer.address(), peer.port());

                            spectators.add(peer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        requestListenerThread.start();
    }

    public Thread getHandshakeThread() {
        return requestListenerThread;
    }

    private void forwardMessage(Message msg) throws IOException {
        for (Peer spectator : spectators) {
            if (!sendReliableMessage(msg, spectator.address(), spectator.port())) {
                spectators.remove(spectator);
            }
        }
    }

    private void forwardACK(int seqNum) throws IOException {
        for (Peer spectator : spectators) {
            sendACK(seqNum, spectator);
        }
    }

    public void sendBattleSetup(Pokemon pokemon, StatBoosts statBoosts) throws IOException {
        var battleSetup = new BattleSetup(communicationMode, pokemon.name(), statBoosts);

        sendMessage(battleSetup);
        forwardMessage(battleSetup);
    }

    public Optional<BattleSetup> receiveBattleSetup() throws IOException {
        Optional<BattleSetup> battleSetup = BattleSetup.decode(receiveBlockingPacket());

        battleSetup.ifPresent((s) -> {
            try {
                forwardMessage(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return battleSetup;
    }

    @Override
    public Optional<AttackAnnounce> receiveAttackAnnouncement() throws IOException {
        var attackAnnouncement = AttackAnnounce.decode(receiveBlockingPacket()).orElse(null);

        if (attackAnnouncement == null) {
            return Optional.empty();
        }

        sendACK(attackAnnouncement.sequenceNumber());

        forwardMessage(attackAnnouncement);

        return Optional.of(attackAnnouncement);
    }

    @Override
    public boolean receiveDefenseAnnouncement() throws IOException {
        var defenseAnnouncement = DefenseAnnounce.decode(receiveBlockingPacket()).orElse(null);

        if (defenseAnnouncement == null) {
            return false;
        }

        sendACK(defenseAnnouncement.sequenceNumber());

        forwardMessage(defenseAnnouncement);

        return true;
    }

    @Override
    public Optional<CalculationReport> receiveCalculationReport() throws IOException {
        var calculationReport = CalculationReport.decode(receiveBlockingPacket()).orElse(null);

        if (calculationReport == null) {
            return Optional.empty();
        }

        sendACK(calculationReport.sequenceNumber());

        forwardMessage(calculationReport);

        return Optional.of(calculationReport);
    }

    @Override
    public Optional<Message> receiveCalculationConfirmation() throws IOException {
        DatagramPacket packet = receiveBlockingPacket();

        var calculationConfirmation = CalculationConfirm.decode(packet).orElse(null);

        if (calculationConfirmation != null) {
            sendACK(calculationConfirmation.sequenceNumber());

            forwardMessage(calculationConfirmation);

            return Optional.of(calculationConfirmation);
        }

        var resolutionReq = ResolutionRequest.decode(packet).orElse(null);

        if (resolutionReq != null) {
            forwardMessage(resolutionReq);

            return Optional.of(resolutionReq);
        }

        return Optional.empty();
    }

    public void sendResolutionConfirmation(ResolutionRequest resolutionReq) throws IOException {
        sendACK(resolutionReq.sequenceNumber());
        forwardACK(resolutionReq.sequenceNumber());
    }

    public boolean receiveResolutionConfirmation() throws IOException {
        Optional<ACK> ack = ACK.decode(receiveBlockingPacket());

        ack.ifPresent((a) -> {
            try {
                forwardMessage(a);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        return ack.isPresent();
    }

    @Override
    public Optional<GameOver> receiveGameOver() throws IOException {
        var gameOver = GameOver.decode(receiveBlockingPacket()).orElse(null);

        if (gameOver == null) {
            return Optional.empty();
        }

        sendACK(gameOver.sequenceNumber());

        forwardMessage(gameOver);

        return Optional.of(gameOver);
    }
}
