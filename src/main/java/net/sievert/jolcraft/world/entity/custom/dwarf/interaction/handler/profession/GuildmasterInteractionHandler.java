package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.player.attachment.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class GuildmasterInteractionHandler
        implements DwarfInteractions.ProfessionInteraction, DwarfInteractions.DwarfInteractionHooks {

    @Override
    public void preCore(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();

        int reputationTier = DwarvenReputationAttachmentHelper.getTier(player);

        int desiredLevel = Math.min(
                reputationTier + 1,
                DwarfMerchantData.MAX_MERCHANT_LEVEL
        );

        int currentLevel = dwarf.getMerchantLevel();

        if (currentLevel >= desiredLevel) {
            return;
        }

        if (dwarf.getOffers().isEmpty()) {
            dwarf.updateTrades();
        }

        for (int level = currentLevel; level < desiredLevel; level++) {
            dwarf.increaseMerchantCareer();
        }

        PlaySound.dwarfYes(dwarf);
    }

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (stack.is(JolCraftTags.Items.REPUTATION_TABLETS)) {
            int maxTier = DwarvenReputationAttachmentHelper.getMaxTier();
            int tier = DwarvenReputationAttachmentHelper.getTier(player);
            int endorsementCount = DwarvenReputationAttachmentHelper.getEndorsementCount(player);

            if (tier >= maxTier) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_MAX_TIER)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            if (!DwarvenReputationAttachmentHelper.canAdvance(player)) {
                int needed = DwarvenReputationAttachmentHelper.getThresholdForTier(tier);
                player.displayClientMessage(
                        Component.translatable(
                                JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_NOT_ENOUGH_ENDORSEMENTS,
                                needed,
                                endorsementCount
                        ).withStyle(ChatFormatting.GRAY),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            if (dwarf.needsPay()) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARF_NOT_PAID)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.REPUTATION_GAIN, player, hand, stack);
            return InteractionResult.SUCCESS;
        }

        PlaySound.dwarfNo(dwarf);
        return InteractionResult.FAIL;
    }
}