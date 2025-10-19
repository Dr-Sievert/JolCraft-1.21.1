package net.sievert.jolcraft.entity.util.dwarf.interaction.type.reputation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.custom.dwarf.profession.DwarfGuildmasterEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class EndorseInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftTags.Items.REPUTATION_TABLETS) || dwarf instanceof DwarfGuildmasterEntity) {
            return InteractionResult.FAIL;
        }

        DwarfProfession profession = dwarf.getProfession();
        boolean hasEndorsement = DwarvenReputationHelper.hasEndorsementBypassCreative(player, profession);

        if (dwarf.neverEndorse()) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.reputation.never_endorse").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (hasEndorsement) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.reputation.already_endorsed").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (!dwarf.canEndorse()) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.reputation.cannot_endorse").withStyle(ChatFormatting.GRAY),
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

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.ENDORSE, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
