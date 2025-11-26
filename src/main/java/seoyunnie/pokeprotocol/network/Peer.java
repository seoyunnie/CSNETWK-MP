package seoyunnie.pokeprotocol.network;

import java.net.InetAddress;

public record Peer(InetAddress address, int port) {
}
