package seoyunnie.pokeprotocol.exception;

public class IncompatiblePeerException extends Exception {
    public IncompatiblePeerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IncompatiblePeerException(String msg) {
        super(msg);
    }
}
