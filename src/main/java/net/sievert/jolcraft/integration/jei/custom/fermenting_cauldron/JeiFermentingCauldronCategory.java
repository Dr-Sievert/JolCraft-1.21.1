package net.sievert.jolcraft.integration.jei.custom.fermenting_cauldron;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiFermentingCauldronCategory
        implements IRecipeCategory<JeiFermentingCauldronRecipe> {

    public static final RecipeType<JeiFermentingCauldronRecipe> RECIPE_TYPE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.FERMENTING_CAULDRON,
                    JeiFermentingCauldronRecipe.class
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

    private static final int WIDTH = 124;
    private static final int HEIGHT = 40;

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_Y = 11;

    private static final int PREVIOUS_INPUT_X = 4;
    private static final int PLUS_X = 27;
    private static final int INGREDIENT_X = 44;
    private static final int ARROW_X = 71;
    private static final int OUTPUT_X = 102;

    private static final int PLUS_Y = 13;

    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 16;
    private static final int ARROW_Y = 12;

    private static final int INGREDIENT_SIZE = 16;

    private final IDrawable background;
    private final IDrawable plus;
    private final IDrawable icon;

    public JeiFermentingCauldronCategory(
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
                guiHelper.createDrawableIngredient(
                        mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                Blocks.CAULDRON
                        )
                );
    }

    @Override
    public RecipeType<JeiFermentingCauldronRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                JolCraftLanguageKeys.JEI_CATEGORY_FERMENTING_CAULDRON
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
            JeiFermentingCauldronRecipe recipe,
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

        if (
                recipe.result()
                        instanceof JeiFermentingCauldronRecipe.EffectResult
                        effectResult
        ) {
            drawEffectResult(
                    graphics,
                    effectResult
            );
        }
    }

    private static void drawEffectResult(
            GuiGraphics graphics,
            JeiFermentingCauldronRecipe.EffectResult result
    ) {
        drawEffectFluid(
                graphics
        );

        TextureAtlasSprite effectSprite =
                Minecraft.getInstance()
                        .getMobEffectTextures()
                        .get(
                                result.effect()
                                        .getEffect()
                        );

        graphics.blit(
                OUTPUT_X,
                SLOT_Y,
                1,
                SLOT_SIZE,
                SLOT_SIZE,
                effectSprite
        );
    }

    @SuppressWarnings("deprecation")
    private static void drawEffectFluid(
            GuiGraphics graphics
    ) {
        FluidStack fluid =
                createUnfinishedBrew();

        IClientFluidTypeExtensions extensions =
                IClientFluidTypeExtensions.of(
                        fluid.getFluid()
                );

        ResourceLocation stillTexture =
                extensions.getStillTexture(
                        fluid
                );

        TextureAtlasSprite fluidSprite =
                Minecraft.getInstance()
                        .getTextureAtlas(
                                TextureAtlas.LOCATION_BLOCKS
                        )
                        .apply(
                                stillTexture
                        );

        int tint =
                extensions.getTintColor(
                        fluid
                );

        float alpha =
                ((tint >> 24) & 0xFF) / 255.0F;

        if (alpha == 0.0F) {
            alpha = 1.0F;
        }

        graphics.setColor(
                ((tint >> 16) & 0xFF) / 255.0F,
                ((tint >> 8) & 0xFF) / 255.0F,
                (tint & 0xFF) / 255.0F,
                alpha
        );

        graphics.blit(
                OUTPUT_X,
                SLOT_Y,
                0,
                SLOT_SIZE,
                SLOT_SIZE,
                fluidSprite
        );

        graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
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
        if (
                !(
                        recipe.result()
                                instanceof JeiFermentingCauldronRecipe.EffectResult(
                                MobEffectInstance effect
                        )
                )
                        || !isInsideOutput(
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

    private static boolean isInsideOutput(
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
            JeiFermentingCauldronRecipe recipe,
            IFocusGroup focuses
    ) {
        addPreviousInput(
                builder,
                recipe.previousInput()
        );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INGREDIENT_X,
                        SLOT_Y
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
            IRecipeLayoutBuilder builder,
            JeiFermentingCauldronRecipe.PreviousInput previousInput
    ) {
        if (
                previousInput
                        instanceof JeiFermentingCauldronRecipe.ItemInput(
                        List<ItemStack> examples
                )
        ) {
            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            PREVIOUS_INPUT_X,
                            SLOT_Y
                    )
                    .addItemStacks(
                            examples
                    );

            addFluidSlot(
                    builder,
                    RecipeIngredientRole.INPUT,
                    PREVIOUS_INPUT_X,
                    createUnfinishedBrew()
            );

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            PREVIOUS_INPUT_X,
                            SLOT_Y
                    )
                    .addItemStacks(
                            examples
                    );

            return;
        }

        if (
                previousInput
                        instanceof JeiFermentingCauldronRecipe.FluidInput(
                        FluidStack fluid
                )
        ) {
            addFluidSlot(
                    builder,
                    RecipeIngredientRole.INPUT,
                    PREVIOUS_INPUT_X,
                    fluid
            );
        }
    }

    private static void addResult(
            IRecipeLayoutBuilder builder,
            JeiFermentingCauldronRecipe.Result result
    ) {
        switch (result) {
            case JeiFermentingCauldronRecipe.ItemResult(
                    List<ItemStack> examples
            ) -> builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            OUTPUT_X,
                            SLOT_Y
                    )
                    .addItemStacks(
                            examples
                    );

            case JeiFermentingCauldronRecipe.FluidResult(
                    FluidStack fluid
            ) -> addFluidSlot(
                    builder,
                    RecipeIngredientRole.OUTPUT,
                    OUTPUT_X,
                    fluid
            );

            default -> {}
        }
    }

    private static FluidStack createUnfinishedBrew() {
        FluidStack fluid =
                new FluidStack(
                        JolCraftFluids
                                .UNFINISHED_DWARVEN_BREW
                                .get(),
                        FluidType.BUCKET_VOLUME
                );

        fluid.set(
                JolCraftDataComponents
                        .BREW_COLOR
                        .get(),
                BrewingColors.UNFINISHED_DWARVEN_BREW
        );

        return fluid;
    }

    private static void addFluidSlot(
            IRecipeLayoutBuilder builder,
            RecipeIngredientRole role,
            int x,
            FluidStack fluid
    ) {
        IRecipeSlotBuilder slot =
                builder.addSlot(
                        role,
                        x,
                        SLOT_Y
                );

        slot.addFluidStack(
                        fluid.getFluid(),
                        fluid.getAmount(),
                        fluid.getComponentsPatch()
                )
                .setFluidRenderer(
                        1,
                        false,
                        INGREDIENT_SIZE,
                        INGREDIENT_SIZE
                );
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}