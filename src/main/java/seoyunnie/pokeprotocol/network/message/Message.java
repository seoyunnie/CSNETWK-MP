package seoyunnie.pokeprotocol.network.message;

public interface Message {
    enum Type {
        HANDSHAKE_REQUEST,
        HANDSHAKE_RESPONSE,
        SPECTATOR_REQUEST,
        BATTLE_SETUP,
        ATTACK_ANNOUNCE,
        DEFENSE_ANNOUNCE,
        CALCULATION_REPORT,
        CALCULATION_CONFIRM,
        RESOLUTION_REQUEST,
        GAME_OVER,
        CHAT_MESSAGE;
    }

    String toString();

    default byte[] encode() {
        return toString().getBytes();
    }
}
