package net.sievert.jolcraft.world.block.fluid.util.brewing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Shared utility for identifying, comparing, aging and merging dwarven brew fluids.
 */
public final class DwarvenBrewFluidHelper {

    public static final int MUG_VOLUME = FluidType.BUCKET_VOLUME / 2;

    public static final DwarvenBrewAge DEFAULT_MAX_AGE = DwarvenBrewAge.AGED;

    public static final float DEFAULT_BREWING_SPEED = 1.0F;

    public static final float BREWING_SPEED_1_5 = 1.5F;
    public static final float BREWING_SPEED_2_0 = 2.0F;
    public static final float BREWING_SPEED_2_5 = 2.5F;
    public static final float BREWING_SPEED_3_0 = 3.0F;

    public static final List<Float> BREWING_SPEED_TIERS = List.of(
            DEFAULT_BREWING_SPEED,
            BREWING_SPEED_1_5,
            BREWING_SPEED_2_0,
            BREWING_SPEED_2_5,
            BREWING_SPEED_3_0
    );

    private DwarvenBrewFluidHelper() {}

    // =====================================================================
    // Canonical brew creation
    // =====================================================================

    public static FluidStack createDwarvenBrew(
            int amount
    ) {
        return createDwarvenBrew(
                amount,
                BrewingColors.DWARVEN_BREW,
                0L,
                DEFAULT_MAX_AGE,
                DEFAULT_BREWING_SPEED,
                PotionContents.EMPTY
        );
    }

    public static FluidStack createDwarvenBrew(
            int amount,
            DwarvenBrewAge age,
            DwarvenBrewAge maxAge,
            float brewingSpeed,
            PotionContents potionContents
    ) {
        return createDwarvenBrew(
                amount,
                BrewingColors.DWARVEN_BREW,
                age.thresholdTicks(),
                maxAge,
                brewingSpeed,
                potionContents
        );
    }

