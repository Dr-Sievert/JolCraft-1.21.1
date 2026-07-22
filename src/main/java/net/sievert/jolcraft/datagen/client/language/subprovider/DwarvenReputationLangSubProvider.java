package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class DwarvenReputationLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDictionary.DWARVEN, JolCraftDictionary.REPUTATION);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // -----------------------------------------------------------------
        // Reputation messages
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_LOCKED, "You need a higher reputation for this.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_MAX_TIER, "You are already at the highest reputation tier!");
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_NOT_ENOUGH_ENDORSEMENTS,
                "You need %1$d endorsements to advance (you have %2$d)."
        );
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_NEVER_ENDORSE, "This dwarf does not give endorsements.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_CANNOT_ENDORSE, "This dwarf is not ready to give you an endorsement.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_ALREADY_ENDORSED, "You already have this endorsement.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_WRONG_TABLET, "You must present the correct reputation tablet.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_LEVEL_UP, "You have advanced in dwarven reputation!");

        // -----------------------------------------------------------------
        // Tablet UI / info
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_TABLET_OWNER, "Granted to: %s");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_REPUTATION, "Reputation: ");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS, "Endorsements: %s");

        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_REPUTATION_PROGRESS,
                "Endorsements for reputation advancement: %s/%s"
        );
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_TABLET_PROGRESS_PREFIX,
                "Endorsements for reputation advancement: "
        );
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS_INFO,
                "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. " +
                        "Endorsements are unique per profession and can only be gained once."
        );
        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_TABLET_ADVANCE_INFO,
                "To advance to the next reputation level, you need endorsements from dwarves with professions. " +
                        "When you have enough, hand over your tablet to a guildmaster to update it."
        );

        // -----------------------------------------------------------------
        // Reputation tiers (jolcraft.reputation_tier.<n>)
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_STRANGER, JolCraftStrings.toTitleCase(JolCraftDictionary.STRANGER));

        putManual(translations, JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_KNOWN_FACE, JolCraftStrings.toTitleCase(JolCraftStrings.spaced(
                JolCraftDictionary.KNOWN,
                JolCraftDictionary.FACE)
        ));

        putManual(translations, JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_TRUSTED, JolCraftStrings.toTitleCase(JolCraftDictionary.TRUSTED));

        putManual(translations, JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_RESPECTED, JolCraftStrings.toTitleCase(JolCraftDictionary.RESPECTED));

        putManual(translations, JolCraftLanguageKeys.DWARVEN_REPUTATION_TIER_BLOOD_KIN, JolCraftStrings.toTitleCase(JolCraftStrings.spaced(
                JolCraftDictionary.BLOOD,
                JolCraftDictionary.KIN)
        ));
    }
}