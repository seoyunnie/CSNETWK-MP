package seoyunnie.pokeprotocol.gui.battle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import seoyunnie.pokeprotocol.gui.pokemon.PokemonSprite;
import seoyunnie.pokeprotocol.pokemon.Pokemon;
import seoyunnie.pokeprotocol.pokemon.PokemonStats;

public class PokemonHealthIndicator {
    private static final int HP_BAR_HEIGHT = 10;
    private static final int HP_BAR_CORNER_RADIUS = 10;

    private final Pokemon pokemon;

    public PokemonHealthIndicator(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    private Color getHPBarColor(int hpPercentage) {
        if (hpPercentage <= PokemonStats.CRITICAL_HP_PERCENTAGE) {
            return Color.RED;
        } else if (hpPercentage <= PokemonStats.LOW_HP_PERCENTAGE) {
            return Color.YELLOW;
        }

        return Color.GREEN;
    }

    private void draw(Graphics2D graphics2d, int x, int y, int spriteScale, int currHP) {
        graphics2d.setColor(Color.WHITE);
        graphics2d.setFont(new Font("Arial", Font.BOLD, 17));

        graphics2d.drawString(pokemon.name(), x + HP_BAR_HEIGHT, y - HP_BAR_HEIGHT);

        int hpPercentage = (int) ((float) currHP / pokemon.stats().hp() * 100);

        graphics2d.setColor(getHPBarColor(hpPercentage));

        int hpBarWidth = PokemonSprite.WIDTH * spriteScale;
        int filledHPBarWidth = (int) (hpPercentage / 100f * hpBarWidth);

        graphics2d.fillRoundRect(x, y, filledHPBarWidth, HP_BAR_HEIGHT, HP_BAR_CORNER_RADIUS, HP_BAR_CORNER_RADIUS);

        graphics2d.setColor(Color.WHITE);

        Stroke originalStroke = graphics2d.getStroke();

        graphics2d.setStroke(new BasicStroke(2));

        graphics2d.drawRoundRect(x, y, hpBarWidth, HP_BAR_HEIGHT, HP_BAR_CORNER_RADIUS, HP_BAR_CORNER_RADIUS);

        graphics2d.setStroke(originalStroke);
        graphics2d.setFont(new Font("Arial", Font.BOLD, 10));

        graphics2d.drawString(currHP + "/" + pokemon.stats().hp(), x + hpBarWidth + 4, y + HP_BAR_HEIGHT - 2);
    }

    public void drawOwn(Graphics2D graphics2d, int currHP) {
        draw(graphics2d, PokemonSprite.OWN_X, PokemonSprite.OWN_Y, PokemonSprite.OWN_SCALE, currHP);
    }

    public void drawEnemy(Graphics2D graphics2d, int currHP) {
        draw(graphics2d, PokemonSprite.ENEMY_X, PokemonSprite.ENEMY_Y, PokemonSprite.ENEMY_SCALE, currHP);
    }
}
