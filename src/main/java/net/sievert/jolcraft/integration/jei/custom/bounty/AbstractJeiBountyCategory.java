package net.sievert.jolcraft.integration.jei.custom.bounty;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.gui.render.JeiDwarfRenderer;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractJeiBountyCategory<R>
        extends AbstractJeiCategory<R> {

    protected AbstractJeiBountyCategory(
            @NotNull IGuiHelper guiHelper,
            @NotNull RecipeType<R> recipeType,
            @NotNull Component title,
            @NotNull IDrawable icon
    ) {
        super(
                guiHelper,
                recipeType,
                title,
                JeiBountyLayout.WIDTH,
                JeiBountyLayout.HEIGHT,
                icon
        );
    }

    @Override
    protected final void drawRecipe(
            @NotNull R entry,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        drawJeiPlus(
                graphics,
                JeiBountyLayout.PLUS
        );

        JeiDrawHelper.drawArrow(
                graphics,
                JeiBountyLayout.ARROW
        );

        JeiDrawHelper.drawRightClick(
                graphics,
                JeiBountyLayout.RIGHT_CLICK
        );

        DwarfProfession profession =
                profession(
                        entry
                );

        JeiDwarfRenderer.drawBountyDwarf(
                graphics,
                profession,
                JeiBountyLayout.DWARF_CENTER_X,
                JeiBountyLayout.DWARF_BOTTOM_Y
        );

        Font font =
                Minecraft.getInstance().font;

        JeiDwarfRenderer.drawHeader(
                graphics,
                font,
                Component.translatable(
                        DwarfMerchantData.Level.langKeyFromId(
                                levelId(
                                        entry
                                )
                        )
                ),
                profession,
                JeiBountyLayout.DWARF_CENTER_X,
                0,
                10
        );

        drawBountyContent(
                entry,
                graphics,
                font
        );

        JeiDrawHelper.drawChance(
                graphics,
                font,
                chancePerRoll(
                        entry
                ),
                JeiBountyLayout.CHANCE.x(),
                JeiBountyLayout.CHANCE.y()
        );

        JeiDrawHelper.drawRolls(
                graphics,
                font,
                rolls(
                        entry
                ),
                JeiBountyLayout.ROLLS.x(),
                JeiBountyLayout.ROLLS.y()
        );
    }

    @Override
    public final void setRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull R entry,
            @NotNull IFocusGroup focuses
    ) {
        addInputSlot(
                builder,
                entry
        );

        builder.addSlot(
                        RecipeIngredientRole.CATALYST,
                        JeiBountyLayout.DWARF_EGG.x(),
                        JeiBountyLayout.DWARF_EGG.y()
                )
                .addItemStack(
                        JeiDwarfRenderer.spawnEgg(
                                profession(
                                        entry
                                )
                        )
                );

        addOutputSlots(
                builder,
                entry
        );
    }

    protected abstract @NotNull DwarfProfession profession(
            @NotNull R entry
    );

    protected abstract int levelId(
            @NotNull R entry
    );

    protected abstract double chancePerRoll(
            @NotNull R entry
    );

    protected abstract int rolls(
            @NotNull R entry
    );

    protected abstract void drawBountyContent(
            @NotNull R entry,
            @NotNull GuiGraphics graphics,
            @NotNull Font font
    );

    protected abstract void addInputSlot(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull R entry
    );

    protected abstract void addOutputSlots(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull R entry
    );
}
