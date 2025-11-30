package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record AttackAnnounce(String moveName, int sequenceNumber) implements Message {
    public static Optional<AttackAnnounce> decode(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(Type.ATTACK_ANNOUNCE.toString())) {
            return Optional.empty();
        }

        return Optional.of(
                new AttackAnnounce(msgEntries.get("move_name"), Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join("\n",
                "message_type: " + Type.ATTACK_ANNOUNCE,
                "move_name: " + moveName,
                "sequence_number: " + sequenceNumber);
    }
}
