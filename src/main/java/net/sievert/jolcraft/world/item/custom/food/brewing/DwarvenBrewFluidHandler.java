package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

public final class DwarvenBrewFluidHandler
        extends FluidHandlerItemStackSimple.SwapEmpty {

    public DwarvenBrewFluidHandler(
            ItemStack container,
            ItemLike emptyContainer
    ) {
        super(
                JolCraftDataComponents.FLUID_CONTENT,
                container,
                new ItemStack(
                        emptyContainer
                ),
                getCapacity(
                        container
                )
        );
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
        return DwarvenBrewFluidHelper.isFinishedBrew(
                fluid
        );
    }

    @Override
    public @NotNull FluidStack drain(
            @NotNull FluidStack resource,
            IFluidHandler.@NotNull FluidAction action
    ) {
        FluidStack stored =
                getFluidInTank(
                        0
                );

        if (stored.isEmpty()
                || resource.getAmount()
                < stored.getAmount()
                || !FluidStack.isSameFluidSameComponents(
                stored,
                resource
        )) {
            return FluidStack.EMPTY;
        }

        return super.drain(
                stored.getAmount(),
                action
        );
    }

    @Override
    public @NotNull FluidStack drain(
            int maxDrain,
            IFluidHandler.@NotNull FluidAction action
    ) {
        FluidStack stored =
                getFluidInTank(
                        0
                );

        if (stored.isEmpty()
                || maxDrain < stored.getAmount()) {
            return FluidStack.EMPTY;
        }

        return super.drain(
                stored.getAmount(),
                action
        );
    }

    private static int getCapacity(
            ItemStack container
    ) {
        SimpleFluidContent content =
                container.getOrDefault(
                        JolCraftDataComponents.FLUID_CONTENT.get(),
                        SimpleFluidContent.EMPTY
                );

        return content.getAmount();
    }
}