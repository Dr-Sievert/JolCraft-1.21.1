package net.sievert.jolcraft.integration.jei.util;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.integration.jei.util.gui.JeiPoint;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractJeiCategory<R>
        implements IRecipeCategory<R> {

    private final RecipeType<R> recipeType;
    private final Component title;
    private final int width;
    private final int height;
    private final IDrawable background;
    private final IDrawable recipePlus;
    private final IDrawable icon;

    protected AbstractJeiCategory(
            @NotNull IGuiHelper guiHelper,
            @NotNull RecipeType<R> recipeType,
            @NotNull Component title,
            int width,
            int height,
            @NotNull IDrawable icon
    ) {
        this(
                guiHelper,
                recipeType,
                title,
                width,
                height,
                width,
                height,
                icon
        );
    }

    protected AbstractJeiCategory(
            @NotNull IGuiHelper guiHelper,
            @NotNull RecipeType<R> recipeType,
            @NotNull Component title,
            int width,
            int height,
            int backgroundWidth,
            int backgroundHeight,
            @NotNull IDrawable icon
    ) {
        this.recipeType =
                recipeType;

        this.title =
                title;

        this.width =
                width;

        this.height =
                height;

        this.background =
                guiHelper.createBlankDrawable(
                        backgroundWidth,
                        backgroundHeight
                );

        this.recipePlus =
                guiHelper.getRecipePlusSign();

        this.icon =
                icon;
    }

    @Override
    public final @NotNull RecipeType<R> getRecipeType() {
        return recipeType;
    }

    @Override
    public final @NotNull Component getTitle() {
        return title;
    }

    @Override
    public final int getWidth() {
        return width;
    }

    @Override
    public final int getHeight() {
        return height;
    }

    @Override
    public final void draw(
            @NotNull R recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        background.draw(
                graphics,
                0,
                0
        );

        drawRecipe(
                recipe,
                slots,
                graphics,
                mouseX,
                mouseY
        );
    }

    protected abstract void drawRecipe(
            @NotNull R recipe,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    );

    protected final void drawJeiPlus(
            @NotNull GuiGraphics graphics,
            @NotNull JeiPoint point
    ) {
        recipePlus.draw(
                graphics,
                point.x(),
                point.y()
        );
    }

    @Override
    public final @NotNull IDrawable getIcon() {
        return icon;
    }
}
