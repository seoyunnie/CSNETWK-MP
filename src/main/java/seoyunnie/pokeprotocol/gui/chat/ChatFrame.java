package seoyunnie.pokeprotocol.gui.chat;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import seoyunnie.pokeprotocol.network.ChatClient;

public class ChatFrame extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 426;

    private final MessageInputPanel messageInputPanel;
    private final StickerSelectionPanel stickerSelectionPanel;
    private final ChatHistoryPanel chatHistoryPanel;

    private final ChatClient client;

    public ChatFrame(String username, InetAddress broadcastAddr) throws SocketException {
        super("Global Chat");

        this.messageInputPanel = new MessageInputPanel();
        this.stickerSelectionPanel = new StickerSelectionPanel();
        this.chatHistoryPanel = new ChatHistoryPanel();

        this.client = new ChatClient(broadcastAddr, username);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();
        addListeners();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setResizable(false);

        client.startChatMessageListener((m) -> SwingUtilities.invokeLater(() -> chatHistoryPanel.appendChatMessage(m)));
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 5, 10);

        add(messageInputPanel, constraints);

        messageInputPanel.setDefaultButton(getRootPane());

        constraints.gridy++;
        constraints.insets.top = 5;

        add(stickerSelectionPanel, constraints);

        constraints.gridy++;
        constraints.insets.bottom = 10;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(chatHistoryPanel, constraints);
    }

    private void addListeners() {
        messageInputPanel.addButtonClickListener((inField) -> {
            String msg = inField.getText();

            if (msg.isEmpty()) {
                return;
            }

            try {
                client.sendChatMessage(msg);

                SwingUtilities.invokeLater(() -> inField.setText(""));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "The message could not be sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        stickerSelectionPanel.addButtonClickListener((sticker) -> {
            try {
                client.sendSticker(sticker);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "The sticker could not be sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                client.stopChatMessageListener();

                ChatFrame.this.dispose();
            };
        });
    }
}
