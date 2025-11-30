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
import seoyunnie.pokeprotocol.network.Peer;
import seoyunnie.pokeprotocol.network.message.ChatMessage;

public class ChatManager implements Runnable {
    private final ChatClient client;
    private final GameClient gameClient;

    private final ChatPanel panel;

    private final boolean isBroadcasting;

    public ChatManager(GameClient gameClient, boolean isBroadcasting, String username) throws SocketException {
        this.client = new ChatClient(isBroadcasting, username);
        this.gameClient = gameClient;

        this.panel = new ChatPanel();

        this.isBroadcasting = isBroadcasting;
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

                    client.sendChatMessage(msg, hostClient.getPeer());

                    if (!isBroadcasting) {
                        for (Peer spectator : hostClient.getSpectators()) {
                            try {
                                client.sendChatMessage(msg, spectator);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (gameClient instanceof GameJoinerClient joinerClient) {
                    client.sendChatMessage(msg, joinerClient.getHost());
                }

                SwingUtilities.invokeLater(() -> inField.setText(""));
            } catch (IOException e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(null, "The message could not be properly sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.setStickerButtonListeners((sticker) -> {
            try {
                if (gameClient instanceof GameHostClient hostClient) {

                    client.sendSticker(sticker, hostClient.getPeer());

                    if (!isBroadcasting) {
                        for (Peer spectator : hostClient.getSpectators()) {
                            client.sendSticker(sticker, spectator);
                        }
                    }

                    return;
                } else if (gameClient instanceof GameJoinerClient joinerClient) {
                    client.sendSticker(sticker, joinerClient.getHost());
                }
            } catch (IOException e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(null, "The sticker could not be properly sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        client.startChatMessageListener((m, p) -> SwingUtilities.invokeLater(() -> {
            panel.appendChatMessage(m);

            if (!isBroadcasting && gameClient instanceof GameHostClient hostClient) {
                Set<Peer> peers = hostClient.getSpectators();

                peers.add(hostClient.getPeer());
                peers.remove(p);

                for (Peer peer : peers) {
                    try {
                        if (m.contentType() == ChatMessage.ContentType.STICKER) {
                            client.sendSticker(m, peer);

                            continue;
                        }

                        client.sendChatMessage(m, peer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }
}
