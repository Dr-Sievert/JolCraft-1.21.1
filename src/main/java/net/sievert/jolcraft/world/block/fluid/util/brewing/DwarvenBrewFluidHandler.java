package net.sievert.jolcraft.world.block.fluid.util.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Exposes a fixed brewing fluid stored in an item and swaps it for its empty
 * counterpart when fully drained.
 */
public final class DwarvenBrewFluidHandler extends FluidHandlerItemStackSimple.SwapEmpty {

    private final Predicate<FluidStack> drainableFluid;

    public DwarvenBrewFluidHandler(
            ItemStack container,
            ItemLike emptyContainer,
            Predicate<FluidStack> drainableFluid
    ) {
        super(
                JolCraftDataComponents.FLUID_CONTENT,
                container,
                new ItemStack(emptyContainer),
                getCapacity(container)
        );

        this.drainableFluid = Objects.requireNonNull(drainableFluid);
    }

    @Override
    public boolean canFillFluidType(
            @NotNull FluidStack fluid
    ) {
        return false;
    }

    @Override
    public boolean canDrainFluidType(
            @NotNull FluidStack fluid
    ) {
        return drainableFluid.test(fluid);
    }

    /**
     * Drains the complete stored fluid only when the requested fluid matches it.
     */
    @Override
    public @NotNull FluidStack drain(
            @NotNull FluidStack resource,
            @NotNull FluidAction action
    ) {
        FluidStack stored = getFluidInTank(0);

        if (stored.isEmpty()
                || resource.getAmount() < stored.getAmount()
                || !FluidStack.isSameFluidSameComponents(stored, resource)) {
            return FluidStack.EMPTY;
        }

        return super.drain(
                stored.getAmount(),
                action
        );
    }

    /**
     * Drains the complete stored fluid only when the requested amount can hold it.
     */
    @Override
    public @NotNull FluidStack drain(
            int maxDrain,
            @NotNull FluidAction action
    ) {
        FluidStack stored = getFluidInTank(0);

        if (stored.isEmpty()
                || maxDrain < stored.getAmount()) {
            return FluidStack.EMPTY;
        }

        return super.drain(
                stored.getAmount(),
                action
        );
    }

    private static int getCapacity(ItemStack container) {
        SimpleFluidContent content = container.getOrDefault(
                JolCraftDataComponents.FLUID_CONTENT.get(),
                SimpleFluidContent.EMPTY
        );

        return content.getAmount();
    }
}
