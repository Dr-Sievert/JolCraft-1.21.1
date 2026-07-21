package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome.HeldItemUse;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class SignContractInteractionHandler
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

        if (!stack.is(JolCraftItems.CONTRACT_WRITTEN.get())) {
            return DwarfInteractionOutcome.pass();
        }

        if (!dwarf.canSign()) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_DWARF_CANNOT_SIGN
                    ).withStyle(ChatFormatting.GRAY),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_DWARF_NOT_PAID
                    ).withStyle(ChatFormatting.GRAY),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        return DwarfInteractionOutcome.startAction(
                DwarfActionType.Subtype.CONTRACT_SIGNING,
                HeldItemUse.CONSUME_ONE
        );
    }
}