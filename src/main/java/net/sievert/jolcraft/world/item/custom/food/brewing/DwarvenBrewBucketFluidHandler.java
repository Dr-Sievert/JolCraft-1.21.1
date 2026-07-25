package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

public final class DwarvenBrewBucketFluidHandler
        extends FluidBucketWrapper {

    public DwarvenBrewBucketFluidHandler(
            ItemStack container
    ) {
        super(container);
    }

    @Override
    public @NotNull FluidStack getFluid() {
        FluidStack brew =
                super.getFluid();

        if (brew.isEmpty()) {
            return FluidStack.EMPTY;
        }

        copyBrewComponents(
                container,
                brew
        );

        return brew;
    }

    @Override
    protected void setFluid(
            @NotNull FluidStack brew
    ) {
        super.setFluid(brew);

        if (brew.isEmpty()) {
            return;
        }

        copyBrewComponents(
                brew,
                container
        );
    }

    private static void copyBrewComponents(
            ItemStack source,
            FluidStack destination
    ) {
        Integer color =
                source.get(
                        JolCraftDataComponents.BREW_COLOR.get()
                );

        if (color != null) {
            destination.set(
                    JolCraftDataComponents.BREW_COLOR.get(),
                    color
            );
        }

        Long age =
                source.get(
                        JolCraftDataComponents.BREW_AGE.get()
                );

        if (age != null) {
            destination.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    age
            );
        }

        PotionContents potionContents =
                source.get(
                        DataComponents.POTION_CONTENTS
                );

        if (potionContents != null) {
            destination.set(
                    DataComponents.POTION_CONTENTS,
                    potionContents
            );
        }
    }

    private static void copyBrewComponents(
            FluidStack source,
            ItemStack destination
    ) {
        Integer color =
                source.get(
                        JolCraftDataComponents.BREW_COLOR.get()
                );

        if (color != null) {
            destination.set(
                    JolCraftDataComponents.BREW_COLOR.get(),
                    color
            );
        }

        Long age =
                source.get(
                        JolCraftDataComponents.BREW_AGE.get()
                );

        if (age != null) {
            destination.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    age
            );
        }

        PotionContents potionContents =
                source.get(
                        DataComponents.POTION_CONTENTS
                );

        if (potionContents != null) {
            destination.set(
                    DataComponents.POTION_CONTENTS,
                    potionContents
            );
        }
    }
}