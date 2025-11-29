package seoyunnie.pokeprotocol.sticker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import seoyunnie.pokeprotocol.util.ImageLoader;

public record Sticker(BufferedImage image) {
    public Sticker(String name) {
        this(ImageLoader.loadFromAssets("stickers/" + name));
    }

    public static Sticker fromBase64String(String base64Str) throws IOException {
        return new Sticker(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64Str))));
    }

    public String encodeToBase64String() {
        var byteStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", byteStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }
}
