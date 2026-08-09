package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ReputationGateInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();

        int requiredTier = dwarf.getRequiredTier();

        if (requiredTier > 0
                && !DwarvenReputationAttachmentHelper.hasTier(
                player,
                requiredTier
        )) {

            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_LOCKED,
                            requiredTier
                    ).withStyle(ChatFormatting.RED),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        return DwarfInteractionOutcome.pass();
    }
}