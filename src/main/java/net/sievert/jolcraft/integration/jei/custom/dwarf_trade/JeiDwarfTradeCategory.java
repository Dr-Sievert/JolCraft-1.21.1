package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

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
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTradeRecipe.AmountRange;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.render.JeiDwarfRenderer;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeLayout;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;
import static net.sievert.jolcraft.integration.jei.util.gui.JeiTextures.ARROW_WIDTH;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiDwarfTradeCategory
        extends AbstractJeiCategory<JeiDwarfTradeRecipe> {

    private static final int WIDTH = 150;
    private static final int HEIGHT = 68;

    private static final int DWARF_CENTER_X = 107;
    private static final int DWARF_BOTTOM_Y = 55;
    private static final int AMOUNT_Y = 43;
    private static final int TRADE_CHANCE_Y = 56;
    private static final int CHANCE_Y = 52;
    private static final int ROLLS_Y = 60;

    private static final JeiRecipeLayout SINGLE_INPUT_LAYOUT =
            JeiRecipeLayout.singleInputToOutput(
                    2,
                    48,
                    25,
                    25,
                    1
            );

    private static final JeiRecipeLayout TWO_INPUT_LAYOUT =
            JeiRecipeLayout.twoInputsToOutput(
                    2,
                    35,
                    78,
                    25,
                    27,
                    25,
                    -1,
                    -1
            );

    public JeiDwarfTradeCategory(
            IGuiHelper guiHelper,
            DwarfProfession profession
    ) {
        super(
                guiHelper,
                recipeTypeFor(
                        profession
                ),
                Component.translatable(
                                JolCraftLanguageKeys.JEI_CATEGORY_DWARF_TRADES
                        )
                        .append(" — ")
                        .append(
                                profession.getDisplayName()
                        ),
                WIDTH,
                HEIGHT,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                DwarfProfessionHelper
                                        .getSpawnEgg(
                                                profession
                                        )
                                        .get()
                        )
                )
        );
    }

    public static @NotNull RecipeType<JeiDwarfTradeRecipe> recipeTypeFor(
            @NotNull DwarfProfession profession
    ) {
        return JeiRecipeTypes.dwarfTrade(
                profession
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiDwarfTradeRecipe entry,
            @NotNull IRecipeSlotsView slots,
            @NotNull GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        Font font =
                Minecraft.getInstance().font;

        drawHeader(
                graphics,
                font,
                entry
        );

        JeiRecipeLayout layout =
                layoutFor(
                        entry
                );

        if (entry.hasInputB()) {
            JeiDrawHelper.drawPlus(
                    graphics,
                    layout.requirePlus()
            );
        }

        JeiDrawHelper.drawArrow(
                graphics,
                layout.arrow()
        );

        if (!entry.tradeGuaranteed()) {
            boolean cumulative =
                    entry.recipe().tradeGroup()
                            == TradeGroup.CUMULATIVE_POOL;

            JeiDrawHelper.drawCenteredChance(
                    graphics,
                    font,
                    entry.tradeSelectionChance(),
                    cumulative
                            ? JolCraftLanguageKeys.JEI_TOOLTIP_CHANCE_TOTAL
                            : JolCraftLanguageKeys.JEI_TOOLTIP_CHANCE_ROLL,
                    SINGLE_INPUT_LAYOUT.arrow().x() + 10,
                    ARROW_WIDTH,
                    TRADE_CHANCE_Y
            );
        }

        JeiDwarfRenderer.drawTradeDwarf(
                graphics,
                entry.profession(),
                DWARF_CENTER_X,
                DWARF_BOTTOM_Y
        );

        drawAmountOverlay(
                graphics,
                font,
                entry.inputAmountA(),
                layout.inputA().x()
        );

        AmountRange inputAmountB =
                entry.inputAmountB();

        if (entry.hasInputB()
                && inputAmountB != null) {
            drawAmountOverlay(
                    graphics,
                    font,
                    inputAmountB,
                    layout.requireInputB().x()
            );
        }

        drawAmountOverlay(
                graphics,
                font,
                entry.outputAmount(),
                layout.output().x()
        );

        if (!entry.outputGuaranteedPerRoll()
                || entry.outputRolls() > 1) {
            JeiDrawHelper.drawCenteredChance(
                    graphics,
                    font,
                    entry.outputChancePerRoll(),
                    layout.output().x(),
                    SLOT_SIZE,
                    CHANCE_Y
            );
        }

        JeiDrawHelper.drawCenteredRolls(
                graphics,
                font,
                entry.outputRolls(),
                layout.output().x(),
                SLOT_SIZE,
                ROLLS_Y
        );
    }

    private static void drawHeader(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull JeiDwarfTradeRecipe entry
    ) {
        Component level =
                entry.level() != null
                        ? Component.translatable(
                                DwarfMerchantData.Level.langKeyFromId(
                                        entry.level().getId()
                                )
                        )
                        : Component.literal(
                                JolCraftStrings.toTitleCase(
                                        JolCraftDictionary.GLOBAL
                                )
                        );

        JeiDwarfRenderer.drawHeader(
                graphics,
                font,
                level,
                entry.profession(),
                DWARF_CENTER_X,
                0,
                10
        );
    }

    private static void drawAmountOverlay(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull AmountRange amount,
            int slotX
    ) {
        if (!amount.known()) {
            JeiDrawHelper.drawUnknownAmount(
                    graphics,
                    font,
                    slotX,
                    AMOUNT_Y
            );

            return;
        }

        JeiDrawHelper.drawAmountRange(
                graphics,
                font,
                amount.min(),
                amount.max(),
                slotX,
                AMOUNT_Y
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiDwarfTradeRecipe entry,
            IFocusGroup focuses
    ) {
        ItemStack egg =
                new ItemStack(
                        entry.spawnEgg()
                                .get()
                );

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        130,
                        42
                )
                .addItemStack(
                        egg
                );

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        130,
                        42
                )
                .addItemStack(
                        egg
                );

        JeiRecipeLayout layout =
                layoutFor(
                        entry
                );

        addTradeInput(
                builder,
                layout.inputA().x(),
                entry.inputAExamples(),
                entry.costAItemIs(
                        JolCraftTags.Items.COINS
                )
        );

        if (entry.hasInputB()) {
            addTradeInput(
                    builder,
                    layout.requireInputB().x(),
                    entry.inputBExamples(),
                    entry.costBItemIs(
                            JolCraftTags.Items.COINS
                    )
            );
        }

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        layout.output().x(),
                        layout.output().y()
                )
                .addItemStack(
                        entry.outputExample()
                );
    }

    private static void addTradeInput(
            @NotNull IRecipeLayoutBuilder builder,
            int x,
            @NotNull List<ItemStack> inputs,
            boolean acceptsCoinPouch
    ) {
        var slot =
                builder.addSlot(
                                RecipeIngredientRole.INPUT,
                                x,
                                SINGLE_INPUT_LAYOUT.inputA().y()
                        )
                        .addItemStacks(
                                inputs
                        );

        if (acceptsCoinPouch) {
            slot.addItemStack(
                    new ItemStack(
                            JolCraftItems.COIN_POUCH.get()
                    )
            );
        }
    }

    private static @NotNull JeiRecipeLayout layoutFor(
            @NotNull JeiDwarfTradeRecipe entry
    ) {
        return entry.hasInputB()
                ? TWO_INPUT_LAYOUT
                : SINGLE_INPUT_LAYOUT;
    }
}