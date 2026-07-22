package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.bounty.BountyCrateItem;
import net.sievert.jolcraft.world.item.custom.bounty.BountyItem;
import net.sievert.jolcraft.world.item.custom.merchant.RerollCrateItem;
import net.sievert.jolcraft.world.item.custom.merchant.RestockCrateItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftBountyItems {

    private JolCraftBountyItems() {}

    public static DeferredItem<Item> registerBounty() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.BOUNTY,
                BountyItem::new,
                new Item.Properties().stacksTo(1)
        );
    }

    public static DeferredItem<Item> registerBountyCrate() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.BOUNTY_CRATE,
                BountyCrateItem::new,
                new Item.Properties().stacksTo(1)
        );
    }

    public static DeferredItem<Item> registerRestockCrate() {
        return registerMerchantCrate(JolCraftItemIds.RESTOCK_CRATE, RestockCrateItem::new);
    }

    public static DeferredItem<Item> registerRerollCrate() {
        return registerMerchantCrate(JolCraftItemIds.REROLL_CRATE, RerollCrateItem::new);
    }

    private static DeferredItem<Item> registerMerchantCrate(
            String id,
            java.util.function.Function<Item.Properties, ? extends Item> factory
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                factory,
                new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
        );
    }
}