package net.sievert.jolcraft.data.recipe.custom.bounty;

import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;

public enum BountyTier implements JolCraftEnumHelper.IntId {

    UNKNOWN(0),

    NOVICE(DwarfMerchantData.Level.NOVICE.getId()),
    APPRENTICE(DwarfMerchantData.Level.APPRENTICE.getId()),
    JOURNEYMAN(DwarfMerchantData.Level.JOURNEYMAN.getId()),
    EXPERT(DwarfMerchantData.Level.EXPERT.getId()),
    MASTER(DwarfMerchantData.Level.MASTER.getId());

    private final int value;
    private final String langKey;

    BountyTier(int value) {
        this.value = value;
        this.langKey = (value == 0)
                ? AbstractLanguageKeys.mod(JolCraftDictionary.UNKNOWN)
                : DwarfMerchantData.Level.langKeyFromId(value);
    }

    @Override
    public int getId() {
        return value;
    }

    public String getLangKey() {
        return langKey;
    }

    public Component getDisplayName() {
        return Component.translatable(langKey);
    }

    public static BountyTier fromValue(int value) {
        return JolCraftEnumHelper.byIntIdExact(BountyTier.class, value, UNKNOWN);
    }
}