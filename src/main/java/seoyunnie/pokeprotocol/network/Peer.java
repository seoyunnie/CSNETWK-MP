package seoyunnie.pokeprotocol.network;

import java.net.InetAddress;
import java.util.Objects;

public record Peer(InetAddress address, int port) {
    @Override
    public final int hashCode() {
        return Objects.hash(address, port);
    }
}
