package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class BountyLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Root strings
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE,
                "Needs to be filled and handed in for a reward. Right-click when held to fill from inventory. " +
                        "Left-click in inventory with required item to fill or right-click to insert single item. " +
                        "Right-click in inventory to extract items."
        );

        p.putManual(JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE, "Can be used to restock the inventory of a dwarf or villager trader.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_NO_NEED, "This trader doesn't need restocking.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_SUCCESS, "Trader inventory restocked!");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE, "Can be used to reroll the inventory of a dwarf or villager trader.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_FAIL, "This trader inventory cannot be rerolled!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_SUCCESS, "Trader inventory rerolled!");

        // -----------------------------------------------------------------
        // Bounty tooltip lines
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_TIER, "Tier: %d");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_TYPE, "Type: ");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE, "This is the wrong type of bounty to give to this dwarf.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_NO_TYPE, "Give this to a dwarf to get a bounty crate.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_MERCHANT, "Give this to a merchant to get a bounty crate.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_MINER, "Give this to a miner to get a bounty crate.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID, "No bounty data");

        // -----------------------------------------------------------------
        // Bounty crate tooltip + messages
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_TARGET, "Target: ");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COUNT, "Required: %s");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_TIER, "Tier: %s");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_LOCKED, "The crate is marked with unfamiliar symbols.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED, "The crate is already full.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED_SOME, "Added %s items to the crate.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NO_ITEMS, "You don't have any items to fill the crate.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COMPLETE, "Ready to be turned in.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE, "This bounty has not been completed.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_WRONG_TYPE, "This is the wrong type of crate to give to this dwarf.");

        // -----------------------------------------------------------------
        // Generic crate messages
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_CRATE_COOLDOWN, "You must wait before you can use another crate.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_CRATE_NO_OFFERS_VILLAGER, "This villager has no trades!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_CRATE_NO_OFFERS_DWARF, "This dwarf has no trades!");
    }
}