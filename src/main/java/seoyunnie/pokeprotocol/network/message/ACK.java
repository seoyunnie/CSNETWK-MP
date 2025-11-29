package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ACK(int ackNumber) {
    public static Optional<ACK> fromPacket(DatagramPacket packet) {
        return Optional.of(NetworkUtils.getMessageEntries(packet).get("ack_number"))
                .map((n) -> new ACK(Integer.parseInt(n)));
    }

    @Override
    public String toString() {
        return "ack_number: " + ackNumber;
    }
}
