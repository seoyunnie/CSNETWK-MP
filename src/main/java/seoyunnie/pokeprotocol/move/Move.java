package seoyunnie.pokeprotocol.move;

import seoyunnie.pokeprotocol.type.Type;

public record Move(String name, Type type, int power, boolean isSpecial) {
    public Move(String name, Type type, int power) {
        this(name, type, power, false);
    }
}
