package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiLapidaryBenchCategory
        implements IRecipeCategory<JeiLapidaryBenchRecipe> {

    public static final RecipeType<JeiLapidaryBenchRecipe> RECIPE_TYPE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.LAPIDARY_BENCH,
                    JeiLapidaryBenchRecipe.class
            );

    private static final ResourceLocation ARROW_TEXTURE =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jei(
                            JolCraftStrings.underscored(
                                    JolCraftDictionary.RECIPE,
                                    JolCraftDictionary.ARROW
                            )
                    )
            );

    private static final ResourceLocation PLUS_TEXTURE =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jei(
                            JolCraftStrings.underscored(
                                    JolCraftDictionary.RECIPE,
                                    JolCraftDictionary.PLUS,
                                    JolCraftDictionary.SIGN
                            )
                    )
            );

    private static final int WIDTH = 96;
    private static final int HEIGHT = 52;

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_Y = 25;

    private static final int INPUT_X = 2;
    private static final int TOOL_X = 35;
    private static final int OUTPUT_X = 75;

    private static final int PLUS_WIDTH = 13;
    private static final int PLUS_HEIGHT = 13;

    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 16;

    private static final int TEXT_COLOR = 0x888888;

    private final IDrawable background;
    private final IDrawable icon;

    public JeiLapidaryBenchCategory(
            IGuiHelper guiHelper
    ) {
        background =
                guiHelper.createBlankDrawable(
                        WIDTH,
                        HEIGHT
                );

        icon =
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftBlocks
                                        .LAPIDARY_BENCH
                                        .get()
                        )
                );
    }

    @Override
    public RecipeType<JeiLapidaryBenchRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                JolCraftLanguageKeys.JEI_CATEGORY_LAPIDARY_BENCH
        );
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(
            JeiLapidaryBenchRecipe recipe,
            IRecipeSlotsView slots,
            GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        background.draw(
                graphics,
                0,
                0
        );

        int inputRight =
                INPUT_X
                        + SLOT_SIZE;

        int inputGap =
                TOOL_X
                        - inputRight;

        int plusX =
                inputRight
                        + (
                        inputGap
                                - PLUS_WIDTH
                ) / 2
                        - 1;

        graphics.blit(
                PLUS_TEXTURE,
                plusX,
                27,
                0,
                0,
                PLUS_WIDTH,
                PLUS_HEIGHT,
                PLUS_WIDTH,
                PLUS_HEIGHT
        );

        int arrowX =
                TOOL_X
                        + SLOT_SIZE
                        - 1;

        graphics.blit(
                ARROW_TEXTURE,
                arrowX,
                25,
                0,
                0,
                ARROW_WIDTH,
                ARROW_HEIGHT,
                ARROW_WIDTH,
                ARROW_HEIGHT
        );

        drawOutputAmount(
                graphics,
                Minecraft.getInstance().font,
                recipe
        );
    }

    private static void drawOutputAmount(
            GuiGraphics graphics,
            Font font,
            JeiLapidaryBenchRecipe recipe
    ) {
        JeiItemOutcome outcome =
                recipe.outcomes()
                        .getFirst();

        int min =
                outcome.minCount();

        int max =
                outcome.maxCount();

        if (min == 1
                && max == 1) {
            return;
        }

        String text =
                min == max
                        ? String.valueOf(
                        min
                )
                        : min
                        + "-"
                        + max;

        drawCenteredScaledText(
                graphics,
                font,
                text
        );
    }

    private static void drawCenteredScaledText(
            GuiGraphics graphics,
            Font font,
            String text
    ) {
        int stringWidth =
                font.width(
                        text
                );

        float centerX =
                JeiLapidaryBenchCategory.OUTPUT_X
                        + SLOT_SIZE / 2.0F
                        - stringWidth
                        * (float) 0.75
                        / 2.0F;

        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        centerX,
                        43,
                        0
                );

        graphics.pose()
                .scale(
                        (float) 0.75,
                        (float) 0.75,
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

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiLapidaryBenchRecipe recipe,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INPUT_X,
                        SLOT_Y
                )
                .addItemStacks(
                        recipe.inputExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        TOOL_X,
                        SLOT_Y
                )
                .addItemStacks(
                        recipe.toolExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        OUTPUT_X,
                        SLOT_Y
                )
                .addItemStacks(
                        recipe.outputExamples()
                );
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}