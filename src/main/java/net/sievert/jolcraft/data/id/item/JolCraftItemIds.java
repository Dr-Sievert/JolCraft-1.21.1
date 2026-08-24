package net.sievert.jolcraft.data.id.item;

import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachment;
import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.entity.creature.JolCraftCreatureIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftItemIds extends JolCraftIds {

    private JolCraftItemIds() {}

    // ---------------------------------------------------------------------
    // Core Items
    // ---------------------------------------------------------------------

    public static final String DEV_KEY = join(JolCraftDictionary.DEV, JolCraftDictionary.KEY);
    public static final String GOLD_COIN = join(JolCraftDictionary.GOLD, JolCraftDictionary.COIN);
    public static final String COIN_POUCH = join(JolCraftDictionary.COIN, JolCraftDictionary.POUCH);

    public static final String DWARVEN_LEXICON = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.LEXICON);
    public static final String ANCIENT_DWARVEN_LEXICON = join(JolCraftDictionary.ANCIENT, JolCraftDictionary.DWARVEN, JolCraftDictionary.LEXICON);

    public static final String STRONGBOX = JolCraftBlockIds.STRONGBOX;

    public static final String LOCKPICK = JolCraftDictionary.LOCKPICK;

    public static final String DEEPSLATE_COMPASS = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.COMPASS);
    public static final String EMPTY_DEEPSLATE_COMPASS = join(JolCraftDictionary.EMPTY, DEEPSLATE_COMPASS);
    public static final String DEEPSLATE_COMPASS_DIAL = join(DEEPSLATE_COMPASS, JolCraftDictionary.DIAL);
    public static final String DIAL_DUST = join(JolCraftDictionary.DIAL, JolCraftDictionary.DUST);

    public static final String WAR_HORN = join(JolCraftDictionary.WAR, JolCraftDictionary.HORN);

    // ---------------------------------------------------------------------
    // Materials / Armors / Tools
    // ---------------------------------------------------------------------

    public static final String IMPURE_MITHRIL = join(JolCraftDictionary.IMPURE, JolCraftDictionary.MITHRIL);
    public static final String PURE_MITHRIL = join(JolCraftDictionary.PURE, JolCraftDictionary.MITHRIL);
    public static final String MITHRIL_INGOT = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.INGOT);
    public static final String MITHRIL_NUGGET = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.NUGGET);
    public static final String MITHRIL_CHAINWEAVE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.CHAINWEAVE);

    public static final String MITHRIL_SWORD = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.SWORD);
    public static final String MITHRIL_WARHAMMER = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.WARHAMMER);
    public static final String MITHRIL_PICKAXE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.PICKAXE);
    public static final String MITHRIL_SHOVEL = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.SHOVEL);
    public static final String MITHRIL_AXE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.AXE);
    public static final String MITHRIL_HOE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.HOE);

    public static final String MITHRIL_HELMET = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.HELMET);
    public static final String MITHRIL_CHESTPLATE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.CHESTPLATE);
    public static final String MITHRIL_LEGGINGS = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.LEGGINGS);
    public static final String MITHRIL_BOOTS = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.BOOTS);

    public static final String DEEPSLATE_PLATE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.PLATE);
    public static final String DEEPSLATE_ROD = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.ROD);

    public static final String DEEPSLATE_SWORD = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.SWORD);
    public static final String DEEPSLATE_WARHAMMER = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.WARHAMMER);
    public static final String DEEPSLATE_PICKAXE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.PICKAXE);
    public static final String DEEPSLATE_SHOVEL = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.SHOVEL);
    public static final String DEEPSLATE_AXE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.AXE);
    public static final String DEEPSLATE_HOE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.HOE);

    public static final String DEEPSLATE_HELMET = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.HELMET);
    public static final String DEEPSLATE_CHESTPLATE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.CHESTPLATE);
    public static final String DEEPSLATE_LEGGINGS = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.LEGGINGS);
    public static final String DEEPSLATE_BOOTS = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.BOOTS);

    public static final String FORGE_ARMOR_TRIM_SMITHING_TEMPLATE = join(
            JolCraftTrimIds.FORGE,
            JolCraftDictionary.ARMOR,
            JolCraftDictionary.TRIM,
            JolCraftDictionary.SMITHING,
            JolCraftDictionary.TEMPLATE
    );

    // ---------------------------------------------------------------------
    // Custom Tools
    // ---------------------------------------------------------------------
    public static final String WOODEN_ARTISAN_HAMMER = join(JolCraftDictionary.WOODEN, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String STONE_ARTISAN_HAMMER = join(JolCraftDictionary.STONE, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String IRON_ARTISAN_HAMMER = join(JolCraftDictionary.IRON, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String GOLDEN_ARTISAN_HAMMER = join(JolCraftDictionary.GOLDEN, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String DIAMOND_ARTISAN_HAMMER = join(JolCraftDictionary.DIAMOND, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String NETHERITE_ARTISAN_HAMMER = join(JolCraftDictionary.NETHERITE, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String DEEPSLATE_ARTISAN_HAMMER = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);
    public static final String MITHRIL_ARTISAN_HAMMER = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER);

    public static final String WOODEN_CHISEL = join(JolCraftDictionary.WOODEN, JolCraftDictionary.CHISEL);
    public static final String STONE_CHISEL = join(JolCraftDictionary.STONE, JolCraftDictionary.CHISEL);
    public static final String IRON_CHISEL = join(JolCraftDictionary.IRON, JolCraftDictionary.CHISEL);
    public static final String GOLDEN_CHISEL = join(JolCraftDictionary.GOLDEN, JolCraftDictionary.CHISEL);
    public static final String DIAMOND_CHISEL = join(JolCraftDictionary.DIAMOND, JolCraftDictionary.CHISEL);
    public static final String NETHERITE_CHISEL = join(JolCraftDictionary.NETHERITE, JolCraftDictionary.CHISEL);
    public static final String DEEPSLATE_CHISEL = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.CHISEL);
    public static final String MITHRIL_CHISEL = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.CHISEL);

    public static final String WOODEN_SPANNER = join(JolCraftDictionary.WOODEN, JolCraftDictionary.SPANNER);
    public static final String STONE_SPANNER = join(JolCraftDictionary.STONE, JolCraftDictionary.SPANNER);
    public static final String IRON_SPANNER = join(JolCraftDictionary.IRON, JolCraftDictionary.SPANNER);
    public static final String GOLDEN_SPANNER = join(JolCraftDictionary.GOLDEN, JolCraftDictionary.SPANNER);
    public static final String DIAMOND_SPANNER = join(JolCraftDictionary.DIAMOND, JolCraftDictionary.SPANNER);
    public static final String NETHERITE_SPANNER = join(JolCraftDictionary.NETHERITE, JolCraftDictionary.SPANNER);
    public static final String DEEPSLATE_SPANNER = join(JolCraftMaterialIds.DEEPSLATE, JolCraftDictionary.SPANNER);
    public static final String MITHRIL_SPANNER = join(JolCraftMaterialIds.MITHRIL, JolCraftDictionary.SPANNER);

    public static final String WOODEN_PESTLE = join(JolCraftDictionary.WOODEN, JolCraftDictionary.PESTLE);
    public static final String STONE_PESTLE = join(JolCraftDictionary.STONE, JolCraftDictionary.PESTLE);
    public static final String IRON_PESTLE = join(JolCraftDictionary.IRON, JolCraftDictionary.PESTLE);
    public static final String GOLDEN_PESTLE = join(JolCraftDictionary.GOLDEN, JolCraftDictionary.PESTLE);
    public static final String DIAMOND_PESTLE = join(JolCraftDictionary.DIAMOND, JolCraftDictionary.PESTLE);
    public static final String NETHERITE_PESTLE = join(JolCraftDictionary.NETHERITE, JolCraftDictionary.PESTLE);
    public static final String DEEPSLATE_PESTLE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.PESTLE);
    public static final String MITHRIL_PESTLE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.PESTLE);

    // ---------------------------------------------------------------------
    // Animal-related
    // ---------------------------------------------------------------------

    public static final String MUFFHORN_FUR = join(JolCraftCreatureIds.MUFFHORN, JolCraftDictionary.FUR);
    public static final String MUFFHORN_MILK_BUCKET = join(JolCraftCreatureIds.MUFFHORN, JolCraftDictionary.MILK, JolCraftDictionary.BUCKET);

    // ---------------------------------------------------------------------
    // Alchemy
    // ---------------------------------------------------------------------

    public static final String DEEPSLATE_MORTAR = JolCraftBlockIds.DEEPSLATE_MORTAR;
    public static final String VITRIOL = JolCraftDictionary.VITRIOL;
    public static final String INVERIX = JolCraftDictionary.INVERIX;

    // ---------------------------------------------------------------------
    // Bounty
    // ---------------------------------------------------------------------

    public static final String PARCHMENT = JolCraftDictionary.PARCHMENT;
    public static final String BOUNTY = JolCraftDictionary.BOUNTY;
    public static final String BOUNTY_CRATE = join(BOUNTY, JolCraftDictionary.CRATE);
    public static final String RESTOCK_CRATE = join(JolCraftDictionary.RESTOCK, JolCraftDictionary.CRATE);
    public static final String REROLL_CRATE = join(JolCraftDictionary.REROLL, JolCraftDictionary.CRATE);
    public static final String REWARD_CRATE = join(JolCraftDictionary.REWARD, JolCraftDictionary.CRATE);

    // ---------------------------------------------------------------------
    // Contracts
    // ---------------------------------------------------------------------

    public static final String CONTRACT_BLANK = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.BLANK);
    public static final String CONTRACT_WRITTEN = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.WRITTEN);
    public static final String CONTRACT_SIGNED = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.SIGNED);
    public static final String GUILD_SIGIL = join(JolCraftDictionary.GUILD, JolCraftDictionary.SIGIL);
    public static final String GUILD_SIGIL_MOULD = join(GUILD_SIGIL, JolCraftDictionary.MOULD);

    public static final String CONTRACT_GUILDMASTER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.GUILDMASTER);

    public static final String CONTRACT_MERCHANT = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.MERCHANT);
    public static final String CONTRACT_HISTORIAN = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.HISTORIAN);
    public static final String CONTRACT_SCRAPPER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.SCRAPPER);

    public static final String CONTRACT_GUARD = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.GUARD);
    public static final String CONTRACT_BREWMASTER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.BREWMASTER);
    public static final String CONTRACT_KEEPER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.KEEPER);

    public static final String CONTRACT_MINER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.MINER);
    public static final String CONTRACT_EXPLORER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.EXPLORER);
    public static final String CONTRACT_ALCHEMIST = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.ALCHEMIST);

    public static final String CONTRACT_ARCANIST = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.ARCANIST);
    public static final String CONTRACT_PRIEST = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.PRIEST);
    public static final String CONTRACT_ARTISAN = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.ARTISAN);

    public static final String CONTRACT_CHAMPION = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.CHAMPION);
    public static final String CONTRACT_BLACKSMITH = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.BLACKSMITH);
    public static final String CONTRACT_SMELTER = join(JolCraftDictionary.CONTRACT, JolCraftDictionary.SMELTER);

    public static final String QUILL_EMPTY = join(JolCraftDictionary.QUILL, JolCraftDictionary.EMPTY);
    public static final String QUILL_SMALL = join(JolCraftDictionary.QUILL, JolCraftDictionary.SMALL);
    public static final String QUILL_HALF = join(JolCraftDictionary.QUILL, JolCraftDictionary.HALF);
    public static final String QUILL_FULL = join(JolCraftDictionary.QUILL, JolCraftDictionary.FULL);

    // ---------------------------------------------------------------------
    // Spawn Eggs
    // ---------------------------------------------------------------------

    public static final String DWARF_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_GUILDMASTER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.GUILDMASTER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_HISTORIAN_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.HISTORIAN, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_MERCHANT_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.MERCHANT, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_SCRAPPER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.SCRAPPER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_BREWMASTER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.BREWMASTER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_GUARD_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.GUARD, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_KEEPER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.KEEPER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_ARTISAN_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.ARTISAN, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_EXPLORER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.EXPLORER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_MINER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.MINER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_ALCHEMIST_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.ALCHEMIST, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_ARCANIST_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.ARCANIST, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_PRIEST_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.PRIEST, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_BLACKSMITH_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.BLACKSMITH, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_CHAMPION_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.CHAMPION, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);
    public static final String DWARF_SMELTER_SPAWN_EGG = join(JolCraftDictionary.DWARF, JolCraftDictionary.SMELTER, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);

    public static final String MUFFHORN_SPAWN_EGG = join(JolCraftCreatureIds.MUFFHORN, JolCraftDictionary.SPAWN, JolCraftDictionary.EGG);

    // ---------------------------------------------------------------------
    // Geodes
    // ---------------------------------------------------------------------

    public static final String GEODE_SMALL = join(JolCraftDictionary.GEODE, JolCraftDictionary.SMALL);
    public static final String GEODE_MEDIUM = join(JolCraftDictionary.GEODE, JolCraftDictionary.MEDIUM);
    public static final String GEODE_LARGE = join(JolCraftDictionary.GEODE, JolCraftDictionary.LARGE);

    // ---------------------------------------------------------------------
    // Gems
    // ---------------------------------------------------------------------

    // Uncut
    public static final String AEGISCORE = JolCraftDictionary.AEGISCORE;
    public static final String ASHFANG = JolCraftDictionary.ASHFANG;
    public static final String DEEPMARROW = JolCraftDictionary.DEEPMARROW;
    public static final String EARTHBLOOD = JolCraftDictionary.EARTHBLOOD;
    public static final String EMBERGLASS = JolCraftDictionary.EMBERGLASS;
    public static final String FROSTVEIN = JolCraftDictionary.FROSTVEIN;
    public static final String GRIMSTONE = JolCraftDictionary.GRIMSTONE;
    public static final String IRONHEART = JolCraftDictionary.IRONHEART;
    public static final String LUMIERE = JolCraftDictionary.LUMIERE;
    public static final String MOONSHARD = JolCraftDictionary.MOONSHARD;
    public static final String RUSTAGATE = JolCraftDictionary.RUSTAGATE;
    public static final String SKYBURROW = JolCraftDictionary.SKYBURROW;
    public static final String SUNGLEAM = JolCraftDictionary.SUNGLEAM;
    public static final String VERDANITE = JolCraftDictionary.VERDANITE;
    public static final String WOECRYSTAL = JolCraftDictionary.WOECRYSTAL;

    // Dust
    public static final String AEGISCORE_DUST = join(AEGISCORE, JolCraftDictionary.DUST);
    public static final String ASHFANG_DUST = join(ASHFANG, JolCraftDictionary.DUST);
    public static final String DEEPMARROW_DUST = join(DEEPMARROW, JolCraftDictionary.DUST);
    public static final String EARTHBLOOD_DUST = join(EARTHBLOOD, JolCraftDictionary.DUST);
    public static final String EMBERGLASS_DUST = join(EMBERGLASS, JolCraftDictionary.DUST);
    public static final String FROSTVEIN_DUST = join(FROSTVEIN, JolCraftDictionary.DUST);
    public static final String GRIMSTONE_DUST = join(GRIMSTONE, JolCraftDictionary.DUST);
    public static final String IRONHEART_DUST = join(IRONHEART, JolCraftDictionary.DUST);
    public static final String LUMIERE_DUST = join(LUMIERE, JolCraftDictionary.DUST);
    public static final String MOONSHARD_DUST = join(MOONSHARD, JolCraftDictionary.DUST);
    public static final String RUSTAGATE_DUST = join(RUSTAGATE, JolCraftDictionary.DUST);
    public static final String SKYBURROW_DUST = join(SKYBURROW, JolCraftDictionary.DUST);
    public static final String SUNGLEAM_DUST = join(SUNGLEAM, JolCraftDictionary.DUST);
    public static final String VERDANITE_DUST = join(VERDANITE, JolCraftDictionary.DUST);
    public static final String WOECRYSTAL_DUST = join(WOECRYSTAL, JolCraftDictionary.DUST);

    // Cut
    public static final String AEGISCORE_CUT = join(AEGISCORE, JolCraftDictionary.CUT);
    public static final String ASHFANG_CUT = join(ASHFANG, JolCraftDictionary.CUT);
    public static final String DEEPMARROW_CUT = join(DEEPMARROW, JolCraftDictionary.CUT);
    public static final String EARTHBLOOD_CUT = join(EARTHBLOOD, JolCraftDictionary.CUT);
    public static final String EMBERGLASS_CUT = join(EMBERGLASS, JolCraftDictionary.CUT);
    public static final String FROSTVEIN_CUT = join(FROSTVEIN, JolCraftDictionary.CUT);
    public static final String GRIMSTONE_CUT = join(GRIMSTONE, JolCraftDictionary.CUT);
    public static final String IRONHEART_CUT = join(IRONHEART, JolCraftDictionary.CUT);
    public static final String LUMIERE_CUT = join(LUMIERE, JolCraftDictionary.CUT);
    public static final String MOONSHARD_CUT = join(MOONSHARD, JolCraftDictionary.CUT);
    public static final String RUSTAGATE_CUT = join(RUSTAGATE, JolCraftDictionary.CUT);
    public static final String SKYBURROW_CUT = join(SKYBURROW, JolCraftDictionary.CUT);
    public static final String SUNGLEAM_CUT = join(SUNGLEAM, JolCraftDictionary.CUT);
    public static final String VERDANITE_CUT = join(VERDANITE, JolCraftDictionary.CUT);
    public static final String WOECRYSTAL_CUT = join(WOECRYSTAL, JolCraftDictionary.CUT);

    // ---------------------------------------------------------------------
    // Crops / Food / Brewing
    // ---------------------------------------------------------------------

    public static final String BLOODROOT = JolCraftBlockIds.BLOODROOT;

    public static final String BARLEY_SEEDS = join(JolCraftDictionary.BARLEY, plural(JolCraftDictionary.SEED));
    public static final String BARLEY = JolCraftDictionary.BARLEY;
    public static final String BARLEY_MALT = join(JolCraftDictionary.BARLEY, JolCraftDictionary.MALT);

    public static final String ASGARNIAN_SEEDS = join(JolCraftDictionary.ASGARNIAN, plural(JolCraftDictionary.SEED));
    public static final String ASGARNIAN_HOPS = join(JolCraftDictionary.ASGARNIAN, plural(JolCraftDictionary.HOP));

    public static final String DUSKHOLD_SEEDS = join(JolCraftDictionary.DUSKHOLD, plural(JolCraftDictionary.SEED));
    public static final String DUSKHOLD_HOPS = join(JolCraftDictionary.DUSKHOLD, plural(JolCraftDictionary.HOP));

    public static final String KRANDONIAN_SEEDS = join(JolCraftDictionary.KRANDONIAN, plural(JolCraftDictionary.SEED));
    public static final String KRANDONIAN_HOPS = join(JolCraftDictionary.KRANDONIAN, plural(JolCraftDictionary.HOP));

    public static final String YANILLIAN_SEEDS = join(JolCraftDictionary.YANILLIAN, plural(JolCraftDictionary.SEED));
    public static final String YANILLIAN_HOPS = join(JolCraftDictionary.YANILLIAN, plural(JolCraftDictionary.HOP));

    public static final String YEAST = JolCraftDictionary.YEAST;
    public static final String YEAST_CULTURE = join(YEAST, JolCraftDictionary.CULTURE);
    public static final String TANNIN = JolCraftDictionary.TANNIN;
    public static final String GLASS_MUG = join(JolCraftDictionary.GLASS, JolCraftDictionary.MUG);
    public static final String DWARVEN_BREW = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.BREW);
    public static final String DWARVEN_BREW_BUCKET = join(DWARVEN_BREW, JolCraftDictionary.BUCKET);
    public static final String DEEPSLATE_BULBS = join(JolCraftDictionary.DEEPSLATE, plural(JolCraftDictionary.BULB));

    // ---------------------------------------------------------------------
    // Reputation
    // ---------------------------------------------------------------------

    private static String reputationTablet(DwarvenReputationAttachment.Tier tier) {
        return join(
                JolCraftDictionary.REPUTATION,
                JolCraftDictionary.TABLET,
                String.valueOf(tier.getId())
        );
    }

    public static final String REPUTATION_TABLET_0 = reputationTablet(DwarvenReputationAttachment.Tier.STRANGER);
    public static final String REPUTATION_TABLET_1 = reputationTablet(DwarvenReputationAttachment.Tier.KNOWN_FACE);
    public static final String REPUTATION_TABLET_2 = reputationTablet(DwarvenReputationAttachment.Tier.TRUSTED);
    public static final String REPUTATION_TABLET_3 = reputationTablet(DwarvenReputationAttachment.Tier.RESPECTED);
    public static final String REPUTATION_TABLET_4 = reputationTablet(DwarvenReputationAttachment.Tier.BLOOD_KIN);

    // ---------------------------------------------------------------------
    // Tomes
    // ---------------------------------------------------------------------

    public static final String DWARVEN_TOME = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.TOME);
    public static final String DWARVEN_TOME_COMMON = join(DWARVEN_TOME, JolCraftRarityIds.COMMON);
    public static final String DWARVEN_TOME_UNCOMMON = join(DWARVEN_TOME, JolCraftRarityIds.UNCOMMON);
    public static final String DWARVEN_TOME_RARE = join(DWARVEN_TOME, JolCraftRarityIds.RARE);
    public static final String DWARVEN_TOME_EPIC = join(DWARVEN_TOME, JolCraftRarityIds.EPIC);

    public static final String ANCIENT_DWARVEN_TOME = join(JolCraftDictionary.ANCIENT, DWARVEN_TOME);
    public static final String ANCIENT_DWARVEN_TOME_COMMON = join(ANCIENT_DWARVEN_TOME, JolCraftRarityIds.COMMON);
    public static final String ANCIENT_DWARVEN_TOME_UNCOMMON = join(ANCIENT_DWARVEN_TOME, JolCraftRarityIds.UNCOMMON);
    public static final String ANCIENT_DWARVEN_TOME_RARE = join(ANCIENT_DWARVEN_TOME, JolCraftRarityIds.RARE);
    public static final String ANCIENT_DWARVEN_TOME_EPIC = join(ANCIENT_DWARVEN_TOME, JolCraftRarityIds.EPIC);
    public static final String ANCIENT_DWARVEN_TOME_LEGENDARY = join(ANCIENT_DWARVEN_TOME, JolCraftRarityIds.LEGENDARY);

    public static final String UNIDENTIFIED_DWARVEN_TOME = join(JolCraftDictionary.UNIDENTIFIED, DWARVEN_TOME);
    public static final String UNIDENTIFIED_ANCIENT_DWARVEN_TOME = join(JolCraftDictionary.UNIDENTIFIED, ANCIENT_DWARVEN_TOME);
    public static final String UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME = join(JolCraftDictionary.UNIDENTIFIED, ANCIENT_DWARVEN_TOME_LEGENDARY);

    public static final String LEGENDARY_PAGE = join(JolCraftRarityIds.LEGENDARY, JolCraftDictionary.PAGE);

    // ---------------------------------------------------------------------
    // Scrap
    // ---------------------------------------------------------------------

    public static final String SCRAP = JolCraftDictionary.SCRAP;
    public static final String SCRAP_HEAP = join(JolCraftDictionary.SCRAP, JolCraftDictionary.HEAP);

    public static final String BROKEN_PICKAXE = join(JolCraftDictionary.BROKEN, JolCraftDictionary.PICKAXE);
    public static final String BROKEN_AMULET = join(JolCraftDictionary.BROKEN, JolCraftDictionary.AMULET);
    public static final String BROKEN_BELT = join(JolCraftDictionary.BROKEN, JolCraftDictionary.BELT);
    public static final String BROKEN_COINS = join(JolCraftDictionary.BROKEN, plural(JolCraftDictionary.COIN));

    public static final String DEEPSLATE_MUG = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.MUG);
    public static final String EXPIRED_POTION = join(JolCraftDictionary.EXPIRED, JolCraftDictionary.POTION);
    public static final String INGOT_MOULD = join(JolCraftDictionary.INGOT, JolCraftDictionary.MOULD);
    public static final String MITHRIL_SCRAP = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.SCRAP);
    public static final String OLD_FABRIC = join(JolCraftDictionary.OLD, JolCraftDictionary.FABRIC);
    public static final String RUSTY_TONGS = join(JolCraftDictionary.RUSTY, plural(JolCraftDictionary.TONG));
    public static final String BROKEN_MITHRIL_SWORD = join(JolCraftDictionary.BROKEN, JolCraftDictionary.MITHRIL, JolCraftDictionary.SWORD);
    public static final String BROKEN_TABLET = join(JolCraftDictionary.BROKEN, JolCraftDictionary.TABLET);

    public static final String BROKEN_DEEPSLATE_PLATES = join(JolCraftDictionary.BROKEN, JolCraftDictionary.DEEPSLATE, plural(JolCraftDictionary.PLATE));
    public static final String BROKEN_MITHRIL_PLATE = join(JolCraftDictionary.BROKEN, JolCraftDictionary.MITHRIL, JolCraftDictionary.PLATE);

    public static final String BROKEN_DEEPSLATE_GEAR = join(JolCraftDictionary.BROKEN, JolCraftDictionary.DEEPSLATE, JolCraftDictionary.GEAR);
    public static final String BROKEN_DEEPSLATE_PICKAXE_HEAD = join(JolCraftDictionary.BROKEN, JolCraftDictionary.DEEPSLATE, JolCraftDictionary.PICKAXE, JolCraftDictionary.HEAD);
}