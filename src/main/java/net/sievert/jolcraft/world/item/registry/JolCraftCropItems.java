package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipBlockItem;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Supplier;

public final class JolCraftCropItems {

    private JolCraftCropItems() {}

    // -------------------------------------------------------------------------
    // Barley
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerBarleySeeds() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.BARLEY_SEEDS,
                props -> new SimpleTooltipBlockItem(
                        JolCraftBlocks.BARLEY_CROP.get(),
                        props,
                        JolCraftLanguageKeys.TOOLTIP_VANILLA_CROP,
                        true
                )
        );
    }

    public static DeferredItem<Item> registerBarley() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.BARLEY);
    }

    // -------------------------------------------------------------------------
    // Hops
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerAsgarnianSeeds() {
        return registerHopSeeds(JolCraftItemIds.ASGARNIAN_SEEDS, () -> JolCraftBlocks.ASGARNIAN_CROP_BOTTOM);
    }

    public static DeferredItem<Item> registerAsgarnianHops() {
        return registerHops(JolCraftItemIds.ASGARNIAN_HOPS);
    }

    public static DeferredItem<Item> registerDuskholdSeeds() {
        return registerHopSeeds(JolCraftItemIds.DUSKHOLD_SEEDS, () -> JolCraftBlocks.DUSKHOLD_CROP_BOTTOM);
    }

    public static DeferredItem<Item> registerDuskholdHops() {
        return registerHops(JolCraftItemIds.DUSKHOLD_HOPS);
    }

    public static DeferredItem<Item> registerKrandonianSeeds() {
        return registerHopSeeds(JolCraftItemIds.KRANDONIAN_SEEDS, () -> JolCraftBlocks.KRANDONIAN_CROP_BOTTOM);
    }

    public static DeferredItem<Item> registerKrandonianHops() {
        return registerHops(JolCraftItemIds.KRANDONIAN_HOPS);
    }

    public static DeferredItem<Item> registerYanillianSeeds() {
        return registerHopSeeds(JolCraftItemIds.YANILLIAN_SEEDS, () -> JolCraftBlocks.YANILLIAN_CROP_BOTTOM);
    }

    public static DeferredItem<Item> registerYanillianHops() {
        return registerHops(JolCraftItemIds.YANILLIAN_HOPS);
    }

    // -------------------------------------------------------------------------
    // Other crops
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerDeepslateBulbs() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DEEPSLATE_BULBS,
                props -> new SimpleTooltipBlockItem(
                        JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(),
                        props,
                        JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_BULBS
                )
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static <B extends Block> DeferredItem<Item> registerHopSeeds(
            String id,
            Supplier<? extends DeferredBlock<B>> crop
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new SimpleTooltipBlockItem(
                        crop.get().get(),
                        props,
                        JolCraftLanguageKeys.TOOLTIP_HOPS_SEEDS,
                        true
                )
        );
    }

    private static DeferredItem<Item> registerHops(String id) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_HOPS)
        );
    }
}