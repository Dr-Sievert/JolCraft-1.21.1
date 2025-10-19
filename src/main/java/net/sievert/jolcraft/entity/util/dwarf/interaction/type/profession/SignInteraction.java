package net.sievert.jolcraft.entity.util.dwarf.interaction.type.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class SignInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftItems.CONTRACT_WRITTEN.get())) {
            return InteractionResult.FAIL;
        }

        if (!dwarf.canSign()) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.dwarf.cannot_sign").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.CONTRACT_SIGNING, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
