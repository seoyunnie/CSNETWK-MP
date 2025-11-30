package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record HandshakeResponse(int seed) implements Message {
    public HandshakeResponse() {
        this(12345);
    }

    public static Optional<HandshakeResponse> decode(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(Type.HANDSHAKE_RESPONSE.toString())) {
            return Optional.empty();
        }

        return Optional.of(new HandshakeResponse(Integer.parseInt(msgEntries.get("seed"))));
    }

    @Override
    public String toString() {
        return String.join("\n",
                "message_type: " + Type.HANDSHAKE_RESPONSE,
                "seed: " + seed);
    }
}
