package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class BountyLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    // ---------------------------------------------------------------------
    // Categories
    // ---------------------------------------------------------------------

    public static final String BOUNTY = "bounty";
    public static final String BOUNTY_CRATE = "bounty_crate";
    public static final String CRATE = "crate";

    // ---------------------------------------------------------------------
    // Root tooltip keys (tooltip.<modid>.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_CRATE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "bounty_crate");

    public static final String TOOLTIP_RESTOCK_CRATE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "restock_crate");

    public static final String TOOLTIP_RESTOCK_CRATE_NO_NEED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "restock_crate.no_need");

    public static final String TOOLTIP_RESTOCK_CRATE_SUCCESS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "restock_crate.success");

    public static final String TOOLTIP_REROLL_CRATE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "reroll_crate");

    public static final String TOOLTIP_REROLL_CRATE_FAIL = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "reroll_crate.fail");

    public static final String TOOLTIP_REROLL_CRATE_SUCCESS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "reroll_crate.success");

    // ---------------------------------------------------------------------
    // Bounty keys (tooltip.<modid>.bounty.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_TIER = JolCraftLanguageKeys.tooltip(BOUNTY, "tier");

    public static final String TOOLTIP_BOUNTY_TYPE = JolCraftLanguageKeys.tooltip(BOUNTY, "type");

    public static final String TOOLTIP_BOUNTY_WRONG_TYPE = JolCraftLanguageKeys.tooltip(BOUNTY, "wrong_type");

    public static final String TOOLTIP_BOUNTY_NO_TYPE = JolCraftLanguageKeys.tooltip(BOUNTY, "no_type");

    public static final String TOOLTIP_BOUNTY_MERCHANT = JolCraftLanguageKeys.tooltip(BOUNTY, "merchant");

    public static final String TOOLTIP_BOUNTY_MINER = JolCraftLanguageKeys.tooltip(BOUNTY, "miner");

    // ---------------------------------------------------------------------
    // Bounty crate keys (tooltip.<modid>.bounty_crate.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_CRATE_TARGET = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "target");

    public static final String TOOLTIP_BOUNTY_CRATE_COUNT = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "count");

    public static final String TOOLTIP_BOUNTY_CRATE_TIER = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "tier");

    public static final String TOOLTIP_BOUNTY_INVALID = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "invalid");

    public static final String TOOLTIP_BOUNTY_CRATE_LOCKED = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "locked");

    public static final String TOOLTIP_BOUNTY_CRATE_FILLED = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "filled");

    public static final String TOOLTIP_BOUNTY_CRATE_FILLED_SOME = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "filled_some");

    public static final String TOOLTIP_BOUNTY_CRATE_NO_ITEMS = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "no_items");

    public static final String TOOLTIP_BOUNTY_CRATE_COMPLETE = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "complete");

    public static final String TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "not_complete");

    public static final String TOOLTIP_BOUNTY_CRATE_WRONG_TYPE = JolCraftLanguageKeys.tooltip(BOUNTY_CRATE, "wrong_type");

    // ---------------------------------------------------------------------
    // Crate keys (tooltip.<modid>.crate.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_CRATE_COOLDOWN = JolCraftLanguageKeys.tooltip(CRATE, "cooldown");

    public static final String TOOLTIP_CRATE_NO_OFFERS_VILLAGER = JolCraftLanguageKeys.tooltip(CRATE, "no_offers_villager");

    public static final String TOOLTIP_CRATE_NO_OFFERS_DWARF = JolCraftLanguageKeys.tooltip(CRATE, "no_offers_dwarf");

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Root strings
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_BOUNTY_CRATE,
                "Needs to be filled and handed in for a reward. Right-click when held to fill from inventory. " +
                        "Left-click in inventory with required item to fill or right-click to insert single item. " +
                        "Right-click in inventory to extract items."
        );

        p.putManual(TOOLTIP_RESTOCK_CRATE, "Can be used to restock the inventory of a dwarf or villager trader.");
        p.putManual(TOOLTIP_RESTOCK_CRATE_NO_NEED, "This trader doesn't need restocking.");
        p.putManual(TOOLTIP_RESTOCK_CRATE_SUCCESS, "Trader inventory restocked!");

        p.putManual(TOOLTIP_REROLL_CRATE, "Can be used to reroll the inventory of a dwarf or villager trader.");
        p.putManual(TOOLTIP_REROLL_CRATE_FAIL, "This trader inventory cannot be rerolled!");
        p.putManual(TOOLTIP_REROLL_CRATE_SUCCESS, "Trader inventory rerolled!");

        // -----------------------------------------------------------------
        // Bounty tooltip lines
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_BOUNTY_TIER, "Tier: %d");
        p.putManual(TOOLTIP_BOUNTY_TYPE, "Type: ");
        p.putManual(TOOLTIP_BOUNTY_WRONG_TYPE, "This is the wrong type of bounty to give to this dwarf.");
        p.putManual(TOOLTIP_BOUNTY_NO_TYPE, "Give this to a dwarf to get a bounty crate.");
        p.putManual(TOOLTIP_BOUNTY_MERCHANT, "Give this to a merchant to get a bounty crate.");
        p.putManual(TOOLTIP_BOUNTY_MINER, "Give this to a miner to get a bounty crate.");
        p.putManual(TOOLTIP_BOUNTY_INVALID, "No bounty data");

        // -----------------------------------------------------------------
        // Bounty crate tooltip + messages
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_BOUNTY_CRATE_TARGET, "Target: ");
        p.putManual(TOOLTIP_BOUNTY_CRATE_COUNT, "Required: %s");
        p.putManual(TOOLTIP_BOUNTY_CRATE_TIER, "Tier: %s");
        p.putManual(TOOLTIP_BOUNTY_CRATE_LOCKED, "The crate is marked with unfamiliar symbols.");
        p.putManual(TOOLTIP_BOUNTY_CRATE_FILLED, "The crate is already full.");
        p.putManual(TOOLTIP_BOUNTY_CRATE_FILLED_SOME, "Added %s items to the crate.");
        p.putManual(TOOLTIP_BOUNTY_CRATE_NO_ITEMS, "You don't have any items to fill the crate.");
        p.putManual(TOOLTIP_BOUNTY_CRATE_COMPLETE, "Ready to be turned in.");
        p.putManual(TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE, "This bounty has not been completed.");
        p.putManual(TOOLTIP_BOUNTY_CRATE_WRONG_TYPE, "This is the wrong type of crate to give to this dwarf.");

        // -----------------------------------------------------------------
        // Generic crate messages
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_CRATE_COOLDOWN, "You must wait before you can use another crate.");
        p.putManual(TOOLTIP_CRATE_NO_OFFERS_VILLAGER, "This villager has no trades!");
        p.putManual(TOOLTIP_CRATE_NO_OFFERS_DWARF, "This dwarf has no trades!");
    }
}