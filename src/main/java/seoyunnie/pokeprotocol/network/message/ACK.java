package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ACK(int ackNumber) {
    public static Optional<ACK> fromPacket(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        return Optional.of(msgEntries.get("ack_number")).map((a) -> new ACK(Integer.parseInt(a)));
    }

    @Override
    public String toString() {
        return "ack_number: " + ackNumber;
    }
}
