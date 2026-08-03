package net.sievert.jolcraft.data.id.attribute;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftAttributeIds extends JolCraftIds {

    private JolCraftAttributeIds() {}

    public static final String EXPERIENCE_INCREASE = join(JolCraftDictionary.EXPERIENCE, JolCraftDictionary.INCREASE);
    public static final String SLOW_RESISTANCE = join(JolCraftDictionary.SLOW, JolCraftDictionary.RESISTANCE);
    public static final String CROP_LOOT_INCREASE = join(JolCraftDictionary.CROP, JolCraftDictionary.LOOT, JolCraftDictionary.INCREASE);
    public static final String CONTAINER_LOOT_INCREASE = join(JolCraftDictionary.CONTAINER, JolCraftDictionary.LOOT, JolCraftDictionary.INCREASE);
    public static final String RADIANT = JolCraftEntityObjectIds.RADIANT;
    public static final String ARMOR_PENETRATION = join(JolCraftDictionary.ARMOR, JolCraftDictionary.PENETRATION);
    public static final String MAGIC_RESISTANCE = join(JolCraftDictionary.MAGIC, JolCraftDictionary.RESISTANCE);
    public static final String ARMOR_TOTAL = join(JolCraftDictionary.ARMOR, JolCraftDictionary.TOTAL);
    public static final String ATTACK_DAMAGE_INCREASE = join(JolCraftDictionary.ATTACK, JolCraftDictionary.DAMAGE, JolCraftDictionary.INCREASE);
    public static final String ITEM_USE_SPEED = join(JolCraftDictionary.ITEM, JolCraftDictionary.USE, JolCraftDictionary.SPEED);
    public static final String MOON_SHIELD = join(JolCraftDictionary.MOON, JolCraftDictionary.SHIELD);
}
