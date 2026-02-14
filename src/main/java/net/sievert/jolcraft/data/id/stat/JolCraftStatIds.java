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

    public static final String COINS_SPENT = join(plural(JolCraftDictionary.COIN), JolCraftDictionary.SPENT);

    public static final String DWARVEN_TOMES_IDENTIFIED = join(JolCraftDictionary.DWARVEN, plural(JolCraftDictionary.TOME), JolCraftDictionary.IDENTIFIED);

    public static final String DWARVEN_BOUNTIES_COMPLETED = join(JolCraftDictionary.DWARVEN, plural(JolCraftDictionary.BOUNTIES), JolCraftDictionary.COMPLETED);

    public static final String DWARVEN_BREWS_CREATED = join(JolCraftDictionary.DWARVEN, plural(JolCraftDictionary.BREW), JolCraftDictionary.CREATED);

    public static final String GEODES_CRACKED = join(plural(JolCraftDictionary.GEODE), JolCraftDictionary.CRACKED);

    public static final String GEMS_CRUSHED = join(plural(JolCraftDictionary.GEM), JolCraftDictionary.CRUSHED);

    public static final String GEMS_CUT = join(plural(JolCraftDictionary.GEM), JolCraftDictionary.CUT);
}