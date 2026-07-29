package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeLayout;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiLapidaryBenchCategory
        extends AbstractJeiCategory<JeiLapidaryBenchRecipe> {

    public static final RecipeType<JeiLapidaryBenchRecipe> RECIPE_TYPE =
            JeiRecipeTypes.LAPIDARY_BENCH;

    private static final int WIDTH = 96;
    private static final int HEIGHT = 52;
    private static final int AMOUNT_Y = 43;

    private static final JeiRecipeLayout LAYOUT =
            JeiRecipeLayout.twoInputsToOutput(
                    2,
                    35,
                    75,
                    25,
                    27,
                    25,
                    -1,
                    -1
            );

    public JeiLapidaryBenchCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_LAPIDARY_BENCH
                ),
                WIDTH,
                HEIGHT,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftBlocks
                                        .LAPIDARY_BENCH
                                        .get()
                        )
                )
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiLapidaryBenchRecipe recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        JeiDrawHelper.drawPlus(
                graphics,
                LAYOUT.requirePlus()
        );

        JeiDrawHelper.drawArrow(
                graphics,
                LAYOUT.arrow()
        );

        JeiDrawHelper.drawAmountRange(
                graphics,
                Minecraft.getInstance().font,
                recipe.outcome().minCount(),
                recipe.outcome().maxCount(),
                LAYOUT.output().x(),
                AMOUNT_Y
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiLapidaryBenchRecipe recipe,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        LAYOUT.inputA().x(),
                        LAYOUT.inputA().y()
                )
                .addItemStacks(
                        recipe.inputExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        LAYOUT.requireInputB().x(),
                        LAYOUT.requireInputB().y()
                )
                .addItemStacks(
                        recipe.toolExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        LAYOUT.output().x(),
                        LAYOUT.output().y()
                )
                .addItemStack(
                        recipe.outputExample()
                );
    }
}
