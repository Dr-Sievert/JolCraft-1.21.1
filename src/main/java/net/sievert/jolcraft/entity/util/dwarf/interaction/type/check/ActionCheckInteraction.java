package net.sievert.jolcraft.entity.util.dwarf.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class ActionCheckInteraction implements DwarfInteraction {

    private boolean isBusy(AbstractDwarfEntity dwarf) {
        return DwarfActionHelper.isActionType(dwarf, DwarfActionType.INSPECT)
                || DwarfActionHelper.isActionType(dwarf, DwarfActionType.DRINK);
    }

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player) {

        if (isBusy(dwarf)) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.dwarf.busy").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}
