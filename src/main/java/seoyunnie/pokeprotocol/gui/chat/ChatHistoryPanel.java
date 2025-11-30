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

public class ChatHistoryPanel extends JPanel {
    private static final int STICKER_SIZE = 30;

    private final JTextPane historyPane = new JTextPane();
    private final JScrollPane historyScrollPane = new JScrollPane(historyPane);

    public ChatHistoryPanel() {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Chat History"));
        setLayout(new GridBagLayout());

        historyPane.setBackground(Color.WHITE);
        historyPane.setEditable(false);

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(ChatPanel.MARGIN, ChatPanel.MARGIN, ChatPanel.MARGIN, ChatPanel.MARGIN);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(historyScrollPane, constraints);
    }

    public void appendChatMessage(ChatMessage chatMsg) {
        Document doc = historyPane.getDocument();

        try {
            doc.insertString(doc.getLength(), chatMsg.senderName() + ": ", null);

            if (chatMsg.contentType() == ChatMessage.ContentType.TEXT) {
                doc.insertString(doc.getLength(), chatMsg.messageText(), null);
            } else {
                historyPane.insertIcon(new ImageIcon(
                        chatMsg.sticker().image().getScaledInstance(STICKER_SIZE, STICKER_SIZE, Image.SCALE_SMOOTH)));
            }

            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
