package net.sievert.jolcraft.entity.util.dwarf.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.util.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class LanguageCheckInteraction implements DwarfInteraction {

    @Override
    public InteractionResult handle(AbstractEntityEntity dwarf, Player player) {
        boolean client = dwarf.level().isClientSide;
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);
        if (!knowsLanguage) {
            if(client){return InteractionResult.CONSUME;}
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.language.locked").withStyle(ChatFormatting.RED), true);
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }
}