    public static FluidStack createDwarvenBrew(
            int amount,
            int brewColor,
            long brewAge,
            DwarvenBrewAge maxBrewAge,
            float brewingSpeed,
            PotionContents potionContents
    ) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Brew amount must be positive"
            );
        }

        long age = Math.max(
                0L,
                brewAge
        );

        DwarvenBrewAge currentAge = DwarvenBrewAge.fromTicks(
                age
        );

        DwarvenBrewAge requestedMaxAge = Objects.requireNonNull(
                maxBrewAge,
                "maxBrewAge"
        );

        DwarvenBrewAge resolvedMaxAge =
                currentAge.ordinal() > requestedMaxAge.ordinal()
                        ? currentAge
                        : requestedMaxAge;

        float resolvedBrewingSpeed =
                Float.isFinite(brewingSpeed)
                        && brewingSpeed > 0.0F
                        ? brewingSpeed
                        : DEFAULT_BREWING_SPEED;

        FluidStack brew = new FluidStack(
                JolCraftFluids.DWARVEN_BREW.get(),
                amount
        );

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                JolCraftColors.toArgb(
                        brewColor
                )
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                age
        );

        brew.set(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                resolvedMaxAge
        );

        brew.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                resolvedBrewingSpeed
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                Objects.requireNonNull(
                        potionContents,
                        "potionContents"
                )
        );

        return brew;
    }

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

    public static boolean isFinishedYeast(
            FluidStack fluid
    ) {
        return !fluid.isEmpty()
                && fluid.is(
                JolCraftFluids.YEAST.get()
        );
    }

    public static boolean isUnfinishedYeast(
            FluidStack fluid
    ) {
        return !fluid.isEmpty()
                && fluid.is(
                JolCraftFluids.UNFINISHED_YEAST.get()
        );
    }

    public static boolean isFinishedTannin(
            FluidStack fluid
    ) {
        return !fluid.isEmpty()
                && (fluid.is(
                JolCraftFluids.TANNIN.get()
        )
                || fluid.is(
                JolCraftFluids.REFINED_TANNIN.get()
        ));
    }

    public static boolean isUnfinishedTannin(
            FluidStack fluid
    ) {
        return !fluid.isEmpty()
                && fluid.is(
                JolCraftFluids.UNFINISHED_TANNIN.get()
        );
    }

    public static boolean isFinishedBrewingFluid(
            FluidStack fluid
    ) {
        return isFinishedBrew(fluid)
                || isFinishedYeast(fluid)
                || isFinishedTannin(fluid);
    }

    public static boolean isUnfinishedBrewingFluid(
            FluidStack fluid
    ) {
        return isUnfinishedBrew(fluid)
                || isUnfinishedYeast(fluid)
                || isUnfinishedTannin(fluid);
    }

    public static Optional<FluidStack> findContainedBrew(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return FluidUtil.getFluidContained(stack)
                .filter(DwarvenBrewFluidHelper::isFinishedBrew);
    }

    public static boolean containsDwarvenBrew(
            ItemStack stack
    ) {
        return findContainedBrew(stack).isPresent();
    }

    /**
     * Extracts the contained brew from a filled dwarven brew mug.
     */
    public static FluidStack getBrewFromMug(
            ItemStack mug
    ) {
        if (!mug.is(JolCraftItems.DWARVEN_BREW.get())) {
            return FluidStack.EMPTY;
        }

        return findContainedBrew(mug)
                .map(FluidStack::copy)
                .orElse(FluidStack.EMPTY);
    }


    // =====================================================================
    // Component normalization
    // =====================================================================

    /**
     * Normalizes the standard components carried by a recognized brewing fluid.
     */
    public static void normalizeBrewingFluid(
            FluidStack fluid
    ) {
        if (fluid.isEmpty()
                || (!isFinishedBrewingFluid(fluid)
                && !isUnfinishedBrewingFluid(fluid))) {
            return;
        }

        FluidStack original = fluid.copy();

        if (!fluid.has(JolCraftDataComponents.BREW_COLOR.get())) {
            fluid.set(
                    JolCraftDataComponents.BREW_COLOR.get(),
                    defaultColor(fluid)
            );
        }

        if (isFinishedBrew(fluid)) {
            fluid.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    getAge(fluid)
            );
        } else {
            fluid.remove(
                    JolCraftDataComponents.BREW_AGE.get()
            );
        }

        if (isFinishedBrew(fluid)
                || isUnfinishedBrew(fluid)
                || isFinishedTannin(fluid)
                || isUnfinishedTannin(fluid)) {
            fluid.set(
                    JolCraftDataComponents.MAX_BREW_AGE.get(),
                    getMaxAge(fluid)
            );
        } else {
            fluid.remove(
                    JolCraftDataComponents.MAX_BREW_AGE.get()
            );
        }

        if (isFinishedBrew(fluid)
                || isUnfinishedBrew(fluid)
                || isFinishedYeast(fluid)
                || isUnfinishedYeast(fluid)) {
            fluid.set(
                    JolCraftDataComponents.BREWING_SPEED.get(),
                    getBrewingSpeed(fluid)
            );
        } else {
            fluid.remove(
                    JolCraftDataComponents.BREWING_SPEED.get()
            );
        }

        if (isFinishedBrew(fluid)
                || isUnfinishedBrew(fluid)) {
            fluid.set(
                    DataComponents.POTION_CONTENTS,
                    fluid.getOrDefault(
                            DataComponents.POTION_CONTENTS,
                            PotionContents.EMPTY
                    )
            );
        } else {
            fluid.remove(
                    DataComponents.POTION_CONTENTS
            );
        }

        FluidStack.isSameFluidSameComponents(
                original,
                fluid
        );
    }

    private static int defaultColor(
            FluidStack fluid
    ) {
        if (isFinishedBrew(fluid)) {
            return BrewingColors.DWARVEN_BREW;
        }

        if (isUnfinishedBrew(fluid)) {
            return BrewingColors.UNFINISHED_DWARVEN_BREW;
        }

        if (isFinishedYeast(fluid)) {
            return BrewingColors.YEAST;
        }

        if (isUnfinishedYeast(fluid)) {
            return BrewingColors.UNFINISHED_YEAST;
        }

        if (fluid.is(JolCraftFluids.REFINED_TANNIN.get())) {
            return BrewingColors.REFINED_TANNIN;
        }

        if (fluid.is(JolCraftFluids.TANNIN.get())) {
            return BrewingColors.TANNIN;
        }

        return BrewingColors.UNFINISHED_TANNIN;
    }

    // =====================================================================
    // Maximum age
    // =====================================================================

    public static DwarvenBrewAge getMaxAge(
            FluidStack fluid
    ) {
        DwarvenBrewAge currentAge = DwarvenBrewAge.fromTicks(
                getAge(fluid)
        );

        DwarvenBrewAge defaultMaxAge;

        if (fluid.is(
                JolCraftFluids.REFINED_TANNIN.get()
        )) {
            defaultMaxAge = DwarvenBrewAge.VINTAGE;
        } else if (isFinishedTannin(fluid)
                || isUnfinishedTannin(fluid)) {
            defaultMaxAge = DwarvenBrewAge.MATURED;
        } else {
            defaultMaxAge = currentAge.ordinal() > DEFAULT_MAX_AGE.ordinal()
                    ? currentAge
                    : DEFAULT_MAX_AGE;
        }

        DwarvenBrewAge storedMaxAge = fluid.getOrDefault(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                defaultMaxAge
        );

        DwarvenBrewAge resolvedMaxAge =
                defaultMaxAge.ordinal() > storedMaxAge.ordinal()
                        ? defaultMaxAge
                        : storedMaxAge;

        return currentAge.ordinal() > resolvedMaxAge.ordinal()
                ? currentAge
                : resolvedMaxAge;
    }

    public static Optional<DwarvenBrewAge> findContainedMaxAge(
            ItemStack stack
    ) {
        return FluidUtil.getFluidContained(stack)
                .filter(fluid -> fluid.has(
                                JolCraftDataComponents.MAX_BREW_AGE.get()
                        )
                                || isFinishedBrew(fluid)
                                || isUnfinishedBrew(fluid)
                                || isFinishedTannin(fluid)
                                || isUnfinishedTannin(fluid))
                .map(DwarvenBrewFluidHelper::getMaxAge);
    }

    public static DwarvenBrewAge getContainedMaxAge(
            ItemStack stack
    ) {
        return findContainedMaxAge(stack)
                .orElse(DEFAULT_MAX_AGE);
    }

    public static void raiseMaxAge(
            FluidStack fluid,
            DwarvenBrewAge maxAge
    ) {
        DwarvenBrewAge current = getMaxAge(fluid);

        fluid.set(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                current.ordinal() >= maxAge.ordinal()
                        ? current
                        : maxAge
        );
    }

    public static void copyMaxAge(
            FluidStack source,
            FluidStack target
    ) {
        if (!source.has(JolCraftDataComponents.MAX_BREW_AGE.get())) {
            return;
        }

        target.set(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                getMaxAge(source)
        );
    }

    // =====================================================================
    // Brewing speed
    // =====================================================================

    public static float getBrewingSpeed(
            FluidStack fluid
    ) {
        float speed = fluid.getOrDefault(
                JolCraftDataComponents.BREWING_SPEED.get(),
                DEFAULT_BREWING_SPEED
        );

        return Float.isFinite(speed) && speed > 0.0F
                ? speed
                : DEFAULT_BREWING_SPEED;
    }

    public static Optional<Float> findBrewingSpeed(
            ItemStack stack
    ) {
        Float directSpeed = stack.get(
                JolCraftDataComponents.BREWING_SPEED.get()
        );

        if (directSpeed != null
                && Float.isFinite(directSpeed)
                && directSpeed > 0.0F) {
            return Optional.of(
                    directSpeed
            );
        }

        return FluidUtil.getFluidContained(stack)
                .filter(fluid -> fluid.has(
                                JolCraftDataComponents.BREWING_SPEED.get()
                        )
                                || isFinishedYeast(fluid)
                                || isUnfinishedYeast(fluid))
                .map(DwarvenBrewFluidHelper::getBrewingSpeed);
    }

    public static float getBrewingSpeed(
            ItemStack stack
    ) {
        return findBrewingSpeed(stack)
                .orElse(DEFAULT_BREWING_SPEED);
    }

    public static void copyBrewingSpeed(
            FluidStack source,
            FluidStack target
    ) {
        if (!source.has(
                JolCraftDataComponents.BREWING_SPEED.get()
        )) {
            return;
        }

        target.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                getBrewingSpeed(source)
        );
    }

    // =====================================================================
    // Age access and mutation
    // =====================================================================

    /**
     * Returns the stored brew age, clamped to a non-negative value.
     */
    public static long getAge(
            FluidStack brew
    ) {
        if (!isFinishedBrew(brew)) {
            return 0L;
        }

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

        if (!isFinishedBrew(brew)) {
            FluidStack normalized = brew.copy();

            normalized.remove(
                    JolCraftDataComponents.BREW_AGE.get()
            );

            return normalized;
        }

        FluidStack fresh = withoutAging(brew);

        fresh.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        return fresh;
    }

    public static boolean canAgeFurther(
            FluidStack brew
    ) {
        return isFinishedBrew(brew)
                && getAge(brew)
                < getMaxAge(brew).thresholdTicks();
    }

    /**
     * Adds age directly to the supplied stack and applies any newly earned
     * amplifier increases.
     */
    public static void addAgeInPlace(
            FluidStack brew,
            long addedTicks
    ) {
        if (!isFinishedBrew(brew) || addedTicks <= 0L) {
            return;
        }

        long previousAgeTicks = getAge(
                brew
        );

        long maxAgeTicks = getMaxAge(
                brew
        ).thresholdTicks();

        if (previousAgeTicks >= maxAgeTicks) {
            return;
        }

        long currentAgeTicks = Math.min(
                maxAgeTicks,
                addClamped(
                        previousAgeTicks,
                        addedTicks
                )
        );

        if (currentAgeTicks <= previousAgeTicks) {
            return;
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

        long mergedAge = Math.min(
                getMaxAge(stored).thresholdTicks(),
                weightedAverageAge(
                        getAge(stored),
                        stored.getAmount(),
                        getAge(incoming),
                        actualIncomingAmount
                )
        );

        FluidStack merged = withoutAging(stored);

        merged.setAmount(stored.getAmount() + actualIncomingAmount);

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

    /**
     * Returns a copy with only the BREW_AGE component removed.
     */
    private static FluidStack withoutAgeComponent(
            FluidStack brew
    ) {
        FluidStack normalized = brew.copy();

        normalizeMaxAgeComponent(normalized);
        normalizeBrewingSpeedComponent(normalized);

        normalized.remove(JolCraftDataComponents.BREW_AGE.get());

        return normalized;
    }

    private static FluidStack withoutAging(
            FluidStack brew
    ) {
        FluidStack normalized = brew.copy();

        int amplifierBonus = DwarvenBrewAge.fromTicks(getAge(normalized)).amplifierBonus();

        normalizeMaxAgeComponent(normalized);
        normalizeBrewingSpeedComponent(normalized);

        normalized.remove(JolCraftDataComponents.BREW_AGE.get());

        adjustBrewEffects(
                normalized,
                -amplifierBonus
        );

        return normalized;
    }

    private static void normalizeMaxAgeComponent(
            FluidStack fluid
    ) {
        if (isFinishedBrew(fluid)
                || isUnfinishedBrew(fluid)
                || isFinishedTannin(fluid)
                || isUnfinishedTannin(fluid)) {
            fluid.set(
                    JolCraftDataComponents.MAX_BREW_AGE.get(),
                    getMaxAge(fluid)
            );
        }
    }

    private static void normalizeBrewingSpeedComponent(
            FluidStack fluid
    ) {
        if (isFinishedBrew(fluid)
                || isUnfinishedBrew(fluid)
                || isFinishedYeast(fluid)
                || isUnfinishedYeast(fluid)) {
            fluid.set(
                    JolCraftDataComponents.BREWING_SPEED.get(),
                    getBrewingSpeed(fluid)
            );
        }
    }

    private static void applyAgeAmplifierIncrease(
            FluidStack brew,
            long previousAgeTicks,
            long currentAgeTicks
    ) {
        int previousBonus = DwarvenBrewAge.fromTicks(previousAgeTicks).amplifierBonus();

        int currentBonus = DwarvenBrewAge.fromTicks(currentAgeTicks).amplifierBonus();

        adjustBrewEffects(
                brew,
                currentBonus - previousBonus
        );
    }

    private static void applyFullAgeAmplifierBonus(
            FluidStack brew,
            long ageTicks
    ) {
        int amplifierBonus = DwarvenBrewAge.fromTicks(ageTicks).amplifierBonus();

        adjustBrewEffects(
                brew,
                amplifierBonus
        );
    }

    /**
     * Applies the supplied amplifier adjustment to every custom potion effect
     * stored within the brew.
     */
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

    /**
     * Creates a copy of an effect with its amplifier adjusted while preserving
     * all other properties.
     */
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

    /**
     * Computes the volume-weighted average age of two brew quantities.
     */
    private static long weightedAverageAge(
            long firstAge,
            int firstAmount,
            long secondAge,
            int secondAmount
    ) {
        long totalAmount = (long) firstAmount + secondAmount;

        if (totalAmount <= 0L) {
            return 0L;
        }

        double weightedAge = (double) firstAge * firstAmount + (double) secondAge * secondAmount;

        return Math.max(
                0L,
                Math.round(
                        weightedAge / totalAmount
                )
        );
    }

    /**
     * Adds two age values while preventing overflow and negative results.
     */
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

        if (normalizedFirst > Long.MAX_VALUE - second) {
            return Long.MAX_VALUE;
        }

        return normalizedFirst + second;
    }
}
