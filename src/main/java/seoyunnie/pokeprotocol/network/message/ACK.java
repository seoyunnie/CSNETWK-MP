package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record ACK(int ackNumber) implements Message {
    public static Optional<ACK> decode(DatagramPacket packet) {
        return Optional.ofNullable(NetworkUtils.getMessageEntries(packet).get("ack_number"))
                .map((n) -> new ACK(Integer.parseInt(n)));
    }

    @Override
    public String toString() {
        return "ack_number: " + ackNumber;
    }
}
