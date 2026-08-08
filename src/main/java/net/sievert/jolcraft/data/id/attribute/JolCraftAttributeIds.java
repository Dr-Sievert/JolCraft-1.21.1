package net.sievert.jolcraft.data.id.attribute;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftAttributeIds extends JolCraftIds {

    private JolCraftAttributeIds() {}

    // Positive

    public static final String EXPERIENCE_INCREASE = join(JolCraftDictionary.EXPERIENCE, JolCraftDictionary.INCREASE);
    public static final String SLOW_RESISTANCE = join(JolCraftDictionary.SLOW, JolCraftDictionary.RESISTANCE);
    public static final String CROP_LOOT_INCREASE = join(JolCraftDictionary.CROP, JolCraftDictionary.LOOT, JolCraftDictionary.INCREASE);
    public static final String CONTAINER_LOOT_INCREASE = join(JolCraftDictionary.CONTAINER, JolCraftDictionary.LOOT, JolCraftDictionary.INCREASE);
    public static final String LUMINANCE = JolCraftDictionary.LUMINANCE;
    public static final String ARMOR_PENETRATION = join(JolCraftDictionary.ARMOR, JolCraftDictionary.PENETRATION);
    public static final String MAGIC_RESISTANCE = join(JolCraftDictionary.MAGIC, JolCraftDictionary.RESISTANCE);
    public static final String POISON_RESISTANCE = join(JolCraftDictionary.POISON, JolCraftDictionary.RESISTANCE);
    public static final String ITEM_USE_SPEED = join(JolCraftDictionary.ITEM, JolCraftDictionary.USE, JolCraftDictionary.SPEED);
    public static final String MOON_SHIELD = join(JolCraftDictionary.MOON, JolCraftDictionary.SHIELD);
    public static final String PROJECTILE_DAMAGE = join(JolCraftDictionary.PROJECTILE, JolCraftDictionary.DAMAGE);
    public static final String TENACITY = JolCraftDictionary.TENACITY;
    public static final String FOCUS = JolCraftDictionary.FOCUS;
    public static final String LOCKPICKING = JolCraftDictionary.LOCKPICKING;
    public static final String SUN_FIRE_DAMAGE = join(JolCraftDictionary.SUN, JolCraftDictionary.FIRE, JolCraftDictionary.DAMAGE);

    // Negative

    public static final String CURSE_VULNERABILITY = join(JolCraftDictionary.CURSE, JolCraftDictionary.VULNERABILITY);
}
