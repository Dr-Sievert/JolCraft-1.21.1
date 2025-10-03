package net.sievert.jolcraft.entity.util.dwarf.interaction.type.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class SignInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractEntityEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        boolean client = dwarf.level().isClientSide;
        assert itemstack != null;
        if (itemstack.is(JolCraftItems.CONTRACT_WRITTEN.get())) {
            if (!dwarf.canSign()) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.dwarf.cannot_sign").withStyle(ChatFormatting.GRAY), true
                );
                JolCraftSoundHelper.playDwarfNo(dwarf);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            if (dwarf.needsPay()) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY), true
                );
                JolCraftSoundHelper.playDwarfNo(dwarf);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.CONTRACT_SIGNING, player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
