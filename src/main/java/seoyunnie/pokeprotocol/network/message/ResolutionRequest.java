package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ResolutionRequest(String attacker, String moveUsed, int damageDealt, int defenderHPRemaining,
        int sequenceNumber) implements Message {
    public static Optional<ResolutionRequest> decode(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(Type.RESOLUTION_REQUEST.toString())) {
            return Optional.empty();
        }

        return Optional.of(new ResolutionRequest(msgEntries.get("attacker"), msgEntries.get("move_used"),
                Integer.parseInt(msgEntries.get("damage_dealth")),
                Integer.parseInt(msgEntries.get("defender_hp_remaining")),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join("\n",
                "message_type: " + Type.RESOLUTION_REQUEST,
                "attacker: " + attacker,
                "move_used: " + moveUsed,
                "damage_dealt: " + damageDealt,
                "defender_hp_remaining: " + defenderHPRemaining,
                "sequence_number: " + sequenceNumber);
    }
}
