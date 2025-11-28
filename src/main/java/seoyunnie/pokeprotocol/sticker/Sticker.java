package seoyunnie.pokeprotocol.sticker;

import java.awt.image.BufferedImage;

import seoyunnie.pokeprotocol.util.ImageUtils;

public record Sticker(BufferedImage icon) {
    public Sticker(String name) {
        this(ImageUtils.loadFromAssets("stickers/" + name));
    }
}
