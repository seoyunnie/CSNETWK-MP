package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ResolutionRequest(String attacker, String moveUsed, int damageDealt, int defenderHPRemaining,
        int sequenceNumber) {
    public static Optional<ResolutionRequest> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.RESOLUTION_REQUEST.toString())) {
            return Optional.empty();
        }

        return Optional.of(new ResolutionRequest(
                msgEntries.get("attacker"),
                msgEntries.get("move_used"),
                Integer.parseInt(msgEntries.get("damage_dealth")),
                Integer.parseInt(msgEntries.get("defender_hp_remaining")),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join("\n",
                "message_type: " + MessageType.RESOLUTION_REQUEST,
                "attacker: " + attacker,
                "move_used: " + moveUsed,
                "damage_dealt: " + damageDealt,
                "defender_hp_remaining: " + defenderHPRemaining,
                "sequence_number: " + sequenceNumber);
    }
}
