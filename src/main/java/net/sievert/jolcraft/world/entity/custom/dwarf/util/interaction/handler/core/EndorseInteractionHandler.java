package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.datagen.language.subprovider.ReputationLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfGuildmasterEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EndorseInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!stack.is(JolCraftTags.Items.REPUTATION_TABLETS)) {
            return InteractionResult.PASS;
        }

        // Guildmaster is intentionally not handled here
        if (dwarf instanceof DwarfGuildmasterEntity) {
            return InteractionResult.PASS;
        }

        if (dwarf.neverEndorse()) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_NEVER_ENDORSE).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (DwarvenReputationHelper.hasEndorsementBypassCreative(player, dwarf.getProfession())) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_ALREADY_ENDORSED).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (!dwarf.canEndorse()) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_CANNOT_ENDORSE).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_NOT_PAID).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.ENDORSE, player, hand, stack);
        return InteractionResult.SUCCESS;
    }
}
