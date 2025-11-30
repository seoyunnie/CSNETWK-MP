package seoyunnie.pokeprotocol.network.message;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Optional;

import seoyunnie.pokeprotocol.game.StatBoosts;
import seoyunnie.pokeprotocol.network.CommunicationMode;
import seoyunnie.pokeprotocol.util.NetworkUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public record BattleSetup(CommunicationMode communicationMode, String pokemonName, StatBoosts statBoosts)
        implements Message {
    public static Optional<BattleSetup> decode(DatagramPacket packet) {
        Map<String, String> msgEntries = NetworkUtils.getMessageEntries(packet);

        if (!msgEntries.getOrDefault("message_type", "").equals(Type.BATTLE_SETUP.toString())) {
            return Optional.empty();
        }

        try {
            return Optional.of(new BattleSetup(CommunicationMode.valueOf(msgEntries.get("communication_mode")),
                    msgEntries.get("pokemon_name"),
                    new ObjectMapper().readValue(msgEntries.get("stat_boosts"), StatBoosts.class)));
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }

    @Override
    public final String toString() {
        return String.join("\n",
                "message_type: " + Type.BATTLE_SETUP,
                "communication_mode: " + communicationMode,
                "pokemon_name: " + pokemonName,
                "stat_boosts: " + new ObjectMapper().writeValueAsString(statBoosts));
    }
}
