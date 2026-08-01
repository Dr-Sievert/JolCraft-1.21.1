package net.sievert.jolcraft.integration.jei.util.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.AMOUNT_TEXT_SCALE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.CHANCE_TEXT_SCALE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.TEXT_COLOR;

public final class JeiDrawHelper {

    private JeiDrawHelper() {
    }

    public static void drawArrow(
            @NotNull GuiGraphics graphics,
            @NotNull JeiPoint point
    ) {
        graphics.blit(
                JeiTextures.RECIPE_ARROW,
                point.x(),
                point.y(),
                0,
                0,
                JeiTextures.ARROW_WIDTH,
                JeiTextures.ARROW_HEIGHT,
                JeiTextures.ARROW_WIDTH,
                JeiTextures.ARROW_HEIGHT
        );
    }

    public static void drawPlus(
            @NotNull GuiGraphics graphics,
            @NotNull JeiPoint point
    ) {
        graphics.blit(
                JeiTextures.RECIPE_PLUS,
                point.x(),
                point.y(),
                0,
                0,
                JeiTextures.PLUS_WIDTH,
                JeiTextures.PLUS_HEIGHT,
                JeiTextures.PLUS_WIDTH,
                JeiTextures.PLUS_HEIGHT
        );
    }

    public static void drawRightClick(
            @NotNull GuiGraphics graphics,
            @NotNull JeiPoint point
    ) {
        //noinspection SuspiciousNameCombination
        graphics.blit(
                JeiTextures.RIGHT_CLICK,
                point.x(),
                point.y(),
                0,
                0,
                JeiTextures.RIGHT_CLICK_SIZE,
                JeiTextures.RIGHT_CLICK_SIZE,
                JeiTextures.RIGHT_CLICK_SIZE,
                JeiTextures.RIGHT_CLICK_SIZE
        );
    }

    public static void drawAmountRange(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            int min,
            int max,
            int slotX,
            int y
    ) {
        if (min == 1
                && max == 1) {
            return;
        }

        drawCenteredScaledText(
                graphics,
                font,
                formatRange(
                        min,
                        max
                ),
                slotX,
                SLOT_SIZE,
                y,
                AMOUNT_TEXT_SCALE
        );
    }

    public static void drawUnknownAmount(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            int slotX,
            int y
    ) {
        drawCenteredScaledText(
                graphics,
                font,
                "?",
                slotX,
                SLOT_SIZE,
                y,
                AMOUNT_TEXT_SCALE
        );
    }

    public static @NotNull String formatRange(
            int min,
            int max
    ) {
        return min == max
                ? String.valueOf(
                min
        )
                : min
                + "-"
                + max;
    }

    public static void drawCenteredText(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull String text,
            float centerX,
            int y
    ) {
        graphics.drawString(
                font,
                text,
                Math.round(
                        centerX
                                - font.width(
                                text
                        ) / 2.0F
                ),
                y,
                TEXT_COLOR,
                false
        );
    }

    public static void drawCenteredText(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull Component text,
            float centerX,
            int y
    ) {
        drawCenteredText(
                graphics,
                font,
                text.getString(),
                centerX,
                y
        );
    }

    public static void drawCenteredScaledText(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull String text,
            int startX,
            int width,
            int y,
            float scale
    ) {
        float centerX =
                startX
                        + width / 2.0F
                        - font.width(
                        text
                )
                        * scale
                        / 2.0F;

        drawScaledText(
                graphics,
                font,
                text,
                centerX,
                y,
                scale
        );
    }

    public static void drawScaledText(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull String text,
            float x,
            float y,
            float scale
    ) {
        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        x,
                        y,
                        0.0F
                );

        graphics.pose()
                .scale(
                        scale,
                        scale,
                        1.0F
                );

        graphics.drawString(
                font,
                text,
                0,
                0,
                TEXT_COLOR,
                false
        );

        graphics.pose()
                .popPose();
    }

    public static void drawChance(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            double chance,
            int x,
            int y
    ) {
        drawScaledText(
                graphics,
                font,
                formatChance(
                        chance
                ),
                x,
                y,
                CHANCE_TEXT_SCALE
        );
    }

    public static void drawRolls(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            int minRolls,
            int maxRolls,
            int x,
            int y
    ) {
        if (maxRolls <= 1) {
            return;
        }

        drawScaledText(
                graphics,
                font,
                formatRolls(
                        minRolls,
                        maxRolls
                ),
                x,
                y,
                CHANCE_TEXT_SCALE
        );
    }

    public static void drawCenteredRolls(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            int minRolls,
            int maxRolls,
            int startX,
            int width,
            int y
    ) {
        if (maxRolls <= 1) {
            return;
        }

        drawCenteredScaledText(
                graphics,
                font,
                formatRolls(
                        minRolls,
                        maxRolls
                ),
                startX,
                width,
                y,
                CHANCE_TEXT_SCALE
        );
    }

    public static @NotNull String formatRolls(
            int minRolls,
            int maxRolls
    ) {
        if (minRolls <= 0) {
            throw new IllegalArgumentException(
                    "minRolls must be positive"
            );
        }

        if (maxRolls < minRolls) {
            throw new IllegalArgumentException(
                    "maxRolls must be at least minRolls"
            );
        }

        if (minRolls == maxRolls) {
            return "Rolls: "
                    + minRolls;
        }

        return "Rolls: "
                + minRolls
                + "-"
                + maxRolls;
    }

    public static void drawCenteredChance(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            double chance,
            int startX,
            int width,
            int y
    ) {
        drawCenteredScaledText(
                graphics,
                font,
                formatChance(
                        chance
                ),
                startX,
                width,
                y,
                CHANCE_TEXT_SCALE
        );
    }

    public static void drawCenteredChance(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            double chance,
            @NotNull String translationKey,
            int startX,
            int width,
            int y
    ) {
        drawCenteredScaledText(
                graphics,
                font,
                Component.translatable(
                                translationKey,
                                formatChance(
                                        chance
                                )
                        )
                        .getString(),
                startX,
                width,
                y,
                CHANCE_TEXT_SCALE
        );
    }

    public static void drawCenteredRolls(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            int rolls,
            int startX,
            int width,
            int y
    ) {
        if (rolls <= 1) {
            return;
        }

        drawCenteredScaledText(
                graphics,
                font,
                formatRolls(
                        rolls
                ),
                startX,
                width,
                y,
                CHANCE_TEXT_SCALE
        );
    }

    public static @NotNull String formatChance(
            double chance
    ) {
        double percentage =
                Math.clamp(
                        chance,
                        0.0D,
                        1.0D
                )
                        * 100.0D;

        if (percentage >= 10.0D) {
            return String.format(
                    Locale.ROOT,
                    "%.0f%%",
                    percentage
            );
        }

        if (percentage >= 1.0D) {
            return String.format(
                    Locale.ROOT,
                    "%.1f%%",
                    percentage
            );
        }

        return String.format(
                Locale.ROOT,
                "%.2f%%",
                percentage
        );
    }

    public static @NotNull String formatRolls(
            int rolls
    ) {
        if (rolls <= 0) {
            throw new IllegalArgumentException(
                    "rolls must be positive"
            );
        }

        return "Rolls: "
                + rolls;
    }

    public static boolean contains(
            double mouseX,
            double mouseY,
            int x,
            int y,
            int width,
            int height
    ) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }
}
