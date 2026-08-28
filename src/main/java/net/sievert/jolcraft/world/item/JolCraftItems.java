package net.sievert.jolcraft.world.item;

import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.custom.alchemy.EssenceItem;
import net.sievert.jolcraft.world.item.custom.instrument.WarHornItem;
import net.sievert.jolcraft.world.item.custom.tool.ArtisanHammerItem;
import net.sievert.jolcraft.world.item.custom.tool.ChiselItem;
import net.sievert.jolcraft.world.item.custom.tool.PestleItem;
import net.sievert.jolcraft.world.item.custom.tool.SpannerItem;
import net.sievert.jolcraft.world.item.equipment.JolCraftArmorItemSet;
import net.sievert.jolcraft.world.item.registry.*;

import java.util.List;

public final class JolCraftItems {

    private JolCraftItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JolCraft.MOD_ID);

    // -------------------------------------------------------------------------
    // Core
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> DEV_KEY = JolCraftCoreItems.registerDevKey();
    public static final DeferredItem<Item> GOLD_COIN = JolCraftCoreItems.registerGoldCoin();
    public static final DeferredItem<Item> COIN_POUCH = JolCraftCoreItems.registerCoinPouch();
    public static final DeferredItem<BlockItem> STRONGBOX_ITEM = JolCraftCoreItems.registerStrongboxItem();
    public static final DeferredItem<Item> LOCKPICK = JolCraftCoreItems.registerLockpick();
    public static final DeferredItem<Item> EMPTY_DEEPSLATE_COMPASS = JolCraftCoreItems.registerEmptyDeepslateCompass();
    public static final DeferredItem<Item> DEEPSLATE_COMPASS = JolCraftCoreItems.registerDeepslateCompass();
    public static final DeferredItem<Item> DEEPSLATE_COMPASS_DIAL = JolCraftCoreItems.registerDeepslateCompassDial();
    public static final DeferredItem<Item> DIAL_DUST = JolCraftCoreItems.registerDialDust();
    public static final DeferredItem<WarHornItem> WAR_HORN = JolCraftEquipmentItems.registerWarHorn();

    public static final DeferredItem<Item> REPUTATION_TABLET_0 = JolCraftCoreItems.registerReputationTablet0();
    public static final DeferredItem<Item> REPUTATION_TABLET_1 = JolCraftCoreItems.registerReputationTablet1();
    public static final DeferredItem<Item> REPUTATION_TABLET_2 = JolCraftCoreItems.registerReputationTablet2();
    public static final DeferredItem<Item> REPUTATION_TABLET_3 = JolCraftCoreItems.registerReputationTablet3();
    public static final DeferredItem<Item> REPUTATION_TABLET_4 = JolCraftCoreItems.registerReputationTablet4();

    // -------------------------------------------------------------------------
    // Materials
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> IMPURE_MITHRIL = JolCraftMaterialItems.registerImpureMithril();
    public static final DeferredItem<Item> PURE_MITHRIL = JolCraftMaterialItems.registerPureMithril();
    public static final DeferredItem<Item> MITHRIL_INGOT = JolCraftMaterialItems.registerMithrilIngot();
    public static final DeferredItem<Item> MITHRIL_NUGGET = JolCraftMaterialItems.registerMithrilNugget();
    public static final DeferredItem<Item> MITHRIL_CHAINWEAVE = JolCraftMaterialItems.registerMithrilChainweave();
    public static final DeferredItem<Item> DEEPSLATE_PLATE = JolCraftMaterialItems.registerDeepslatePlate();
    public static final DeferredItem<Item> DEEPSLATE_ROD = JolCraftMaterialItems.registerDeepslateRod();
    public static final DeferredItem<Item> MUFFHORN_FUR = JolCraftMaterialItems.registerMuffhornFur();

    public static final DeferredItem<Item> GEODE_SMALL = JolCraftMaterialItems.registerGeodeSmall();
    public static final DeferredItem<Item> GEODE_MEDIUM = JolCraftMaterialItems.registerGeodeMedium();
    public static final DeferredItem<Item> GEODE_LARGE = JolCraftMaterialItems.registerGeodeLarge();

    // -------------------------------------------------------------------------
    // Armor
    // -------------------------------------------------------------------------

    public static final JolCraftArmorItemSet DEEPSLATE_ARMOR_SET = JolCraftArmorItems.DEEPSLATE;
    public static final JolCraftArmorItemSet MITHRIL_ARMOR_SET = JolCraftArmorItems.MITHRIL;

    public static final DeferredItem<Item> DEEPSLATE_HELMET = DEEPSLATE_ARMOR_SET.helmet();
    public static final DeferredItem<Item> DEEPSLATE_CHESTPLATE = DEEPSLATE_ARMOR_SET.chestplate();
    public static final DeferredItem<Item> DEEPSLATE_LEGGINGS = DEEPSLATE_ARMOR_SET.leggings();
    public static final DeferredItem<Item> DEEPSLATE_BOOTS = DEEPSLATE_ARMOR_SET.boots();

    public static final DeferredItem<Item> MITHRIL_HELMET = MITHRIL_ARMOR_SET.helmet();
    public static final DeferredItem<Item> MITHRIL_CHESTPLATE = MITHRIL_ARMOR_SET.chestplate();
    public static final DeferredItem<Item> MITHRIL_LEGGINGS = MITHRIL_ARMOR_SET.leggings();
    public static final DeferredItem<Item> MITHRIL_BOOTS = MITHRIL_ARMOR_SET.boots();

    public static final List<JolCraftArmorItemSet> ARMOR_SETS = List.of(
            DEEPSLATE_ARMOR_SET,
            MITHRIL_ARMOR_SET
    );

    public static final DeferredItem<Item> FORGE_ARMOR_TRIM_SMITHING_TEMPLATE = JolCraftArmorItems.registerForgeArmorTrimTemplate();

    // -------------------------------------------------------------------------
    // Equipment
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> MITHRIL_SWORD = JolCraftEquipmentItems.registerMithrilSword();
    public static final DeferredItem<Item> MITHRIL_WARHAMMER = JolCraftEquipmentItems.registerMithrilWarhammer();
    public static final DeferredItem<Item> MITHRIL_PICKAXE = JolCraftEquipmentItems.registerMithrilPickaxe();
    public static final DeferredItem<ShovelItem> MITHRIL_SHOVEL = JolCraftEquipmentItems.registerMithrilShovel();
    public static final DeferredItem<AxeItem> MITHRIL_AXE = JolCraftEquipmentItems.registerMithrilAxe();
    public static final DeferredItem<HoeItem> MITHRIL_HOE = JolCraftEquipmentItems.registerMithrilHoe();

    public static final DeferredItem<Item> DEEPSLATE_SWORD = JolCraftEquipmentItems.registerDeepslateSword();
    public static final DeferredItem<Item> DEEPSLATE_WARHAMMER = JolCraftEquipmentItems.registerDeepslateWarhammer();
    public static final DeferredItem<Item> DEEPSLATE_PICKAXE = JolCraftEquipmentItems.registerDeepslatePickaxe();
    public static final DeferredItem<ShovelItem> DEEPSLATE_SHOVEL = JolCraftEquipmentItems.registerDeepslateShovel();
    public static final DeferredItem<AxeItem> DEEPSLATE_AXE = JolCraftEquipmentItems.registerDeepslateAxe();
    public static final DeferredItem<HoeItem> DEEPSLATE_HOE = JolCraftEquipmentItems.registerDeepslateHoe();

    // -------------------------------------------------------------------------
    // Artisan hammers
    // -------------------------------------------------------------------------

    public static final DeferredItem<ArtisanHammerItem> WOODEN_ARTISAN_HAMMER = JolCraftEquipmentItems.registerWoodenArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> STONE_ARTISAN_HAMMER = JolCraftEquipmentItems.registerStoneArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> IRON_ARTISAN_HAMMER = JolCraftEquipmentItems.registerIronArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> GOLDEN_ARTISAN_HAMMER = JolCraftEquipmentItems.registerGoldenArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> DIAMOND_ARTISAN_HAMMER = JolCraftEquipmentItems.registerDiamondArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> NETHERITE_ARTISAN_HAMMER = JolCraftEquipmentItems.registerNetheriteArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> DEEPSLATE_ARTISAN_HAMMER = JolCraftEquipmentItems.registerDeepslateArtisanHammer();
    public static final DeferredItem<ArtisanHammerItem> MITHRIL_ARTISAN_HAMMER = JolCraftEquipmentItems.registerMithrilArtisanHammer();

    // -------------------------------------------------------------------------
    // Chisels
    // -------------------------------------------------------------------------

    public static final DeferredItem<ChiselItem> WOODEN_CHISEL = JolCraftEquipmentItems.registerWoodenChisel();
    public static final DeferredItem<ChiselItem> STONE_CHISEL = JolCraftEquipmentItems.registerStoneChisel();
    public static final DeferredItem<ChiselItem> IRON_CHISEL = JolCraftEquipmentItems.registerIronChisel();
    public static final DeferredItem<ChiselItem> GOLDEN_CHISEL = JolCraftEquipmentItems.registerGoldenChisel();
    public static final DeferredItem<ChiselItem> DIAMOND_CHISEL = JolCraftEquipmentItems.registerDiamondChisel();
    public static final DeferredItem<ChiselItem> NETHERITE_CHISEL = JolCraftEquipmentItems.registerNetheriteChisel();
    public static final DeferredItem<ChiselItem> DEEPSLATE_CHISEL = JolCraftEquipmentItems.registerDeepslateChisel();
    public static final DeferredItem<ChiselItem> MITHRIL_CHISEL = JolCraftEquipmentItems.registerMithrilChisel();

    // -------------------------------------------------------------------------
    // Pestles
    // -------------------------------------------------------------------------

    public static final DeferredItem<PestleItem> WOODEN_PESTLE = JolCraftEquipmentItems.registerWoodenPestle();
    public static final DeferredItem<PestleItem> STONE_PESTLE = JolCraftEquipmentItems.registerStonePestle();
    public static final DeferredItem<PestleItem> IRON_PESTLE = JolCraftEquipmentItems.registerIronPestle();
    public static final DeferredItem<PestleItem> GOLDEN_PESTLE = JolCraftEquipmentItems.registerGoldenPestle();
    public static final DeferredItem<PestleItem> DIAMOND_PESTLE = JolCraftEquipmentItems.registerDiamondPestle();
    public static final DeferredItem<PestleItem> NETHERITE_PESTLE = JolCraftEquipmentItems.registerNetheritePestle();
    public static final DeferredItem<PestleItem> DEEPSLATE_PESTLE = JolCraftEquipmentItems.registerDeepslatePestle();
    public static final DeferredItem<PestleItem> MITHRIL_PESTLE = JolCraftEquipmentItems.registerMithrilPestle();

    // -------------------------------------------------------------------------
    // Spanners
    // -------------------------------------------------------------------------

    public static final DeferredItem<SpannerItem> WOODEN_SPANNER = JolCraftEquipmentItems.registerWoodenSpanner();
    public static final DeferredItem<SpannerItem> STONE_SPANNER = JolCraftEquipmentItems.registerStoneSpanner();
    public static final DeferredItem<SpannerItem> IRON_SPANNER = JolCraftEquipmentItems.registerIronSpanner();
    public static final DeferredItem<SpannerItem> GOLDEN_SPANNER = JolCraftEquipmentItems.registerGoldenSpanner();
    public static final DeferredItem<SpannerItem> DIAMOND_SPANNER = JolCraftEquipmentItems.registerDiamondSpanner();
    public static final DeferredItem<SpannerItem> NETHERITE_SPANNER = JolCraftEquipmentItems.registerNetheriteSpanner();
    public static final DeferredItem<SpannerItem> DEEPSLATE_SPANNER = JolCraftEquipmentItems.registerDeepslateSpanner();
    public static final DeferredItem<SpannerItem> MITHRIL_SPANNER = JolCraftEquipmentItems.registerMithrilSpanner();

    // -------------------------------------------------------------------------
    // Food
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> MUFFHORN_MILK_BUCKET = JolCraftFoodItems.registerMuffhornMilkBucket();

    // -------------------------------------------------------------------------
    // Alchemy
    // -------------------------------------------------------------------------

    public static final DeferredItem<BlockItem> MORTAR_ITEM = JolCraftAlchemyItems.registerMortar();
    public static final DeferredItem<EssenceItem> ESSENCE = JolCraftAlchemyItems.registerEssence();
    public static final DeferredItem<Item> VITRIOL = JolCraftAlchemyItems.registerVitriol();
    public static final DeferredItem<Item> INVERIX = JolCraftAlchemyItems.registerInverix();

    // -------------------------------------------------------------------------
    // Gems
    // -------------------------------------------------------------------------

    public static final JolCraftGemItems.GemSet AEGISCORE_SET = JolCraftGemItems.AEGISCORE;
    public static final JolCraftGemItems.GemSet ASHFANG_SET = JolCraftGemItems.ASHFANG;
    public static final JolCraftGemItems.GemSet DEEPMARROW_SET = JolCraftGemItems.DEEPMARROW;
    public static final JolCraftGemItems.GemSet EARTHBLOOD_SET = JolCraftGemItems.EARTHBLOOD;
    public static final JolCraftGemItems.GemSet EMBERGLASS_SET = JolCraftGemItems.EMBERGLASS;
    public static final JolCraftGemItems.GemSet FROSTVEIN_SET = JolCraftGemItems.FROSTVEIN;
    public static final JolCraftGemItems.GemSet GRIMSTONE_SET = JolCraftGemItems.GRIMSTONE;
    public static final JolCraftGemItems.GemSet IRONHEART_SET = JolCraftGemItems.IRONHEART;
    public static final JolCraftGemItems.GemSet LUMIERE_SET = JolCraftGemItems.LUMIERE;
    public static final JolCraftGemItems.GemSet MOONSHARD_SET = JolCraftGemItems.MOONSHARD;
    public static final JolCraftGemItems.GemSet RUSTAGATE_SET = JolCraftGemItems.RUSTAGATE;
    public static final JolCraftGemItems.GemSet SKYBURROW_SET = JolCraftGemItems.SKYBURROW;
    public static final JolCraftGemItems.GemSet SUNGLEAM_SET = JolCraftGemItems.SUNGLEAM;
    public static final JolCraftGemItems.GemSet VERDANITE_SET = JolCraftGemItems.VERDANITE;
    public static final JolCraftGemItems.GemSet WOECRYSTAL_SET = JolCraftGemItems.WOECRYSTAL;

    public static final DeferredItem<Item> AEGISCORE = AEGISCORE_SET.uncut();
    public static final DeferredItem<Item> AEGISCORE_CUT = AEGISCORE_SET.cut();
    public static final DeferredItem<Item> AEGISCORE_DUST = AEGISCORE_SET.dust();

    public static final DeferredItem<Item> ASHFANG = ASHFANG_SET.uncut();
    public static final DeferredItem<Item> ASHFANG_CUT = ASHFANG_SET.cut();
    public static final DeferredItem<Item> ASHFANG_DUST = ASHFANG_SET.dust();

    public static final DeferredItem<Item> DEEPMARROW = DEEPMARROW_SET.uncut();
    public static final DeferredItem<Item> DEEPMARROW_CUT = DEEPMARROW_SET.cut();
    public static final DeferredItem<Item> DEEPMARROW_DUST = DEEPMARROW_SET.dust();

    public static final DeferredItem<Item> EARTHBLOOD = EARTHBLOOD_SET.uncut();
    public static final DeferredItem<Item> EARTHBLOOD_CUT = EARTHBLOOD_SET.cut();
    public static final DeferredItem<Item> EARTHBLOOD_DUST = EARTHBLOOD_SET.dust();

    public static final DeferredItem<Item> EMBERGLASS = EMBERGLASS_SET.uncut();
    public static final DeferredItem<Item> EMBERGLASS_CUT = EMBERGLASS_SET.cut();
    public static final DeferredItem<Item> EMBERGLASS_DUST = EMBERGLASS_SET.dust();

    public static final DeferredItem<Item> FROSTVEIN = FROSTVEIN_SET.uncut();
    public static final DeferredItem<Item> FROSTVEIN_CUT = FROSTVEIN_SET.cut();
    public static final DeferredItem<Item> FROSTVEIN_DUST = FROSTVEIN_SET.dust();

    public static final DeferredItem<Item> GRIMSTONE = GRIMSTONE_SET.uncut();
    public static final DeferredItem<Item> GRIMSTONE_CUT = GRIMSTONE_SET.cut();
    public static final DeferredItem<Item> GRIMSTONE_DUST = GRIMSTONE_SET.dust();

    public static final DeferredItem<Item> IRONHEART = IRONHEART_SET.uncut();
    public static final DeferredItem<Item> IRONHEART_CUT = IRONHEART_SET.cut();
    public static final DeferredItem<Item> IRONHEART_DUST = IRONHEART_SET.dust();

    public static final DeferredItem<Item> LUMIERE = LUMIERE_SET.uncut();
    public static final DeferredItem<Item> LUMIERE_CUT = LUMIERE_SET.cut();
    public static final DeferredItem<Item> LUMIERE_DUST = LUMIERE_SET.dust();

    public static final DeferredItem<Item> MOONSHARD = MOONSHARD_SET.uncut();
    public static final DeferredItem<Item> MOONSHARD_CUT = MOONSHARD_SET.cut();
    public static final DeferredItem<Item> MOONSHARD_DUST = MOONSHARD_SET.dust();

    public static final DeferredItem<Item> RUSTAGATE = RUSTAGATE_SET.uncut();
    public static final DeferredItem<Item> RUSTAGATE_CUT = RUSTAGATE_SET.cut();
    public static final DeferredItem<Item> RUSTAGATE_DUST = RUSTAGATE_SET.dust();

    public static final DeferredItem<Item> SKYBURROW = SKYBURROW_SET.uncut();
    public static final DeferredItem<Item> SKYBURROW_CUT = SKYBURROW_SET.cut();
    public static final DeferredItem<Item> SKYBURROW_DUST = SKYBURROW_SET.dust();

    public static final DeferredItem<Item> SUNGLEAM = SUNGLEAM_SET.uncut();
    public static final DeferredItem<Item> SUNGLEAM_CUT = SUNGLEAM_SET.cut();
    public static final DeferredItem<Item> SUNGLEAM_DUST = SUNGLEAM_SET.dust();

    public static final DeferredItem<Item> VERDANITE = VERDANITE_SET.uncut();
    public static final DeferredItem<Item> VERDANITE_CUT = VERDANITE_SET.cut();
    public static final DeferredItem<Item> VERDANITE_DUST = VERDANITE_SET.dust();

    public static final DeferredItem<Item> WOECRYSTAL = WOECRYSTAL_SET.uncut();
    public static final DeferredItem<Item> WOECRYSTAL_CUT = WOECRYSTAL_SET.cut();
    public static final DeferredItem<Item> WOECRYSTAL_DUST = WOECRYSTAL_SET.dust();

    // -------------------------------------------------------------------------
    // Crops
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> BLOODROOT = JolCraftCropItems.registerBloodroot();

    public static final DeferredItem<Item> BARLEY_SEEDS = JolCraftCropItems.registerBarleySeeds();
    public static final DeferredItem<Item> BARLEY = JolCraftCropItems.registerBarley();

    public static final DeferredItem<Item> ASGARNIAN_SEEDS = JolCraftCropItems.registerAsgarnianSeeds();
    public static final DeferredItem<Item> ASGARNIAN_HOPS = JolCraftCropItems.registerAsgarnianHops();

    public static final DeferredItem<Item> DUSKHOLD_SEEDS = JolCraftCropItems.registerDuskholdSeeds();
    public static final DeferredItem<Item> DUSKHOLD_HOPS = JolCraftCropItems.registerDuskholdHops();

    public static final DeferredItem<Item> KRANDONIAN_SEEDS = JolCraftCropItems.registerKrandonianSeeds();
    public static final DeferredItem<Item> KRANDONIAN_HOPS = JolCraftCropItems.registerKrandonianHops();

    public static final DeferredItem<Item> YANILLIAN_SEEDS = JolCraftCropItems.registerYanillianSeeds();
    public static final DeferredItem<Item> YANILLIAN_HOPS = JolCraftCropItems.registerYanillianHops();

    public static final DeferredItem<Item> DEEPSLATE_BULBS = JolCraftCropItems.registerDeepslateBulbs();

    // -------------------------------------------------------------------------
    // Brewing
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> BARLEY_MALT = JolCraftBrewingItems.registerBarleyMalt();
    public static final DeferredItem<Item> YEAST_CULTURE = JolCraftBrewingItems.registerYeastCulture();
    public static final DeferredItem<Item> YEAST = JolCraftBrewingItems.registerYeast(JolCraftFluids.YEAST);
    public static final DeferredItem<Item> TANNIN = JolCraftBrewingItems.registerTannin(JolCraftFluids.TANNIN);
    public static final DeferredItem<Item> GLASS_MUG = JolCraftBrewingItems.registerGlassMug();
    public static final DeferredItem<Item> DWARVEN_BREW = JolCraftBrewingItems.registerDwarvenBrew(GLASS_MUG);
    public static final DeferredItem<Item> DWARVEN_BREW_BUCKET = JolCraftBrewingItems.registerDwarvenBrewBucket();

    // -------------------------------------------------------------------------
    // Bounty
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> BOUNTY = JolCraftBountyItems.registerBounty();
    public static final DeferredItem<Item> BOUNTY_CRATE = JolCraftBountyItems.registerBountyCrate();
    public static final DeferredItem<Item> RESTOCK_CRATE = JolCraftBountyItems.registerRestockCrate();
    public static final DeferredItem<Item> REROLL_CRATE = JolCraftBountyItems.registerRerollCrate();
    public static final DeferredItem<Item> REWARD_CRATE = JolCraftBountyItems.registerRewardCrate();

    // -------------------------------------------------------------------------
    // Contracts
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> PARCHMENT = JolCraftContractItems.registerParchment();
    public static final DeferredItem<Item> CONTRACT_BLANK = JolCraftContractItems.registerBlank();
    public static final DeferredItem<Item> CONTRACT_WRITTEN = JolCraftContractItems.registerWritten();
    public static final DeferredItem<Item> CONTRACT_SIGNED = JolCraftContractItems.registerSigned();
    public static final DeferredItem<Item> GUILD_SIGIL_MOULD = JolCraftContractItems.registerGuildSigilMould();
    public static final DeferredItem<Item> GUILD_SIGIL = JolCraftContractItems.registerGuildSigil();

    public static final DeferredItem<Item> CONTRACT_GUILDMASTER = JolCraftContractItems.registerGuildmaster();
    public static final DeferredItem<Item> CONTRACT_MERCHANT = JolCraftContractItems.registerMerchant();
    public static final DeferredItem<Item> CONTRACT_HISTORIAN = JolCraftContractItems.registerHistorian();
    public static final DeferredItem<Item> CONTRACT_SCRAPPER = JolCraftContractItems.registerScrapper();
    public static final DeferredItem<Item> CONTRACT_GUARD = JolCraftContractItems.registerGuard();
    public static final DeferredItem<Item> CONTRACT_BREWMASTER = JolCraftContractItems.registerBrewmaster();
    public static final DeferredItem<Item> CONTRACT_KEEPER = JolCraftContractItems.registerKeeper();
    public static final DeferredItem<Item> CONTRACT_MINER = JolCraftContractItems.registerMiner();
    public static final DeferredItem<Item> CONTRACT_EXPLORER = JolCraftContractItems.registerExplorer();
    public static final DeferredItem<Item> CONTRACT_ALCHEMIST = JolCraftContractItems.registerAlchemist();
    public static final DeferredItem<Item> CONTRACT_ARCANIST = JolCraftContractItems.registerArcanist();
    public static final DeferredItem<Item> CONTRACT_PRIEST = JolCraftContractItems.registerPriest();
    public static final DeferredItem<Item> CONTRACT_ARTISAN = JolCraftContractItems.registerArtisan();
    public static final DeferredItem<Item> CONTRACT_CHAMPION = JolCraftContractItems.registerChampion();
    public static final DeferredItem<Item> CONTRACT_BLACKSMITH = JolCraftContractItems.registerBlacksmith();
    public static final DeferredItem<Item> CONTRACT_SMELTER = JolCraftContractItems.registerSmelter();

    public static final DeferredItem<Item> QUILL_EMPTY = JolCraftContractItems.registerQuillEmpty();
    public static final DeferredItem<Item> QUILL_SMALL = JolCraftContractItems.registerQuillSmall(QUILL_EMPTY);
    public static final DeferredItem<Item> QUILL_HALF = JolCraftContractItems.registerQuillHalf(QUILL_SMALL);
    public static final DeferredItem<Item> QUILL_FULL = JolCraftContractItems.registerQuillFull(QUILL_HALF);

    // -------------------------------------------------------------------------
    // Spawn Eggs
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> DWARF_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfSpawnEgg();
    public static final DeferredItem<Item> DWARF_GUILDMASTER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfGuildmasterSpawnEgg();
    public static final DeferredItem<Item> DWARF_HISTORIAN_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfHistorianSpawnEgg();
    public static final DeferredItem<Item> DWARF_MERCHANT_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfMerchantSpawnEgg();
    public static final DeferredItem<Item> DWARF_SCRAPPER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfScrapperSpawnEgg();
    public static final DeferredItem<Item> DWARF_BREWMASTER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfBrewmasterSpawnEgg();
    public static final DeferredItem<Item> DWARF_GUARD_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfGuardSpawnEgg();
    public static final DeferredItem<Item> DWARF_KEEPER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfKeeperSpawnEgg();
    public static final DeferredItem<Item> DWARF_ARTISAN_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfArtisanSpawnEgg();
    public static final DeferredItem<Item> DWARF_EXPLORER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfExplorerSpawnEgg();
    public static final DeferredItem<Item> DWARF_MINER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfMinerSpawnEgg();
    public static final DeferredItem<Item> DWARF_ALCHEMIST_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfAlchemistSpawnEgg();
    public static final DeferredItem<Item> DWARF_ARCANIST_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfArcanistSpawnEgg();
    public static final DeferredItem<Item> DWARF_PRIEST_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfPriestSpawnEgg();
    public static final DeferredItem<Item> DWARF_BLACKSMITH_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfBlacksmithSpawnEgg();
    public static final DeferredItem<Item> DWARF_CHAMPION_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfChampionSpawnEgg();
    public static final DeferredItem<Item> DWARF_SMELTER_SPAWN_EGG = JolCraftSpawnEggItems.registerDwarfSmelterSpawnEgg();
    public static final DeferredItem<Item> MUFFHORN_SPAWN_EGG = JolCraftSpawnEggItems.registerMuffhornSpawnEgg();

    // -------------------------------------------------------------------------
    // Tomes
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> DWARVEN_LEXICON = JolCraftTomeItems.registerDwarvenLexicon();
    public static final DeferredItem<Item> ANCIENT_DWARVEN_LEXICON = JolCraftTomeItems.registerAncientDwarvenLexicon();

    public static final DeferredItem<Item> DWARVEN_TOME = JolCraftTomeItems.registerDwarvenTome();
    public static final DeferredItem<Item> UNIDENTIFIED_DWARVEN_TOME = JolCraftTomeItems.registerUnidentifiedDwarvenTome();
    public static final DeferredItem<Item> DWARVEN_TOME_COMMON = JolCraftTomeItems.registerDwarvenTomeCommon();
    public static final DeferredItem<Item> DWARVEN_TOME_UNCOMMON = JolCraftTomeItems.registerDwarvenTomeUncommon();
    public static final DeferredItem<Item> DWARVEN_TOME_RARE = JolCraftTomeItems.registerDwarvenTomeRare();
    public static final DeferredItem<Item> DWARVEN_TOME_EPIC = JolCraftTomeItems.registerDwarvenTomeEpic();

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME = JolCraftTomeItems.registerAncientDwarvenTome();
    public static final DeferredItem<Item> UNIDENTIFIED_ANCIENT_DWARVEN_TOME = JolCraftTomeItems.registerUnidentifiedAncientDwarvenTome();
    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_COMMON = JolCraftTomeItems.registerAncientDwarvenTomeCommon();
    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_UNCOMMON = JolCraftTomeItems.registerAncientDwarvenTomeUncommon();
    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_RARE = JolCraftTomeItems.registerAncientDwarvenTomeRare();
    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_EPIC = JolCraftTomeItems.registerAncientDwarvenTomeEpic();

    public static final DeferredItem<Item> LEGENDARY_PAGE = JolCraftTomeItems.registerLegendaryPage();
    public static final DeferredItem<Item> UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME = JolCraftTomeItems.registerUnidentifiedLegendaryAncientDwarvenTome();
    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_LEGENDARY = JolCraftTomeItems.registerAncientDwarvenTomeLegendary();

    // -------------------------------------------------------------------------
    // Salvage
    // -------------------------------------------------------------------------

    public static final DeferredItem<Item> SCRAP = JolCraftSalvageItems.registerScrap();
    public static final DeferredItem<Item> SCRAP_HEAP = JolCraftSalvageItems.registerScrapHeap();
    public static final DeferredItem<Item> BROKEN_PICKAXE = JolCraftSalvageItems.registerBrokenPickaxe();
    public static final DeferredItem<Item> BROKEN_AMULET = JolCraftSalvageItems.registerBrokenAmulet();
    public static final DeferredItem<Item> BROKEN_BELT = JolCraftSalvageItems.registerBrokenBelt();
    public static final DeferredItem<Item> BROKEN_COINS = JolCraftSalvageItems.registerBrokenCoins();
    public static final DeferredItem<Item> DEEPSLATE_MUG = JolCraftSalvageItems.registerDeepslateMug();
    public static final DeferredItem<Item> EXPIRED_POTION = JolCraftSalvageItems.registerExpiredPotion();
    public static final DeferredItem<Item> INGOT_MOULD = JolCraftSalvageItems.registerIngotMould();
    public static final DeferredItem<Item> MITHRIL_SCRAP = JolCraftSalvageItems.registerMithrilScrap();
    public static final DeferredItem<Item> OLD_FABRIC = JolCraftSalvageItems.registerOldFabric();
    public static final DeferredItem<Item> RUSTY_TONGS = JolCraftSalvageItems.registerRustyTongs();
    public static final DeferredItem<Item> BROKEN_MITHRIL_SWORD = JolCraftSalvageItems.registerBrokenMithrilSword();
    public static final DeferredItem<Item> BROKEN_TABLET = JolCraftSalvageItems.registerBrokenTablet();
    public static final DeferredItem<Item> BROKEN_DEEPSLATE_PLATES = JolCraftSalvageItems.registerBrokenDeepslatePlates();
    public static final DeferredItem<Item> BROKEN_MITHRIL_PLATE = JolCraftSalvageItems.registerBrokenMithrilPlate();
    public static final DeferredItem<Item> BROKEN_DEEPSLATE_GEAR = JolCraftSalvageItems.registerBrokenDeepslateGear();
    public static final DeferredItem<Item> BROKEN_DEEPSLATE_PICKAXE_HEAD = JolCraftSalvageItems.registerBrokenDeepslatePickaxeHead();

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} items",
                ITEMS.getEntries().size()
        );
    }
}