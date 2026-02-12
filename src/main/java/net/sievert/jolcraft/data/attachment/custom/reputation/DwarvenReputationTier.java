package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

public enum DwarvenReputationTier {

    STRANGER(0, JolCraftLanguageKeys.REPUTATION_TIER_0),
    KNOWN_FACE(1, JolCraftLanguageKeys.REPUTATION_TIER_1),
    TRUSTED(2, JolCraftLanguageKeys.REPUTATION_TIER_2),
    RESPECTED(3, JolCraftLanguageKeys.REPUTATION_TIER_3),
    BLOOD_KIN(4, JolCraftLanguageKeys.REPUTATION_TIER_4);

    private final int id;
    private final String langKey;

    DwarvenReputationTier(int id, String langKey) {
        this.id = id;
        this.langKey = langKey;
    }

    public int id() {
        return id;
    }

    public String idToString() {
        return String.valueOf(id);
    }

    public String langKey() {
        return langKey;
    }

    public static DwarvenReputationTier fromId(int id) {
        for (DwarvenReputationTier tier : values()) {
            if (tier.id == id) return tier;
        }
        return STRANGER;
    }

    public DwarvenReputationTier next() {
        return fromId(this.id + 1);
    }
}
