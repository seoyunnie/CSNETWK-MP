package seoyunnie.pokeprotocol.game;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.swing.JOptionPane;

import seoyunnie.pokeprotocol.exception.IncompatiblePeerException;
import seoyunnie.pokeprotocol.gui.GameFrame;
import seoyunnie.pokeprotocol.gui.dialog.HostNetworkInfoDialog;
import seoyunnie.pokeprotocol.gui.dialog.HostNetworkInputPanel;
import seoyunnie.pokeprotocol.gui.dialog.PokemonSelectionPanel;
import seoyunnie.pokeprotocol.network.GameClient;
import seoyunnie.pokeprotocol.network.GameHostClient;
import seoyunnie.pokeprotocol.network.GameJoinerClient;
import seoyunnie.pokeprotocol.network.message.BattleSetup;
import seoyunnie.pokeprotocol.pokemon.GamePokemon;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public class GameManager implements Runnable {
    private static final int SUCCESS = 0;
    private static final int CANCELLED = 1;

    private boolean isBroadcasting;
    private GameClient client;

    private BattleManager battleManager;
    private ChatManager chatManager;

    private String username;
    private PlayerRole role;

    private int initializePlayer() throws IOException {
        username = JOptionPane.showInputDialog(null, "Input your preferred username:", "Login",
                JOptionPane.PLAIN_MESSAGE);

        if (username == null) {
            return CANCELLED;
        } else if (username.isBlank()) {
            username = "Anonymous";
        }

        int opt = JOptionPane.showConfirmDialog(null, "Should the application use your broadcast IP address?",
                "Broadcast Mode", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opt == JOptionPane.CANCEL_OPTION) {
            return CANCELLED;
        }

        this.isBroadcasting = opt == JOptionPane.YES_OPTION;

        PlayerRole[] roles = PlayerRole.values();

        this.role = (PlayerRole) JOptionPane.showInputDialog(null, "Choose your role in the Pokémon battle:",
                "Role Selection", JOptionPane.QUESTION_MESSAGE, null,
                isBroadcasting ? Arrays.copyOfRange(roles, 0, 2) : roles, PlayerRole.HOST);

        if (role == null) {
            return CANCELLED;
        }

        this.client = switch (role) {
            case PlayerRole.HOST -> new GameHostClient(isBroadcasting);
            case PlayerRole.JOINER -> {
                if (isBroadcasting) {
                    yield new GameJoinerClient(GameHostClient.PORT);
                }

                var hostNetInDialog = new HostNetworkInputPanel();

                while (true) {
                    JOptionPane.showMessageDialog(null, hostNetInDialog, "Host Network Details",
                            JOptionPane.PLAIN_MESSAGE);

                    if (hostNetInDialog.getAddress().isEmpty() || hostNetInDialog.getPort().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill up all input fields.", "Missing Input",
                                JOptionPane.ERROR_MESSAGE);

                        continue;
                    }

                    try {
                        yield new GameJoinerClient(hostNetInDialog.getAddress().get(), hostNetInDialog.getPort().get());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();

                        JOptionPane.showMessageDialog(null, "Please input a valid IP address.", "Invalid IP Address",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            case PlayerRole.SPECTATOR -> throw new IllegalStateException();
        };

        this.chatManager = new ChatManager(client, isBroadcasting, username);

        return SUCCESS;
    }

    private void setupBattle() throws IOException, IncompatiblePeerException {
        if (client instanceof GameHostClient hostClient) {
            hostClient.startHandshake();

            var netInfoFrame = new HostNetworkInfoDialog(
                    isBroadcasting ? NetworkUtils.getBroadcastAddress().get() : NetworkUtils.getAddress().get(),
                    GameHostClient.PORT);

            try {
                hostClient.getHandshakeThread().join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            netInfoFrame.dispose();
        } else if (client instanceof GameJoinerClient peerClient) {
            if (!peerClient.connectToHost()) {
                throw new IOException();
            }
        }

        var pokemonSelDialog = new PokemonSelectionPanel();

        JOptionPane.showMessageDialog(null, pokemonSelDialog, "Select Your Pokémon:", JOptionPane.PLAIN_MESSAGE);

        Pokemon ownPokemon = pokemonSelDialog.getPokemon();
        var ownStatBoosts = new StatBoosts();

        BattleSetup battleSetup;

        if (role == PlayerRole.HOST) {
            client.sendBattleSetup(ownPokemon, ownStatBoosts);

            battleSetup = client.receiveBattleSetup().orElseThrow();
        } else {
            battleSetup = client.receiveBattleSetup().orElseThrow();

            client.sendBattleSetup(ownPokemon, ownStatBoosts);
        }

        Pokemon enemyPokemon = GamePokemon.getByName(battleSetup.pokemonName()).orElseThrow(
                () -> new IncompatiblePeerException("'" + battleSetup.pokemonName() + "' is not implemented"));
        StatBoosts enemyStatBoosts = battleSetup.statBoosts();

        this.battleManager = new BattleManager(client, ownPokemon, ownStatBoosts, enemyPokemon, enemyStatBoosts);
    }

    @Override
    public void run() {
        try {
            if (initializePlayer() == CANCELLED) {
                return;
            }

            setupBattle();
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A network-related issue was encountered.", "Network Error",
                    JOptionPane.ERROR_MESSAGE);

            client.close();

            return;
        } catch (IncompatiblePeerException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "The peer is using an application with a different implementation.",
                    "Incompatible Peer", JOptionPane.ERROR_MESSAGE);

            client.close();

            return;
        }

        new GameFrame(battleManager.getGamePanel(), chatManager.getPanel());

        chatManager.run();
        battleManager.run();

        System.exit(0);
    }
}
