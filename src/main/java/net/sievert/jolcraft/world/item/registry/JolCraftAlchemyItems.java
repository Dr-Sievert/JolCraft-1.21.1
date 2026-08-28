package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import net.sievert.jolcraft.world.item.custom.alchemy.EssenceItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftAlchemyItems {

    private JolCraftAlchemyItems() {}

    public static DeferredItem<BlockItem> registerMortar() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.MORTAR,
                props -> new BlockItem(
                        JolCraftBlocks.MORTAR.get(),
                        props.stacksTo(3)
                )
        );
    }

    public static DeferredItem<EssenceItem> registerEssence() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.ESSENCE,
                properties -> new EssenceItem(
                        properties.component(
                                JolCraftDataComponents.ESSENCE_TYPE.get(),
                                EssenceType.INFUSED
                        )
                )
        );
    }

    public static DeferredItem<Item> registerInverix() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.INVERIX,
                new Item.Properties()
        );
    }

    public static DeferredItem<Item> registerVitriol() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.VITRIOL);
    }
}