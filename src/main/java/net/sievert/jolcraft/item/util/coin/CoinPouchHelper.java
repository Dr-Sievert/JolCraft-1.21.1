package net.sievert.jolcraft.item.util.coin;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftComponents;

public class CoinPouchHelper {

    public static int getCoins(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        Integer value = stack.get(JolCraftComponents.COIN_POUCH_AMOUNT);
        return value != null ? value : 0;
    }

    public static void setCoins(ItemStack stack, int count) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        stack.set(JolCraftComponents.COIN_POUCH_AMOUNT, count);
    }
}
