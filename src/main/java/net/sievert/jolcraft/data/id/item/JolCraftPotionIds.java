package net.sievert.jolcraft.data.id.item;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftPotionIds extends JolCraftIds {

    private JolCraftPotionIds() {}

    // Beneficial
    public static final String ANCIENT_MEMORY = JolCraftEffectIds.ANCIENT_MEMORY;
    public static final String LONG_ANCIENT_MEMORY = longPotion(ANCIENT_MEMORY);

    public static final String LOCKPICKING = JolCraftEffectIds.LOCKPICKING;
    public static final String LONG_LOCKPICKING = longPotion(LOCKPICKING);
    public static final String STRONG_LOCKPICKING = strongPotion(LOCKPICKING);

    public static final String DWARVEN_HASTE = JolCraftEffectIds.DWARVEN_HASTE;
    public static final String LONG_DWARVEN_HASTE = longPotion(DWARVEN_HASTE);
    public static final String STRONG_DWARVEN_HASTE = strongPotion(DWARVEN_HASTE);

    // Harmful
    public static final String CURSED_WOUND = JolCraftEffectIds.CURSED_WOUND;
    public static final String DELIRIUM_CURSE = JolCraftEffectIds.DELIRIUM_CURSE;

    public static final String CORROSION = JolCraftEffectIds.CORROSION;
    public static final String LONG_CORROSION = longPotion(CORROSION);
    public static final String STRONG_CORROSION = strongPotion(CORROSION);

    private static String strongPotion(String baseId) {
        return join(JolCraftDictionary.STRONG, baseId);
    }

    private static String longPotion(String baseId) {
        return join(JolCraftDictionary.LONG, baseId);
    }
}
