package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import seoyunnie.pokeprotocol.network.message.ChatMessage;

public class ChatClient {
    private static final int PORT = 8080;

    private static final int BUFFER_SIZE = 2048;

    private final InetAddress broadcastAddress;
    private final DatagramSocket socket;

    private boolean isListening;
    private Thread messageListenerThread;

    private final String username;
    private final AtomicInteger sequenceNumber = new AtomicInteger(1);

    public ChatClient(InetAddress broadcastAddr, String username) throws SocketException {
        this.broadcastAddress = broadcastAddr;

        DatagramSocket tempSocket;

        try {
            tempSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            tempSocket = new DatagramSocket(PORT - 1);
        }

        this.socket = tempSocket;
        socket.setReuseAddress(true);
        socket.setBroadcast(true);

        this.username = username;
    }

    public void sendChatMessage(String chatMsg) throws IOException {
        byte[] buff = new ChatMessage(
                username,
                ChatMessage.ContentType.TEXT, chatMsg,
                sequenceNumber.getAndIncrement()).toString().getBytes();

        socket.send(new DatagramPacket(buff, buff.length, broadcastAddress, PORT));
    }

    public void startChatMessageListener(Consumer<ChatMessage> cb) {
        isListening = true;
        messageListenerThread = new Thread(() -> {
            var buff = new byte[BUFFER_SIZE];

            while (isListening) {
                try {
                    var packet = new DatagramPacket(buff, buff.length);

                    socket.receive(packet);

                    ChatMessage.fromPacket(packet).ifPresent((m) -> cb.accept(m));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        messageListenerThread.setDaemon(true);

        messageListenerThread.start();
    }

    public void stopChatMessageListener() {
        isListening = false;

        socket.close();
    }
}
