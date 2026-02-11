package net.sievert.jolcraft.data.id.item;

import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;

public final class JolCraftPotionIds {

    private JolCraftPotionIds() {}

    // Beneficial
    public static final String ANCIENT_MEMORY = JolCraftEffectIds.ANCIENT_MEMORY;
    public static final String LONG_ANCIENT_MEMORY = longer(ANCIENT_MEMORY);

    public static final String LOCKPICKING = JolCraftEffectIds.LOCKPICKING;
    public static final String LONG_LOCKPICKING = longer(LOCKPICKING);
    public static final String STRONG_LOCKPICKING = strong(LOCKPICKING);

    public static final String DWARVEN_HASTE = JolCraftEffectIds.DWARVEN_HASTE;
    public static final String LONG_DWARVEN_HASTE = longer(DWARVEN_HASTE);
    public static final String STRONG_DWARVEN_HASTE = strong(DWARVEN_HASTE);

    // Harmful
    public static final String CURSED_WOUND = JolCraftEffectIds.CURSED_WOUND;
    public static final String DELIRIUM_CURSE = JolCraftEffectIds.DELIRIUM_CURSE;

    public static final String CORROSION = JolCraftEffectIds.CORROSION;
    public static final String LONG_CORROSION = longer(CORROSION);
    public static final String STRONG_CORROSION = strong(CORROSION);

    private static String strong(String baseId) {
        return "strong_" + baseId;
    }

    private static String longer(String baseId) {
        return "long_" + baseId;
    }
}
