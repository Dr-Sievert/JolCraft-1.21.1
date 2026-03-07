package net.sievert.jolcraft.world.item.util.coin;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.component.JolCraftDataComponents;

public class CoinPouchHelper {

    public static int getCoins(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        Integer value = stack.get(JolCraftDataComponents.COIN_POUCH_AMOUNT);
        return value != null ? value : 0;
    }

    public static void setCoins(ItemStack stack, int count) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        stack.set(JolCraftDataComponents.COIN_POUCH_AMOUNT, count);
    }
}
