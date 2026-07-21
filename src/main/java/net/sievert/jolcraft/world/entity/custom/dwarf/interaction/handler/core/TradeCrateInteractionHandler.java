package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TradeCrateInteractionHandler
        implements DwarfInteractions.CoreInteraction {

    private static final int CRATE_COOLDOWN_TICKS = 60;

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var stack = ctx.stack();

        boolean isRestock =
                stack.is(JolCraftItems.RESTOCK_CRATE.get());

        boolean isReroll =
                stack.is(JolCraftItems.REROLL_CRATE.get());

        if (!isRestock && !isReroll) {
            return DwarfInteractionOutcome.pass();
        }

        if (player.getCooldowns().isOnCooldown(
                stack.getItem()
        )) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_CRATE_COOLDOWN
                    ).withStyle(ChatFormatting.GRAY),
                    true
            );

            return DwarfInteractionOutcome.handled();
        }

        if (dwarf.getOffers().isEmpty()) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_CRATE_NO_OFFERS_DWARF
                    ).withStyle(ChatFormatting.RED),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        if (isRestock) {
            boolean changed =
                    dwarf.crateRestock();

            if (!changed) {
                player.displayClientMessage(
                        Component.translatable(
                                JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_NO_NEED
                        ).withStyle(ChatFormatting.RED),
                        true
                );

                PlaySound.dwarfNo(dwarf);

                return DwarfInteractionOutcome.handled();
            }

            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_SUCCESS
                    ).withStyle(ChatFormatting.GREEN),
                    true
            );

            PlaySound.dwarfYes(dwarf);
        } else {
            if (!dwarf.canReroll()) {
                player.displayClientMessage(
                        Component.translatable(
                                JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_FAIL
                        ).withStyle(ChatFormatting.RED),
                        true
                );

                PlaySound.dwarfNo(dwarf);

                return DwarfInteractionOutcome.handled();
            }

            dwarf.rerollTrades();

            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_SUCCESS
                    ).withStyle(ChatFormatting.GREEN),
                    true
            );

            PlaySound.dwarfYes(dwarf);
        }

        player.getCooldowns().addCooldown(
                stack.getItem(),
                CRATE_COOLDOWN_TICKS
        );

        return DwarfInteractionOutcome.consumeOne();
    }
}