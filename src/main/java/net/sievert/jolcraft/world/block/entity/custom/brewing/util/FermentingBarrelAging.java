package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.event.game.world.time.JolCraftTimeHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class FermentingBarrelAging {

    private static final String NBT_LAST_AGE_TIME =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.AGE,
                    JolCraftDictionary.TIME
            );

    private long lastAgeTime;

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

        long elapsedTicks = getElapsedTicks(
                currentGameTime
        );

        if (elapsedTicks <= 0L) {
            return current;
        }

        DwarvenBrewFluidHelper.addAgeInPlace(
                current,
                elapsedTicks
        );

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

        if (lastAgeTime <= 0L) {
            lastAgeTime = currentGameTime;
            return true;
        }

        long elapsedTicks =
                currentGameTime - lastAgeTime;

        if (elapsedTicks <= 0L) {
            return false;
        }

        boolean changed =
                DwarvenBrewFluidHelper.addAgeInPlace(
                        storedBrew,
                        elapsedTicks
                );

        lastAgeTime = currentGameTime;

        return changed;
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

        boolean changed = applyElapsedAge(
                storedBrew,
                currentGameTime
        );

        changed |= DwarvenBrewFluidHelper.addAgeInPlace(
                storedBrew,
                skippedTicks
        );

        lastAgeTime = currentGameTime;

        return changed;
    }

    /**
     * Advances the brew to one tick beyond the next age threshold.
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

        long currentAge =
                DwarvenBrewFluidHelper.getAge(
                        storedBrew
                );

        long nextAge = getNextAgeThreshold(
                currentAge
        );

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

    public void ensureTimerStarted(
            boolean hasBrew,
            long currentGameTime
    ) {
        if (!hasBrew) {
            clear();
            return;
        }

        if (lastAgeTime <= 0L) {
            lastAgeTime = currentGameTime;
        }
    }

    public void reset(
            long currentGameTime
    ) {
        lastAgeTime = currentGameTime;
    }

    public boolean clear() {
        if (lastAgeTime == 0L) {
            return false;
        }

        lastAgeTime = 0L;

        return true;
    }

    public long getLastAgeTime() {
        return lastAgeTime;
    }

    // =====================================================================
    // Persistence
    // =====================================================================

    public void save(
            CompoundTag tag
    ) {
        if (lastAgeTime > 0L) {
            tag.putLong(
                    NBT_LAST_AGE_TIME,
                    lastAgeTime
            );
        }
    }

    public void load(
            CompoundTag tag,
            boolean hasBrew
    ) {
        lastAgeTime = hasBrew
                ? Math.max(
                0L,
                tag.getLong(
                        NBT_LAST_AGE_TIME
                )
        )
                : 0L;
    }

    // =====================================================================
    // Internal helpers
    // =====================================================================

    private long getElapsedTicks(
            long currentGameTime
    ) {
        if (lastAgeTime <= 0L) {
            return 0L;
        }

        return Math.max(
                0L,
                currentGameTime - lastAgeTime
        );
    }

    private static long getNextAgeThreshold(
            long currentAge
    ) {
        long day = JolCraftTimeHelper.TICKS_PER_DAY;

        if (currentAge <= day) {
            return day + 1L;
        }

        if (currentAge <= day * 3L) {
            return day * 3L + 1L;
        }

        if (currentAge <= day * 5L) {
            return day * 5L + 1L;
        }

        return currentAge;
    }
}