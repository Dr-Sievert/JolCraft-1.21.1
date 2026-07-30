package net.sievert.jolcraft.data.id.entity.dwarf;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftDwarfIds extends JolCraftIds {

    private JolCraftDwarfIds(){}

    public static final String DWARF   = JolCraftDictionary.DWARF;
    public static final String DWARF_ALCHEMIST   = dwarfProfession(JolCraftDictionary.ALCHEMIST);
    public static final String DWARF_ARCANIST    = dwarfProfession(JolCraftDictionary.ARCANIST);
    public static final String DWARF_ARTISAN     = dwarfProfession(JolCraftDictionary.ARTISAN);
    public static final String DWARF_BREWMASTER  = dwarfProfession(JolCraftDictionary.BREWMASTER);
    public static final String DWARF_EXPLORER    = dwarfProfession(JolCraftDictionary.EXPLORER);
    public static final String DWARF_GUARD       = dwarfProfession(JolCraftDictionary.GUARD);
    public static final String DWARF_GUILDMASTER = dwarfProfession(JolCraftDictionary.GUILDMASTER);
    public static final String DWARF_HISTORIAN   = dwarfProfession(JolCraftDictionary.HISTORIAN);
    public static final String DWARF_KEEPER      = dwarfProfession(JolCraftDictionary.KEEPER);
    public static final String DWARF_MERCHANT    = dwarfProfession(JolCraftDictionary.MERCHANT);
    public static final String DWARF_MINER       = dwarfProfession(JolCraftDictionary.MINER);
    public static final String DWARF_PRIEST      = dwarfProfession(JolCraftDictionary.PRIEST);
    public static final String DWARF_SCRAPPER    = dwarfProfession(JolCraftDictionary.SCRAPPER);
    public static final String DWARF_BLACKSMITH  = dwarfProfession(JolCraftDictionary.BLACKSMITH);
    public static final String DWARF_CHAMPION    = dwarfProfession(JolCraftDictionary.CHAMPION);
    public static final String DWARF_SMELTER     = dwarfProfession(JolCraftDictionary.SMELTER);

    private static String dwarfProfession(String profession) {
        return join(JolCraftDictionary.DWARF, profession);
    }
}
