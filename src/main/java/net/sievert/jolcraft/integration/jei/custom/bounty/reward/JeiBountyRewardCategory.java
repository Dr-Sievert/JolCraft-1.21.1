package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.custom.bounty.AbstractJeiBountyCategory;
import net.sievert.jolcraft.integration.jei.custom.bounty.JeiBountyLayout;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiBountyRewardCategory
        extends AbstractJeiBountyCategory<JeiBountyRewardRecipe> {

    public static final RecipeType<JeiBountyRewardRecipe> RECIPE_TYPE =
            JeiRecipeTypes.BOUNTY_REWARD;

    private static final int OUTPUT_AMOUNT_Y = 46;

    public JeiBountyRewardCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_BOUNTY_REWARD
                ),
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftItems.REWARD_CRATE.get()
                        )
                )
        );
    }

    @Override
    protected @NotNull DwarfProfession profession(
            @NotNull JeiBountyRewardRecipe entry
    ) {
        return entry.recipe()
                .bountyType();
    }

    @Override
    protected int levelId(
            @NotNull JeiBountyRewardRecipe entry
    ) {
        return entry.recipe()
                .tier()
                .getId();
    }

    @Override
    protected double chancePerRoll(
            @NotNull JeiBountyRewardRecipe entry
    ) {
        return entry.reward()
                .chancePerRoll();
    }

    @Override
    protected int rolls(
            @NotNull JeiBountyRewardRecipe entry
    ) {
        return entry.reward()
                .rolls();
    }

    @Override
    protected void drawBountyContent(
            @NotNull JeiBountyRewardRecipe entry,
            @NotNull GuiGraphics graphics,
            @NotNull Font font
    ) {
        JeiItemOutcome outcome =
                entry.reward();

        JeiDrawHelper.drawAmountRange(
                graphics,
                font,
                outcome.minCount(),
                outcome.maxCount(),
                JeiBountyLayout.OUTPUT.x(),
                OUTPUT_AMOUNT_Y
        );
    }

    @Override
    protected void addInputSlot(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiBountyRewardRecipe entry
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        JeiBountyLayout.INPUT.x(),
                        JeiBountyLayout.INPUT.y()
                )
                .addItemStacks(
                        entry.inputs()
                );
    }

    @Override
    protected void addOutputSlots(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiBountyRewardRecipe entry
    ) {
        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        JeiBountyLayout.OUTPUT.x(),
                        JeiBountyLayout.OUTPUT.y()
                )
                .addItemStacks(
                        List.of(
                                entry.reward()
                                        .stack()
                        )
                );
    }
}
