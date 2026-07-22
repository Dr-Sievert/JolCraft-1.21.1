package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftFoodItems {

    private JolCraftFoodItems() {}

    public static DeferredItem<Item> registerMuffhornMilkBucket() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.MUFFHORN_MILK_BUCKET,
                props -> new MilkBucketItem(
                        props.craftRemainder(Items.BUCKET).stacksTo(1)
                )
        );
    }
}