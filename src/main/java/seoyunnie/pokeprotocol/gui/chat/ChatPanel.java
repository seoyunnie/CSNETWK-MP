package seoyunnie.pokeprotocol.gui.chat;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JTextField;

import seoyunnie.pokeprotocol.gui.battle.BattlePanel;
import seoyunnie.pokeprotocol.gui.battle.HUDPanel;
import seoyunnie.pokeprotocol.network.message.ChatMessage;
import seoyunnie.pokeprotocol.sticker.Sticker;

public class ChatPanel extends JPanel {
    private static final int WIDTH = 360;
    private static final int HEIGHT = BattlePanel.HEIGHT + HUDPanel.HEIGHT;

    public static final int MARGIN = 10;

    private final MessageInputPanel messageInputPanel;
    private final StickerSelectionPanel stickerSelectionPanel;
    private final ChatHistoryPanel chatHistoryPanel;

    public ChatPanel() {
        this.messageInputPanel = new MessageInputPanel();
        this.stickerSelectionPanel = new StickerSelectionPanel();
        this.chatHistoryPanel = new ChatHistoryPanel();

        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(MARGIN, MARGIN, MARGIN / 2, MARGIN);

        add(messageInputPanel, constraints);

        constraints.gridy++;
        constraints.insets.top = MARGIN / 2;

        add(stickerSelectionPanel, constraints);

        constraints.gridy++;
        constraints.insets.bottom = MARGIN;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(chatHistoryPanel, constraints);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public void setSendButtonListener(Consumer<JTextField> cb) {
        messageInputPanel.setSendButtonListener(cb);
    }

    public void setStickerButtonListeners(Consumer<Sticker> cb) {
        stickerSelectionPanel.setStickerButtonListeners(cb);
    }

    public void appendChatMessage(ChatMessage msg) {
        chatHistoryPanel.appendChatMessage(msg);
    }
}
