package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class ReputationLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    // -----------------------------------------------------------------
    // Reputation tiers (jolcraft.reputation_tier.<n>)
    // -----------------------------------------------------------------

    public static final String REPUTATION_TIER_0 = JolCraftLanguageKeys.mod("reputation_tier.0");

    public static final String REPUTATION_TIER_1 = JolCraftLanguageKeys.mod("reputation_tier.1");

    public static final String REPUTATION_TIER_2 = JolCraftLanguageKeys.mod("reputation_tier.2");

    public static final String REPUTATION_TIER_3 = JolCraftLanguageKeys.mod("reputation_tier.3");

    public static final String REPUTATION_TIER_4 = JolCraftLanguageKeys.mod("reputation_tier.4");


    // ---------------------------------------------------------------------
    // Reputation category (tooltip.<modid>.reputation.<path>)
    // ---------------------------------------------------------------------

    public static final String REPUTATION = "reputation";

    public static final String TOOLTIP_REPUTATION_LOCKED = JolCraftLanguageKeys.tooltip(REPUTATION, "locked");

    public static final String TOOLTIP_REPUTATION_MAX_TIER = JolCraftLanguageKeys.tooltip(REPUTATION, "max_tier");

    public static final String TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS = JolCraftLanguageKeys.tooltip(REPUTATION, "not_enough_endorsements");

    public static final String TOOLTIP_REPUTATION_NEVER_ENDORSE = JolCraftLanguageKeys.tooltip(REPUTATION, "never_endorse");

    public static final String TOOLTIP_REPUTATION_CANNOT_ENDORSE = JolCraftLanguageKeys.tooltip(REPUTATION, "cannot_endorse");

    public static final String TOOLTIP_REPUTATION_ALREADY_ENDORSED = JolCraftLanguageKeys.tooltip(REPUTATION, "already_endorsed");

    public static final String TOOLTIP_REPUTATION_WRONG_TABLET = JolCraftLanguageKeys.tooltip(REPUTATION, "wrong_tablet");

    public static final String TOOLTIP_REPUTATION_LEVEL_UP = JolCraftLanguageKeys.tooltip(REPUTATION, "level_up");

    // ---------------------------------------------------------------------
    // Tablet category (tooltip.<modid>.tablet.<path>)
    // ---------------------------------------------------------------------

    public static final String TABLET = "tablet";

    public static final String TOOLTIP_TABLET_OWNER = JolCraftLanguageKeys.tooltip(TABLET, "owner");

    public static final String TOOLTIP_TABLET_REPUTATION = JolCraftLanguageKeys.tooltip(TABLET, "tier");

    public static final String TOOLTIP_TABLET_ENDORSEMENTS = JolCraftLanguageKeys.tooltip(TABLET, "endorsements");

    public static final String TOOLTIP_TABLET_PROGRESS = JolCraftLanguageKeys.tooltip(TABLET, "progress");

    public static final String TOOLTIP_TABLET_PROGRESS_PREFIX = JolCraftLanguageKeys.tooltip(TABLET, "progress.prefix");

    public static final String TOOLTIP_TABLET_ENDORSEMENTS_INFO = JolCraftLanguageKeys.tooltip(TABLET, "endorsements_info");

    public static final String TOOLTIP_TABLET_ADVANCE_INFO = JolCraftLanguageKeys.tooltip(TABLET, "advance_info");

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Reputation messages
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_REPUTATION_LOCKED, "You need a higher reputation for this.");
        p.putManual(TOOLTIP_REPUTATION_MAX_TIER, "You are already at the highest reputation tier!");
        p.putManual(TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS, "You need %1$d endorsements to advance (you have %2$d).");
        p.putManual(TOOLTIP_REPUTATION_NEVER_ENDORSE, "This dwarf does not give endorsements.");
        p.putManual(TOOLTIP_REPUTATION_CANNOT_ENDORSE, "This dwarf is not ready to give you an endorsement.");
        p.putManual(TOOLTIP_REPUTATION_ALREADY_ENDORSED, "You already have this endorsement.");
        p.putManual(TOOLTIP_REPUTATION_WRONG_TABLET, "You must present the correct reputation tablet.");
        p.putManual(TOOLTIP_REPUTATION_LEVEL_UP, "You have advanced in dwarven reputation!");

        // -----------------------------------------------------------------
        // Tablet UI / info
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_TABLET_OWNER, "Granted to: %s");
        p.putManual(TOOLTIP_TABLET_REPUTATION, "Reputation: ");
        p.putManual(TOOLTIP_TABLET_ENDORSEMENTS, "Endorsements: %s");

        p.putManual(
                TOOLTIP_TABLET_PROGRESS,
                "Endorsements for reputation advancement: %s/%s"
        );
        p.putManual(
                TOOLTIP_TABLET_PROGRESS_PREFIX,
                "Endorsements for reputation advancement: "
        );
        p.putManual(
                TOOLTIP_TABLET_ENDORSEMENTS_INFO,
                "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. " +
                        "Endorsements are unique per profession and can only be gained once."
        );
        p.putManual(
                TOOLTIP_TABLET_ADVANCE_INFO,
                "To advance in reputation level you need endorsements from dwarves with professions. " +
                        "When you have enough, hand over your tablet to a guildmaster to update it."
        );

        // -----------------------------------------------------------------
        // Reputation tiers (jolcraft.reputation_tier.<n>)
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.mod(REPUTATION_TIER_0), "Stranger");
        p.putManual(JolCraftLanguageKeys.mod(REPUTATION_TIER_1), "Known Face");
        p.putManual(JolCraftLanguageKeys.mod(REPUTATION_TIER_2), "Trusted");
        p.putManual(JolCraftLanguageKeys.mod(REPUTATION_TIER_3), "Respected");
        p.putManual(JolCraftLanguageKeys.mod(REPUTATION_TIER_4), "Blood-Kin");

    }
}