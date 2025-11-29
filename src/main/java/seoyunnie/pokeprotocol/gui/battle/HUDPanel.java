package seoyunnie.pokeprotocol.gui.battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class HUDPanel extends JPanel {
    public static final int WIDTH = BattlePanel.WIDTH;
    public static final int HEIGHT = BattlePanel.HEIGHT / 3;

    public static final int MARGIN = 5;

    private final HUDMessagePanel messagePanel = new HUDMessagePanel();

    public HUDPanel() {
        setBackground(Color.BLACK);

        setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        setLayout(new GridLayout(1, 1, MARGIN, MARGIN));

        add(messagePanel);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public void setMessage(String msg) {
        messagePanel.setMessage((graphics2d) -> {
            if (msg.contains("  ")) {
                String[] messages = msg.split("  ", 2);

                graphics2d.drawString(messages[0], 25, 40);
                graphics2d.drawString(messages[1], 25, 80);
            } else {
                graphics2d.drawString(msg, 25, 40);
            }
        });

        repaint();
    }
}
