package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import seoyunnie.pokeprotocol.network.message.HandshakeRequest;
import seoyunnie.pokeprotocol.network.message.HandshakeResponse;

public class GamePeerClient extends GameClient {
    private static final int PORT = 8082;

    private final Peer host;

    public GamePeerClient(String hostAddrName, int hostPort) throws UnknownHostException, SocketException {
        super(PORT);

        this.host = new Peer(InetAddress.getByName(hostAddrName), hostPort);
    }

    @Override
    protected void sendMessage(Object msg) throws IOException {
        sendMessage(msg, host.address(), host.port());
    }

    @Override
    protected boolean sendTimedMessage(Object msg) throws IOException {
        return sendTimedMessage(msg, host.address(), host.port());
    }

    @Override
    protected void sendACK(int seqNum) throws IOException {
        sendACK(seqNum, host.address(), host.port());
    }

    public boolean connectToHost() throws IOException {
        sendMessage(new HandshakeRequest());

        return receiveHandshakeResponse().isPresent();
    }

    public Optional<HandshakeResponse> receiveHandshakeResponse() throws IOException {
        return HandshakeResponse.fromPacket(receiveBlockingPacket());
    }
}
