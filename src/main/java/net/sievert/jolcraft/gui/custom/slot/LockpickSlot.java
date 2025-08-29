package net.sievert.jolcraft.gui.custom.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public class LockpickSlot extends Slot {
    public LockpickSlot(Container container, int index, int xPosition, int yPosition) {
        super(container, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(JolCraftItems.LOCKPICK);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 64;
    }
}
