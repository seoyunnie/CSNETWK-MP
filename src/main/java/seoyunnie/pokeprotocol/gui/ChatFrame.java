package seoyunnie.pokeprotocol.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import seoyunnie.pokeprotocol.network.ChatClient;
import seoyunnie.pokeprotocol.network.message.ChatMessage;
import seoyunnie.pokeprotocol.sticker.ChatStickers;

public class ChatFrame extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 426;

    private static final int STICKER_BUTTON_SIZE = 25;

    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    private final JButton sendStickerOButton = new JButton(
            new ImageIcon(ChatStickers.WobbuffetMaru.icon().getScaledInstance(
                    STICKER_BUTTON_SIZE, STICKER_BUTTON_SIZE,
                    Image.SCALE_SMOOTH)));
    private final JButton sendStickerXButton = new JButton(
            new ImageIcon(ChatStickers.WobbuffetBatsu.icon().getScaledInstance(
                    STICKER_BUTTON_SIZE, STICKER_BUTTON_SIZE,
                    Image.SCALE_SMOOTH)));

    private final JTextPane historyPane = new JTextPane();
    private final JScrollPane historyScrollPane = new JScrollPane(historyPane);

    private final ChatClient client;

    public ChatFrame(String username, InetAddress broadcastAddr) throws SocketException {
        super("Global Chat");

        this.client = new ChatClient(broadcastAddr, username);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();
        addListeners();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setResizable(false);

        if (!client.isTesting()) {
            client.startChatMessageListener((chatMsg) -> SwingUtilities.invokeLater(
                    () -> {
                        Document document = historyPane.getDocument();

                        try {
                            if (chatMsg.contentType() == ChatMessage.ContentType.TEXT) {
                                document.insertString(
                                        document.getLength(),
                                        chatMsg.senderName() + ": " + chatMsg.messageText() + "\n",
                                        null);
                            } else {
                                document.insertString(document.getLength(), chatMsg.senderName() + ": ", null);
                                historyPane.insertIcon(chatMsg.sticker());
                                document.insertString(document.getLength(), "\n", null);
                            }
                        } catch (BadLocationException e) {
                        }
                    }));
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 4, 10);
        constraints.weightx = 1.0;
        constraints.gridwidth = 4;

        add(new JLabel("Message Global Chat"), constraints);

        constraints.gridy++;
        constraints.insets.top = 3;
        constraints.insets.right = 5;
        constraints.insets.bottom = 10;
        constraints.gridwidth = 1;

        add(inputField, constraints);

        sendButton.setBackground(Color.BLUE);
        sendButton.setForeground(Color.WHITE);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx++;
        constraints.insets.left = 5;
        constraints.weightx = 0.0;

        add(sendButton, constraints);
        getRootPane().setDefaultButton(sendButton);

        sendStickerOButton.setBackground(Color.WHITE);

        constraints.gridx++;

        add(sendStickerOButton, constraints);

        sendStickerXButton.setBackground(Color.WHITE);

        constraints.gridx++;
        constraints.insets.right = 10;

        add(sendStickerXButton, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets.left = 10;
        constraints.insets.bottom = 3;
        constraints.weightx = 1.0;
        constraints.gridwidth = 4;

        add(new JLabel("Chat History"), constraints);

        historyPane.setEditable(false);

        if (client.isTesting()) {
            Document document = historyPane.getDocument();

            try {
                document.insertString(
                        document.getLength(), "THIS FEATURE IS CURRENTLY DISABLED!\n\nTESTING CLIENT",
                        null);
            } catch (BadLocationException e) {
            }
        }

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy++;
        constraints.insets.top = 3;
        constraints.insets.bottom = 10;
        constraints.weighty = 1.0;

        add(historyScrollPane, constraints);
    }

    private void addListeners() {
        sendButton.addActionListener((evt) -> {
            String msg = inputField.getText();

            if (msg.isEmpty()) {
                return;
            }

            try {
                client.sendChatMessage(msg);

                SwingUtilities.invokeLater(() -> inputField.setText(""));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "The message could not be sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        sendStickerOButton.addActionListener((evt) -> {
            try {
                client.sendSticker(new ImageIcon(ChatStickers.WobbuffetMaru.icon()));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "The sticker could not be sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        sendStickerXButton.addActionListener((evt) -> {
            try {
                client.sendSticker(new ImageIcon(ChatStickers.WobbuffetBatsu.icon()));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "The sticker could not be sent.", "Failed to Send",
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
