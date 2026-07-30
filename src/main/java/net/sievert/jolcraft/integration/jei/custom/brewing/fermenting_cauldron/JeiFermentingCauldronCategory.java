package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_cauldron;

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
public final class JeiFermentingCauldronCategory
        extends AbstractJeiCategory<JeiFermentingCauldronRecipe> {

    public static final RecipeType<JeiFermentingCauldronRecipe> RECIPE_TYPE =
            JeiRecipeTypes.FERMENTING_CAULDRON;

    private static final int WIDTH = 124;
    private static final int HEIGHT = 52;

    private static final int WAITING_THRESHOLD_TICKS = 200;
    private static final int CLOCK_Y = 34;

    private static final JeiRecipeLayout LAYOUT =
            JeiRecipeLayout.twoInputsToOutput(
                    4,
                    44,
                    102,
                    11,
                    13,
                    12,
                    1,
                    9
            );

    public JeiFermentingCauldronCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_FERMENTING_CAULDRON
                ),
                WIDTH,
                HEIGHT,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                Blocks.CAULDRON
                        )
                )
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiFermentingCauldronRecipe recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        drawPreviousInput(
                graphics,
                recipe.previousInput()
        );

        drawJeiPlus(
                graphics,
                LAYOUT.requirePlus()
        );

        JeiDrawHelper.drawArrow(
                graphics,
                LAYOUT.arrow()
        );

        drawResult(
                graphics,
                recipe.result()
        );

        if (recipe.brewTicks() > WAITING_THRESHOLD_TICKS) {
            graphics.renderItem(
                    new ItemStack(
                            Items.CLOCK
                    ),
                    LAYOUT.arrow().x()
                            + (
                            ARROW_WIDTH
                                    - SLOT_CONTENT_SIZE
                    ) / 2,
                    CLOCK_Y
            );
        }
    }

    private static void drawPreviousInput(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingCauldronRecipe.PreviousInput previousInput
    ) {
        switch (previousInput) {
            case JeiFermentingCauldronRecipe.ItemInput ignored -> {
            }

            case JeiFermentingCauldronRecipe.FluidInput fluidInput ->
                    JeiFluidRenderer.drawTinted(
                            graphics,
                            fluidInput.fluid(),
                            LAYOUT.inputA().x(),
                            LAYOUT.inputA().y(),
                            0,
                            SLOT_CONTENT_SIZE,
                            SLOT_CONTENT_SIZE
                    );
        }
    }

    private static void drawResult(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingCauldronRecipe.Result result
    ) {
        switch (result) {
            case JeiFermentingCauldronRecipe.ItemResult ignored -> {
            }

            case JeiFermentingCauldronRecipe.FluidResult fluidResult ->
                    JeiFluidRenderer.drawTinted(
                            graphics,
                            fluidResult.fluid(),
                            LAYOUT.output().x(),
                            LAYOUT.output().y(),
                            0,
                            SLOT_CONTENT_SIZE,
                            SLOT_CONTENT_SIZE
                    );
        }
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiFermentingCauldronRecipe recipe,
            IFocusGroup focuses
    ) {
        addPreviousInput(
                builder,
                recipe.previousInput()
        );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        LAYOUT.requireInputB().x(),
                        LAYOUT.requireInputB().y()
                )
                .addItemStacks(
                        recipe.ingredientExamples()
                );

        addResult(
                builder,
                recipe.result()
        );
    }

    private static void addPreviousInput(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiFermentingCauldronRecipe.PreviousInput previousInput
    ) {
        switch (previousInput) {
            case JeiFermentingCauldronRecipe.ItemInput itemInput ->
                    builder.addSlot(
                                    RecipeIngredientRole.INPUT,
                                    LAYOUT.inputA().x(),
                                    LAYOUT.inputA().y()
                            )
                            .addItemStacks(
                                    itemInput.examples()
                            );

            case JeiFermentingCauldronRecipe.FluidInput fluidInput ->
                    JeiFluidRenderer.addSlot(
                            builder,
                            RecipeIngredientRole.INPUT,
                            LAYOUT.inputA().x(),
                            LAYOUT.inputA().y(),
                            fluidInput.fluid(),
                            SLOT_CONTENT_SIZE,
                            SLOT_CONTENT_SIZE
                    );
        }
    }

    private static void addResult(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiFermentingCauldronRecipe.Result result
    ) {
        switch (result) {
            case JeiFermentingCauldronRecipe.ItemResult itemResult ->
                    builder.addSlot(
                                    RecipeIngredientRole.OUTPUT,
                                    LAYOUT.output().x(),
                                    LAYOUT.output().y()
                            )
                            .addItemStacks(
                                    itemResult.examples()
                            );

            case JeiFermentingCauldronRecipe.FluidResult fluidResult ->
                    JeiFluidRenderer.addSlot(
                            builder,
                            RecipeIngredientRole.OUTPUT,
                            LAYOUT.output().x(),
                            LAYOUT.output().y(),
                            fluidResult.fluid(),
                            SLOT_CONTENT_SIZE,
                            SLOT_CONTENT_SIZE
                    );
        }
    }
}