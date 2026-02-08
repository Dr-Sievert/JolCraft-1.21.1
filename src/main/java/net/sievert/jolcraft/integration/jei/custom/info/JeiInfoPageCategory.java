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
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.language.subprovider.JeiLangSubProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiInfoPageCategory implements IRecipeCategory<JeiInfoPageRecipe> {
    public static final IRecipeType<JeiInfoPageRecipe> RECIPE_TYPE = IRecipeType.create(JolCraft.MOD_ID, "info_page", JeiInfoPageRecipe.class);

    private final int textStartY = 32;
    private final int textHeight = getHeight() - textStartY - 8;
    private int scrollOffset = 0;
    private boolean draggingScrollThumb = false;
    private int dragStartMouseY = 0;
    private int dragStartScrollOffset = 0;

    private final IDrawable background;
    private final IDrawable icon;

    public JeiInfoPageCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 100);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(JolCraftItems.DWARVEN_TOME.get()));
    }

    @Override
    public IRecipeType<JeiInfoPageRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(JeiLangSubProvider.JEI_CATEGORY_INFO_PAGE);
    }

    @Override
    public int getWidth() {
        return 200;
    }

    @Override
    public int getHeight() {
        return 150;
    }

    @Override
    public void draw(JeiInfoPageRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);

        int lineHeight = 10;
        int maxLines = Math.max(1, textHeight / lineHeight);

        List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(recipe.getContent(), getWidth() - 16);
        int totalLines = lines.size();
        int maxScroll = Math.max(0, totalLines - maxLines);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        for (int i = 0; i < Math.min(maxLines, totalLines - scrollOffset); ++i) {
            graphics.drawString(
                    Minecraft.getInstance().font,
                    lines.get(i + scrollOffset),
                    8,
                    textStartY + i * lineHeight,
                    0x444444,
                    false
            );
        }

        if (totalLines > maxLines) {
            int barX = getWidth() - 8;
            int barY = textStartY;
            int barWidth = 4;
            int barHeight = textHeight;

            graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x66BBBBBB);
            float ratio = maxLines / (float) totalLines;
            int thumbHeight = Math.max(12, Math.round(barHeight * ratio));
            int maxThumbMove = barHeight - thumbHeight;
            int thumbY = barY + (maxScroll == 0 ? 0 : Math.round(maxThumbMove * (scrollOffset / (float)maxScroll)));

            graphics.fill(barX, thumbY, barX + barWidth, thumbY + thumbHeight, 0xFF888888);
            graphics.fill(barX + 1, thumbY + 1, barX + barWidth - 1, thumbY + thumbHeight - 1, 0xFF666666);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, JeiInfoPageRecipe recipe, IFocusGroup focuses) {

        builder.addInputHandler(new IJeiInputHandler() {
            @Override
            public ScreenRectangle getArea() {
                return new ScreenRectangle(0, 0, getWidth(), getHeight());
            }
            @Override
            public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
                int sign = (int) Math.signum(scrollDeltaY);
                int maxLines = textHeight / 10;
                int lines = Minecraft.getInstance().font.split(recipe.getContent(), getWidth() - 16).size();
                int maxScroll = Math.max(0, lines - maxLines);
                scrollOffset = Math.max(0, Math.min(scrollOffset - sign, maxScroll));
                return true;
            }
        });

        builder.addInputHandler(new IJeiInputHandler() {

            @Override
            public ScreenRectangle getArea() {
                int barX = getWidth() - 8;
                int barY = 32;
                int barWidth = 4;
                int barHeight = getHeight() - barY - 8;
                return new ScreenRectangle(barX, barY, barWidth, barHeight);
            }

            @Override
            public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
                if (input.getKey().equals(InputConstants.Type.MOUSE.getOrCreate(0))) {
                    int textStartY = 32;
                    int textHeight = getHeight() - textStartY - 8;
                    int lineHeight = 10;
                    int maxLines = Math.max(1, textHeight / lineHeight);
                    int totalLines = Minecraft.getInstance().font.split(recipe.getContent(), getWidth() - 16).size();
                    int maxScroll = Math.max(0, totalLines - maxLines);
                    int thumbHeight = Math.max(12, Math.round(textHeight * (maxLines / (float) totalLines)));
                    int maxThumbMove = textHeight - thumbHeight;
                    int thumbY = (maxScroll == 0 ? 0 : Math.round(maxThumbMove * (scrollOffset / (float) maxScroll)));

                    if (input.isSimulate()) {
                        if (mouseY >= thumbY && mouseY < (thumbY + thumbHeight)) {
                            draggingScrollThumb = true;
                            dragStartMouseY = (int) mouseY;
                            dragStartScrollOffset = scrollOffset;
                            return true;
                        }
                    } else {
                        draggingScrollThumb = false;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
                if (draggingScrollThumb) {
                    int textStartY = 32;
                    int textHeight = getHeight() - textStartY - 8;
                    int lineHeight = 10;
                    int maxLines = Math.max(1, textHeight / lineHeight);
                    int totalLines = Minecraft.getInstance().font.split(recipe.getContent(), getWidth() - 16).size();
                    int maxScroll = Math.max(0, totalLines - maxLines);

                    int thumbHeight = Math.max(12, Math.round(textHeight * (maxLines / (float) totalLines)));
                    int thumbTravel = textHeight - thumbHeight;

                    int deltaY = (int) mouseY - dragStartMouseY;
                    float ratio = thumbTravel == 0 ? 0 : (float) deltaY / thumbTravel;

                    int newScroll = Math.round(dragStartScrollOffset + ratio * maxScroll);
                    scrollOffset = Math.max(0, Math.min(newScroll, maxScroll));
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JeiInfoPageRecipe recipe, IFocusGroup focuses) {
        int slotX = (getWidth() - 18) / 2;
        int slotY = 8;

        if (recipe.isGroup()) {
            List<ItemStack> group = recipe.getGroupStacks();
            int groupSize = group.size();
            if (groupSize == 0) return;

            int slotSpacing = 20;
            int totalWidth = slotSpacing * (groupSize - 1);
            int startX = slotX - (totalWidth / 2);

            for (int i = 0; i < groupSize; i++) {
                ItemStack stack = group.get(i);
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSpacing, slotY).add(stack);
                builder.addSlot(RecipeIngredientRole.OUTPUT, startX + i * slotSpacing, slotY).add(stack);
            }
            return;
        }

        //Tags
        if (recipe.isTag()) {
            List<ItemStack> stacks = JeiInfoPageHelper.getAllStacksForTag(recipe.getFocusTag());
            if (!stacks.isEmpty()) {
                var slot = builder.addSlot(RecipeIngredientRole.INPUT, slotX - 10, slotY);
                for (ItemStack stack : stacks) {
                    slot.add(stack);
                }
                var outSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, slotX + 10, slotY);
                for (ItemStack stack : stacks) {
                    outSlot.add(stack);
                }
            }
        }

        //Single items
        else {
            builder.addSlot(RecipeIngredientRole.INPUT, slotX, slotY)
                    .add(recipe.getFocusStack());
            builder.addSlot(RecipeIngredientRole.OUTPUT, slotX, slotY)
                    .add(recipe.getFocusStack());
        }

    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}
