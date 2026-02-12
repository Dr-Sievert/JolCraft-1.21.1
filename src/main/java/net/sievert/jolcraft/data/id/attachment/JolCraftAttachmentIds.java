package net.sievert.jolcraft.data.id.attachment;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftAttachmentIds extends JolCraftIds {

    private JolCraftAttachmentIds() {}

    public static final String ATTRIBUTES = plural(JolCraftDictionary.ATTRIBUTE);
    public static final String DWARVEN_LANGUAGE = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.LANGUAGE);
    public static final String ANCIENT_DWARVEN_LANGUAGE = join(JolCraftDictionary.ANCIENT, DWARVEN_LANGUAGE);
    public static final String DWARVEN_REPUTATION = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.REPUTATION);
    public static final String DWARF_TOME_UNLOCKS = join(JolCraftDwarfIds.DWARF, JolCraftDictionary.TOME, plural(JolCraftDictionary.UNLOCK));
    public static final String HEARTH = JolCraftBlockIds.HEARTH;
    public static final String DISCOVERED_STRUCTURES = join(JolCraftDictionary.DISCOVERED, plural(JolCraftDictionary.STRUCTURE));
}
