package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record DefenseAnnounce(int sequenceNumber) {
    public static Optional<DefenseAnnounce> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.DEFENSE_ANNOUNCE.toString())) {
            return Optional.empty();
        }

        return Optional.of(new DefenseAnnounce(Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join("\n",
                "message_type: " + MessageType.DEFENSE_ANNOUNCE,
                "sequence_number: " + sequenceNumber);
    }
}
