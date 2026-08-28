package net.sievert.jolcraft.integration.jei.custom.brewing.corruption;

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
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.gui.JeiPoint;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiCorruptionCategory
        extends AbstractJeiCategory<JeiCorruptionRecipe> {

    public static final RecipeType<JeiCorruptionRecipe> RECIPE_TYPE =
            JeiRecipeTypes.CORRUPTION;

    private static final int WIDTH = 114;
    private static final int HEIGHT = 44;

    private static final JeiPoint INPUT =
            new JeiPoint(
                    8,
                    13
            );

    private static final JeiPoint PLUS =
            new JeiPoint(
                    27,
                    15
            );

    private static final JeiPoint INGREDIENT =
            new JeiPoint(
                    41,
                    13
            );

    private static final JeiPoint ARROW =
            new JeiPoint(
                    62,
                    13
            );

    private static final JeiPoint OUTPUT =
            new JeiPoint(
                    87,
                    13
            );

    public JeiCorruptionCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_CORRUPTION
                ),
                WIDTH,
                HEIGHT,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                Blocks.BREWING_STAND
                        )
                )
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiCorruptionRecipe recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        JeiDrawHelper.drawPlus(
                graphics,
                PLUS
        );

        JeiDrawHelper.drawArrow(
                graphics,
                ARROW
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiCorruptionRecipe recipe,
            IFocusGroup focuses
    ) {
        var inputSlot =
                builder.addSlot(
                                RecipeIngredientRole.INPUT,
                                INPUT.x(),
                                INPUT.y()
                        )
                        .addItemStacks(
                                recipe.potionInputs()
                        );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INGREDIENT.x(),
                        INGREDIENT.y()
                )
                .addItemStack(
                        recipe.ingredient()
                );

        var outputSlot =
                builder.addSlot(
                                RecipeIngredientRole.OUTPUT,
                                OUTPUT.x(),
                                OUTPUT.y()
                        )
                        .addItemStacks(
                                recipe.potionOutputs()
                        );

        builder.createFocusLink(
                inputSlot,
                outputSlot
        );
    }
}
