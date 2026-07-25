package net.sievert.jolcraft.world.block.fluid.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

public final class DwarvenBrewFluidType extends FluidType {

    public DwarvenBrewFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack getBucket(FluidStack brew) {
        if (brew.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack bucket = new ItemStack(JolCraftItems.DWARVEN_BREW_BUCKET.get());

        Integer color = brew.get(JolCraftDataComponents.BREW_COLOR.get());

        if (color != null) {
            bucket.set(
                    JolCraftDataComponents.BREW_COLOR.get(),
                    color
            );
        }

        Long age = brew.get(JolCraftDataComponents.BREW_AGE.get());

        if (age != null) {
            bucket.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    age
            );
        }

        PotionContents potionContents = brew.get(DataComponents.POTION_CONTENTS);

        if (potionContents != null) {
            bucket.set(
                    DataComponents.POTION_CONTENTS,
                    potionContents
            );
        }

        return bucket;
    }
}