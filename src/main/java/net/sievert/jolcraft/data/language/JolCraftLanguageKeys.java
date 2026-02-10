package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.data.id.JolCraftIds;

public final class JolCraftLanguageKeys extends AbstractLanguageKeys {

    private JolCraftLanguageKeys() {}

    // ---------------------------------------------------------------------
    // BountyLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_CRATE = category(JolCraftLanguageCategory.TOOLTIP, "bounty_crate");
    public static final String TOOLTIP_RESTOCK_CRATE = category(JolCraftLanguageCategory.TOOLTIP, "restock_crate");
    public static final String TOOLTIP_RESTOCK_CRATE_NO_NEED = category(JolCraftLanguageCategory.TOOLTIP, "restock_crate.no_need");
    public static final String TOOLTIP_RESTOCK_CRATE_SUCCESS = category(JolCraftLanguageCategory.TOOLTIP, "restock_crate.success");
    public static final String TOOLTIP_REROLL_CRATE = category(JolCraftLanguageCategory.TOOLTIP, "reroll_crate");
    public static final String TOOLTIP_REROLL_CRATE_FAIL = category(JolCraftLanguageCategory.TOOLTIP, "reroll_crate.fail");
    public static final String TOOLTIP_REROLL_CRATE_SUCCESS = category(JolCraftLanguageCategory.TOOLTIP, "reroll_crate.success");

    public static final String TOOLTIP_BOUNTY_TIER = tooltip(JolCraftKeyParts.BOUNTY, "tier");
    public static final String TOOLTIP_BOUNTY_TYPE = tooltip(JolCraftKeyParts.BOUNTY, "type");
    public static final String TOOLTIP_BOUNTY_WRONG_TYPE = tooltip(JolCraftKeyParts.BOUNTY, "wrong_type");
    public static final String TOOLTIP_BOUNTY_NO_TYPE = tooltip(JolCraftKeyParts.BOUNTY, "no_type");
    public static final String TOOLTIP_BOUNTY_MERCHANT = tooltip(JolCraftKeyParts.BOUNTY, "merchant");
    public static final String TOOLTIP_BOUNTY_MINER = tooltip(JolCraftKeyParts.BOUNTY, "miner");

