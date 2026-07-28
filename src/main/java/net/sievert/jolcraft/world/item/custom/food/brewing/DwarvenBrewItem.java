package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.event.game.world.time.JolCraftTimeHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarvenBrewItem extends PotionItem {

    public DwarvenBrewItem(
            Properties properties
    ) {
        super(
                properties
        );
    }

    @Override
    public ItemStack getDefaultInstance() {
        return new ItemStack(
                this
        );
    }

    @Override
    public String getDescriptionId(
            ItemStack stack
    ) {
        return getDescriptionId();
    }

    @Override
    public InteractionResult useOn(
            UseOnContext context
    ) {
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(
            ItemStack stack
    ) {
        return Component.translatable(
                JolCraftLanguageKeys.BREW_AGE_NAME,
                Component.translatable(
                        DwarvenBrewAge.fromStack(
                                stack
                        ).translationKey()
                ),
                super.getName(
                        stack
                )
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
        FluidStack brew =
                DwarvenBrewFluidHelper.getBrewFromMug(
                        stack
                );

        if (!brew.isEmpty()) {
            PotionContents contents =
                    brew.getOrDefault(
                            DataComponents.POTION_CONTENTS,
                            PotionContents.EMPTY
                    );

            contents.addPotionTooltip(
                    tooltip::add,
                    1.0F,
                    (float) JolCraftTimeHelper.TICKS_PER_SECOND
            );
        }

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }

    @Override
    public ItemStack finishUsingItem(
            ItemStack stack,
            Level level,
            LivingEntity entity
    ) {
        FluidStack brew =
                DwarvenBrewFluidHelper.getBrewFromMug(
                        stack
                );

        ItemStack remaining =
                entity.eat(
                        level,
                        stack
                );

        if (!level.isClientSide) {
            applyBrewEffects(
                    brew,
                    entity
            );

            entity.addEffect(
                    new MobEffectInstance(
                            MobEffects.CONFUSION,
                            200,
                            0
                    )
            );
        }

        if (entity instanceof Player player
                && player.isCreative()) {
            return remaining;
        }

        ItemStack emptyMug =
                new ItemStack(
                        JolCraftItems.GLASS_MUG.get()
                );

        if (remaining.isEmpty()) {
            return emptyMug;
        }

        if (entity instanceof Player player
                && !player.getInventory().add(
                emptyMug
        )) {
            player.drop(
                    emptyMug,
                    false
            );
        }

        return remaining;
    }

    private static void applyBrewEffects(
            FluidStack brew,
            LivingEntity entity
    ) {
        if (brew.isEmpty()) {
            return;
        }

        PotionContents contents =
                brew.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                );

        contents.forEachEffect(
                effect -> applyEffect(
                        effect,
                        entity
                )
        );
    }

    private static void applyEffect(
            MobEffectInstance effect,
            LivingEntity entity
    ) {
        if (effect.getEffect()
                .value()
                .isInstantenous()) {
            effect.getEffect()
                    .value()
                    .applyInstantenousEffect(
                            entity,
                            entity,
                            entity,
                            effect.getAmplifier(),
                            1.0D
                    );

            return;
        }

        entity.addEffect(
                new MobEffectInstance(
                        effect
                )
        );
    }
}