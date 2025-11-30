package seoyunnie.pokeprotocol.network;

import java.net.InetAddress;
import java.util.Objects;

public record Peer(InetAddress address, int port) {
    @Override
    public final int hashCode() {
        return Objects.hash(address, port);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Peer other = (Peer) obj;

        return port == other.port && Objects.equals(address, other.address);
    }
}
