package net.sievert.jolcraft.gui.slot.strongbox;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.item.JolCraftItems;

public class LockpickSlot extends Slot {
    public LockpickSlot(Container container, int index, int xPosition, int yPosition) {
        super(container, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(JolCraftItems.LOCKPICK);
    }
}
