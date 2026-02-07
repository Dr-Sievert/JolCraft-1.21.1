package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PromoteInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!stack.is(JolCraftTags.Items.PROFESSION_CONTRACTS)) {
            return InteractionResult.PASS;
        }

        boolean promotable = dwarf.isAlive()
                && !dwarf.isBaby()
                && dwarf.getType() == JolCraftEntities.DWARF.get();

        if (!promotable) {
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_CANNOT_PROMOTE).withStyle(ChatFormatting.GRAY),
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

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.PROMOTE, player, hand, stack);
        return InteractionResult.SUCCESS;
    }
}
