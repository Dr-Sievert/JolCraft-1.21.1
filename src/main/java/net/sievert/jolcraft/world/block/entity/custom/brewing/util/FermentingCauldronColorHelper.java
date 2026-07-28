package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * All color blending / mixing math for the Fermenting Cauldron.
 * Server-authoritative state is stored on the block entity:
 * - currentColor, startColor, targetColor, brewStartTime, blendTotalTicks
 * The renderer may interpolate using partial ticks, but must not invent state.
 */
public final class FermentingCauldronColorHelper {

    private FermentingCauldronColorHelper() {}

    /** Sentinel for unset ARGB values (real output colors are always opaque). */
    public static final int UNSET_COLOR = 0x00000000;

    public interface IngredientView {
        int count();
        /** ARGB; scale is ignored for mixing output. */
        int color();
    }

    // =====================================================================
    // Base / biome
    // =====================================================================

    public static int biomeWaterArgb(Level level, BlockPos pos) {
        int rgb = level.getBiome(pos).value().getWaterColor();
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    /**
     * Resolves the base water color for a cauldron.
     * If the current color is unset, this returns the biome water color (opaque).
     */
    public static int resolveBaseWaterColor(Level level, BlockPos pos, int currentArgb) {
        if (level == null) return currentArgb;
        return (currentArgb != UNSET_COLOR) ? currentArgb : biomeWaterArgb(level, pos);
    }

    // =====================================================================
    // Timing
    // =====================================================================

    public static long blendEndTime(long brewStartTime, int blendTotalTicks) {
        return brewStartTime + Math.max(1L, (long) blendTotalTicks);
    }

    public static boolean isComplete(Level level, long brewStartTime, int blendTotalTicks) {
        if (level == null) return false;
        if (brewStartTime < 0L) return false;
        return level.getGameTime() >= blendEndTime(brewStartTime, blendTotalTicks);
    }

    // =====================================================================
    // Render blend
    // =====================================================================

    /**
     * Returns the display color for rendering.
     * If brewing is active, this returns a deterministic lerp between start and target.
     * If not brewing, it returns currentArgb.
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
        if (level == null) return currentArgb;
        if (brewStartTime < 0L) return currentArgb;
        return blendedColor(level, partialTicks, brewStartTime, blendTotalTicks, startArgb, targetArgb);
    }

    public static int blendedColor(
            Level level,
            float partialTicks,
            long brewStartTime,
            int blendTotalTicks,
            int startArgb,
            int targetArgb
    ) {
        if (level == null) return startArgb;
        if (brewStartTime < 0L) return startArgb;

        int total = Math.max(1, blendTotalTicks);
        float elapsed = (float) (level.getGameTime() - brewStartTime) + partialTicks;
        float t = clamp01(elapsed / (float) total);

        return lerpArgb(startArgb, targetArgb, t);
    }

    // =====================================================================
    // Mixing
    // =====================================================================

    /**
     * Weighted mixing: per ingredient count, weight steps 1, 1/2, 1/4 (max 3).
     * Output is opaque (scale=FF).
     */
    public static int computeMixedIngredientColor(Iterable<? extends IngredientView> ingredients, int fallbackArgb) {
        double sumW = 0.0;
        double sumR = 0.0;
        double sumG = 0.0;
        double sumB = 0.0;

        for (IngredientView data : ingredients) {
            if (data == null) continue;

            int count = data.count();
            if (count <= 0) continue;

            int c = data.color();
            int r = (c >>> 16) & 0xFF;
            int g = (c >>> 8) & 0xFF;
            int b = c & 0xFF;

            int steps = Math.min(3, count);
            for (int i = 0; i < steps; i++) {
                double w = 1.0 / (1 << i);
                sumW += w;
                sumR += r * w;
                sumG += g * w;
                sumB += b * w;
            }
        }

        if (sumW <= 0.0) return fallbackArgb;

        int outR = clamp255((int) Math.round(sumR / sumW));
        int outG = clamp255((int) Math.round(sumG / sumW));
        int outB = clamp255((int) Math.round(sumB / sumW));

        return 0xFF000000 | (outR << 16) | (outG << 8) | outB;
    }

    // =====================================================================
    // Math
    // =====================================================================

    public static int clamp255(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }

    public static float clamp01(float v) {
        return v < 0f ? 0f : Math.min(v, 1f);
    }

    public static int lerpArgb(int a, int b, float t) {
        int aA = (a >>> 24) & 0xFF, aR = (a >>> 16) & 0xFF, aG = (a >>> 8) & 0xFF, aB = a & 0xFF;
        int bA = (b >>> 24) & 0xFF, bR = (b >>> 16) & 0xFF, bG = (b >>> 8) & 0xFF, bB = b & 0xFF;

        int oA = (int) (aA + (bA - aA) * t);
        int oR = (int) (aR + (bR - aR) * t);
        int oG = (int) (aG + (bG - aG) * t);
        int oB = (int) (aB + (bB - aB) * t);

        return (oA << 24) | (oR << 16) | (oG << 8) | oB;
    }
}