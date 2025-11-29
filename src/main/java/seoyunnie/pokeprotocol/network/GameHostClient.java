package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.network.message.AttackAnnounce;
import seoyunnie.pokeprotocol.network.message.HandshakeRequest;
import seoyunnie.pokeprotocol.network.message.HandshakeResponse;
import seoyunnie.pokeprotocol.network.message.SpectatorRequest;

public class GameHostClient extends GameClient {
    public static final int PORT = 8081;

    private boolean isListening;
    private Thread requestListenerThread;

    private Peer peer;
    private final Set<Peer> spectators = new HashSet<>();

    public GameHostClient(boolean isBroadcasting) throws SocketException {
        super(isBroadcasting, PORT);
    }

    public GameHostClient() throws SocketException {
        super(false, PORT);
    }

    public void startHandshake() throws IOException {
        isListening = true;
        requestListenerThread = new Thread(() -> {
            while (isListening) {
                try {
                    DatagramPacket packet = receiveBlockingPacket();

                    var peer = new Peer(packet.getAddress(), packet.getPort());

                    HandshakeRequest.fromPacket(packet).ifPresentOrElse((r) -> {
                        try {
                            sendMessage(new HandshakeResponse(12345), peer.address(), peer.port());

                            this.peer = peer;

                            this.isListening = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                            () -> SpectatorRequest.fromPacket(packet).ifPresent((r) -> spectators.add(peer)));
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

    @Override
    protected void sendMessage(Object msg) throws IOException {
        sendMessage(msg, peer.address(), peer.port());
    }

    @Override
    protected boolean sendTimedMessage(Object msg) throws IOException {
        return sendTimedMessage(msg, peer.address(), peer.port());
    }

    @Override
    protected void sendACK(int seqNum) throws IOException {
        sendACK(seqNum, peer.address(), peer.port());
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
}
