package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;

@OnlyIn(Dist.CLIENT)
public final class BountyLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_TIER, "Tier: %s");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_TYPE, "Type: %s");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID, "No bounty data");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE, "This is the wrong type of bounty to give to this dwarf.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_DWARF_PROFESSION, "Give this to a %s to get a bounty task.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_NOT_COMPLETE, "This bounty has not been completed.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_COMPLETE, "This bounty is ready to be turned in.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_COMPLETED, "Your current %s bounty has been completed!");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY, "Slay: %s");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY_ALT, "Completed by slaying the specified amount of mobs and handed in for a reward.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COLLECT, "Collect: %s");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COLLECT_ALT, "Filled with specified items and handed in for a reward.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_LOCKED, "The crate is marked with unfamiliar symbols.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED, "The crate is already full.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NO_ITEMS, "You don't have any items to fill the crate.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_WRONG_TYPE, "This is the wrong type of crate to give to this dwarf.");
    }
}