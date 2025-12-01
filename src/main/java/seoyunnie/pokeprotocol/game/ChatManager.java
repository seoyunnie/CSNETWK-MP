package seoyunnie.pokeprotocol.game;

import java.io.IOException;
import java.net.SocketException;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import seoyunnie.pokeprotocol.gui.chat.ChatPanel;
import seoyunnie.pokeprotocol.network.ChatClient;
import seoyunnie.pokeprotocol.network.GameClient;
import seoyunnie.pokeprotocol.network.GameHostClient;
import seoyunnie.pokeprotocol.network.GameJoinerClient;
import seoyunnie.pokeprotocol.network.GameSpectatorClient;
import seoyunnie.pokeprotocol.network.Peer;
import seoyunnie.pokeprotocol.network.message.ChatMessage;

public class ChatManager implements Runnable {
    private final ChatClient client;
    private final GameClient gameClient;

    private final ChatPanel panel;

    private final boolean isBroadcasting;

    private final String username;

    public ChatManager(GameClient gameClient, boolean isBroadcasting, String username) throws SocketException {
        this.client = new ChatClient(
                gameClient instanceof GameHostClient ? ChatClient.HOST_PORT
                        : gameClient instanceof GameJoinerClient ? ChatClient.JOINER_PORT : ChatClient.SPECTATOR_PORT,
                isBroadcasting, username);
        this.gameClient = gameClient;

        this.panel = new ChatPanel();

        this.isBroadcasting = isBroadcasting;

        this.username = username;
    }

    public ChatPanel getPanel() {
        return panel;
    }

    public void stop() {
        client.close();
    }

    @Override
    public void run() {
        panel.setSendButtonListener((inField) -> {
            String msg = inField.getText();

            if (msg.isEmpty()) {
                return;
            }

            try {
                if (gameClient instanceof GameHostClient hostClient) {

                    client.sendChatMessage(msg, hostClient.getPeer().address(), ChatClient.JOINER_PORT);

                    for (Peer spectator : hostClient.getSpectators()) {
                        try {
                            client.sendChatMessage(msg, spectator.address(), ChatClient.SPECTATOR_PORT);

                            if (isBroadcasting) {
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                } else if (gameClient instanceof GameJoinerClient joinerClient) {
                    client.sendChatMessage(msg, joinerClient.getHost().address());
                } else if (gameClient instanceof GameSpectatorClient spectatorClient) {
                    client.sendChatMessage(msg, spectatorClient.getHost().address());
                }

                SwingUtilities.invokeLater(() -> {
                    inField.setText("");

                    if (!isBroadcasting
                            || (gameClient instanceof GameHostClient || gameClient instanceof GameJoinerClient)) {
                        panel.appendChatMessage(username, msg);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(null, "The message could not be properly sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.setStickerButtonListeners((sticker) -> {
            try {
                if (gameClient instanceof GameHostClient hostClient) {

                    client.sendSticker(sticker, hostClient.getPeer().address(), ChatClient.JOINER_PORT);

                    for (Peer spectator : hostClient.getSpectators()) {
                        client.sendSticker(sticker, spectator.address(), ChatClient.SPECTATOR_PORT);

                        if (isBroadcasting) {
                            break;
                        }

                    }
                } else if (gameClient instanceof GameJoinerClient joinerClient) {
                    client.sendSticker(sticker, joinerClient.getHost().address());
                } else if (gameClient instanceof GameSpectatorClient spectatorClient) {
                    client.sendSticker(sticker, spectatorClient.getHost().address());
                }

                if (!isBroadcasting
                        || (gameClient instanceof GameHostClient || gameClient instanceof GameJoinerClient)) {
                    SwingUtilities.invokeLater(() -> panel.appendChatMessage(username, sticker));
                }
            } catch (IOException e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(null, "The sticker could not be properly sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        client.startChatMessageListener((m, p) -> SwingUtilities.invokeLater(() -> {
            panel.appendChatMessage(m);

            if (gameClient instanceof GameHostClient hostClient) {
                Peer joiner = hostClient.getPeer();

                if (p.port() != ChatClient.JOINER_PORT) {
                    try {
                        if (m.contentType() == ChatMessage.ContentType.STICKER) {
                            client.sendSticker(m, joiner.address(), ChatClient.JOINER_PORT);
                        } else {
                            client.sendChatMessage(m, joiner.address(), ChatClient.JOINER_PORT);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Set<Peer> peers = hostClient.getSpectators();

                peers.remove(p);

                for (Peer peer : peers) {
                    try {
                        if (m.contentType() == ChatMessage.ContentType.STICKER) {
                            client.sendSticker(m, peer.address(), ChatClient.SPECTATOR_PORT);
                        } else {
                            client.sendChatMessage(m, peer.address(), ChatClient.SPECTATOR_PORT);
                        }

                        if (isBroadcasting) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }
}
