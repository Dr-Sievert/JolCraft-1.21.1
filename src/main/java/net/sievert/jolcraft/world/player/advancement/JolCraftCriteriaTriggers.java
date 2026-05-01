package net.sievert.jolcraft.world.player.advancement;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.sievert.jolcraft.world.player.advancement.custom.*;

public class JolCraftCriteriaTriggers {

    public static final AdvancementTrigger HAS_ADVANCEMENT = new AdvancementTrigger();
    public static final LanguageTrigger KNOWS_LANGUAGE = new LanguageTrigger();
    public static final DwarfTradeTrigger TRADE_WITH_DWARF = new DwarfTradeTrigger();
    public static final DwarfEndorsementTrigger ENDORSEMENT_GAIN = new DwarfEndorsementTrigger();
    public static final ReputationTrigger REPUTATION_GAIN = new ReputationTrigger();

    public static void register(RegisterEvent event) {
        event.register(Registries.TRIGGER_TYPE, helper -> {
            helper.register(AdvancementTrigger.ID, HAS_ADVANCEMENT);
            helper.register(LanguageTrigger.ID, KNOWS_LANGUAGE);
            helper.register(DwarfTradeTrigger.ID, TRADE_WITH_DWARF);
            helper.register(DwarfEndorsementTrigger.ID, ENDORSEMENT_GAIN);
            helper.register(ReputationTrigger.ID, REPUTATION_GAIN);
        });
    }
}