package seoyunnie.pokeprotocol.network.message;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import seoyunnie.pokeprotocol.util.ImageUtils;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ChatMessage(String senderName, ContentType contentType, String messageText, ImageIcon sticker,
        int sequenceNumber) {
    public enum ContentType {
        TEXT,
        STICKER;
    }

    public ChatMessage(String senderName, String msgText, int seqNum) {
        this(senderName, ContentType.TEXT, msgText, null, seqNum);
    }

    public ChatMessage(String senderName, ImageIcon sticker, int seqNum) {
        this(senderName, ContentType.STICKER, null, sticker, seqNum);
    }

    public static Optional<ChatMessage> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CHAT_MESSAGE.toString())) {
            return Optional.empty();
        }

        return Optional.of(new ChatMessage(
                msgEntries.get("sender_name"),
                msgEntries.get("message_text"),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    public static Optional<ChatMessage> fromString(StringBuilder strBuilder) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(strBuilder);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CHAT_MESSAGE.toString())) {
            return Optional.empty();
        }

        BufferedImage sticker;

        try {
            sticker = ImageIO.read(
                    new ByteArrayInputStream(Base64.getDecoder().decode(msgEntries.get("sticker_data"))));
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.of(new ChatMessage(
                msgEntries.get("sender_name"),
                new ImageIcon(sticker.getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        if (contentType == ContentType.TEXT) {
            return String.join(
                    "\n",
                    "message_type: " + MessageType.CHAT_MESSAGE,
                    "sender_name: " + senderName,
                    "content_type: " + contentType,
                    "message_text: " + messageText,
                    "sequence_number: " + sequenceNumber);
        }

        return String.join(
                "\n",
                "message_type: " + MessageType.CHAT_MESSAGE,
                "sender_name: " + senderName,
                "content_type: " + contentType,
                "sticker_data: " + ImageUtils.encodeImageIconToBase64String(sticker),
                "sequence_number: " + sequenceNumber);
    }
}
