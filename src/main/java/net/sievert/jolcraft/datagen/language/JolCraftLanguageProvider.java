package net.sievert.jolcraft.datagen.language;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.datagen.advancement.AdvancementKey;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.potion.JolCraftPotions;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class JolCraftLanguageProvider extends LanguageProvider {
    public JolCraftLanguageProvider(PackOutput output) {
        super(output, "jolcraft", "en_us");
    }

    private final Set<String> addedKeys = new HashSet<>();


    @Override
    protected void addTranslations() {

        //Items

        //Manual entries
        addManual(JolCraftItems.YEAST, "Brewing Yeast");
        addManual(JolCraftItems.QUILL_FULL, "Ink and Quill");
        addManual(JolCraftItems.QUILL_HALF, "Ink and Quill");
        addManual(JolCraftItems.QUILL_SMALL, "Ink and Quill");
        addManual(JolCraftItems.QUILL_EMPTY, "Empty Ink and Quill");
        addManual(JolCraftItems.REPUTATION_TABLET_0, "Reputation Tablet");
        addManual(JolCraftItems.REPUTATION_TABLET_1, "Reputation Tablet");
        addManual(JolCraftItems.REPUTATION_TABLET_2, "Reputation Tablet");
        addManual(JolCraftItems.REPUTATION_TABLET_3, "Reputation Tablet");
        addManual(JolCraftItems.REPUTATION_TABLET_4, "Reputation Tablet");
        addManual(JolCraftItems.DWARVEN_TOME_COMMON, "Dwarven Tome");
        addManual(JolCraftItems.DWARVEN_TOME_UNCOMMON, "Dwarven Tome");
        addManual(JolCraftItems.DWARVEN_TOME_RARE, "Dwarven Tome");
        addManual(JolCraftItems.DWARVEN_TOME_EPIC, "Dwarven Tome");
        addManual(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON, "Ancient Dwarven Tome");
        addManual(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON, "Ancient Dwarven Tome");
        addManual(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE, "Ancient Dwarven Tome");
        addManual(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC, "Ancient Dwarven Tome");
        addManual(JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME, "Ancient Unidentified Dwarven Tome");
        addManual(JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME, "Ancient Unidentified Dwarven Tome");
        addManual(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY, "Ancient Dwarven Tome");
        addManual(JolCraftItems.SCRAP_HEAP, "Heap of Scrap");
        addManual(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE, "Forge Armor Trim");

        //Simple flipped entries
        addManualFlipped(JolCraftItems.CONTRACT_BLANK);
        addManualFlipped(JolCraftItems.CONTRACT_WRITTEN);
        addManualFlipped(JolCraftItems.CONTRACT_SIGNED);
        addManualFlipped(JolCraftItems.CONTRACT_GUILDMASTER);
        addManualFlipped(JolCraftItems.CONTRACT_HISTORIAN);
        addManualFlipped(JolCraftItems.CONTRACT_MERCHANT);
        addManualFlipped(JolCraftItems.CONTRACT_SCRAPPER);
        addManualFlipped(JolCraftItems.CONTRACT_BREWMASTER);
        addManualFlipped(JolCraftItems.CONTRACT_GUARD);
        addManualFlipped(JolCraftItems.CONTRACT_KEEPER);
        addManualFlipped(JolCraftItems.CONTRACT_ARTISAN);
        addManualFlipped(JolCraftItems.CONTRACT_EXPLORER);
        addManualFlipped(JolCraftItems.CONTRACT_MINER);
        addManualFlipped(JolCraftItems.CONTRACT_ALCHEMIST);
        addManualFlipped(JolCraftItems.CONTRACT_ARCANIST);
        addManualFlipped(JolCraftItems.CONTRACT_PRIEST);
        addManualFlipped(JolCraftItems.CONTRACT_BLACKSMITH);
        addManualFlipped(JolCraftItems.CONTRACT_CHAMPION);
        addManualFlipped(JolCraftItems.CONTRACT_SMELTER);
        addManualFlipped(JolCraftItems.GEODE_SMALL);
        addManualFlipped(JolCraftItems.GEODE_MEDIUM);
        addManualFlipped(JolCraftItems.GEODE_LARGE);
        addManualFlipped(JolCraftItems.AEGISCORE_CUT);
        addManualFlipped(JolCraftItems.ASHFANG_CUT);
        addManualFlipped(JolCraftItems.DEEPMARROW_CUT);
        addManualFlipped(JolCraftItems.EARTHBLOOD_CUT);
        addManualFlipped(JolCraftItems.EMBERGLASS_CUT);
        addManualFlipped(JolCraftItems.FROSTVEIN_CUT);
        addManualFlipped(JolCraftItems.GRIMSTONE_CUT);
        addManualFlipped(JolCraftItems.IRONHEART_CUT);
        addManualFlipped(JolCraftItems.LUMIERE_CUT);
        addManualFlipped(JolCraftItems.MOONSHARD_CUT);
        addManualFlipped(JolCraftItems.RUSTAGATE_CUT);
        addManualFlipped(JolCraftItems.SKYBURROW_CUT);
        addManualFlipped(JolCraftItems.SUNGLEAM_CUT);
        addManualFlipped(JolCraftItems.VERDANITE_CUT);
        addManualFlipped(JolCraftItems.WOECRYSTAL_CUT);

        //Add rest using normal translation convention
        addMissingRegistryTranslations(
                JolCraftItems.class,
                "item",
                addedKeys,
                Item.class,
                BuiltInRegistries.ITEM::getKey
        );


        //Blocks

        //Manual entries
        addManual(JolCraftBlocks.BARLEY_CROP, "Barley Crops");
        addManual(JolCraftBlocks.BARLEY_BLOCK, "Barley Hay Bale");
        addManual(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM, "Asgarnian Hops");
        addManual(JolCraftBlocks.ASGARNIAN_CROP_TOP, "Asgarnian Hops");
        addManual(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM, "Duskhold Hops");
        addManual(JolCraftBlocks.DUSKHOLD_CROP_TOP, "Duskhold Hops");
        addManual(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM, "Krandonian Hops");
        addManual(JolCraftBlocks.KRANDONIAN_CROP_TOP, "Krandonian Hops");
        addManual(JolCraftBlocks.YANILLIAN_CROP_BOTTOM, "Yanillian Hops");
        addManual(JolCraftBlocks.YANILLIAN_CROP_TOP, "Yanillian Hops");
        addManual(JolCraftBlocks.DEEPSLATE_BULBS_CROP, "Deepslate Bulbs");
        addManual(JolCraftBlocks.FESTERLING_CROP, "Cultivated Festerling");
        addManual(JolCraftBlocks.MUFFHORN_FUR_BLOCK, "Muffhorn Fur Bundle");
        addManual(JolCraftBlocks.GEODE_BLOCK, "Basalt Geode Cluster");

        //Add rest using normal translation convention
        addMissingRegistryTranslations(
                JolCraftBlocks.class,
                "block",
                addedKeys,
                Block.class,
                BuiltInRegistries.BLOCK::getKey
        );

        //Creative tabs
        addManual("itemGroup.jolcraft.jolcraft_items_tab", "JolCraft");
        addManual("itemGroup.jolcraft.jolcraft_egg_tab", "JolCraft Spawn Eggs");

        //Containers

        add("container.jolcraft.lapidary_bench", "Lapidary Bench");
        add("container.jolcraft.strongbox", "Strongbox");
        add("container.jolcraft.strongbox_locked", "Locked Strongbox");

        //Trim patterns and materials

        add("trim_pattern.jolcraft.forge", "Forge Armor Trim");
        add("trim_material.jolcraft.deepslate", "Deepslate");
        add("trim_material.jolcraft.mithril", "Mithril");
        add("trim_material.jolcraft.aegiscore", "Aegiscore");
        add("trim_material.jolcraft.ashfang", "Ashfang");
        add("trim_material.jolcraft.deepmarrow", "Deepmarrow");
        add("trim_material.jolcraft.earthblood", "Earthblood");
        add("trim_material.jolcraft.emberglass", "Emberglass");
        add("trim_material.jolcraft.frostvein", "Frostvein");
        add("trim_material.jolcraft.grimstone", "Grimstone");
        add("trim_material.jolcraft.ironheart", "Ironheart");
        add("trim_material.jolcraft.lumiere", "Lumiere");
        add("trim_material.jolcraft.moonshard", "Moonshard");
        add("trim_material.jolcraft.rustagate", "Rustagate");
        add("trim_material.jolcraft.skyburrow", "Skyburrow");
        add("trim_material.jolcraft.sungleam", "Sungleam");
        add("trim_material.jolcraft.verdanite", "Verdanite");
        add("trim_material.jolcraft.woecrystal", "Woecrystal");

        //Entities

        add("entity.jolcraft.dwarf", "Dwarf");
        add("entity.jolcraft.dwarf_guildmaster", "Guildmaster");
        add("entity.jolcraft.dwarf_historian", "Historian");
        add("entity.jolcraft.dwarf_merchant", "Merchant");
        add("entity.jolcraft.dwarf_scrapper", "Scrapper");
        add("entity.jolcraft.dwarf_brewmaster", "Brewmaster");
        add("entity.jolcraft.dwarf_guard", "Guard");
        add("entity.jolcraft.dwarf_keeper", "Keeper");
        add("entity.jolcraft.dwarf_artisan", "Artisan");
        add("entity.jolcraft.dwarf_explorer", "Explorer");
        add("entity.jolcraft.dwarf_miner", "Miner");
        add("entity.jolcraft.dwarf_alchemist", "Alchemist");
        add("entity.jolcraft.dwarf_arcanist", "Arcanist");
        add("entity.jolcraft.dwarf_priest", "Priest");

        add("entity.jolcraft.muffhorn", "Muffhorn");
        add("entity.jolcraft.radiant", "Radiant");


        //Attributes

        //Manual entries
        addManual(JolCraftAttributes.XP_BOOST, "Experience Boost");
        addManual(JolCraftAttributes.EXTRA_CROP, "Extra Crop Harvest");
        addManual(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY, "Sunlight Speed Boost");
        addManual(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT, "Moonlight Speed Boost");

        //Add rest using normal translation convention
        addMissingRegistryTranslations(
                JolCraftAttributes.class,
                "attribute",
                addedKeys,
                Attribute.class,
                BuiltInRegistries.ATTRIBUTE::getKey
        );

        //Effects

        //Manual entries
        //addManual("effect.jolcraft.homestead", "Homestead");

        //Add rest using normal translation convention
        addMissingRegistryTranslations(
                JolCraftEffects.class,
                "effect",
                addedKeys,
                MobEffect.class,
                BuiltInRegistries.MOB_EFFECT::getKey
        );

        //Potions

        addPotion(JolCraftPotions.ANCIENT_MEMORY, "Ancient Memory", true, false);
        addPotion(JolCraftPotions.LOCKPICKING, "Lockpicking", true, true);
        addPotion(JolCraftPotions.DWARVEN_HASTE, "Mining", true, true);
        addPotion(JolCraftPotions.CORROSION, "Corrosion", false, false);
        addPotion(JolCraftPotions.DELIRIUM_CURSE, "Delirium Curse", false, false);
        addPotion(JolCraftPotions.CURSED_WOUND, "Cursed Wound", false, false);

        //Tooltips

        add("tooltip.jolcraft.hold_key", "Hold %s for more info");
        add("tooltip.jolcraft.dev_key", "Used for testing/creative mode.");
        add("tooltip.jolcraft.quill_empty", "Can be filled with ink sacs or by right-clicking a squid.");
        add("tooltip.jolcraft.quill", "Used for writing on paper. Can be filled by right-clicking a squid.");
        add("tooltip.jolcraft.quill_full", "Used for writing on paper.");
        add("tooltip.jolcraft.written_contract", "Given to dwarves without professions to get signed contracts. Signed contracts are used to buy profession contracts from a guildmaster. If given to a dwarf with a profession they will create a contract for their profession.");
        add("tooltip.jolcraft.signed_contract", "Signed contracts are used to buy profession contracts from a guildmaster.");
        add("tooltip.jolcraft.profession_contract", "Profession contracts can be given to dwarves without professions to set their profession.");
        add("tooltip.jolcraft.guild_sigil", "Can be bought from a master dwarf without a profession.");
        add("tooltip.jolcraft.legendary_page", "Salvaged from ancient tomes by historians. Can be used to restore ancient legendary tomes by certain dwarven professions.");
        add("tooltip.jolcraft.trim_material", "Can be used to trim armor.");
        add("tooltip.jolcraft.vanilla_crop", "Grows like vanilla crops.");
        add("tooltip.jolcraft.hops_seed", "Needs two blocks height and a light level of 8 or less to grow.");
        add("tooltip.jolcraft.deepslate_bulbs", "Needs a light level of 8 or less and a y-level of 0 or less to grow. Can only be planted on Deepslate, Tuff or Verdant Soil.");
        add("tooltip.jolcraft.malt", "Can be used on a water cauldron as a first step in brewing.");
        add("tooltip.jolcraft.hops", "Can be used on a cauldron with malt to add effects to a brew.");
        add("tooltip.jolcraft.yeast", "Can be used on a fermenting cauldron with malt/hops to start the brewing process. Created by using sugar on a water cauldron and extracted using glass bottles.");
        add("tooltip.jolcraft.glass_mug", "Can be used to extract a finished dwarven brew from a cauldron.");
        add("tooltip.jolcraft.spanner", "Can be used to produce scrap from salvage. Hold the spanner in one hand and salvage in the other, then right click!");
        add("tooltip.jolcraft.geode", "Can be broken into dust using an artisan hammer at a lapidary bench.");
        add("tooltip.jolcraft.uncut_gem", "Can be broken into dust using an artisan hammer or cut using a chisel at a lapidary bench.");
        add("tooltip.jolcraft.artisan_hammer", "Can be used to break geodes and gems at a lapidary bench.");
        add("tooltip.jolcraft.cut_gem", "Can be used to trim armor for bonus stats. Applying additional cosmetic trims does not override given stats.");
        add("tooltip.jolcraft.chisel", "Can be used to cut gems at a lapidary bench.");
        add("tooltip.jolcraft.chisel.cut_locked", "You have not learned how to cut gems!");
        add("tooltip.jolcraft.need_lang", "You need to understand dwarvish to use this.");
        add("tooltip.jolcraft.need_ancient", "You need to understand ancient dwarvish to use this.");
        add("tooltip.jolcraft.ancient_memory", "Ancient memory effect gives you temporary understanding of ancient dwarvish.");
        add("tooltip.jolcraft.unidentified", "Right-click to identify.");
        add("tooltip.jolcraft.bounty_crate", "Needs to be filled and handed in for a reward. Right-click when held to fill from inventory. Left-click in inventory with required item to fill or right-click to insert single item. Right-click in inventory to extract items.");
        add("tooltip.jolcraft.language.locked", "You do not understand each other.");
        add("tooltip.jolcraft.dwarf.busy", "This dwarf is currently busy.");
        add("tooltip.jolcraft.dwarf.not_paid", "You should pay the dwarf first.");
        add("tooltip.jolcraft.dwarf.cannot_sign", "This dwarf cannot sign contracts.");
        add("tooltip.jolcraft.dwarf.cannot_promote", "This dwarf cannot accept a new contract.");
        add("tooltip.jolcraft.dwarven_lexicon.locked", "The pages are filled with unfamiliar symbols.");
        add("tooltip.jolcraft.dwarven_lexicon.unlocked", "The key to dwarven speech lies within.");
        add("tooltip.jolcraft.dwarven_lexicon.use", "You have learned to understand the dwarven language!");
        add("tooltip.jolcraft.dwarven_lexicon.knows", "You already understand the dwarven language.");
        add("tooltip.jolcraft.ancient_dwarven_lexicon.locked", "The pages are filled with unfamiliar symbols.");
        add("tooltip.jolcraft.ancient_dwarven_lexicon.unlocked", "What was once silent may now speak again.");
        add("tooltip.jolcraft.ancient_dwarven_lexicon.use", "You have learned to understand the ancient dwarven language!");
        add("tooltip.jolcraft.ancient_dwarven_lexicon.cant_read", "You have no idea how to decipher this.");
        add("tooltip.jolcraft.ancient_dwarven_lexicon.cant_use", "The textKey is clearly dwarvish, but you cannot decipher its secrets.");
        add("tooltip.jolcraft.ancient_dwarven_lexicon.knows", "You already understand the ancient dwarven language.");
        add("tooltip.jolcraft.unidentified_dwarven_tome", "An unidentified dwarven tome.");
        add("tooltip.jolcraft.dwarven_tome.shift", "Can be sold to Dwarven Historians.");
        add("tooltip.jolcraft.ancient_dwarven_tome.unidentified", "An unidentified dwarven tome, written in ancient dwarvish.");
        add("tooltip.jolcraft.ancient_dwarven_tome.partial_understanding", "You recognize the language as Dwarvish, but cannot understand it.");
        add("tooltip.jolcraft.legendary_ancient_dwarven_tome.shift", "Can be used to gain permanent knowledge.");
        add("tooltip.jolcraft.dwarven_tome.identify_success", "You identify the contents of the tome.");
        add("tooltip.jolcraft.dwarven_tome.identify_fail", "You cannot make sense of the dwarven runes.");
        add("tooltip.jolcraft.dwarven_tome.locked", "The pages are filled with unfamiliar symbols.");
        add("tooltip.jolcraft.dwarven_tome.unlocked", "A dwarven tome.");
        add("tooltip.jolcraft.ancient_dwarven_tome.unlocked", "An ancient dwarven tome.");
        add("tooltip.jolcraft.deepslate_compass_dial.unknown", "Unknown");
        add("tooltip.jolcraft.deepslate_compass_dial.dwarven_structures", "Dwarven Structures");
        add("tooltip.jolcraft.deepslate_compass_dial.ancient_structures", "Ancient Structures");
        add("tooltip.jolcraft.deepslate_compass.no_structure", "No structures found!");
        add("tooltip.jolcraft.deepslate_compass.locate", "The tracked %s is at %s (%s blocks away)");
        add("tooltip.jolcraft.deepslate_compass", "Currently tracking: ");
        add("tooltip.jolcraft.structure.unknown", "Unknown");
        add("tooltip.jolcraft.structure.jolcraft:forge", "Dwarven Forge");
        add("tooltip.jolcraft.structure.jolcraft:dwarven_trail_ruin", "Dwarven Trail Ruin");
        add("tooltip.jolcraft.structure.minecraft:ancient_city", "Ancient City");
        add("tooltip.jolcraft.structure.minecraft:trail_ruins", "Trail Ruins");
        add("tooltip.jolcraft.structure.discovered", "Discovered: ");
        add("tooltip.jolcraft.tome_unlock.empty", "This tome lacks knowledge that you find useful.");
        add("tooltip.jolcraft.tome_unlock.brew", "You can now brew with multiple ingredients!");
        add("tooltip.jolcraft.tome_unlock.gems", "You can now cut gems using a chisel!");
        add("tooltip.jolcraft.lapidary_bench.locked_cut_gems", "You have no idea how to cut this gem without breaking it.");
        add("tooltip.jolcraft.paper.locked", "The paper is marked with unfamiliar symbols.");
        add("tooltip.jolcraft.lockpick", "Used to pick locks. Will break on failure. Lockpicking is easier when using potions.");
        add("tooltip.jolcraft.strongbox.not_empty", "This strongbox has items inside.");
        add("tooltip.jolcraft.strongbox.loot", "This strongbox has loot inside.");
        add("tooltip.jolcraft.strongbox.set_locked", "You have locked this strongbox.");
        add("tooltip.jolcraft.strongbox.set_unlocked", "You have unlocked this strongbox.");
        add("tooltip.jolcraft.strongbox.locked", "This strongbox is locked.");
        add("tooltip.jolcraft.strongbox.busy", "Someone else is trying to pick this lock.");
        add("tooltip.jolcraft.bounty.locked", "The parchment is marked with unfamiliar symbols.");
        add("tooltip.jolcraft.bounty.tier.invalid", "Tier: Invalid");
        add("tooltip.jolcraft.bounty.tier", "Tier: %d");
        add("tooltip.jolcraft.bounty.type.invalid", "Type: Invalid");
        add("tooltip.jolcraft.bounty.type", "Type: ");
        add("tooltip.jolcraft.bounty.wrong_type", "This is the wrong type of bounty to give to this dwarf.");
        add("tooltip.jolcraft.bounty.no_type", "Give this to a dwarf to get a bounty crate.");
        add("tooltip.jolcraft.bounty.merchant", "Give this to a merchant to get a bounty crate.");
        add("tooltip.jolcraft.bounty.miner", "Give this to a miner to get a bounty crate.");
        add("tooltip.jolcraft.bounty_crate.target", "Target: ");
        add("tooltip.jolcraft.bounty_crate.count", "Required: %s");
        add("tooltip.jolcraft.bounty_crate.tier", "Tier: %s");
        add("tooltip.jolcraft.bounty_crate.invalid", "No bounty data");
        add("tooltip.jolcraft.bounty_crate.locked", "The crate is marked with unfamiliar symbols.");
        add("tooltip.jolcraft.bounty_crate.filled", "The crate is already full.");
        add("tooltip.jolcraft.bounty_crate.filled_some", "Added %s items to the crate.");
        add("tooltip.jolcraft.bounty_crate.no_items", "You don't have any items to fill the crate.");
        add("tooltip.jolcraft.bounty_crate.complete", "Ready to be turned in.");
        add("tooltip.jolcraft.bounty_crate.not_complete", "This bounty has not been completed.");
        add("tooltip.jolcraft.bounty_crate.wrong_type", "This is the wrong type of crate to give to this dwarf.");
        add("tooltip.jolcraft.crate.cooldown", "You must wait before you can use another crate.");
        add("tooltip.jolcraft.crate.no_offers_villager", "This villager has no trades!");
        add("tooltip.jolcraft.crate.no_offers_dwarf", "This dwarf has no trades!");
        add("tooltip.jolcraft.restock_crate", "Can be used to restock the inventory of a dwarf or villager trader.");
        add("tooltip.jolcraft.restock_crate.no_need", "This trader doesn't need restocking.");
        add("tooltip.jolcraft.restock_crate.success", "Trader inventory restocked!");
        add("tooltip.jolcraft.reroll_crate", "Can be used to reroll the inventory of a dwarf or villager trader.");
        add("tooltip.jolcraft.reroll_crate.fail", "This trader inventory cannot be rerolled!");
        add("tooltip.jolcraft.reroll_crate.success", "Trader inventory rerolled!");
        add("tooltip.jolcraft.salvage_tag", "Salvagable");
        add("tooltip.jolcraft.salvage", "Can be used to produce scrap using a spanner. Hold the spanner in one hand and the salvage in the other, then right click!");
        add("tooltip.jolcraft.rep_owner", "Granted to: %s");
        add("tooltip.jolcraft.reputation_tier", "Reputation: ");
        add("tooltip.jolcraft.endorsement_count", "Endorsements: %s");
        add("tooltip.jolcraft.reputation.locked", "You need a higher reputation for this.");
        add("tooltip.jolcraft.reputation.max_tier", "You are already at the highest reputation tier!");
        add("tooltip.jolcraft.reputation.not_enough_endorsements", "You need %1$d endorsements to advance (you have %2$d).");
        add("tooltip.jolcraft.reputation.never_endorse", "This dwarf does not give endorsements.");
        add("tooltip.jolcraft.reputation.cannot_endorse", "This dwarf is not ready to give you an endorsement.");
        add("tooltip.jolcraft.reputation.already_endorsed", "You already have this endorsement.");
        add("tooltip.jolcraft.reputation.wrong_tablet", "You must present the correct reputation tablet.");
        add("tooltip.jolcraft.reputation.level_up", "You have advanced in dwarven reputation!");
        add("tooltip.jolcraft.tablet.locked", "The stone is marked with unfamiliar symbols.");
        add("tooltip.jolcraft.tablet.progress", "Endorsements for reputation advancement: %s/%s");
        add("tooltip.jolcraft.tablet.progress.prefix", "Endorsements for reputation advancement: ");
        add("tooltip.jolcraft.tablet.endorsements_info", "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. Endorsements are unique per profession and can only be gained once.");
        add("tooltip.jolcraft.tablet.advance_info", "To advance in reputation level you need endorsements from dwarves with professions. When you have enough, hand over your tablet to a guildmaster to update it.");
        add("tooltip.jolcraft.fermenting_cauldron.ingredient_max", "You already added the max amount of this ingredient to the brew.");
        add("tooltip.jolcraft.fermenting_cauldron.locked_multi", "Adding more ingredients without proper knowledge would ruin the brew.");
        add("tooltip.jolcraft.hops.asgarnian", "Asgarnian");
        add("tooltip.jolcraft.hops.duskhold", "Duskhold");
        add("tooltip.jolcraft.hops.krandonian", "Krandonian");
        add("tooltip.jolcraft.hops.yanillian", "Yanillian");
        add("tooltip.jolcraft.guard.promotion", "Guard promoted to %s!");
        add("tooltip.jolcraft.hearth.cooldown", "You must rest before light a hearth.");
        add("tooltip.jolcraft.hearth.need_coal", "You need coal to light this.");
        add("tooltip.jolcraft.hearth.not_safe", "Cannot light with monsters nearby!");
        add("tooltip.jolcraft.hearth.no_bed_nearby", "No claimed bed nearby.");

        //Lore

        // MODERN

        addLoreEntry(DwarfLoreKey.TUNNEL_STABILITY, "A Survey of Tunnel Stability in Soft Granite, Volume II");
        addLoreEntry(DwarfLoreKey.BARREL_SEALING, "Proper Barrel Sealing Techniques, Volume I");
        addLoreEntry(DwarfLoreKey.TURNIP_YIELDS, "A Record of Turnip Yields, Year 538");
        addLoreEntry(DwarfLoreKey.BEARD_GROOMING, "The Art of Beard Grooming: A Beginner's Guide, 4th Edition");
        addLoreEntry(DwarfLoreKey.MINECART_WHEELS, "Catalog of Minecart Wheel Failures, Volume VII");
        addLoreEntry(DwarfLoreKey.FURNACE_TEMPERATURES, "Furnace Temperatures and You, Revised 987");
        addLoreEntry(DwarfLoreKey.PIPEWORKS_KARRAM_DUN, "The Pipeworks of Lower Karram-Dûn, Year 1112");
        addLoreEntry(DwarfLoreKey.FORGE_ETIQUETTE, "Basic Forge Etiquette for Apprentices, Volume IV");
        addLoreEntry(DwarfLoreKey.LEDGERS, "Ledgers and Ledgers: On the Keeping of Ledgers, Volume IX");
        addLoreEntry(DwarfLoreKey.FUNGUS_UPPER_CAVERNS, "Common Fungus of the Upper Caverns, Survey of 1014");
        addLoreEntry(DwarfLoreKey.ECHO_PATTERNS, "Observations on Echo Patterns in Vaulted Halls, Volume II");

        addLoreEntry(DwarfLoreKey.CHISELED_DEEPSLATE, "Properties of Chiseled Deepslate, Volume VI");
        addLoreEntry(DwarfLoreKey.FORGE_MARKS, "Ancestral Forge Marks and Their Variants, Volume III");
        addLoreEntry(DwarfLoreKey.AQUEDUCT_COLLAPSE, "Survey of the Northern Aqueduct Collapse, Year 1198");
        addLoreEntry(DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE, "Gem Vein Activity by Lunar Cycle, Volume VIII");
        addLoreEntry(DwarfLoreKey.MOLD_ID_CONTAINMENT, "Subterranean Mold: Identification & Containment, Year 1243");
        addLoreEntry(DwarfLoreKey.WHISPERS_OLD_PILLARS, "Whispers Among the Old Pillars, Volume V");
        addLoreEntry(DwarfLoreKey.QUEEN_HRAGA, "A Disputed Account of Forge-Queen Hraga's Reign, Volume I");
        addLoreEntry(DwarfLoreKey.UNSPOKEN_TUNNELS, "The Unspoken Tunnels: A Guard Captain’s Memoir, Year 982");
        addLoreEntry(DwarfLoreKey.HALL_LANTERNS, "Inventory of the Hall of Lanterns, Year 1024");
        addLoreEntry(DwarfLoreKey.RITUAL_BEARD_OIL, "On the Use of Beard Oil in Ritual Contexts, Volume X");
        addLoreEntry(DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, "Chronology of Roof Collapses in Irondeep Sector, Volume IX");

        addLoreEntry(DwarfLoreKey.ECHO_CARTOGRAPHY, "Echo-Chamber Cartography: The First Attempts, Year 1251");
        addLoreEntry(DwarfLoreKey.GEMLINES_BEARERS, "The Fifteen Gemlines and Their Bearers, Volume I");
        addLoreEntry(DwarfLoreKey.STONEGUARD_PROTOCOLS, "Stoneguard Protocols for Deep Siege Defense, Year 1066");
        addLoreEntry(DwarfLoreKey.ARCANIST_BINDING_RITUALS, "Rituals of Binding: Arcanist Practices, Volume XIII");
        addLoreEntry(DwarfLoreKey.MITHRIL_FORGING, "On the Forging of Mithril Alloy, Year 987");
        addLoreEntry(DwarfLoreKey.CONTRACT_SEALS, "Contract Seals and Binding Ink Formulas, Volume XI");
        addLoreEntry(DwarfLoreKey.EMBERGLASS_FIRES, "Mysteries of the Emberglass Furnace-Fires, Year 1187");
        addLoreEntry(DwarfLoreKey.DEEPMARROW_SIGILS, "Ancestral Sigils of the Deepmarrow Keepers, Volume XII");
        addLoreEntry(DwarfLoreKey.CHAOS_DWARVES_WARNING, "Chaos Dwarves: A Warning to the Forgeborn, Year 1293");
        addLoreEntry(DwarfLoreKey.WOECRYSTAL_RUNES, "Runes of Woecrystal and Their Applications, Volume XVI");
        addLoreEntry(DwarfLoreKey.LOST_CARAVANS, "Ledger of Lost Caravans, Volume VI");

        addLoreEntry(DwarfLoreKey.BREWERIES_STEWS, "Warden-Blessed Breweries and Sacred Stews, Volume XIV");
        addLoreEntry(DwarfLoreKey.STATUE_SPIRIT_BINDING, "Spirit-Binding Rites for Guardian Statues, Year 1010");
        addLoreEntry(DwarfLoreKey.FURNACE_EXPERIMENTS, "Experimental Furnace Designs, Year 1303");
        addLoreEntry(DwarfLoreKey.SECRET_TRADE_ROUTES, "Secret Trade Routes of the Westward Expansion, Year 1027");
        addLoreEntry(DwarfLoreKey.GIANT_SPORE_BLOOMS, "Giant Spore Blooms of the Deeps, Volume V");
        addLoreEntry(DwarfLoreKey.THE_LAST_BALROG, "The Last Balrog Sighting, Year 1387");
        addLoreEntry(DwarfLoreKey.DEEPFIRE_BALROG, "Chronicle of the Deepfire Balrog");
        addLoreEntry(DwarfLoreKey.MAGMA_WRITINGS, "Writings from the Magma Archives, Volume XIX");
        addLoreEntry(DwarfLoreKey.STORMCARVED_LEDGE, "Chronicle of the Stormcarved Ledge, Year 1502");
        addLoreEntry(DwarfLoreKey.ANCIENT_TOMB_KEYS, "Keys of the Ancient Tombs, Volume X");
        addLoreEntry(DwarfLoreKey.STARFALL_LEDGER, "Ledger of the Starfall Years, Volume XVII");

        // ANCIENT

        addLoreEntry(DwarfLoreKey.KEYSTONE_SHAPES, "On the Shapes and Placement of Keystones, Age of Foundations");
        addLoreEntry(DwarfLoreKey.CISTERN_SEALS, "Inspections of Cistern Seals, Year 132");
        addLoreEntry(DwarfLoreKey.BREW_YIELDS, "Brew Yields and Yeast Logs, Year 91");
        addLoreEntry(DwarfLoreKey.BEARD_OILS, "Beard Oil Recipes for the Elder Kin, Volume III");
        addLoreEntry(DwarfLoreKey.PIPE_ASSEMBLY, "Pipe Assembly Diagrams of the Early Guild, Volume I");
        addLoreEntry(DwarfLoreKey.ANVIL_WEAR, "Patterns of Anvil Wear and Maintenance, First Forgemasters");
        addLoreEntry(DwarfLoreKey.DOWSING_METHODS, "Practical Dowsing Methods for Water and Ore, Second Era");
        addLoreEntry(DwarfLoreKey.HALL_GREETINGS, "Proper Greetings in the Great Halls, Year 59");
        addLoreEntry(DwarfLoreKey.LEDGER_FORMATS, "Accepted Formats for Stone Ledger Tablets, Early Record-Keepers");
        addLoreEntry(DwarfLoreKey.FUNGAL_COLONY_NOTES, "Notes on the Great Fungal Colony Collapse, Age of Growth");
        addLoreEntry(DwarfLoreKey.SEATING_CHART, "Seating Charts for Guild Banquets, Old Calendar");

        addLoreEntry(DwarfLoreKey.SPARE_KEYS, "The Making and Keeping of Spare Keys, Generation I");
        addLoreEntry(DwarfLoreKey.RUNE_ACCOUNTING, "Rune-Tallies for Trade Accounting, Year 287");
        addLoreEntry(DwarfLoreKey.CISTERN_CLEANING, "Cistern Cleaning Practices, Generation IV");
        addLoreEntry(DwarfLoreKey.STARSTONE_RUMORS, "Rumors of Starstones Falling, Night of Terrors");
        addLoreEntry(DwarfLoreKey.DUST_CONTROL, "Sweeping Schedules for Dust Control, Early Quarters");
        addLoreEntry(DwarfLoreKey.LANTERN_MAINTENANCE, "Daily Maintenance of Oil Lanterns, Year 60");
        addLoreEntry(DwarfLoreKey.CHAMPION_OATHS, "The Oaths of the First Champions, Battleborn Era");
        addLoreEntry(DwarfLoreKey.RAT_WARNINGS, "Old Rat Infestation Warnings, Year 22");
        addLoreEntry(DwarfLoreKey.BEDROLL_RULES, "Bedroll Placement Rules for Shared Chambers, Founders’ Years");
        addLoreEntry(DwarfLoreKey.ROOT_PRESERVES, "Recipes for Preserving Roots and Tubers, First Preservers");
        addLoreEntry(DwarfLoreKey.LOST_TOOLS, "The Lost Tools Ledger, Age of Loss");

        addLoreEntry(DwarfLoreKey.STONEGUARD_SEATING, "Seating Charts for Stoneguard Banquets, Early Stoneguard");
        addLoreEntry(DwarfLoreKey.ANIMAL_TOKENS, "Tokens Used in Early Kinship Rituals, Rituals Volume I");
        addLoreEntry(DwarfLoreKey.STONEGUARD_PACT, "The Stoneguard Pact, Pact Year");
        addLoreEntry(DwarfLoreKey.RUNE_LOCK_DIAGRAMS, "Diagrams of Rune-Locked Chests, Keymasters’ Era");
        addLoreEntry(DwarfLoreKey.FORGE_OF_MITHRIL, "The Forging of Mithril, Old Metallurgists");
        addLoreEntry(DwarfLoreKey.CONTRACT_SIGNATURES, "Ledger of Old Contract Signatures, Scribe’s Year");
        addLoreEntry(DwarfLoreKey.EMBERGLASS_FORGE_LOGS, "Emberglass Forge Logs, First Era");
        addLoreEntry(DwarfLoreKey.MEMORY_SHARD_DISCOVERY, "Discovery of the Memory Shards, Year 7");
        addLoreEntry(DwarfLoreKey.EXILE_RECORDS, "Records of Exiles and Outcasts, Outcast Scrolls");
        addLoreEntry(DwarfLoreKey.SPIRIT_ENCOUNTER, "An Early Encounter with a Dwarven Spirit, Lost Era");
        addLoreEntry(DwarfLoreKey.SEALED_VAULTS, "Account of the Sealed Vaults of Hraga, Vault Year");

        addLoreEntry(DwarfLoreKey.ROOT_STORAGE, "On the Storage of Root Vegetables in Deep Cellars, First Storage");
        addLoreEntry(DwarfLoreKey.DAWN_HALL_RELICS, "Relics of the Dawn Hall, Dawn Era");
        addLoreEntry(DwarfLoreKey.PRIMEVAL_IRONWORKS, "Primeval Ironworks of the Deep, Era of Makers");
        addLoreEntry(DwarfLoreKey.STARFORGED_HELM, "Discovery of the Starforged Helm, Night of Comets");
        addLoreEntry(DwarfLoreKey.FIRST_EMBERGLASS, "The First Emberglass Crucible, Age of Flames");
        addLoreEntry(DwarfLoreKey.BINDING_OF_THE_BALROG, "The Binding of the Deepfire Balrog");
        addLoreEntry(DwarfLoreKey.ETERNAL_EMBER, "Eternal Ember of the Ancients, Cycle XX");
        addLoreEntry(DwarfLoreKey.ORACLE_INSCRIPTIONS, "Oracle Inscriptions of the Crystal Vault, Volume VII");
        addLoreEntry(DwarfLoreKey.DEEP_CURSE_TABLET, "Tablet of the Deep Curse, Age of Shadows");
        addLoreEntry(DwarfLoreKey.SUNKEN_FORGE_RITES, "Rites of the Sunken Forge, Lost Age");
        addLoreEntry(DwarfLoreKey.CAVERN_LIGHT_CHRONICLE, "Chronicle of Cavern Light, Dawn Cycle");

        addLoreEntry(DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE, "Mithril Forging Technique, Forge of the First Flame");
        addLoreEntry(DwarfLoreKey.ANCIENT_GEMCRAFT, "The Art of Gemcutting, Archives of Karaz-Un");
        addLoreEntry(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS, "Formulas of Dwarven Brews, Vaults of Stonehearth");
        addLoreEntry(DwarfLoreKey.COIN_PRESS_MANUAL, "Coin Press Manual, Bank of Barak-Zul");
        addLoreEntry(DwarfLoreKey.ALCHEMY_RECIPES, "Codex Alchemica, Transcribed by the Final Thaumaturge");

        add("jolcraft.reputation_tier.0", "Stranger");
        add("jolcraft.reputation_tier.1", "Known Face");
        add("jolcraft.reputation_tier.2", "Trusted");
        add("jolcraft.reputation_tier.3", "Respected");
        add("jolcraft.reputation_tier.4", "Blood-Kin");

        addAdvancement(AdvancementKey.ROOT,
                "The Dwarven Path",
                "A journey through dwarven halls"
        );

        addAdvancement(AdvancementKey.READ_LEXICON,
                "Bilingual",
                "Learn to understand the dwarven language"
        );

        addAdvancement(AdvancementKey.REP_0_DUMMY,
                "What now?",
                "You are a stranger to most dwarves. You need to earn their trust."
        );

        addAdvancement(AdvancementKey.TRADE_DWARF,
                "Dwarven Commerce",
                "Trade with a dwarf"
        );

        addAdvancement(AdvancementKey.TRADE_HISTORIAN,
                "Curious Curator",
                "Trade with a historian"
        );

        addAdvancement(AdvancementKey.ENDORSE_HISTORIAN,
                "Footnote in History",
                "Get endorsed by a master historian"
        );

        addAdvancement(AdvancementKey.TRADE_MERCHANT,
                "Assorted Goods",
                "Trade with a merchant"
        );

        addAdvancement(AdvancementKey.ENDORSE_MERCHANT,
                "Distinguished Fetcher",
                "Get endorsed by a master merchant"
        );

        addAdvancement(AdvancementKey.TRADE_SCRAPPER,
                "First Salvage",
                "Trade with a scrapper"
        );

        addAdvancement(AdvancementKey.ENDORSE_SCRAPPER,
                "Certified Scavenger",
                "Get endorsed by a master scrapper"
        );

        addAdvancement(AdvancementKey.REP_1,
                "Known Face",
                "Reach this rank in reputation"
        );

        addAdvancement(AdvancementKey.REP_1_DUMMY,
                "New Faces",
                "More dwarves are now willing to interact with you."
        );

        addAdvancement(AdvancementKey.TRADE_BREWMASTER,
                "Toasting Traditions",
                "Trade with a brewmaster"
        );

        addAdvancement(AdvancementKey.ENDORSE_BREWMASTER,
                "Honored in Hops",
                "Get endorsed by a master brewmaster"
        );

        addAdvancement(AdvancementKey.TRADE_GUARD,
                "Shield and Service",
                "Trade with a guard"
        );

        addAdvancement(AdvancementKey.ENDORSE_GUARD,
                "Writ of Protection",
                "Get endorsed by a master guard"
        );

        addAdvancement(AdvancementKey.TRADE_KEEPER,
                "Horns and Harvest",
                "Trade with a keeper"
        );

        addAdvancement(AdvancementKey.ENDORSE_KEEPER,
                "Caretaker's Mark",
                "Get endorsed by a master keeper"
        );

        addAdvancement(AdvancementKey.REP_2,
                "Trusted",
                "Reach this rank in reputation"
        );

        addAdvancement(AdvancementKey.REP_2_DUMMY,
                "Gaining Ground",
                "Your deeds echo through the halls. More dwarves are willing to work with you."
        );

        addAdvancement(AdvancementKey.TRADE_ARTISAN,
                "Facets of Trade",
                "Trade with an artisan"
        );

        addAdvancement(AdvancementKey.ENDORSE_ARTISAN,
                "Signature Cut",
                "Get endorsed by a master artisan"
        );

        addAdvancement(AdvancementKey.TRADE_EXPLORER,
                "Wayfarer’s Bargain",
                "Trade with an explorer"
        );

        addAdvancement(AdvancementKey.ENDORSE_EXPLORER,
                "Surveyor’s Seal",
                "Get endorsed by a master explorer"
        );

        addAdvancement(AdvancementKey.TRADE_MINER,
                "Digging Deals",
                "Trade with a miner"
        );

        addAdvancement(AdvancementKey.ENDORSE_MINER,
                "Deep Delver",
                "Get endorsed by a master miner"
        );

        addAdvancement(AdvancementKey.REP_3,
                "Respected",
                "Reach this rank in reputation"
        );

        addAdvancement(AdvancementKey.REP_3_DUMMY,
                "Resounding Renown",
                "Your reputation now precedes you in every corner of the dwarven realm."
        );

        addAdvancement(AdvancementKey.TRADE_ALCHEMIST,
                "Bubbling Business",
                "Trade with an alchemist"
        );

        addAdvancement(AdvancementKey.ENDORSE_ALCHEMIST,
                "Formula of Fame",
                "Get endorsed by a master alchemist"
        );

        addAdvancement(AdvancementKey.TRADE_ARCANIST,
                "Arcane Accord",
                "Trade with an arcanist"
        );

        addAdvancement(AdvancementKey.ENDORSE_ARCANIST,
                "Mystic Mark",
                "Get endorsed by a master arcanist"
        );

        addAdvancement(AdvancementKey.TRADE_PRIEST,
                "Rites and Rituals",
                "Trade with a priest"
        );

        addAdvancement(AdvancementKey.ENDORSE_PRIEST,
                "Divine Approval",
                "Get endorsed by a master priest"
        );

        addAdvancement(AdvancementKey.REP_4,
                "Blood-Kin",
                "Reach this rank in reputation"
        );

        addAdvancement(AdvancementKey.REP_4_DUMMY,
                "Legacy Forged",
                "You are celebrated as family by any dwarf."
        );

        add("stat.jolcraft.structures_discovered", "Structures Discovered");

        add("subtitles.jolcraft.entity.dwarf_ambient", "Dwarf mumbles");
        add("subtitles.jolcraft.entity.dwarf_hit", "Dwarf hurts");
        add("subtitles.jolcraft.entity.dwarf_death", "Dwarf dies");
        add("subtitles.jolcraft.entity.dwarf_yes", "Dwarf agrees");
        add("subtitles.jolcraft.entity.dwarf_no", "Dwarf disagrees");
        add("subtitles.jolcraft.entity.dwarf_trade", "Dwarf haggles");

        add("filled_map.forge", "Map to a Dwarven Forge");

        add("jei.jolcraft.dwarf_trades", "Dwarf Trades");

        add("jei.jolcraft.info_page", "Information");

        add("jei.jolcraft.info_page.reputation_tablet", "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. Endorsements are unique per profession and can only be gained once. To advance in reputation level you need endorsements from dwarves with professions. When you have enough, hand over your tablet to a guildmaster to update it.");
        add("jei.jolcraft.info_page.strongbox", "Strongboxes are chests that can generate with locks that needs to be picked. Breaking a locked strongbox also breaks the lock but removes any ungenerated loot. Breaking a strongbox with silk touch retains all of its contents.");
        add("jei.jolcraft.info_page.deepslate_compass", "Empty deepslate compass needs to be combined with a deepslate compass dial. Hold the empty compass and the dial one in each hand then right-click to combine them. Dials are sold by explorers or found as loot. Combined compass points to a structure from a pool based on the dial in the compass. Compass can be dyed multiple times to blend color. Shapeless craft empty compass to to remove dye and combined compass to remove the dial.");
        add("jei.jolcraft.info_page.coin_pouch", "Stores up to 999 coins. Can be used in trades. Right-click when held to deposit all coins in inventory. Left-click with coins in inventory to deposit up to a stack. Right-click in inventory to withdraw up to a stack.");
        add("jei.jolcraft.info_page.dwarven_lexicon", "Can be used to learn dwarvish. Found in mineshafts or stronghold libraries. Can be bought from master librarians.");
        add("jei.jolcraft.info_page.ancient_dwarven_lexicon", "Can be used to learn ancient dwarvish. Found in ancient dwarven ruins.");
        add("jei.jolcraft.info_page.hearth", "Can be activated if placed within 10 blocks of a claimed bed. Need to sleep in a bed to activate a hearth again. Multiple players can be bound to a hearth. Provides Homestead effect to bound players within range.");
        add("jei.jolcraft.info_page.verdant", "JolCraft crops ignores growing conditions and also grow faster on these. Other crops can be planted but no extra effects.");
        add("jei.jolcraft.info_page.mushroom", "Planted and spreads like vanilla mushrooms. No giant variant.");
        add("jei.jolcraft.info_page.festerling", "Can be cultivated with rotten flesh. Planted on top of log ends. Spreads like vanilla mushrooms. No giant variant.");
    }

    //Helpers

    private void addManual(Object thing, String value) {
        String key = resolveKey(thing);
        add(key, value);
        addedKeys.add(key);
    }

    private void addManualFlipped(Object thing) {
        String key = resolveKey(thing);
        String path = key.substring(key.lastIndexOf('.') + 1);
        String value = flipAndTitle(path);
        addManual(key, value);
    }

    private String resolveKey(Object thing) {
        if (thing instanceof String str) return str;

        if (thing instanceof DeferredHolder<?, ?> deferred) {
            var resourceKey = deferred.getKey();
            return resourceKey.registry().getPath() + "." +
                    resourceKey.location().getNamespace() + "." +
                    resourceKey.location().getPath();
        }

        try {
            var getMethod = thing.getClass().getMethod("get");
            Object actual = getMethod.invoke(thing);
            if (actual != null && actual != thing) return resolveKey(actual);
        } catch (Exception ignored) {}

        if (thing instanceof Item item) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            return "item." + id.getNamespace() + "." + id.getPath();
        }

        throw new IllegalArgumentException("Unsupported or unregistered object: " + thing + " (class: " + thing.getClass() + ")");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String toTitleCase(String path) {
        String[] words = path.split("_");
        StringBuilder result = new StringBuilder();
        for (String w : words) if (!w.isEmpty()) result.append(capitalize(w)).append(" ");
        return result.toString().trim();
    }

    public static String flipAndTitle(String path) {
        String[] words = path.split("_");
        if (words.length == 2)
            return capitalize(words[1]) + " " + capitalize(words[0]);
        return toTitleCase(path);
    }

    public <T> void addMissingRegistryTranslations(
            Class<?> registryClass,
            String type,
            Set<String> addedKeys,
            Class<T> entryType,
            Function<T, ResourceLocation> idGetter
    ) {
        for (Field field : registryClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object obj = field.get(null);

                if (obj != null && obj.getClass().getSimpleName().startsWith("Deferred")) {
                    try {
                        obj = obj.getClass().getMethod("get").invoke(obj);
                    } catch (Exception ignored) {
                        // Not a Deferred type, use raw value
                    }
                }

                if (!entryType.isInstance(obj)) continue;

                T entry = entryType.cast(obj);
                ResourceLocation id = idGetter.apply(entry);
                if (id == null) continue;

                String key = type + "." + id.getNamespace() + "." + id.getPath();
                if (addedKeys.contains(key)) {
                    continue;
                }

                String autoName = toTitleCase(id.getPath());
                add(key, autoName);
                addedKeys.add(key);
            } catch (Exception ignored) {}
        }
    }

    private void addPotion(Object potionHolder, String displayName, boolean hasLong, boolean hasStrong) {
        String baseName = resolvePotionName(potionHolder);

        add("item.minecraft.potion.effect." + baseName, displayName + " Potion");
        add("item.minecraft.splash_potion.effect." + baseName, displayName + " Splash Potion");
        add("item.minecraft.lingering_potion.effect." + baseName, displayName + " Lingering Potion");
        add("item.minecraft.tipped_arrow.effect." + baseName, "Arrow of " + displayName);

        if (hasLong) {
            add("item.minecraft.potion.effect.long_" + baseName, displayName + " Potion");
            add("item.minecraft.splash_potion.effect.long_" + baseName, displayName + " Splash Potion");
            add("item.minecraft.lingering_potion.effect.long_" + baseName, displayName + " Lingering Potion");
            add("item.minecraft.tipped_arrow.effect.long_" + baseName, "Arrow of " + displayName);
        }
        if (hasStrong) {
            add("item.minecraft.potion.effect.strong_" + baseName, displayName + " Potion");
            add("item.minecraft.splash_potion.effect.strong_" + baseName, displayName + " Splash Potion");
            add("item.minecraft.lingering_potion.effect.strong_" + baseName, displayName + " Lingering Potion");
            add("item.minecraft.tipped_arrow.effect.strong_" + baseName, "Arrow of " + displayName);
        }
    }

    private String resolvePotionName(Object potionHolder) {
        if (potionHolder instanceof Holder.Reference<?> ref) {
            ResourceKey<?> key = ref.unwrapKey().orElse(null);
            if (key != null)
                return key.location().getPath();
        }
        if (potionHolder instanceof net.neoforged.neoforge.registries.DeferredHolder<?,?> deferred) {
            return deferred.getId().getPath();
        }
        try {
            var getMethod = potionHolder.getClass().getMethod("get");
            Object actual = getMethod.invoke(potionHolder);
            if (actual != null && actual != potionHolder) {
                return resolvePotionName(actual);
            }
        } catch (Exception ignored) {}
        if (potionHolder instanceof String str) return str;
        throw new IllegalArgumentException("Can't resolve potion name for " + potionHolder);
    }

    public <K extends Enum<K>> void addLoreEntry(K key, String text) {
        String translationKey = LoreHelper.getEntryTranslationKey(key);
        addManual(translationKey, text);
    }

    private void addAdvancement(AdvancementKey key, String title, String description) {
        String base = "advancement.jolcraft." + key.id();
        addManual(base + ".title", title);
        addManual(base + ".description", description);
    }

}
