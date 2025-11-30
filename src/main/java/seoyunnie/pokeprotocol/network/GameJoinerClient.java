package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import seoyunnie.pokeprotocol.network.message.HandshakeRequest;
import seoyunnie.pokeprotocol.network.message.HandshakeResponse;
import seoyunnie.pokeprotocol.network.message.Message;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public class GameJoinerClient extends GameClient {
    private static final int PORT = 8082;

    private final Peer host;

    public GameJoinerClient(boolean isBroadcasting, InetAddress hostAddr, int hostPort) throws SocketException {
        super(isBroadcasting, PORT);

        this.host = new Peer(hostAddr, hostPort);
    }

    public Peer getHost() {
        return host;
    }

    public GameJoinerClient(String hostAddrName, int hostPort) throws UnknownHostException, SocketException {
        this(false, InetAddress.getByName(hostAddrName), hostPort);
    }

    public GameJoinerClient(int hostPort) throws SocketException {
        this(true, NetworkUtils.getAddress().get(), hostPort);
    }

    @Override
    protected void sendMessage(Message msg) throws IOException {
        sendMessage(msg, host.address(), host.port());
    }

    @Override
    protected boolean sendReliableMessage(Message msg) throws IOException {
        return sendReliableMessage(msg, host.address(), host.port());
    }

    @Override
    protected void sendACK(int seqNum) throws IOException {
        sendACK(seqNum, host);
    }

    public boolean connectToHost() throws IOException {
        sendMessage(new HandshakeRequest());

        return HandshakeResponse.decode(receiveBlockingPacket()).isPresent();
    }
}
