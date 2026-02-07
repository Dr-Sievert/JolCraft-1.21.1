package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class LanguageGateInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();

        if (!DwarvenLanguageHelper.knowsDwarvish(player)) {
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_LOCKED).withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
