package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.data.id.item.JolCraftCreativeTabIds;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;

public final class JolCraftLanguageKeys extends AbstractLanguageKeys {

    private JolCraftLanguageKeys() {}

    // ---------------------------------------------------------------------
    // BountyLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_CRATE = category(JolCraftDataKeys.TOOLTIP, "bounty_crate");
    public static final String TOOLTIP_RESTOCK_CRATE = category(JolCraftDataKeys.TOOLTIP, "restock_crate");
    public static final String TOOLTIP_RESTOCK_CRATE_NO_NEED = category(JolCraftDataKeys.TOOLTIP, "restock_crate.no_need");
    public static final String TOOLTIP_RESTOCK_CRATE_SUCCESS = category(JolCraftDataKeys.TOOLTIP, "restock_crate.success");
    public static final String TOOLTIP_REROLL_CRATE = category(JolCraftDataKeys.TOOLTIP, "reroll_crate");
    public static final String TOOLTIP_REROLL_CRATE_FAIL = category(JolCraftDataKeys.TOOLTIP, "reroll_crate.fail");
    public static final String TOOLTIP_REROLL_CRATE_SUCCESS = category(JolCraftDataKeys.TOOLTIP, "reroll_crate.success");

    public static final String TOOLTIP_BOUNTY_TIER = tooltip(JolCraftDataKeys.BOUNTY, "tier");
    public static final String TOOLTIP_BOUNTY_TYPE = tooltip(JolCraftDataKeys.BOUNTY, "type");
    public static final String TOOLTIP_BOUNTY_WRONG_TYPE = tooltip(JolCraftDataKeys.BOUNTY, "wrong_type");
    public static final String TOOLTIP_BOUNTY_NO_TYPE = tooltip(JolCraftDataKeys.BOUNTY, "no_type");
    public static final String TOOLTIP_BOUNTY_MERCHANT = tooltip(JolCraftDataKeys.BOUNTY, "merchant");
    public static final String TOOLTIP_BOUNTY_MINER = tooltip(JolCraftDataKeys.BOUNTY, "miner");

