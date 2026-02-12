package net.sievert.jolcraft.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.item.custom.armor.DeepslateArmorItem;
import net.sievert.jolcraft.world.item.custom.armor.MithrilArmorItem;
import net.sievert.jolcraft.world.item.custom.book.*;
import net.sievert.jolcraft.world.item.custom.bounty.BountyCrateItem;
import net.sievert.jolcraft.world.item.custom.bounty.BountyItem;
import net.sievert.jolcraft.world.item.custom.compass.DeepslateCompassDialItem;
import net.sievert.jolcraft.world.item.custom.compass.DeepslateCompassItem;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.item.custom.container.StrongboxItem;
import net.sievert.jolcraft.world.item.custom.food.DwarvenBrewItem;
import net.sievert.jolcraft.world.item.custom.gem.*;
import net.sievert.jolcraft.world.item.custom.merchant.*;
import net.sievert.jolcraft.world.item.custom.paper.ProfessionContractItem;
import net.sievert.jolcraft.world.item.custom.paper.QuillItem;
import net.sievert.jolcraft.world.item.custom.paper.SignedContractItem;
import net.sievert.jolcraft.world.item.custom.paper.WrittenContractItem;
import net.sievert.jolcraft.world.item.custom.scrapper.*;
import net.sievert.jolcraft.world.item.custom.tablet.ReputationTabletItem;
import net.sievert.jolcraft.world.item.custom.tool.ArtisanHammerItem;
import net.sievert.jolcraft.world.item.custom.tool.ChiselItem;
import net.sievert.jolcraft.world.item.custom.tool.PestleItem;
import net.sievert.jolcraft.world.item.custom.tool.SpannerItem;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipBlockItem;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.food.JolCraftFoodProperties;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import net.sievert.jolcraft.world.item.material.tool.JolCraftToolMaterials;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.item.util.rarity.JolCraftEnumParams;

import java.util.List;

public final class JolCraftItems {

