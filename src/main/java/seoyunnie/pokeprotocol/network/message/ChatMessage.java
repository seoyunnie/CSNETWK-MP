package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ChatMessage(String senderName, ContentType contentType, String messageText, int sequenceNumber)
        implements ReliableMessage {
    public enum ContentType {
        TEXT,
        STICKER;
    }

    public static Optional<ChatMessage> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CHAT_MESSAGE.toString())) {
            return Optional.empty();
        }

        return Optional.of(new ChatMessage(
                msgEntries.get("sender_name"),
                ContentType.valueOf(msgEntries.get("content_type")), msgEntries.get("message_text"),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join(
                "\n",
                "message_type: " + MessageType.CHAT_MESSAGE,
                "sender_name: " + senderName,
                "content_type: " + contentType,
                "message_text: " + messageText,
                "sequence_number: " + sequenceNumber);
    }
}
