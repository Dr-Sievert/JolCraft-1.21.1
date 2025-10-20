package net.sievert.jolcraft.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.item.armor.JolCraftArmorMaterials;
import net.sievert.jolcraft.item.custom.tool.PestleItem;
import net.sievert.jolcraft.item.custom.bounty.BountyCrateItem;
import net.sievert.jolcraft.item.custom.bounty.BountyItem;
import net.sievert.jolcraft.item.custom.*;
import net.sievert.jolcraft.item.custom.contract.ProfessionContractItem;
import net.sievert.jolcraft.item.custom.QuillItem;
import net.sievert.jolcraft.item.custom.contract.SignedContractItem;
import net.sievert.jolcraft.item.custom.contract.WrittenContractItem;
import net.sievert.jolcraft.item.custom.compass.DeepslateCompassDialItem;
import net.sievert.jolcraft.item.custom.compass.DeepslateCompassItem;
import net.sievert.jolcraft.item.custom.scrapper.*;
import net.sievert.jolcraft.item.custom.merchant.*;
import net.sievert.jolcraft.item.custom.book.*;
import net.sievert.jolcraft.item.custom.gem.*;
import net.sievert.jolcraft.item.custom.equipment.armor.DeepslateArmorItem;
import net.sievert.jolcraft.item.custom.equipment.armor.MithrilArmorItem;
import net.sievert.jolcraft.item.custom.food.DwarvenBrewItem;
import net.sievert.jolcraft.item.custom.tool.ArtisanHammerItem;
import net.sievert.jolcraft.item.custom.tool.ChiselItem;
import net.sievert.jolcraft.item.custom.tool.SpannerItem;
import net.sievert.jolcraft.item.custom.tablet.LegendaryReputationTabletItem;
import net.sievert.jolcraft.item.custom.tablet.ReputationTabletItem;
import net.sievert.jolcraft.item.custom.tooltip.LegendaryItem;
import net.sievert.jolcraft.item.custom.tooltip.SimpleTooltipBlockItem;
import net.sievert.jolcraft.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.item.tool.JolCraftToolMaterials;
import net.sievert.jolcraft.item.food.JolCraftFoodProperties;


