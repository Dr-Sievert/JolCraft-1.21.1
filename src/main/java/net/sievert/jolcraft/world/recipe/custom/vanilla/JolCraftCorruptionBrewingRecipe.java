package net.sievert.jolcraft.world.recipe.custom.vanilla;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.sievert.jolcraft.config.custom.brewing.CorruptionEffectsConfigManager;
import net.sievert.jolcraft.mixin.MobEffectInstanceAccessor;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.alchemy.CorruptionData;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JolCraftCorruptionBrewingRecipe
        implements IBrewingRecipe {

    private static final float ORIGINAL_COLOR_WEIGHT = 0.75F;
    private static final float CORRUPTION_COLOR_WEIGHT = 0.25F;

    private final Ingredient ingredient;

    public JolCraftCorruptionBrewingRecipe(
            Ingredient ingredient
    ) {
        this.ingredient = ingredient;
    }

    @Override
    public boolean isInput(
            @NotNull ItemStack stack
    ) {
        if (!isPotionContainer(stack)
                || !hasBeneficialEffect(stack)) {
            return false;
        }

        CorruptionData corruption =
                stack.get(
                        JolCraftDataComponents.CORRUPTION_DATA.get()
                );

        PotionContents contents =
                stack.get(
                        DataComponents.POTION_CONTENTS
                );

        if (contents == null
                || corruption == null
                && contents.potion().isEmpty()) {
            return false;
        }

        Holder<MobEffect> excludedEffect =
                corruption == null
                        ? null
                        : corruption.corruptionEffect().getEffect();

        return CorruptionEffectsConfigManager.INSTANCE
                .hasEligibleEffect(
                        excludedEffect
                );
    }

    @Override
    public boolean isIngredient(
            @NotNull ItemStack stack
    ) {
        return ingredient.test(
                stack
        );
    }

    @Override
    public @NotNull ItemStack getOutput(
            @NotNull ItemStack inputStack,
            @NotNull ItemStack ingredientStack
    ) {
        if (!isInput(inputStack)
                || !isIngredient(ingredientStack)) {
            return ItemStack.EMPTY;
        }

        CorruptionData previousCorruption =
                inputStack.get(
                        JolCraftDataComponents.CORRUPTION_DATA.get()
                );

        Holder<MobEffect> excludedEffect =
                previousCorruption == null
                        ? null
                        : previousCorruption.corruptionEffect().getEffect();

        Optional<MobEffectInstance> corruptionEffect =
                CorruptionEffectsConfigManager.INSTANCE.roll(
                        excludedEffect
                );

        if (corruptionEffect.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return previousCorruption == null
                ? createFirstCorruption(
                inputStack,
                corruptionEffect.orElseThrow()
        )
                : createRerolledCorruption(
                inputStack,
                previousCorruption,
                corruptionEffect.orElseThrow()
        );
    }

    public static boolean hasBeneficialEffect(
            ItemStack stack
    ) {
        PotionContents contents =
                stack.get(
                        DataComponents.POTION_CONTENTS
                );

        if (contents == null) {
            return false;
        }

        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getEffect().value().isBeneficial()) {
                return true;
            }
        }

        return false;
    }

    public static @NotNull ItemStack createPreviewOutput(
            @NotNull ItemStack inputStack,
            @NotNull MobEffectInstance corruptionEffect
    ) {
        if (!isPotionContainer(inputStack)
                || !hasBeneficialEffect(inputStack)) {
            return ItemStack.EMPTY;
        }

        return createFirstCorruption(
                inputStack,
                corruptionEffect
        );
    }

    private static @NotNull ItemStack createFirstCorruption(
            ItemStack inputStack,
            MobEffectInstance corruptionEffect
    ) {
        PotionContents contents =
                inputStack.get(
                        DataComponents.POTION_CONTENTS
                );

        if (contents == null
                || contents.potion().isEmpty()) {
            return ItemStack.EMPTY;
        }

        Holder<Potion> originalPotion =
                contents.potion().orElseThrow();

        List<MobEffectInstance> effects =
                new ArrayList<>();

        for (MobEffectInstance effect : contents.getAllEffects()) {
            effects.add(
                    applyCorruptionBenefit(
                            effect
                    )
            );
        }

        CorruptionData corruptionData =
                applyCorruptionEffect(
                        effects,
                        originalPotion,
                        corruptionEffect
                );

        int color =
                blendCorruptionColor(
                        contents.getColor()
                );

        ItemStack result =
                inputStack.copyWithCount(
                        1
                );

        result.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.empty(),
                        Optional.of(color),
                        List.copyOf(effects)
                )
        );

        result.set(
                JolCraftDataComponents.CORRUPTION_DATA.get(),
                corruptionData
        );

        return result;
    }

    private static @NotNull ItemStack createRerolledCorruption(
            ItemStack inputStack,
            CorruptionData previousCorruption,
            MobEffectInstance corruptionEffect
    ) {
        PotionContents contents =
                inputStack.get(
                        DataComponents.POTION_CONTENTS
                );

        if (contents == null) {
            return ItemStack.EMPTY;
        }

        List<MobEffectInstance> effects =
                copyEffects(
                        contents.getAllEffects()
                );

        restorePreviousEffect(
                effects,
                previousCorruption
        );

        CorruptionData corruptionData =
                applyCorruptionEffect(
                        effects,
                        previousCorruption.originalPotion(),
                        corruptionEffect
                );

        ItemStack result =
                inputStack.copyWithCount(
                        1
                );

        result.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.empty(),
                        contents.customColor(),
                        List.copyOf(effects)
                )
        );

        result.set(
                JolCraftDataComponents.CORRUPTION_DATA.get(),
                corruptionData
        );

        return result;
    }

    private static @NotNull MobEffectInstance applyCorruptionBenefit(
            MobEffectInstance effect
    ) {
        int amplifier =
                Math.min(
                        MobEffectInstance.MAX_AMPLIFIER,
                        effect.getAmplifier() + 1
                );

        int duration =
                effect.getDuration();

        if (!effect.isInfiniteDuration()
                && !effect.getEffect().value().isInstantenous()) {
            duration =
                    (int) Math.min(
                            Integer.MAX_VALUE,
                            Math.round(
                                    duration * 1.5D
                            )
                    );
        }

        return copyEffect(
                effect,
                duration,
                amplifier
        );
    }

    private static @NotNull CorruptionData applyCorruptionEffect(
            List<MobEffectInstance> effects,
            Holder<Potion> originalPotion,
            MobEffectInstance corruptionEffect
    ) {
        int existingIndex =
                findEffectIndex(
                        effects,
                        corruptionEffect.getEffect()
                );

        Optional<MobEffectInstance> replacedEffect =
                Optional.empty();

        if (existingIndex < 0) {
            effects.add(
                    new MobEffectInstance(
                            corruptionEffect
                    )
            );
        } else {
            MobEffectInstance existing =
                    effects.get(
                            existingIndex
                    );

            replacedEffect =
                    Optional.of(
                            new MobEffectInstance(
                                    existing
                            )
                    );

            effects.set(
                    existingIndex,
                    mergeEffects(
                            existing,
                            corruptionEffect
                    )
            );
        }

        return new CorruptionData(
                originalPotion,
                corruptionEffect,
                replacedEffect
        );
    }

    private static void restorePreviousEffect(
            List<MobEffectInstance> effects,
            CorruptionData corruption
    ) {
        Holder<MobEffect> effect =
                corruption.corruptionEffect().getEffect();

        int index =
                findEffectIndex(
                        effects,
                        effect
                );

        Optional<MobEffectInstance> replacedEffect =
                corruption.replacedEffect();

        if (replacedEffect.isPresent()) {
            MobEffectInstance restored =
                    new MobEffectInstance(
                            replacedEffect.orElseThrow()
                    );

            if (index >= 0) {
                effects.set(
                        index,
                        restored
                );
            } else {
                effects.add(
                        restored
                );
            }

            return;
        }

        if (index >= 0) {
            effects.remove(
                    index
            );
        }
    }

    private static @NotNull MobEffectInstance mergeEffects(
            MobEffectInstance existing,
            MobEffectInstance corruptionEffect
    ) {
        int duration =
                combinedDuration(
                        existing,
                        corruptionEffect
                );

        int amplifier =
                Math.max(
                        existing.getAmplifier(),
                        corruptionEffect.getAmplifier()
                );

        return copyEffect(
                existing,
                duration,
                amplifier
        );
    }

    private static int combinedDuration(
            MobEffectInstance first,
            MobEffectInstance second
    ) {
        if (first.getEffect().value().isInstantenous()) {
            return first.getDuration();
        }

        if (first.isInfiniteDuration()
                || second.isInfiniteDuration()) {
            return MobEffectInstance.INFINITE_DURATION;
        }

        return (int) Math.min(
                Integer.MAX_VALUE,
                (long) first.getDuration()
                        + second.getDuration()
        );
    }

    private static @NotNull MobEffectInstance copyEffect(
            MobEffectInstance source,
            int duration,
            int amplifier
    ) {
        MobEffectInstance copy =
                new MobEffectInstance(
                        source
                );

        MobEffectInstanceAccessor accessor =
                (MobEffectInstanceAccessor) (Object) copy;

        accessor.jolcraft$setDuration(
                duration
        );
        accessor.jolcraft$setAmplifier(
                amplifier
        );

        return copy;
    }

    private static @NotNull List<MobEffectInstance> copyEffects(
            Iterable<MobEffectInstance> source
    ) {
        List<MobEffectInstance> effects =
                new ArrayList<>();

        for (MobEffectInstance effect : source) {
            effects.add(
                    new MobEffectInstance(
                            effect
                    )
            );
        }

        return effects;
    }

    private static int findEffectIndex(
            List<MobEffectInstance> effects,
            Holder<MobEffect> effect
    ) {
        for (int index = 0;
             index < effects.size();
             index++) {
            if (effects.get(index).is(effect)) {
                return index;
            }
        }

        return -1;
    }

    private static int blendCorruptionColor(
            int originalColor
    ) {
        int corruptionColor =
                JolCraftColors.rgb(
                        EssenceType.CORRUPTED.color()
                );

        float totalWeight =
                ORIGINAL_COLOR_WEIGHT
                        + CORRUPTION_COLOR_WEIGHT;

        float corruptionProgress =
                CORRUPTION_COLOR_WEIGHT
                / totalWeight;

        return JolCraftColors.toRgb(
                JolCraftColors.lerpArgb(
                        JolCraftColors.toArgb(
                                originalColor
                        ),
                        JolCraftColors.toArgb(
                                corruptionColor
                        ),
                        corruptionProgress
                )
        );
    }

    private static boolean isPotionContainer(
            ItemStack stack
    ) {
        return stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION);
    }
}