package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record SpectatorRequest() implements Message {
    public static Optional<SpectatorRequest> decode(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(Type.SPECTATOR_REQUEST.toString())) {
            return Optional.empty();
        }

        return Optional.of(new SpectatorRequest());
    }

    @Override
    public String toString() {
        return "message_type: " + Type.SPECTATOR_REQUEST;
    }
}
