package seoyunnie.pokeprotocol.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import seoyunnie.pokeprotocol.gui.chat.ChatPanel;

public class GameFrame extends JFrame {
    public GameFrame(GamePanel gamePanel, ChatPanel chatPanel) {
        super("PokeProtocol");

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        add(gamePanel, BorderLayout.LINE_START);
        add(chatPanel, BorderLayout.LINE_END);

        pack();
        setResizable(false);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
