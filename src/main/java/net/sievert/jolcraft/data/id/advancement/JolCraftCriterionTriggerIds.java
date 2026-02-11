package net.sievert.jolcraft.data.id.advancement;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;

public final class JolCraftCriterionTriggerIds extends JolCraftIds {

    private JolCraftCriterionTriggerIds() {}

    public static final String HAS_ADVANCEMENT = join(JolCraftDictionary.HAS, JolCraftDictionary.ADVANCEMENT);
    public static final String TRADE_WITH_DWARF = join(JolCraftDictionary.TRADE, JolCraftDictionary.WITH, JolCraftDwarfIds.DWARF);
    public static final String HAS_DWARVEN_LANGUAGE = join(JolCraftDictionary.HAS, JolCraftAttachmentIds.DWARVEN_LANGUAGE);
    public static final String ENDORSEMENT = JolCraftDictionary.ENDORSEMENT;
    public static final String DWARVEN_REPUTATION = join(JolCraftAttachmentIds.DWARVEN_REPUTATION);
}
