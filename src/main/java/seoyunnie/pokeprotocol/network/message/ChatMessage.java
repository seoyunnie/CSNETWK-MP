package seoyunnie.pokeprotocol.network.message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.sticker.Sticker;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ChatMessage(String senderName, ContentType contentType, String messageText, Sticker sticker,
        int sequenceNumber) {
    public enum ContentType {
        TEXT, STICKER;
    }

    public ChatMessage(String senderName, String msgText, int seqNum) {
        this(senderName, ContentType.TEXT, msgText, null, seqNum);
    }

    public ChatMessage(String senderName, Sticker sticker, int seqNum) {
        this(senderName, ContentType.STICKER, null, sticker, seqNum);
    }

    public static Optional<ChatMessage> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CHAT_MESSAGE.toString())) {
            return Optional.empty();
        }

        return Optional.of(new ChatMessage(msgEntries.get("sender_name"), msgEntries.get("message_text"),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    public static Optional<ChatMessage> fromString(StringBuilder strBuilder) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(strBuilder);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CHAT_MESSAGE.toString())) {
            return Optional.empty();
        }

        try {
            return Optional.of(new ChatMessage(msgEntries.get("sender_name"),
                    Sticker.fromBase64String(msgEntries.get("sticker_data")),
                    Integer.parseInt(msgEntries.get("sequence_number"))));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        if (contentType == ContentType.TEXT) {
            return String.join("\n",
                    "message_type: " + MessageType.CHAT_MESSAGE,
                    "sender_name: " + senderName,
                    "content_type: " + contentType,
                    "message_text: " + messageText,
                    "sequence_number: " + sequenceNumber);
        }

        return String.join("\n",
                "message_type: " + MessageType.CHAT_MESSAGE,
                "sender_name: " + senderName,
                "content_type: " + contentType,
                "sticker_data: " + sticker.encodeToBase64String(),
                "sequence_number: " + sequenceNumber);
    }
}
