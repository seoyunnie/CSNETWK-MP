package seoyunnie.pokeprotocol.gui.battle;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import seoyunnie.pokeprotocol.game.BattlePokemon;
import seoyunnie.pokeprotocol.gui.pokemon.PokemonSprite;
import seoyunnie.pokeprotocol.util.ImageLoader;

public class BattlePanel extends JPanel {
    public static final int WIDTH = 720;
    public static final int HEIGHT = 336;

    private final BufferedImage backgroundImage = ImageLoader.loadFromAssets("background");

    private final BattlePokemon ownPokemon;
    private final BattlePokemon enemyPokemon;

    public BattlePanel(BattlePokemon ownPokemon, BattlePokemon enemyPokemon) {
        this.ownPokemon = ownPokemon;
        this.enemyPokemon = enemyPokemon;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);

        Graphics2D graphics2d = (Graphics2D) graphics.create();

        new PokemonHealthIndicator(enemyPokemon.getBasePokemon()).drawEnemy(graphics2d, enemyPokemon.getCurrentHP());
        new PokemonSprite(enemyPokemon.getBasePokemon()).drawEnemy(graphics2d, this);

        new PokemonHealthIndicator(ownPokemon.getBasePokemon()).drawOwn(graphics2d, ownPokemon.getCurrentHP());
        new PokemonSprite(ownPokemon.getBasePokemon()).drawOwn(graphics2d, this);

        graphics2d.dispose();
    }
}
