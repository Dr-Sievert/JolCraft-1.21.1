package net.sievert.jolcraft.world.item.util.bounty;

import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;

public enum BountyTier implements JolCraftEnumHelper.IntId {

    UNKNOWN(0),
    NOVICE(1),
    APPRENTICE(2),
    JOURNEYMAN(3),
    EXPERT(4),
    MASTER(5);

    private final int value;
    private final String langKey;

    BountyTier(int value) {
        this.value = value;

        this.langKey = (value == 0)
                ? AbstractLanguageKeys.mod(JolCraftDictionary.UNKNOWN)
                : AbstractLanguageKeys.mod(DwarfMerchantData.Level.langKeyFromId(value));
    }

    /** For saving to DataComponent / NBT. */
    @Override
    public int getId() {
        return value;
    }

    public String getLangKey() {
        return langKey;
    }

    /** Convenience for UI. */
    public Component getDisplayName() {
        return Component.translatable(langKey);
    }

    public static BountyTier fromValue(int value) {
        return JolCraftEnumHelper.byIntIdExact(BountyTier.class, value, UNKNOWN);
    }
}