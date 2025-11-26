package seoyunnie.pokeprotocol.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.pokemon.PokemonSprite;
import seoyunnie.pokeprotocol.type.TypeIcon;

public class PokemonDetailsPanel extends JPanel {
    private static final int WIDTH = 491;
    private static final int HEIGHT = PokemonSprite.HEIGHT + 24;
    private static final int CORNER_RADIUS = 5;

    private static final int MARGIN = 10;

    private static final int CONTAINER_HEIGHT = HEIGHT - MARGIN * 2;

    private final Pokemon pokemon;

    public PokemonDetailsPanel(Pokemon pokemon) {
        this.pokemon = pokemon;

        setOpaque(false);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void drawContainer(Graphics2D graphics2d, int x, int y, int width, int height) {
        graphics2d.setColor(Color.GRAY.darker().darker().darker());

        graphics2d.fillRoundRect(x, y, width, height, CORNER_RADIUS, CORNER_RADIUS);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2d = (Graphics2D) graphics.create();

        graphics2d.setColor(Color.GRAY.darker());

        graphics2d.fillRoundRect(0, 0, WIDTH, HEIGHT, CORNER_RADIUS, CORNER_RADIUS);

        int typeNameIconWidth = TypeIcon.NAMED_ICON_WIDTH / 2;
        int typeNameIconHeight = TypeIcon.NAMED_ICON_HEIGHT / 2;

        int overviewCntrWidth = PokemonSprite.WIDTH + (typeNameIconWidth * 2) + 25;

        drawContainer(graphics2d, MARGIN, MARGIN, overviewCntrWidth, CONTAINER_HEIGHT);

        var sprite = new PokemonSprite(pokemon);
        int spriteSize = 12;

        sprite.draw(graphics2d, PokemonSprite.Direction.FRONT, spriteSize, spriteSize, 1, this);

        graphics2d.setColor(Color.WHITE);
        graphics2d.setFont(new Font("Arial", Font.BOLD, 20));

        graphics2d.drawString(pokemon.name(), PokemonSprite.WIDTH + 20, 40);

        new TypeIcon(pokemon.firstType()).drawWithName(
                graphics2d,
                PokemonSprite.WIDTH + 20, 50,
                typeNameIconWidth, typeNameIconHeight,
                this);

        if (pokemon.isDualTyped()) {
            new TypeIcon(pokemon.secondType()).drawWithName(
                    graphics2d,
                    PokemonSprite.WIDTH + typeNameIconWidth + 25, 50,
                    typeNameIconWidth, typeNameIconHeight,
                    this);
        }

        int statsCntrWidth = overviewCntrWidth + 20;

        drawContainer(graphics2d, statsCntrWidth, MARGIN, 70, CONTAINER_HEIGHT);

        graphics2d.setColor(Color.WHITE);
        graphics2d.setFont(new Font("Arial", Font.PLAIN, 10));

        graphics2d.drawString("HP   ", overviewCntrWidth + 28, 26);
        graphics2d.drawString("Atk  ", overviewCntrWidth + 28, 41);
        graphics2d.drawString("Def  ", overviewCntrWidth + 28, 56);
        graphics2d.drawString("SpA  ", overviewCntrWidth + 28, 71);
        graphics2d.drawString("SpD  ", overviewCntrWidth + 28, 86);
        graphics2d.drawString("Spe  ", overviewCntrWidth + 28, 101);

        graphics2d.drawString(Integer.toString(pokemon.stats().hp()), overviewCntrWidth + 64, 26);
        graphics2d.drawString(Integer.toString(pokemon.stats().attack()), overviewCntrWidth + 64, 41);
        graphics2d.drawString(Integer.toString(pokemon.stats().defense()), overviewCntrWidth + 64, 56);
        graphics2d.drawString(Integer.toString(pokemon.stats().specialAttack()), overviewCntrWidth + 64, 71);
        graphics2d.drawString(Integer.toString(pokemon.stats().specialDefense()), overviewCntrWidth + 64, 86);
        graphics2d.drawString(Integer.toString(pokemon.stats().speed()), overviewCntrWidth + 64, 101);

        drawContainer(graphics2d, statsCntrWidth + 80, MARGIN, 120, CONTAINER_HEIGHT);

        graphics2d.setColor(Color.WHITE);
        graphics2d.setFont(new Font("Arial", Font.PLAIN, 12));

        int moveTypeIconY = 86;
        int moveTypeIconSize = CONTAINER_HEIGHT / 4 - 5;
        int moveNameY = 101;

        for (Move move : pokemon.moves()) {
            new TypeIcon(move.type()).draw(
                    graphics2d,
                    statsCntrWidth + 84, moveTypeIconY,
                    moveTypeIconSize, moveTypeIconSize,
                    this);

            graphics2d.drawString(move.name(), statsCntrWidth + 84 + moveTypeIconSize + 4, moveNameY);

            moveTypeIconY -= 24;
            moveNameY -= 24;
        }

        graphics2d.dispose();
    }
}
