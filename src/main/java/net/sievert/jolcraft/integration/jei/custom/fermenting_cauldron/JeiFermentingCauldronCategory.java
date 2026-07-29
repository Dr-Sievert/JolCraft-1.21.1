package net.sievert.jolcraft.integration.jei.custom.fermenting_cauldron;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.gui.render.JeiEffectRenderer;
import net.sievert.jolcraft.integration.jei.util.gui.render.JeiFluidRenderer;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeLayout;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_CONTENT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiFermentingCauldronCategory extends AbstractJeiCategory<JeiFermentingCauldronRecipe> {

    public static final RecipeType<JeiFermentingCauldronRecipe> RECIPE_TYPE = JeiRecipeTypes.FERMENTING_CAULDRON;

    private static final int WIDTH = 124;
    private static final int HEIGHT = 40;

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
    }

    private static void drawPreviousInput(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingCauldronRecipe.PreviousInput previousInput
    ) {
        FluidStack fluid = switch (previousInput) {
            case JeiFermentingCauldronRecipe.ItemInput ignored ->
                    JeiFluidRenderer.unfinishedDwarvenBrew();

            case JeiFermentingCauldronRecipe.FluidInput(
                    FluidStack inputFluid
            ) -> inputFluid;
        };

        JeiFluidRenderer.drawTinted(
                graphics,
                fluid,
                LAYOUT.inputA().x(),
                LAYOUT.inputA().y(),
                0,
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );
    }

    private static void drawResult(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingCauldronRecipe.Result result
    ) {
        switch (result) {
            case JeiFermentingCauldronRecipe.EffectResult effectResult ->
                    drawEffectResult(
                            graphics,
                            effectResult
                    );

            case JeiFermentingCauldronRecipe.FluidResult(
                    FluidStack fluid
            ) -> JeiFluidRenderer.drawTinted(
                    graphics,
                    fluid,
                    LAYOUT.output().x(),
                    LAYOUT.output().y(),
                    1,
                    SLOT_CONTENT_SIZE,
                    SLOT_CONTENT_SIZE
            );

            default -> {
            }
        }
    }

    private static void drawEffectResult(
            @NotNull GuiGraphics graphics,
            @NotNull JeiFermentingCauldronRecipe.EffectResult result
    ) {
        JeiFluidRenderer.drawTinted(
                graphics,
                JeiFluidRenderer.unfinishedDwarvenBrew(),
                LAYOUT.output().x(),
                LAYOUT.output().y(),
                0,
                SLOT_SIZE,
                SLOT_SIZE
        );

        JeiEffectRenderer.draw(
                graphics,
                result.effect(),
                LAYOUT.output().x(),
                LAYOUT.output().y(),
                1,
                SLOT_SIZE,
                SLOT_SIZE
        );
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltipStrings(
            JeiFermentingCauldronRecipe recipe,
            IRecipeSlotsView slots,
            double mouseX,
            double mouseY
    ) {
        if (!(recipe.result()
                instanceof JeiFermentingCauldronRecipe.EffectResult(
                MobEffectInstance effect
        )) || !JeiDrawHelper.contains(
                mouseX,
                mouseY,
                LAYOUT.output().x(),
                LAYOUT.output().y(),
                SLOT_SIZE,
                SLOT_SIZE
        )) {
            return List.of();
        }

        return JeiEffectRenderer.tooltip(
                effect
        );
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
            case JeiFermentingCauldronRecipe.ItemInput(
                    List<ItemStack> examples
            ) -> builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            LAYOUT.inputA().x(),
                            LAYOUT.inputA().y()
                    )
                    .addItemStacks(
                            examples
                    );

            case JeiFermentingCauldronRecipe.FluidInput(
                    FluidStack fluid
            ) -> JeiFluidRenderer.addSlot(
                    builder,
                    RecipeIngredientRole.INPUT,
                    LAYOUT.inputA().x(),
                    LAYOUT.inputA().y(),
                    fluid,
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
            case JeiFermentingCauldronRecipe.ItemResult(
                    List<ItemStack> examples
            ) -> builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            LAYOUT.output().x(),
                            LAYOUT.output().y()
                    )
                    .addItemStacks(
                            examples
                    );

            case JeiFermentingCauldronRecipe.FluidResult(
                    FluidStack fluid
            ) -> JeiFluidRenderer.addSlot(
                    builder,
                    RecipeIngredientRole.OUTPUT,
                    LAYOUT.output().x(),
                    LAYOUT.output().y(),
                    fluid,
                    SLOT_CONTENT_SIZE,
                    SLOT_CONTENT_SIZE
            );

            default -> {
            }
        }
    }
}