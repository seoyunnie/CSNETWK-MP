package seoyunnie.pokeprotocol.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatBoosts {
    public static final int MAX_ATTACK_USES = 5;
    public static final int MAX_DEFENSE_USES = 5;

    @JsonProperty("special_attack_uses")
    private int specialAttackUses = MAX_ATTACK_USES;
    @JsonProperty("special_defense_uses")
    private int specialDefenseUses = MAX_DEFENSE_USES;

    public int getSpecialAttackUses() {
        return specialAttackUses;
    }

    public void useSpecialAttack() {
        this.specialAttackUses -= 1;
    }

    public int getSpecialDefenseUses() {
        return specialDefenseUses;
    }

    public void useSpecialDefense() {
        this.specialDefenseUses -= 1;
    }
}
