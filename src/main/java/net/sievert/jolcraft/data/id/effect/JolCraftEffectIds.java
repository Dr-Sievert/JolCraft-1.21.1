package net.sievert.jolcraft.data.id.effect;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftEffectIds extends JolCraftIds {

    private JolCraftEffectIds() {}

    // Beneficial
    public static final String HOMESTEAD = JolCraftDictionary.HOMESTEAD;
    public static final String ANCIENT_MEMORY = join(JolCraftDictionary.ANCIENT, JolCraftDictionary.MEMORY);
    public static final String LOCKPICKING = JolCraftDictionary.LOCKPICKING;
    public static final String DWARVEN_HASTE = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.HASTE);
    public static final String RADIANT = JolCraftEntityObjectIds.RADIANT;

    // Harmful
    public static final String CURSED_WOUND = join(JolCraftDictionary.CURSED, JolCraftDictionary.WOUND);
    public static final String DELIRIUM_CURSE = join(JolCraftDictionary.DELIRIUM, JolCraftDictionary.CURSE);
    public static final String CORROSION = JolCraftDictionary.CORROSION;
}
