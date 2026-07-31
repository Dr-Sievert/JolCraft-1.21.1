package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.tool.ArtisanHammerItem;
import net.sievert.jolcraft.world.item.custom.tool.ChiselItem;
import net.sievert.jolcraft.world.item.custom.tool.PestleItem;
import net.sievert.jolcraft.world.item.custom.tool.SpannerItem;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.tool.JolCraftToolMaterials;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.UnaryOperator;

@SuppressWarnings("SameParameterValue")
public final class JolCraftEquipmentItems {

    private JolCraftEquipmentItems() {}

    private static final UnaryOperator<Item.Properties> MITHRIL = JolCraftItemRegistryHelper::mithrilProperties;

    // -------------------------------------------------------------------------
    // Mithril
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerMithrilSword() {
        return registerSword(JolCraftItemIds.MITHRIL_SWORD, JolCraftMaterials.Material.MITHRIL, 3.0F, -2.4F, MITHRIL);
    }

    public static DeferredItem<Item> registerMithrilWarhammer() {
        return registerSword(JolCraftItemIds.MITHRIL_WARHAMMER, JolCraftMaterials.Material.MITHRIL, 13.0F, -3.5F, MITHRIL);
    }

    public static DeferredItem<Item> registerMithrilPickaxe() {
        return registerPickaxe(JolCraftItemIds.MITHRIL_PICKAXE, JolCraftMaterials.Material.MITHRIL, 1.0F, -2.8F, MITHRIL);
    }

    public static DeferredItem<ShovelItem> registerMithrilShovel() {
        return registerShovel(JolCraftItemIds.MITHRIL_SHOVEL, JolCraftMaterials.Material.MITHRIL, 1.5F, -3.0F, MITHRIL);
    }

    public static DeferredItem<AxeItem> registerMithrilAxe() {
        return registerAxe(JolCraftItemIds.MITHRIL_AXE, JolCraftMaterials.Material.MITHRIL, 5.0F, -3.0F, MITHRIL);
    }

    public static DeferredItem<HoeItem> registerMithrilHoe() {
        return registerHoe(JolCraftItemIds.MITHRIL_HOE, JolCraftMaterials.Material.MITHRIL, -4.0F, 0.0F, MITHRIL);
    }

    // -------------------------------------------------------------------------
    // Deepslate
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerDeepslateSword() {
        return registerSword(JolCraftItemIds.DEEPSLATE_SWORD, JolCraftMaterials.Material.DEEPSLATE, 3.0F, -2.4F);
    }

    public static DeferredItem<Item> registerDeepslateWarhammer() {
        return registerSword(JolCraftItemIds.DEEPSLATE_WARHAMMER, JolCraftMaterials.Material.DEEPSLATE, 13.0F, -3.5F);
    }

    public static DeferredItem<Item> registerDeepslatePickaxe() {
        return registerPickaxe(JolCraftItemIds.DEEPSLATE_PICKAXE, JolCraftMaterials.Material.DEEPSLATE, 1.0F, -2.8F);
    }

    public static DeferredItem<ShovelItem> registerDeepslateShovel() {
        return registerShovel(JolCraftItemIds.DEEPSLATE_SHOVEL, JolCraftMaterials.Material.DEEPSLATE, 1.5F, -3.0F);
    }

    public static DeferredItem<AxeItem> registerDeepslateAxe() {
        return registerAxe(JolCraftItemIds.DEEPSLATE_AXE, JolCraftMaterials.Material.DEEPSLATE, 6.0F, -3.1F);
    }

    public static DeferredItem<HoeItem> registerDeepslateHoe() {
        return registerHoe(JolCraftItemIds.DEEPSLATE_HOE, JolCraftMaterials.Material.DEEPSLATE, -1.0F, -2.0F);
    }

    // -------------------------------------------------------------------------
    // Utility tools
    // -------------------------------------------------------------------------

    public static DeferredItem<ArtisanHammerItem> registerDeepslateArtisanHammer() {
        return registerArtisanHammer(JolCraftItemIds.DEEPSLATE_ARTISAN_HAMMER, JolCraftMaterials.Material.DEEPSLATE);
    }

    public static DeferredItem<ArtisanHammerItem> registerMithrilArtisanHammer() {
        return registerArtisanHammer(JolCraftItemIds.MITHRIL_ARTISAN_HAMMER, JolCraftMaterials.Material.MITHRIL, MITHRIL);
    }

    public static DeferredItem<ChiselItem> registerDeepslateChisel() {
        return registerChisel(JolCraftItemIds.DEEPSLATE_CHISEL, JolCraftMaterials.Material.DEEPSLATE);
    }

