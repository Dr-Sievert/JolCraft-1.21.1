package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class BountyLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.BOUNTIES;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_TIER, "Tier: %s");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_TYPE, "Type: %s");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID, "No bounty data");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE, "This is the wrong type of bounty to give to this dwarf.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_DWARF_PROFESSION, "Give this to a %s to get a bounty task.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_NOT_COMPLETE, "This bounty has not been completed.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_COMPLETE, "This bounty is ready to be turned in.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_COMPLETED, "Your current %s bounty has been completed!");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY, "Slay: %s");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY_ALT, "Completed by slaying the specified amount of mobs and handed in for a reward.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COLLECT, "Collect: %s");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COLLECT_ALT, "Filled with specified items and handed in for a reward.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_LOCKED, "The crate is marked with unfamiliar symbols.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED, "The crate is already full.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NO_ITEMS, "You don't have any items to fill the crate.");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_WRONG_TYPE, "This is the wrong type of crate to give to this dwarf.");
    }
}