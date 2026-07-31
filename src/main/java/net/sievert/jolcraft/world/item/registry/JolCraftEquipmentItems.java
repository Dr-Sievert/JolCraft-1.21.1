package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.tool.ArtisanHammerItem;
import net.sievert.jolcraft.world.item.custom.tool.ChiselItem;
import net.sievert.jolcraft.world.item.custom.tool.PestleItem;
import net.sievert.jolcraft.world.item.custom.tool.SpannerItem;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.tool.JolCraftToolMaterials;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

@SuppressWarnings("SameParameterValue")
public final class JolCraftEquipmentItems {

    private static final UnaryOperator<Item.Properties> MITHRIL =
            JolCraftItemRegistryHelper::mithrilProperties;

    private static final UnaryOperator<Item.Properties> NETHERITE =
            Item.Properties::fireResistant;

    private JolCraftEquipmentItems() {}

    // -------------------------------------------------------------------------
    // Mithril
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerMithrilSword() {
        return registerSword(
                JolCraftItemIds.MITHRIL_SWORD,
                JolCraftMaterials.Material.MITHRIL,
                3.0F,
                -2.4F,
                MITHRIL
        );
    }

    public static DeferredItem<Item> registerMithrilWarhammer() {
        return registerSword(
                JolCraftItemIds.MITHRIL_WARHAMMER,
                JolCraftMaterials.Material.MITHRIL,
                13.0F,
                -3.5F,
                MITHRIL
        );
    }

    public static DeferredItem<Item> registerMithrilPickaxe() {
        return registerPickaxe(
                JolCraftItemIds.MITHRIL_PICKAXE,
                JolCraftMaterials.Material.MITHRIL,
                1.0F,
                -2.8F,
                MITHRIL
        );
    }

    public static DeferredItem<ShovelItem> registerMithrilShovel() {
        return registerShovel(
                JolCraftItemIds.MITHRIL_SHOVEL,
                JolCraftMaterials.Material.MITHRIL,
                1.5F,
                -3.0F,
                MITHRIL
        );
    }

    public static DeferredItem<AxeItem> registerMithrilAxe() {
        return registerAxe(
                JolCraftItemIds.MITHRIL_AXE,
                JolCraftMaterials.Material.MITHRIL,
                5.0F,
                -3.0F,
                MITHRIL
        );
    }

    public static DeferredItem<HoeItem> registerMithrilHoe() {
        return registerHoe(
                JolCraftItemIds.MITHRIL_HOE,
                JolCraftMaterials.Material.MITHRIL,
                -4.0F,
                0.0F,
                MITHRIL
        );
    }

    // -------------------------------------------------------------------------
    // Deepslate
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerDeepslateSword() {
        return registerSword(
                JolCraftItemIds.DEEPSLATE_SWORD,
                JolCraftMaterials.Material.DEEPSLATE,
                3.0F,
                -2.4F
        );
    }

    public static DeferredItem<Item> registerDeepslateWarhammer() {
        return registerSword(
                JolCraftItemIds.DEEPSLATE_WARHAMMER,
                JolCraftMaterials.Material.DEEPSLATE,
                13.0F,
                -3.5F
        );
    }

    public static DeferredItem<Item> registerDeepslatePickaxe() {
        return registerPickaxe(
                JolCraftItemIds.DEEPSLATE_PICKAXE,
                JolCraftMaterials.Material.DEEPSLATE,
                1.0F,
                -2.8F
        );
    }

    public static DeferredItem<ShovelItem> registerDeepslateShovel() {
        return registerShovel(
                JolCraftItemIds.DEEPSLATE_SHOVEL,
                JolCraftMaterials.Material.DEEPSLATE,
                1.5F,
                -3.0F
        );
    }

    public static DeferredItem<AxeItem> registerDeepslateAxe() {
        return registerAxe(
                JolCraftItemIds.DEEPSLATE_AXE,
                JolCraftMaterials.Material.DEEPSLATE,
                6.0F,
                -3.1F
        );
    }

    public static DeferredItem<HoeItem> registerDeepslateHoe() {
        return registerHoe(
                JolCraftItemIds.DEEPSLATE_HOE,
                JolCraftMaterials.Material.DEEPSLATE,
                -1.0F,
                -2.0F
        );
    }

    // -------------------------------------------------------------------------
    // Artisan hammers
    // -------------------------------------------------------------------------

