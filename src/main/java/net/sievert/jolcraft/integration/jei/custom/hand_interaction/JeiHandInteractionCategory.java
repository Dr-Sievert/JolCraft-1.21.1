package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

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
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiHandInteractionCategory
        implements IRecipeCategory<JeiHandInteractionRecipe> {

    public static final RecipeType<JeiHandInteractionRecipe> RECIPE_TYPE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.HAND_INTERACTION,
                    JeiHandInteractionRecipe.class
            );

    private static final ResourceLocation HAND_RIGHT =
            JolCraftTextures.modSprite(
                    "hand_right"
            );

    private static final ResourceLocation HAND_LEFT =
            JolCraftTextures.modSprite(
                    "hand_left"
            );

    private static final ResourceLocation RIGHT_CLICK_TEXTURE =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/sprites/toast/right_click.png"
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

    private static final int WIDTH = 138;
    private static final int HEIGHT = 72;

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_Y = 4;
    private static final int ENTITY_EGG_Y = 42;

    private static final int GAP = 4;
    private static final int PLUS_WIDTH = 13;

    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 16;
    private static final int ARROW_Y = 5;

    private static final int INPUT_A_X = 4;

    private static final int PLUS_X =
            INPUT_A_X
                    + SLOT_SIZE
                    + GAP;

    private static final int INPUT_B_X =
            PLUS_X
                    + PLUS_WIDTH
                    + GAP;

    private static final int ARROW_X =
            INPUT_B_X
                    + SLOT_SIZE
                    + 10;

    private static final int OUTPUT_X =
            ARROW_X
                    + ARROW_WIDTH
                    + 16;

    private static final int HAND_SIZE = 16;
    private static final int HAND_Y = 34;

    private static final int HAND_LEFT_X =
            INPUT_A_X
                    + (
                    SLOT_SIZE - HAND_SIZE
            ) / 2;

    private static final int HAND_RIGHT_X =
            INPUT_B_X
                    + (
                    SLOT_SIZE - HAND_SIZE
            ) / 2;

    private static final int PLUS_Y = 5;

    private static final int RIGHT_CLICK_SIZE = 20;

    private static final int RIGHT_CLICK_X =
            ARROW_X
                    + (
                    ARROW_WIDTH - RIGHT_CLICK_SIZE
            ) / 2;

    private static final int RIGHT_CLICK_Y = 32;

    private static final int AMOUNT_Y = 23;
    private static final int ENTITY_AMOUNT_Y = 60;

    private static final int TEXT_COLOR = 0x888888;

    private final IDrawable background;
    private final IDrawable plus;
    private final IDrawable icon;

    public JeiHandInteractionCategory(
            IGuiHelper guiHelper
    ) {
        background =
                guiHelper.createBlankDrawable(
                        WIDTH,
                        HEIGHT
                );

        plus =
                guiHelper.getRecipePlusSign();

        icon =
                new IDrawable() {

                    @Override
                    public int getWidth() {
                        return HAND_SIZE;
                    }

                    @Override
                    public int getHeight() {
                        return HAND_SIZE;
                    }

                    @Override
                    public void draw(
                            GuiGraphics graphics,
                            int xOffset,
                            int yOffset
                    ) {
                        graphics.blitSprite(
                                HAND_RIGHT,
                                xOffset,
                                yOffset,
                                HAND_SIZE,
                                HAND_SIZE
                        );
                    }
                };
    }

    @Override
    public RecipeType<JeiHandInteractionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                JolCraftLanguageKeys.JEI_CATEGORY_HAND_INTERACTION
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

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void draw(
            JeiHandInteractionRecipe entry,
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

        plus.draw(
                graphics,
                PLUS_X,
                PLUS_Y
        );

        graphics.blitSprite(
                HAND_LEFT,
                HAND_LEFT_X,
                HAND_Y,
                HAND_SIZE,
                HAND_SIZE
        );

        graphics.blitSprite(
                HAND_RIGHT,
                HAND_RIGHT_X,
                HAND_Y,
                HAND_SIZE,
                HAND_SIZE
        );

        graphics.blit(
                ARROW_TEXTURE,
                ARROW_X,
                ARROW_Y,
                0,
                0,
                ARROW_WIDTH,
                ARROW_HEIGHT,
                ARROW_WIDTH,
                ARROW_HEIGHT
        );

        graphics.blit(
                RIGHT_CLICK_TEXTURE,
                RIGHT_CLICK_X,
                RIGHT_CLICK_Y,
                0,
                0,
                RIGHT_CLICK_SIZE,
                RIGHT_CLICK_SIZE,
                RIGHT_CLICK_SIZE,
                RIGHT_CLICK_SIZE
        );

        if (
                entry.result()
                        instanceof JeiHandInteractionRecipe.EffectResult
                        effectResult
        ) {
            drawEffectResult(
                    graphics,
                    effectResult
            );
        }

        if (
                entry.result()
                        instanceof JeiHandInteractionRecipe.EntityResult
                        entityResult
        ) {
            drawEntityResult(
                    graphics,
                    entityResult
            );
        }

        Font font =
                Minecraft.getInstance().font;

        drawInputAmount(
                graphics,
                font,
                entry.recipe().actionA(),
                INPUT_A_X
        );

        drawInputAmount(
                graphics,
                font,
                entry.recipe().actionB(),
                INPUT_B_X
        );

        drawOutputAmount(
                graphics,
                font,
                entry
        );
    }

    private static void drawEffectResult(
            GuiGraphics graphics,
            JeiHandInteractionRecipe.EffectResult result
    ) {
        TextureAtlasSprite sprite =
                Minecraft.getInstance()
                        .getMobEffectTextures()
                        .get(
                                result.effect()
                                        .getEffect()
                        );

        graphics.blit(
                OUTPUT_X + 1,
                SLOT_Y + 1,
                0,
                16,
                16,
                sprite
        );
    }

    private static void drawEntityResult(
            GuiGraphics graphics,
            JeiHandInteractionRecipe.EntityResult result
    ) {
        LivingEntity entity =
                createDisplayEntity(
                        result
                );

        if (entity == null) {
            return;
        }

        Font font =
                Minecraft.getInstance().font;

        Component label =
                Component.literal(
                        "Spawns "
                ).append(
                        result.entityType()
                                .getDescription()
                );

        float labelScale =
                0.6F;

        float labelX =
                OUTPUT_X
                        + SLOT_SIZE / 2.0F
                        - font.width(
                        label
                )
                        * labelScale
                        / 2.0F;

        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        labelX,
                        0.0F,
                        0.0F
                );

        graphics.pose()
                .scale(
                        labelScale,
                        labelScale,
                        1.0F
                );

        graphics.drawString(
                font,
                label,
                0,
                0,
                TEXT_COLOR,
                false
        );

        graphics.pose()
                .popPose();

        Quaternionf pose =
                new Quaternionf()
                        .rotateZ(
                                (float) Math.PI
                        );

        Quaternionf camera =
                new Quaternionf()
                        .rotateX(
                                -10.0F
                                        * (float) (
                                        Math.PI / 180.0F
                                )
                        );

        float rotation =
                142.0F;

        entity.yBodyRot =
                rotation;

        entity.yBodyRotO =
                rotation;

        entity.setYRot(
                rotation
        );

        entity.yRotO =
                rotation;

        entity.yHeadRot =
                rotation;

        entity.yHeadRotO =
                rotation;

        entity.setXRot(
                -5.0F
        );

        entity.xRotO =
                -5.0F;

        float largestDimension =
                Math.max(
                        entity.getBbWidth(),
                        entity.getBbHeight()
                );

        float scale =
                22.0F
                        / Math.max(
                        largestDimension,
                        0.25F
                );

        Vector3f translate =
                new Vector3f(
                        0.0F,
                        entity.getBbHeight()
                                * 0.10F,
                        0.0F
                );

        InventoryScreen.renderEntityInInventory(
                graphics,
                OUTPUT_X + SLOT_SIZE / 2.0F,
                35.0F,
                scale,
                translate,
                pose,
                camera,
                entity
        );
    }

    private static @Nullable LivingEntity createDisplayEntity(
            JeiHandInteractionRecipe.EntityResult result
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return null;
        }

        Entity entity =
                result.entityType()
                        .create(
                                minecraft.level
                        );

        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }

        return livingEntity;
    }

    private static void drawInputAmount(
            GuiGraphics graphics,
            Font font,
            ItemInputAction action,
            int slotX
    ) {
        if (
                action.type()
                        == ItemInputAction.Type.CATALYST
        ) {
            return;
        }

        int amount =
                action.resolvedAmount();

        drawAmountOverlay(
                graphics,
                font,
                amount,
                amount,
                slotX,
                AMOUNT_Y
        );
    }

    private static void drawOutputAmount(
            GuiGraphics graphics,
            Font font,
            JeiHandInteractionRecipe entry
    ) {
        int min;
        int max;
        int y;

        if (
                entry.result()
                        instanceof JeiHandInteractionRecipe.ItemResult itemResult
        ) {
            JeiItemOutcome outcome = itemResult.outcome();

            min =
                    outcome.minCount();

            max =
                    outcome.maxCount();

            y =
                    AMOUNT_Y;
        } else if (
                entry.result()
                        instanceof JeiHandInteractionRecipe.EntityResult
                        entityResult
        ) {
            min =
                    entityResult.minCount();

            max =
                    entityResult.maxCount();

            y =
                    ENTITY_AMOUNT_Y;
        } else {
            return;
        }

        drawAmountOverlay(
                graphics,
                font,
                min,
                max,
                OUTPUT_X,
                y
        );
    }

    private static void drawAmountOverlay(
            GuiGraphics graphics,
            Font font,
            int min,
            int max,
            int slotX,
            int y
    ) {
        if (
                min == 1
                        && max == 1
        ) {
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
                text,
                slotX,
                y
        );
    }

    private static void drawCenteredScaledText(
            GuiGraphics graphics,
            Font font,
            String text,
            int slotX,
            int y
    ) {
        int stringWidth =
                font.width(
                        text
                );

        float centerX =
                slotX
                        + SLOT_SIZE / 2.0F
                        - stringWidth
                        * (float) 0.75
                        / 2.0F;

        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        centerX,
                        y,
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

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltipStrings(
            JeiHandInteractionRecipe entry,
            IRecipeSlotsView slots,
            double mouseX,
            double mouseY
    ) {
        if (
                !(
                        entry.result()
                                instanceof JeiHandInteractionRecipe.EffectResult(
                                MobEffectInstance effect
                        )
                )
                        || !isInsideEffectOutput(
                        mouseX,
                        mouseY
                )
        ) {
            return List.of();
        }

        List<Component> tooltip =
                new ArrayList<>();

        PotionContents.addPotionTooltip(
                List.of(
                        effect
                ),
                tooltip::add,
                1.0F,
                20.0F
        );

        return tooltip;
    }

    private static boolean isInsideEffectOutput(
            double mouseX,
            double mouseY
    ) {
        return mouseX >= OUTPUT_X
                && mouseX < OUTPUT_X + SLOT_SIZE
                && mouseY >= SLOT_Y
                && mouseY < SLOT_Y + SLOT_SIZE;
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiHandInteractionRecipe entry,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INPUT_A_X,
                        SLOT_Y
                )
                .addItemStacks(
                        entry.ingredientAExamples()
                );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INPUT_B_X,
                        SLOT_Y
                )
                .addItemStacks(
                        entry.ingredientBExamples()
                );

        if (
                entry.result()
                        instanceof JeiHandInteractionRecipe.ItemResult
                        itemResult
        ) {
            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            OUTPUT_X,
                            SLOT_Y
                    )
                    .addItemStacks(
                            List.of(itemResult.example())
                    );

            return;
        }

        if (
                entry.result()
                        instanceof JeiHandInteractionRecipe.EntityResult
                        entityResult
        ) {
            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            OUTPUT_X,
                            ENTITY_EGG_Y
                    )
                    .addItemStack(
                            entityResult.spawnEgg()
                    );
        }
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}