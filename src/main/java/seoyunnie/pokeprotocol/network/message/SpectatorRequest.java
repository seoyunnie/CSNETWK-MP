package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record SpectatorRequest() {
    public static Optional<SpectatorRequest> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.SPECTATOR_REQUEST.toString())) {
            return Optional.empty();
        }

        return Optional.of(new SpectatorRequest());
    }

    @Override
    public String toString() {
        return "message_type: " + MessageType.SPECTATOR_REQUEST;
    }
}
