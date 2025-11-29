package seoyunnie.pokeprotocol.sticker;

import java.util.ArrayList;
import java.util.List;

public final class ChatStickers {
    private static final List<Sticker> STICKERS = new ArrayList<>();

    public static final Sticker WOBBUFFET_MARU = add(new Sticker("wobbuffet_maru"));
    public static final Sticker WOBBUFFET_BATSU = add(new Sticker("wobbuffet_batsu"));

    private ChatStickers() {}

    private static Sticker add(Sticker sticker) {
        STICKERS.add(sticker);

        return sticker;
    }

    public static Sticker[] values() {
        return STICKERS.toArray(Sticker[]::new);
    }
}
