package net.sievert.jolcraft.integration.jei.custom.mortar;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.gui.JeiPoint;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiLootConditionTooltip;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.ARROW_WIDTH;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.PLUS_WIDTH;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiMortarCategory
        extends AbstractJeiCategory<JeiMortarRecipe> {

    public static final RecipeType<JeiMortarRecipe> RECIPE_TYPE = JeiRecipeTypes.MORTAR;

    private static final int WIDTH = 168;
    private static final int HEIGHT = 68;

    private static final int SLOT_Y = 25;
    private static final int PLUS_Y = 27;
    private static final int ARROW_Y = 25;
    private static final int OPERAND_GAP = 15;
    private static final int ARROW_GAP = 3;
    private static final int GRINDING_WORK_Y = 8;

    private static final int AMOUNT_Y = 43;
    private static final int CHANCE_Y = 52;
    private static final int ROLLS_Y = 60;

    public JeiMortarCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_MORTAR
                ),
                WIDTH,
                HEIGHT,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftBlocks.MORTAR.get()
                        )
                )
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiMortarRecipe recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        Layout layout =
                createLayout(
                        recipe.inputExamples().size()
                );

        for (JeiPoint plus : layout.pluses()) {
            JeiDrawHelper.drawPlus(
                    graphics,
                    plus
            );
        }

        JeiDrawHelper.drawArrow(
                graphics,
                layout.arrow()
        );

        Font font =
                Minecraft.getInstance().font;

        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_MORTAR_GRINDING_WORK,
                        recipe.recipe().grindingWork()
                ),
                WIDTH / 2.0F,
                GRINDING_WORK_Y
        );

        JeiItemOutcome outcome =
                recipe.outcome();

        JeiDrawHelper.drawAmountRange(
                graphics,
                font,
                outcome.minCount(),
                outcome.maxCount(),
                layout.output().x(),
                AMOUNT_Y
        );

        if (outcome.chancePerRoll() < 1.0D
                || outcome.hasMultipleRolls()) {
            JeiDrawHelper.drawCenteredChance(
                    graphics,
                    font,
                    outcome.chancePerRoll(),
                    layout.output().x(),
                    SLOT_SIZE,
                    CHANCE_Y
            );
        }

        JeiDrawHelper.drawCenteredRolls(
                graphics,
                font,
                outcome.minRolls(),
                outcome.maxRolls(),
                layout.output().x(),
                SLOT_SIZE,
                ROLLS_Y
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiMortarRecipe recipe,
            IFocusGroup focuses
    ) {
        Layout layout =
                createLayout(
                        recipe.inputExamples().size()
                );

        for (int index = 0;
             index < recipe.inputExamples().size();
             index++) {
            JeiPoint input =
                    layout.inputs().get(index);

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            input.x(),
                            input.y()
                    )
                    .addItemStacks(
                            recipe.inputExamples().get(index)
                    );
        }

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        layout.pestle().x(),
                        layout.pestle().y()
                )
                .addItemStacks(
                        recipe.pestleExamples()
                );

        var outputSlot =
                builder.addSlot(
                                RecipeIngredientRole.OUTPUT,
                                layout.output().x(),
                                layout.output().y()
                        )
                        .addItemStack(
                                recipe.outputExample()
                        );

        JeiLootConditionTooltip.add(
                outputSlot,
                recipe.outcome()
        );
    }

    private static @NotNull Layout createLayout(
            int inputCount
    ) {
        if (inputCount < 1 || inputCount > 3) {
            throw new IllegalArgumentException(
                    "Mortar JEI layout requires one to three inputs"
            );
        }

        int operandCount =
                inputCount + 1;

        int rowWidth =
                operandCount * SLOT_SIZE
                        + inputCount * OPERAND_GAP
                        + ARROW_GAP
                        + ARROW_WIDTH
                        + ARROW_GAP
                        + SLOT_SIZE;

        int x =
                (WIDTH - rowWidth) / 2;

        List<JeiPoint> inputs =
                new ArrayList<>(inputCount);

        List<JeiPoint> pluses =
                new ArrayList<>(inputCount);

        for (int index = 0;
             index < inputCount;
             index++) {
            inputs.add(
                    new JeiPoint(
                            x,
                            SLOT_Y
                    )
            );

            x += SLOT_SIZE;

            pluses.add(
                    new JeiPoint(
                            x
                                    + (
                                    OPERAND_GAP
                                            - PLUS_WIDTH
                            ) / 2,
                            PLUS_Y
                    )
            );

            x += OPERAND_GAP;
        }

        JeiPoint pestle =
                new JeiPoint(
                        x,
                        SLOT_Y
                );

        x += SLOT_SIZE + ARROW_GAP;

        JeiPoint arrow =
                new JeiPoint(
                        x,
                        ARROW_Y
                );

        x += ARROW_WIDTH + ARROW_GAP;

        JeiPoint output =
                new JeiPoint(
                        x,
                        SLOT_Y
                );

        return new Layout(
                List.copyOf(inputs),
                pestle,
                output,
                List.copyOf(pluses),
                arrow
        );
    }

    private record Layout(
            List<JeiPoint> inputs,
            JeiPoint pestle,
            JeiPoint output,
            List<JeiPoint> pluses,
            JeiPoint arrow
    ) {
    }
}
