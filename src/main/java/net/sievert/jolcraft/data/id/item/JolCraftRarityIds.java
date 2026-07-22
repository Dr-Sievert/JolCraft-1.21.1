package net.sievert.jolcraft.data.id.item;

import net.minecraft.world.item.Rarity;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftRarityIds {

    private JolCraftRarityIds() {}

    public static final String COMMON = name(Rarity.COMMON);
    public static final String UNCOMMON = name(Rarity.UNCOMMON);
    public static final String RARE = name(Rarity.RARE);
    public static final String EPIC = name(Rarity.EPIC);
    public static final String LEGENDARY = JolCraftDictionary.LEGENDARY;

    private static String name(Rarity rarity) {
        return rarity.getSerializedName();
    }
}
