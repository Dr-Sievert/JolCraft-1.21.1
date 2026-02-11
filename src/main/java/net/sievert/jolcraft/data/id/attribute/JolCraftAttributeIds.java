package net.sievert.jolcraft.data.id.attribute;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;

public final class JolCraftAttributeIds extends JolCraftIds {

    private JolCraftAttributeIds() {}

    public static final String XP_INCREASE = join(JolCraftDictionary.XP, JolCraftDictionary.INCREASE);
    public static final String SLOW_RESISTANCE = join(JolCraftDictionary.SLOW, JolCraftDictionary.RESISTANCE);
    public static final String CROP_LOOT_INCREASE = join(JolCraftDictionary.CROP, JolCraftDictionary.LOOT, JolCraftDictionary.INCREASE);
    public static final String CHEST_LOOT_INCREASE = join(JolCraftDictionary.CHEST, JolCraftDictionary.LOOT, JolCraftDictionary.INCREASE);
    public static final String RADIANT = JolCraftEntityObjectIds.RADIANT;
    public static final String ARMOR_UNBREAKING = join(JolCraftDictionary.ARMOR, JolCraftDictionary.UNBREAKING);
    public static final String MAGIC_RESISTANCE = join(JolCraftDictionary.MAGIC, JolCraftDictionary.RESISTANCE);
    public static final String ARMOR_INCREASE = join(JolCraftDictionary.ARMOR, JolCraftDictionary.INCREASE);
    public static final String ATTACK_DAMAGE_INCREASE = join(JolCraftDictionary.ATTACK, JolCraftDictionary.DAMAGE, JolCraftDictionary.INCREASE);
    public static final String MOVEMENT_SPEED_DAY_INCREASE = join(JolCraftDictionary.MOVEMENT, JolCraftDictionary.SPEED, JolCraftDictionary.DAY, JolCraftDictionary.INCREASE);
    public static final String MOVEMENT_SPEED_NIGHT_INCREASE = join(JolCraftDictionary.MOVEMENT, JolCraftDictionary.SPEED, JolCraftDictionary.NIGHT, JolCraftDictionary.INCREASE);
}
