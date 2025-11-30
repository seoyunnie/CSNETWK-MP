package seoyunnie.pokeprotocol.exception;

public class GameSynchronizationException extends Exception {
    public GameSynchronizationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public GameSynchronizationException(String msg) {
        super(msg);
    }
}
