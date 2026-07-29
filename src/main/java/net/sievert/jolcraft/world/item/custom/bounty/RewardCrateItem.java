package net.sievert.jolcraft.world.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RewardCrateItem extends Item {

    private static final int OPEN_DURATION_TICKS = 40;

    public RewardCrateItem(
            Properties properties
    ) {
        super(properties);
    }

    /**
     * Prefixes the reward crate name with its current rarity tier.
     */
    @Override
    public Component getName(ItemStack stack) {
        Rarity rarity = stack.get(DataComponents.RARITY);

        return Component.translatable(
                JolCraftLanguageKeys.TOOLTIP_RARITY_NAME,
                Component.translatable(nameForRarity(Objects.requireNonNull(rarity))),
                super.getName(stack)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (emptyRewards(stack)) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(
            ItemStack stack,
            LivingEntity entity
    ) {
        return OPEN_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(
            ItemStack stack
    ) {
        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(
            ItemStack stack,
            Level level,
            LivingEntity entity
    ) {
        if (!(entity instanceof ServerPlayer player) || emptyRewards(stack)) {
            return stack;
        }

        List<ItemStack> rewards = stack.get(
                JolCraftDataComponents.BOUNTY_REWARDS.get()
        );

        for (ItemStack reward : Objects.requireNonNull(rewards)) {
            if (reward == null || reward.isEmpty()) {
                continue;
            }

            JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                    player,
                    reward.copy()
            );
        }

        JolCraftSoundHelper.player(
                player,
                SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                0.60F,
                0.75F
        );

        JolCraftItemHelper.consume(
                player,
                player.getUsedItemHand()
        );

        return player.getItemInHand(
                player.getUsedItemHand()
        );
    }

    private static boolean emptyRewards(
            ItemStack stack
    ) {
        List<ItemStack> rewards = stack.get(JolCraftDataComponents.BOUNTY_REWARDS.get());

        if (rewards == null || rewards.isEmpty()) {
            return true;
        }

        return rewards.stream().noneMatch(reward -> reward != null && !reward.isEmpty());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        if(emptyRewards(stack)) return;

        JolCraftTooltipHelper.addAltTooltip(
                tooltip,
                Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_REWARD_CRATE
                ).withStyle(ChatFormatting.GRAY),
                List.of()
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }

    private static @NotNull String nameForRarity(
            @NotNull Rarity rarity
    ) {
        if (rarity == JolCraftEnumExtensions.Rarity.LEGENDARY.getValue()) {
            return JolCraftLanguageKeys.RARITY_LEGENDARY;
        }

        return switch (rarity) {
            case COMMON -> JolCraftLanguageKeys.RARITY_COMMON;
            case UNCOMMON -> JolCraftLanguageKeys.RARITY_UNCOMMON;
            case RARE -> JolCraftLanguageKeys.RARITY_RARE;
            case EPIC -> JolCraftLanguageKeys.RARITY_EPIC;
        };
    }
}