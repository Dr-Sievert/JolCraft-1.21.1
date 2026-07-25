package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarvenBrewItem extends PotionItem {

    public DwarvenBrewItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(
                JolCraftLanguageKeys.BREW_AGE_NAME,
                Component.translatable(DwarvenBrewAge.fromStack(stack).translationKey()),
                super.getName(stack)
        );
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player playerEntity
                ? playerEntity
                : null;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        if (!level.isClientSide) {
            PotionContents potionContents = stack.getOrDefault(
                    DataComponents.POTION_CONTENTS,
                    PotionContents.EMPTY
            );

            potionContents.forEachEffect(effect -> {
                if (effect.getEffect().value().isInstantenous()) {
                    effect.getEffect().value().applyInstantenousEffect(
                            player,
                            player,
                            entityLiving,
                            effect.getAmplifier(),
                            1.0D
                    );
                } else {
                    entityLiving.addEffect(effect);
                }
            });

            entityLiving.addEffect(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0)
            );
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.consume(1, player);
        }

        if (player == null || !player.isCreative()) {
            if (stack.isEmpty()) {
                return new ItemStack(JolCraftItems.GLASS_MUG.get());
            }

            if (player != null) {
                ItemStack mug = new ItemStack(JolCraftItems.GLASS_MUG.get());

                if (!player.getInventory().add(mug)) {
                    player.drop(mug, false);
                }
            }
        }

        entityLiving.gameEvent(GameEvent.DRINK);
        return stack;
    }
}