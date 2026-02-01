package net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class ActionCheckInteraction implements DwarfInteraction {

    private boolean isBusy(AbstractDwarfEntity dwarf) {
        return !DwarfActionHelper.isActionType(dwarf, DwarfActionType.IDLE)
                || dwarf.isTrading();
    }

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player) {

        if (isBusy(dwarf)) {
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_BUSY).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
