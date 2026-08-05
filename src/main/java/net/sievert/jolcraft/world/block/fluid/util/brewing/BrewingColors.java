package net.sievert.jolcraft.world.block.fluid.util.brewing;

public final class BrewingColors {

    public static final int DWARVEN_BREW = argb(
            0x9A652B
    );

    public static final int UNFINISHED_DWARVEN_BREW = argb(
            0x805D37
    );

    public static final int YEAST = argb(
            0x40B14A
    );

    public static final int YEAST_CULTURE = argb(
            0x7EB140
    );

    public static final int UNFINISHED_YEAST = argb(
            0x7EB140
    );

    public static final int TANNIN = argb(
            0x7C4B4B
    );

    public static final int REFINED_TANNIN = argb(
            0x3D2525
    );

    public static final int UNFINISHED_TANNIN = argb(
            0x835A5A
    );

    public static final int ASGARNIAN_HOPS = argb(
            0x91706E
    );

    public static final int DUSKHOLD_HOPS = argb(
            0x817788
    );

    public static final int KRANDONIAN_HOPS = argb(
            0x6E918F
    );

    public static final int YANILLIAN_HOPS = argb(
            0x54832E
    );

    private BrewingColors() {}

    public static int argb(
            int rgb
    ) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }
}