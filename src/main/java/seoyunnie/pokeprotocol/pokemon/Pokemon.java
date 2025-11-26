package seoyunnie.pokeprotocol.pokemon;

import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.type.Type;

public record Pokemon(String name, Type firstType, Type secondType, PokemonStats stats, Move[] moves) {
    public Pokemon(String name, Type type, PokemonStats stats, Move[] moves) {
        this(name, type, null, stats, moves);
    }

    public boolean isDualTyped() {
        return secondType != null;
    }
}
