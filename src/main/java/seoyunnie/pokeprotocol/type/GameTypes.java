package seoyunnie.pokeprotocol.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GameTypes {
    private static final List<Type> TYPES = new ArrayList<>();

    public static final Type NORMAL = add(new Type("normal"));
    public static final Type FIGHTING = add(new Type("fighting"));
    public static final Type FLYING = add(new Type("flying"));
    public static final Type POISON = add(new Type("poison"));
    public static final Type GROUND = add(new Type("ground"));
    public static final Type ROCK = add(new Type("rock"));
    public static final Type BUG = add(new Type("bug"));
    public static final Type GHOST = add(new Type("ghost"));
    public static final Type STEEL = add(new Type("steel"));
    public static final Type FIRE = add(new Type("fire"));
    public static final Type WATER = add(new Type("water"));
    public static final Type GRASS = add(new Type("grass"));
    public static final Type ELECTRIC = add(new Type("electric"));
    public static final Type PSYCHIC = add(new Type("psychic"));
    public static final Type ICE = add(new Type("ice"));
    public static final Type DRAGON = add(new Type("dragon"));
    public static final Type DARK = add(new Type("dark"));
    public static final Type FAIRY = add(new Type("fairy"));

    public static final Map<Type, Map<Type, Float>> EFFECTIVENESS_MAP;

    static {
        EFFECTIVENESS_MAP = Map.ofEntries(
                Map.entry(NORMAL, Map.ofEntries(
                        Map.entry(ROCK, 0.5f),
                        Map.entry(GHOST, 0f),
                        Map.entry(STEEL, 0.5f))),
                Map.entry(FIGHTING, Map.ofEntries(
                        Map.entry(NORMAL, 2f),
                        Map.entry(FLYING, 0.5f),
                        Map.entry(POISON, 0.5f),
                        Map.entry(ROCK, 2f),
                        Map.entry(BUG, 0.5f),
                        Map.entry(GHOST, 0f),
                        Map.entry(STEEL, 2f),
                        Map.entry(PSYCHIC, 0.5f),
                        Map.entry(ICE, 2f),
                        Map.entry(DARK, 2f),
                        Map.entry(FAIRY, 0.5f))),
                Map.entry(FLYING, Map.ofEntries(
                        Map.entry(FIGHTING, 2f),
                        Map.entry(ROCK, 0.5f),
                        Map.entry(BUG, 2f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(GRASS, 2f),
                        Map.entry(ELECTRIC, 0.5f))),
                Map.entry(POISON, Map.ofEntries(
                        Map.entry(POISON, 0.5f),
                        Map.entry(GROUND, 0.5f),
                        Map.entry(ROCK, 0.5f),
                        Map.entry(GHOST, 0.5f),
                        Map.entry(STEEL, 0f),
                        Map.entry(GRASS, 2f),
                        Map.entry(FAIRY, 2f))),
                Map.entry(GROUND, Map.ofEntries(
                        Map.entry(FLYING, 0f),
                        Map.entry(POISON, 2f),
                        Map.entry(ROCK, 2f),
                        Map.entry(BUG, 0.5f),
                        Map.entry(FIRE, 2f),
                        Map.entry(GRASS, 0.5f),
                        Map.entry(ELECTRIC, 2f))),
                Map.entry(ROCK, Map.ofEntries(
                        Map.entry(FIGHTING, 0.5f),
                        Map.entry(FLYING, 2f),
                        Map.entry(GROUND, 0.5f),
                        Map.entry(BUG, 2f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(FIRE, 2f),
                        Map.entry(ICE, 2f))),
                Map.entry(BUG, Map.ofEntries(
                        Map.entry(FIGHTING, 0.5f),
                        Map.entry(FLYING, 0.5f),
                        Map.entry(POISON, 0.5f),
                        Map.entry(GHOST, 0.5f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(FIRE, 0.5f),
                        Map.entry(GRASS, 2f),
                        Map.entry(PSYCHIC, 2f),
                        Map.entry(DARK, 2f),
                        Map.entry(FAIRY, 0.5f))),
                Map.entry(GHOST, Map.ofEntries(
                        Map.entry(NORMAL, 0f),
                        Map.entry(GHOST, 2f),
                        Map.entry(PSYCHIC, 2f),
                        Map.entry(DARK, 0.5f))),
                Map.entry(STEEL, Map.ofEntries(
                        Map.entry(ROCK, 2f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(FIRE, 0.5f),
                        Map.entry(WATER, 0.5f),
                        Map.entry(ELECTRIC, 0.5f),
                        Map.entry(ICE, 2f),
                        Map.entry(FAIRY, 2f))),
                Map.entry(FIRE, Map.ofEntries(
                        Map.entry(ROCK, 0.5f),
                        Map.entry(BUG, 2f),
                        Map.entry(STEEL, 2f),
                        Map.entry(FIRE, 0.5f),
                        Map.entry(WATER, 0.5f),
                        Map.entry(GRASS, 2f),
                        Map.entry(ICE, 2f),
                        Map.entry(DRAGON, 0.5f))),
                Map.entry(WATER, Map.ofEntries(
                        Map.entry(GROUND, 2f),
                        Map.entry(ROCK, 2f),
                        Map.entry(FIRE, 2f),
                        Map.entry(WATER, 0.5f),
                        Map.entry(GRASS, 0.5f),
                        Map.entry(DRAGON, 0.5f))),
                Map.entry(GRASS, Map.ofEntries(
                        Map.entry(FLYING, 0.5f),
                        Map.entry(POISON, 0.5f),
                        Map.entry(GROUND, 2f),
                        Map.entry(ROCK, 2f),
                        Map.entry(BUG, 0.5f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(FIRE, 0.5f),
                        Map.entry(WATER, 2f),
                        Map.entry(GRASS, 0.5f),
                        Map.entry(DRAGON, 0.5f))),
                Map.entry(ELECTRIC, Map.ofEntries(
                        Map.entry(FLYING, 2f),
                        Map.entry(GROUND, 0f),
                        Map.entry(WATER, 2f),
                        Map.entry(GRASS, 0.5f),
                        Map.entry(ELECTRIC, 0.5f),
                        Map.entry(DRAGON, 0.5f))),
                Map.entry(PSYCHIC, Map.ofEntries(
                        Map.entry(FIGHTING, 2f),
                        Map.entry(POISON, 2f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(PSYCHIC, 0.5f),
                        Map.entry(DARK, 0f))),
                Map.entry(ICE, Map.ofEntries(
                        Map.entry(FLYING, 2f),
                        Map.entry(GROUND, 2f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(FIRE, 0.5f),
                        Map.entry(WATER, 0.5f),
                        Map.entry(GRASS, 2f),
                        Map.entry(ICE, 0.5f),
                        Map.entry(DRAGON, 2f))),
                Map.entry(DRAGON, Map.ofEntries(
                        Map.entry(STEEL, 0.5f),
                        Map.entry(DRAGON, 2f),
                        Map.entry(FAIRY, 0f))),
                Map.entry(DARK, Map.ofEntries(
                        Map.entry(FIGHTING, 0.5f),
                        Map.entry(GHOST, 2f),
                        Map.entry(PSYCHIC, 2f),
                        Map.entry(DARK, 0.5f),
                        Map.entry(FAIRY, 0.5f))),
                Map.entry(FAIRY, Map.ofEntries(
                        Map.entry(FIGHTING, 2f),
                        Map.entry(POISON, 0.5f),
                        Map.entry(STEEL, 0.5f),
                        Map.entry(FIRE, 0.5f),
                        Map.entry(DRAGON, 2f),
                        Map.entry(DARK, 2f))));
    }

    private GameTypes() {}

    private static Type add(Type type) {
        TYPES.add(type);

        return type;
    }

    public static Type[] values() {
        return TYPES.toArray(Type[]::new);
    }
}
