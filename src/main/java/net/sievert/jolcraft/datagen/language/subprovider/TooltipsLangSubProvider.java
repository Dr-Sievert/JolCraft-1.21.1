package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class TooltipsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        general(p);
        itemsAndBlocks(p);
        languageAndTomes(p);
        bountyAndCrates(p);
        reputation(p);
        fermentingCauldron(p);
        compass(p);
        strongboxAndLocks(p);
        hearth(p);
    }

    private enum TooltipCategory {
        ROOT(""),
        LANGUAGE("language"),
        TOME_UNLOCK("tome_unlock"),
        BOUNTY("bounty"),
        BOUNTY_CRATE("bounty_crate"),
        CRATE("crate"),
        REPUTATION("reputation"),
        TABLET("tablet"),
        FERMENTING_CAULDRON("fermenting_cauldron"),
        HOPS("hops"),
        DEEPSLATE_COMPASS("deepslate_compass"),
        DEEPSLATE_COMPASS_DIAL("deepslate_compass_dial"),
        STRUCTURE("structure"),
        STRONGBOX("strongbox"),
        HEARTH("hearth"),
        CHISEL("chisel"),
        LAPIDARY_BENCH("lapidary_bench");

        private final String id;

        TooltipCategory(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    private static String t(TooltipCategory category, String path) {
        String cat = category.id();
        return (cat == null || cat.isEmpty())
                ? JolCraftLanguageKeys.category("tooltip", path)
                : JolCraftLanguageKeys.tooltip(cat, path);
    }

    private static void general(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.ROOT, "hold_key"), "Hold %s for more info");
        p.putManual(t(TooltipCategory.ROOT, "dev_key"), "Used for testing/creative mode.");
    }

    private static void itemsAndBlocks(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.ROOT, "quill_empty"), "Can be filled with ink sacs or by right-clicking a squid.");
        p.putManual(t(TooltipCategory.ROOT, "quill"), "Used for writing on paper. Can be filled by right-clicking a squid.");
        p.putManual(t(TooltipCategory.ROOT, "quill_full"), "Used for writing on paper.");

        p.putManual(t(TooltipCategory.ROOT, "vanilla_crop"), "Grows like vanilla crops.");
        p.putManual(t(TooltipCategory.ROOT, "hops_seed"), "Needs two blocks height and a light level of 8 or less to grow.");
        p.putManual(t(TooltipCategory.ROOT, "deepslate_bulbs"), "Needs a light level of 8 or less and a y-level of 0 or less to grow. Can only be planted on Deepslate, Tuff or Verdant Soil.");

        p.putManual(t(TooltipCategory.ROOT, "malt"), "Can be used on a water cauldron as a first step in brewing.");
        p.putManual(t(TooltipCategory.ROOT, "hops"), "Can be used on a cauldron with malt to add effects to a brew.");
        p.putManual(t(TooltipCategory.ROOT, "yeast"), "Can be used on a fermenting cauldron with malt/hops to start the brewing process. Created by using sugar on a water cauldron and extracted using glass bottles.");
        p.putManual(t(TooltipCategory.ROOT, "glass_mug"), "Can be used to extract a finished dwarven brew from a cauldron.");

        p.putManual(t(TooltipCategory.ROOT, "spanner"), "Can be used to produce scrap from salvage. Hold the spanner in one hand and salvage in the other, then right click!");
        p.putManual(t(TooltipCategory.ROOT, "geode"), "Can be broken into dust using an artisan hammer at a lapidary bench.");
        p.putManual(t(TooltipCategory.ROOT, "uncut_gem"), "Can be broken into dust using an artisan hammer or cut using a chisel at a lapidary bench.");
        p.putManual(t(TooltipCategory.ROOT, "artisan_hammer"), "Can be used to break geodes and gems at a lapidary bench.");
        p.putManual(t(TooltipCategory.ROOT, "cut_gem"), "Can be used to trim armor for bonus stats. Applying additional cosmetic trims does not override given stats.");
        p.putManual(t(TooltipCategory.ROOT, "chisel"), "Can be used to cut gems at a lapidary bench.");
        p.putManual(t(TooltipCategory.CHISEL, "cut_locked"), "You have not learned how to cut gems!");
    }

    private static void languageAndTomes(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.ROOT, "need_lang"), "You need to understand dwarvish to use this.");
        p.putManual(t(TooltipCategory.ROOT, "need_ancient"), "You need to understand ancient dwarvish to use this.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_memory"), "Ancient memory effect gives you temporary understanding of ancient dwarvish.");

        p.putManual(t(TooltipCategory.LANGUAGE, "locked"), "You do not understand each other.");

        p.putManual(t(TooltipCategory.ROOT, "dwarven_lexicon.locked"), "The pages are filled with unfamiliar symbols.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_lexicon.unlocked"), "The key to dwarven speech lies within.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_lexicon.use"), "You have learned to understand the dwarven language!");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_lexicon.knows"), "You already understand the dwarven language.");

        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_lexicon.locked"), "The pages are filled with unfamiliar symbols.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_lexicon.unlocked"), "What was once silent may now speak again.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_lexicon.use"), "You have learned to understand the ancient dwarven language!");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_lexicon.cant_read"), "You have no idea how to decipher this.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_lexicon.cant_use"), "The text is clearly dwarvish, but you cannot decipher its secrets.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_lexicon.knows"), "You already understand the ancient dwarven language.");

        p.putManual(t(TooltipCategory.ROOT, "unidentified"), "Right-click to identify.");
        p.putManual(t(TooltipCategory.ROOT, "unidentified_dwarven_tome"), "An unidentified dwarven tome.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_tome.shift"), "Can be sold to Dwarven Historians.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_tome.unidentified"), "An unidentified dwarven tome, written in ancient dwarvish.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_tome.partial_understanding"), "You recognize the language as Dwarvish, but cannot understand it.");
        p.putManual(t(TooltipCategory.ROOT, "legendary_ancient_dwarven_tome.shift"), "Can be used to gain permanent knowledge.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_tome.identify_success"), "You identify the contents of the tome.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_tome.identify_fail"), "You cannot make sense of the dwarven runes.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_tome.locked"), "The pages are filled with unfamiliar symbols.");
        p.putManual(t(TooltipCategory.ROOT, "dwarven_tome.unlocked"), "A dwarven tome.");
        p.putManual(t(TooltipCategory.ROOT, "ancient_dwarven_tome.unlocked"), "An ancient dwarven tome.");
        p.putManual(t(TooltipCategory.ROOT, "legendary_page"), "Salvaged from ancient tomes by historians. Can be used to restore ancient legendary tomes by certain dwarven professions.");

        p.putManual(t(TooltipCategory.TOME_UNLOCK, "empty"), "This tome lacks knowledge that you find useful.");
        p.putManual(t(TooltipCategory.TOME_UNLOCK, "brew"), "You can now brew with multiple ingredients!");
        p.putManual(t(TooltipCategory.TOME_UNLOCK, "gems"), "You can now cut gems using a chisel!");
        p.putManual(t(TooltipCategory.LAPIDARY_BENCH, "locked_cut_gems"), "You have no idea how to cut this gem without breaking it.");

        p.putManual(t(TooltipCategory.ROOT, "paper.locked"), "The paper is marked with unfamiliar symbols.");
        p.putManual(t(TooltipCategory.ROOT, "tablet.locked"), "The stone is marked with unfamiliar symbols.");
        p.putManual(t(TooltipCategory.ROOT, "bounty.locked"), "The parchment is marked with unfamiliar symbols.");
    }

    private static void bountyAndCrates(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.ROOT, "bounty_crate"), "Needs to be filled and handed in for a reward. Right-click when held to fill from inventory. Left-click in inventory with required item to fill or right-click to insert single item. Right-click in inventory to extract items.");

        p.putManual(t(TooltipCategory.BOUNTY, "tier.invalid"), "Tier: Invalid");
        p.putManual(t(TooltipCategory.BOUNTY, "tier"), "Tier: %d");
        p.putManual(t(TooltipCategory.BOUNTY, "type.invalid"), "Type: Invalid");
        p.putManual(t(TooltipCategory.BOUNTY, "type"), "Type: ");
        p.putManual(t(TooltipCategory.BOUNTY, "wrong_type"), "This is the wrong type of bounty to give to this dwarf.");
        p.putManual(t(TooltipCategory.BOUNTY, "no_type"), "Give this to a dwarf to get a bounty crate.");
        p.putManual(t(TooltipCategory.BOUNTY, "merchant"), "Give this to a merchant to get a bounty crate.");
        p.putManual(t(TooltipCategory.BOUNTY, "miner"), "Give this to a miner to get a bounty crate.");

        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "target"), "Target: ");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "count"), "Required: %s");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "tier"), "Tier: %s");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "invalid"), "No bounty data");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "locked"), "The crate is marked with unfamiliar symbols.");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "filled"), "The crate is already full.");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "filled_some"), "Added %s items to the crate.");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "no_items"), "You don't have any items to fill the crate.");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "complete"), "Ready to be turned in.");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "not_complete"), "This bounty has not been completed.");
        p.putManual(t(TooltipCategory.BOUNTY_CRATE, "wrong_type"), "This is the wrong type of crate to give to this dwarf.");

        p.putManual(t(TooltipCategory.CRATE, "cooldown"), "You must wait before you can use another crate.");
        p.putManual(t(TooltipCategory.CRATE, "no_offers_villager"), "This villager has no trades!");
        p.putManual(t(TooltipCategory.CRATE, "no_offers_dwarf"), "This dwarf has no trades!");

        p.putManual(t(TooltipCategory.ROOT, "restock_crate"), "Can be used to restock the inventory of a dwarf or villager trader.");
        p.putManual(t(TooltipCategory.ROOT, "restock_crate.no_need"), "This trader doesn't need restocking.");
        p.putManual(t(TooltipCategory.ROOT, "restock_crate.success"), "Trader inventory restocked!");

        p.putManual(t(TooltipCategory.ROOT, "reroll_crate"), "Can be used to reroll the inventory of a dwarf or villager trader.");
        p.putManual(t(TooltipCategory.ROOT, "reroll_crate.fail"), "This trader inventory cannot be rerolled!");
        p.putManual(t(TooltipCategory.ROOT, "reroll_crate.success"), "Trader inventory rerolled!");
    }

    private static void reputation(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.ROOT, "rep_owner"), "Granted to: %s");
        p.putManual(t(TooltipCategory.ROOT, "reputation_tier"), "Reputation: ");
        p.putManual(t(TooltipCategory.ROOT, "endorsement_count"), "Endorsements: %s");
        p.putManual(t(TooltipCategory.REPUTATION, "locked"), "You need a higher reputation for this.");
        p.putManual(t(TooltipCategory.REPUTATION, "max_tier"), "You are already at the highest reputation tier!");
        p.putManual(t(TooltipCategory.REPUTATION, "not_enough_endorsements"), "You need %1$d endorsements to advance (you have %2$d).");
        p.putManual(t(TooltipCategory.REPUTATION, "never_endorse"), "This dwarf does not give endorsements.");
        p.putManual(t(TooltipCategory.REPUTATION, "cannot_endorse"), "This dwarf is not ready to give you an endorsement.");
        p.putManual(t(TooltipCategory.REPUTATION, "already_endorsed"), "You already have this endorsement.");
        p.putManual(t(TooltipCategory.REPUTATION, "wrong_tablet"), "You must present the correct reputation tablet.");
        p.putManual(t(TooltipCategory.REPUTATION, "level_up"), "You have advanced in dwarven reputation!");
        p.putManual(t(TooltipCategory.TABLET, "progress"), "Endorsements for reputation advancement: %s/%s");
        p.putManual(t(TooltipCategory.TABLET, "progress.prefix"), "Endorsements for reputation advancement: ");
        p.putManual(t(TooltipCategory.TABLET, "endorsements_info"), "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. Endorsements are unique per profession and can only be gained once.");
        p.putManual(t(TooltipCategory.TABLET, "advance_info"), "To advance in reputation level you need endorsements from dwarves with professions. When you have enough, hand over your tablet to a guildmaster to update it.");
        p.putManual(t(TooltipCategory.ROOT, "guard.promotion"), "Guard promoted to %s!");
    }

    private static void fermentingCauldron(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.FERMENTING_CAULDRON, "ingredient_max"), "You already added the max amount of this ingredient to the brew.");
        p.putManual(t(TooltipCategory.FERMENTING_CAULDRON, "locked_multi"), "Adding more ingredients without proper knowledge would ruin the brew.");

        p.putManual(t(TooltipCategory.HOPS, "asgarnian"), "Asgarnian");
        p.putManual(t(TooltipCategory.HOPS, "duskhold"), "Duskhold");
        p.putManual(t(TooltipCategory.HOPS, "krandonian"), "Krandonian");
        p.putManual(t(TooltipCategory.HOPS, "yanillian"), "Yanillian");
    }

    private static void compass(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.DEEPSLATE_COMPASS_DIAL, "unknown"), "Unknown");
        p.putManual(t(TooltipCategory.DEEPSLATE_COMPASS_DIAL, "dwarven_structures"), "Dwarven Structures");
        p.putManual(t(TooltipCategory.DEEPSLATE_COMPASS_DIAL, "ancient_structures"), "Ancient Structures");
        p.putManual(t(TooltipCategory.DEEPSLATE_COMPASS, "no_structure"), "No structures found!");
        p.putManual(t(TooltipCategory.DEEPSLATE_COMPASS, "locate"), "The tracked %s is at %s (%s blocks away)");
        p.putManual(t(TooltipCategory.ROOT, "deepslate_compass"), "Currently tracking: ");

        p.putManual(t(TooltipCategory.STRUCTURE, "unknown"), "Unknown");
        p.putManual(t(TooltipCategory.STRUCTURE, "jolcraft:forge"), "Dwarven Forge");
        p.putManual(t(TooltipCategory.STRUCTURE, "jolcraft:dwarven_trail_ruin"), "Dwarven Trail Ruin");
        p.putManual(t(TooltipCategory.STRUCTURE, "minecraft:ancient_city"), "Ancient City");
        p.putManual(t(TooltipCategory.STRUCTURE, "minecraft:trail_ruins"), "Trail Ruins");
        p.putManual(t(TooltipCategory.STRUCTURE, "discovered"), "Discovered: ");
    }

    private static void strongboxAndLocks(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.ROOT, "lockpick"), "Used to pick locks. Will break on failure. Lockpicking is easier when using potions.");
        p.putManual(t(TooltipCategory.STRONGBOX, "not_empty"), "This strongbox has items inside.");
        p.putManual(t(TooltipCategory.STRONGBOX, "loot"), "This strongbox has loot inside.");
        p.putManual(t(TooltipCategory.STRONGBOX, "set_locked"), "You have locked this strongbox.");
        p.putManual(t(TooltipCategory.STRONGBOX, "set_unlocked"), "You have unlocked this strongbox.");
        p.putManual(t(TooltipCategory.STRONGBOX, "locked"), "This strongbox is locked.");
        p.putManual(t(TooltipCategory.STRONGBOX, "busy"), "Someone else is trying to pick this lock.");
    }

    private static void hearth(AbstractLanguageProvider p) {
        p.putManual(t(TooltipCategory.HEARTH, "cooldown"), "You must rest before light a hearth.");
        p.putManual(t(TooltipCategory.HEARTH, "need_coal"), "You need coal to light this.");
        p.putManual(t(TooltipCategory.HEARTH, "not_safe"), "Cannot light with monsters nearby!");
        p.putManual(t(TooltipCategory.HEARTH, "no_bed_nearby"), "No claimed bed nearby.");
    }
}