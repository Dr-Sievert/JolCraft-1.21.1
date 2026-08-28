package net.sievert.jolcraft.util.client;

import java.util.Locale;

public final class JolCraftColors {

    private static final int CHANNEL_MASK = 255;
    private static final int RGB_MASK = 16_777_215;
    private static final int OPAQUE_ALPHA = 255;

    private JolCraftColors() {}

    /**
     * Converts the canonical JolCraft color format, RRGGBB, to an RGB integer.
     */
    public static int rgb(String hex) {
        return parse(hex);
    }

    /**
     * Converts the canonical JolCraft color format, RRGGBB, to opaque ARGB.
     */
    public static int argb(String hex) {
        return argb(hex, OPAQUE_ALPHA);
    }

    /**
     * Converts the canonical JolCraft color format, RRGGBB, to ARGB with the supplied alpha.
     */
    public static int argb(String hex, int alpha) {
        return withAlpha(rgb(hex), alpha);
    }

    /**
     * Removes any alpha channel and returns RGB in the format expected by RGB-only APIs.
     */
    public static int toRgb(int color) {
        return color & RGB_MASK;
    }

    /**
     * Converts a runtime RGB/ARGB value to opaque ARGB.
     */
    public static int toArgb(int color) {
        return withAlpha(color, OPAQUE_ALPHA);
    }

    /**
     * Applies an alpha channel to a runtime RGB/ARGB value.
     */
    public static int withAlpha(int color, int alpha) {
        return channel(alpha) << 24 | toRgb(color);
    }

    /**
     * Creates an RGB value from individual channels.
     */
    public static int rgb(int red, int green, int blue) {
        return channel(red) << 16
                | channel(green) << 8
                | channel(blue);
    }

    /**
     * Creates an ARGB value from individual channels.
     */
    public static int argb(int alpha, int red, int green, int blue) {
        return channel(alpha) << 24
                | rgb(red, green, blue);
    }

    public static int alpha(int color) {
        return color >>> 24 & CHANNEL_MASK;
    }

    public static int red(int color) {
        return color >>> 16 & CHANNEL_MASK;
    }

    public static int green(int color) {
        return color >>> 8 & CHANNEL_MASK;
    }

    public static int blue(int color) {
        return color & CHANNEL_MASK;
    }

    /**
     * Multiplies the RGB channels while preserving the existing alpha channel.
     */
    public static int multiplyRgb(int color, float multiplier) {
        return argb(
                alpha(color),
                Math.round(red(color) * multiplier),
                Math.round(green(color) * multiplier),
                Math.round(blue(color) * multiplier)
        );
    }

    /**
     * Linearly interpolates every ARGB channel between two runtime colors.
     */
    public static int lerpArgb(int start, int target, float progress) {
        float clamped = Math.max(0.0F, Math.min(progress, 1.0F));

        return argb(
                lerpChannel(alpha(start), alpha(target), clamped),
                lerpChannel(red(start), red(target), clamped),
                lerpChannel(green(start), green(target), clamped),
                lerpChannel(blue(start), blue(target), clamped)
        );
    }

    /**
     * Converts a runtime RGB/ARGB value back to canonical RRGGBB text.
     */
    public static String hex(int color) {
        return String.format(Locale.ROOT, "%06X", toRgb(color));
    }

    private static int parse(String hex) {
        if (hex == null || hex.length() != 6) {
            throw new IllegalArgumentException(
                    "Color must use RRGGBB format: " + hex
            );
        }

        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal color: " + hex,
                    exception
            );
        }
    }

    private static int channel(int value) {
        if (value < 0 || value > CHANNEL_MASK) {
            throw new IllegalArgumentException(
                    "Color channel must be between 0 and 255: " + value
            );
        }

        return value;
    }

    private static int lerpChannel(int start, int target, float progress) {
        return (int) (start + (target - start) * progress);
    }
}
