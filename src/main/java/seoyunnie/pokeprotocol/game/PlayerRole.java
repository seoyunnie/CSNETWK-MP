package seoyunnie.pokeprotocol.game;

public enum PlayerRole {
    HOST,
    CHALLENGER,
    SPECTATOR;

    @Override
    public String toString() {
        return switch (this) {
            case PlayerRole.HOST -> "Host: The host of the match and its primary player";
            case PlayerRole.CHALLENGER -> "Challenger: The secondary player or opponent";
            case PlayerRole.SPECTATOR -> "Spectator: Watching the match on the sidelines";
        };
    }
}
