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

    private long lastAgeTime = -1L;

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

        long elapsedTicks = getElapsedTicks(currentGameTime);

        if (elapsedTicks > 0L) {
            DwarvenBrewFluidHelper.addAgeInPlace(
                    current,
                    elapsedTicks
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
        if (storedBrew.isEmpty()) {
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

        DwarvenBrewFluidHelper.addAgeInPlace(
                storedBrew,
                elapsedTicks
        );

        lastAgeTime = currentGameTime;

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
                || skippedTicks <= 0L) {
            return false;
        }

        applyElapsedAge(
                storedBrew,
                currentGameTime
        );

        DwarvenBrewFluidHelper.addAgeInPlace(
                storedBrew,
                skippedTicks
        );

        lastAgeTime = currentGameTime;

        return true;
    }

    /**
     * Advances the brew to the next age threshold.
     *
     * @return whether the brew advanced
     */
    public boolean advanceToNextAge(
            FluidStack storedBrew,
            long currentGameTime
    ) {
        if (storedBrew.isEmpty()) {
            return false;
        }

        applyElapsedAge(
                storedBrew,
                currentGameTime
        );

        long currentAge = DwarvenBrewFluidHelper.getAge(storedBrew);

        long nextAge = getNextAgeThreshold(currentAge);

        if (nextAge <= currentAge) {
            return false;
        }

        DwarvenBrewFluidHelper.addAgeInPlace(
                storedBrew,
                nextAge - currentAge
        );

        lastAgeTime = currentGameTime;

        return true;
    }

    // =====================================================================
    // Timer state
    // =====================================================================

    /**
     * Starts the aging timer when brew is present or clears it when the barrel
     * is empty.
     */
    public void ensureTimerStarted(
            boolean hasBrew,
            long currentGameTime
    ) {
        if (!hasBrew) {
            clear();

            return;
        }

        if (lastAgeTime < 0L) {
            lastAgeTime = currentGameTime;
        }
    }

    /**
     * Resets the aging timer to the supplied game time.
     */
    public void reset(
            long currentGameTime
    ) {
        lastAgeTime = currentGameTime;
    }

    /**
     * Clears the aging timer.
     *
     * @return whether the timer was active
     */
    public boolean clear() {
        if (lastAgeTime < 0L) {
            return false;
        }

        lastAgeTime = -1L;

        return true;
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
    }

    /**
     * Loads the aging timer from NBT when the barrel still contains brew.
     */
    public void load(
            CompoundTag tag,
            boolean hasBrew
    ) {
        if (!hasBrew
                || !tag.contains(
                NBT_LAST_AGE_TIME,
                Tag.TAG_LONG
        )) {
            lastAgeTime = -1L;

            return;
        }

        lastAgeTime = Math.max(
                -1L,
                tag.getLong(
                        NBT_LAST_AGE_TIME
                )
        );
    }

    // =====================================================================
    // Internal helpers
    // =====================================================================

    /**
     * Returns the non-negative number of ticks elapsed since aging was last
     * committed.
     */
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

    /**
     * Returns the next age threshold after the supplied brew age.
     */
    private static long getNextAgeThreshold(
            long currentAge
    ) {
        return DwarvenBrewAge.nextThreshold(
                currentAge
        );
    }
}