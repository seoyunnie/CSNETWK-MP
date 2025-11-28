package seoyunnie.pokeprotocol.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static BufferedImage loadFromAssets(String path) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(ImageUtils.class.getResourceAsStream("/assets/" + path + ".png"));
        } catch (IOException e) {
            e.printStackTrace();

            // Asset must exist; everything assumes it exits.
            System.exit(1);
        }

        return image;
    }

    public static String encodeImageIconToBase64String(ImageIcon imageIcon) {
        Image img = imageIcon.getImage();
        BufferedImage bufferedImg = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        bufferedImg.getGraphics().drawImage(img, 0, 0, null);

        var byteStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImg, "png", byteStream);
        } catch (IOException e) {
            e.printStackTrace();

            // This should NEVER happen...
            System.exit(1);
        }

        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }
}
