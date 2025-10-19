package net.sievert.jolcraft.entity.util.dwarf.interaction.type.reputation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class ReputationGainInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftTags.Items.REPUTATION_TABLETS)) {
            return InteractionResult.FAIL;
        }

        boolean client = dwarf.level().isClientSide();
        int maxTier = DwarvenReputationImpl.getThresholdCount();
        int strictTier = client ? DwarvenReputationHelper.getClientTier() : DwarvenReputationHelper.getTierBypassCreative(player);
        int strictEndorsementCount = client ?
                DwarvenReputationHelper.getClientEndorsementCount() : DwarvenReputationHelper.getEndorsementCountBypassCreative(player);

        if (strictTier >= maxTier) {
            if (client) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.reputation.max_tier").withStyle(ChatFormatting.GRAY), true
                );
            }
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (!DwarvenReputationImpl.canAdvance(strictTier, strictEndorsementCount)) {
            if (!client) {
                int needed = DwarvenReputationImpl.getThresholdForTier(strictTier);
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.reputation.not_enough_endorsements", needed, strictEndorsementCount)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            if (!client) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY), true
                );
            }
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.REPUTATION_GAIN, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
