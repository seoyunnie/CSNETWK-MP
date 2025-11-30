package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import seoyunnie.pokeprotocol.network.message.ACK;
import seoyunnie.pokeprotocol.network.message.Message;

public abstract class Client {
    protected static final int TIMEOUT_MS = 500;
    protected static final int MAX_RETRIES = 3;

    protected static final int BUFFER_SIZE = 4096;

    protected final DatagramSocket socket;

    protected final AtomicInteger sequenceNumber = new AtomicInteger(1);

    protected Client(boolean isBroadcasting, int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.setReuseAddress(isBroadcasting);
        socket.setBroadcast(isBroadcasting);
    }

    protected void sendMessage(Message msg, InetAddress destAddr, int destPort) throws IOException {
        byte[] buff = msg.encode();

        socket.send(new DatagramPacket(buff, buff.length, destAddr, destPort));
    }

    protected boolean sendReliableMessage(Message msg, InetAddress destAddr, int destPort) throws IOException {
        byte[] buff = msg.encode();
        var packet = new DatagramPacket(buff, buff.length, destAddr, destPort);

        int attempts;

        for (attempts = 0; attempts < MAX_RETRIES; attempts++) {
            socket.send(packet);

            try {
                if (ACK.decode(receivePacket()).isPresent()) {
                    break;
                }
            } catch (SocketTimeoutException e) {
            }
        }

        return attempts <= MAX_RETRIES;
    }

    protected void sendACK(int seqNum, Peer peer) throws IOException {
        sendMessage(new ACK(seqNum), peer.address(), peer.port());
    }

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

    public void close() {
        socket.close();
    }
}
