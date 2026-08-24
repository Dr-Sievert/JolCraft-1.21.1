package net.sievert.jolcraft.event.game.world.player.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public final class JolCraftCoinPickupEventsHelper {

    private JolCraftCoinPickupEventsHelper() {}

    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack groundStack = itemEntity.getItem();
        Player player = event.getPlayer();

        if (!groundStack.is(JolCraftItems.GOLD_COIN.get())
                || itemEntity.hasPickUpDelay()) {
            return;
        }

        int remaining = groundStack.getCount();

        for (int slot = 0;
                slot < player.getInventory().getContainerSize();
                slot++) {
            ItemStack inventoryStack =
                    player.getInventory().getItem(slot);

            if (!inventoryStack.is(JolCraftItems.COIN_POUCH.get())) {
                continue;
            }

            remaining -= CoinPouchItem.insertCoins(
                    inventoryStack,
                    remaining,
                    player
            );

            if (remaining <= 0) break;
        }

        if (remaining == groundStack.getCount()) return;

        groundStack.setCount(remaining);
        JolCraftSoundHelper.player(player, SoundEvents.ITEM_PICKUP);
    }
}
