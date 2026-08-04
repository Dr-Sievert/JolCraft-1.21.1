package net.sievert.jolcraft.world.item.custom.crate;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.loot.custom.reward.RewardCrateLootResolver;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RewardCrateItem extends Item {

    private static final int OPEN_DURATION_TICKS = 40;

    public RewardCrateItem(
            Properties properties
    ) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack =
                player.getItemInHand(
                        hand
                );

        if (noLootSource(stack)) {
            return InteractionResultHolder.fail(
                    stack
            );
        }

        player.startUsingItem(
                hand
        );

        return InteractionResultHolder.consume(
                stack
        );
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
        if (!(entity instanceof ServerPlayer player)) {
            return stack;
        }

        RewardCrateSource source =
                stack.get(
                        JolCraftDataComponents.REWARD_CRATE_SOURCE.get()
                );

        if (source == null) {
            return stack;
        }

        Optional<List<ItemStack>> resolved =
                RewardCrateLootResolver.generate(
                        player,
                        source
                );

        if (resolved.isEmpty()) {
            return stack;
        }

        for (ItemStack reward : resolved.get()) {
            if (reward == null || reward.isEmpty()) {
                continue;
            }

            JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                    player,
                    reward
            );
        }

        JolCraftSoundHelper.player(
                player,
                SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                0.30F,
                0.60F
        );

        JolCraftItemHelper.consume(
                player,
                player.getUsedItemHand()
        );

        return player.getItemInHand(
                player.getUsedItemHand()
        );
    }

    private static boolean noLootSource(
            ItemStack stack
    ) {
        return !stack.has(
                JolCraftDataComponents.REWARD_CRATE_SOURCE.get()
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        if (noLootSource(stack)) {
            return;
        }

        Component.translatable(JolCraftLanguageKeys.TOOLTIP_REWARD_CRATE).withStyle(ChatFormatting.GRAY);

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}
