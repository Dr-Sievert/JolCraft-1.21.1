package net.sievert.jolcraft.data.id.item;

import net.minecraft.world.effect.MobEffects;
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

    public static final String STRONG_LUCK = strongPotion(MobEffects.LUCK.unwrapKey().orElseThrow().location().getPath());

    public static final String BULWARK = JolCraftEffectIds.BULWARK;
    public static final String LONG_BULWARK = longPotion(BULWARK);
    public static final String STRONG_BULWARK = strongPotion(BULWARK);

    public static final String ALCHEMIST_FOCUS = JolCraftEffectIds.ALCHEMIST_FOCUS;
    public static final String LONG_ALCHEMIST_FOCUS = longPotion(ALCHEMIST_FOCUS);
    public static final String STRONG_ALCHEMIST_FOCUS = strongPotion(ALCHEMIST_FOCUS);

    public static final String ANCHOR = JolCraftEffectIds.ANCHOR;
    public static final String LONG_ANCHOR = longPotion(ANCHOR);

    public static final String DEXTERITY = JolCraftEffectIds.DEXTERITY;
    public static final String LONG_DEXTERITY = longPotion(DEXTERITY);
    public static final String STRONG_DEXTERITY = strongPotion(DEXTERITY);

    public static final String DWARVEN_RAGE = JolCraftEffectIds.DWARVEN_RAGE;
    public static final String LONG_DWARVEN_RAGE = longPotion(DWARVEN_RAGE);
    public static final String STRONG_DWARVEN_RAGE = strongPotion(DWARVEN_RAGE);

    public static final String ENDURANCE = JolCraftEffectIds.ENDURANCE;
    public static final String LONG_ENDURANCE = longPotion(ENDURANCE);
    public static final String STRONG_ENDURANCE = strongPotion(ENDURANCE);

    public static final String MAGIC_RESISTANCE = JolCraftEffectIds.MAGIC_RESISTANCE;
    public static final String LONG_MAGIC_RESISTANCE = longPotion(MAGIC_RESISTANCE);
    public static final String STRONG_MAGIC_RESISTANCE = strongPotion(MAGIC_RESISTANCE);

    public static final String POISON_RESISTANCE = JolCraftEffectIds.POISON_RESISTANCE;
    public static final String LONG_POISON_RESISTANCE = longPotion(POISON_RESISTANCE);
    public static final String STRONG_POISON_RESISTANCE = strongPotion(POISON_RESISTANCE);

    public static final String MARKSMAN = JolCraftEffectIds.MARKSMAN;
    public static final String LONG_MARKSMAN = longPotion(MARKSMAN);
    public static final String STRONG_MARKSMAN = strongPotion(MARKSMAN);

    public static final String STONE_SKIN = JolCraftEffectIds.STONE_SKIN;
    public static final String LONG_STONE_SKIN = longPotion(STONE_SKIN);
    public static final String STRONG_STONE_SKIN = strongPotion(STONE_SKIN);

    // Harmful
    public static final String ATAXIA_CURSE = JolCraftEffectIds.ATAXIA_CURSE;
    public static final String CURSED_WOUND = JolCraftEffectIds.CURSED_WOUND;
    public static final String DELIRIUM_CURSE = JolCraftEffectIds.DELIRIUM_CURSE;
    public static final String FAMINE_CURSE = JolCraftEffectIds.FAMINE_CURSE;
    public static final String FRAILTY_CURSE = JolCraftEffectIds.FRAILTY_CURSE;
    public static final String HEX = JolCraftEffectIds.HEX;
    public static final String VITALITY_CURSE = JolCraftEffectIds.VITALITY_CURSE;

    public static final String DISARMED = JolCraftEffectIds.DISARMED;
    public static final String ROOTED = JolCraftEffectIds.ROOTED;
    public static final String STUNNED = JolCraftEffectIds.STUNNED;
    public static final String SUPPRESSED = JolCraftEffectIds.SUPPRESSED;

    public static final String CORROSION = JolCraftEffectIds.CORROSION;
    public static final String LONG_CORROSION = longPotion(CORROSION);
    public static final String STRONG_CORROSION = strongPotion(CORROSION);
    public static final String UNLUCK = MobEffects.UNLUCK.unwrapKey().orElseThrow().location().getPath();
    public static final String STRONG_UNLUCK = strongPotion(UNLUCK);

    private static String strongPotion(String baseId) {
        return join(JolCraftDictionary.STRONG, baseId);
    }

    private static String longPotion(String baseId) {
        return join(JolCraftDictionary.LONG, baseId);
    }
}
