package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.util.NetworkUtils;

public record GameOver(String winner, String loser, int sequenceNumber) implements Message {
    public static Optional<GameOver> decode(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(Type.GAME_OVER.toString())) {
            return Optional.empty();
        }

        return Optional.of(new GameOver(msgEntries.get("winner"), msgEntries.get("loser"),
                Integer.parseInt(msgEntries.get("sequence_number"))));
    }

    @Override
    public String toString() {
        return String.join("\n",
                "message_type: " + Type.GAME_OVER,
                "winner: " + winner,
                "loser: " + loser,
                "sequence_number: " + sequenceNumber);
    }
}
