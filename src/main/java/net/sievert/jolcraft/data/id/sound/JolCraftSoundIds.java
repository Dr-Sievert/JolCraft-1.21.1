package net.sievert.jolcraft.data.id.sound;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.item.JolCraftMaterialIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftSoundIds extends JolCraftIds {

    private JolCraftSoundIds() {}

    // Armor / equipment
    public static final String ARMOR_EQUIP_DEEPSLATE = JolCraftStrings.underscored(JolCraftDictionary.ARMOR, JolCraftDictionary.EQUIP, JolCraftMaterialIds.DEEPSLATE);

    // Random
    public static final String LEVEL_UP = JolCraftStrings.underscored(JolCraftDictionary.LEVEL, JolCraftDictionary.UP);

    // Blocks
    public static final String STRONGBOX_OPEN = JolCraftStrings.underscored(JolCraftBlockIds.STRONGBOX, JolCraftDictionary.OPEN);
    public static final String STRONGBOX_CLOSE = JolCraftStrings.underscored(JolCraftBlockIds.STRONGBOX, JolCraftDictionary.CLOSE);
    public static final String STRONGBOX_LOCKPICK = JolCraftStrings.underscored(JolCraftBlockIds.STRONGBOX, JolCraftDictionary.LOCKPICK);
    public static final String STRONGBOX_LOCKPICK_BREAK = JolCraftStrings.underscored(JolCraftBlockIds.STRONGBOX, JolCraftItemIds.LOCKPICK, JolCraftDictionary.BREAK);
    public static final String STRONGBOX_UNLOCK = JolCraftStrings.underscored(JolCraftBlockIds.STRONGBOX, JolCraftDictionary.UNLOCK);
    public static final String GEM_CUT = JolCraftStrings.underscored(JolCraftDictionary.GEM, JolCraftDictionary.CUT);

    // Items
    public static final String COIN_STACK = JolCraftStrings.underscored(JolCraftDictionary.COIN, JolCraftDictionary.STACK);
    public static final String COIN_SINGLE = JolCraftStrings.underscored(JolCraftDictionary.COIN, JolCraftDictionary.SINGLE);

    // Entity
    public static final String DWARF_AMBIENT = JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.AMBIENT);
    public static final String DWARF_HURT = JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.HURT);
    public static final String DWARF_DEATH = JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.DEATH);
    public static final String DWARF_YES = JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.YES);
    public static final String DWARF_NO = JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.NO);
    public static final String DWARF_TRADE = JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.TRADE);

    // Curse
    public static final String CURSE = JolCraftDictionary.CURSE;
}