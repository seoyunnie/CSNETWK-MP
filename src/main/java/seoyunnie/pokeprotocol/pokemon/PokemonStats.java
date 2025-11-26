package seoyunnie.pokeprotocol.pokemon;

public record PokemonStats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
    public static final int LOW_HP_PERCENTAGE = 50;
    public static final int CRITICAL_HP_PERCENTAGE = 20;
}
