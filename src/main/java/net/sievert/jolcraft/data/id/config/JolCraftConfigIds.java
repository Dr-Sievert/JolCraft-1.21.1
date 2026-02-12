package net.sievert.jolcraft.data.id.config;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftConfigIds extends JolCraftIds {

    private JolCraftConfigIds() {}

    public static final String DWARF_PROFESSIONS = join(JolCraftDwarfIds.DWARF, plural(JolCraftDictionary.PROFESSION));
}
