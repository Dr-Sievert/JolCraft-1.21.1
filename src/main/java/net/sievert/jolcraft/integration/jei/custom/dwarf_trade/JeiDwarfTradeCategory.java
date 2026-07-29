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
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTrade.AmountRange;
import net.sievert.jolcraft.integration.jei.util.*;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeLayout;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.integration.jei.util.gui.render.JeiDwarfRenderer;
import net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.CHANCE_TEXT_SCALE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiDwarfTradeCategory
        extends AbstractJeiCategory<JeiDwarfTrade> {

    private static final int WIDTH = 150;
    private static final int HEIGHT = 60;

    private static final int DWARF_CENTER_X = 105;
    private static final int DWARF_BOTTOM_Y = 55;
    private static final int AMOUNT_Y = 43;
    private static final int CHANCE_Y = 52;

    private static final JeiRecipeLayout SINGLE_INPUT_LAYOUT =
            JeiRecipeLayout.singleInputToOutput(
                    2,
                    45,
                    25,
                    25,
                    1
            );

    private static final JeiRecipeLayout TWO_INPUT_LAYOUT =
            JeiRecipeLayout.twoInputsToOutput(
                    2,
                    35,
                    75,
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

    public static @NotNull RecipeType<JeiDwarfTrade> recipeTypeFor(
            @NotNull DwarfProfession profession
    ) {
        return JeiRecipeTypes.dwarfTrade(
                profession
        );
    }

    @Override
    protected void drawRecipe(
            @NotNull JeiDwarfTrade entry,
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

        if (layout.inputB() != null) {
            JeiDrawHelper.drawPlus(
                    graphics,
                    layout.requirePlus()
            );
        }

        JeiDrawHelper.drawArrow(
                graphics,
                layout.arrow()
        );

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

        if (layout.inputB() != null
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

        if (!entry.outputGuaranteed()) {
            JeiDrawHelper.drawCenteredScaledText(
                    graphics,
                    font,
                    JeiDrawHelper.formatChance(
                            entry.outputChance()
                    ),
                    layout.output().x(),
                    JeiGuiConstants.SLOT_SIZE,
                    CHANCE_Y,
                    CHANCE_TEXT_SCALE
            );
        }
    }

    private static void drawHeader(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull JeiDwarfTrade entry
    ) {
        Component level =
                entry.level() != null
                        ? Component.translatable(
                        DwarfMerchantData.Level.langKeyFromId(
                                entry.level()
                                        .getId()
                        )
                )
                        : Component.literal(
                        JolCraftDictionary.GLOBAL
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
            JeiDwarfTrade entry,
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
                entry.inputAExample(),
                entry.costAItemIs(
                        JolCraftTags.Items.COINS
                )
        );

        @Nullable ItemStack inputB =
                entry.inputBExample();

        if (layout.inputB() != null
                && inputB != null
                && !inputB.isEmpty()) {
            addTradeInput(
                    builder,
                    layout.requireInputB().x(),
                    inputB,
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
            @NotNull ItemStack input,
            boolean acceptsCoinPouch
    ) {
        var slot =
                builder.addSlot(
                                RecipeIngredientRole.INPUT,
                                x,
                                SINGLE_INPUT_LAYOUT.inputA().y()
                        )
                        .addItemStack(
                                input
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
            @NotNull JeiDwarfTrade entry
    ) {
        @Nullable ItemStack inputB =
                entry.inputBExample();

        return inputB != null
                && !inputB.isEmpty()
                ? TWO_INPUT_LAYOUT
                : SINGLE_INPUT_LAYOUT;
    }
}
