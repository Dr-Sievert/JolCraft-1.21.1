package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.render.JeiFluidRenderer;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeLayout;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_CONTENT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.ARROW_WIDTH;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiFermentingBarrelCategory
        extends AbstractJeiCategory<JeiFermentingBarrelRecipe> {

    public static final RecipeType<JeiFermentingBarrelRecipe> RECIPE_TYPE =
            JeiRecipeTypes.FERMENTING_BARREL;

    private static final int WIDTH = 124;
    private static final int HEIGHT = 52;

    private static final int AGING_SLOT_Y = 13;
    private static final int CLOCK_Y = 34;

    private static final JeiRecipeLayout AGING_LAYOUT =
            JeiRecipeLayout.singleInputToOutput(
                    20,
                    86,
                    AGING_SLOT_Y,
                    14,
                    13
            );

    private static final JeiRecipeLayout EXTRACTION_LAYOUT =
            JeiRecipeLayout.twoInputsToOutput(
                    4,
                    44,
                    102,
                    17,
                    19,
                    18,
                    1,
                    9
            );

    public JeiFermentingBarrelCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_FERMENTING_BARREL
                ),
                WIDTH,
                HEIGHT,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                Blocks.BARREL
                        )
                )
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiFermentingBarrelRecipe recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        switch (recipe.process()) {
            case JeiFermentingBarrelRecipe.AgingProcess aging ->
                    drawAgingProcess(
                            graphics,
                            aging
                    );

            case JeiFermentingBarrelRecipe.ExtractionProcess extraction ->
                    drawExtractionProcess(
                            graphics,
                            extraction
                    );
        }
    }

    private static void drawAgingProcess(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingBarrelRecipe.AgingProcess process
    ) {
        JeiFluidRenderer.drawTinted(
                graphics,
                process.input().brew(),
                AGING_LAYOUT.inputA().x(),
                AGING_LAYOUT.inputA().y(),
                0,
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );

        JeiFluidRenderer.drawTinted(
                graphics,
                process.output().brew(),
                AGING_LAYOUT.output().x(),
                AGING_LAYOUT.output().y(),
                0,
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );

        JeiDrawHelper.drawArrow(
                graphics,
                AGING_LAYOUT.arrow()
        );

        graphics.renderItem(
                new ItemStack(
                        Items.CLOCK
                ),
                AGING_LAYOUT.arrow().x()
                        + (
                        ARROW_WIDTH
                                - SLOT_CONTENT_SIZE
                ) / 2,
                CLOCK_Y
        );

    }

    private void drawExtractionProcess(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingBarrelRecipe.ExtractionProcess process
    ) {
        JeiFluidRenderer.drawTinted(
                graphics,
                process.brew(),
                EXTRACTION_LAYOUT.inputA().x(),
                EXTRACTION_LAYOUT.inputA().y(),
                0,
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );

        drawJeiPlus(
                graphics,
                EXTRACTION_LAYOUT.requirePlus()
        );

        JeiDrawHelper.drawArrow(
                graphics,
                EXTRACTION_LAYOUT.arrow()
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiFermentingBarrelRecipe recipe,
            IFocusGroup focuses
    ) {
        switch (recipe.process()) {
            case JeiFermentingBarrelRecipe.AgingProcess aging ->
                    setAgingRecipe(
                            builder,
                            aging
                    );

            case JeiFermentingBarrelRecipe.ExtractionProcess extraction ->
                    setExtractionRecipe(
                            builder,
                            extraction
                    );
        }
    }

    private static void setAgingRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiFermentingBarrelRecipe.AgingProcess process
    ) {
        JeiFluidRenderer.addSlot(
                builder,
                RecipeIngredientRole.INPUT,
                AGING_LAYOUT.inputA().x(),
                AGING_LAYOUT.inputA().y(),
                process.input().brew(),
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );

        JeiFluidRenderer.addSlot(
                builder,
                RecipeIngredientRole.OUTPUT,
                AGING_LAYOUT.output().x(),
                AGING_LAYOUT.output().y(),
                process.output().brew(),
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );
    }

    private static void setExtractionRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiFermentingBarrelRecipe.ExtractionProcess process
    ) {
        JeiFluidRenderer.addSlot(
                builder,
                RecipeIngredientRole.INPUT,
                EXTRACTION_LAYOUT.inputA().x(),
                EXTRACTION_LAYOUT.inputA().y(),
                process.brew(),
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        EXTRACTION_LAYOUT.requireInputB().x(),
                        EXTRACTION_LAYOUT.requireInputB().y()
                )
                .addItemStacks(
                        process.containerExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        EXTRACTION_LAYOUT.output().x(),
                        EXTRACTION_LAYOUT.output().y()
                )
                .addItemStacks(
                        process.resultExamples()
                );
    }
}