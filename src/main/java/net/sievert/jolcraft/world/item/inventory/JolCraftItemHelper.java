package net.sievert.jolcraft.world.item.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;

public final class JolCraftItemHelper {

    private JolCraftItemHelper(){}

    public static void consume(ServerPlayer player, InteractionHand hand, int amount) {
        if(player.isCreative()) return;
        ItemStack stack = player.getItemInHand(hand);
        ItemStack remainder = stack.getCraftingRemainingItem();
        if (!remainder.isEmpty()) {
            ItemStack result = ItemUtils.createFilledResult(stack, player, remainder);
            player.setItemInHand(hand, result);
        } else {
            stack.consume(amount, player);
        }
    }

    public static void consume(ServerPlayer player, InteractionHand hand){
        consume(player, hand, 1);
    }

    public static void replaceInventorySlot(ServerPlayer player, Slot slot, ItemStack stack) {
        if (slot == null) return;

        slot.container.setItem(slot.getContainerSlot(), stack);
        slot.setChanged();

        if (slot.container == player.getInventory()) {
            player.getInventory().setChanged();
        }

        player.inventoryMenu.broadcastChanges();
    }
}
