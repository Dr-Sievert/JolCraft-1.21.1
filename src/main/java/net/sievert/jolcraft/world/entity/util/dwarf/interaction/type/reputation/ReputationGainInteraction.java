package net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.reputation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.datagen.language.subprovider.ReputationLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class ReputationGainInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftTags.Items.REPUTATION_TABLETS)) {
            return InteractionResult.FAIL;
        }

        boolean client = dwarf.level().isClientSide();
        int maxTier = DwarvenReputationImpl.getThresholdCount();
        int strictTier = DwarvenReputationHelper.getTier(player);
        int strictEndorsementCount = DwarvenReputationHelper.getEndorsementCount(player);

        if (strictTier >= maxTier) {
            if (client) {
                player.displayClientMessage(
                        Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_MAX_TIER).withStyle(ChatFormatting.GRAY), true
                );
            }
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (!DwarvenReputationImpl.canAdvance(strictTier, strictEndorsementCount)) {
            if (!client) {
                int needed = DwarvenReputationImpl.getThresholdForTier(strictTier);
                player.displayClientMessage(
                        Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS, needed, strictEndorsementCount)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            if (!client) {
                player.displayClientMessage(
                        Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_NOT_PAID).withStyle(ChatFormatting.GRAY), true
                );
            }
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.REPUTATION_GAIN, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
