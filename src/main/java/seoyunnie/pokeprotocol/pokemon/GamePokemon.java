package seoyunnie.pokeprotocol.pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seoyunnie.pokeprotocol.move.GameMoves;
import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.type.GameTypes;

public final class GamePokemon {
    private static final List<Pokemon> POKEMON = new ArrayList<>();

    public static final Pokemon BLAZIKEN = add(new Pokemon("Blaziken", GameTypes.FIRE, GameTypes.FIGHTING,
            new PokemonStats(80, 120, 70, 110, 70, 80),
            new Move[] {
                    GameMoves.FLARE_BLITZ, GameMoves.CLOSE_COMBAT,
                    GameMoves.HIGH_JUMP_KICK, GameMoves.BRAVE_BIRD
            }));
    public static final Pokemon HYDREIGON = add(new Pokemon("Hydreigon", GameTypes.DARK, GameTypes.DRAGON,
            new PokemonStats(92, 105, 90, 125, 90, 98),
            new Move[] {
                    GameMoves.DARK_PULSE, GameMoves.DRACO_METEOR,
                    GameMoves.FLASH_CANNON, GameMoves.FIRE_BLAST
            }));
    public static final Pokemon MILOTIC = add(new Pokemon("Milotic", GameTypes.WATER,
            new PokemonStats(95, 60, 79, 100, 125, 81),
            new Move[] {
                    GameMoves.SCALD, GameMoves.ALLURING_VOICE,
                    GameMoves.ICE_BEAM, GameMoves.BLIZZARD
            }));
    public static final Pokemon RAYQUAZA = add(new Pokemon("Rayquaza", GameTypes.DRAGON, GameTypes.FLYING,
            new PokemonStats(105, 150, 90, 150, 90, 95),
            new Move[] {
                    GameMoves.OUTRAGE, GameMoves.EXTREME_SPEED,
                    GameMoves.HYPER_BEAM, GameMoves.EARTHQUAKE
            }));

    private GamePokemon() {}

    private static Pokemon add(Pokemon pokemon) {
        POKEMON.add(pokemon);

        return pokemon;
    }

    public static Pokemon[] values() {
        return POKEMON.toArray(Pokemon[]::new);
    }

    public static String[] names() {
        return POKEMON.stream().map((p) -> p.name()).toArray(String[]::new);
    }

    public static Optional<Pokemon> getByName(String name) {
        return POKEMON.stream().filter((p) -> p.name().equals(name)).findFirst();
    }
}
