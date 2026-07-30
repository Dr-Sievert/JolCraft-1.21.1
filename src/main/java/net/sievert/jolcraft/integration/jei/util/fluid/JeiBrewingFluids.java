package net.sievert.jolcraft.integration.jei.util.fluid;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

public final class JeiBrewingFluids {

    private static final int DISPLAY_EFFECT_DURATION =
            30
                    * 20;

    private JeiBrewingFluids() {
    }

    public static @NotNull FluidStack dwarvenBrew() {
        return dwarvenBrew(
                FluidType.BUCKET_VOLUME
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            int amount
    ) {
        return dwarvenBrew(
                amount,
                DwarvenBrewAge.FRESH,
                PotionContents.EMPTY
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            @NotNull DwarvenBrewAge age,
            @NotNull MobEffectInstance effect
    ) {
        return dwarvenBrew(
                FluidType.BUCKET_VOLUME,
                age,
                PotionContents.EMPTY.withEffectAdded(
                        effect
                )
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            int amount,
            @NotNull DwarvenBrewAge age,
            @NotNull PotionContents potionContents
    ) {
        FluidStack fluid =
                createColoredFluid(
                        JolCraftFluids.DWARVEN_BREW.get(),
                        amount,
                        BrewingColors.DWARVEN_BREW
                );

        fluid.set(
                DataComponents.POTION_CONTENTS,
                potionContents
        );

        fluid.set(
                JolCraftDataComponents.BREW_AGE.get(),
                age.thresholdTicks()
        );

        return fluid;
    }

    public static @NotNull FluidStack unfinishedDwarvenBrew() {
        return unfinishedDwarvenBrew(
                FluidType.BUCKET_VOLUME
        );
    }

    public static @NotNull FluidStack unfinishedDwarvenBrew(
            int amount
    ) {
        FluidStack fluid =
                createColoredFluid(
                        JolCraftFluids.UNFINISHED_DWARVEN_BREW.get(),
                        amount,
                        BrewingColors.UNFINISHED_DWARVEN_BREW
                );

        fluid.set(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        return fluid;
    }

    public static @NotNull FluidStack yeast() {
        return yeast(
                FluidType.BUCKET_VOLUME
        );
    }

    public static @NotNull FluidStack yeast(
            int amount
    ) {
        return createColoredFluid(
                JolCraftFluids.YEAST.get(),
                amount,
                BrewingColors.YEAST
        );
    }

    public static @NotNull FluidStack unfinishedYeast() {
        return unfinishedYeast(
                FluidType.BUCKET_VOLUME
        );
    }

    public static @NotNull FluidStack unfinishedYeast(
            int amount
    ) {
        return createColoredFluid(
                JolCraftFluids.UNFINISHED_YEAST.get(),
                amount,
                BrewingColors.UNFINISHED_YEAST
        );
    }

    public static @NotNull MobEffectInstance displayStrengthEffect() {
        return displayStrengthEffect(
                0
        );
    }

    public static @NotNull MobEffectInstance displayStrengthEffect(
            int amplifier
    ) {
        if (amplifier < 0) {
            throw new IllegalArgumentException(
                    "Effect amplifier must not be negative"
            );
        }

        return new MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                DISPLAY_EFFECT_DURATION,
                amplifier
        );
    }

    public static @NotNull FluidStack dwarvenBrewMug() {
        return dwarvenBrew(
                DwarvenBrewFluidHelper.MUG_VOLUME,
                DwarvenBrewAge.FRESH,
                PotionContents.EMPTY.withEffectAdded(
                        displayStrengthEffect()
                )
        );
    }

    public static @NotNull ItemStack dwarvenBrewItem() {
        ItemStack stack = new ItemStack(
                JolCraftItems.DWARVEN_BREW.get()
        );

        stack.set(
                JolCraftDataComponents.FLUID_CONTENT,
                SimpleFluidContent.copyOf(
                        dwarvenBrewMug()
                )
        );

        return stack;
    }

    private static @NotNull FluidStack createColoredFluid(
            @NotNull Fluid fluid,
            int amount,
            int color
    ) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Fluid amount must be positive"
            );
        }

        FluidStack result =
                new FluidStack(
                        fluid,
                        amount
                );

        result.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                0xFF000000
                        | color
                        & 0x00FFFFFF
        );

        return result;
    }
}