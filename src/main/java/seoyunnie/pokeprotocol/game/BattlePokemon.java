package seoyunnie.pokeprotocol.game;

import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.type.Type;

public class BattlePokemon {
    private final Pokemon basePokemon;

    private int currentHP;

    public BattlePokemon(Pokemon basePokemon) {
        this.basePokemon = basePokemon;

        this.currentHP = basePokemon.stats().hp();
    }

    public Pokemon getBasePokemon() {
        return basePokemon;
    }

    public String getName() {
        return basePokemon.name();
    }

    public Type getFirstType() {
        return basePokemon.firstType();
    }

    public Type getSecondType() {
        return basePokemon.secondType();
    }

    public boolean isDualTyped() {
        return basePokemon.isDualTyped();
    }

    public Move[] getMoves() {
        return basePokemon.moves();
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void decreaseHP(int amount) {
        this.currentHP -= amount;
    }

    public boolean hasFainted() {
        return currentHP == 0;
    }
}
