package seoyunnie.pokeprotocol.gui.battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

import seoyunnie.pokeprotocol.gui.pokemon.TypeIcon;
import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.type.TypeEffectiveness;

public class MoveButton extends JButton {
    private final Move move;
    private final TypeEffectiveness moveEffectiveness;

    public MoveButton(Move move, TypeEffectiveness moveEffectiveness) {
        this.move = move;
        this.moveEffectiveness = moveEffectiveness;

        setBackground(Color.GRAY.darker());

        setPreferredSize(new Dimension(HUDPanel.WIDTH / 4, (HUDPanel.HEIGHT / 2) - 15));
    }

    public Move getMove() {
        return move;
    }

    public TypeEffectiveness getMoveEffectiveness() {
        return moveEffectiveness;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2d = (Graphics2D) graphics.create();

        new TypeIcon(move.type()).draw(graphics2d, 5, 5, getHeight() - 10, getHeight() - 10, this);

        graphics2d.setColor(Color.WHITE);
        graphics2d.setFont(new Font("Arial", Font.BOLD, 13));

        graphics2d.drawString(move.name(), 50, 20);

        if (moveEffectiveness != TypeEffectiveness.NORMAL) {
            graphics2d.setFont(new Font("Arial", Font.PLAIN, 10));

            graphics2d.drawString(moveEffectiveness.getMessage(), 50, 37);
        }

        graphics2d.dispose();
    }
}
