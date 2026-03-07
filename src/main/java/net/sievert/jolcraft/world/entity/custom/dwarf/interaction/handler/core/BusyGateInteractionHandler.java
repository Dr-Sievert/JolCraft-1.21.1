package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BusyGateInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();

        if (!DwarfActionHelper.isActionType(dwarf, DwarfActionType.IDLE) || dwarf.isTrading()) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARF_BUSY).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
