package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.world.player.attachment.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;

@MethodsReturnNonnullByDefault
public final class LanguageGateInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();

        if (!LanguageAttachmentHelper.knowsDwarvish(player)) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARF_LOCKED).withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