    public static final String TOOLTIP_BOUNTY_CRATE_TARGET = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "target");
    public static final String TOOLTIP_BOUNTY_CRATE_COUNT = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "count");
    public static final String TOOLTIP_BOUNTY_CRATE_TIER = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "tier");
    public static final String TOOLTIP_BOUNTY_INVALID = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "invalid");
    public static final String TOOLTIP_BOUNTY_CRATE_LOCKED = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "locked");
    public static final String TOOLTIP_BOUNTY_CRATE_FILLED = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "filled");
    public static final String TOOLTIP_BOUNTY_CRATE_FILLED_SOME = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "filled_some");
    public static final String TOOLTIP_BOUNTY_CRATE_NO_ITEMS = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "no_items");
    public static final String TOOLTIP_BOUNTY_CRATE_COMPLETE = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "complete");
    public static final String TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "not_complete");
    public static final String TOOLTIP_BOUNTY_CRATE_WRONG_TYPE = tooltip(JolCraftKeyParts.BOUNTY_CRATE, "wrong_type");

    public static final String TOOLTIP_CRATE_COOLDOWN = tooltip(JolCraftKeyParts.CRATE, "cooldown");
    public static final String TOOLTIP_CRATE_NO_OFFERS_VILLAGER = tooltip(JolCraftKeyParts.CRATE, "no_offers_villager");
    public static final String TOOLTIP_CRATE_NO_OFFERS_DWARF = tooltip(JolCraftKeyParts.CRATE, "no_offers_dwarf");

    // ---------------------------------------------------------------------
    // CompassLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_STRUCTURE_UNKNOWN = tooltip(JolCraftKeyParts.STRUCTURE, JolCraftKeyParts.UNKNOWN);
    public static final String TOOLTIP_STRUCTURE_DISCOVERED = tooltip(JolCraftKeyParts.STRUCTURE, "discovered");

    public static final String TOOLTIP_DEEPSLATE_COMPASS_TRACKING = category(JolCraftLanguageCategory.TOOLTIP, JolCraftKeyParts.DEEPSLATE_COMPASS);
    public static final String TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE = tooltip(JolCraftKeyParts.DEEPSLATE_COMPASS, "no_structure");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_LOCATE = tooltip(JolCraftKeyParts.DEEPSLATE_COMPASS, "locate");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_DIAL_UNKNOWN = tooltip(JolCraftKeyParts.DEEPSLATE_COMPASS_DIAL, JolCraftKeyParts.UNKNOWN);

    // ---------------------------------------------------------------------
    // ContainerLangSubProvider
    // ---------------------------------------------------------------------

    public static final String CONTAINER_LAPIDARY_BENCH = category(JolCraftLanguageCategory.CONTAINER, JolCraftKeyParts.LAPIDARY_BENCH);

    public static final String TOOLTIP_LAPIDARY_BENCH_LOCKED_CUT_GEMS = tooltip(JolCraftKeyParts.LAPIDARY_BENCH, "locked_cut_gems");
    public static final String TOOLTIP_GEODE = category(JolCraftLanguageCategory.TOOLTIP, "geode");
    public static final String TOOLTIP_UNCUT_GEM = category(JolCraftLanguageCategory.TOOLTIP, "uncut_gem");
    public static final String TOOLTIP_ARTISAN_HAMMER = category(JolCraftLanguageCategory.TOOLTIP, "artisan_hammer");
    public static final String TOOLTIP_CHISEL = category(JolCraftLanguageCategory.TOOLTIP, "chisel");
    public static final String TOOLTIP_CUT_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "cut_locked");

    public static final String TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX = tooltip(JolCraftKeyParts.FERMENTING_CAULDRON, "ingredient_max");
    public static final String TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI = tooltip(JolCraftKeyParts.FERMENTING_CAULDRON, "locked_multi");

    public static final String CONTAINER_STRONGBOX = category(JolCraftLanguageCategory.CONTAINER, JolCraftKeyParts.STRONGBOX);
    public static final String CONTAINER_STRONGBOX_LOCKED = category(JolCraftLanguageCategory.CONTAINER, "strongbox_locked");

    public static final String TOOLTIP_LOCKPICK = category(JolCraftLanguageCategory.TOOLTIP, "lockpick");
    public static final String TOOLTIP_STRONGBOX_NOT_EMPTY = tooltip(JolCraftKeyParts.STRONGBOX, "not_empty");
    public static final String TOOLTIP_STRONGBOX_LOOT = tooltip(JolCraftKeyParts.STRONGBOX, "loot");
    public static final String TOOLTIP_STRONGBOX_SET_LOCKED = tooltip(JolCraftKeyParts.STRONGBOX, "set_locked");
    public static final String TOOLTIP_STRONGBOX_SET_UNLOCKED = tooltip(JolCraftKeyParts.STRONGBOX, "set_unlocked");
    public static final String TOOLTIP_STRONGBOX_LOCKED = tooltip(JolCraftKeyParts.STRONGBOX, "locked");
    public static final String TOOLTIP_STRONGBOX_BUSY = tooltip(JolCraftKeyParts.STRONGBOX, "busy");

    public static final String TOOLTIP_HEARTH_COOLDOWN = tooltip(JolCraftKeyParts.HEARTH, "cooldown");
    public static final String TOOLTIP_HEARTH_NEED_COAL = tooltip(JolCraftKeyParts.HEARTH, "need_coal");
    public static final String TOOLTIP_HEARTH_NOT_SAFE = tooltip(JolCraftKeyParts.HEARTH, "not_safe");
    public static final String TOOLTIP_HEARTH_NO_BED_NEARBY = tooltip(JolCraftKeyParts.HEARTH, "no_bed_nearby");

    // ---------------------------------------------------------------------
    // DwarfLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_NEED_LANG = category(JolCraftLanguageCategory.TOOLTIP, "need_lang");
    public static final String TOOLTIP_NEED_ANCIENT = category(JolCraftLanguageCategory.TOOLTIP, "need_ancient");
    public static final String TOOLTIP_ANCIENT_MEMORY = category(JolCraftLanguageCategory.TOOLTIP, "ancient_memory");
    public static final String TOOLTIP_UNIDENTIFIED = category(JolCraftLanguageCategory.TOOLTIP, "unidentified");
    public static final String TOOLTIP_UNIDENTIFIED_DWARVEN_TOME = category(JolCraftLanguageCategory.TOOLTIP, "unidentified_dwarven_tome");
    public static final String TOOLTIP_DWARVEN_TOME_SHIFT = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome.shift");
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNIDENTIFIED = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_tome.unidentified");
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_tome.partial_understanding");
    public static final String TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT = category(JolCraftLanguageCategory.TOOLTIP, "legendary_ancient_dwarven_tome.shift");
    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome.identify_success");
    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome.identify_fail");
    public static final String TOOLTIP_DWARVEN_TOME_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome." + JolCraftKeyParts.LOCKED);
    public static final String TOOLTIP_DWARVEN_TOME_UNLOCKED = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome." + JolCraftKeyParts.UNLOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_tome." + JolCraftKeyParts.UNLOCKED);
    public static final String TOOLTIP_LEGENDARY_PAGE = category(JolCraftLanguageCategory.TOOLTIP, "legendary_page");
    public static final String TOOLTIP_PAPER_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "paper.locked");
    public static final String TOOLTIP_PARCHMENT_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "parchment.locked");
    public static final String TOOLTIP_STONE_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "stone.locked");

    public static final String TOOLTIP_DWARVEN_LEXICON_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + JolCraftKeyParts.LOCKED);
    public static final String TOOLTIP_DWARVEN_LEXICON_UNLOCKED = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + JolCraftKeyParts.UNLOCKED);
    public static final String TOOLTIP_DWARVEN_LEXICON_USE = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + JolCraftKeyParts.USE);
    public static final String TOOLTIP_DWARVEN_LEXICON_KNOWS = category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + JolCraftKeyParts.KNOWS);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftKeyParts.LOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftKeyParts.UNLOCKED);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftKeyParts.USE);
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_READ = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon.cant_read");
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_USE = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon.cant_use");
    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS = category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + JolCraftKeyParts.KNOWS);

    public static final String TOOLTIP_TOME_UNLOCK_EMPTY = tooltip("tome_unlock", "empty");
    public static final String TOOLTIP_TOME_UNLOCK_BREW = tooltip("tome_unlock", "brew");
    public static final String TOOLTIP_TOME_UNLOCK_GEMS = tooltip("tome_unlock", "gems");

    public static final String TOOLTIP_WRITTEN_CONTRACT = tooltip("contract", "written");
    public static final String TOOLTIP_SIGNED_CONTRACT = tooltip("contract", "signed");
    public static final String TOOLTIP_PROFESSION_CONTRACT = tooltip("contract", "profession");
    public static final String TOOLTIP_GUILD_SIGIL = category(JolCraftLanguageCategory.TOOLTIP, "guild_sigil");

    public static final String LEVEL_NOVICE      = "merchant.level.1";
    public static final String LEVEL_APPRENTICE  = "merchant.level.2";
    public static final String LEVEL_JOURNEYMAN  = "merchant.level.3";
    public static final String LEVEL_EXPERT      = "merchant.level.4";
    public static final String LEVEL_MASTER      = "merchant.level.5";

    public static final String TOOLTIP_DWARF_LOCKED = tooltip(JolCraftKeyParts.DWARF, JolCraftKeyParts.LOCKED);
    public static final String TOOLTIP_DWARF_BUSY = tooltip(JolCraftKeyParts.DWARF, "busy");
    public static final String TOOLTIP_DWARF_NOT_PAID = tooltip(JolCraftKeyParts.DWARF, "not_paid");
    public static final String TOOLTIP_DWARF_CANNOT_PROMOTE = tooltip(JolCraftKeyParts.DWARF, "cannot_promote");
    public static final String TOOLTIP_DWARF_CANNOT_SIGN = tooltip(JolCraftKeyParts.DWARF, "cannot_sign");
    public static final String TOOLTIP_GUARD_PROMOTION = tooltip("guard", "promotion");

    // ---------------------------------------------------------------------
    // ItemLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB_KEY = "itemGroup." + MODID + "." + JolCraftIds.JOLCRAFT_GENERAL_CREATIVE_TAB;
    public static final String JOLCRAFT_EGG_CREATIVE_TAB_KEY = "itemGroup." + MODID + "." + JolCraftIds.JOLCRAFT_EGG_CREATIVE_TAB;

    // ---------------------------------------------------------------------
    // JeiLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JEI_CATEGORY_DWARF_TRADES = category(JolCraftLanguageCategory.JEI, "dwarf_trades");
    public static final String JEI_CATEGORY_INFO_PAGE   = category(JolCraftLanguageCategory.JEI, "info_page");

    public static final String JEI_INFO_REPUTATION_TABLET       = category(JolCraftLanguageCategory.JEI, "info_page.reputation_tablet");
    public static final String JEI_INFO_STRONGBOX               = category(JolCraftLanguageCategory.JEI, "info_page.strongbox");
    public static final String JEI_INFO_DEEPSLATE_COMPASS       = category(JolCraftLanguageCategory.JEI, "info_page.deepslate_compass");
    public static final String JEI_INFO_COIN_POUCH              = category(JolCraftLanguageCategory.JEI, "info_page.coin_pouch");
    public static final String JEI_INFO_DWARVEN_LEXICON         = category(JolCraftLanguageCategory.JEI, "info_page.dwarven_lexicon");
    public static final String JEI_INFO_ANCIENT_DWARVEN_LEXICON = category(JolCraftLanguageCategory.JEI, "info_page.ancient_dwarven_lexicon");
    public static final String JEI_INFO_HEARTH                  = category(JolCraftLanguageCategory.JEI, "info_page.hearth");
    public static final String JEI_INFO_VERDANT                 = category(JolCraftLanguageCategory.JEI, "info_page.verdant");
    public static final String JEI_INFO_MUSHROOM                = category(JolCraftLanguageCategory.JEI, "info_page.mushroom");
    public static final String JEI_INFO_FESTERLING              = category(JolCraftLanguageCategory.JEI, "info_page.festerling");

    // ---------------------------------------------------------------------
    // MiscLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_HOLD_KEY = category(JolCraftLanguageCategory.TOOLTIP, "hold_key");
    public static final String TOOLTIP_DEV_KEY = category(JolCraftLanguageCategory.TOOLTIP, "dev_key");

    public static final String TOOLTIP_QUILL_EMPTY = category(JolCraftLanguageCategory.TOOLTIP, "quill_empty");
    public static final String TOOLTIP_QUILL = category(JolCraftLanguageCategory.TOOLTIP, "quill");
    public static final String TOOLTIP_QUILL_FULL = category(JolCraftLanguageCategory.TOOLTIP, "quill_full");

    public static final String TOOLTIP_VANILLA_CROP = category(JolCraftLanguageCategory.TOOLTIP, "vanilla_crop");
    public static final String TOOLTIP_HOPS_SEED = category(JolCraftLanguageCategory.TOOLTIP, "hops_seed");
    public static final String TOOLTIP_DEEPSLATE_BULBS = category(JolCraftLanguageCategory.TOOLTIP, "deepslate_bulbs");

    public static final String TOOLTIP_MALT = category(JolCraftLanguageCategory.TOOLTIP, "malt");
    public static final String TOOLTIP_HOPS = category(JolCraftLanguageCategory.TOOLTIP, "hops");
    public static final String TOOLTIP_YEAST = category(JolCraftLanguageCategory.TOOLTIP, "yeast");
    public static final String TOOLTIP_GLASS_MUG = category(JolCraftLanguageCategory.TOOLTIP, "glass_mug");

    public static final String TOOLTIP_SPANNER = category(JolCraftLanguageCategory.TOOLTIP, "spanner");
    public static final String TOOLTIP_SALVAGEABLE = category(JolCraftLanguageCategory.TOOLTIP, "salvageable");
    public static final String TOOLTIP_SALVAGE = category(JolCraftLanguageCategory.TOOLTIP, "salvage");

    // ---------------------------------------------------------------------
    // ReputationLangSubProvider
    // ---------------------------------------------------------------------

    public static final String REPUTATION_TIER_0 = mod("reputation_tier.0");
    public static final String REPUTATION_TIER_1 = mod("reputation_tier.1");
    public static final String REPUTATION_TIER_2 = mod("reputation_tier.2");
    public static final String REPUTATION_TIER_3 = mod("reputation_tier.3");
    public static final String REPUTATION_TIER_4 = mod("reputation_tier.4");

    public static final String TOOLTIP_REPUTATION_LOCKED = tooltip(JolCraftKeyParts.REPUTATION, "locked");
    public static final String TOOLTIP_REPUTATION_MAX_TIER = tooltip(JolCraftKeyParts.REPUTATION, "max_tier");
    public static final String TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS = tooltip(JolCraftKeyParts.REPUTATION, "not_enough_endorsements");
    public static final String TOOLTIP_REPUTATION_NEVER_ENDORSE = tooltip(JolCraftKeyParts.REPUTATION, "never_endorse");
    public static final String TOOLTIP_REPUTATION_CANNOT_ENDORSE = tooltip(JolCraftKeyParts.REPUTATION, "cannot_endorse");
    public static final String TOOLTIP_REPUTATION_ALREADY_ENDORSED = tooltip(JolCraftKeyParts.REPUTATION, "already_endorsed");
    public static final String TOOLTIP_REPUTATION_WRONG_TABLET = tooltip(JolCraftKeyParts.REPUTATION, "wrong_tablet");
    public static final String TOOLTIP_REPUTATION_LEVEL_UP = tooltip(JolCraftKeyParts.REPUTATION, "level_up");

    public static final String TOOLTIP_TABLET_OWNER = tooltip(JolCraftKeyParts.TABLET, "owner");
    public static final String TOOLTIP_TABLET_REPUTATION = tooltip(JolCraftKeyParts.TABLET, "tier");
    public static final String TOOLTIP_TABLET_ENDORSEMENTS = tooltip(JolCraftKeyParts.TABLET, "endorsements");
    public static final String TOOLTIP_TABLET_PROGRESS = tooltip(JolCraftKeyParts.TABLET, "progress");
    public static final String TOOLTIP_TABLET_PROGRESS_PREFIX = tooltip(JolCraftKeyParts.TABLET, "progress.prefix");
    public static final String TOOLTIP_TABLET_ENDORSEMENTS_INFO = tooltip(JolCraftKeyParts.TABLET, "endorsements_info");
    public static final String TOOLTIP_TABLET_ADVANCE_INFO = tooltip(JolCraftKeyParts.TABLET, "advance_info");

    // ---------------------------------------------------------------------
    // TrimLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_TRIM_MATERIALS = category(JolCraftLanguageCategory.TOOLTIP, "trim_material");
    public static final String TOOLTIP_ATTRIBUTE_TRIM_MATERIALS = category(JolCraftLanguageCategory.TOOLTIP, "attribute_trim_material");
}
