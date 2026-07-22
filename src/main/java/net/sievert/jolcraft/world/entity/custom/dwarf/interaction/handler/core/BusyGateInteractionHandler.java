package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BusyGateInteractionHandler
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

        boolean busy =
                !DwarfActionHelper.isActionType(
                        dwarf,
                        DwarfActionType.IDLE
                )
                        || dwarf.isTrading();

        if (busy) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_DWARF_BUSY
                    ).withStyle(ChatFormatting.GRAY),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        return DwarfInteractionOutcome.pass();
    }
}