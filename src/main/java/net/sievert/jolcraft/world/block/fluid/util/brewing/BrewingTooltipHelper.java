package net.sievert.jolcraft.world.block.fluid.util.brewing;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.sievert.jolcraft.world.util.JolCraftTimeHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class BrewingTooltipHelper {

    private BrewingTooltipHelper() {}

    public static void appendItemFluidTooltip(
            @NotNull ItemStack stack,
            @NotNull Consumer<Component> tooltip
    ) {
        FluidUtil.getFluidContained(stack)
                .ifPresent(fluid -> appendFluidTooltip(
                        fluid,
                        tooltip,
                        1.0F,
                        (float) JolCraftTimeHelper.TICKS_PER_SECOND
                ));
    }

    public static void appendFluidTooltip(
            @NotNull FluidStack fluid,
            @NotNull Consumer<Component> tooltip,
            float durationFactor,
            float ticksPerSecond
    ) {
        if (fluid.isEmpty()) {
            return;
        }

        boolean dwarvenBrew = DwarvenBrewFluidHelper.isFinishedBrew(fluid)
                || DwarvenBrewFluidHelper.isUnfinishedBrew(fluid);

        boolean tannin = DwarvenBrewFluidHelper.isFinishedTannin(fluid)
                || DwarvenBrewFluidHelper.isUnfinishedTannin(fluid);

        boolean yeast = DwarvenBrewFluidHelper.isFinishedYeast(fluid)
                || DwarvenBrewFluidHelper.isUnfinishedYeast(fluid);

        if (dwarvenBrew
                || fluid.has(JolCraftDataComponents.BREW_AGE.get())) {
            tooltip.accept(
                    brewAge(
                            DwarvenBrewAge.fromTicks(
                                    fluid.getOrDefault(
                                            JolCraftDataComponents.BREW_AGE.get(),
                                            0L
                                    )
                            )
                    )
            );
        }

        if (dwarvenBrew
                || tannin
                || fluid.has(JolCraftDataComponents.MAX_BREW_AGE.get())) {
            tooltip.accept(
                    maxBrewAge(
                            DwarvenBrewFluidHelper.getMaxAge(fluid)
                    )
            );
        }

        if (dwarvenBrew
                || yeast
                || fluid.has(JolCraftDataComponents.BREWING_SPEED.get())) {
            tooltip.accept(
                    brewingSpeed(
                            DwarvenBrewFluidHelper.getBrewingSpeed(fluid)
                    )
            );
        }

        PotionContents contents = fluid.get(DataComponents.POTION_CONTENTS);

        if (contents != null && contents.hasEffects()) {
            contents.addPotionTooltip(
                    tooltip,
                    durationFactor,
                    ticksPerSecond
            );
        }
    }

    public static @NotNull Component brewAge(
            @NotNull DwarvenBrewAge age
    ) {
        return Component.translatable(
                        JolCraftLanguageKeys.BREW_AGE,
                        Component.translatable(
                                age.translationKey()
                        )
                )
                .withStyle(
                        ChatFormatting.GRAY
                );
    }

    public static @NotNull Component maxBrewAge(
            @NotNull DwarvenBrewAge maxAge
    ) {
        return Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_MAX_BREW_AGE,
                        Component.translatable(
                                maxAge.translationKey()
                        )
                )
                .withStyle(
                        ChatFormatting.GRAY
                );
    }

    public static @NotNull Component brewingSpeed(
            float brewingSpeed
    ) {
        return Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_BREWING_SPEED,
                        brewingSpeed
                )
                .withStyle(
                        ChatFormatting.GRAY
                );
    }
}
