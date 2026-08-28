package net.sievert.jolcraft.util.client;

public final class JolCraftColors {

    private static final int OPAQUE_ALPHA = 0xFF;

    private JolCraftColors() {}

    /**
     * Converts a hexadecimal color such as "401C4F"
     * into an RGB integer in the format 0xRRGGBB.
     */
    public static int rgb(String hex) {
        return parse(hex);
    }

    /**
     * Converts a hexadecimal color such as "401C4F"
     * into an opaque ARGB integer in the format 0xFFRRGGBB.
     */
    public static int argb(String hex) {
        return (OPAQUE_ALPHA << 24) | parse(hex);
    }

    /**
     * Converts a hexadecimal color such as "401C4F"
     * into an ARGB integer using the supplied alpha value.
     *
     * @param alpha alpha from 0 to 255
     */
    public static int argb(String hex, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Alpha must be between 0 and 255");
        }

        return (alpha << 24) | parse(hex);
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
}