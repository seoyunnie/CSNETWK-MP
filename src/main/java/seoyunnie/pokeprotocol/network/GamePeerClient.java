package seoyunnie.pokeprotocol.network;

import java.io.IOException;

public interface GamePeerClient {
    Peer getHost();

    boolean connectToHost() throws IOException;
}
