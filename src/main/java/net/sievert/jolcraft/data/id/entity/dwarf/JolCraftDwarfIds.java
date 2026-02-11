package net.sievert.jolcraft.data.id.entity.dwarf;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;

public final class JolCraftDwarfIds extends JolCraftIds {

    private JolCraftDwarfIds(){}

    public static final String DWARF   = JolCraftDataKeys.DWARF;
    public static final String DWARF_ALCHEMIST   = dwarfProfession(JolCraftDataKeys.ALCHEMIST);
    public static final String DWARF_ARCANIST    = dwarfProfession(JolCraftDataKeys.ARCANIST);
    public static final String DWARF_ARTISAN     = dwarfProfession(JolCraftDataKeys.ARTISAN);
    public static final String DWARF_BREWMASTER  = dwarfProfession(JolCraftDataKeys.BREWMASTER);
    public static final String DWARF_EXPLORER    = dwarfProfession(JolCraftDataKeys.EXPLORER);
    public static final String DWARF_GUARD       = dwarfProfession(JolCraftDataKeys.GUARD);
    public static final String DWARF_GUILDMASTER = dwarfProfession(JolCraftDataKeys.GUILDMASTER);
    public static final String DWARF_HISTORIAN   = dwarfProfession(JolCraftDataKeys.HISTORIAN);
    public static final String DWARF_KEEPER      = dwarfProfession(JolCraftDataKeys.KEEPER);
    public static final String DWARF_MERCHANT    = dwarfProfession(JolCraftDataKeys.MERCHANT);
    public static final String DWARF_MINER       = dwarfProfession(JolCraftDataKeys.MINER);
    public static final String DWARF_PRIEST      = dwarfProfession(JolCraftDataKeys.PRIEST);
    public static final String DWARF_SCRAPPER    = dwarfProfession(JolCraftDataKeys.SCRAPPER);

    private static String dwarfProfession(String profession) {
        return joined(JolCraftDataKeys.DWARF, profession);
    }
}
