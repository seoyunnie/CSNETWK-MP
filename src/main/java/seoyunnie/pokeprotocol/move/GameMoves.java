package seoyunnie.pokeprotocol.move;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seoyunnie.pokeprotocol.type.GameTypes;

public final class GameMoves {
    private static final Set<Move> MOVES = new HashSet<>();

    public static final Move ICE_BEAM = add(new Move("Ice Beam", GameTypes.ICE, 9, true));
    public static final Move BLIZZARD = add(new Move("Blizzard", GameTypes.ICE, 11, true));
    public static final Move HYPER_BEAM = add(new Move("Hyper Beam", GameTypes.NORMAL, 15, true));
    public static final Move EARTHQUAKE = add(new Move("Earthquake", GameTypes.GROUND, 10));
    public static final Move FIRE_BLAST = add(new Move("Fire Blast", GameTypes.FIRE, 11, true));
    public static final Move HIGH_JUMP_KICK = add(new Move("High Jump Kick", GameTypes.FIGHTING, 13));
    public static final Move OUTRAGE = add(new Move("Outrage", GameTypes.DRAGON, 12));
    public static final Move EXTREME_SPEED = add(new Move("Extreme Speed", GameTypes.NORMAL, 8));
    public static final Move CLOSE_COMBAT = add(new Move("Close Combat", GameTypes.FIGHTING, 12));
    public static final Move FLARE_BLITZ = add(new Move("Flare Blitz", GameTypes.FIRE, 12));
    public static final Move DARK_PULSE = add(new Move("Dark Pulse", GameTypes.DARK, 8, true));
    public static final Move BRAVE_BIRD = add(new Move("Brave Bird", GameTypes.FLYING, 12));
    public static final Move FLASH_CANNON = add(new Move("Flash Cannon", GameTypes.STEEL, 8, true));
    public static final Move DRACO_METEOR = add(new Move("Draco Meteor", GameTypes.DRAGON, 13, true));
    public static final Move SCALD = add(new Move("Scald", GameTypes.WATER, 8, true));
    public static final Move ALLURING_VOICE = add(new Move("Alluring Voice", GameTypes.FAIRY, 8, true));

    private GameMoves() {
    }

    private static Move add(Move move) {
        MOVES.add(move);

        return move;
    }

    public static Move[] values() {
        return MOVES.toArray(Move[]::new);
    }

    public static Optional<Move> getByName(String name) {
        return MOVES.stream().filter((m) -> m.name().equals(name)).findFirst();
    }
}
