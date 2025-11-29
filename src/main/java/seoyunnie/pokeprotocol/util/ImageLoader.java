package seoyunnie.pokeprotocol.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageLoader {
    private ImageLoader() {}

    public static BufferedImage loadFromAssets(String path) {
        try {
            return ImageIO.read(ImageLoader.class.getResourceAsStream("/assets/" + path + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
