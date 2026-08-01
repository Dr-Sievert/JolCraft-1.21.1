package net.sievert.jolcraft.world.item.custom.paper;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class QuillItem extends SimpleTooltipItem {

    public QuillItem(Properties properties, String tooltipTranslationKey) {
        super(properties, tooltipTranslationKey);
    }

    @Override
    public InteractionResult interactLivingEntity(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand
    ) {
        if (target.getType() != EntityType.SQUID
                || stack.is(JolCraftItems.QUILL_FULL.get())) {
            return super.interactLivingEntity(stack, player, target, hand);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack fullQuill = new ItemStack(JolCraftItems.QUILL_FULL.get());

            if (stack.getCount() == 1) {
                serverPlayer.setItemInHand(hand, fullQuill);
            } else {
                JolCraftItemHelper.consume(serverPlayer, hand);
                JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                        serverPlayer,
                        fullQuill
                );
            }

            PlaySound.bottleFill(serverPlayer, 1.0F, 1.5F);
        }

        return InteractionResult.SUCCESS;
    }
}
