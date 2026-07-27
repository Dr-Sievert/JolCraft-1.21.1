package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewAge;

import java.util.ArrayList;
import java.util.List;

public final class DwarvenBrewFluidHelper {

    public static final int MUGS_PER_BUCKET = 3;

    public static final int MUG_VOLUME =
            FluidType.BUCKET_VOLUME / MUGS_PER_BUCKET;

    public static final int FIRST_MUG_VOLUME =
            FluidType.BUCKET_VOLUME - MUG_VOLUME * 2;

    private DwarvenBrewFluidHelper() {}

    // =====================================================================
    // Fluid identification
    // =====================================================================

    public static boolean isFinishedBrew(
            FluidStack brew
    ) {
        return !brew.isEmpty()
                && brew.is(
                JolCraftFluids.DWARVEN_BREW.get()
        );
    }

    public static boolean isUnfinishedBrew(
            FluidStack brew
    ) {
        return !brew.isEmpty()
                && brew.is(
                JolCraftFluids.UNFINISHED_DWARVEN_BREW.get()
        );
    }

    public static boolean containsDwarvenBrew(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(
                JolCraftItems.DWARVEN_BREW.get()
        )) {
            return true;
        }

        return FluidUtil.getFluidContained(
                        stack
                )
                .filter(
                        fluid -> fluid.is(
                                JolCraftFluids.DWARVEN_BREW.get()
                        )
                )
                .isPresent();
    }

    // =====================================================================
    // Mug volumes
    // =====================================================================

    public static int getMugDrainAmount(
            int storedAmount
    ) {
        if (storedAmount == FluidType.BUCKET_VOLUME) {
            return FIRST_MUG_VOLUME;
        }

        if (storedAmount >= MUG_VOLUME
                && storedAmount <= FIRST_MUG_VOLUME) {
            return storedAmount;
        }

        return storedAmount >= MUG_VOLUME
                ? MUG_VOLUME
                : 0;
    }

    public static int getMugFillAmount(
            int storedAmount,
            int capacity
    ) {
        int remaining = capacity - storedAmount;

        if (remaining < MUG_VOLUME) {
            return 0;
        }

        if (storedAmount == 0
                || remaining == FIRST_MUG_VOLUME) {
            return FIRST_MUG_VOLUME;
        }

        return MUG_VOLUME;
    }

    // =====================================================================
    // Mug conversion
    // =====================================================================

    public static ItemStack createBrewMug(
            FluidStack brew
    ) {
        ItemStack mug = new ItemStack(
                JolCraftItems.DWARVEN_BREW.get()
        );

        mug.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                brew.getOrDefault(
                        JolCraftDataComponents.BREW_COLOR.get(),
                        0xFFFFFFFF
                )
        );

        mug.set(
                JolCraftDataComponents.BREW_AGE.get(),
                getAge(brew)
        );

        mug.set(
                DataComponents.POTION_CONTENTS,
                brew.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );

        return mug;
    }

    public static FluidStack createBrewFluidFromMug(
            ItemStack mug,
            int amount
    ) {
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack brew = new FluidStack(
                JolCraftFluids.DWARVEN_BREW.get(),
                amount
        );

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                mug.getOrDefault(
                        JolCraftDataComponents.BREW_COLOR.get(),
                        0xFFFFFFFF
                )
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                Math.max(
                        0L,
                        mug.getOrDefault(
                                JolCraftDataComponents.BREW_AGE.get(),
                                0L
                        )
                )
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                mug.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );

        return brew;
    }

    // =====================================================================
    // Age access and mutation
    // =====================================================================

    public static long getAge(
            FluidStack brew
    ) {
        return Math.max(
                0L,
                brew.getOrDefault(
                        JolCraftDataComponents.BREW_AGE.get(),
                        0L
                )
        );
    }

    /**
     * Returns a copy representing the underlying brew at age zero.
     * Both the age component and all age-derived amplifier bonuses are removed.
     */
    public static FluidStack withFreshAge(
            FluidStack brew
    ) {
        if (brew.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack fresh = withoutAging(
                brew
        );

        fresh.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        return fresh;
    }

    /**
     * Adds age directly to the supplied stack and applies any newly earned
     * amplifier increases.
     *
     * @return whether the stack was changed
     */
    public static boolean addAgeInPlace(
            FluidStack brew,
            long addedTicks
    ) {
        if (brew.isEmpty() || addedTicks <= 0L) {
            return false;
        }

        long previousAgeTicks = getAge(
                brew
        );

        long currentAgeTicks = addClamped(
                previousAgeTicks,
                addedTicks
        );

        if (currentAgeTicks <= previousAgeTicks) {
            return false;
        }

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                currentAgeTicks
        );

        applyAgeAmplifierIncrease(
                brew,
                previousAgeTicks,
                currentAgeTicks
        );

        return true;
    }

    // =====================================================================
    // Comparison
    // =====================================================================

    /**
     * Compares stacks after removing only their BREW_AGE component.
     *
     * This is appropriate for the fermenting cauldron, where aged brew is
     * rejected separately and age-derived amplifier changes are therefore
     * not expected.
     */
    public static boolean matchesIgnoringAgeComponent(
            FluidStack first,
            FluidStack second
    ) {
        if (first.isEmpty() || second.isEmpty()) {
            return false;
        }

        return FluidStack.isSameFluidSameComponents(
                withoutAgeComponent(first),
                withoutAgeComponent(second)
        );
    }

    /**
     * Compares the underlying recipe brew after removing both its age
     * component and all amplifier bonuses derived from that age.
     *
     * This is appropriate for the fermenting barrel, where differently aged
     * quantities of the same brew are allowed to combine.
     */
    public static boolean matchesUnderlyingBrew(
            FluidStack first,
            FluidStack second
    ) {
        if (first.isEmpty() || second.isEmpty()) {
            return false;
        }

        return FluidStack.isSameFluidSameComponents(
                withoutAging(first),
                withoutAging(second)
        );
    }

    // =====================================================================
    // Aged brew merging
    // =====================================================================

    /**
     * Combines matching quantities of the same underlying brew.
     *
     * The returned stack:
     * - uses a volume-weighted age;
     * - has the combined amount;
     * - is normalized back to its recipe effects;
     * - receives the full amplifier bonus for the merged age.
     *
     * The caller is responsible for ensuring the accepted amount fits its
     * tank capacity.
     */
    public static FluidStack mergeAgedBrew(
            FluidStack stored,
            FluidStack incoming,
            int acceptedAmount
    ) {
        if (stored.isEmpty()
                || incoming.isEmpty()
                || acceptedAmount <= 0
                || !matchesUnderlyingBrew(
                stored,
                incoming
        )) {
            return FluidStack.EMPTY;
        }

        int actualIncomingAmount = Math.min(
                acceptedAmount,
                incoming.getAmount()
        );

        if (actualIncomingAmount <= 0) {
            return FluidStack.EMPTY;
        }

        long mergedAge = weightedAverageAge(
                getAge(stored),
                stored.getAmount(),
                getAge(incoming),
                actualIncomingAmount
        );

        FluidStack merged = withoutAging(
                stored
        );

        merged.setAmount(
                stored.getAmount()
                        + actualIncomingAmount
        );

        merged.set(
                JolCraftDataComponents.BREW_AGE.get(),
                mergedAge
        );

        applyFullAgeAmplifierBonus(
                merged,
                mergedAge
        );

        return merged;
    }

    // =====================================================================
    // Internal age normalization
    // =====================================================================

    private static FluidStack withoutAgeComponent(
            FluidStack brew
    ) {
        FluidStack normalized = brew.copy();

        normalized.remove(
                JolCraftDataComponents.BREW_AGE.get()
        );

        return normalized;
    }

    private static FluidStack withoutAging(
            FluidStack brew
    ) {
        FluidStack normalized = brew.copy();

        int amplifierBonus = DwarvenBrewAge.fromTicks(
                getAge(normalized)
        ).amplifierBonus();

        normalized.remove(
                JolCraftDataComponents.BREW_AGE.get()
        );

        adjustBrewEffects(
                normalized,
                -amplifierBonus
        );

        return normalized;
    }

    private static void applyAgeAmplifierIncrease(
            FluidStack brew,
            long previousAgeTicks,
            long currentAgeTicks
    ) {
        int previousBonus = DwarvenBrewAge.fromTicks(
                previousAgeTicks
        ).amplifierBonus();

        int currentBonus = DwarvenBrewAge.fromTicks(
                currentAgeTicks
        ).amplifierBonus();

        adjustBrewEffects(
                brew,
                currentBonus - previousBonus
        );
    }

    private static void applyFullAgeAmplifierBonus(
            FluidStack brew,
            long ageTicks
    ) {
        int amplifierBonus = DwarvenBrewAge.fromTicks(
                ageTicks
        ).amplifierBonus();

        adjustBrewEffects(
                brew,
                amplifierBonus
        );
    }

    private static void adjustBrewEffects(
            FluidStack brew,
            int amplifierChange
    ) {
        if (amplifierChange == 0) {
            return;
        }

        PotionContents contents = brew.getOrDefault(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        List<MobEffectInstance> adjustedEffects = new ArrayList<>(
                contents.customEffects().size()
        );

        for (MobEffectInstance effect : contents.customEffects()) {
            adjustedEffects.add(
                    adjustEffect(
                            effect,
                            amplifierChange
                    )
            );
        }

        brew.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        contents.potion(),
                        contents.customColor(),
                        adjustedEffects
                )
        );
    }

    private static MobEffectInstance adjustEffect(
            MobEffectInstance effect,
            int amplifierChange
    ) {
        return new MobEffectInstance(
                effect.getEffect(),
                effect.getDuration(),
                Math.max(
                        0,
                        effect.getAmplifier()
                                + amplifierChange
                ),
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        );
    }

    private static long weightedAverageAge(
            long firstAge,
            int firstAmount,
            long secondAge,
            int secondAmount
    ) {
        long totalAmount =
                (long) firstAmount
                        + secondAmount;

        if (totalAmount <= 0L) {
            return 0L;
        }

        double weightedAge =
                (double) firstAge * firstAmount
                        + (double) secondAge * secondAmount;

        return Math.max(
                0L,
                Math.round(
                        weightedAge / totalAmount
                )
        );
    }

    private static long addClamped(
            long first,
            long second
    ) {
        if (second <= 0L) {
            return Math.max(
                    0L,
                    first
            );
        }

        long normalizedFirst = Math.max(
                0L,
                first
        );

        if (normalizedFirst
                > Long.MAX_VALUE - second) {
            return Long.MAX_VALUE;
        }

        return normalizedFirst + second;
    }
}