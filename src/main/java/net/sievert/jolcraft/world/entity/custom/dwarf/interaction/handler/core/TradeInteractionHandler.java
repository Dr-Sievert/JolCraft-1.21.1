package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TradeInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!dwarf.canTrade()) {
            return InteractionResult.PASS;
        }

        if (!stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (player.isCreative() && !player.getInventory().getSelected().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(JolCraftStats.TALK_TO_DWARF.get());
        }

        JolCraftLogs.info(JolCraftLogTags.ENTITY,
                "TradeInteractionHandler: dwarf={} profession={} level={} canTrade={} offers={}",
                dwarf.getUUID(),
                dwarf.getTradeProfession(),
                dwarf.getMerchantLevel(),
                dwarf.canTrade(),
                dwarf.getOffers().size());

        if (dwarf.getOffers().isEmpty()) {
            return InteractionResult.SUCCESS;
        }

        dwarf.openTradingScreen(player, dwarf.getDisplayName(), dwarf.getMerchantLevel());
        return InteractionResult.SUCCESS;
    }
}