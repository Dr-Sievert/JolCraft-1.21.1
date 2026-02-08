package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;
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

        int reputationTier = DwarvenReputationHelper.getTier(player);

        int desiredLevel = Math.min(
                reputationTier + 1,
                DwarfMerchantData.MAX_MERCHANT_LEVEL
        );

        int currentLevel = dwarf.getMerchantLevel();

        if (currentLevel < desiredLevel) {
            if (dwarf.getOffers().isEmpty()) {
                dwarf.updateTrades();
            }

            for (int level = currentLevel; level < desiredLevel; level++) {
                dwarf.increaseMerchantCareer();
            }
        }
    }

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        // Reputation tablet action
        if (stack.is(JolCraftTags.Items.REPUTATION_TABLETS)) {
            int maxTier = DwarvenReputationImpl.getThresholdCount();
            int tier = DwarvenReputationHelper.getTier(player);
            int endorsementCount = DwarvenReputationHelper.getEndorsementCount(player);

            if (tier >= maxTier) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_REPUTATION_MAX_TIER)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                PlaySound.dwarfNo(dwarf);
                return InteractionResult.SUCCESS;
            }

            if (!DwarvenReputationImpl.canAdvance(tier, endorsementCount)) {
                int needed = DwarvenReputationImpl.getThresholdForTier(tier);
                player.displayClientMessage(
                        Component.translatable(
                                JolCraftLanguageKeys.TOOLTIP_REPUTATION_NOT_ENOUGH_ENDORSEMENTS,
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