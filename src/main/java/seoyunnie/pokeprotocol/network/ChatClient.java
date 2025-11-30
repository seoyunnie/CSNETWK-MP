package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import seoyunnie.pokeprotocol.network.message.ChatMessage;
import seoyunnie.pokeprotocol.sticker.Sticker;

public class ChatClient extends Client {
    public static final int PORT = 8080;

    private static final int MAX_CHUNK_SIZE = 1300;

    private boolean isListening;
    private Thread messageListenerThread;

    private final String username;

    public ChatClient(boolean isBroadcasting, String username) throws SocketException {
        super(isBroadcasting, ChatClient.PORT);

        this.username = username;
    }

    public void sendChatMessage(ChatMessage chatMsg, Peer peer) throws IOException {
        sendReliableMessage(chatMsg, peer.address(), PORT);
    }

    public void sendChatMessage(String chatMsg, Peer peer) throws IOException {
        sendChatMessage(new ChatMessage(username, chatMsg, sequenceNumber.getAndIncrement()), peer);
    }

    public void sendSticker(ChatMessage chatMsg, Peer peer) throws IOException {
        byte[] buff = chatMsg.encode();

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

            socket.send(new DatagramPacket(chunk, headerBytes.length + len, peer.address(), PORT));
        }
    }

    public void sendSticker(Sticker sticker, Peer peer) throws IOException {
        sendSticker(new ChatMessage(username, sticker, sequenceNumber.getAndIncrement()), peer);
    }

    public void startChatMessageListener(BiConsumer<ChatMessage, Peer> cb) {
        isListening = true;
        messageListenerThread = new Thread(() -> {
            var receivedChunks = new HashMap<String, Map<Integer, byte[]>>();
            var chunkCounts = new HashMap<String, Integer>();

            while (isListening) {
                try {
                    DatagramPacket packet = receiveBlockingPacket();

                    var data = new String(packet.getData(), packet.getOffset(), packet.getLength());

                    Consumer<ChatMessage> handleMessage = (m) -> {
                        var peer = new Peer(packet.getAddress(), packet.getPort());

                        if (!peer.address().equals(socket.getInetAddress())) {
                            try {
                                sendACK(m.sequenceNumber(), peer);

                                cb.accept(m, peer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    if (data.contains("content_type: TEXT")) {
                        ChatMessage.decode(packet).ifPresent(handleMessage);

                        continue;
                    }

                    int sepIdx = data.indexOf("\n");
                    String header = data.substring(0, sepIdx);
                    String body = data.substring(sepIdx + 1);

                    String[] headerMetadata = header.split(" \\| ");
                    String msgId = headerMetadata[0];
                    int idx = Integer.parseInt(headerMetadata[1]);
                    int packetCnt = Integer.parseInt(headerMetadata[2]);

                    receivedChunks.putIfAbsent(msgId, new HashMap<>());
                    chunkCounts.putIfAbsent(msgId, packetCnt);

                    receivedChunks.get(msgId).put(idx, body.getBytes());

                    if (receivedChunks.get(msgId).size() == packetCnt) {
                        StringBuilder strBuilder = new StringBuilder();

                        for (int i = 0; i < packetCnt; i++) {
                            strBuilder.append(new String(receivedChunks.get(msgId).get(i)));
                        }

                        ChatMessage.fromString(strBuilder).ifPresent(handleMessage);

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

        close();
    }

}
