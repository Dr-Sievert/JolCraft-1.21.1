package net.sievert.jolcraft.data.id.config;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;

public final class JolCraftConfigIds extends JolCraftIds {

    private JolCraftConfigIds() {}

    public static final String DWARF_PROFESSIONS = join(JolCraftDirectoryIds.DWARF, plural(JolCraftDirectoryIds.PROFESSION));
}
