package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.player.attachment.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
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

        if (dwarf.getProfession() == DwarfProfession.GUILDMASTER) {
            return InteractionResult.PASS;
        }

        if (!dwarf.canEndorse()) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_NEVER_ENDORSE).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (DwarvenReputationAttachmentHelper.hasEndorsementBypassCreative(player, dwarf.getProfession())) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_ALREADY_ENDORSED).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (!dwarf.canEndorse()) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_CANNOT_ENDORSE).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARF_NOT_PAID).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.ENDORSE, player, hand, stack);
        return InteractionResult.SUCCESS;
    }
}