    public static DeferredItem<ArtisanHammerItem> registerWoodenArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.WOODEN_ARTISAN_HAMMER,
                Tiers.WOOD,
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerStoneArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.STONE_ARTISAN_HAMMER,
                Tiers.STONE,
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerIronArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.IRON_ARTISAN_HAMMER,
                Tiers.IRON,
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerGoldenArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.GOLDEN_ARTISAN_HAMMER,
                Tiers.GOLD,
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerDiamondArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.DIAMOND_ARTISAN_HAMMER,
                Tiers.DIAMOND,
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerNetheriteArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.NETHERITE_ARTISAN_HAMMER,
                Tiers.NETHERITE,
                NETHERITE,
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerDeepslateArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.DEEPSLATE_ARTISAN_HAMMER,
                toolMaterial(JolCraftMaterials.Material.DEEPSLATE),
                ArtisanHammerItem::new
        );
    }

    public static DeferredItem<ArtisanHammerItem> registerMithrilArtisanHammer() {
        return registerCustomTool(
                JolCraftItemIds.MITHRIL_ARTISAN_HAMMER,
                toolMaterial(JolCraftMaterials.Material.MITHRIL),
                MITHRIL,
                ArtisanHammerItem::new
        );
    }

    // -------------------------------------------------------------------------
    // Chisels
    // -------------------------------------------------------------------------

    public static DeferredItem<ChiselItem> registerWoodenChisel() {
        return registerCustomTool(
                JolCraftItemIds.WOODEN_CHISEL,
                Tiers.WOOD,
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerStoneChisel() {
        return registerCustomTool(
                JolCraftItemIds.STONE_CHISEL,
                Tiers.STONE,
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerIronChisel() {
        return registerCustomTool(
                JolCraftItemIds.IRON_CHISEL,
                Tiers.IRON,
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerGoldenChisel() {
        return registerCustomTool(
                JolCraftItemIds.GOLDEN_CHISEL,
                Tiers.GOLD,
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerDiamondChisel() {
        return registerCustomTool(
                JolCraftItemIds.DIAMOND_CHISEL,
                Tiers.DIAMOND,
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerNetheriteChisel() {
        return registerCustomTool(
                JolCraftItemIds.NETHERITE_CHISEL,
                Tiers.NETHERITE,
                NETHERITE,
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerDeepslateChisel() {
        return registerCustomTool(
                JolCraftItemIds.DEEPSLATE_CHISEL,
                toolMaterial(JolCraftMaterials.Material.DEEPSLATE),
                ChiselItem::new
        );
    }

    public static DeferredItem<ChiselItem> registerMithrilChisel() {
        return registerCustomTool(
                JolCraftItemIds.MITHRIL_CHISEL,
                toolMaterial(JolCraftMaterials.Material.MITHRIL),
                MITHRIL,
                ChiselItem::new
        );
    }

    // -------------------------------------------------------------------------
    // Spanners
    // -------------------------------------------------------------------------

    public static DeferredItem<SpannerItem> registerWoodenSpanner() {
        return registerCustomTool(
                JolCraftItemIds.WOODEN_SPANNER,
                Tiers.WOOD,
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerStoneSpanner() {
        return registerCustomTool(
                JolCraftItemIds.STONE_SPANNER,
                Tiers.STONE,
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerIronSpanner() {
        return registerCustomTool(
                JolCraftItemIds.IRON_SPANNER,
                Tiers.IRON,
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerGoldenSpanner() {
        return registerCustomTool(
                JolCraftItemIds.GOLDEN_SPANNER,
                Tiers.GOLD,
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerDiamondSpanner() {
        return registerCustomTool(
                JolCraftItemIds.DIAMOND_SPANNER,
                Tiers.DIAMOND,
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerNetheriteSpanner() {
        return registerCustomTool(
                JolCraftItemIds.NETHERITE_SPANNER,
                Tiers.NETHERITE,
                NETHERITE,
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerDeepslateSpanner() {
        return registerCustomTool(
                JolCraftItemIds.DEEPSLATE_SPANNER,
                toolMaterial(JolCraftMaterials.Material.DEEPSLATE),
                SpannerItem::new
        );
    }

    public static DeferredItem<SpannerItem> registerMithrilSpanner() {
        return registerCustomTool(
                JolCraftItemIds.MITHRIL_SPANNER,
                toolMaterial(JolCraftMaterials.Material.MITHRIL),
                MITHRIL,
                SpannerItem::new
        );
    }

    // -------------------------------------------------------------------------
    // Pestles
    // -------------------------------------------------------------------------

    public static DeferredItem<PestleItem> registerWoodenPestle() {
        return registerCustomTool(
                JolCraftItemIds.WOODEN_PESTLE,
                Tiers.WOOD,
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerStonePestle() {
        return registerCustomTool(
                JolCraftItemIds.STONE_PESTLE,
                Tiers.STONE,
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerIronPestle() {
        return registerCustomTool(
                JolCraftItemIds.IRON_PESTLE,
                Tiers.IRON,
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerGoldenPestle() {
        return registerCustomTool(
                JolCraftItemIds.GOLDEN_PESTLE,
                Tiers.GOLD,
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerDiamondPestle() {
        return registerCustomTool(
                JolCraftItemIds.DIAMOND_PESTLE,
                Tiers.DIAMOND,
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerNetheritePestle() {
        return registerCustomTool(
                JolCraftItemIds.NETHERITE_PESTLE,
                Tiers.NETHERITE,
                NETHERITE,
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerDeepslatePestle() {
        return registerCustomTool(
                JolCraftItemIds.DEEPSLATE_PESTLE,
                toolMaterial(JolCraftMaterials.Material.DEEPSLATE),
                PestleItem::new
        );
    }

    public static DeferredItem<PestleItem> registerMithrilPestle() {
        return registerCustomTool(
                JolCraftItemIds.MITHRIL_PESTLE,
                toolMaterial(JolCraftMaterials.Material.MITHRIL),
                MITHRIL,
                PestleItem::new
        );
    }

    // -------------------------------------------------------------------------
    // Canonical combat/tool helpers
    // -------------------------------------------------------------------------

    private static DeferredItem<Item> registerSword(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed
    ) {
        return registerSword(
                id,
                material,
                attackDamage,
                attackSpeed,
                UnaryOperator.identity()
        );
    }

    private static DeferredItem<Item> registerSword(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed,
            UnaryOperator<Item.Properties> properties
    ) {
        Tier tier = toolMaterial(material);

        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new SwordItem(
                        tier,
                        properties.apply(props).attributes(
                                SwordItem.createAttributes(
                                        tier,
                                        attackDamage,
                                        attackSpeed
                                )
                        )
                )
        );
    }

    private static DeferredItem<Item> registerPickaxe(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed
    ) {
        return registerPickaxe(
                id,
                material,
                attackDamage,
                attackSpeed,
                UnaryOperator.identity()
        );
    }

    private static DeferredItem<Item> registerPickaxe(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed,
            UnaryOperator<Item.Properties> properties
    ) {
        Tier tier = toolMaterial(material);

        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new PickaxeItem(
                        tier,
                        properties.apply(props).attributes(
                                DiggerItem.createAttributes(
                                        tier,
                                        attackDamage,
                                        attackSpeed
                                )
                        )
                )
        );
    }

    private static DeferredItem<ShovelItem> registerShovel(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed
    ) {
        return registerShovel(
                id,
                material,
                attackDamage,
                attackSpeed,
                UnaryOperator.identity()
        );
    }

    private static DeferredItem<ShovelItem> registerShovel(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed,
            UnaryOperator<Item.Properties> properties
    ) {
        Tier tier = toolMaterial(material);

        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new ShovelItem(
                        tier,
                        properties.apply(props).attributes(
                                DiggerItem.createAttributes(
                                        tier,
                                        attackDamage,
                                        attackSpeed
                                )
                        )
                )
        );
    }

    private static DeferredItem<AxeItem> registerAxe(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed
    ) {
        return registerAxe(
                id,
                material,
                attackDamage,
                attackSpeed,
                UnaryOperator.identity()
        );
    }

    private static DeferredItem<AxeItem> registerAxe(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed,
            UnaryOperator<Item.Properties> properties
    ) {
        Tier tier = toolMaterial(material);

        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new AxeItem(
                        tier,
                        properties.apply(props).attributes(
                                DiggerItem.createAttributes(
                                        tier,
                                        attackDamage,
                                        attackSpeed
                                )
                        )
                )
        );
    }

    private static DeferredItem<HoeItem> registerHoe(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed
    ) {
        return registerHoe(
                id,
                material,
                attackDamage,
                attackSpeed,
                UnaryOperator.identity()
        );
    }

    private static DeferredItem<HoeItem> registerHoe(
            String id,
            JolCraftMaterials.Material material,
            float attackDamage,
            float attackSpeed,
            UnaryOperator<Item.Properties> properties
    ) {
        Tier tier = toolMaterial(material);

        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new HoeItem(
                        tier,
                        properties.apply(props).attributes(
                                DiggerItem.createAttributes(
                                        tier,
                                        attackDamage,
                                        attackSpeed
                                )
                        )
                )
        );
    }

    // -------------------------------------------------------------------------
    // Custom tool helper
    // -------------------------------------------------------------------------

    private static <T extends Item> DeferredItem<T> registerCustomTool(
            String id,
            Tier tier,
            BiFunction<Tier, Item.Properties, T> factory
    ) {
        return registerCustomTool(
                id,
                tier,
                UnaryOperator.identity(),
                factory
        );
    }

    private static <T extends Item> DeferredItem<T> registerCustomTool(
            String id,
            Tier tier,
            UnaryOperator<Item.Properties> properties,
            BiFunction<Tier, Item.Properties, T> factory
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> factory.apply(
                        tier,
                        properties.apply(props)
                )
        );
    }

    private static Tier toolMaterial(JolCraftMaterials.Material material) {
        return JolCraftToolMaterials.toolMaterial(material);
    }
}