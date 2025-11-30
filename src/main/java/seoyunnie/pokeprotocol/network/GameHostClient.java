package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import seoyunnie.pokeprotocol.network.message.HandshakeRequest;
import seoyunnie.pokeprotocol.network.message.HandshakeResponse;
import seoyunnie.pokeprotocol.network.message.Message;
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
}
