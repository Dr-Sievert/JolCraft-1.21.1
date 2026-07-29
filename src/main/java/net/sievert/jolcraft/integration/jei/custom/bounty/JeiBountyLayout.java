package net.sievert.jolcraft.integration.jei.custom.bounty;

import net.sievert.jolcraft.integration.jei.util.gui.JeiPoint;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.ARROW_WIDTH;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.PLUS_WIDTH;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.RIGHT_CLICK_SIZE;

public final class JeiBountyLayout {

    public static final int WIDTH = 172;
    public static final int HEIGHT = 76;

    public static final JeiPoint INPUT =
            new JeiPoint(
                    4,
                    26
            );

    public static final JeiPoint PLUS =
            new JeiPoint(
                    INPUT.x()
                            + SLOT_SIZE
                            + 4,
                    INPUT.y()
                            + (
                            SLOT_SIZE
                                    - PLUS_WIDTH
                    ) / 2
            );

    public static final float DWARF_CENTER_X = 64.0F;
    public static final float DWARF_BOTTOM_Y = 46.0F;

    public static final JeiPoint DWARF_EGG =
            new JeiPoint(
                    (int) DWARF_CENTER_X
                            - SLOT_SIZE / 2,
                    54
            );

    public static final JeiPoint ARROW =
            new JeiPoint(
                    91,
                    27
            );

    public static final JeiPoint CHANCE =
            new JeiPoint(
                    ARROW.x()
                            + ARROW_WIDTH
                            + 5,
                    29
            );

    public static final JeiPoint ROLLS =
            new JeiPoint(
                    CHANCE.x(),
                    CHANCE.y()
                            + 8
            );

    public static final JeiPoint RIGHT_CLICK =
            new JeiPoint(
                    ARROW.x()
                            + (
                            ARROW_WIDTH
                                    - RIGHT_CLICK_SIZE
                    ) / 2,
                    51
            );

    public static final JeiPoint OUTPUT =
            new JeiPoint(
                    142,
                    26
            );

    public static final float OUTPUT_CENTER_X =
            OUTPUT.x()
                    + SLOT_SIZE / 2.0F;

    private JeiBountyLayout() {
    }
}
