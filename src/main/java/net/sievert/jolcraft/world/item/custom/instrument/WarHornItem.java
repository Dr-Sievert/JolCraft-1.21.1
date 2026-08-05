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
import net.sievert.jolcraft.world.item.instrument.JolCraftInstruments;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WarHornItem extends InstrumentItem {

    private static final double CURE_RADIUS = 10.0D;

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

        if (!level.isClientSide && result.getResult().consumesAction()) {
            level.getEntitiesOfClass(
                    Player.class,
                    player.getBoundingBox().inflate(CURE_RADIUS),
                    Player::isAlive
            ).forEach(WarHornItem::clearDefaultHarmfulEffects);
        }

        return result;
    }

    private static void clearDefaultHarmfulEffects(
            @NotNull Player player
    ) {
        List<MobEffectInstance> removableEffects =
                player.getActiveEffects()
                        .stream()
                        .filter(effect ->
                                effect.getEffect()
                                        .value()
                                        .getCategory()
                                        == MobEffectCategory.HARMFUL
                        )
                        .filter(effect ->
                                effect.getCures()
                                        .equals(EffectCures.DEFAULT_CURES)
                        )
                        .toList();

        removableEffects.forEach(
                effect -> player.removeEffect(effect.getEffect())
        );
    }
}