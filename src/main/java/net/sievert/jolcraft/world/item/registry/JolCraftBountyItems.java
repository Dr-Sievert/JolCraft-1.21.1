package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.bounty.*;
import net.sievert.jolcraft.world.item.custom.crate.RerollCrateItem;
import net.sievert.jolcraft.world.item.custom.crate.RestockCrateItem;
import net.sievert.jolcraft.world.item.custom.crate.RewardCrateItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Function;

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
        return registerCrate(JolCraftItemIds.RESTOCK_CRATE, RestockCrateItem::new);
    }

    public static DeferredItem<Item> registerRerollCrate() {
        return registerCrate(JolCraftItemIds.REROLL_CRATE, RerollCrateItem::new);
    }

    public static DeferredItem<Item> registerRewardCrate() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.REWARD_CRATE,
                RewardCrateItem::new,
                new Item.Properties().stacksTo(1)
        );
    }

    private static DeferredItem<Item> registerCrate(
            String id,
            Function<Item.Properties, ? extends Item> factory
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                factory,
                new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
        );
    }
}