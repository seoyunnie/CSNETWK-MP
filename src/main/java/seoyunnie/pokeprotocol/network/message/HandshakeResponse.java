package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record HandshakeResponse() {
    public static Optional<HandshakeResponse> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.HANDSHAKE_RESPONSE.toString())) {
            return Optional.empty();
        }

        return Optional.of(new HandshakeResponse());
    }

    @Override
    public String toString() {
        return "message_type: " + MessageType.HANDSHAKE_RESPONSE;
    }
}
