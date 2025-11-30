package seoyunnie.pokeprotocol.exception;

public class LostConnectionException extends Exception {
    public LostConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LostConnectionException(String msg) {
        super(msg);
    }
}
