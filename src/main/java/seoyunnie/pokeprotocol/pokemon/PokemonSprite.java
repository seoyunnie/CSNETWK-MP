package seoyunnie.pokeprotocol.pokemon;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import seoyunnie.pokeprotocol.util.ImageLoader;

public class PokemonSprite {
    public enum Direction {
        FRONT,
        BACK;
    }

    public static final int WIDTH = 96;
    public static final int HEIGHT = 96;

    public static final int OWN_X = 50;
    public static final int OWN_Y = 105;
    public static final int OWN_SCALE = 3;

    public static final int ENEMY_X = 430;
    public static final int ENEMY_Y = 45;
    public static final int ENEMY_SCALE = 2;

    private final BufferedImage front;
    private final BufferedImage back;

    public PokemonSprite(String pokemonName) {
        this.front = ImageLoader.loadFromAssets("sprites/" + pokemonName + "-front");
        this.back = ImageLoader.loadFromAssets("sprites/" + pokemonName + "-back");
    }

    public PokemonSprite(Pokemon pokemon) {
        this(pokemon.name());
    }

    public void draw(Graphics2D graphics2d, Direction direction, int x, int y, int scale, ImageObserver imgObserver) {
        graphics2d.drawImage(switch (direction) {
            case Direction.FRONT -> front;
            case Direction.BACK -> back;
        }, x, y, WIDTH * scale, HEIGHT * scale, imgObserver);
    }

    public void drawOwn(Graphics2D graphics2d, ImageObserver imgObserver) {
        draw(graphics2d, Direction.BACK, OWN_X, OWN_Y, OWN_SCALE, imgObserver);
    }

    public void drawEnemy(Graphics2D graphics2d, ImageObserver imgObserver) {
        draw(graphics2d, Direction.FRONT, ENEMY_X, ENEMY_Y, ENEMY_SCALE, imgObserver);
    }
}
