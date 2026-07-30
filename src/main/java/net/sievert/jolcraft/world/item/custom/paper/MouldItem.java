package net.sievert.jolcraft.world.item.custom.paper;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.item.custom.scrapper.SalvageItem;
import org.jetbrains.annotations.NotNull;

public class MouldItem extends SalvageItem {

    public MouldItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.copy();
    }
}
