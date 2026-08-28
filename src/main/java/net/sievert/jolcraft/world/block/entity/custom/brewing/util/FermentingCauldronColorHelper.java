package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.util.client.JolCraftColors;

/**
 * Owns all color mixing and interpolation for the fermenting cauldron.
 * The block entity remains authoritative for current, start, and target colors.
 */
public final class FermentingCauldronColorHelper {

    /**
     * Sentinel for unset ARGB values. All real brew colors are opaque.
     */
    public static final int UNSET_COLOR = JolCraftColors.argb("000000", 0);

    private FermentingCauldronColorHelper() {}

    /**
     * Provides the ingredient data required when computing a mixed brew color.
     */
    public interface IngredientView {

        /**
         * Returns the number of copies of the ingredient.
         */
        int count();

        /**
         * Returns the ingredient color as ARGB.
         * Alpha is ignored while ingredient colors are mixed.
         */
        int color();
    }

    /**
     * Returns the biome water color at the supplied position as opaque ARGB.
     */
    public static int biomeWaterArgb(
            Level level,
            BlockPos pos
    ) {
        int rgb = level.getBiome(pos).value().getWaterColor();

        return JolCraftColors.toArgb(rgb);
    }

    /**
     * Returns the current base water color or resolves it from the biome when
     * no color has been assigned yet.
     */
    public static int resolveBaseWaterColor(
            Level level,
            BlockPos pos,
            int currentArgb
    ) {
        if (level == null) {
            return currentArgb;
        }

        return currentArgb != UNSET_COLOR ? currentArgb : biomeWaterArgb(level, pos);
    }

    /**
     * Returns the game time at which the current color blend completes.
     */
    public static long blendEndTime(
            long brewStartTime,
            int blendTotalTicks
    ) {
        return brewStartTime + Math.max(
                1L,
                blendTotalTicks
        );
    }

    /**
     * Returns whether the current color blend has completed.
     */
    public static boolean isComplete(
            Level level,
            long brewStartTime,
            int blendTotalTicks
    ) {
        return level != null
                && brewStartTime >= 0L
                && level.getGameTime() >= blendEndTime(brewStartTime, blendTotalTicks);
    }

    /**
     * Returns the color that should currently be displayed for the brew.
     */
    public static int displayColor(
            Level level,
            float partialTicks,
            long brewStartTime,
            int blendTotalTicks,
            int currentArgb,
            int startArgb,
            int targetArgb
    ) {
        if (level == null || brewStartTime < 0L) {
            return currentArgb;
        }

        return blendedColor(
                level,
                partialTicks,
                brewStartTime,
                blendTotalTicks,
                startArgb,
                targetArgb
        );
    }

    /**
     * Interpolates between the start and target colors using the current blend
     * progress.
     */
    public static int blendedColor(
            Level level,
            float partialTicks,
            long brewStartTime,
            int blendTotalTicks,
            int startArgb,
            int targetArgb
    ) {
        if (level == null || brewStartTime < 0L) {
            return startArgb;
        }

        int total = Math.max(
                1,
                blendTotalTicks
        );

        float elapsed = (float) (level.getGameTime() - brewStartTime) + partialTicks;

        float progress = clamp01(elapsed / total);

        return lerpArgb(
                startArgb,
                targetArgb,
                progress
        );
    }

    /**
     * Mixes up to the first three copies of each ingredient using weights
     * 1, 1/2, and 1/4. The returned color is always opaque.
     */
    public static int computeMixedIngredientColor(
            Iterable<? extends IngredientView> ingredients,
            int fallbackArgb
    ) {
        double totalWeight = 0.0D;
        double totalRed = 0.0D;
        double totalGreen = 0.0D;
        double totalBlue = 0.0D;

        for (IngredientView data : ingredients) {
            if (data == null || data.count() <= 0) {
                continue;
            }

            int color = data.color();
            int red = JolCraftColors.red(color);
            int green = JolCraftColors.green(color);
            int blue = JolCraftColors.blue(color);

            int steps = Math.min(
                    3,
                    data.count()
            );

            for (
                    int index = 0;
                    index < steps;
                    index++
            ) {
                double weight = 1.0D / (1 << index);

                totalWeight += weight;
                totalRed += red * weight;
                totalGreen += green * weight;
                totalBlue += blue * weight;
            }
        }

        if (totalWeight <= 0.0D) {
            return fallbackArgb;
        }

        int red = clamp255((int) Math.round(totalRed / totalWeight));

        int green = clamp255((int) Math.round(totalGreen / totalWeight));

        int blue = clamp255((int) Math.round(totalBlue / totalWeight));

        return JolCraftColors.argb(
                255,
                red,
                green,
                blue
        );
    }

    /**
     * Clamps an integer to the valid range of an RGB color channel.
     */
    public static int clamp255(
            int value
    ) {
        return value < 0 ? 0 : Math.min(
                value,
                255
        );
    }

    /**
     * Clamps a floating-point value between zero and one.
     */
    public static float clamp01(
            float value
    ) {
        return value < 0.0F ? 0.0F : Math.min(
                value,
                1.0F
        );
    }

    /**
     * Linearly interpolates every ARGB channel between two colors.
     */
    public static int lerpArgb(
            int start,
            int target,
            float progress
    ) {
        return JolCraftColors.lerpArgb(
                start,
                target,
                progress
        );
    }
}
