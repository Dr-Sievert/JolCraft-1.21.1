package net.sievert.jolcraft.integration.jei.custom.bounty.task;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.mixin.UniformGeneratorAccessor;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("SuspiciousNameCombination")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiBountyTaskCategory
        implements IRecipeCategory<JeiBountyTaskRecipe> {

    public static final RecipeType<JeiBountyTaskRecipe> RECIPE_TYPE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.BOUNTY_TASK,
                    JeiBountyTaskRecipe.class
            );

    private static final ResourceLocation RIGHT_CLICK_TEXTURE =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/sprites/toast/right_click.png"
            );

    private static final ResourceLocation ARROW_TEXTURE =
            net.sievert.jolcraft.util.client.JolCraftTextures.jeiRl(
                    net.sievert.jolcraft.util.client.JolCraftTextures.jei(
                            net.sievert.jolcraft.util.JolCraftStrings.underscored(
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
    private static final int OUTPUT_ENTITY_BOTTOM_Y = 46;
    private static final int OUTPUT_ENTITY_AMOUNT_Y = 52;
    private static final int OUTPUT_EGG_Y = 58;

    private static final int TEXT_COLOR = 0x888888;

    private final IDrawable background;
    private final IDrawable plus;
    private final IDrawable icon;

    public JeiBountyTaskCategory(
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
                                net.sievert.jolcraft.world.item.JolCraftItems
                                        .BOUNTY
                                        .get()
                        )
                );
    }

    @Override
    public RecipeType<JeiBountyTaskRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                JolCraftLanguageKeys.JEI_CATEGORY_BOUNTY_TASK
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
            JeiBountyTaskRecipe entry,
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

        drawDwarf(
                graphics,
                entry.recipe()
                        .bountyType()
        );

        Font font =
                Minecraft.getInstance()
                        .font;

        drawDwarfName(
                graphics,
                font,
                entry.recipe()
                        .bountyType()
        );

        drawObjective(
                graphics,
                font,
                entry
        );

        drawChance(
                graphics,
                font,
                entry
        );
    }

    private static void drawDwarfName(
            GuiGraphics graphics,
            Font font,
            DwarfProfession profession
    ) {
        String text =
                profession.getDisplayName()
                        .getString();

        int x =
                DWARF_CENTER_X
                        - font.width(
                        text
                ) / 2;

        graphics.drawString(
                font,
                text,
                x,
                2,
                TEXT_COLOR,
                false
        );
    }

    private static void drawObjective(
            GuiGraphics graphics,
            Font font,
            JeiBountyTaskRecipe entry
    ) {
        if (
                entry.objective()
                        instanceof ItemOutput itemOutput
        ) {
            List<JeiItemOutcome> outcomes =
                    ItemOutputJeiTranslator.translate(
                            itemOutput
                    );

            if (outcomes.isEmpty()) {
                return;
            }

            JeiItemOutcome outcome =
                    outcomes.getFirst();

            drawCenteredText(
                    graphics,
                    font,
                    "Collect"
            );

            drawAmountRange(
                    graphics,
                    font,
                    outcome.minCount(),
                    outcome.maxCount(),
                    46
            );

            return;
        }

        if (
                entry.objective()
                        instanceof EntityOutput entityOutput
        ) {
            EntityType<?> entityType =
                    entityOutput.entity();

            String title = "Slay";

            drawCenteredText(
                    graphics,
                    font,
                    title
            );

            drawEntity(
                    graphics,
                    entityType
            );

            int[] amount =
                    resolveRange(
                            entityOutput.count()
                    );

            drawAmountRange(
                    graphics,
                    font,
                    amount[0],
                    amount[1],
                    OUTPUT_ENTITY_AMOUNT_Y
            );
        }
    }

    private static void drawChance(
            GuiGraphics graphics,
            Font font,
            JeiBountyTaskRecipe entry
    ) {
        drawScaledText(
                graphics,
                font,
                formatChance(
                        entry.chance()
                ),
                CHANCE_X,
                CHANCE_Y,
                0.65F
        );
    }

    private static void drawAmountRange(
            GuiGraphics graphics,
            Font font,
            int min,
            int max,
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
                JeiBountyTaskCategory.OUTPUT_X,
                y,
                0.75F
        );
    }

    private static void drawCenteredText(
            GuiGraphics graphics,
            Font font,
            String text
    ) {
        int center =
                JeiBountyTaskCategory.OUTPUT_X
                        + SLOT_SIZE / 2;

        graphics.drawString(
                font,
                text,
                center
                        - font.width(
                        text
                ) / 2,
                2,
                TEXT_COLOR,
                false
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
                        0.0F
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

    private static void drawScaledText(
            GuiGraphics graphics,
            Font font,
            String text,
            float x,
            float y,
            float scale
    ) {
        graphics.pose()
                .pushPose();

        graphics.pose()
                .translate(
                        x,
                        y,
                        0.0F
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
                dwarf,
                DWARF_CENTER_X,
                DWARF_BOTTOM_Y
        );
    }

    private static void drawEntity(
            GuiGraphics graphics,
            EntityType<?> entityType
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        Entity entity =
                entityType.create(
                        minecraft.level
                );

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        renderEntity(
                graphics,
                livingEntity,
                (float) 151.0,
                (float) JeiBountyTaskCategory.OUTPUT_ENTITY_BOTTOM_Y
        );
    }

    private static void renderEntity(
            GuiGraphics graphics,
            LivingEntity entity,
            float centerX,
            float bottomY
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
                (float) 22.0
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
                centerX,
                bottomY,
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

    private static int[] resolveRange(
            NumberProvider provider
    ) {
        if (
                provider
                        instanceof ConstantValue
        ) {
            int value =
                    readConstantInt(
                            provider,
                            "bounty entity count"
                    );

            return new int[]{
                    value,
                    value
            };
        }

        if (
                provider
                        instanceof UniformGenerator uniform
        ) {
            UniformGeneratorAccessor accessor =
                    (UniformGeneratorAccessor) (Object) uniform;

            int min =
                    readConstantInt(
                            accessor.jolcraft$getMin(),
                            "bounty entity count minimum"
                    );

            int max =
                    readConstantInt(
                            accessor.jolcraft$getMax(),
                            "bounty entity count maximum"
                    );

            return new int[]{
                    min,
                    max
            };
        }

        throw new IllegalArgumentException(
                "Unsupported bounty entity count provider for JEI: "
                        + provider.getClass()
                        .getName()
        );
    }

    private static int readConstantInt(
            NumberProvider provider,
            String description
    ) {
        if (
                !(
                        provider
                                instanceof ConstantValue(
                                float value
                        )
                )
        ) {
            throw new IllegalArgumentException(
                    "JEI translation requires constant "
                            + description
                            + ", found "
                            + provider.getClass()
                            .getName()
            );
        }

        if (
                value
                        != Math.floor(
                        value
                )
        ) {
            throw new IllegalArgumentException(
                    "Expected an integer "
                            + description
                            + ", found "
                            + value
            );
        }

        return (int) value;
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiBountyTaskRecipe entry,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        INPUT_X,
                        INPUT_Y
                )
                .addItemStack(
                        entry.bounty()
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

        if (
                entry.objective()
                        instanceof ItemOutput itemOutput
        ) {
            List<JeiItemOutcome> outcomes =
                    ItemOutputJeiTranslator.translate(
                            itemOutput
                    );

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            OUTPUT_X,
                            OUTPUT_Y
                    )
                    .addItemStacks(
                            outcomes.stream()
                                    .map(
                                            JeiItemOutcome::stack
                                    )
                                    .toList()
                    );

            return;
        }

        if (
                entry.objective()
                        instanceof EntityOutput entityOutput
        ) {
            SpawnEggItem egg =
                    SpawnEggItem.byId(
                            entityOutput.entity()
                    );

            if (egg == null) {
                return;
            }

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            OUTPUT_X,
                            OUTPUT_EGG_Y
                    )
                    .addItemStack(
                            new ItemStack(
                                    egg
                            )
                    );
        }
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}