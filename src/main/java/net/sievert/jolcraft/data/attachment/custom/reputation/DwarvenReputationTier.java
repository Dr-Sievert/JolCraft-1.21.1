package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

public enum DwarvenReputationTier implements JolCraftEnumHelper.IntId {

    STRANGER(0),
    KNOWN_FACE(1),
    TRUSTED(2),
    RESPECTED(3),
    BLOOD_KIN(4);

    private final int id;

    DwarvenReputationTier(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public String idToString() {
        return String.valueOf(id);
    }

    public String langKey() {
        return AbstractLanguageKeys.mod(
                JolCraftStrings.dotted(
                        JolCraftStrings.underscored(
                                JolCraftAttachmentIds.DWARVEN_REPUTATION,
                                JolCraftDictionary.TIER
                        ),
                        idToString()
                )
        );
    }

    public static DwarvenReputationTier fromId(int id) {
        return JolCraftEnumHelper.byIntIdExact(DwarvenReputationTier.class, id, STRANGER);
    }
}