package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome.HeldItemUse;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionTraits;
import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EndorseInteractionHandler
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
        var stack = ctx.stack();

        if (!stack.is(
                JolCraftTags.Items.REPUTATION_TABLETS
        )) {
            return DwarfInteractionOutcome.pass();
        }

        if (
                dwarf.getProfession() == DwarfProfession.GUILDMASTER
        ) {
            return DwarfInteractionOutcome.pass();
        }

        if (!DwarfProfessionTraits.canEndorseFlag(
                dwarf.getProfession()
        )) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys
                                    .TOOLTIP_DWARVEN_REPUTATION_NEVER_ENDORSE
                    ).withStyle(
                            ChatFormatting.GRAY
                    ),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        if (
                DwarvenReputationAttachmentHelper
                        .hasEndorsementBypassCreative(
                                player,
                                dwarf.getProfession()
                        )
        ) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys
                                    .TOOLTIP_DWARVEN_REPUTATION_ALREADY_ENDORSED
                    ).withStyle(
                            ChatFormatting.GRAY
                    ),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        if (!player.isCreative() && !dwarf.canEndorse()) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys
                                    .TOOLTIP_DWARVEN_REPUTATION_CANNOT_ENDORSE
                    ).withStyle(
                            ChatFormatting.GRAY
                    ),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys
                                    .TOOLTIP_DWARF_NOT_PAID
                    ).withStyle(
                            ChatFormatting.GRAY
                    ),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        return DwarfInteractionOutcome.startAction(
                DwarfActionType.Subtype.ENDORSE,
                HeldItemUse.CONSUME_ONE
        );
    }
}