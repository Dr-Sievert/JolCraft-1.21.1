package net.sievert.jolcraft.data.id.attachment;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftAttachmentIds extends JolCraftIds {

    private JolCraftAttachmentIds() {}

    public static final String LANGUAGE = join(JolCraftDictionary.LANGUAGE);
    public static final String DWARVEN_REPUTATION = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.REPUTATION);
    public static final String DWARF_LORE = join(JolCraftDwarfIds.DWARF, JolCraftDictionary.LORE);
    public static final String HEARTH = JolCraftBlockIds.HEARTH;
    public static final String DISCOVERED_STRUCTURES = join(JolCraftDictionary.DISCOVERED, plural(JolCraftDictionary.STRUCTURE));
}
