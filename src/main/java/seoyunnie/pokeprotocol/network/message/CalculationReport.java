package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record CalculationReport(String attacker, String moveUsed, int remainingHealth, int damageDealt,
        int defenderHPRemaining, String statusMessage, int sequenceNumber) implements ReliableMessage {
    public static Optional<CalculationReport> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CALCULATION_REPORT.toString())) {
            return Optional.empty();
        }

        return Optional.of(new CalculationReport(
                msgEntries.get("attacker"),
                msgEntries.get("move_used"),
                Integer.parseInt(msgEntries.get("remaining_health")),
                Integer.parseInt(msgEntries.get("damage_dealt")),
                Integer.parseInt(msgEntries.get("defender_hp_remaining")),
                msgEntries.get("status_message"),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join(
                "\n",
                "message_type: " + MessageType.CALCULATION_REPORT,
                "attacker: " + attacker,
                "move_used: " + moveUsed,
                "remaining_health: " + remainingHealth,
                "damage_dealt: " + damageDealt,
                "defender_hp_remaining: " + defenderHPRemaining,
                "status_message: " + statusMessage,
                "sequence_number: " + sequenceNumber);
    }
}
