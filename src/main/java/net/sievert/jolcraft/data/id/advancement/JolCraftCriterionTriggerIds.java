package net.sievert.jolcraft.data.id.advancement;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftCriterionTriggerIds extends JolCraftIds {

    private JolCraftCriterionTriggerIds() {}

    public static final String ADVANCEMENT = JolCraftDictionary.ADVANCEMENT;
    public static final String TRADE_WITH_DWARF = join(JolCraftDictionary.TRADE, JolCraftDictionary.WITH, JolCraftDwarfIds.DWARF);
    public static final String LANGUAGE = JolCraftAttachmentIds.LANGUAGE;
    public static final String DWARVEN_ENDORSEMENT = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.ENDORSEMENT);
    public static final String DWARVEN_REPUTATION = JolCraftAttachmentIds.DWARVEN_REPUTATION;
}
