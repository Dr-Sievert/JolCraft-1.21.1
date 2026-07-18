package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTrade.AmountRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiDwarfTradeCategory
        implements IRecipeCategory<JeiDwarfTrade> {

    private static final Map<
            DwarfProfession,
            RecipeType<JeiDwarfTrade>
            > TYPES =
            new EnumMap<>(
                    DwarfProfession.class
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

    private static final ResourceLocation PLUS_TEXTURE =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jei(
                            JolCraftStrings.underscored(
                                    JolCraftDictionary.RECIPE,
                                    JolCraftDictionary.PLUS,
                                    JolCraftDictionary.SIGN
                            )
                    )
            );

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_Y = 25;
    private static final int SLOT_A_X = 2;
    private static final int SLOT_B_X = 28;
    private static final int HAS_B_SHIFT = 7;
    private static final int PLUS_W = 13;
    private static final int PLUS_H = 13;
    private static final int ARROW_W = 22;
    private static final int ARROW_H = 16;

    private static final int TEXT_COLOR = 0x888888;

    private final IDrawable background;
    private final IDrawable icon;
    private final DwarfProfession profession;

    public JeiDwarfTradeCategory(
            IGuiHelper guiHelper,
            DwarfProfession profession
    ) {
        this.profession =
                profession;

        this.background =
                guiHelper.createBlankDrawable(
                        150,
                        60
                );

        this.icon =
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                DwarfProfessionHelper
                                        .getSpawnEgg(
                                                profession
                                        )
                                        .get()
                        )
                );
    }

    public static RecipeType<JeiDwarfTrade> recipeTypeFor(
            DwarfProfession profession
    ) {
        return TYPES.computeIfAbsent(
                profession,
                currentProfession ->
                        RecipeType.create(
                                JolCraft.MOD_ID,
                                JolCraftStrings.underscored(
                                        JolCraftJeiIds.DWARF_TRADE,
                                        currentProfession.getId()
                                ),
                                JeiDwarfTrade.class
                        )
        );
    }

    @Override
    public RecipeType<JeiDwarfTrade> getRecipeType() {
        return recipeTypeFor(
                profession
        );
    }

    @Override
    public Component getTitle() {
        return Component
                .translatable(
                        JolCraftLanguageKeys
                                .JEI_CATEGORY_DWARF_TRADES
                )
                .append(" — ")
                .append(
                        profession.getDisplayName()
                );
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public void draw(
            JeiDwarfTrade entry,
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

        Font font =
                Minecraft.getInstance().font;

        drawHeader(
                graphics,
                font,
                entry
        );

        @Nullable ItemStack inputB =
                entry.inputBExample();

        boolean hasB =
                inputB != null
                        && !inputB.isEmpty();

        int shift =
                hasB
                        ? HAS_B_SHIFT
                        : 0;

        int slotAX =
                SLOT_A_X;

        int slotBX =
                SLOT_B_X
                        + shift;

        int outputX =
                hasB
                        ? 68 + shift
                        : 45;

        if (hasB) {
            int aRight =
                    slotAX
                            + SLOT_SIZE;

            int gap =
                    slotBX
                            - aRight;

            int plusX =
                    aRight
                            + (
                            gap - PLUS_W
                    ) / 2
                            - 1;

            graphics.blit(
                    PLUS_TEXTURE,
                    plusX,
                    27,
                    0,
                    0,
                    PLUS_W,
                    PLUS_H,
                    PLUS_W,
                    PLUS_H
            );
        }

        int arrowX =
                hasB
                        ? slotBX + SLOT_SIZE - 1
                        : slotAX + SLOT_SIZE + 1;

        graphics.blit(
                ARROW_TEXTURE,
                arrowX,
                25,
                0,
                0,
                ARROW_W,
                ARROW_H,
                ARROW_W,
                ARROW_H
        );

        drawDwarf(
                graphics,
                entry.profession()
        );

        drawAmountOverlay(
                graphics,
                font,
                entry.inputAmountA(),
                slotAX
        );

        AmountRange inputAmountB =
                entry.inputAmountB();

        if (hasB
                && inputAmountB != null) {
            drawAmountOverlay(
                    graphics,
                    font,
                    inputAmountB,
                    slotBX
            );
        }

        drawAmountOverlay(
                graphics,
                font,
                entry.outputAmount(),
                outputX
        );

        if (!entry.outputGuaranteed()) {
            drawChanceOverlay(
                    graphics,
                    font,
                    entry.outputChance(),
                    outputX
            );
        }
    }

    private static void drawHeader(
            GuiGraphics graphics,
            Font font,
            JeiDwarfTrade entry
    ) {
        Component levelComponent;

        if (entry.level() != null) {
            levelComponent =
                    Component.translatable(
                            DwarfMerchantData.Level
                                    .langKeyFromId(
                                            entry.level()
                                                    .getId()
                                    )
                    );
        } else {
            levelComponent =
                    Component.literal(
                            JolCraftDictionary.GLOBAL
                    );
        }

        Component professionComponent =
                entry.profession()
                        .getDisplayName();

        String levelText =
                levelComponent.getString();

        String professionText =
                professionComponent.getString();

        int levelX =
                105
                        - font.width(
                        levelText
                ) / 2;

        graphics.drawString(
                font,
                levelText,
                levelX,
                0,
                TEXT_COLOR,
                false
        );

        int professionX =
                105
                        - font.width(
                        professionText
                ) / 2;

        graphics.drawString(
                font,
                professionText,
                professionX,
                10,
                TEXT_COLOR,
                false
        );
    }

    private static void drawDwarf(
            GuiGraphics graphics,
            DwarfProfession profession
    ) {
        LivingEntity dwarf =
                createDisplayDwarf(
                        profession
                );

        if (dwarf == null) {
            return;
        }

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

        dwarf.yBodyRot =
                200.0F;

        dwarf.setYRot(
                200.0F
        );

        dwarf.setXRot(
                -5.0F
        );

        dwarf.yHeadRot =
                170.0F;

        dwarf.yHeadRotO =
                dwarf.yHeadRot;

        float scale =
                22.0F
                        / dwarf.getScale();

        Vector3f translate =
                new Vector3f(
                        0.0F,
                        dwarf.getBbHeight()
                                * 0.10F,
                        0.0F
                );

        InventoryScreen.renderEntityInInventory(
                graphics,
                105.0F,
                55.0F,
                scale,
                translate,
                pose,
                camera,
                dwarf
        );
    }

    private static void drawAmountOverlay(
            GuiGraphics graphics,
            Font font,
            AmountRange amount,
            int slotX
    ) {
        if (amount.min() == 1
                && amount.max() == 1) {
            return;
        }

        String text =
                amount.fixed()
                        ? String.valueOf(
                        amount.min()
                )
                        : amount.min()
                        + "-"
                        + amount.max();

        drawCenteredScaledText(
                graphics,
                font,
                text,
                slotX,
                43,
                0.75F
        );
    }

    private static void drawChanceOverlay(
            GuiGraphics graphics,
            Font font,
            double chance,
            int slotX
    ) {
        String text =
                formatChance(
                        chance
                );

        drawCenteredScaledText(
                graphics,
                font,
                text,
                slotX,
                52,
                0.65F
        );
    }

    private static void drawCenteredScaledText(
            GuiGraphics graphics,
            Font font,
            String text,
            int slotX,
            int y,
            float scale
    ) {
        int stringWidth =
                font.width(
                        text
                );

        float centerX =
                slotX
                        + SLOT_SIZE / 2.0F
                        - stringWidth
                        * scale
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
                        scale,
                        scale,
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

    private static String formatChance(
            double chance
    ) {
        double percentage =
                Math.clamp(
                        chance,
                        0.0D,
                        1.0D
                )
                        * 100.0D;

        if (percentage >= 10.0D) {
            return String.format(
                    Locale.ROOT,
                    "%.0f%%",
                    percentage
            );
        }

        if (percentage >= 1.0D) {
            return String.format(
                    Locale.ROOT,
                    "%.1f%%",
                    percentage
            );
        }

        return String.format(
                Locale.ROOT,
                "%.2f%%",
                percentage
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

        @Nullable ItemStack inputB =
                entry.inputBExample();

        boolean hasB =
                inputB != null
                        && !inputB.isEmpty();

        int shift =
                hasB
                        ? HAS_B_SHIFT
                        : 0;

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

        ItemStack inputA =
                entry.inputAExample();

        ItemStack output =
                entry.outputExample();

        if (entry.costAItemIs(
                JolCraftTags.Items.COINS
        )) {
            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            SLOT_A_X,
                            SLOT_Y
                    )
                    .addItemStack(
                            inputA
                    )
                    .addItemStack(
                            new ItemStack(
                                    JolCraftItems
                                            .COIN_POUCH
                                            .get()
                            )
                    );
        } else {
            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            SLOT_A_X,
                            SLOT_Y
                    )
                    .addItemStack(
                            inputA
                    );
        }

        if (hasB) {
            int slotBX =
                    SLOT_B_X
                            + shift;

            if (entry.costBItemIs(
                    JolCraftTags.Items.COINS
            )) {
                builder.addSlot(
                                RecipeIngredientRole.INPUT,
                                slotBX,
                                SLOT_Y
                        )
                        .addItemStack(
                                inputB
                        )
                        .addItemStack(
                                new ItemStack(
                                        JolCraftItems
                                                .COIN_POUCH
                                                .get()
                                )
                        );
            } else {
                builder.addSlot(
                                RecipeIngredientRole.INPUT,
                                slotBX,
                                SLOT_Y
                        )
                        .addItemStack(
                                inputB
                        );
            }

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            68 + shift,
                            SLOT_Y
                    )
                    .addItemStack(
                            output
                    );
        } else {
            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            45,
                            SLOT_Y
                    )
                    .addItemStack(
                            output
                    );
        }
    }

    private static @Nullable LivingEntity createDisplayDwarf(
            DwarfProfession profession
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return null;
        }

        DwarfEntity dwarf =
                new DwarfEntity(
                        DwarfProfessionHelper
                                .getEntityType(
                                        profession
                                ),
                        minecraft.level
                );

        dwarf.getEntityData()
                .set(
                        AbstractDwarfEntity.PROFESSION,
                        profession.getId()
                );

        return dwarf;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}