    private JolCraftItems(){}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JolCraft.MOD_ID);

    //Core Items
    public static final DeferredItem<Item> DEV_KEY = ITEMS.registerItem(
            JolCraftItemIds.DEV_KEY,
            props -> new SimpleTooltipItem(props.rarity(Rarity.EPIC).stacksTo(1), JolCraftItemIds.DEV_KEY)
    );

    public static final DeferredItem<Item> GOLD_COIN = ITEMS.registerItem(
            JolCraftItemIds.GOLD_COIN,
            Item::new, new Item.Properties().rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> COIN_POUCH = ITEMS.registerItem(
            JolCraftItemIds.COIN_POUCH,
            CoinPouchItem::new,
            new Item.Properties()
                    .stacksTo(1)
    );

    public static final DeferredItem<Item> DWARVEN_LEXICON =
            ITEMS.registerItem(JolCraftItemIds.DWARVEN_LEXICON, DwarvenLexiconItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_LEXICON =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, AncientDwarvenLexiconItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<BlockItem> STRONGBOX_ITEM = JolCraftItems.ITEMS.registerItem(JolCraftItemIds.STRONGBOX,
            props -> new StrongboxItem(JolCraftBlocks.STRONGBOX.get(), props
                    .stacksTo(1)
                    .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY))
    );

    public static final DeferredItem<Item> LOCKPICK = ITEMS.registerItem(
            JolCraftItemIds.LOCKPICK,
            props -> new SimpleTooltipItem(props, JolCraftItemIds.LOCKPICK)
    );

    public static final DeferredItem<Item> EMPTY_DEEPSLATE_COMPASS = ITEMS.registerItem(JolCraftItemIds.EMPTY_DEEPSLATE_COMPASS,
            Item::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> DEEPSLATE_COMPASS = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_COMPASS,
            DeepslateCompassItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> DEEPSLATE_COMPASS_DIAL = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_COMPASS_DIAL,
            DeepslateCompassDialItem::new, new Item.Properties().stacksTo(1));

    //Materials, Armors, Trims, Tools and Weapons
    public static final DeferredItem<Item> IMPURE_MITHRIL = ITEMS.registerItem(JolCraftItemIds.IMPURE_MITHRIL,
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> PURE_MITHRIL = ITEMS.registerItem(JolCraftItemIds.PURE_MITHRIL,
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> MITHRIL_INGOT = ITEMS.registerItem(
            JolCraftItemIds.MITHRIL_INGOT,
            props -> new SimpleTooltipItem(props.fireResistant().rarity(Rarity.RARE), JolCraftTrimIds.TRIM_MATERIAL)
    );

    public static final DeferredItem<Item> MITHRIL_NUGGET = ITEMS.registerItem(JolCraftItemIds.MITHRIL_NUGGET,
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> MITHRIL_CHAINWEAVE = ITEMS.registerItem(JolCraftItemIds.MITHRIL_CHAINWEAVE,
            Item::new, new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> MITHRIL_SWORD = ITEMS.registerItem(JolCraftItemIds.MITHRIL_SWORD,
            (properties) -> new SwordItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL)
                    , 3.0F, -2.4F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_WARHAMMER = ITEMS.registerItem(JolCraftItemIds.MITHRIL_WARHAMMER,
            (properties) -> new SwordItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL)
                    , 13.0F, -3.5F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_PICKAXE = ITEMS.registerItem(JolCraftItemIds.MITHRIL_PICKAXE,
            (properties) -> new PickaxeItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL)
                    , 1.0F, -2.8F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<ShovelItem> MITHRIL_SHOVEL = ITEMS.registerItem(JolCraftItemIds.MITHRIL_SHOVEL,
            (properties) -> new ShovelItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL)
                    , 1.5F, -3.0F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<AxeItem> MITHRIL_AXE = ITEMS.registerItem(JolCraftItemIds.MITHRIL_AXE,
            (properties) -> new AxeItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL)
                    , 5.0F, -3.0F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<HoeItem> MITHRIL_HOE = ITEMS.registerItem(JolCraftItemIds.MITHRIL_HOE,
            (properties) -> new HoeItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL)
                    , -2.0F, -1.0F, properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MITHRIL_HELMET = ITEMS.registerItem(
            JolCraftItemIds.MITHRIL_HELMET,
            props -> new MithrilArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.MITHRIL),
                    ArmorType.HELMET,
                    props.fireResistant().rarity(Rarity.RARE)
            )
    );

    public static final DeferredItem<Item> MITHRIL_CHESTPLATE = ITEMS.registerItem(
            JolCraftItemIds.MITHRIL_CHESTPLATE,
            props -> new MithrilArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.MITHRIL),
                    ArmorType.CHESTPLATE,
                    props.fireResistant().rarity(Rarity.RARE)
            )
    );

    public static final DeferredItem<Item> MITHRIL_LEGGINGS = ITEMS.registerItem(
            JolCraftItemIds.MITHRIL_LEGGINGS,
            props -> new MithrilArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.MITHRIL),
                    ArmorType.LEGGINGS,
                    props.fireResistant().rarity(Rarity.RARE)
            )
    );

    public static final DeferredItem<Item> MITHRIL_BOOTS = ITEMS.registerItem(
            JolCraftItemIds.MITHRIL_BOOTS,
            props -> new MithrilArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.MITHRIL),
                    ArmorType.BOOTS,
                    props.fireResistant().rarity(Rarity.RARE)
            )
    );

    public static final DeferredItem<Item> DEEPSLATE_PLATE = ITEMS.registerItem(
            JolCraftItemIds.DEEPSLATE_PLATE,
            props -> new SimpleTooltipItem(props, JolCraftTrimIds.TRIM_MATERIAL)
    );

    public static final DeferredItem<Item> DEEPSLATE_ROD = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_ROD,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> DEEPSLATE_SWORD = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_SWORD,
            (properties) -> new SwordItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), 3.0F, -2.4F, properties));

    public static final DeferredItem<Item> DEEPSLATE_WARHAMMER = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_WARHAMMER,
            (properties) -> new SwordItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), 13.0F, -3.5F, properties));

    public static final DeferredItem<Item> DEEPSLATE_PICKAXE = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_PICKAXE,
            (properties) -> new PickaxeItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), 1.0F, -2.8F, properties));

    public static final DeferredItem<ShovelItem> DEEPSLATE_SHOVEL = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_SHOVEL,
            (properties) -> new ShovelItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), 1.5F, -3.0F, properties));

    public static final DeferredItem<AxeItem> DEEPSLATE_AXE = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_AXE,
            (properties) -> new AxeItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), 6.0F, -3.1F, properties));

    public static final DeferredItem<HoeItem> DEEPSLATE_HOE = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_HOE,
            (properties) -> new HoeItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), -2.0F, -1.0F, properties));

    public static final DeferredItem<Item> DEEPSLATE_HELMET = ITEMS.registerItem(
            JolCraftItemIds.DEEPSLATE_HELMET,
            props -> new DeepslateArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.DEEPSLATE),
                    ArmorType.HELMET,
                    props
            )
    );

    public static final DeferredItem<Item> DEEPSLATE_CHESTPLATE = ITEMS.registerItem(
            JolCraftItemIds.DEEPSLATE_CHESTPLATE,
            props -> new DeepslateArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.DEEPSLATE),
                    ArmorType.CHESTPLATE,
                    props
            )
    );

    public static final DeferredItem<Item> DEEPSLATE_LEGGINGS = ITEMS.registerItem(
            JolCraftItemIds.DEEPSLATE_LEGGINGS,
            props -> new DeepslateArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.DEEPSLATE),
                    ArmorType.LEGGINGS,
                    props
            )
    );

    public static final DeferredItem<Item> DEEPSLATE_BOOTS = ITEMS.registerItem(
            JolCraftItemIds.DEEPSLATE_BOOTS,
            props -> new DeepslateArmorItem(
                    JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.DEEPSLATE),
                    ArmorType.BOOTS,
                    props
            )
    );

    // -------------------------------------------------------------------------
    // Armor sets
    // -------------------------------------------------------------------------

    public static final JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>> DEEPSLATE_ARMOR_SET =
            JolCraftEquipmentHelper.armorSet(
                    DEEPSLATE_HELMET,
                    DEEPSLATE_CHESTPLATE,
                    DEEPSLATE_LEGGINGS,
                    DEEPSLATE_BOOTS
            );

    public static final JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>> MITHRIL_ARMOR_SET =
            JolCraftEquipmentHelper.armorSet(
                    MITHRIL_HELMET,
                    MITHRIL_CHESTPLATE,
                    MITHRIL_LEGGINGS,
                    MITHRIL_BOOTS
            );

    public static final List<JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>>> ARMOR_SETS = List.of(
            DEEPSLATE_ARMOR_SET,
            MITHRIL_ARMOR_SET
    );

    public static final DeferredItem<Item> FORGE_ARMOR_TRIM_SMITHING_TEMPLATE = ITEMS.registerItem(JolCraftItemIds.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE,
            SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Animal-related
    public static final DeferredItem<Item> MUFFHORN_FUR = ITEMS.registerItem(JolCraftItemIds.MUFFHORN_FUR,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> MUFFHORN_MILK_BUCKET = ITEMS.registerItem(JolCraftItemIds.MUFFHORN_MILK_BUCKET,
            Item::new, new Item.Properties().craftRemainder(Items.BUCKET).component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET).usingConvertsTo(Items.BUCKET).stacksTo(1));

    //Alchemy
    public static final DeferredItem<BlockItem> DEEPSLATE_MORTAR_ITEM = JolCraftItems.ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_MORTAR,
            properties -> new BlockItem(JolCraftBlocks.DEEPSLATE_MORTAR.get(), properties.stacksTo(3)));

    public static final DeferredItem<PestleItem> DEEPSLATE_PESTLE = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_PESTLE,
            (properties) -> new PestleItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), properties));

    public static final DeferredItem<PestleItem> MITHRIL_PESTLE = ITEMS.registerItem(JolCraftItemIds.MITHRIL_PESTLE,
            (properties) -> new PestleItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL), properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> INVERIX = ITEMS.registerItem(JolCraftItemIds.INVERIX,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> AEGISCORE_DUST = ITEMS.registerItem(JolCraftItemIds.AEGISCORE_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> ASHFANG_DUST = ITEMS.registerItem(JolCraftItemIds.ASHFANG_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> DEEPMARROW_DUST = ITEMS.registerItem(JolCraftItemIds.DEEPMARROW_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> EARTHBLOOD_DUST = ITEMS.registerItem(JolCraftItemIds.EARTHBLOOD_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> EMBERGLASS_DUST = ITEMS.registerItem(JolCraftItemIds.EMBERGLASS_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> FROSTVEIN_DUST = ITEMS.registerItem(JolCraftItemIds.FROSTVEIN_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> GRIMSTONE_DUST = ITEMS.registerItem(JolCraftItemIds.GRIMSTONE_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> IRONHEART_DUST = ITEMS.registerItem(JolCraftItemIds.IRONHEART_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> LUMIERE_DUST = ITEMS.registerItem(JolCraftItemIds.LUMIERE_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> MOONSHARD_DUST = ITEMS.registerItem(JolCraftItemIds.MOONSHARD_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> RUSTAGATE_DUST = ITEMS.registerItem(JolCraftItemIds.RUSTAGATE_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> SKYBURROW_DUST = ITEMS.registerItem(JolCraftItemIds.SKYBURROW_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> SUNGLEAM_DUST = ITEMS.registerItem(JolCraftItemIds.SUNGLEAM_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> VERDANITE_DUST = ITEMS.registerItem(JolCraftItemIds.VERDANITE_DUST,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> WOECRYSTAL_DUST = ITEMS.registerItem(JolCraftItemIds.WOECRYSTAL_DUST,
            Item::new, new Item.Properties());

    //Bounty
    public static final DeferredItem<Item> PARCHMENT = ITEMS.registerSimpleItem(JolCraftItemIds.PARCHMENT);

    public static final DeferredItem<Item> BOUNTY = ITEMS.registerItem(JolCraftItemIds.BOUNTY,
            BountyItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BOUNTY_CRATE = ITEMS.registerItem(JolCraftItemIds.BOUNTY_CRATE,
            BountyCrateItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> RESTOCK_CRATE = ITEMS.registerItem(JolCraftItemIds.RESTOCK_CRATE,
            RestockCrateItem::new, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> REROLL_CRATE = ITEMS.registerItem(JolCraftItemIds.REROLL_CRATE,
            RerollCrateItem::new, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    //Contracts and Associated Items
    public static final DeferredItem<Item> CONTRACT_BLANK = ITEMS.registerItem(JolCraftItemIds.CONTRACT_BLANK,
            Item::new, new Item.Properties());

    public static final DeferredItem<Item> CONTRACT_WRITTEN = ITEMS.registerItem(JolCraftItemIds.CONTRACT_WRITTEN,
            WrittenContractItem::new, new Item.Properties());

    public static final DeferredItem<Item> CONTRACT_SIGNED = ITEMS.registerItem(JolCraftItemIds.CONTRACT_SIGNED,
            SignedContractItem::new, new Item.Properties());

    public static final DeferredItem<Item> GUILD_SIGIL = ITEMS.registerItem(
            JolCraftItemIds.GUILD_SIGIL,
            props -> new SimpleTooltipItem(props, JolCraftItemIds.GUILD_SIGIL)
    );

    public static final DeferredItem<Item> CONTRACT_GUILDMASTER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_GUILDMASTER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 1
    public static final DeferredItem<Item> CONTRACT_MERCHANT = ITEMS.registerItem(JolCraftItemIds.CONTRACT_MERCHANT,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_HISTORIAN = ITEMS.registerItem(JolCraftItemIds.CONTRACT_HISTORIAN,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_SCRAPPER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_SCRAPPER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 2
    public static final DeferredItem<Item> CONTRACT_GUARD = ITEMS.registerItem(JolCraftItemIds.CONTRACT_GUARD,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_BREWMASTER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_BREWMASTER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_KEEPER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_KEEPER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 3
    public static final DeferredItem<Item> CONTRACT_MINER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_MINER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_EXPLORER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_EXPLORER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_ALCHEMIST = ITEMS.registerItem(JolCraftItemIds.CONTRACT_ALCHEMIST,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 4
    public static final DeferredItem<Item> CONTRACT_ARCANIST = ITEMS.registerItem(JolCraftItemIds.CONTRACT_ARCANIST,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_PRIEST = ITEMS.registerItem(JolCraftItemIds.CONTRACT_PRIEST,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_ARTISAN = ITEMS.registerItem(JolCraftItemIds.CONTRACT_ARTISAN,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    //Tier 5
    public static final DeferredItem<Item> CONTRACT_CHAMPION = ITEMS.registerItem(JolCraftItemIds.CONTRACT_CHAMPION,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_BLACKSMITH = ITEMS.registerItem(JolCraftItemIds.CONTRACT_BLACKSMITH,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> CONTRACT_SMELTER = ITEMS.registerItem(JolCraftItemIds.CONTRACT_SMELTER,
            ProfessionContractItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> QUILL_EMPTY =
            ITEMS.registerItem(JolCraftItemIds.QUILL_EMPTY,
                    props -> new QuillItem(props.stacksTo(16), JolCraftLanguageKeys.TOOLTIP_QUILL_EMPTY));

    public static final DeferredItem<Item> QUILL_SMALL =
            ITEMS.registerItem(JolCraftItemIds.QUILL_SMALL,
                    props -> new QuillItem(props.craftRemainder(JolCraftItems.QUILL_EMPTY.get()).stacksTo(1), JolCraftLanguageKeys.TOOLTIP_QUILL));

    public static final DeferredItem<Item> QUILL_HALF =
            ITEMS.registerItem(JolCraftItemIds.QUILL_HALF,
                    props -> new QuillItem(props.craftRemainder(JolCraftItems.QUILL_SMALL.get()).stacksTo(1), JolCraftLanguageKeys.TOOLTIP_QUILL));

    public static final DeferredItem<Item> QUILL_FULL =
            ITEMS.registerItem(JolCraftItemIds.QUILL_FULL,
                    props -> new QuillItem(props.craftRemainder(JolCraftItems.QUILL_HALF.get()).stacksTo(1), JolCraftLanguageKeys.TOOLTIP_QUILL_FULL));

    //Eggs

    public static final DeferredItem<Item> DWARF_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF.get(), properties));

    public static final DeferredItem<Item> DWARF_GUILDMASTER_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_GUILDMASTER_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_GUILDMASTER.get(), properties));

    public static final DeferredItem<Item> DWARF_HISTORIAN_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_HISTORIAN_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_HISTORIAN.get(), properties));

    public static final DeferredItem<Item> DWARF_MERCHANT_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_MERCHANT_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_MERCHANT.get(), properties));

    public static final DeferredItem<Item> DWARF_SCRAPPER_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_SCRAPPER_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_SCRAPPER.get(), properties));

    public static final DeferredItem<Item> DWARF_BREWMASTER_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_BREWMASTER_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_BREWMASTER.get(), properties));

    public static final DeferredItem<Item> DWARF_GUARD_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_GUARD_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_GUARD.get(), properties));

    public static final DeferredItem<Item> DWARF_KEEPER_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_KEEPER_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_KEEPER.get(), properties));

    public static final DeferredItem<Item> DWARF_ARTISAN_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_ARTISAN_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_ARTISAN.get(), properties));

    public static final DeferredItem<Item> DWARF_EXPLORER_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_EXPLORER_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_EXPLORER.get(), properties));

    public static final DeferredItem<Item> DWARF_MINER_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_MINER_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_MINER.get(), properties));

    public static final DeferredItem<Item> DWARF_ALCHEMIST_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_ALCHEMIST_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_ALCHEMIST.get(), properties));

    public static final DeferredItem<Item> DWARF_ARCANIST_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_ARCANIST_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_ARCANIST.get(), properties));

    public static final DeferredItem<Item> DWARF_PRIEST_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.DWARF_PRIEST_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.DWARF_PRIEST.get(), properties));

    public static final DeferredItem<Item> MUFFHORN_SPAWN_EGG = ITEMS.registerItem(JolCraftItemIds.MUFFHORN_SPAWN_EGG,
            (properties) -> new SpawnEggItem(JolCraftEntities.MUFFHORN.get(), properties));

    //Gems

    public static final DeferredItem<ArtisanHammerItem> DEEPSLATE_ARTISAN_HAMMER = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_ARTISAN_HAMMER,
            (properties) -> new ArtisanHammerItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), properties));

    public static final DeferredItem<ArtisanHammerItem> MITHRIL_ARTISAN_HAMMER = ITEMS.registerItem(JolCraftItemIds.MITHRIL_ARTISAN_HAMMER,
            (properties) -> new ArtisanHammerItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL), properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<ChiselItem> DEEPSLATE_CHISEL = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_CHISEL,
            (properties) -> new ChiselItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.DEEPSLATE), properties));

    public static final DeferredItem<ChiselItem> MITHRIL_CHISEL = ITEMS.registerItem(JolCraftItemIds.MITHRIL_CHISEL,
            (properties) -> new ChiselItem(JolCraftToolMaterials.toolMaterial(JolCraftMaterials.Material.MITHRIL), properties.fireResistant().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> GEODE_SMALL = ITEMS.registerItem(
            JolCraftItemIds.GEODE_SMALL,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_GEODE)
    );

    public static final DeferredItem<Item> GEODE_MEDIUM = ITEMS.registerItem(
            JolCraftItemIds.GEODE_MEDIUM,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_GEODE)
    );

    public static final DeferredItem<Item> GEODE_LARGE = ITEMS.registerItem(
            JolCraftItemIds.GEODE_LARGE,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_GEODE)
    );

    public static final DeferredItem<Item> AEGISCORE = ITEMS.registerItem(
            JolCraftItemIds.AEGISCORE, UncutGemItem::new
    );

    public static final DeferredItem<Item> ASHFANG = ITEMS.registerItem(
            JolCraftItemIds.ASHFANG, UncutGemItem::new
    );

    public static final DeferredItem<Item> DEEPMARROW = ITEMS.registerItem(
            JolCraftItemIds.DEEPMARROW, UncutGemItem::new
    );

    public static final DeferredItem<Item> EARTHBLOOD = ITEMS.registerItem(
            JolCraftItemIds.EARTHBLOOD, UncutGemItem::new
    );

    public static final DeferredItem<Item> EMBERGLASS = ITEMS.registerItem(
            JolCraftItemIds.EMBERGLASS, UncutGemItem::new
    );

    public static final DeferredItem<Item> FROSTVEIN = ITEMS.registerItem(
            JolCraftItemIds.FROSTVEIN, UncutGemItem::new
    );

    public static final DeferredItem<Item> GRIMSTONE = ITEMS.registerItem(
            JolCraftItemIds.GRIMSTONE, UncutGemItem::new
    );

    public static final DeferredItem<Item> IRONHEART = ITEMS.registerItem(
            JolCraftItemIds.IRONHEART, UncutGemItem::new
    );

    public static final DeferredItem<Item> LUMIERE = ITEMS.registerItem(
            JolCraftItemIds.LUMIERE, UncutGemItem::new
    );

    public static final DeferredItem<Item> MOONSHARD = ITEMS.registerItem(
            JolCraftItemIds.MOONSHARD, UncutGemItem::new
    );

    public static final DeferredItem<Item> RUSTAGATE = ITEMS.registerItem(
            JolCraftItemIds.RUSTAGATE, UncutGemItem::new
    );

    public static final DeferredItem<Item> SKYBURROW = ITEMS.registerItem(
            JolCraftItemIds.SKYBURROW, UncutGemItem::new
    );

    public static final DeferredItem<Item> SUNGLEAM = ITEMS.registerItem(
            JolCraftItemIds.SUNGLEAM, UncutGemItem::new
    );

    public static final DeferredItem<Item> VERDANITE = ITEMS.registerItem(
            JolCraftItemIds.VERDANITE, UncutGemItem::new
    );

    public static final DeferredItem<Item> WOECRYSTAL = ITEMS.registerItem(
            JolCraftItemIds.WOECRYSTAL, UncutGemItem::new
    );

    public static final DeferredItem<Item> AEGISCORE_CUT  = registerCutGem(JolCraftItemIds.AEGISCORE,  Attributes.ARMOR_TOUGHNESS, 0.5,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> ASHFANG_CUT    = registerCutGem(JolCraftItemIds.ASHFANG,    JolCraftAttributes.ATTACK_DAMAGE_INCREASE, 0.05,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> DEEPMARROW_CUT = registerCutGem(JolCraftItemIds.DEEPMARROW, JolCraftAttributes.XP_INCREASE, 0.125, AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> EARTHBLOOD_CUT = registerCutGem(JolCraftItemIds.EARTHBLOOD, Attributes.MINING_EFFICIENCY, 0.05,  AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public static final DeferredItem<Item> EMBERGLASS_CUT = registerCutGem(JolCraftItemIds.EMBERGLASS, Attributes.MAX_HEALTH, 2.0,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> FROSTVEIN_CUT  = registerCutGem(JolCraftItemIds.FROSTVEIN,  JolCraftAttributes.SLOW_RESISTANCE, 0.2,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> GRIMSTONE_CUT  = registerCutGem(JolCraftItemIds.GRIMSTONE,  Attributes.ATTACK_SPEED, 0.05,  AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    public static final DeferredItem<Item> IRONHEART_CUT  = registerCutGem(JolCraftItemIds.IRONHEART,  JolCraftAttributes.ARMOR_INCREASE, 0.05,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> LUMIERE_CUT    = registerCutGem(JolCraftItemIds.LUMIERE,    JolCraftAttributes.RADIANT, 0.25,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> MOONSHARD_CUT  = registerCutGem(JolCraftItemIds.MOONSHARD,  JolCraftAttributes.MOVEMENT_SPEED_NIGHT_INCREASE, 0.05,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> RUSTAGATE_CUT  = registerCutGem(JolCraftItemIds.RUSTAGATE,  JolCraftAttributes.ARMOR_UNBREAKING, 0.075,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> SKYBURROW_CUT  = registerCutGem(JolCraftItemIds.SKYBURROW,  JolCraftAttributes.MOVEMENT_SPEED_DAY_INCREASE, 0.05,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> SUNGLEAM_CUT   = registerCutGem(JolCraftItemIds.SUNGLEAM,   JolCraftAttributes.CHEST_LOOT_INCREASE, 0.1,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> VERDANITE_CUT  = registerCutGem(JolCraftItemIds.VERDANITE,  JolCraftAttributes.CROP_LOOT_INCREASE, 0.25,  AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredItem<Item> WOECRYSTAL_CUT = registerCutGem(JolCraftItemIds.WOECRYSTAL, JolCraftAttributes.MAGIC_RESISTANCE, 0.1,  AttributeModifier.Operation.ADD_VALUE);

    private static DeferredItem<Item> registerCutGem(
            String gemKey,
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation op
    ) {
        ResourceLocation id = JolCraft.location("gem/" + gemKey);

        ItemAttributeModifiers modifiers = ItemAttributeModifiers.builder()
                .add(attribute, new AttributeModifier(id, amount, op), EquipmentSlotGroup.ARMOR)
                .build();

        return ITEMS.registerItem(
                gemKey + "_cut",
                props -> new CutGemItem(
                        props.component(DataComponents.ATTRIBUTE_MODIFIERS, modifiers)
                )
        );
    }

    //Crops, food and brewing

    public static final DeferredItem<Item> BARLEY_SEEDS = ITEMS.registerItem(JolCraftItemIds.BARLEY_SEEDS,
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.BARLEY_CROP.get(), properties, JolCraftLanguageKeys.TOOLTIP_VANILLA_CROP));

    public static final DeferredItem<Item> BARLEY =
            ITEMS.registerItem(JolCraftItemIds.BARLEY, Item::new,
                    new Item.Properties());

    public static final DeferredItem<Item> BARLEY_MALT =
            ITEMS.registerItem(JolCraftItemIds.BARLEY_MALT,
                    props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_MALT));

    public static final DeferredItem<Item> ASGARNIAN_SEEDS = ITEMS.registerItem(JolCraftItemIds.ASGARNIAN_SEEDS,
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), properties, JolCraftLanguageKeys.TOOLTIP_HOPS_SEED));

    public static final DeferredItem<Item> ASGARNIAN_HOPS = ITEMS.registerItem(
            JolCraftItemIds.ASGARNIAN_HOPS,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_HOPS)
    );

    public static final DeferredItem<Item> DUSKHOLD_SEEDS = ITEMS.registerItem(JolCraftItemIds.DUSKHOLD_SEEDS,
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), properties, JolCraftLanguageKeys.TOOLTIP_HOPS_SEED));

    public static final DeferredItem<Item> DUSKHOLD_HOPS = ITEMS.registerItem(
            JolCraftItemIds.DUSKHOLD_HOPS,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_HOPS)
    );

    public static final DeferredItem<Item> KRANDONIAN_SEEDS = ITEMS.registerItem(JolCraftItemIds.KRANDONIAN_SEEDS,
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), properties, JolCraftLanguageKeys.TOOLTIP_HOPS_SEED));

    public static final DeferredItem<Item> KRANDONIAN_HOPS = ITEMS.registerItem(
            JolCraftItemIds.KRANDONIAN_HOPS,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_HOPS)
    );

    public static final DeferredItem<Item> YANILLIAN_SEEDS = ITEMS.registerItem(JolCraftItemIds.YANILLIAN_SEEDS,
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), properties, JolCraftLanguageKeys.TOOLTIP_HOPS_SEED));

    public static final DeferredItem<Item> YANILLIAN_HOPS = ITEMS.registerItem(
            JolCraftItemIds.YANILLIAN_HOPS,
            props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_HOPS)
    );

    public static final DeferredItem<Item> YEAST = ITEMS.registerItem(
            JolCraftItemIds.YEAST,
            props -> new SimpleTooltipItem(props.stacksTo(16), JolCraftLanguageKeys.TOOLTIP_YEAST)
    );

    public static final DeferredItem<Item> GLASS_MUG = ITEMS.registerItem(
            JolCraftItemIds.GLASS_MUG,
            props -> new SimpleTooltipItem(props.stacksTo(16), JolCraftLanguageKeys.TOOLTIP_GLASS_MUG)
    );

    public static final DeferredItem<Item> DWARVEN_BREW =
            ITEMS.registerItem(JolCraftItemIds.DWARVEN_BREW,  (properties) -> new DwarvenBrewItem(properties.food(JolCraftFoodProperties.DWARVEN_BREW,
                    JolCraftFoodProperties.DWARVEN_BREW_EFFECT).usingConvertsTo(JolCraftItems.GLASS_MUG.get()).stacksTo(1)));

    public static final DeferredItem<Item> DEEPSLATE_BULBS = ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_BULBS,
            properties -> new SimpleTooltipBlockItem(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(), properties.food(JolCraftFoodProperties.DWARVEN_BREW,
                    JolCraftFoodProperties.DEEPSLATE_BULBS_EFFECT), JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_BULBS));

    //Reputation
    public static final DeferredItem<Item> REPUTATION_TABLET_0 =
            ITEMS.registerItem(JolCraftItemIds.REPUTATION_TABLET_0, ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> REPUTATION_TABLET_1 =
            ITEMS.registerItem(JolCraftItemIds.REPUTATION_TABLET_1, ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> REPUTATION_TABLET_2 =
            ITEMS.registerItem(JolCraftItemIds.REPUTATION_TABLET_2, ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> REPUTATION_TABLET_3 =
            ITEMS.registerItem(JolCraftItemIds.REPUTATION_TABLET_3, ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> REPUTATION_TABLET_4 =
            ITEMS.registerItem(JolCraftItemIds.REPUTATION_TABLET_4, ReputationTabletItem::new,
                    new Item.Properties().stacksTo(1).rarity(JolCraftEnumParams.LEGENDARY_RARITY.getValue()));

    // Tomes
    public static final DeferredItem<Item> DWARVEN_TOME = ITEMS.registerSimpleItem(JolCraftItemIds.DWARVEN_TOME);

    public static final DeferredItem<Item> UNIDENTIFIED_DWARVEN_TOME =
            ITEMS.registerItem(JolCraftItemIds.UNIDENTIFIED_DWARVEN_TOME, properties -> new UnidentifiedDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> DWARVEN_TOME_COMMON =
            ITEMS.registerItem(JolCraftItemIds.DWARVEN_TOME_COMMON, properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> DWARVEN_TOME_UNCOMMON =
            ITEMS.registerItem(JolCraftItemIds.DWARVEN_TOME_UNCOMMON, properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> DWARVEN_TOME_RARE =
            ITEMS.registerItem(JolCraftItemIds.DWARVEN_TOME_RARE, properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> DWARVEN_TOME_EPIC =
            ITEMS.registerItem(JolCraftItemIds.DWARVEN_TOME_EPIC, properties -> new DwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME = ITEMS.registerSimpleItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME);

    public static final DeferredItem<Item> ANCIENT_UNIDENTIFIED_DWARVEN_TOME =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_UNIDENTIFIED_DWARVEN_TOME, properties -> new AncientUnidentifiedTomeItem(properties) {
            }, new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_COMMON =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME_COMMON, properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_UNCOMMON =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME_UNCOMMON, properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_RARE =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME_RARE, properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_EPIC =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME_EPIC, properties -> new AncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> LEGENDARY_PAGE = ITEMS.registerItem(JolCraftItemIds.LEGENDARY_PAGE,
            Item::new, new Item.Properties().rarity(JolCraftEnumParams.LEGENDARY_RARITY.getValue()));

    public static final DeferredItem<Item> LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME =
            ITEMS.registerItem(JolCraftItemIds.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME, properties -> new LegendaryAncientUnidentifiedTomeItem(properties) {
            }, new Item.Properties().stacksTo(16).rarity(JolCraftEnumParams.LEGENDARY_RARITY.getValue()));

    public static final DeferredItem<Item> ANCIENT_DWARVEN_TOME_LEGENDARY =
            ITEMS.registerItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME_LEGENDARY, properties -> new LegendaryAncientDwarvenTomeItem(properties) {
            }, new Item.Properties().stacksTo(1).rarity(JolCraftEnumParams.LEGENDARY_RARITY.getValue()));

    //Tools
    public static final DeferredItem<Item> COPPER_SPANNER =
            ITEMS.registerItem(JolCraftItemIds.COPPER_SPANNER,
                    SpannerItem::new,
                    new Item.Properties().durability(16).stacksTo(1).enchantable(10).repairable(Items.COPPER_INGOT)
            );

    public static final DeferredItem<Item> IRON_SPANNER =
            ITEMS.registerItem(JolCraftItemIds.IRON_SPANNER,
                    SpannerItem::new,
                    new Item.Properties().durability(64).stacksTo(1).enchantable(10).repairable(Items.IRON_INGOT)
            );

    //Scrap
    public static final DeferredItem<Item> SCRAP = ITEMS.registerSimpleItem(JolCraftItemIds.SCRAP);

    public static final DeferredItem<Item> SCRAP_HEAP = ITEMS.registerSimpleItem(JolCraftItemIds.SCRAP_HEAP);

    public static final DeferredItem<Item> BROKEN_PICKAXE =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_PICKAXE, SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_AMULET =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_AMULET, SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_BELT =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_BELT, SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_COINS =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_COINS, SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> DEEPSLATE_MUG =
            ITEMS.registerItem(JolCraftItemIds.DEEPSLATE_MUG, SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> EXPIRED_POTION =
            ITEMS.registerItem(JolCraftItemIds.EXPIRED_POTION, SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> INGOT_MOULD =
            ITEMS.registerItem(JolCraftItemIds.INGOT_MOULD, SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> MITHRIL_SCRAP =
            ITEMS.registerItem(JolCraftItemIds.MITHRIL_SCRAP, SalvageItem::new, new Item.Properties().rarity(Rarity.RARE));

    public static final DeferredItem<Item> OLD_FABRIC =
            ITEMS.registerItem(JolCraftItemIds.OLD_FABRIC, SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> RUSTY_TONGS =
            ITEMS.registerItem(JolCraftItemIds.RUSTY_TONGS, SalvageItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BROKEN_MITHRIL_SWORD =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_MITHRIL_SWORD, SalvageItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> BROKEN_TABLET =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_TABLET, SalvageItem::new, new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> BROKEN_DEEPSLATE_PLATES =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_DEEPSLATE_PLATES, SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> BROKEN_MITHRIL_PLATE =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_MITHRIL_PLATE, SalvageItem::new, new Item.Properties().rarity(Rarity.RARE));

    public static final DeferredItem<Item> BROKEN_DEEPSLATE_GEAR =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_DEEPSLATE_GEAR, SalvageItem::new, new Item.Properties());

    public static final DeferredItem<Item> BROKEN_DEEPSLATE_PICKAXE_HEAD =
            ITEMS.registerItem(JolCraftItemIds.BROKEN_DEEPSLATE_PICKAXE_HEAD, SalvageItem::new, new Item.Properties());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}