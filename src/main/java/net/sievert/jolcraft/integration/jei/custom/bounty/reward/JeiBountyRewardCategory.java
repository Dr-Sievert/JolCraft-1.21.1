package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
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
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("SuspiciousNameCombination")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiBountyRewardCategory
        implements IRecipeCategory<JeiBountyRewardRecipe> {

    public static final RecipeType<JeiBountyRewardRecipe> RECIPE_TYPE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.BOUNTY_REWARD,
                    JeiBountyRewardRecipe.class
            );

    private static final ResourceLocation RIGHT_CLICK_TEXTURE =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/sprites/toast/right_click.png"
            );

    private static final ResourceLocation ARROW_TEXTURE =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jei(
                            JolCraftStrings.underscored(
                                    net.sievert.jolcraft.data.language.JolCraftDictionary.RECIPE,
                                    net.sievert.jolcraft.data.language.JolCraftDictionary.ARROW
                            )
                    )
            );

    private static final int WIDTH = 172;
    private static final int HEIGHT = 76;

    private static final int SLOT_SIZE = 18;
    private static final int GAP = 4;
    private static final int PLUS_WIDTH = 13;

    private static final int INPUT_X = 4;
    private static final int INPUT_Y = 26;

    private static final int PLUS_X =
            INPUT_X
                    + SLOT_SIZE
                    + GAP;

    private static final int PLUS_Y =
            INPUT_Y
                    + (
                    SLOT_SIZE - PLUS_WIDTH
            ) / 2;

    private static final int DWARF_CENTER_X = 64;
    private static final int DWARF_BOTTOM_Y = 46;

    private static final int DWARF_EGG_X =
            DWARF_CENTER_X
                    - SLOT_SIZE / 2;

    private static final int DWARF_EGG_Y = 54;

    private static final int ARROW_X = 91;
    private static final int ARROW_Y = 27;
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 16;

    private static final int CHANCE_X =
            ARROW_X
                    + ARROW_WIDTH
                    + 5;

    private static final int CHANCE_Y = 33;

    private static final int RIGHT_CLICK_SIZE = 20;

    private static final int RIGHT_CLICK_X =
            ARROW_X
                    + (
                    ARROW_WIDTH - RIGHT_CLICK_SIZE
            ) / 2;

    private static final int RIGHT_CLICK_Y = 51;

    private static final int OUTPUT_X = 142;
    private static final int OUTPUT_Y = 26;
    private static final int OUTPUT_AMOUNT_Y = 46;

    private static final int TEXT_COLOR = 0x888888;

    private final IDrawable background;
    private final IDrawable plus;
    private final IDrawable icon;

    public JeiBountyRewardCategory(
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
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftItems.REWARD_CRATE.get()
                        )
                );
    }

    @Override
    public RecipeType<JeiBountyRewardRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                JolCraftLanguageKeys.JEI_CATEGORY_BOUNTY_REWARD
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
            JeiBountyRewardRecipe entry,
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

        DwarfProfession profession =
                entry.recipe()
                        .bountyType();

        drawDwarf(
                graphics,
                profession
        );

        Font font =
                Minecraft.getInstance()
                        .font;

        drawDwarfName(
                graphics,
                font,
                profession,
                entry.recipe()
                        .tier()
                        .getId()
        );

        findDisplayedReward(
                entry,
                slots
        ).ifPresent(
                outcome -> {
                    drawAmountRange(
                            graphics,
                            font,
                            outcome.minCount(),
                            outcome.maxCount()
                    );

                    drawChance(
                            graphics,
                            font,
                            outcome
                    );
                }
        );
    }

    private static void drawDwarfName(
            GuiGraphics graphics,
            Font font,
            DwarfProfession profession,
            int levelId
    ) {
        Component levelComponent =
                Component.translatable(
                        DwarfMerchantData.Level.langKeyFromId(
                                levelId
                        )
                );

        String levelText =
                levelComponent.getString();

        int levelX =
                DWARF_CENTER_X
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

        String professionText =
                profession.getDisplayName()
                        .getString();

        int professionX =
                DWARF_CENTER_X
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

    private static void drawAmountRange(
            GuiGraphics graphics,
            Font font,
            int min,
            int max
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
                text
        );
    }

    private static void drawChance(
            GuiGraphics graphics,
            Font font,
            JeiItemOutcome outcome
    ) {
        String chance =
                formatChance(
                        outcome.chancePerRoll()
                );

        if (outcome.rolls() > 1) {
            chance +=
                    " ×"
                            + outcome.rolls();
        }

        drawScaledText(
                graphics,
                font,
                chance
        );
    }

    private static Optional<JeiItemOutcome> findDisplayedReward(
            JeiBountyRewardRecipe entry,
            IRecipeSlotsView slots
    ) {
        List<IRecipeSlotView> outputSlots =
                slots.getSlotViews(
                        RecipeIngredientRole.OUTPUT
                );

        if (outputSlots.isEmpty()) {
            return Optional.empty();
        }

        Optional<ItemStack> displayedStack =
                outputSlots.getFirst()
                        .getDisplayedIngredient(
                                VanillaTypes.ITEM_STACK
                        );

        if (displayedStack.isEmpty()) {
            return Optional.empty();
        }

        ItemStack displayed =
                displayedStack.get();

        return entry.rewards()
                .stream()
                .filter(
                        outcome ->
                                ItemStack.isSameItemSameComponents(
                                        outcome.stack(),
                                        displayed
                                )
                )
                .findFirst();
    }

    private static void drawCenteredScaledText(
            GuiGraphics graphics,
            Font font,
            String text
    ) {
        int stringWidth =
                font.width(
                        text
                );

        float centerX =
                JeiBountyRewardCategory.OUTPUT_X
                        + SLOT_SIZE / 2.0F
                        - stringWidth
                        * (float) 0.75
                        / 2.0F;

        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        centerX,
                        JeiBountyRewardCategory.OUTPUT_AMOUNT_Y,
                        0.0F
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

    private static void drawScaledText(
            GuiGraphics graphics,
            Font font,
            String text
    ) {
        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        (float) JeiBountyRewardCategory.CHANCE_X,
                        (float) JeiBountyRewardCategory.CHANCE_Y,
                        0.0F
                );

        graphics.pose()
                .scale(
                        (float) 0.65,
                        (float) 0.65,
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

        renderEntity(
                graphics,
                dwarf
        );
    }

    private static void renderEntity(
            GuiGraphics graphics,
            LivingEntity entity
    ) {
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
                200.0F;

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
                (float) JeiBountyRewardCategory.DWARF_CENTER_X,
                (float) JeiBountyRewardCategory.DWARF_BOTTOM_Y,
                scale,
                translate,
                pose,
                camera,
                entity
        );
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
                        DwarfProfessionHelper.getEntityType(
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
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiBountyRewardRecipe entry,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INPUT_X,
                        INPUT_Y
                )
                .addItemStacks(
                        entry.inputs()
                );

        builder.addSlot(
                        RecipeIngredientRole.CATALYST,
                        DWARF_EGG_X,
                        DWARF_EGG_Y
                )
                .addItemStack(
                        new ItemStack(
                                DwarfProfessionHelper
                                        .getSpawnEgg(
                                                entry.recipe()
                                                        .bountyType()
                                        )
                                        .get()
                        )
                );

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        OUTPUT_X,
                        OUTPUT_Y
                )
                .addItemStacks(
                        entry.rewards()
                                .stream()
                                .map(
                                        JeiItemOutcome::stack
                                )
                                .toList()
                );
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}