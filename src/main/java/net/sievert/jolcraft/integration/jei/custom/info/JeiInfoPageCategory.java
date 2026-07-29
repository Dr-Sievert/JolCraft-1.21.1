package net.sievert.jolcraft.integration.jei.custom.info;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiInfoPageCategory
        implements IRecipeCategory<JeiInfoPageRecipe> {

    public static final RecipeType<JeiInfoPageRecipe> RECIPE_TYPE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.INFO_PAGE,
                    JeiInfoPageRecipe.class
            );

    private static final int TEXT_START_Y = 32;
    private static final int LINE_HEIGHT = 10;

    private int scrollOffset = 0;
    private boolean draggingScrollThumb = false;
    private int dragStartMouseY = 0;
    private int dragStartScrollOffset = 0;

    private final IDrawable background;
    private final IDrawable icon;

    public JeiInfoPageCategory(
            IGuiHelper guiHelper
    ) {
        this.background =
                guiHelper.createBlankDrawable(
                        150,
                        100
                );

        this.icon =
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(
                                JolCraftItems.DWARVEN_TOME.get()
                        )
                );
    }

    @Override
    public RecipeType<JeiInfoPageRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                JolCraftLanguageKeys.JEI_CATEGORY_INFO_PAGE
        );
    }

    @Override
    public int getWidth() {
        return 200;
    }

    @Override
    public int getHeight() {
        return 150;
    }

    private int getTextHeight() {
        return getHeight()
                - TEXT_START_Y
                - 8;
    }

    @Override
    public void draw(
            JeiInfoPageRecipe recipe,
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

        int textHeight =
                getTextHeight();

        int maxLines =
                Math.max(
                        1,
                        textHeight / LINE_HEIGHT
                );

        List<FormattedCharSequence> lines =
                Minecraft.getInstance()
                        .font
                        .split(
                                recipe.getContent(),
                                getWidth() - 16
                        );

        int totalLines =
                lines.size();

        int maxScroll =
                Math.max(
                        0,
                        totalLines - maxLines
                );

        scrollOffset =
                Math.max(
                        0,
                        Math.min(
                                scrollOffset,
                                maxScroll
                        )
                );

        int visibleLines =
                Math.min(
                        maxLines,
                        totalLines - scrollOffset
                );

        for (int i = 0; i < visibleLines; i++) {
            graphics.drawString(
                    Minecraft.getInstance().font,
                    lines.get(i + scrollOffset),
                    8,
                    TEXT_START_Y + i * LINE_HEIGHT,
                    0x444444,
                    false
            );
        }

        if (totalLines <= maxLines) {
            return;
        }

        int barX =
                getWidth() - 8;

        int barY =
                TEXT_START_Y;

        int barWidth =
                4;

        graphics.fill(
                barX,
                barY,
                barX + barWidth,
                barY + textHeight,
                0x66BBBBBB
        );

        float ratio =
                maxLines / (float) totalLines;

        int thumbHeight =
                Math.max(
                        12,
                        Math.round(
                                textHeight * ratio
                        )
                );

        int maxThumbMove =
                textHeight - thumbHeight;

        int thumbY =
                barY
                        + (
                        maxScroll == 0
                                ? 0
                                : Math.round(
                                maxThumbMove
                                        * (
                                        scrollOffset
                                                / (float) maxScroll
                                )
                        )
                );

        graphics.fill(
                barX,
                thumbY,
                barX + barWidth,
                thumbY + thumbHeight,
                0xFF888888
        );

        graphics.fill(
                barX + 1,
                thumbY + 1,
                barX + barWidth - 1,
                thumbY + thumbHeight - 1,
                0xFF666666
        );
    }

    @Override
    public void createRecipeExtras(
            IRecipeExtrasBuilder builder,
            JeiInfoPageRecipe recipe,
            IFocusGroup focuses
    ) {
        builder.addInputHandler(
                new IJeiInputHandler() {

                    @Override
                    public ScreenRectangle getArea() {
                        return new ScreenRectangle(
                                0,
                                0,
                                getWidth(),
                                getHeight()
                        );
                    }

                    @Override
                    public boolean handleMouseScrolled(
                            double mouseX,
                            double mouseY,
                            double scrollDeltaX,
                            double scrollDeltaY
                    ) {
                        int sign =
                                (int) Math.signum(
                                        scrollDeltaY
                                );

                        int maxLines =
                                getTextHeight()
                                        / LINE_HEIGHT;

                        int lines =
                                Minecraft.getInstance()
                                        .font
                                        .split(
                                                recipe.getContent(),
                                                getWidth() - 16
                                        )
                                        .size();

                        int maxScroll =
                                Math.max(
                                        0,
                                        lines - maxLines
                                );

                        scrollOffset =
                                Math.max(
                                        0,
                                        Math.min(
                                                scrollOffset - sign,
                                                maxScroll
                                        )
                                );

                        return maxScroll > 0;
                    }
                }
        );

        builder.addInputHandler(
                new IJeiInputHandler() {

                    @Override
                    public ScreenRectangle getArea() {
                        return new ScreenRectangle(
                                getWidth() - 8,
                                TEXT_START_Y,
                                4,
                                getTextHeight()
                        );
                    }

                    @Override
                    public boolean handleInput(
                            double mouseX,
                            double mouseY,
                            IJeiUserInput input
                    ) {
                        if (!input.getKey().equals(
                                InputConstants.Type.MOUSE
                                        .getOrCreate(0)
                        )) {
                            return false;
                        }

                        int textHeight =
                                getTextHeight();

                        int maxLines =
                                Math.max(
                                        1,
                                        textHeight / LINE_HEIGHT
                                );

                        int totalLines =
                                Minecraft.getInstance()
                                        .font
                                        .split(
                                                recipe.getContent(),
                                                getWidth() - 16
                                        )
                                        .size();

                        int maxScroll =
                                Math.max(
                                        0,
                                        totalLines - maxLines
                                );

                        if (maxScroll == 0) {
                            draggingScrollThumb = false;
                            return false;
                        }

                        int thumbHeight =
                                Math.max(
                                        12,
                                        Math.round(
                                                textHeight
                                                        * (
                                                        maxLines
                                                                / (float) totalLines
                                                )
                                        )
                                );

                        int maxThumbMove =
                                textHeight - thumbHeight;

                        int thumbY =
                                Math.round(
                                maxThumbMove
                                        * (
                                        scrollOffset
                                                / (float) maxScroll
                                )
                        );

                        if (input.isSimulate()) {
                            if (
                                    mouseY >= thumbY
                                            && mouseY < thumbY + thumbHeight
                            ) {
                                draggingScrollThumb = true;
                                dragStartMouseY = (int) mouseY;
                                dragStartScrollOffset = scrollOffset;
                                return true;
                            }

                            return false;
                        }

                        draggingScrollThumb = false;
                        return true;
                    }

                    @Override
                    public boolean handleMouseDragged(
                            double mouseX,
                            double mouseY,
                            InputConstants.Key mouseKey,
                            double dragX,
                            double dragY
                    ) {
                        if (!draggingScrollThumb) {
                            return false;
                        }

                        int textHeight =
                                getTextHeight();

                        int maxLines =
                                Math.max(
                                        1,
                                        textHeight / LINE_HEIGHT
                                );

                        int totalLines =
                                Minecraft.getInstance()
                                        .font
                                        .split(
                                                recipe.getContent(),
                                                getWidth() - 16
                                        )
                                        .size();

                        int maxScroll =
                                Math.max(
                                        0,
                                        totalLines - maxLines
                                );

                        int thumbTravel = getThumbTravel(textHeight, maxLines, (float) totalLines);

                        int newScroll = getNewScroll((int) mouseY, thumbTravel, maxScroll);

                        scrollOffset =
                                Math.max(
                                        0,
                                        Math.min(
                                                newScroll,
                                                maxScroll
                                        )
                                );

                        return true;
                    }
                }
        );
    }

    private static int getThumbTravel(int textHeight, int maxLines, float totalLines) {
        int thumbHeight =
                Math.max(
                        12,
                        Math.round(
                                textHeight
                                        * (
                                        maxLines
                                                / totalLines
                                )
                        )
                );

        return textHeight - thumbHeight;
    }

    private int getNewScroll(int mouseY, int thumbTravel, int maxScroll) {
        int deltaY =
                mouseY
                        - dragStartMouseY;

        float ratio =
                thumbTravel == 0
                        ? 0.0F
                        : deltaY
                        / (float) thumbTravel;

        return Math.round(
                dragStartScrollOffset
                        + ratio * maxScroll
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JeiInfoPageRecipe recipe,
            IFocusGroup focuses
    ) {
        int centerX = (getWidth() - 16) / 2;
        int slotY = 8;

        Minecraft minecraft = Minecraft.getInstance();

        RegistryAccess registryAccess =
                minecraft.level != null
                        ? minecraft.level.registryAccess()
                        : null;

        if (recipe.isGroup() || recipe.isBlockTag()) {
            addItemGroup(
                    builder,
                    recipe,
                    registryAccess,
                    centerX,
                    slotY
            );
            return;
        }

        if (recipe.isTag()) {
            addItemTag(
                    builder,
                    recipe,
                    registryAccess,
                    centerX,
                    slotY
            );
            return;
        }

        ItemStack focusStack = recipe.getFocusStack();

        boolean hasItem =
                focusStack != null
                        && !focusStack.isEmpty();

        boolean hasFluid =
                recipe.isFluid()
                        || recipe.isFluidTag();

        if (hasItem && hasFluid) {
            int itemX = centerX - 12;
            int fluidX = centerX + 12;

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            itemX,
                            slotY
                    )
                    .addItemStack(focusStack);

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            itemX,
                            slotY
                    )
                    .addItemStack(focusStack);

            addFluidFocus(
                    builder,
                    recipe,
                    registryAccess,
                    fluidX,
                    slotY
            );

            return;
        }

        if (hasFluid) {
            addFluidFocus(
                    builder,
                    recipe,
                    registryAccess,
                    centerX,
                    slotY
            );
            return;
        }

        if (!hasItem) {
            return;
        }

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        centerX,
                        slotY
                )
                .addItemStack(focusStack);

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        centerX,
                        slotY
                )
                .addItemStack(focusStack);
    }

    private void addItemGroup(
            IRecipeLayoutBuilder builder,
            JeiInfoPageRecipe recipe,
            RegistryAccess registryAccess,
            int slotX,
            int slotY
    ) {

        List<ItemStack> group =
                recipe.getGroupStacks(
                registryAccess
        );

        if (group.isEmpty()) {
            return;
        }

        int slotSpacing =
                20;

        int totalWidth =
                slotSpacing
                        * (group.size() - 1);

        int startX =
                slotX
                        - totalWidth / 2;

        for (int i = 0; i < group.size(); i++) {
            ItemStack stack =
                    group.get(i);

            if (
                    stack == null
                            || stack.isEmpty()
            ) {
                continue;
            }

            int x =
                    startX
                            + i * slotSpacing;

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            x,
                            slotY
                    )
                    .addItemStack(stack);

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            x,
                            slotY
                    )
                    .addItemStack(stack);
        }
    }

    private void addItemTag(
            IRecipeLayoutBuilder builder,
            JeiInfoPageRecipe recipe,
            RegistryAccess registryAccess,
            int slotX,
            int slotY
    ) {

        TagKey<Item> tag =
                recipe.getFocusTag();

        if (tag == null) {
            return;
        }

        List<ItemStack> stacks =
                stacksForItemTag(
                        registryAccess,
                        tag
                );

        if (stacks.isEmpty()) {
            return;
        }

        var input =
                builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        slotX - 10,
                        slotY
                );

        var output =
                builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        slotX + 10,
                        slotY
                );

        for (ItemStack stack : stacks) {
            if (
                    stack == null
                            || stack.isEmpty()
            ) {
                continue;
            }

            input.addItemStack(stack);
            output.addItemStack(stack);
        }
    }

    private void addFluidFocus(
            IRecipeLayoutBuilder builder,
            JeiInfoPageRecipe recipe,
            RegistryAccess registryAccess,
            int slotX,
            int slotY
    ) {

        List<FluidStack> stacks =
                recipe.getFluidStacks(
                registryAccess
        );

        if (
                stacks.isEmpty()
                        && recipe.isFluid()
        ) {
            FluidStack direct =
                    recipe.getFocusFluidStack();

            if (
                    direct != null
                            && !direct.isEmpty()
            ) {
                stacks =
                        List.of(direct);
            }
        }

        if (stacks.isEmpty()) {
            return;
        }

        var input =
                builder.addSlot(
                                RecipeIngredientRole.INPUT,
                                slotX,
                                slotY
                        )
                        .setFluidRenderer(
                                FluidType.BUCKET_VOLUME,
                                false,
                                16,
                                16
                        );

        var output =
                builder.addSlot(
                                RecipeIngredientRole.OUTPUT,
                                slotX,
                                slotY
                        )
                        .setFluidRenderer(
                                FluidType.BUCKET_VOLUME,
                                false,
                                16,
                                16
                        );

        for (FluidStack stack : stacks) {
            if (
                    stack == null
                            || stack.isEmpty()
            ) {
                continue;
            }

            input.addIngredient(
                    NeoForgeTypes.FLUID_STACK,
                    stack
            );

            output.addIngredient(
                    NeoForgeTypes.FLUID_STACK,
                    stack
            );
        }
    }

    private static List<ItemStack> stacksForItemTag(
            RegistryAccess registryAccess,
            TagKey<Item> tag
    ) {
        var items =
                registryAccess.lookupOrThrow(
                        Registries.ITEM
                );

        var named =
                items.get(tag)
                        .orElse(null);

        List<ItemStack> stacks =
                new ArrayList<>();

        if (named == null) {
            return stacks;
        }

        for (var holder : named) {
            ItemStack stack =
                    new ItemStack(
                            holder.value()
                    );

            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        return stacks;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}