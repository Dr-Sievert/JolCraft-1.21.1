package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.data.id.item.JolCraftCreativeTabIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;

public final class JolCraftLanguageKeys extends AbstractLanguageKeys {

    private JolCraftLanguageKeys() {}

    // ---------------------------------------------------------------------
    // BountyLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_CRATE = category(JolCraftDictionary.TOOLTIP, "bounty_crate");
    public static final String TOOLTIP_RESTOCK_CRATE = category(JolCraftDictionary.TOOLTIP, "restock_crate");
    public static final String TOOLTIP_RESTOCK_CRATE_NO_NEED = category(JolCraftDictionary.TOOLTIP, "restock_crate.no_need");
    public static final String TOOLTIP_RESTOCK_CRATE_SUCCESS = category(JolCraftDictionary.TOOLTIP, "restock_crate.success");
    public static final String TOOLTIP_REROLL_CRATE = category(JolCraftDictionary.TOOLTIP, "reroll_crate");
    public static final String TOOLTIP_REROLL_CRATE_FAIL = category(JolCraftDictionary.TOOLTIP, "reroll_crate.fail");
    public static final String TOOLTIP_REROLL_CRATE_SUCCESS = category(JolCraftDictionary.TOOLTIP, "reroll_crate.success");

    public static final String TOOLTIP_BOUNTY_TIER = tooltip(JolCraftDictionary.BOUNTY, "tier");
    public static final String TOOLTIP_BOUNTY_TYPE = tooltip(JolCraftDictionary.BOUNTY, "type");
    public static final String TOOLTIP_BOUNTY_WRONG_TYPE = tooltip(JolCraftDictionary.BOUNTY, "wrong_type");
    public static final String TOOLTIP_BOUNTY_NO_TYPE = tooltip(JolCraftDictionary.BOUNTY, "no_type");
    public static final String TOOLTIP_BOUNTY_MERCHANT = tooltip(JolCraftDictionary.BOUNTY, "merchant");
    public static final String TOOLTIP_BOUNTY_MINER = tooltip(JolCraftDictionary.BOUNTY, "miner");

    public static final String TOOLTIP_BOUNTY_CRATE_TARGET = tooltip(JolCraftDictionary.BOUNTY_CRATE, "target");
    public static final String TOOLTIP_BOUNTY_CRATE_COUNT = tooltip(JolCraftDictionary.BOUNTY_CRATE, "count");
    public static final String TOOLTIP_BOUNTY_CRATE_TIER = tooltip(JolCraftDictionary.BOUNTY_CRATE, "tier");
    public static final String TOOLTIP_BOUNTY_INVALID = tooltip(JolCraftDictionary.BOUNTY_CRATE, "invalid");
    public static final String TOOLTIP_BOUNTY_CRATE_LOCKED = tooltip(JolCraftDictionary.BOUNTY_CRATE, "locked");
    public static final String TOOLTIP_BOUNTY_CRATE_FILLED = tooltip(JolCraftDictionary.BOUNTY_CRATE, "filled");
    public static final String TOOLTIP_BOUNTY_CRATE_FILLED_SOME = tooltip(JolCraftDictionary.BOUNTY_CRATE, "filled_some");
    public static final String TOOLTIP_BOUNTY_CRATE_NO_ITEMS = tooltip(JolCraftDictionary.BOUNTY_CRATE, "no_items");
    public static final String TOOLTIP_BOUNTY_CRATE_COMPLETE = tooltip(JolCraftDictionary.BOUNTY_CRATE, "complete");
    public static final String TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE = tooltip(JolCraftDictionary.BOUNTY_CRATE, "not_complete");
    public static final String TOOLTIP_BOUNTY_CRATE_WRONG_TYPE = tooltip(JolCraftDictionary.BOUNTY_CRATE, "wrong_type");

    public static final String TOOLTIP_CRATE_COOLDOWN = tooltip(JolCraftDictionary.CRATE, "cooldown");
    public static final String TOOLTIP_CRATE_NO_OFFERS_VILLAGER = tooltip(JolCraftDictionary.CRATE, "no_offers_villager");
    public static final String TOOLTIP_CRATE_NO_OFFERS_DWARF = tooltip(JolCraftDictionary.CRATE, "no_offers_dwarf");

