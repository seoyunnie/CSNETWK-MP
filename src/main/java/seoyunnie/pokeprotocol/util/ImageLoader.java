package seoyunnie.pokeprotocol.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public final class ImageLoader {
    private ImageLoader() {
    }

    public static BufferedImage loadFromAssets(String assetPath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(ImageLoader.class.getResourceAsStream("/assets/" + assetPath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();

            // Asset must exist; everything assumes it exits.
            System.exit(1);
        }

        return image;
    }

    public static BufferedImage loadFromURL(String url) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new URI(url).toURL());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
}
