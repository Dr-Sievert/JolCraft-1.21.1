package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class DwarvenReputationLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Reputation messages
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_LOCKED, "You need a higher reputation for this.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_MAX_TIER, "You are already at the highest reputation tier!");
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_NOT_ENOUGH_ENDORSEMENTS,
                "You need %1$d endorsements to advance (you have %2$d)."
        );
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_NEVER_ENDORSE, "This dwarf does not give endorsements.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_CANNOT_ENDORSE, "This dwarf is not ready to give you an endorsement.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_ALREADY_ENDORSED, "You already have this endorsement.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_WRONG_TABLET, "You must present the correct reputation tablet.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_LEVEL_UP, "You have advanced in dwarven reputation!");

        // -----------------------------------------------------------------
        // Tablet UI / info
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_TABLET_OWNER, "Granted to: %s");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_REPUTATION, "Reputation: ");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS, "Endorsements: %s");

        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_REPUTATION_PROGRESS,
                "Endorsements for reputation advancement: %s/%s"
        );
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_TABLET_PROGRESS_PREFIX,
                "Endorsements for reputation advancement: "
        );
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS_INFO,
                "To gain endorsements, give your reputation tablet to a masterTask-level dwarf with a profession. " +
                        "Endorsements are unique per profession and can only be gained once."
        );
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_TABLET_ADVANCE_INFO,
                "To advance in reputation level you need endorsements from dwarves with professions. " +
                        "When you have enough, hand over your tablet to a guildmaster to update it."
        );

        // -----------------------------------------------------------------
        // Reputation tiers (jolcraft.reputation_tier.<n>)
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_0, "Stranger");
        p.putManual(JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_1, "Known Face");
        p.putManual(JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_2, "Trusted");
        p.putManual(JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_3, "Respected");
        p.putManual(JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_4, "Blood-Kin");
    }
}