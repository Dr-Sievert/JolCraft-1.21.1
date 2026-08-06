package net.sievert.jolcraft.world.item.custom.instrument;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.entity.effect.cure.JolCraftEffectCures;
import net.sievert.jolcraft.world.item.instrument.JolCraftInstruments;
import org.jetbrains.annotations.NotNull;

public class WarHornItem extends InstrumentItem {

    private static final double CURE_RADIUS = 10.0D;
    private static final int COOLDOWN_TICKS = 30 * 20;

    public WarHornItem(Properties properties) {
        super(properties, JolCraftTags.Instruments.WAR_HORNS);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return InstrumentItem.create(
                this,
                JolCraftInstruments.WAR_HORN
        );
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand usedHand
    ) {
        InteractionResultHolder<ItemStack> result =
                super.use(level, player, usedHand);

        if (!result.getResult().consumesAction()) {
            return result;
        }

        player.getCooldowns().addCooldown(
                this,
                COOLDOWN_TICKS
        );

        if (!level.isClientSide) {
            level.getEntitiesOfClass(
                    Player.class,
                    player.getBoundingBox().inflate(CURE_RADIUS),
                    Player::isAlive
            ).forEach(WarHornItem::clearCurableHarmfulEffects);
        }

        return result;
    }

    private static void clearCurableHarmfulEffects(
            @NotNull Player player
    ) {
        player.getActiveEffects()
                .stream()
                .filter(WarHornItem::isCurableHarmfulEffect)
                .map(MobEffectInstance::getEffect)
                .toList()
                .forEach(player::removeEffect);
    }

    private static boolean isCurableHarmfulEffect(
            @NotNull MobEffectInstance effect
    ) {
        return effect.getEffect()
                .value()
                .getCategory()
                == MobEffectCategory.HARMFUL
                && (
                effect.getCures().contains(EffectCures.MILK)
                        || effect.getCures().contains(EffectCures.PROTECTED_BY_TOTEM)
                        || effect.getCures().contains(JolCraftEffectCures.WAR_HORN)
        );
    }
}