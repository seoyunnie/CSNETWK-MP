package seoyunnie.pokeprotocol.gui.chat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import seoyunnie.pokeprotocol.network.message.ChatMessage;
import seoyunnie.pokeprotocol.sticker.Sticker;

public class ChatHistoryPanel extends JPanel {
    private static final int STICKER_SIZE = 30;

    private final JTextPane historyPane = new JTextPane();
    private final JScrollPane historyScrollPane = new JScrollPane(historyPane);

    public ChatHistoryPanel() {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Chat History"));
        setLayout(new GridBagLayout());

        historyPane.setBackground(Color.WHITE);
        historyPane.setEditable(false);
        historyPane.setFocusable(false);

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(ChatPanel.MARGIN, ChatPanel.MARGIN, ChatPanel.MARGIN, ChatPanel.MARGIN);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(historyScrollPane, constraints);
    }

    public void appendChatMessage(String username, String msg) {
        Document doc = historyPane.getDocument();

        try {
            doc.insertString(doc.getLength(), username + ": " + msg + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendChatMessage(String username, Sticker sticker) {
        Document doc = historyPane.getDocument();

        try {
            doc.insertString(doc.getLength(), username + ": ", null);

            historyPane.setCaretPosition(doc.getLength());
            historyPane.insertIcon(
                    new ImageIcon(sticker.image().getScaledInstance(STICKER_SIZE, STICKER_SIZE, Image.SCALE_SMOOTH)));

            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendChatMessage(ChatMessage chatMsg) {
        if (chatMsg.contentType() == ChatMessage.ContentType.TEXT) {
            appendChatMessage(chatMsg.senderName(), chatMsg.messageText());

            return;
        }

        appendChatMessage(chatMsg.senderName(), chatMsg.sticker());
    }
}
