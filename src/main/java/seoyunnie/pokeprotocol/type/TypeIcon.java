package seoyunnie.pokeprotocol.type;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import seoyunnie.pokeprotocol.util.ImageLoader;

public class TypeIcon {
    public static final int ICON_SIZE = 60;

    public static final int NAMED_ICON_WIDTH = 140;
    public static final int NAMED_ICON_HEIGHT = 34;

    private final BufferedImage icon;
    private final BufferedImage namedIcon;

    public TypeIcon(String typeName) {
        this.icon = ImageLoader.loadFromAssets("types/" + typeName + "-icon");
        this.namedIcon = ImageLoader.loadFromAssets("types/" + typeName + "-name");
    }

    public TypeIcon(Type type) {
        this(type.name());
    }

    public void draw(Graphics2D graphics2d, int x, int y, int width, int height, ImageObserver imgObserver) {
        graphics2d.drawImage(icon, x, y, width, height, imgObserver);
    }

    public void drawWithName(Graphics2D graphics2d, int x, int y, int width, int height, ImageObserver imgObserver) {
        graphics2d.drawImage(namedIcon, x, y, width, height, imgObserver);
    }
}
