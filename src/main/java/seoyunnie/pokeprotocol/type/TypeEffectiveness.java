package seoyunnie.pokeprotocol.type;

public enum TypeEffectiveness {
    NO_EFFECT("No effect"),
    NOT_VERY_EFFECTIVE("Not very effective"),
    NORMAL(""),
    SUPER_EFFECTIVE("Super effective");

    private final String message;

    TypeEffectiveness(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
