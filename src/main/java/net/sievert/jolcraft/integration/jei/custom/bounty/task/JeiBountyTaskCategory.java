package net.sievert.jolcraft.integration.jei.custom.bounty.task;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.custom.bounty.AbstractJeiBountyCategory;
import net.sievert.jolcraft.integration.jei.custom.bounty.JeiBountyLayout;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.render.JeiEntityRenderer;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiLootConditionTooltip;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator.NumberRange;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiBountyTaskCategory
        extends AbstractJeiBountyCategory<JeiBountyTaskRecipe> {

    public static final RecipeType<JeiBountyTaskRecipe> RECIPE_TYPE =
            JeiRecipeTypes.BOUNTY_TASK;

    private static final int ITEM_AMOUNT_Y = 46;
    private static final int ENTITY_BOTTOM_Y = 46;
    private static final int ENTITY_AMOUNT_Y = 52;
    private static final int ENTITY_EGG_Y = 58;

    public JeiBountyTaskCategory(
            IGuiHelper guiHelper
    ) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_CATEGORY_BOUNTY_TASK
                ),
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftItems.BOUNTY.get()
                        )
                )
        );
    }

    @Override
    protected @NotNull DwarfProfession profession(
            @NotNull JeiBountyTaskRecipe entry
    ) {
        return entry.recipe()
                .bountyType();
    }

    @Override
    protected int levelId(
            @NotNull JeiBountyTaskRecipe entry
    ) {
        return entry.recipe()
                .tier()
                .getId();
    }

    @Override
    protected double chancePerRoll(
            @NotNull JeiBountyTaskRecipe entry
    ) {
        return entry.chancePerRoll();
    }

    @Override
    protected int minRolls(
            @NotNull JeiBountyTaskRecipe entry
    ) {
        return entry.minRolls();
    }

    @Override
    protected int maxRolls(
            @NotNull JeiBountyTaskRecipe entry
    ) {
        return entry.maxRolls();
    }

    @Override
    protected void drawBountyContent(
            @NotNull JeiBountyTaskRecipe entry,
            @NotNull GuiGraphics graphics,
            @NotNull Font font
    ) {
        switch (entry.objective()) {
            case JeiBountyTaskRecipe.ItemObjective itemObjective ->
                    drawItemObjective(
                            graphics,
                            font,
                            itemObjective.outcome()
                    );

            case JeiBountyTaskRecipe.EntityObjective entityObjective ->
                    drawEntityObjective(
                            graphics,
                            font,
                            entityObjective.output(),
                            entityObjective.amount()
                    );
        }
    }

    private static void drawItemObjective(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull JeiItemOutcome outcome
    ) {
        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_TOOLTIP_BOUNTY_COLLECT
                ),
                JeiBountyLayout.OUTPUT_CENTER_X,
                2
        );

        JeiDrawHelper.drawAmountRange(
                graphics,
                font,
                outcome.minCount(),
                outcome.maxCount(),
                JeiBountyLayout.OUTPUT.x(),
                ITEM_AMOUNT_Y
        );
    }

    private static void drawEntityObjective(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull EntityOutput entityOutput,
            @NotNull NumberRange amount
    ) {
        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                Component.translatable(
                        JolCraftLanguageKeys.JEI_TOOLTIP_BOUNTY_SLAY
                ),
                JeiBountyLayout.OUTPUT_CENTER_X,
                2
        );

        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                entityOutput.entity().getDescription(),
                JeiBountyLayout.OUTPUT_CENTER_X,
                12
        );

        LivingEntity entity =
                JeiEntityRenderer.createLiving(
                        entityOutput.entity()
                );

        if (entity != null) {
            JeiEntityRenderer.renderToBounds(
                    graphics,
                    entity,
                    JeiBountyLayout.OUTPUT_CENTER_X,
                    ENTITY_BOTTOM_Y,
                    22.0F,
                    200.0F,
                    -5.0F
            );
        }

        JeiDrawHelper.drawAmountRange(
                graphics,
                font,
                amount.min(),
                amount.max(),
                JeiBountyLayout.OUTPUT.x(),
                ENTITY_AMOUNT_Y
        );
    }

    @Override
    protected void addInputSlot(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiBountyTaskRecipe entry
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        JeiBountyLayout.INPUT.x(),
                        JeiBountyLayout.INPUT.y()
                )
                .addItemStack(
                        entry.bounty()
                );
    }

    @Override
    protected void addOutputSlots(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull JeiBountyTaskRecipe entry
    ) {
        switch (entry.objective()) {
            case JeiBountyTaskRecipe.ItemObjective itemObjective -> {
                var outputSlot =
                        builder.addSlot(
                                        RecipeIngredientRole.OUTPUT,
                                        JeiBountyLayout.OUTPUT.x(),
                                        JeiBountyLayout.OUTPUT.y()
                                )
                                .addItemStack(
                                        itemObjective.outcome()
                                                .stack()
                                );

                JeiLootConditionTooltip.add(
                        outputSlot,
                        itemObjective.outcome()
                );
            }

            case JeiBountyTaskRecipe.EntityObjective entityObjective -> {
                SpawnEggItem egg =
                        SpawnEggItem.byId(
                                entityObjective.output()
                                        .entity()
                        );

                if (egg == null) {
                    return;
                }

                builder.addSlot(
                                RecipeIngredientRole.OUTPUT,
                                JeiBountyLayout.OUTPUT.x(),
                                ENTITY_EGG_Y
                        )
                        .addItemStack(
                                new ItemStack(
                                        egg
                                )
                        );
            }
        }
    }
}
