package net.sievert.jolcraft.integration.jei.util.recipe;

import net.sievert.jolcraft.integration.jei.util.gui.JeiPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.PLUS_WIDTH;

public record JeiRecipeLayout(
        @NotNull JeiPoint inputA,
        @Nullable JeiPoint inputB,
        @NotNull JeiPoint output,
        @Nullable JeiPoint plus,
        @NotNull JeiPoint arrow
) {

    public static @NotNull JeiRecipeLayout singleInputToOutput(
            int inputX,
            int outputX,
            int slotY,
            int arrowY,
            int arrowOffsetX
    ) {
        return new JeiRecipeLayout(
                new JeiPoint(
                        inputX,
                        slotY
                ),
                null,
                new JeiPoint(
                        outputX,
                        slotY
                ),
                null,
                new JeiPoint(
                        inputX
                                + SLOT_SIZE
                                + arrowOffsetX,
                        arrowY
                )
        );
    }

    public static @NotNull JeiRecipeLayout twoInputsToOutput(
            int inputAX,
            int inputBX,
            int outputX,
            int slotY,
            int plusY,
            int arrowY,
            int plusOffsetX,
            int arrowOffsetX
    ) {
        int inputARight =
                inputAX
                        + SLOT_SIZE;

        int inputGap =
                inputBX
                        - inputARight;

        int plusX =
                inputARight
                        + (
                        inputGap
                                - PLUS_WIDTH
                ) / 2
                        + plusOffsetX;

        return new JeiRecipeLayout(
                new JeiPoint(
                        inputAX,
                        slotY
                ),
                new JeiPoint(
                        inputBX,
                        slotY
                ),
                new JeiPoint(
                        outputX,
                        slotY
                ),
                new JeiPoint(
                        plusX,
                        plusY
                ),
                new JeiPoint(
                        inputBX
                                + SLOT_SIZE
                                + arrowOffsetX,
                        arrowY
                )
        );
    }

    public @NotNull JeiPoint requireInputB() {
        if (inputB == null) {
            throw new IllegalStateException(
                    "Layout does not define a second input"
            );
        }

        return inputB;
    }

    public @NotNull JeiPoint requirePlus() {
        if (plus == null) {
            throw new IllegalStateException(
                    "Layout does not define a plus sign"
            );
        }

        return plus;
    }
}
