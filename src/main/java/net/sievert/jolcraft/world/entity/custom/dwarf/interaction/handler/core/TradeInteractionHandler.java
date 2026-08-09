package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.player.JolCraftStats;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TradeInteractionHandler
        implements DwarfInteractions.CoreInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!dwarf.canTrade()) {
            return DwarfInteractionOutcome.pass();
        }

        if (!stack.isEmpty()) {
            return DwarfInteractionOutcome.pass();
        }

        if (player.isCreative()
                && !player.getInventory().getSelected().isEmpty()) {

            return DwarfInteractionOutcome.pass();
        }

        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(
                    JolCraftStats.TALK_TO_DWARF.get()
            );
        }

        if (dwarf.getOffers().isEmpty()) {
            return DwarfInteractionOutcome.handled();
        }

        dwarf.openTradingScreen(
                player,
                dwarf.getDisplayName(),
                dwarf.getMerchantLevel()
        );

        return DwarfInteractionOutcome.handled();
    }
}