public class JolCraftItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JolCraft.MOD_ID);

    //Core Items
    public static final DeferredItem<Item> DEV_KEY = ITEMS.registerItem(
            "dev_key",
            props -> new SimpleTooltipItem(props.rarity(Rarity.EPIC).stacksTo(1), "dev_key")
    );

    public static final DeferredItem<Item> GOLD_COIN = ITEMS.registerItem("gold_coin",
            Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> COIN_POUCH = ITEMS.registerItem(
            "coin_pouch",
            CoinPouchItem::new,
            new Item.Properties()
                    .stacksTo(1)
    );

    public static final DeferredItem<Item> DWARVEN_LEXICON =
            ITEMS.registerItem("dwarven_lexicon", DwarvenLexiconItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_LEXICON =
            ITEMS.registerItem("ancient_dwarven_lexicon", AncientDwarvenLexiconItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<BlockItem> STRONGBOX_ITEM = JolCraftItems.ITEMS.registerItem("strongbox",
            props -> new StrongboxItem(JolCraftBlocks.STRONGBOX.get(), props
                    .stacksTo(1)
                    .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY))
    );

    public static final DeferredItem<Item> LOCKPICK = ITEMS.registerItem(
            "lockpick",
            props -> new SimpleTooltipItem(props, "lockpick")
    );

    public static final DeferredItem<Item> EMPTY_DEEPSLATE_COMPASS = ITEMS.registerItem("empty_deepslate_compass",
            Item::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> DEEPSLATE_COMPASS = ITEMS.registerItem("deepslate_compass",
            DeepslateCompassItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> DEEPSLATE_COMPASS_DIAL = ITEMS.registerItem("deepslate_compass_dial",
            DeepslateCompassDialItem::new, new Item.Properties().stacksTo(1));

    //Materials, Armors, Trims, Tools and Weapons
    public static final DeferredItem<Item> IMPURE_MITHRIL = ITEMS.registerItem("impure_mithril",
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> PURE_MITHRIL = ITEMS.registerItem("pure_mithril",
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> MITHRIL_INGOT = ITEMS.registerItem(
            "mithril_ingot",
            props -> new SimpleTooltipItem(props.fireResistant().rarity(Rarity.RARE), "trim_material")
    );

    public static final DeferredItem<Item> MITHRIL_NUGGET = ITEMS.registerItem("mithril_nugget",
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> MITHRIL_CHAINWEAVE = ITEMS.registerItem("mithril_chainweave",
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> MITHRIL_SWORD = ITEMS.registerItem("mithril_sword",
            (properties) -> new SwordItem(JolCraftToolMaterials.MITHRIL, 3.0F, -2.4F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_WARHAMMER = ITEMS.registerItem("mithril_warhammer",
            (properties) -> new SwordItem(JolCraftToolMaterials.MITHRIL, 13.0F, -3.5F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_PICKAXE = ITEMS.registerItem("mithril_pickaxe",
            (properties) -> new PickaxeItem(JolCraftToolMaterials.MITHRIL, 1.0F, -2.8F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<ShovelItem> MITHRIL_SHOVEL = ITEMS.registerItem("mithril_shovel",
            (properties) -> new ShovelItem(JolCraftToolMaterials.MITHRIL, 1.5F, -3.0F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<AxeItem> MITHRIL_AXE = ITEMS.registerItem("mithril_axe",
            (properties) -> new AxeItem(JolCraftToolMaterials.MITHRIL, 5.0F, -3.0F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<HoeItem> MITHRIL_HOE = ITEMS.registerItem("mithril_hoe",
            (properties) -> new HoeItem(JolCraftToolMaterials.MITHRIL, -2.0F, -1.0F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_HELMET = ITEMS.registerItem("mithril_helmet",
            props -> new MithrilArmorItem(JolCraftArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.HELMET, props.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_CHESTPLATE = ITEMS.registerItem("mithril_chestplate",
            props -> new MithrilArmorItem(JolCraftArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.CHESTPLATE, props.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_LEGGINGS = ITEMS.registerItem("mithril_leggings",
            props -> new MithrilArmorItem(JolCraftArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.LEGGINGS, props.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_BOOTS = ITEMS.registerItem("mithril_boots",
            props -> new MithrilArmorItem(JolCraftArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.BOOTS, props.fireResistant().rarity(Rarity.RARE)));


    public static final DeferredItem<Item> DEEPSLATE_PLATE = ITEMS.registerItem(
            "deepslate_plate",
            props -> new SimpleTooltipItem(props, "trim_material")
    );

    public static final DeferredItem<Item> DEEPSLATE_ROD = ITEMS.registerItem("deepslate_rod",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> DEEPSLATE_SWORD = ITEMS.registerItem("deepslate_sword",
            (properties) -> new SwordItem(JolCraftToolMaterials.DEEPSLATE, 3.0F, -2.4F, properties));

    public static final DeferredItem<Item> DEEPSLATE_WARHAMMER = ITEMS.registerItem("deepslate_warhammer",
            (properties) -> new SwordItem(JolCraftToolMaterials.DEEPSLATE, 13.0F, -3.5F, properties));

    public static final DeferredItem<Item> DEEPSLATE_PICKAXE = ITEMS.registerItem("deepslate_pickaxe",
            (properties) -> new PickaxeItem(JolCraftToolMaterials.DEEPSLATE, 1.0F, -2.8F, properties));

    public static final DeferredItem<ShovelItem> DEEPSLATE_SHOVEL = ITEMS.registerItem("deepslate_shovel",
            (properties) -> new ShovelItem(JolCraftToolMaterials.DEEPSLATE, 1.5F, -3.0F, properties));

    public static final DeferredItem<AxeItem> DEEPSLATE_AXE = ITEMS.registerItem("deepslate_axe",
            (properties) -> new AxeItem(JolCraftToolMaterials.DEEPSLATE, 6.0F, -3.1F, properties));

    public static final DeferredItem<HoeItem> DEEPSLATE_HOE = ITEMS.registerItem("deepslate_hoe",
            (properties) -> new HoeItem(JolCraftToolMaterials.DEEPSLATE, -2.0F, -1.0F, properties));

    public static final DeferredItem<Item> DEEPSLATE_HELMET = ITEMS.registerItem("deepslate_helmet",
            props -> new DeepslateArmorItem(JolCraftArmorMaterials.DEEPSLATE_ARMOR_MATERIAL, ArmorType.HELMET, props));

    public static final DeferredItem<Item> DEEPSLATE_CHESTPLATE = ITEMS.registerItem("deepslate_chestplate",
            props -> new DeepslateArmorItem(JolCraftArmorMaterials.DEEPSLATE_ARMOR_MATERIAL, ArmorType.CHESTPLATE, props));

    public static final DeferredItem<Item> DEEPSLATE_LEGGINGS = ITEMS.registerItem("deepslate_leggings",
            props -> new DeepslateArmorItem(JolCraftArmorMaterials.DEEPSLATE_ARMOR_MATERIAL, ArmorType.LEGGINGS, props));

    public static final DeferredItem<Item> DEEPSLATE_BOOTS = ITEMS.registerItem("deepslate_boots",
            props -> new DeepslateArmorItem(JolCraftArmorMaterials.DEEPSLATE_ARMOR_MATERIAL, ArmorType.BOOTS, props));

    public static final DeferredItem<Item> FORGE_ARMOR_TRIM_SMITHING_TEMPLATE = ITEMS.registerItem("forge_armor_trim_smithing_template",
            SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Animal-related
    public static final DeferredItem<Item> MUFFHORN_FUR = ITEMS.registerItem("muffhorn_fur",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> MUFFHORN_MILK_BUCKET = ITEMS.registerItem("muffhorn_milk_bucket",
            Item::new, new Item.Properties().craftRemainder(Items.BUCKET).component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET).usingConvertsTo(Items.BUCKET).stacksTo(1));

    //Alchemy
    public static final DeferredItem<BlockItem> DEEPSLATE_MORTAR_ITEM = JolCraftItems.ITEMS.registerItem("deepslate_mortar",
            properties -> new BlockItem(JolCraftBlocks.DEEPSLATE_MORTAR.get(), properties.stacksTo(3)));

    public static final DeferredItem<PestleItem> DEEPSLATE_PESTLE = ITEMS.registerItem("deepslate_pestle",
            (properties) -> new PestleItem(JolCraftToolMaterials.DEEPSLATE, properties));

    public static final DeferredItem<PestleItem> MITHRIL_PESTLE = ITEMS.registerItem("mithril_pestle",
            (properties) -> new PestleItem(JolCraftToolMaterials.MITHRIL, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> INVERIX = ITEMS.registerItem("inverix",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> AEGISCORE_DUST = ITEMS.registerItem("aegiscore_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> ASHFANG_DUST = ITEMS.registerItem("ashfang_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> DEEPMARROW_DUST = ITEMS.registerItem("deepmarrow_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> EARTHBLOOD_DUST = ITEMS.registerItem("earthblood_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> EMBERGLASS_DUST = ITEMS.registerItem("emberglass_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> FROSTVEIN_DUST = ITEMS.registerItem("frostvein_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> GRIMSTONE_DUST = ITEMS.registerItem("grimstone_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> IRONHEART_DUST = ITEMS.registerItem("ironheart_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> LUMIERE_DUST = ITEMS.registerItem("lumiere_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> MOONSHARD_DUST = ITEMS.registerItem("moonshard_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> RUSTAGATE_DUST = ITEMS.registerItem("rustagate_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> SKYBURROW_DUST = ITEMS.registerItem("skyburrow_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> SUNGLEAM_DUST = ITEMS.registerItem("sungleam_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> VERDANITE_DUST = ITEMS.registerItem("verdanite_dust",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> WOECRYSTAL_DUST = ITEMS.registerItem("woecrystal_dust",
            Item::new, new Item.Properties());


    //Bounty
    public static final DeferredItem<Item> PARCHMENT = ITEMS.registerSimpleItem("parchment");

    public static final DeferredItem<Item> BOUNTY = ITEMS.registerItem("bounty",
            BountyItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BOUNTY_CRATE = ITEMS.registerItem("bounty_crate",
            BountyCrateItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> RESTOCK_CRATE = ITEMS.registerItem("restock_crate",
            RestockCrateItem::new, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> REROLL_CRATE = ITEMS.registerItem("reroll_crate",
            RerollCrateItem::new, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    //Contracts and Associated Items
    public static final DeferredItem<Item> CONTRACT_BLANK = ITEMS.registerItem("contract_blank",
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> CONTRACT_WRITTEN = ITEMS.registerItem("contract_written",
            WrittenContractItem::new, new Item.Properties());

    public static final DeferredItem<Item> CONTRACT_SIGNED = ITEMS.registerItem("contract_signed",
            SignedContractItem::new, new Item.Properties());

    public static final DeferredItem<Item> GUILD_SIGIL = ITEMS.registerItem(
            "guild_sigil",
            props -> new SimpleTooltipItem(props, "guild_sigil")
    );

    public static final DeferredItem<Item> CONTRACT_GUILDMASTER = ITEMS.registerItem("contract_guildmaster",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));


    //Tier 1
    public static final DeferredItem<Item> CONTRACT_MERCHANT = ITEMS.registerItem("contract_merchant",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_HISTORIAN = ITEMS.registerItem("contract_historian",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_SCRAPPER = ITEMS.registerItem("contract_scrapper",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 2
    public static final DeferredItem<Item> CONTRACT_GUARD = ITEMS.registerItem("contract_guard",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_BREWMASTER = ITEMS.registerItem("contract_brewmaster",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_KEEPER = ITEMS.registerItem("contract_keeper",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 3
    public static final DeferredItem<Item> CONTRACT_MINER = ITEMS.registerItem("contract_miner",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_EXPLORER = ITEMS.registerItem("contract_explorer",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_ALCHEMIST = ITEMS.registerItem("contract_alchemist",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 4
    public static final DeferredItem<Item> CONTRACT_ARCANIST = ITEMS.registerItem("contract_arcanist",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_PRIEST = ITEMS.registerItem("contract_priest",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_ARTISAN = ITEMS.registerItem("contract_artisan",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 5
    public static final DeferredItem<Item> CONTRACT_CHAMPION = ITEMS.registerItem("contract_champion",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_BLACKSMITH = ITEMS.registerItem("contract_blacksmith",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_SMELTER = ITEMS.registerItem("contract_smelter",
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));


    public static final DeferredItem<Item> QUILL_EMPTY =
            ITEMS.registerItem("quill_empty",
                    props -> new QuillItem(props.stacksTo(16), "quill_empty"));

    public static final DeferredItem<Item> QUILL_SMALL =
            ITEMS.registerItem("quill_small",
                    props -> new QuillItem(props.craftRemainder(JolCraftItems.QUILL_EMPTY.get()).stacksTo(1), "quill"));

    public static final DeferredItem<Item> QUILL_HALF =
            ITEMS.registerItem("quill_half",
                    props -> new QuillItem(props.craftRemainder(JolCraftItems.QUILL_SMALL.get()).stacksTo(1), "quill"));

    public static final DeferredItem<Item> QUILL_FULL =
            ITEMS.registerItem("quill_full",
                    props -> new QuillItem(props.craftRemainder(JolCraftItems.QUILL_HALF.get()).stacksTo(1), "quill_full"));

    //Eggs

    public static final DeferredItem<Item> DWARF_SPAWN_EGG = ITEMS.registerItem("dwarf_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF.get(), properties));

    public static final DeferredItem<Item> DWARF_GUILDMASTER_SPAWN_EGG = ITEMS.registerItem("dwarf_guildmaster_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_GUILDMASTER.get(), properties));

    public static final DeferredItem<Item> DWARF_HISTORIAN_SPAWN_EGG = ITEMS.registerItem("dwarf_historian_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_HISTORIAN.get(), properties));

    public static final DeferredItem<Item> DWARF_MERCHANT_SPAWN_EGG = ITEMS.registerItem("dwarf_merchant_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_MERCHANT.get(), properties));

    public static final DeferredItem<Item> DWARF_SCRAPPER_SPAWN_EGG = ITEMS.registerItem("dwarf_scrapper_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_SCRAPPER.get(), properties));

    public static final DeferredItem<Item> DWARF_BREWMASTER_SPAWN_EGG = ITEMS.registerItem("dwarf_brewmaster_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_BREWMASTER.get(), properties));

    public static final DeferredItem<Item> DWARF_GUARD_SPAWN_EGG = ITEMS.registerItem("dwarf_guard_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_GUARD.get(), properties));

    public static final DeferredItem<Item> DWARF_KEEPER_SPAWN_EGG = ITEMS.registerItem("dwarf_keeper_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_KEEPER.get(), properties));

    public static final DeferredItem<Item> DWARF_ARTISAN_SPAWN_EGG = ITEMS.registerItem("dwarf_artisan_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_ARTISAN.get(), properties));

    public static final DeferredItem<Item> DWARF_EXPLORER_SPAWN_EGG = ITEMS.registerItem("dwarf_explorer_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_EXPLORER.get(), properties));

    public static final DeferredItem<Item> DWARF_MINER_SPAWN_EGG = ITEMS.registerItem("dwarf_miner_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_MINER.get(), properties));

    public static final DeferredItem<Item> DWARF_ALCHEMIST_SPAWN_EGG = ITEMS.registerItem("dwarf_alchemist_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_ALCHEMIST.get(), properties));

    public static final DeferredItem<Item> DWARF_ARCANIST_SPAWN_EGG = ITEMS.registerItem("dwarf_arcanist_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_ARCANIST.get(), properties));

    public static final DeferredItem<Item> DWARF_PRIEST_SPAWN_EGG = ITEMS.registerItem("dwarf_priest_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_PRIEST.get(), properties));


    public static final DeferredItem<Item> MUFFHORN_SPAWN_EGG = ITEMS.registerItem("muffhorn_spawn_egg",
            (properties) -> new SpawnEggItem(JolCraftEntities.MUFFHORN.get(), properties));

    //Gems

    public static final DeferredItem<ArtisanHammerItem> DEEPSLATE_ARTISAN_HAMMER = ITEMS.registerItem("deepslate_artisan_hammer",
            (properties) -> new ArtisanHammerItem(JolCraftToolMaterials.DEEPSLATE, properties));

    public static final DeferredItem<ArtisanHammerItem> MITHRIL_ARTISAN_HAMMER = ITEMS.registerItem("mithril_artisan_hammer",
            (properties) -> new ArtisanHammerItem(JolCraftToolMaterials.MITHRIL, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<ChiselItem> DEEPSLATE_CHISEL = ITEMS.registerItem("deepslate_chisel",
            (properties) -> new ChiselItem(JolCraftToolMaterials.DEEPSLATE, properties));

    public static final DeferredItem<ChiselItem> MITHRIL_CHISEL = ITEMS.registerItem("mithril_chisel",
            (properties) -> new ChiselItem(JolCraftToolMaterials.MITHRIL, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> GEODE_SMALL = ITEMS.registerItem(
            "geode_small",
            props -> new SimpleTooltipItem(props, "geode")
    );

    public static final DeferredItem<Item> GEODE_MEDIUM = ITEMS.registerItem(
            "geode_medium",
            props -> new SimpleTooltipItem(props, "geode")
    );

    public static final DeferredItem<Item> GEODE_LARGE = ITEMS.registerItem(
            "geode_large",
            props -> new SimpleTooltipItem(props, "geode")
    );

    public static final DeferredItem<Item> AEGISCORE = ITEMS.registerItem(
            "aegiscore", UncutGemItem::new
    );

    public static final DeferredItem<Item> ASHFANG = ITEMS.registerItem(
            "ashfang", UncutGemItem::new
    );

    public static final DeferredItem<Item> DEEPMARROW = ITEMS.registerItem(
            "deepmarrow", UncutGemItem::new
    );

    public static final DeferredItem<Item> EARTHBLOOD = ITEMS.registerItem(
            "earthblood", UncutGemItem::new
    );

    public static final DeferredItem<Item> EMBERGLASS = ITEMS.registerItem(
            "emberglass", UncutGemItem::new
    );

    public static final DeferredItem<Item> FROSTVEIN = ITEMS.registerItem(
            "frostvein", UncutGemItem::new
    );

    public static final DeferredItem<Item> GRIMSTONE = ITEMS.registerItem(
            "grimstone", UncutGemItem::new
    );

    public static final DeferredItem<Item> IRONHEART = ITEMS.registerItem(
            "ironheart", UncutGemItem::new
    );

    public static final DeferredItem<Item> LUMIERE = ITEMS.registerItem(
            "lumiere", UncutGemItem::new
    );

    public static final DeferredItem<Item> MOONSHARD = ITEMS.registerItem(
            "moonshard", UncutGemItem::new
    );

    public static final DeferredItem<Item> RUSTAGATE = ITEMS.registerItem(
            "rustagate", UncutGemItem::new
    );

    public static final DeferredItem<Item> SKYBURROW = ITEMS.registerItem(
            "skyburrow", UncutGemItem::new
    );

    public static final DeferredItem<Item> SUNGLEAM = ITEMS.registerItem(
            "sungleam", UncutGemItem::new
    );

    public static final DeferredItem<Item> VERDANITE = ITEMS.registerItem(
            "verdanite", UncutGemItem::new
    );

    public static final DeferredItem<Item> WOECRYSTAL = ITEMS.registerItem(
            "woecrystal", UncutGemItem::new
    );



    public static final DeferredItem<Item> AEGISCORE_CUT    = ITEMS.registerItem("aegiscore_cut",    props -> new CutGemItem(props, "aegiscore"));
    public static final DeferredItem<Item> ASHFANG_CUT      = ITEMS.registerItem("ashfang_cut",      props -> new CutGemItem(props, "ashfang"));
    public static final DeferredItem<Item> DEEPMARROW_CUT   = ITEMS.registerItem("deepmarrow_cut",   props -> new CutGemItem(props, "deepmarrow"));
    public static final DeferredItem<Item> EARTHBLOOD_CUT   = ITEMS.registerItem("earthblood_cut",   props -> new CutGemItem(props, "earthblood"));
    public static final DeferredItem<Item> EMBERGLASS_CUT   = ITEMS.registerItem("emberglass_cut",   props -> new CutGemItem(props, "emberglass"));
    public static final DeferredItem<Item> FROSTVEIN_CUT    = ITEMS.registerItem("frostvein_cut",    props -> new CutGemItem(props, "frostvein"));
    public static final DeferredItem<Item> GRIMSTONE_CUT    = ITEMS.registerItem("grimstone_cut",    props -> new CutGemItem(props, "grimstone"));
    public static final DeferredItem<Item> IRONHEART_CUT    = ITEMS.registerItem("ironheart_cut",    props -> new CutGemItem(props, "ironheart"));
    public static final DeferredItem<Item> LUMIERE_CUT      = ITEMS.registerItem("lumiere_cut",      props -> new CutGemItem(props, "lumiere"));
    public static final DeferredItem<Item> MOONSHARD_CUT    = ITEMS.registerItem("moonshard_cut",    props -> new CutGemItem(props, "moonshard"));
    public static final DeferredItem<Item> RUSTAGATE_CUT    = ITEMS.registerItem("rustagate_cut",    props -> new CutGemItem(props, "rustagate"));
    public static final DeferredItem<Item> SKYBURROW_CUT    = ITEMS.registerItem("skyburrow_cut",    props -> new CutGemItem(props, "skyburrow"));
    public static final DeferredItem<Item> SUNGLEAM_CUT     = ITEMS.registerItem("sungleam_cut",     props -> new CutGemItem(props, "sungleam"));
    public static final DeferredItem<Item> VERDANITE_CUT    = ITEMS.registerItem("verdanite_cut",    props -> new CutGemItem(props, "verdanite"));
    public static final DeferredItem<Item> WOECRYSTAL_CUT   = ITEMS.registerItem("woecrystal_cut",   props -> new CutGemItem(props, "woecrystal"));

    //Crops, food and brewing

    public static final DeferredItem<Item> BARLEY_SEEDS = ITEMS.registerItem("barley_seeds",
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.BARLEY_CROP.get(), properties, "vanilla_crop"));

    public static final DeferredItem<Item> BARLEY =
            ITEMS.registerItem("barley", Item::new,
                    new Item.Properties());

    public static final DeferredItem<Item> BARLEY_MALT =
            ITEMS.registerItem("barley_malt",
                    props -> new SimpleTooltipItem(props, "malt"));


    public static final DeferredItem<Item> ASGARNIAN_SEEDS = ITEMS.registerItem("asgarnian_seeds",
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), properties, "hops_seed"));

    public static final DeferredItem<Item> ASGARNIAN_HOPS = ITEMS.registerItem(
            "asgarnian_hops",
            props -> new SimpleTooltipItem(props, "hops")
    );

    public static final DeferredItem<Item> DUSKHOLD_SEEDS = ITEMS.registerItem("duskhold_seeds",
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), properties, "hops_seed"));

    public static final DeferredItem<Item> DUSKHOLD_HOPS = ITEMS.registerItem(
            "duskhold_hops",
            props -> new SimpleTooltipItem(props, "hops")
    );

    public static final DeferredItem<Item> KRANDONIAN_SEEDS = ITEMS.registerItem("krandonian_seeds",
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), properties, "hops_seed"));

    public static final DeferredItem<Item> KRANDONIAN_HOPS = ITEMS.registerItem(
            "krandonian_hops",
            props -> new SimpleTooltipItem(props, "hops")
    );

    public static final DeferredItem<Item> YANILLIAN_SEEDS = ITEMS.registerItem("yanillian_seeds",
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), properties, "hops_seed"));

    public static final DeferredItem<Item> YANILLIAN_HOPS = ITEMS.registerItem(
            "yanillian_hops",
            props -> new SimpleTooltipItem(props, "hops")
    );

    public static final DeferredItem<Item> YEAST = ITEMS.registerItem(
            "yeast",
            props -> new SimpleTooltipItem(props.stacksTo(16), "yeast")
    );

    public static final DeferredItem<Item> GLASS_MUG = ITEMS.registerItem(
            "glass_mug",
            props -> new SimpleTooltipItem(props.stacksTo(16), "glass_mug")
    );

    public static final DeferredItem<Item> DWARVEN_BREW =
            ITEMS.registerItem("dwarven_brew",  (properties) -> new DwarvenBrewItem(properties.food(JolCraftFoodProperties.DWARVEN_BREW, JolCraftFoodProperties.DWARVEN_BREW_EFFECT).usingConvertsTo(JolCraftItems.GLASS_MUG.get()).stacksTo(1)));

    public static final DeferredItem<Item> DEEPSLATE_BULBS = ITEMS.registerItem("deepslate_bulbs",
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(), properties.food(JolCraftFoodProperties.DWARVEN_BREW, JolCraftFoodProperties.DEEPSLATE_BULBS_EFFECT), "deepslate_bulbs"));

    //Reputation
    public static final DeferredItem<Item> REPUTATION_TABLET_0 =
            ITEMS.registerItem("reputation_tablet_0", ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> REPUTATION_TABLET_1 =
            ITEMS.registerItem("reputation_tablet_1", ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> REPUTATION_TABLET_2 =
            ITEMS.registerItem("reputation_tablet_2", ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> REPUTATION_TABLET_3 =
            ITEMS.registerItem("reputation_tablet_3", ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> REPUTATION_TABLET_4 =
            ITEMS.registerItem("reputation_tablet_4", LegendaryReputationTabletItem::new,
                    new Item.Properties().stacksTo(1));

    // Tomes
    public static final DeferredItem<Item> DWARVEN_TOME = ITEMS.registerSimpleItem("dwarven_tome");

    public static final DeferredItem<Item> UNIDENTIFIED_DWARVEN_TOME =
            ITEMS.registerItem("unidentified_dwarven_tome", properties -> new UnidentifiedDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> DWARVEN_TOME_COMMON =
            ITEMS.registerItem("dwarven_tome_common", properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> DWARVEN_TOME_UNCOMMON =
            ITEMS.registerItem("dwarven_tome_uncommon", properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> DWARVEN_TOME_RARE =
            ITEMS.registerItem("dwarven_tome_rare", properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> DWARVEN_TOME_EPIC =
            ITEMS.registerItem("dwarven_tome_epic", properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME = ITEMS.registerSimpleItem("ancient_dwarven_tome");

    public static final DeferredItem<Item> ANCIENT_UNIDENTIFIED_DWARVEN_TOME =
            ITEMS.registerItem("unidentified_ancient_dwarven_tome", properties -> new AncientUnidentifiedTomeItem(properties) {
            }, new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_COMMON =
            ITEMS.registerItem("ancient_dwarven_tome_common", properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_UNCOMMON =
            ITEMS.registerItem("ancient_dwarven_tome_uncommon", properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_RARE =
            ITEMS.registerItem("ancient_dwarven_tome_rare", properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_EPIC =
            ITEMS.registerItem("ancient_dwarven_tome_epic", properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> LEGENDARY_PAGE = ITEMS.registerItem(
            "legendary_page",
            LegendaryItem::new
    );

    public static final DeferredItem<Item> LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME =
            ITEMS.registerItem("legendary_unidentified_ancient_dwarven_tome", properties -> new LegendaryAncientUnidentifiedTomeItem(properties) {
            }, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_LEGENDARY =
            ITEMS.registerItem("ancient_dwarven_tome_legendary", properties -> new LegendaryAncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1));


    //Tools
    public static final DeferredItem<Item> COPPER_SPANNER =
            ITEMS.registerItem("copper_spanner",
                    SpannerItem::new,
                    new Item.Properties().durability(16).stacksTo(1).enchantable(10).repairable(Items.COPPER_INGOT)
            );

    public static final DeferredItem<Item> IRON_SPANNER =
            ITEMS.registerItem("iron_spanner",
                    SpannerItem::new,
                    new Item.Properties().durability(64).stacksTo(1).enchantable(10).repairable(Items.IRON_INGOT)
            );

    //Scrap
    public static final DeferredItem<Item> SCRAP = ITEMS.registerSimpleItem("scrap");

    public static final DeferredItem<Item> SCRAP_HEAP = ITEMS.registerSimpleItem("scrap_heap");

    public static final DeferredItem<Item> BROKEN_PICKAXE =
            ITEMS.registerItem("broken_pickaxe", SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_AMULET =
            ITEMS.registerItem("broken_amulet", SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_BELT =
            ITEMS.registerItem("broken_belt", SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_COINS =
            ITEMS.registerItem("broken_coins", SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> DEEPSLATE_MUG =
            ITEMS.registerItem("deepslate_mug", SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> EXPIRED_POTION =
            ITEMS.registerItem("expired_potion", SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> INGOT_MOULD =
            ITEMS.registerItem("ingot_mould", SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> MITHRIL_SALVAGE =
            ITEMS.registerItem("mithril_salvage", SalvageItem::new, new Item.Properties().rarity(Rarity.RARE));

    public static final DeferredItem<Item> OLD_FABRIC =
            ITEMS.registerItem("old_fabric", SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> RUSTY_TONGS =
            ITEMS.registerItem("rusty_tongs", SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_MITHRIL_SWORD =
            ITEMS.registerItem("broken_mithril_sword", SalvageItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> BROKEN_TABLET =
            ITEMS.registerItem("broken_tablet", SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> BROKEN_DEEPSLATE_PLATES =
            ITEMS.registerItem("broken_deepslate_plates", SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> BROKEN_MITHRIL_PLATE =
            ITEMS.registerItem("broken_mithril_plate", SalvageItem::new, new Item.Properties().rarity(Rarity.RARE));

    public static final DeferredItem<Item> BROKEN_DEEPSLATE_GEAR =
            ITEMS.registerItem("broken_deepslate_gear", SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> BROKEN_DEEPSLATE_PICKAXE_HEAD =
            ITEMS.registerItem("broken_deepslate_pickaxe_head", SalvageItem::new, new Item.Properties());


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