    public static DeferredItem<ChiselItem> registerMithrilChisel() {
        return registerChisel(JolCraftItemIds.MITHRIL_CHISEL, JolCraftMaterials.Material.MITHRIL, MITHRIL);
    }

    public static DeferredItem<PestleItem> registerDeepslatePestle() {
        return registerPestle(JolCraftItemIds.DEEPSLATE_PESTLE, JolCraftMaterials.Material.DEEPSLATE);
    }

    public static DeferredItem<PestleItem> registerMithrilPestle() {
        return registerPestle(JolCraftItemIds.MITHRIL_PESTLE, JolCraftMaterials.Material.MITHRIL, MITHRIL);
    }

    public static DeferredItem<SpannerItem> registerDeepslateSpanner() {
        return registerSpanner(JolCraftItemIds.DEEPSLATE_SPANNER, JolCraftMaterials.Material.DEEPSLATE);
    }

    public static DeferredItem<SpannerItem> registerMithrilSpanner() {
        return registerSpanner(JolCraftItemIds.MITHRIL_SPANNER, JolCraftMaterials.Material.MITHRIL, MITHRIL);
    }

    // -------------------------------------------------------------------------
    // Canonical combat/tool helpers
    // -------------------------------------------------------------------------

    private static DeferredItem<Item> registerSword(String id, JolCraftMaterials.Material material, float attackDamage, float attackSpeed) {
        return registerSword(id, material, attackDamage, attackSpeed, UnaryOperator.identity());
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
                        properties.apply(props).attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed))
                )
        );
    }

    private static DeferredItem<Item> registerPickaxe(String id, JolCraftMaterials.Material material, float attackDamage, float attackSpeed) {
        return registerPickaxe(id, material, attackDamage, attackSpeed, UnaryOperator.identity());
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
                        properties.apply(props).attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed))
                )
        );
    }

    private static DeferredItem<ShovelItem> registerShovel(String id, JolCraftMaterials.Material material, float attackDamage, float attackSpeed) {
        return registerShovel(id, material, attackDamage, attackSpeed, UnaryOperator.identity());
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
                        properties.apply(props).attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed))
                )
        );
    }

    private static DeferredItem<AxeItem> registerAxe(String id, JolCraftMaterials.Material material, float attackDamage, float attackSpeed) {
        return registerAxe(id, material, attackDamage, attackSpeed, UnaryOperator.identity());
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
                        properties.apply(props).attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed))
                )
        );
    }

    private static DeferredItem<HoeItem> registerHoe(String id, JolCraftMaterials.Material material, float attackDamage, float attackSpeed) {
        return registerHoe(id, material, attackDamage, attackSpeed, UnaryOperator.identity());
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
                        properties.apply(props).attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed))
                )
        );
    }

    // -------------------------------------------------------------------------
    // Custom tool helpers
    // -------------------------------------------------------------------------

    private static DeferredItem<ArtisanHammerItem> registerArtisanHammer(String id, JolCraftMaterials.Material material) {
        return registerArtisanHammer(id, material, UnaryOperator.identity());
    }

    private static DeferredItem<ArtisanHammerItem> registerArtisanHammer(
            String id,
            JolCraftMaterials.Material material,
            UnaryOperator<Item.Properties> properties
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new ArtisanHammerItem(toolMaterial(material), properties.apply(props))
        );
    }

    private static DeferredItem<ChiselItem> registerChisel(String id, JolCraftMaterials.Material material) {
        return registerChisel(id, material, UnaryOperator.identity());
    }

    private static DeferredItem<ChiselItem> registerChisel(
            String id,
            JolCraftMaterials.Material material,
            UnaryOperator<Item.Properties> properties
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new ChiselItem(toolMaterial(material), properties.apply(props))
        );
    }

    private static DeferredItem<PestleItem> registerPestle(String id, JolCraftMaterials.Material material) {
        return registerPestle(id, material, UnaryOperator.identity());
    }

    private static DeferredItem<PestleItem> registerPestle(
            String id,
            JolCraftMaterials.Material material,
            UnaryOperator<Item.Properties> properties
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new PestleItem(toolMaterial(material), properties.apply(props))
        );
    }

    private static DeferredItem<SpannerItem> registerSpanner(String id, JolCraftMaterials.Material material) {
        return registerSpanner(id, material, UnaryOperator.identity());
    }

    private static DeferredItem<SpannerItem> registerSpanner(
            String id,
            JolCraftMaterials.Material material,
            UnaryOperator<Item.Properties> properties
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new SpannerItem(toolMaterial(material), properties.apply(props))
        );
    }

    private static Tier toolMaterial(JolCraftMaterials.Material material) {
        return JolCraftToolMaterials.toolMaterial(material);
    }
}