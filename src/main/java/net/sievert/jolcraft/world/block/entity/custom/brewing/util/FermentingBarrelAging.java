package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;

/**
 * Tracks and applies the passage of time to brew stored within a fermenting
 * barrel.
 */
public final class FermentingBarrelAging {

    private static final String NBT_LAST_AGE_TIME =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.AGE,
                    JolCraftDictionary.TIME
            );

    private static final String NBT_AGE_REMAINDER =
            "age_remainder";

    private long lastAgeTime = -1L;
    private double ageRemainder;

    // =====================================================================
    // Projection
    // =====================================================================

    /**
     * Returns the brew as it exists at the supplied game time without
     * mutating the stored stack or aging state.
     */
    public FluidStack getCurrentBrew(
            FluidStack storedBrew,
            long currentGameTime
    ) {
        if (storedBrew.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack current = storedBrew.copy();

        if (!DwarvenBrewFluidHelper.canAgeFurther(current)) {
            return current;
        }

        long elapsedTicks = getElapsedTicks(currentGameTime);
        long addedAge = getScaledAgeTicks(
                current,
                elapsedTicks,
                false
        );

        if (addedAge > 0L) {
            DwarvenBrewFluidHelper.addAgeInPlace(
                    current,
                    addedAge
            );
        }

        return current;
    }

    // =====================================================================
    // Committing elapsed age
    // =====================================================================

    /**
     * Applies all elapsed time directly to the stored brew.
     *
     * @return whether the brew or timer state changed
     */
    public boolean applyElapsedAge(
            FluidStack storedBrew,
            long currentGameTime
    ) {
        if (storedBrew.isEmpty()
                || !DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            return clear();
        }

        if (lastAgeTime < 0L) {
            lastAgeTime = currentGameTime;

            return true;
        }

        long elapsedTicks = getElapsedTicks(currentGameTime);

        if (elapsedTicks <= 0L) {
            return false;
        }

        long addedAge = getScaledAgeTicks(
                storedBrew,
                elapsedTicks,
                true
        );

        if (addedAge > 0L) {
            DwarvenBrewFluidHelper.addAgeInPlace(
                    storedBrew,
                    addedAge
            );
        }

        lastAgeTime = currentGameTime;

        if (!DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            clear();
        }

        return true;
    }

    /**
     * Commits naturally elapsed time and then adds the supplied skipped time.
     *
     * @return whether the brew or timer state changed
     */
    public boolean fastForward(
            FluidStack storedBrew,
            long currentGameTime,
            long skippedTicks
    ) {
        if (storedBrew.isEmpty()
                || skippedTicks <= 0L
                || !DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            return false;
        }

        applyElapsedAge(
                storedBrew,
                currentGameTime
        );

        if (!DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            return true;
        }

        long addedAge = getScaledAgeTicks(
                storedBrew,
                skippedTicks,
                true
        );

        if (addedAge > 0L) {
            DwarvenBrewFluidHelper.addAgeInPlace(
                    storedBrew,
                    addedAge
            );
        }

        lastAgeTime = currentGameTime;

        if (!DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            clear();
        }

        return true;
    }

    /**
     * Advances the brew to the next permitted age threshold.
     *
     * @return whether the brew advanced
     */
    public boolean advanceToNextAge(
            FluidStack storedBrew,
            long currentGameTime
    ) {
        if (storedBrew.isEmpty()
                || !DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            return false;
        }

        applyElapsedAge(
                storedBrew,
                currentGameTime
        );

        if (!DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            return false;
        }

        long currentAge = DwarvenBrewFluidHelper.getAge(storedBrew);
        long maxAge = DwarvenBrewFluidHelper.getMaxAge(
                storedBrew
        ).thresholdTicks();
        long nextAge = Math.min(
                maxAge,
                getNextAgeThreshold(currentAge)
        );

        if (nextAge <= currentAge) {
            return false;
        }

        DwarvenBrewFluidHelper.addAgeInPlace(
                storedBrew,
                nextAge - currentAge
        );

        lastAgeTime = currentGameTime;
        ageRemainder = 0.0D;

        if (!DwarvenBrewFluidHelper.canAgeFurther(storedBrew)) {
            clear();
        }

        return true;
    }

    // =====================================================================
    // Timer state
    // =====================================================================

    /**
     * Starts the aging timer while the supplied brew can still age.
     */
    public void ensureTimerStarted(
            FluidStack brew,
            long currentGameTime
    ) {
        if (brew.isEmpty()
                || !DwarvenBrewFluidHelper.canAgeFurther(brew)) {
            clear();

            return;
        }

        if (lastAgeTime < 0L) {
            lastAgeTime = currentGameTime;
        }
    }

    /**
     * Clears the aging timer and fractional age progress.
     *
     * @return whether the timer or remainder was active
     */
    public boolean clear() {
        boolean changed = lastAgeTime >= 0L
                || ageRemainder > 0.0D;

        lastAgeTime = -1L;
        ageRemainder = 0.0D;

        return changed;
    }

    // =====================================================================
    // Persistence
    // =====================================================================

    /**
     * Saves the active aging timer to NBT.
     */
    public void save(
            CompoundTag tag
    ) {
        if (lastAgeTime >= 0L) {
            tag.putLong(
                    NBT_LAST_AGE_TIME,
                    lastAgeTime
            );
        }

        if (ageRemainder > 0.0D) {
            tag.putDouble(
                    NBT_AGE_REMAINDER,
                    ageRemainder
            );
        }
    }

    /**
     * Loads the aging timer when the stored brew can still age.
     */
    public void load(
            CompoundTag tag,
            FluidStack brew
    ) {
        if (brew.isEmpty()
                || !DwarvenBrewFluidHelper.canAgeFurther(brew)
                || !tag.contains(
                NBT_LAST_AGE_TIME,
                Tag.TAG_LONG
        )) {
            clear();

            return;
        }

        lastAgeTime = Math.max(
                -1L,
                tag.getLong(
                        NBT_LAST_AGE_TIME
                )
        );

        double loadedRemainder = tag.contains(
                NBT_AGE_REMAINDER,
                Tag.TAG_DOUBLE
        )
                ? tag.getDouble(NBT_AGE_REMAINDER)
                : 0.0D;

        ageRemainder = Double.isFinite(loadedRemainder)
                ? Math.max(
                        0.0D,
                        Math.min(
                                Math.nextDown(1.0D),
                                loadedRemainder
                        )
                )
                : 0.0D;
    }

    // =====================================================================
    // Internal helpers
    // =====================================================================

    private long getElapsedTicks(
            long currentGameTime
    ) {
        if (lastAgeTime < 0L) {
            return 0L;
        }

        return Math.max(
                0L,
                currentGameTime - lastAgeTime
        );
    }

    private long getScaledAgeTicks(
            FluidStack brew,
            long elapsedTicks,
            boolean consumeRemainder
    ) {
        if (elapsedTicks <= 0L) {
            return 0L;
        }

        double scaledAge = elapsedTicks
                * (double) DwarvenBrewFluidHelper.getBrewingSpeed(brew)
                + ageRemainder;

        if (!Double.isFinite(scaledAge)
                || scaledAge >= Long.MAX_VALUE) {
            if (consumeRemainder) {
                ageRemainder = 0.0D;
            }

            return Long.MAX_VALUE;
        }

        long wholeTicks = Math.max(
                0L,
                (long) Math.floor(scaledAge)
        );

        if (consumeRemainder) {
            ageRemainder = scaledAge - wholeTicks;
        }

        return wholeTicks;
    }

    private static long getNextAgeThreshold(
            long currentAge
    ) {
        return DwarvenBrewAge.nextThreshold(
                currentAge
        );
    }
}
