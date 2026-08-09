package net.sievert.jolcraft.world.item.custom.tablet;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachment;
import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReputationTabletItem extends Item {

    public ReputationTabletItem(Properties properties) {
        super(properties);
    }

    private static final int[] ENDORSEMENT_THRESHOLDS = DwarvenReputationAttachment.ENDORSEMENT_THRESHOLDS;

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!LanguageAttachmentHelper.knowsDwarvish(serverPlayer)) {
                serverPlayer.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_STONE_LOCKED).withStyle(ChatFormatting.GRAY),
                        true
                );
                return InteractionResultHolder.success(stack);
            }

            int currentTier = DwarvenReputationAttachmentHelper.getTier(serverPlayer);
            int endorsements = DwarvenReputationAttachmentHelper.getEndorsementCount(serverPlayer);

            if (currentTier >= ENDORSEMENT_THRESHOLDS.length) {
                serverPlayer.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_MAX_TIER).withStyle(ChatFormatting.GRAY),
                        true
                );
            } else {
                int needed = ENDORSEMENT_THRESHOLDS[currentTier];
                serverPlayer.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_REPUTATION_PROGRESS, endorsements, needed)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }

            JolCraftSoundHelper.player(player, SoundEvents.CHISELED_BOOKSHELF_INSERT, 1.0F, 0.5F);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            int tier = DwarvenReputationAttachmentHelper.getTier(serverPlayer);
            int endorsements = DwarvenReputationAttachmentHelper.getEndorsementCount(serverPlayer);

            stack.set(JolCraftDataComponents.REPUTATION_OWNER.get(), serverPlayer.getName().getString());
            stack.set(JolCraftDataComponents.REPUTATION_TIER.get(), tier);
            stack.set(JolCraftDataComponents.REPUTATION_ENDORSEMENTS.get(), endorsements);
        }
        super.onCraftedBy(stack, level, player);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        if (LanguageAttachmentHelper.knowsDwarvish(player)) {
            String ownerName = stack.getOrDefault(JolCraftDataComponents.REPUTATION_OWNER.get(), JolCraftDictionary.UNKNOWN);
            int statictier = stack.getOrDefault(JolCraftDataComponents.REPUTATION_TIER.get(), 0);
            int staticendorsements = stack.getOrDefault(JolCraftDataComponents.REPUTATION_ENDORSEMENTS.get(), 0);
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_TABLET_OWNER, ownerName)
                        .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_REPUTATION)
                        .append(Component.translatable(JolCraftStrings.dotted(JolCraft.MOD_ID,
                                JolCraftStrings.dotted(JolCraftStrings.underscored(JolCraftAttachmentIds.DWARVEN_REPUTATION, JolCraftDictionary.TIER),
                                        String.valueOf(statictier)))))
                        .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS, staticendorsements)
                        .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_STONE_LOCKED)
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
