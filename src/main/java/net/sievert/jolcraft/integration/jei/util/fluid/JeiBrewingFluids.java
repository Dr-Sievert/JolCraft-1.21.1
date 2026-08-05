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
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
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
                age,
                age.ordinal() > DwarvenBrewAge.AGED.ordinal()
                        ? age
                        : DwarvenBrewFluidHelper.DEFAULT_MAX_AGE,
                effect
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            @NotNull MobEffectInstance effect
    ) {
        return dwarvenBrew(
                FluidType.BUCKET_VOLUME,
                age,
                maxAge,
                PotionContents.EMPTY.withEffectAdded(
                        effect
                )
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed,
            @NotNull MobEffectInstance effect
    ) {
        return dwarvenBrew(
                FluidType.BUCKET_VOLUME,
                age,
                maxAge,
                brewingSpeed,
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
        return dwarvenBrew(
                amount,
                age,
                age.ordinal() > DwarvenBrewAge.AGED.ordinal()
                        ? age
                        : DwarvenBrewFluidHelper.DEFAULT_MAX_AGE,
                potionContents
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            int amount,
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            @NotNull PotionContents potionContents
    ) {
        return dwarvenBrew(
                amount,
                age,
                maxAge,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED,
                potionContents
        );
    }

    public static @NotNull FluidStack dwarvenBrew(
            int amount,
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed,
            @NotNull PotionContents potionContents
    ) {
        return DwarvenBrewFluidHelper.createDwarvenBrew(
                amount,
                age,
                maxAge,
                brewingSpeed,
                potionContents
        );
    }

    public static @NotNull FluidStack unfinishedDwarvenBrew() {
        return unfinishedDwarvenBrew(
                FluidType.BUCKET_VOLUME
        );
    }

    public static @NotNull FluidStack unfinishedDwarvenBrew(
            int amount
    ) {
        return unfinishedDwarvenBrew(
                amount,
                DwarvenBrewFluidHelper.DEFAULT_MAX_AGE,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );
    }

    public static @NotNull FluidStack unfinishedDwarvenBrew(
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed
    ) {
        return unfinishedDwarvenBrew(
                FluidType.BUCKET_VOLUME,
                maxAge,
                brewingSpeed
        );
    }

    public static @NotNull FluidStack unfinishedDwarvenBrew(
            int amount,
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed
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

        fluid.set(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                maxAge
        );

        fluid.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                brewingSpeed
        );

        return fluid;
    }

    public static @NotNull FluidStack yeast() {
        return yeast(
                FluidType.BUCKET_VOLUME,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );
    }

    public static @NotNull FluidStack yeast(
            float brewingSpeed
    ) {
        return yeast(
                FluidType.BUCKET_VOLUME,
                brewingSpeed
        );
    }

    public static @NotNull FluidStack yeast(
            int amount
    ) {
        return yeast(
                amount,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );
    }

    public static @NotNull FluidStack yeast(
            int amount,
            float brewingSpeed
    ) {
        FluidStack fluid = JolCraftBrewingItems.createYeastFluid(
                JolCraftFluids.YEAST.get()
        );

        fluid.setAmount(amount);
        fluid.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                brewingSpeed
        );

        return fluid;
    }

    public static @NotNull FluidStack unfinishedYeast() {
        return unfinishedYeast(
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );
    }

    public static @NotNull FluidStack unfinishedYeast(
            float brewingSpeed
    ) {
        FluidStack fluid = createColoredFluid(
                JolCraftFluids.UNFINISHED_YEAST.get(),
                FluidType.BUCKET_VOLUME,
                BrewingColors.UNFINISHED_YEAST
        );

        fluid.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                brewingSpeed
        );

        return fluid;
    }

    public static @NotNull ItemStack yeastCultureItem(
            float brewingSpeed
    ) {
        return JolCraftBrewingItems.createYeastCultureStack(
                JolCraftItems.YEAST_CULTURE.get(),
                brewingSpeed
        );
    }

    public static @NotNull ItemStack yeastItem(
            float brewingSpeed
    ) {
        return JolCraftBrewingItems.createYeastStack(
                JolCraftItems.YEAST.get(),
                brewingSpeed
        );
    }

    public static @NotNull FluidStack tannin(
            @NotNull DwarvenBrewAge maxAge
    ) {
        return tannin(
                FluidType.BUCKET_VOLUME,
                maxAge
        );
    }

    public static @NotNull FluidStack tannin(
            int amount,
            @NotNull DwarvenBrewAge maxAge
    ) {
        FluidStack fluid = JolCraftBrewingItems.createTanninFluid(
                maxAge.ordinal() >= DwarvenBrewAge.VINTAGE.ordinal()
                        ? JolCraftFluids.REFINED_TANNIN.get()
                        : JolCraftFluids.TANNIN.get(),
                maxAge
        );

        fluid.setAmount(amount);

        return fluid;
    }

    public static @NotNull FluidStack unfinishedTannin(
            @NotNull DwarvenBrewAge maxAge
    ) {
        FluidStack fluid = createColoredFluid(
                JolCraftFluids.UNFINISHED_TANNIN.get(),
                FluidType.BUCKET_VOLUME,
                BrewingColors.UNFINISHED_TANNIN
        );

        fluid.set(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                maxAge
        );

        return fluid;
    }

    public static @NotNull ItemStack tanninItem(
            @NotNull DwarvenBrewAge maxAge
    ) {
        boolean refined = maxAge.ordinal()
                >= DwarvenBrewAge.VINTAGE.ordinal();

        return JolCraftBrewingItems.createTanninStack(
                JolCraftItems.TANNIN.get(),
                refined
                        ? JolCraftFluids.REFINED_TANNIN.get()
                        : JolCraftFluids.TANNIN.get(),
                maxAge
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
        return dwarvenBrewMug(
                DwarvenBrewAge.FRESH,
                DwarvenBrewFluidHelper.DEFAULT_MAX_AGE
        );
    }

    public static @NotNull FluidStack dwarvenBrewMug(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge
    ) {
        return dwarvenBrew(
                DwarvenBrewFluidHelper.MUG_VOLUME,
                age,
                maxAge,
                PotionContents.EMPTY.withEffectAdded(
                        displayStrengthEffect(
                                age.amplifierBonus()
                        )
                )
        );
    }

    public static @NotNull ItemStack dwarvenBrewItem() {
        return dwarvenBrewItem(
                DwarvenBrewAge.FRESH,
                DwarvenBrewFluidHelper.DEFAULT_MAX_AGE
        );
    }

    public static @NotNull ItemStack dwarvenBrewItem(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge
    ) {
        return dwarvenBrewItem(
                age,
                maxAge,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );
    }

    public static @NotNull ItemStack dwarvenBrewItem(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed
    ) {
        return fluidItem(
                new ItemStack(
                        JolCraftItems.DWARVEN_BREW.get()
                ),
                dwarvenBrew(
                        DwarvenBrewFluidHelper.MUG_VOLUME,
                        age,
                        maxAge,
                        brewingSpeed,
                        PotionContents.EMPTY.withEffectAdded(
                                displayStrengthEffect(
                                        age.amplifierBonus()
                                )
                        )
                )
        );
    }

    public static @NotNull ItemStack dwarvenBrewBucket(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge
    ) {
        return dwarvenBrewBucket(
                age,
                maxAge,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );
    }

    public static @NotNull ItemStack dwarvenBrewBucket(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed
    ) {
        return fluidItem(
                new ItemStack(
                        JolCraftItems.DWARVEN_BREW_BUCKET.get()
                ),
                dwarvenBrew(
                        FluidType.BUCKET_VOLUME,
                        age,
                        maxAge,
                        brewingSpeed,
                        PotionContents.EMPTY
                )
        );
    }

    private static @NotNull ItemStack fluidItem(
            @NotNull ItemStack stack,
            @NotNull FluidStack fluid
    ) {
        stack.set(
                JolCraftDataComponents.FLUID_CONTENT,
                SimpleFluidContent.copyOf(
                        fluid
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