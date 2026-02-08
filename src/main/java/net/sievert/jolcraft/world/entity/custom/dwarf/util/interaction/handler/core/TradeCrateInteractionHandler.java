package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.datagen.client.language.subprovider.BountyLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TradeCrateInteractionHandler implements DwarfInteractions.CoreInteraction {

    private static final int CRATE_COOLDOWN_TICKS = 60;

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var stack = ctx.stack();

        boolean isRestock = stack.is(JolCraftItems.RESTOCK_CRATE.get());
        boolean isReroll = stack.is(JolCraftItems.REROLL_CRATE.get());

        if (!isRestock && !isReroll) {
            return InteractionResult.PASS;
        }

        if (player.getCooldowns().isOnCooldown(stack)) {
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_COOLDOWN).withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (dwarf.getOffers().isEmpty()) {
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_NO_OFFERS_DWARF).withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (isRestock) {
            boolean anyOutOfStock = dwarf.getOffers().stream().anyMatch(DwarfMerchantOffer::isOutOfStock);
            if (!anyOutOfStock && !dwarf.hasRandomTrades()) {
                player.displayClientMessage(
                        Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_NO_NEED).withStyle(ChatFormatting.RED),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            dwarf.crateRestock();
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                    true
            );
            PlaySound.dwarfYes(dwarf);
        } else {
            if (!dwarf.canReroll()) {
                player.displayClientMessage(
                        Component.translatable(BountyLangSubProvider.TOOLTIP_REROLL_CRATE_FAIL).withStyle(ChatFormatting.RED),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            dwarf.rerollTrades();
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_REROLL_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                    true
            );
            PlaySound.dwarfYes(dwarf);
        }

        player.getCooldowns().addCooldown(stack, CRATE_COOLDOWN_TICKS);

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}