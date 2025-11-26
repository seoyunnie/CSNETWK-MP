package seoyunnie.pokeprotocol.gui.battle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.function.Consumer;

import javax.swing.JPanel;

public class HUDMessagePanel extends JPanel {
    private static final int CORNER_RADIUS = 5;

    private Consumer<Graphics2D> drawMessage;

    public HUDMessagePanel() {
        setOpaque(false);
    }

    public void setMessage(Consumer<Graphics2D> cb) {
        drawMessage = cb;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2d = (Graphics2D) graphics.create();

        graphics2d.setColor(Color.CYAN.darker().darker().darker().darker());

        graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

        graphics2d.setColor(Color.WHITE);

        Stroke originalStroke = graphics2d.getStroke();

        int borderWidth = 4;

        graphics2d.setStroke(new BasicStroke(borderWidth));

        graphics2d.drawRoundRect(
                borderWidth / 2, borderWidth / 2,
                getWidth() - borderWidth, getHeight() - borderWidth,
                CORNER_RADIUS, CORNER_RADIUS);

        graphics2d.setStroke(originalStroke);

        graphics2d.setColor(Color.WHITE);
        graphics2d.setFont(new Font("Arial", Font.BOLD, 25));

        if (drawMessage != null) {
            drawMessage.accept(graphics2d);
        }

        graphics2d.dispose();
    }
}
