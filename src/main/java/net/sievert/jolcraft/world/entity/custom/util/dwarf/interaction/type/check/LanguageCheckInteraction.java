package net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class LanguageCheckInteraction implements DwarfInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player) {
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);

        if (!knowsLanguage) {
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_LOCKED).withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

}