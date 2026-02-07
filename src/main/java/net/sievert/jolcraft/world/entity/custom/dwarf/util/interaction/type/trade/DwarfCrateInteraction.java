package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.type.trade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.datagen.language.subprovider.BountyLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public final class DwarfCrateInteraction {

    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack stack) {

        boolean restock = stack.is(JolCraftItems.RESTOCK_CRATE.get());
        boolean reroll = stack.is(JolCraftItems.REROLL_CRATE.get());
        if (!restock && !reroll) return InteractionResult.FAIL;

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack cooldownStack = stack.copyWithCount(1);
        if (player.getCooldowns().isOnCooldown(cooldownStack)) {
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

        // -------------------------------------------------------------------------
        // Restock crate
        // -------------------------------------------------------------------------
        if (restock) {

            boolean needsRestock = dwarf.getOffers().stream().anyMatch(DwarfMerchantOffer::isOutOfStock);

            if (!needsRestock && !dwarf.hasRandomTrades()) {
                player.displayClientMessage(
                        Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_NO_NEED).withStyle(ChatFormatting.RED),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                    true
            );
            dwarf.crateRestock();
            PlaySound.dwarfYes(dwarf);

            player.getCooldowns().addCooldown(cooldownStack, 60);
            if (!player.isCreative()) stack.shrink(1);

            return InteractionResult.SUCCESS;
        }

        // -------------------------------------------------------------------------
        // Reroll crate
        // -------------------------------------------------------------------------
        if (!dwarf.canReroll()) {
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_REROLL_CRATE_FAIL).withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        player.displayClientMessage(
                Component.translatable(BountyLangSubProvider.TOOLTIP_REROLL_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                true
        );
        dwarf.rerollTrades();
        PlaySound.dwarfYes(dwarf);

        player.getCooldowns().addCooldown(cooldownStack, 60);
        if (!player.isCreative()) stack.shrink(1);

        return InteractionResult.SUCCESS;
    }
}