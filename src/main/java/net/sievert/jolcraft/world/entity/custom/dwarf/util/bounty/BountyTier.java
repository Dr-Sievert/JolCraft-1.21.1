package net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty;

import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

public enum BountyTier {

    UNKNOWN(0, JolCraftLanguageKeys.UNKNOWN),
    NOVICE(1, JolCraftLanguageKeys.LEVEL_NOVICE),
    APPRENTICE(2, JolCraftLanguageKeys.LEVEL_APPRENTICE),
    JOURNEYMAN(3, JolCraftLanguageKeys.LEVEL_JOURNEYMAN),
    EXPERT(4, JolCraftLanguageKeys.LEVEL_EXPERT),
    MASTER(5, JolCraftLanguageKeys.LEVEL_MASTER);

    private final int value;
    private final String langKey;

    BountyTier(int value, String langKey) {
        this.value = value;
        this.langKey = langKey;
    }

    public String getLangKey() {
        return langKey;
    }


    /** For saving to DataComponent / NBT. */
    public int getValue() {
        return value;
    }

    /** Convenience for UI. */
    public Component getDisplayName() {
        return Component.translatable(langKey);
    }

    public static BountyTier fromValue(int value) {
        for (BountyTier tier : values()) {
            if (tier.value == value) return tier;
        }
        return UNKNOWN;
    }
}