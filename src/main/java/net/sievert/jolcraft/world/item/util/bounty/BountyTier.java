package net.sievert.jolcraft.world.item.util.bounty;

import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BountyTier {

    UNKNOWN(0, JolCraftLanguageKeys.UNKNOWN),
    NOVICE(1, JolCraftLanguageKeys.LEVEL_NOVICE),
    APPRENTICE(2, JolCraftLanguageKeys.LEVEL_APPRENTICE),
    JOURNEYMAN(3, JolCraftLanguageKeys.LEVEL_JOURNEYMAN),
    EXPERT(4, JolCraftLanguageKeys.LEVEL_EXPERT),
    MASTER(5, JolCraftLanguageKeys.LEVEL_MASTER);

    private static final Map<Integer, BountyTier> BY_VALUE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(BountyTier::getValue, t -> t));

    private final int value;
    private final String langKey;
    private final Component displayName;

    BountyTier(int value, String langKey) {
        this.value = value;
        this.langKey = langKey;
        this.displayName = Component.translatable(langKey);
    }

    /** For saving to DataComponent / NBT. */
    public int getValue() {
        return value;
    }

    public String getLangKey() {
        return langKey;
    }

    /** Convenience for UI. */
    public Component getDisplayName() {
        return displayName;
    }

    public static BountyTier fromValue(int value) {
        return BY_VALUE.getOrDefault(value, UNKNOWN);
    }
}