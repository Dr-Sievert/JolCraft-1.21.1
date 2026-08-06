package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftCropItems {

    private JolCraftCropItems() {}

    // -------------------------------------------------------------------------
    // Barley
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerBarleySeeds() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.BARLEY_SEEDS,
                props -> new ItemNameBlockItem(
                        JolCraftBlocks.BARLEY_CROP.get(),
                        props
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
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.ASGARNIAN_SEEDS,
                props -> new ItemNameBlockItem(
                        JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(),
                        props
                )
        );
    }

    public static DeferredItem<Item> registerAsgarnianHops() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.ASGARNIAN_HOPS);
    }

    public static DeferredItem<Item> registerDuskholdSeeds() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DUSKHOLD_SEEDS,
                props -> new ItemNameBlockItem(
                        JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(),
                        props
                )
        );
    }

    public static DeferredItem<Item> registerDuskholdHops() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.DUSKHOLD_HOPS);
    }

    public static DeferredItem<Item> registerKrandonianSeeds() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.KRANDONIAN_SEEDS,
                props -> new ItemNameBlockItem(
                        JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(),
                        props
                )
        );
    }

    public static DeferredItem<Item> registerKrandonianHops() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.KRANDONIAN_HOPS);
    }

    public static DeferredItem<Item> registerYanillianSeeds() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.YANILLIAN_SEEDS,
                props -> new ItemNameBlockItem(
                        JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(),
                        props
                )
        );
    }

    public static DeferredItem<Item> registerYanillianHops() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.YANILLIAN_HOPS);
    }

    // -------------------------------------------------------------------------
    // Other crops
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerDeepslateBulbs() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DEEPSLATE_BULBS,
                props -> new BlockItem(
                        JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(), props
                )
        );
    }
}