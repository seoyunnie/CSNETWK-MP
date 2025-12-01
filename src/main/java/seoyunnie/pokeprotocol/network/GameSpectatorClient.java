package seoyunnie.pokeprotocol.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import seoyunnie.pokeprotocol.game.BattlePokemon;
import seoyunnie.pokeprotocol.game.StatBoosts;
import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.network.message.HandshakeResponse;
import seoyunnie.pokeprotocol.network.message.Message;
import seoyunnie.pokeprotocol.network.message.SpectatorRequest;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public class GameSpectatorClient extends GameClient implements GamePeerClient {
    private static final int PORT = 8083;

    private final Peer host;

    public GameSpectatorClient(boolean isBroadcasting, InetAddress hostAddr, int hostPort) throws SocketException {
        super(PORT, isBroadcasting);

        this.host = new Peer(hostAddr, hostPort);
    }

    public Peer getHost() {
        return host;
    }

    public GameSpectatorClient(String hostAddrName, int hostPort) throws UnknownHostException, SocketException {
        this(false, InetAddress.getByName(hostAddrName), hostPort);
    }

    public GameSpectatorClient(int hostPort) throws SocketException {
        this(true, NetworkUtils.getBroadcastAddress().get(), hostPort);
    }

    @Override
    protected void sendMessage(Message msg) throws IOException {
        sendMessage(msg, host.address(), host.port());
    }

    @Override
    protected boolean sendReliableMessage(Message msg) throws IOException {
        return sendReliableMessage(msg, host.address(), host.port());
    }

    @Override
    protected void sendACK(int seqNum) throws IOException {
        sendACK(seqNum, host);
    }

    public boolean connectToHost() throws IOException {
        sendMessage(new SpectatorRequest());

        return HandshakeResponse.decode(receiveBlockingPacket()).isPresent();
    }

    @Override
    public void sendBattleSetup(Pokemon pokemon, StatBoosts statBoosts) throws IOException {
        throw new UnsupportedOperationException("Spectators cannot send battle messages");
    }

    @Override
    public boolean announceAttack(Move move) throws IOException {
        throw new UnsupportedOperationException("Spectators cannot send battle messages");
    }

    @Override
    public boolean announceDefense() throws IOException {
        throw new UnsupportedOperationException("Spectators cannot send battle messages");
    }

    @Override
    public boolean sendCalculationReport(BattlePokemon pokemon, Move moveUsed, int damageDealt, int defenderHP,
            String msg) throws IOException {
        throw new UnsupportedOperationException("Spectators cannot send battle messages");
    }

    @Override
    public void sendResolutionRequest(BattlePokemon pokemon, Move moveUsed, int damageDealt, int defenderHP)
            throws IOException {
        throw new UnsupportedOperationException("Spectators cannot send battle messages");
    }

    @Override
    public boolean sendGameOver(BattlePokemon winningPokemon, BattlePokemon losingPokemon) throws IOException {
        throw new UnsupportedOperationException("Spectators cannot send battle messages");
    }
}