    // ---------------------------------------------------------------------
    // CompassLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_STRUCTURE_DISCOVERED = tooltip(JolCraftDictionary.STRUCTURE, "discovered");

    public static final String TOOLTIP_DEEPSLATE_COMPASS_TRACKING = category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.DEEPSLATE_COMPASS);
    public static final String TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE = tooltip(JolCraftDictionary.DEEPSLATE_COMPASS, "no_structure");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_LOCATE = tooltip(JolCraftDictionary.DEEPSLATE_COMPASS, "locate");

    // ---------------------------------------------------------------------
    // ContainerLangSubProvider
    // ---------------------------------------------------------------------

    public static final String CONTAINER_LAPIDARY_BENCH = category(JolCraftDictionary.CONTAINER, JolCraftDictionary.LAPIDARY_BENCH);

    public static final String TOOLTIP_LAPIDARY_BENCH_LOCKED_CUT_GEMS = tooltip(JolCraftDictionary.LAPIDARY_BENCH, "locked_cut_gems");
    public static final String TOOLTIP_GEODE = category(JolCraftDictionary.TOOLTIP, "geode");
    public static final String TOOLTIP_UNCUT_GEM = category(JolCraftDictionary.TOOLTIP, "uncut_gem");
    public static final String TOOLTIP_ARTISAN_HAMMER = category(JolCraftDictionary.TOOLTIP, "artisan_hammer");
    public static final String TOOLTIP_CHISEL = category(JolCraftDictionary.TOOLTIP, "chisel");
    public static final String TOOLTIP_CUT_LOCKED = category(JolCraftDictionary.TOOLTIP, "cut_locked");

    public static final String TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX = tooltip(JolCraftDictionary.FERMENTING_CAULDRON, "ingredient_max");
    public static final String TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI = tooltip(JolCraftDictionary.FERMENTING_CAULDRON, "locked_multi");

    public static final String CONTAINER_STRONGBOX = category(JolCraftDictionary.CONTAINER, JolCraftDictionary.STRONGBOX);
    public static final String CONTAINER_STRONGBOX_LOCKED = category(JolCraftDictionary.CONTAINER, "strongbox_locked");

    public static final String TOOLTIP_LOCKPICK = category(JolCraftDictionary.TOOLTIP, "lockpick");
    public static final String TOOLTIP_STRONGBOX_NOT_EMPTY = tooltip(JolCraftDictionary.STRONGBOX, "not_empty");
    public static final String TOOLTIP_STRONGBOX_LOOT = tooltip(JolCraftDictionary.STRONGBOX, "loot");
    public static final String TOOLTIP_STRONGBOX_SET_LOCKED = tooltip(JolCraftDictionary.STRONGBOX, "set_locked");
    public static final String TOOLTIP_STRONGBOX_SET_UNLOCKED = tooltip(JolCraftDictionary.STRONGBOX, "set_unlocked");
    public static final String TOOLTIP_STRONGBOX_LOCKED = tooltip(JolCraftDictionary.STRONGBOX, "locked");
    public static final String TOOLTIP_STRONGBOX_BUSY = tooltip(JolCraftDictionary.STRONGBOX, "busy");

    public static final String TOOLTIP_HEARTH_COOLDOWN = tooltip(JolCraftDictionary.HEARTH, "cooldown");
    public static final String TOOLTIP_HEARTH_NEED_COAL = tooltip(JolCraftDictionary.HEARTH, "need_coal");
    public static final String TOOLTIP_HEARTH_NOT_SAFE = tooltip(JolCraftDictionary.HEARTH, "not_safe");
    public static final String TOOLTIP_HEARTH_NO_BED_NEARBY = tooltip(JolCraftDictionary.HEARTH, "no_bed_nearby");

    // ---------------------------------------------------------------------
    // DwarfLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_NEED_LANG = category(JolCraftDictionary.TOOLTIP, "need_lang");
    public static final String TOOLTIP_NEED_ANCIENT = category(JolCraftDictionary.TOOLTIP, "need_ancient");
    public static final String TOOLTIP_ANCIENT_MEMORY = category(JolCraftDictionary.TOOLTIP, "ancient_memory");
    public static final String TOOLTIP_UNIDENTIFIED = category(JolCraftDictionary.TOOLTIP, "unidentified");
    public static final String TOOLTIP_UNIDENTIFIED_DWARVEN_TOME = category(JolCraftDictionary.TOOLTIP, "unidentified_dwarven_tome");
    public static final String TOOLTIP_DWARVEN_TOME_SHIFT = category(JolCraftDictionary.TOOLTIP, "dwarven_tome.shift");
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNIDENTIFIED = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_tome.unidentified");
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_tome.partial_understanding");
    public static final String TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT = category(JolCraftDictionary.TOOLTIP, "legendary_ancient_dwarven_tome.shift");
    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS = category(JolCraftDictionary.TOOLTIP, "dwarven_tome.identify_success");
    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL = category(JolCraftDictionary.TOOLTIP, "dwarven_tome.identify_fail");
    public static final String TOOLTIP_DWARVEN_TOME_LOCKED = category(JolCraftDictionary.TOOLTIP, "dwarven_tome." + JolCraftDictionary.LOCKED);
    public static final String TOOLTIP_DWARVEN_TOME_UNLOCKED = category(JolCraftDictionary.TOOLTIP, "dwarven_tome." + JolCraftDictionary.UNLOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_tome." + JolCraftDictionary.UNLOCKED);
    public static final String TOOLTIP_LEGENDARY_PAGE = category(JolCraftDictionary.TOOLTIP, "legendary_page");
    public static final String TOOLTIP_PAPER_LOCKED = category(JolCraftDictionary.TOOLTIP, "paper.locked");
    public static final String TOOLTIP_PARCHMENT_LOCKED = category(JolCraftDictionary.TOOLTIP, "parchment.locked");
    public static final String TOOLTIP_STONE_LOCKED = category(JolCraftDictionary.TOOLTIP, "stone.locked");

    public static final String TOOLTIP_DWARVEN_LEXICON_LOCKED = category(JolCraftDictionary.TOOLTIP, "dwarven_lexicon." + JolCraftDictionary.LOCKED);
    public static final String TOOLTIP_DWARVEN_LEXICON_UNLOCKED = category(JolCraftDictionary.TOOLTIP, "dwarven_lexicon." + JolCraftDictionary.UNLOCKED);
    public static final String TOOLTIP_DWARVEN_LEXICON_USE = category(JolCraftDictionary.TOOLTIP, "dwarven_lexicon." + JolCraftDictionary.USE);
    public static final String TOOLTIP_DWARVEN_LEXICON_KNOWS = category(JolCraftDictionary.TOOLTIP, "dwarven_lexicon." + JolCraftDictionary.KNOWS);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDictionary.LOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDictionary.UNLOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDictionary.USE);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_READ = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_lexicon.cant_read");
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_USE = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_lexicon.cant_use");
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS = category(JolCraftDictionary.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDictionary.KNOWS);

    public static final String TOOLTIP_TOME_UNLOCK_EMPTY = tooltip("tome_unlock", "empty");
    public static final String TOOLTIP_TOME_UNLOCK_BREW = tooltip("tome_unlock", "brew");
    public static final String TOOLTIP_TOME_UNLOCK_GEMS = tooltip("tome_unlock", "gems");

    public static final String TOOLTIP_WRITTEN_CONTRACT = tooltip("contract", "written");
    public static final String TOOLTIP_SIGNED_CONTRACT = tooltip("contract", "signed");
    public static final String TOOLTIP_PROFESSION_CONTRACT = tooltip("contract", "profession");
    public static final String TOOLTIP_GUILD_SIGIL = category(JolCraftDictionary.TOOLTIP, "guild_sigil");

    public static final String MERCHANT_TITLE = "merchant.title";
    public static final String MERCHANT_TRADES = "merchant.trades";
    public static final String MERCHANT_DEPRECATED = "merchant.deprecated";

    public static final String LEVEL_NOVICE      = "merchant.level.1";
    public static final String LEVEL_APPRENTICE  = "merchant.level.2";
    public static final String LEVEL_JOURNEYMAN  = "merchant.level.3";
    public static final String LEVEL_EXPERT      = "merchant.level.4";
    public static final String LEVEL_MASTER      = "merchant.level.5";

    public static final String TOOLTIP_DWARF_LOCKED = tooltip(JolCraftDictionary.DWARF, JolCraftDictionary.LOCKED);
    public static final String TOOLTIP_DWARF_BUSY = tooltip(JolCraftDictionary.DWARF, "busy");
    public static final String TOOLTIP_DWARF_NOT_PAID = tooltip(JolCraftDictionary.DWARF, "not_paid");
    public static final String TOOLTIP_DWARF_CANNOT_PROMOTE = tooltip(JolCraftDictionary.DWARF, "cannot_promote");
    public static final String TOOLTIP_DWARF_CANNOT_SIGN = tooltip(JolCraftDictionary.DWARF, "cannot_sign");
    public static final String TOOLTIP_GUARD_PROMOTION = tooltip("guard", "promotion");

    // ---------------------------------------------------------------------
    // ItemLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB = itemGroup(JolCraftCreativeTabIds.JOLCRAFT_GENERAL_CREATIVE_TAB);
    public static final String JOLCRAFT_EGG_CREATIVE_TAB = itemGroup(JolCraftCreativeTabIds.JOLCRAFT_EGG_CREATIVE_TAB);

    // ---------------------------------------------------------------------
    // JeiLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JEI_CATEGORY_DWARF_TRADES = category(JolCraftDictionary.JEI, "dwarf_trades");
    public static final String JEI_CATEGORY_INFO_PAGE   = category(JolCraftDictionary.JEI, "info_page");

    public static final String JEI_INFO_REPUTATION_TABLET       = category(JolCraftDictionary.JEI, "info_page.reputation_tablet");
    public static final String JEI_INFO_STRONGBOX               = category(JolCraftDictionary.JEI, "info_page.strongbox");
    public static final String JEI_INFO_DEEPSLATE_COMPASS       = category(JolCraftDictionary.JEI, "info_page.deepslate_compass");
    public static final String JEI_INFO_COIN_POUCH              = category(JolCraftDictionary.JEI, "info_page.coin_pouch");
    public static final String JEI_INFO_DWARVEN_LEXICON         = category(JolCraftDictionary.JEI, "info_page.dwarven_lexicon");
    public static final String JEI_INFO_ANCIENT_DWARVEN_LEXICON = category(JolCraftDictionary.JEI, "info_page.ancient_dwarven_lexicon");
    public static final String JEI_INFO_HEARTH                  = category(JolCraftDictionary.JEI, "info_page.hearth");
    public static final String JEI_INFO_VERDANT                 = category(JolCraftDictionary.JEI, "info_page.verdant");
    public static final String JEI_INFO_MUSHROOM                = category(JolCraftDictionary.JEI, "info_page.mushroom");
    public static final String JEI_INFO_FESTERLING              = category(JolCraftDictionary.JEI, "info_page.festerling");

    // ---------------------------------------------------------------------
    // MiscLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_HOLD_KEY = category(JolCraftDictionary.TOOLTIP, "hold_key");
    public static final String TOOLTIP_DEV_KEY = category(JolCraftDictionary.TOOLTIP, "dev_key");
    public static final String UNKNOWN = mod(JolCraftDictionary.UNKNOWN);

    public static final String TOOLTIP_QUILL_EMPTY = category(JolCraftDictionary.TOOLTIP, "quill_empty");
    public static final String TOOLTIP_QUILL = category(JolCraftDictionary.TOOLTIP, "quill");
    public static final String TOOLTIP_QUILL_FULL = category(JolCraftDictionary.TOOLTIP, "quill_full");

    public static final String TOOLTIP_VANILLA_CROP = category(JolCraftDictionary.TOOLTIP, "vanilla_crop");
    public static final String TOOLTIP_HOPS_SEED = category(JolCraftDictionary.TOOLTIP, "hops_seed");
    public static final String TOOLTIP_DEEPSLATE_BULBS = category(JolCraftDictionary.TOOLTIP, "deepslate_bulbs");

    public static final String TOOLTIP_MALT = category(JolCraftDictionary.TOOLTIP, "malt");
    public static final String TOOLTIP_HOPS = category(JolCraftDictionary.TOOLTIP, "hops");
    public static final String TOOLTIP_YEAST = category(JolCraftDictionary.TOOLTIP, "yeast");
    public static final String TOOLTIP_GLASS_MUG = category(JolCraftDictionary.TOOLTIP, "glass_mug");

    public static final String TOOLTIP_SPANNER = category(JolCraftDictionary.TOOLTIP, "spanner");
    public static final String TOOLTIP_SALVAGEABLE = category(JolCraftDictionary.TOOLTIP, "salvageable");
    public static final String TOOLTIP_SALVAGE = category(JolCraftDictionary.TOOLTIP, "salvage");

    // ---------------------------------------------------------------------
    // ReputationLangSubProvider
    // ---------------------------------------------------------------------

    public static final String REPUTATION_TIER_0 = mod("reputation_tier.0");
    public static final String REPUTATION_TIER_1 = mod("reputation_tier.1");
    public static final String REPUTATION_TIER_2 = mod("reputation_tier.2");
    public static final String REPUTATION_TIER_3 = mod("reputation_tier.3");
    public static final String REPUTATION_TIER_4 = mod("reputation_tier.4");

    public static final String TOOLTIP_REPUTATION_LOCKED = tooltip(JolCraftDictionary.REPUTATION, "locked");
    public static final String TOOLTIP_REPUTATION_MAX_TIER = tooltip(JolCraftDictionary.REPUTATION, "max_tier");
    public static final String TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS = tooltip(JolCraftDictionary.REPUTATION, "not_enough_endorsements");
    public static final String TOOLTIP_REPUTATION_NEVER_ENDORSE = tooltip(JolCraftDictionary.REPUTATION, "never_endorse");
    public static final String TOOLTIP_REPUTATION_CANNOT_ENDORSE = tooltip(JolCraftDictionary.REPUTATION, "cannot_endorse");
    public static final String TOOLTIP_REPUTATION_ALREADY_ENDORSED = tooltip(JolCraftDictionary.REPUTATION, "already_endorsed");
    public static final String TOOLTIP_REPUTATION_WRONG_TABLET = tooltip(JolCraftDictionary.REPUTATION, "wrong_tablet");
    public static final String TOOLTIP_REPUTATION_LEVEL_UP = tooltip(JolCraftDictionary.REPUTATION, "level_up");

    public static final String TOOLTIP_TABLET_OWNER = tooltip(JolCraftDictionary.TABLET, "owner");
    public static final String TOOLTIP_TABLET_REPUTATION = tooltip(JolCraftDictionary.TABLET, "tier");
    public static final String TOOLTIP_TABLET_ENDORSEMENTS = tooltip(JolCraftDictionary.TABLET, "endorsements");
    public static final String TOOLTIP_TABLET_PROGRESS = tooltip(JolCraftDictionary.TABLET, "progress");
    public static final String TOOLTIP_TABLET_PROGRESS_PREFIX = tooltip(JolCraftDictionary.TABLET, "progress.prefix");
    public static final String TOOLTIP_TABLET_ENDORSEMENTS_INFO = tooltip(JolCraftDictionary.TABLET, "endorsements_info");
    public static final String TOOLTIP_TABLET_ADVANCE_INFO = tooltip(JolCraftDictionary.TABLET, "advance_info");

    // ---------------------------------------------------------------------
    // TrimLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_TRIM_MATERIALS = category(JolCraftDictionary.TOOLTIP, "trim_material");
    public static final String TOOLTIP_ATTRIBUTE_TRIM_MATERIALS = category(JolCraftDictionary.TOOLTIP, "attribute_trim_material");
}
