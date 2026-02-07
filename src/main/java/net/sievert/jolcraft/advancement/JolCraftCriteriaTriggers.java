package net.sievert.jolcraft.advancement;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.sievert.jolcraft.advancement.custom.*;

public class JolCraftCriteriaTriggers {

    public static final AdvancementTrigger HAS_ADVANCEMENT = new AdvancementTrigger();
    public static final DwarvenLanguageTrigger KNOWS_DWARVEN_LANGUAGE = new DwarvenLanguageTrigger();
    public static final DwarfTradeTrigger TRADE_WITH_DWARF = new DwarfTradeTrigger();
    public static final EndorsementTrigger ENDORSEMENT_GAIN = new EndorsementTrigger();
    public static final ReputationTrigger REPUTATION_GAIN = new ReputationTrigger();

    public static void register(RegisterEvent event) {
        event.register(Registries.TRIGGER_TYPE, helper -> {
            helper.register(AdvancementTrigger.ID, HAS_ADVANCEMENT);
            helper.register(DwarvenLanguageTrigger.ID, KNOWS_DWARVEN_LANGUAGE);
            helper.register(DwarfTradeTrigger.ID, TRADE_WITH_DWARF);
            helper.register(EndorsementTrigger.ID, ENDORSEMENT_GAIN);
            helper.register(ReputationTrigger.ID, REPUTATION_GAIN);
        });
    }
}