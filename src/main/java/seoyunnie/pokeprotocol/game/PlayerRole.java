package seoyunnie.pokeprotocol.game;

public enum PlayerRole {
    HOST,
    JOINER,
    SPECTATOR;

    @Override
    public String toString() {
        return switch (this) {
            case PlayerRole.HOST -> "Host: The host of the match and its primary player";
            case PlayerRole.JOINER -> "Joiner: The secondary player or opponent";
            case PlayerRole.SPECTATOR -> "Spectator: Watching the match on the sidelines";
        };
    }
}
