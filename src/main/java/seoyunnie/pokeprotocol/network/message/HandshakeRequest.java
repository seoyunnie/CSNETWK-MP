package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record HandshakeRequest() {
    public static Optional<HandshakeRequest> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.HANDSHAKE_REQUEST.toString())) {
            return Optional.empty();
        }

        return Optional.of(new HandshakeRequest());
    }

    @Override
    public String toString() {
        return "message_type: " + MessageType.HANDSHAKE_REQUEST;
    }
}
