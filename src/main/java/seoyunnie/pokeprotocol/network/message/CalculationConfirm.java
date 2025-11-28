package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record CalculationConfirm(int sequenceNumber) {
    public static Optional<CalculationConfirm> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(MessageType.CALCULATION_CONFIRM.toString())) {
            return Optional.empty();
        }

        return Optional.of(new CalculationConfirm(Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join(
                "\n",
                "message_type: " + MessageType.CALCULATION_CONFIRM,
                "sequence_number: " + sequenceNumber);
    }
}
