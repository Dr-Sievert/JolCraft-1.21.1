package net.sievert.jolcraft.util.dwarf.trade;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;

public class CoinPouchHelper {
    // Get coin count
    public static int getCoins(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        Integer value = stack.get(JolCraftDataComponents.COIN_POUCH_AMOUNT);
        return value != null ? value : 0;
    }

    // Set coin count
    public static void setCoins(ItemStack stack, int count) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        stack.set(JolCraftDataComponents.COIN_POUCH_AMOUNT, count);
    }
}
