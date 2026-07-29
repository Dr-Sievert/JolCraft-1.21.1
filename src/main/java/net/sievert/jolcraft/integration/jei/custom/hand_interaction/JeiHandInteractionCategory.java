package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawableHelper;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.gui.JeiPoint;
import net.sievert.jolcraft.integration.jei.util.gui.JeiTextures;
import net.sievert.jolcraft.integration.jei.util.gui.render.JeiEffectRenderer;
import net.sievert.jolcraft.integration.jei.util.gui.render.JeiEntityRenderer;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeLayout;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_CONTENT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiHandInteractionCategory
        extends AbstractJeiCategory<JeiHandInteractionRecipe> {

    public static final RecipeType<JeiHandInteractionRecipe> RECIPE_TYPE =
            JeiRecipeTypes.HAND_INTERACTION;

    private static final int WIDTH = 152;
    private static final int HEIGHT = 72;

    private static final int HAND_Y = 34;
    private static final int AMOUNT_Y = 26;

    private static final int ENTITY_VERTICAL_OFFSET = 8;
    private static final int ENTITY_AMOUNT_Y = 60;
    private static final int ENTITY_EGG_Y =
            42 + ENTITY_VERTICAL_OFFSET;
    private static final int ENTITY_BOTTOM_Y =
            35 + ENTITY_VERTICAL_OFFSET;

    private static final JeiRecipeLayout LAYOUT =
            JeiRecipeLayout.twoInputsToOutput(
                    4,
                    43,
                    122,
                    4,
                    5,
                    5,
                    0,
                    10
            );

    private static final int OUTPUT_CENTER_X =
            LAYOUT.output().x()
                    + SLOT_SIZE / 2;

    private static final JeiPoint CHANCE =
            new JeiPoint(
                    LAYOUT.arrow().x()
                            + JeiTextures.ARROW_WIDTH
                            + 5,
                    LAYOUT.arrow().y()
                            + 6
            );

    private static final JeiPoint RIGHT_CLICK =
            new JeiPoint(
                    LAYOUT.arrow().x()
                            + (
                            JeiTextures.ARROW_WIDTH
                                    - JeiTextures.RIGHT_CLICK_SIZE
                    ) / 2,
                    32
            );

    public JeiHandInteractionCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_HAND_INTERACTION
                ),
                WIDTH,
                HEIGHT,
                JeiDrawableHelper.sprite(
                        JeiTextures.HAND_RIGHT,
                        JeiTextures.HAND_SIZE,
                        JeiTextures.HAND_SIZE
                )
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiHandInteractionRecipe entry,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        drawJeiPlus(
                graphics,
                LAYOUT.requirePlus()
        );

        drawHands(
                graphics
        );

        JeiDrawHelper.drawArrow(
                graphics,
                LAYOUT.arrow()
        );

        JeiDrawHelper.drawRightClick(
                graphics,
                RIGHT_CLICK
        );

        switch (entry.result()) {
            case JeiHandInteractionRecipe.EffectResult effectResult ->
                    drawEffectResult(
                            graphics,
                            effectResult
                    );

            case JeiHandInteractionRecipe.EntityResult entityResult ->
                    drawEntityResult(
                            graphics,
                            entityResult
                    );

            default -> {
            }
        }

        Font font =
                Minecraft.getInstance().font;

        drawOutputChanceAndRolls(
                graphics,
                font,
                entry
        );

        drawInputAmount(
                graphics,
                font,
                entry.recipe().actionA(),
                LAYOUT.inputA().x()
        );

        drawInputAmount(
                graphics,
                font,
                entry.recipe().actionB(),
                LAYOUT.requireInputB().x()
        );

        drawOutputAmount(
                graphics,
                font,
                entry
        );
    }

    private static void drawHands(
            @NotNull GuiGraphics graphics
    ) {
        graphics.blitSprite(
                JeiTextures.HAND_LEFT,
                centeredSpriteX(
                        LAYOUT.inputA().x()
                ),
                HAND_Y,
                JeiTextures.HAND_SIZE,
                JeiTextures.HAND_SIZE
        );

        graphics.blitSprite(
                JeiTextures.HAND_RIGHT,
                centeredSpriteX(
                        LAYOUT.requireInputB().x()
                ),
                HAND_Y,
                JeiTextures.HAND_SIZE,
                JeiTextures.HAND_SIZE
        );
    }

    private static int centeredSpriteX(
            int slotX
    ) {
        return slotX
                + (
                SLOT_SIZE
                        - JeiTextures.HAND_SIZE
        ) / 2;
    }

    private static void drawEffectResult(
            @NotNull GuiGraphics graphics,
            @NotNull JeiHandInteractionRecipe.EffectResult result
    ) {
        JeiEffectRenderer.draw(
                graphics,
                result.effect(),
                LAYOUT.output().x() + 1,
                LAYOUT.output().y() + 1,
                0,
                SLOT_CONTENT_SIZE,
                SLOT_CONTENT_SIZE
        );
    }

    private static void drawEntityResult(
            @NotNull GuiGraphics graphics,
            @NotNull JeiHandInteractionRecipe.EntityResult result
    ) {
        LivingEntity entity =
                JeiEntityRenderer.createLiving(
                        result.entityType()
                );

        if (entity == null) {
            return;
        }

        Font font =
                Minecraft.getInstance().font;

        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_TOOLTIP_SPAWN
                ),
                OUTPUT_CENTER_X,
                2
        );

        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                result.entityType().getDescription(),
                OUTPUT_CENTER_X,
                12
        );

        JeiEntityRenderer.renderToBounds(
                graphics,
                entity,
                OUTPUT_CENTER_X,
                ENTITY_BOTTOM_Y,
                22.0F,
                142.0F,
                -5.0F
        );
    }

    private static void drawOutputChanceAndRolls(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull JeiHandInteractionRecipe entry
    ) {
        if (!(entry.result()
                instanceof JeiHandInteractionRecipe.ItemResult(
                JeiItemOutcome outcome
        ))) {
            return;
        }

        double chancePerRoll =
                outcome.chancePerRoll();

        if (chancePerRoll < 1.0D
                || outcome.rolls() > 1) {
            JeiDrawHelper.drawChance(
                    graphics,
                    font,
                    chancePerRoll,
                    CHANCE.x(),
                    CHANCE.y()
            );
        }

        JeiDrawHelper.drawRolls(
                graphics,
                font,
                outcome.rolls(),
                CHANCE.x(),
                CHANCE.y() + 8
        );
    }

    private static void drawInputAmount(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull ItemInputAction action,
            int slotX
    ) {
        if (action.type()
                == ItemInputAction.Type.CATALYST) {
            return;
        }

        int amount =
                action.resolvedAmount();

        JeiDrawHelper.drawAmountRange(
                graphics,
                font,
                amount,
                amount,
                slotX,
                AMOUNT_Y
        );
    }

    private static void drawOutputAmount(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull JeiHandInteractionRecipe entry
    ) {
        if (entry.result()
                instanceof JeiHandInteractionRecipe.ItemResult(
                JeiItemOutcome outcome
        )) {
            JeiDrawHelper.drawAmountRange(
                    graphics,
                    font,
                    outcome.minCount(),
                    outcome.maxCount(),
                    LAYOUT.output().x(),
                    AMOUNT_Y
            );

            return;
        }

        if (entry.result()
                instanceof JeiHandInteractionRecipe.EntityResult entityResult) {
            JeiDrawHelper.drawAmountRange(
                    graphics,
                    font,
                    entityResult.minCount(),
                    entityResult.maxCount(),
                    LAYOUT.output().x(),
                    ENTITY_AMOUNT_Y
            );
        }
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltipStrings(
            JeiHandInteractionRecipe entry,
            IRecipeSlotsView slots,
            double mouseX,
            double mouseY
    ) {
        if (!(entry.result()
                instanceof JeiHandInteractionRecipe.EffectResult(
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
            JeiHandInteractionRecipe entry,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        LAYOUT.inputA().x(),
                        LAYOUT.inputA().y()
                )
                .addItemStacks(
                        entry.ingredientAExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        LAYOUT.requireInputB().x(),
                        LAYOUT.requireInputB().y()
                )
                .addItemStacks(
                        entry.ingredientBExamples()
                );

        if (entry.result()
                instanceof JeiHandInteractionRecipe.ItemResult itemResult) {
            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            LAYOUT.output().x(),
                            LAYOUT.output().y()
                    )
                    .addItemStack(
                            itemResult.example()
                    );

            return;
        }

        if (entry.result()
                instanceof JeiHandInteractionRecipe.EntityResult entityResult) {
            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            LAYOUT.output().x(),
                            ENTITY_EGG_Y
                    )
                    .addItemStack(
                            entityResult.spawnEgg()
                    );
        }
    }
}