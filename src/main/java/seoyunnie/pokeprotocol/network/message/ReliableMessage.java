package seoyunnie.pokeprotocol.network.message;

public interface ReliableMessage {
    public int sequenceNumber();

    public String toString();
}