    public static final String TOOLTIP_BOUNTY_CRATE_TARGET = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "target");
    public static final String TOOLTIP_BOUNTY_CRATE_COUNT = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "count");
    public static final String TOOLTIP_BOUNTY_CRATE_TIER = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "tier");
    public static final String TOOLTIP_BOUNTY_INVALID = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "invalid");
    public static final String TOOLTIP_BOUNTY_CRATE_LOCKED = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "locked");
    public static final String TOOLTIP_BOUNTY_CRATE_FILLED = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "filled");
    public static final String TOOLTIP_BOUNTY_CRATE_FILLED_SOME = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "filled_some");
    public static final String TOOLTIP_BOUNTY_CRATE_NO_ITEMS = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "no_items");
    public static final String TOOLTIP_BOUNTY_CRATE_COMPLETE = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "complete");
    public static final String TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "not_complete");
    public static final String TOOLTIP_BOUNTY_CRATE_WRONG_TYPE = tooltip(JolCraftDataKeys.BOUNTY_CRATE, "wrong_type");

    public static final String TOOLTIP_CRATE_COOLDOWN = tooltip(JolCraftDataKeys.CRATE, "cooldown");
    public static final String TOOLTIP_CRATE_NO_OFFERS_VILLAGER = tooltip(JolCraftDataKeys.CRATE, "no_offers_villager");
    public static final String TOOLTIP_CRATE_NO_OFFERS_DWARF = tooltip(JolCraftDataKeys.CRATE, "no_offers_dwarf");

    // ---------------------------------------------------------------------
    // CompassLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_STRUCTURE_UNKNOWN = tooltip(JolCraftDataKeys.STRUCTURE, JolCraftDataKeys.UNKNOWN);
    public static final String TOOLTIP_STRUCTURE_DISCOVERED = tooltip(JolCraftDataKeys.STRUCTURE, "discovered");

    public static final String TOOLTIP_DEEPSLATE_COMPASS_TRACKING = category(JolCraftDataKeys.TOOLTIP, JolCraftDataKeys.DEEPSLATE_COMPASS);
    public static final String TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE = tooltip(JolCraftDataKeys.DEEPSLATE_COMPASS, "no_structure");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_LOCATE = tooltip(JolCraftDataKeys.DEEPSLATE_COMPASS, "locate");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_DIAL_UNKNOWN = tooltip(JolCraftDataKeys.DEEPSLATE_COMPASS_DIAL, JolCraftDataKeys.UNKNOWN);

    // ---------------------------------------------------------------------
    // ContainerLangSubProvider
    // ---------------------------------------------------------------------

    public static final String CONTAINER_LAPIDARY_BENCH = category(JolCraftDataKeys.CONTAINER, JolCraftDataKeys.LAPIDARY_BENCH);

    public static final String TOOLTIP_LAPIDARY_BENCH_LOCKED_CUT_GEMS = tooltip(JolCraftDataKeys.LAPIDARY_BENCH, "locked_cut_gems");
    public static final String TOOLTIP_GEODE = category(JolCraftDataKeys.TOOLTIP, "geode");
    public static final String TOOLTIP_UNCUT_GEM = category(JolCraftDataKeys.TOOLTIP, "uncut_gem");
    public static final String TOOLTIP_ARTISAN_HAMMER = category(JolCraftDataKeys.TOOLTIP, "artisan_hammer");
    public static final String TOOLTIP_CHISEL = category(JolCraftDataKeys.TOOLTIP, "chisel");
    public static final String TOOLTIP_CUT_LOCKED = category(JolCraftDataKeys.TOOLTIP, "cut_locked");

    public static final String TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX = tooltip(JolCraftDataKeys.FERMENTING_CAULDRON, "ingredient_max");
    public static final String TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI = tooltip(JolCraftDataKeys.FERMENTING_CAULDRON, "locked_multi");

    public static final String CONTAINER_STRONGBOX = category(JolCraftDataKeys.CONTAINER, JolCraftDataKeys.STRONGBOX);
    public static final String CONTAINER_STRONGBOX_LOCKED = category(JolCraftDataKeys.CONTAINER, "strongbox_locked");

    public static final String TOOLTIP_LOCKPICK = category(JolCraftDataKeys.TOOLTIP, "lockpick");
    public static final String TOOLTIP_STRONGBOX_NOT_EMPTY = tooltip(JolCraftDataKeys.STRONGBOX, "not_empty");
    public static final String TOOLTIP_STRONGBOX_LOOT = tooltip(JolCraftDataKeys.STRONGBOX, "loot");
    public static final String TOOLTIP_STRONGBOX_SET_LOCKED = tooltip(JolCraftDataKeys.STRONGBOX, "set_locked");
    public static final String TOOLTIP_STRONGBOX_SET_UNLOCKED = tooltip(JolCraftDataKeys.STRONGBOX, "set_unlocked");
    public static final String TOOLTIP_STRONGBOX_LOCKED = tooltip(JolCraftDataKeys.STRONGBOX, "locked");
    public static final String TOOLTIP_STRONGBOX_BUSY = tooltip(JolCraftDataKeys.STRONGBOX, "busy");

    public static final String TOOLTIP_HEARTH_COOLDOWN = tooltip(JolCraftDataKeys.HEARTH, "cooldown");
    public static final String TOOLTIP_HEARTH_NEED_COAL = tooltip(JolCraftDataKeys.HEARTH, "need_coal");
    public static final String TOOLTIP_HEARTH_NOT_SAFE = tooltip(JolCraftDataKeys.HEARTH, "not_safe");
    public static final String TOOLTIP_HEARTH_NO_BED_NEARBY = tooltip(JolCraftDataKeys.HEARTH, "no_bed_nearby");

    // ---------------------------------------------------------------------
    // DwarfLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_NEED_LANG = category(JolCraftDataKeys.TOOLTIP, "need_lang");
    public static final String TOOLTIP_NEED_ANCIENT = category(JolCraftDataKeys.TOOLTIP, "need_ancient");
    public static final String TOOLTIP_ANCIENT_MEMORY = category(JolCraftDataKeys.TOOLTIP, "ancient_memory");
    public static final String TOOLTIP_UNIDENTIFIED = category(JolCraftDataKeys.TOOLTIP, "unidentified");
    public static final String TOOLTIP_UNIDENTIFIED_DWARVEN_TOME = category(JolCraftDataKeys.TOOLTIP, "unidentified_dwarven_tome");
    public static final String TOOLTIP_DWARVEN_TOME_SHIFT = category(JolCraftDataKeys.TOOLTIP, "dwarven_tome.shift");
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNIDENTIFIED = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_tome.unidentified");
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_tome.partial_understanding");
    public static final String TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT = category(JolCraftDataKeys.TOOLTIP, "legendary_ancient_dwarven_tome.shift");
    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS = category(JolCraftDataKeys.TOOLTIP, "dwarven_tome.identify_success");
    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL = category(JolCraftDataKeys.TOOLTIP, "dwarven_tome.identify_fail");
    public static final String TOOLTIP_DWARVEN_TOME_LOCKED = category(JolCraftDataKeys.TOOLTIP, "dwarven_tome." + JolCraftDataKeys.LOCKED);
    public static final String TOOLTIP_DWARVEN_TOME_UNLOCKED = category(JolCraftDataKeys.TOOLTIP, "dwarven_tome." + JolCraftDataKeys.UNLOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_tome." + JolCraftDataKeys.UNLOCKED);
    public static final String TOOLTIP_LEGENDARY_PAGE = category(JolCraftDataKeys.TOOLTIP, "legendary_page");
    public static final String TOOLTIP_PAPER_LOCKED = category(JolCraftDataKeys.TOOLTIP, "paper.locked");
    public static final String TOOLTIP_PARCHMENT_LOCKED = category(JolCraftDataKeys.TOOLTIP, "parchment.locked");
    public static final String TOOLTIP_STONE_LOCKED = category(JolCraftDataKeys.TOOLTIP, "stone.locked");

    public static final String TOOLTIP_DWARVEN_LEXICON_LOCKED = category(JolCraftDataKeys.TOOLTIP, "dwarven_lexicon." + JolCraftDataKeys.LOCKED);
    public static final String TOOLTIP_DWARVEN_LEXICON_UNLOCKED = category(JolCraftDataKeys.TOOLTIP, "dwarven_lexicon." + JolCraftDataKeys.UNLOCKED);
    public static final String TOOLTIP_DWARVEN_LEXICON_USE = category(JolCraftDataKeys.TOOLTIP, "dwarven_lexicon." + JolCraftDataKeys.USE);
    public static final String TOOLTIP_DWARVEN_LEXICON_KNOWS = category(JolCraftDataKeys.TOOLTIP, "dwarven_lexicon." + JolCraftDataKeys.KNOWS);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDataKeys.LOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDataKeys.UNLOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDataKeys.USE);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_READ = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_lexicon.cant_read");
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_USE = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_lexicon.cant_use");
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS = category(JolCraftDataKeys.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftDataKeys.KNOWS);

    public static final String TOOLTIP_TOME_UNLOCK_EMPTY = tooltip("tome_unlock", "empty");
    public static final String TOOLTIP_TOME_UNLOCK_BREW = tooltip("tome_unlock", "brew");
    public static final String TOOLTIP_TOME_UNLOCK_GEMS = tooltip("tome_unlock", "gems");

    public static final String TOOLTIP_WRITTEN_CONTRACT = tooltip("contract", "written");
    public static final String TOOLTIP_SIGNED_CONTRACT = tooltip("contract", "signed");
    public static final String TOOLTIP_PROFESSION_CONTRACT = tooltip("contract", "profession");
    public static final String TOOLTIP_GUILD_SIGIL = category(JolCraftDataKeys.TOOLTIP, "guild_sigil");

    public static final String LEVEL_NOVICE      = "merchant.level.1";
    public static final String LEVEL_APPRENTICE  = "merchant.level.2";
    public static final String LEVEL_JOURNEYMAN  = "merchant.level.3";
    public static final String LEVEL_EXPERT      = "merchant.level.4";
    public static final String LEVEL_MASTER      = "merchant.level.5";

    public static final String TOOLTIP_DWARF_LOCKED = tooltip(JolCraftDataKeys.DWARF, JolCraftDataKeys.LOCKED);
    public static final String TOOLTIP_DWARF_BUSY = tooltip(JolCraftDataKeys.DWARF, "busy");
    public static final String TOOLTIP_DWARF_NOT_PAID = tooltip(JolCraftDataKeys.DWARF, "not_paid");
    public static final String TOOLTIP_DWARF_CANNOT_PROMOTE = tooltip(JolCraftDataKeys.DWARF, "cannot_promote");
    public static final String TOOLTIP_DWARF_CANNOT_SIGN = tooltip(JolCraftDataKeys.DWARF, "cannot_sign");
    public static final String TOOLTIP_GUARD_PROMOTION = tooltip("guard", "promotion");

    // ---------------------------------------------------------------------
    // ItemLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB = itemGroup(JolCraftCreativeTabIds.JOLCRAFT_GENERAL_CREATIVE_TAB);
    public static final String JOLCRAFT_EGG_CREATIVE_TAB = itemGroup(JolCraftCreativeTabIds.JOLCRAFT_EGG_CREATIVE_TAB);

    // ---------------------------------------------------------------------
    // JeiLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JEI_CATEGORY_DWARF_TRADES = category(JolCraftDataKeys.JEI, "dwarf_trades");
    public static final String JEI_CATEGORY_INFO_PAGE   = category(JolCraftDataKeys.JEI, "info_page");

    public static final String JEI_INFO_REPUTATION_TABLET       = category(JolCraftDataKeys.JEI, "info_page.reputation_tablet");
    public static final String JEI_INFO_STRONGBOX               = category(JolCraftDataKeys.JEI, "info_page.strongbox");
    public static final String JEI_INFO_DEEPSLATE_COMPASS       = category(JolCraftDataKeys.JEI, "info_page.deepslate_compass");
    public static final String JEI_INFO_COIN_POUCH              = category(JolCraftDataKeys.JEI, "info_page.coin_pouch");
    public static final String JEI_INFO_DWARVEN_LEXICON         = category(JolCraftDataKeys.JEI, "info_page.dwarven_lexicon");
    public static final String JEI_INFO_ANCIENT_DWARVEN_LEXICON = category(JolCraftDataKeys.JEI, "info_page.ancient_dwarven_lexicon");
    public static final String JEI_INFO_HEARTH                  = category(JolCraftDataKeys.JEI, "info_page.hearth");
    public static final String JEI_INFO_VERDANT                 = category(JolCraftDataKeys.JEI, "info_page.verdant");
    public static final String JEI_INFO_MUSHROOM                = category(JolCraftDataKeys.JEI, "info_page.mushroom");
    public static final String JEI_INFO_FESTERLING              = category(JolCraftDataKeys.JEI, "info_page.festerling");

    // ---------------------------------------------------------------------
    // MiscLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_HOLD_KEY = category(JolCraftDataKeys.TOOLTIP, "hold_key");
    public static final String TOOLTIP_DEV_KEY = category(JolCraftDataKeys.TOOLTIP, "dev_key");

    public static final String TOOLTIP_QUILL_EMPTY = category(JolCraftDataKeys.TOOLTIP, "quill_empty");
    public static final String TOOLTIP_QUILL = category(JolCraftDataKeys.TOOLTIP, "quill");
    public static final String TOOLTIP_QUILL_FULL = category(JolCraftDataKeys.TOOLTIP, "quill_full");

    public static final String TOOLTIP_VANILLA_CROP = category(JolCraftDataKeys.TOOLTIP, "vanilla_crop");
    public static final String TOOLTIP_HOPS_SEED = category(JolCraftDataKeys.TOOLTIP, "hops_seed");
    public static final String TOOLTIP_DEEPSLATE_BULBS = category(JolCraftDataKeys.TOOLTIP, "deepslate_bulbs");

    public static final String TOOLTIP_MALT = category(JolCraftDataKeys.TOOLTIP, "malt");
    public static final String TOOLTIP_HOPS = category(JolCraftDataKeys.TOOLTIP, "hops");
    public static final String TOOLTIP_YEAST = category(JolCraftDataKeys.TOOLTIP, "yeast");
    public static final String TOOLTIP_GLASS_MUG = category(JolCraftDataKeys.TOOLTIP, "glass_mug");

    public static final String TOOLTIP_SPANNER = category(JolCraftDataKeys.TOOLTIP, "spanner");
    public static final String TOOLTIP_SALVAGEABLE = category(JolCraftDataKeys.TOOLTIP, "salvageable");
    public static final String TOOLTIP_SALVAGE = category(JolCraftDataKeys.TOOLTIP, "salvage");

    // ---------------------------------------------------------------------
    // ReputationLangSubProvider
    // ---------------------------------------------------------------------

    public static final String REPUTATION_TIER_0 = mod("reputation_tier.0");
    public static final String REPUTATION_TIER_1 = mod("reputation_tier.1");
    public static final String REPUTATION_TIER_2 = mod("reputation_tier.2");
    public static final String REPUTATION_TIER_3 = mod("reputation_tier.3");
    public static final String REPUTATION_TIER_4 = mod("reputation_tier.4");

    public static final String TOOLTIP_REPUTATION_LOCKED = tooltip(JolCraftDataKeys.REPUTATION, "locked");
    public static final String TOOLTIP_REPUTATION_MAX_TIER = tooltip(JolCraftDataKeys.REPUTATION, "max_tier");
    public static final String TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS = tooltip(JolCraftDataKeys.REPUTATION, "not_enough_endorsements");
    public static final String TOOLTIP_REPUTATION_NEVER_ENDORSE = tooltip(JolCraftDataKeys.REPUTATION, "never_endorse");
    public static final String TOOLTIP_REPUTATION_CANNOT_ENDORSE = tooltip(JolCraftDataKeys.REPUTATION, "cannot_endorse");
    public static final String TOOLTIP_REPUTATION_ALREADY_ENDORSED = tooltip(JolCraftDataKeys.REPUTATION, "already_endorsed");
    public static final String TOOLTIP_REPUTATION_WRONG_TABLET = tooltip(JolCraftDataKeys.REPUTATION, "wrong_tablet");
    public static final String TOOLTIP_REPUTATION_LEVEL_UP = tooltip(JolCraftDataKeys.REPUTATION, "level_up");

    public static final String TOOLTIP_TABLET_OWNER = tooltip(JolCraftDataKeys.TABLET, "owner");
    public static final String TOOLTIP_TABLET_REPUTATION = tooltip(JolCraftDataKeys.TABLET, "tier");
    public static final String TOOLTIP_TABLET_ENDORSEMENTS = tooltip(JolCraftDataKeys.TABLET, "endorsements");
    public static final String TOOLTIP_TABLET_PROGRESS = tooltip(JolCraftDataKeys.TABLET, "progress");
    public static final String TOOLTIP_TABLET_PROGRESS_PREFIX = tooltip(JolCraftDataKeys.TABLET, "progress.prefix");
    public static final String TOOLTIP_TABLET_ENDORSEMENTS_INFO = tooltip(JolCraftDataKeys.TABLET, "endorsements_info");
    public static final String TOOLTIP_TABLET_ADVANCE_INFO = tooltip(JolCraftDataKeys.TABLET, "advance_info");

    // ---------------------------------------------------------------------
    // TrimLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_TRIM_MATERIALS = category(JolCraftDataKeys.TOOLTIP, "trim_material");
    public static final String TOOLTIP_ATTRIBUTE_TRIM_MATERIALS = category(JolCraftDataKeys.TOOLTIP, "attribute_trim_material");
}
