package net.sievert.jolcraft.world.gui.custom.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class JolCraftSlot extends Slot {

    private static final Runnable EMPTY_RUNNABLE = () -> {};
    private static final Predicate<Player> ALWAYS_TRUE = p -> true;
    private static final BiConsumer<Player, ItemStack> EMPTY_CONSUMER = (p, s) -> {};

    private Runnable onSlotChanged = EMPTY_RUNNABLE;
    private Predicate<Player> mayPickup = ALWAYS_TRUE;
    private BiConsumer<Player, ItemStack> onTake = EMPTY_CONSUMER;

    private Predicate<ItemStack> mayPlace = stack -> true;

    public JolCraftSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public JolCraftSlot mayPlaceRule(Predicate<ItemStack> rule) {
        this.mayPlace = rule;
        return this;
    }

    public JolCraftSlot onSlotChanged(Runnable runnable) {
        this.onSlotChanged = runnable;
        return this;
    }

    public JolCraftSlot mayPickupRule(Predicate<Player> rule) {
        this.mayPickup = rule;
        return this;
    }

    public JolCraftSlot onTake(BiConsumer<Player, ItemStack> consumer) {
        this.onTake = consumer;
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.mayPlace.test(stack);
    }

    @Override
    public void setChanged() {
        this.onSlotChanged.run();
        super.setChanged();
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.mayPickup.test(player);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.onTake.accept(player, stack);
        super.onTake(player, stack);
    }
}
