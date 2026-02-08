package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.datagen.client.language.subprovider.ReputationLangSubProvider;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ReputationGateInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();

        int requiredTier = dwarf.getRequiredTier();
        if (requiredTier > 0 && !DwarvenReputationHelper.hasTier(player, requiredTier)) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_LOCKED, requiredTier)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}