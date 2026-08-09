package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public final class LanguageGateInteractionHandler
        implements DwarfInteractions.CoreInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.@NotNull DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();

        if (!LanguageAttachmentHelper.knowsDwarvish(player)) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_DWARF_LOCKED
                    ).withStyle(ChatFormatting.RED),
                    true
            );

            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.handled();
        }

        return DwarfInteractionOutcome.pass();
    }
}