package net.sievert.jolcraft.integration.jade.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.event.game.world.time.JolCraftTimeHelper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewAge;
import snownee.jade.api.ITooltip;

public final class JolCraftJadeBrewingTooltipHelper {

    private JolCraftJadeBrewingTooltipHelper() {}

    public static void addBrewInfo(
            ITooltip tooltip,
            FluidStack brew
    ) {
        if (brew.isEmpty()) {
            return;
        }

        long ageTicks = brew.getOrDefault(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        DwarvenBrewAge age = DwarvenBrewAge.fromTicks(
                ageTicks
        );

        tooltip.add(
                Component.translatable(
                        JolCraftLanguageKeys.BREW_AGE,
                        Component.translatable(
                                age.translationKey()
                        )
                )
        );

        PotionContents contents = brew.getOrDefault(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        contents.addPotionTooltip(
                tooltip::add,
                1.0F,
                (float) JolCraftTimeHelper.TICKS_PER_SECOND
        );
    }
}