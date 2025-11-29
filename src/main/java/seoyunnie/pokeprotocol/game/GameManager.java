package seoyunnie.pokeprotocol.game;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import seoyunnie.pokeprotocol.gui.chat.ChatFrame;
import seoyunnie.pokeprotocol.gui.dialog.HostNetworkInfoDialog;
import seoyunnie.pokeprotocol.gui.dialog.HostNetworkInputPanel;
import seoyunnie.pokeprotocol.gui.dialog.PokemonSelectionPanel;
import seoyunnie.pokeprotocol.network.GameClient;
import seoyunnie.pokeprotocol.network.GameHostClient;
import seoyunnie.pokeprotocol.network.GamePeerClient;
import seoyunnie.pokeprotocol.network.message.BattleSetup;
import seoyunnie.pokeprotocol.pokemon.GamePokemon;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public class GameManager implements Consumer<ChatFrame> {
    private static final int SUCCESS = 0;
    private static final int CANCELLED = 1;
    private static final int INVALID_PEER = 2;

    private PlayerRole role;

    private boolean isBroadcasting;
    private GameClient client;

    private BattleManager battleManager;

    private int initializePlayer() throws IOException {
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
            case PlayerRole.CHALLENGER -> {
                var hostNetInDialog = new HostNetworkInputPanel();

                if (isBroadcasting) {
                    hostNetInDialog.setAddress(NetworkUtils.getBroadcastAddress().get());

                    while (true) {
                        JOptionPane.showMessageDialog(null, hostNetInDialog, "Host Network Details",
                                JOptionPane.PLAIN_MESSAGE);

                        if (hostNetInDialog.getPort().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Please fill up all input fields.", "Missing Input",
                                    JOptionPane.ERROR_MESSAGE);

                            continue;
                        }

                        yield new GamePeerClient(hostNetInDialog.getPort().get());
                    }
                }

                while (true) {
                    JOptionPane.showMessageDialog(null, hostNetInDialog, "Host Network Details",
                            JOptionPane.PLAIN_MESSAGE);

                    if (hostNetInDialog.getAddress().isEmpty() || hostNetInDialog.getPort().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill up all input fields.", "Missing Input",
                                JOptionPane.ERROR_MESSAGE);

                        continue;
                    }

                    try {
                        yield new GamePeerClient(hostNetInDialog.getAddress().get(), hostNetInDialog.getPort().get());
                    } catch (UnknownHostException e) {
                        JOptionPane.showMessageDialog(null, "Please input a valid IP address.", "Invalid IP Address",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            case PlayerRole.SPECTATOR -> throw new IllegalStateException();
        };

        return SUCCESS;
    }

    private void setupBattle() throws IOException, NoSuchElementException {
        if (client instanceof GameHostClient hostClient) {
            hostClient.startHandshake();

            var netInfoFrame = new HostNetworkInfoDialog(
                    isBroadcasting ? NetworkUtils.getBroadcastAddress().get() : NetworkUtils.getAddress().get(),
                    GameHostClient.PORT);

            try {
                hostClient.getHandshakeThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            netInfoFrame.dispose();
        } else if (client instanceof GamePeerClient peerClient) {
            if (!peerClient.connectToHost()) {
                throw new IOException();
            }
        }

        var pokemonSelDialog = new PokemonSelectionPanel();

        JOptionPane.showMessageDialog(null, pokemonSelDialog, "Select Your Pokémon", JOptionPane.PLAIN_MESSAGE);

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

        Pokemon enemyPokemon = GamePokemon.getByName(battleSetup.pokemonName()).get();
        StatBoosts enemyStatBoosts = battleSetup.statBoosts();

        this.battleManager = new BattleManager(client, ownPokemon, ownStatBoosts, enemyPokemon, enemyStatBoosts);
    }

    @Override
    public void accept(ChatFrame chatFrame) {
        try {
            if (initializePlayer() == CANCELLED) {
                chatFrame.dispose();

                return;
            }

            setupBattle();

            Rectangle battleFrameBounds = battleManager.getFrame().getBounds();

            chatFrame.setLocation(battleFrameBounds.x + battleFrameBounds.width, battleFrameBounds.y);
            chatFrame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "A network-related issue was encountered.", "Network Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(null, "The peer is using an application with a different implementation.",
                    "Invalid Peer", JOptionPane.ERROR_MESSAGE);

            client.close();

            chatFrame.dispose();

            return;
        }

        battleManager.run();

        chatFrame.dispose();

        System.exit(0);
    }
}
