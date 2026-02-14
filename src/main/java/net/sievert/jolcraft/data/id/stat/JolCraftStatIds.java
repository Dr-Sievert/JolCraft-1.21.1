package net.sievert.jolcraft.data.id.stat;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftStatIds extends JolCraftIds {

    private JolCraftStatIds() {}

    public static final String DISCOVERED_STRUCTURES = join(JolCraftDictionary.DISCOVERED, JolCraftStrings.plural(JolCraftDictionary.STRUCTURE));

    public static final String TALK_TO_DWARF = join(JolCraftDictionary.TALK, JolCraftDictionary.TO, JolCraftDwarfIds.DWARF);

    public static final String TRADE_WITH_DWARF = join(JolCraftDictionary.TRADE, JolCraftDictionary.WITH, JolCraftDwarfIds.DWARF);
}