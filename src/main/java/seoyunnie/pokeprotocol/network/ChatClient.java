package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import seoyunnie.pokeprotocol.network.message.ChatMessage;
import seoyunnie.pokeprotocol.sticker.Sticker;

public class ChatClient {
    private static final int PORT = 8080;

    private static final int BUFFER_SIZE = 2048;
    private static final int MAX_CHUNK_SIZE = 1300;

    private final InetAddress broadcastAddress;
    private final DatagramSocket socket;

    private boolean isTesting;

    private boolean isListening;
    private Thread messageListenerThread;

    private final String username;
    private final AtomicInteger sequenceNumber = new AtomicInteger(1);

    public ChatClient(InetAddress broadcastAddr, String username) throws SocketException {
        this.broadcastAddress = broadcastAddr;

        DatagramSocket tempSocket;

        try {
            tempSocket = new DatagramSocket(PORT);

            this.isTesting = false;
        } catch (SocketException e) {
            tempSocket = new DatagramSocket(PORT - 1);

            this.isTesting = true;
        }

        this.socket = tempSocket;
        socket.setReuseAddress(true);
        socket.setBroadcast(true);

        this.username = username;
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void sendChatMessage(String chatMsg) throws IOException {
        byte[] buff = new ChatMessage(username, chatMsg, sequenceNumber.getAndIncrement()).toString().getBytes();

        socket.send(new DatagramPacket(buff, buff.length, broadcastAddress, PORT));
    }

    public void sendSticker(Sticker sticker) throws IOException {
        byte[] buff = new ChatMessage(username, sticker, sequenceNumber.getAndIncrement()).toString().getBytes();

        int packetCnt = (int) Math.ceil(buff.length / (double) MAX_CHUNK_SIZE);

        String msgId = UUID.randomUUID().toString();

        for (int i = 0; i < packetCnt; i++) {
            int start = i * MAX_CHUNK_SIZE;
            int len = Math.min(MAX_CHUNK_SIZE, buff.length - start);

            var chunk = new byte[len + 200];

            String header = msgId + " | " + i + " | " + packetCnt + "\n";
            byte[] headerBytes = header.getBytes();

            System.arraycopy(headerBytes, 0, chunk, 0, headerBytes.length);
            System.arraycopy(buff, start, chunk, headerBytes.length, len);

            socket.send(new DatagramPacket(chunk, headerBytes.length + len, broadcastAddress, PORT));
        }
    }

    public void startChatMessageListener(Consumer<ChatMessage> cb) {
        isListening = true;
        messageListenerThread = new Thread(() -> {
            var receivedChunks = new HashMap<String, Map<Integer, byte[]>>();
            var chunkCounts = new HashMap<String, Integer>();

            var buff = new byte[BUFFER_SIZE];

            while (isListening) {
                try {
                    var packet = new DatagramPacket(buff, buff.length);

                    socket.receive(packet);

                    String data = new String(packet.getData(), packet.getOffset(), packet.getLength());

                    if (data.contains("content_type: TEXT")) {
                        ChatMessage.fromPacket(packet).ifPresent((m) -> cb.accept(m));

                        continue;
                    }

                    int sepIdx = data.indexOf("\n");
                    String header = data.substring(0, sepIdx);
                    String body = data.substring(sepIdx + 1);

                    String[] headerMetadata = header.split(" \\| ");
                    String msgId = headerMetadata[0];
                    int idx = Integer.parseInt(headerMetadata[1]);
                    int total = Integer.parseInt(headerMetadata[2]);

                    receivedChunks.putIfAbsent(msgId, new HashMap<>());
                    chunkCounts.putIfAbsent(msgId, total);

                    receivedChunks.get(msgId).put(idx, body.getBytes());

                    if (receivedChunks.get(msgId).size() == total) {
                        StringBuilder strBuilder = new StringBuilder();

                        for (int i = 0; i < total; i++) {
                            strBuilder.append(new String(receivedChunks.get(msgId).get(i)));
                        }

                        ChatMessage.fromString(strBuilder).ifPresent((m) -> cb.accept(m));

                        receivedChunks.remove(msgId);
                        chunkCounts.remove(msgId);
                    }
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
