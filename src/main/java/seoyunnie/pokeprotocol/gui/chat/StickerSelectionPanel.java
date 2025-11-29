package seoyunnie.pokeprotocol.gui.chat;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import seoyunnie.pokeprotocol.sticker.ChatStickers;
import seoyunnie.pokeprotocol.sticker.Sticker;

public class StickerSelectionPanel extends JPanel {
    private static final int BUTTON_SIZE = 25;

    private final JButton[] sendStickerButtons;

    public StickerSelectionPanel() {
        Sticker[] stickers = ChatStickers.values();

        this.sendStickerButtons = new JButton[stickers.length];

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Message Chat"),
                BorderFactory.createEmptyBorder(ChatFrame.MARGIN, ChatFrame.MARGIN, ChatFrame.MARGIN,
                        ChatFrame.MARGIN)));
        setLayout(new GridLayout(1, stickers.length, ChatFrame.MARGIN, ChatFrame.MARGIN));

        for (int i = 0; i < stickers.length; i++) {
            sendStickerButtons[i] = new JButton(
                    new ImageIcon(stickers[i].image().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH)));

            add(sendStickerButtons[i]);
        }
    }

    public void setStickerButtonListeners(Consumer<Sticker> cb) {
        Sticker[] stickers = ChatStickers.values();

        for (int i = 0; i < stickers.length; i++) {
            Sticker sticker = stickers[i];

            sendStickerButtons[i].addActionListener((e) -> cb.accept(sticker));
        }
    }
}
