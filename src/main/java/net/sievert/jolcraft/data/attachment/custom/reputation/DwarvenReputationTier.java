package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;

public enum DwarvenReputationTier {

    STRANGER(0),
    KNOWN_FACE(1),
    TRUSTED(2),
    RESPECTED(3),
    BLOOD_KIN(4);

    private final int id;

    DwarvenReputationTier(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public String idToString() {
        return String.valueOf(id);
    }

    public String langKey() {
        return AbstractLanguageKeys.mod(
                JolCraftStrings.dotted(
                        JolCraftStrings.underscored(JolCraftAttachmentIds.DWARVEN_REPUTATION, JolCraftDictionary.TIER),
                        idToString()
                )
        );
    }

    public static DwarvenReputationTier fromId(int id) {
        for (DwarvenReputationTier tier : values()) {
            if (tier.id == id) return tier;
        }
        return STRANGER;
    }
}
