package net.sievert.jolcraft.data.id.effect;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
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
    public static final String MOON_SHIELD = JolCraftAttributeIds.MOON_SHIELD;
    public static final String BULWARK = JolCraftDictionary.BULWARK;

    // Harmful
    public static final String ATAXIA_CURSE = join(JolCraftDictionary.ATAXIA, JolCraftDictionary.CURSE);
    public static final String CURSED_WOUND = join(JolCraftDictionary.CURSED, JolCraftDictionary.WOUND);
    public static final String DELIRIUM_CURSE = join(JolCraftDictionary.DELIRIUM, JolCraftDictionary.CURSE);
    public static final String FAMINE_CURSE = join(JolCraftDictionary.FAMINE, JolCraftDictionary.CURSE);
    public static final String FRAILTY_CURSE = join(JolCraftDictionary.FRAILTY, JolCraftDictionary.CURSE);
    public static final String HEX = JolCraftDictionary.HEX;
    public static final String VITALITY_CURSE = join(JolCraftDictionary.VITALITY, JolCraftDictionary.CURSE);

    public static final String DISARMED = JolCraftDictionary.DISARMED;
    public static final String ROOTED = JolCraftDictionary.ROOTED;
    public static final String STUNNED = JolCraftDictionary.STUNNED;
    public static final String SUPPRESSED = JolCraftDictionary.SUPPRESSED;

    public static final String CORROSION = JolCraftDictionary.CORROSION;
}
