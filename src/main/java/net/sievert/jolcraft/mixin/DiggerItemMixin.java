package net.sievert.jolcraft.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.entity.attachment.player.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DiggerItem.class)
@SuppressWarnings({"AddedMixinMembersNamePattern", "unused"})
public abstract class DiggerItemMixin {

    @Unique
    private static final int JOLCRAFT$CHANNEL_DURATION_TICKS = 3 * 20;

    @Unique
    private static final int JOLCRAFT$RHYTHM_DURATION_TICKS = 60 * 20;

    @Unique
    private static final int JOLCRAFT$RHYTHM_COOLDOWN_TICKS = 180 * 20;

    @Unique
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND
                || !stack.is(ItemTags.PICKAXES)
                || !player.getOffhandItem().isEmpty()
                || player.getCooldowns().isOnCooldown(stack.getItem())
                || !DwarfLoreAttachmentHelper.hasUnlock(
                player,
                DwarfLoreKey.MINING_RHYTHM
        )) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Unique
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return stack.is(ItemTags.PICKAXES)
                ? JOLCRAFT$CHANNEL_DURATION_TICKS
                : 0;
    }

    @Unique
    public UseAnim getUseAnimation(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES)
                ? UseAnim.BLOCK
                : UseAnim.NONE;
    }

    @Unique
    public ItemStack finishUsingItem(
            ItemStack stack,
            Level level,
            LivingEntity entity
    ) {
        if (!(entity instanceof Player player)
                || !stack.is(ItemTags.PICKAXES)
                || !player.getOffhandItem().isEmpty()
                || player.getCooldowns().isOnCooldown(stack.getItem())
                || !DwarfLoreAttachmentHelper.hasUnlock(
                player,
                DwarfLoreKey.MINING_RHYTHM
        )) {
            return stack;
        }

        if (!level.isClientSide) {

            if(!player.isCreative()){
                BuiltInRegistries.ITEM
                        .getTag(ItemTags.PICKAXES)
                        .ifPresent(pickaxes -> pickaxes.forEach(holder ->
                                player.getCooldowns().addCooldown(
                                        holder.value(),
                                        JOLCRAFT$RHYTHM_COOLDOWN_TICKS
                                )
                        ));
            }

            player.addEffect(new MobEffectInstance(
                    JolCraftEffects.DWARVEN_HASTE,
                    JOLCRAFT$RHYTHM_DURATION_TICKS,
                    0,
                    true,
                    false,
                    true
            ));

            JolCraftSoundHelper.player(
                    player,
                    SoundEvents.ANVIL_LAND,
                    0.6F,
                    1.25F
            );
        }

        return stack;
    }
}