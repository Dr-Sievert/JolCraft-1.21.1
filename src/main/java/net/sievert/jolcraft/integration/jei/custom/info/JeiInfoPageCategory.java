package net.sievert.jolcraft.integration.jei.custom.info;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
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
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.AbstractJeiCategory;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.SLOT_SIZE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiInfoPageCategory extends AbstractJeiCategory<JeiInfoPageRecipe> {

    public static final RecipeType<JeiInfoPageRecipe> RECIPE_TYPE = JeiRecipeTypes.INFO_PAGE;

    private final int textStartY = 32;
    private final int textHeight = getHeight() - textStartY - 8;
    private int scrollOffset = 0;
    private boolean draggingScrollThumb = false;
    private int dragStartMouseY = 0;
    private int dragStartScrollOffset = 0;

    public JeiInfoPageCategory(IGuiHelper guiHelper) {
        super(
                guiHelper,
                RECIPE_TYPE,
                Component.translatable(JolCraftLanguageKeys.JEI_CATEGORY_INFO_PAGE),
                200,
                150,
                150,
                100,
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(JolCraftItems.DWARVEN_TOME.get())
                )
        );
    }

    @Override
    protected void drawRecipe(JeiInfoPageRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {

        int lineHeight = 10;
        int maxLines = Math.max(1, textHeight / lineHeight);

        List<FormattedCharSequence> lines =
                Minecraft.getInstance().font.split(recipe.getContent(), getWidth() - 16);

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
            int thumbY = barY + (maxScroll == 0 ? 0 : Math.round(maxThumbMove * (scrollOffset / (float) maxScroll)));

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
                    int thumbY = textStartY + (maxScroll == 0 ? 0 : Math.round(maxThumbMove * (scrollOffset / (float) maxScroll)));

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
        int slotX = (getWidth() - SLOT_SIZE) / 2;
        int slotY = 8;

        Minecraft mc = Minecraft.getInstance();
        RegistryAccess registryAccess = mc.level != null ? mc.level.registryAccess() : null;

        if (recipe.isGroup() || recipe.isBlockTag()) {
            if (recipe.isBlockTag() && registryAccess == null) {
                return;
            }

            List<ItemStack> group = registryAccess != null
                    ? recipe.getGroupStacks(registryAccess)
                    : recipe.getGroupStacks();

            if (group.isEmpty()) {
                return;
            }

            int slotSpacing = 20;
            int totalWidth = slotSpacing * (group.size() - 1);
            int startX = slotX - (totalWidth / 2);

            for (int i = 0; i < group.size(); i++) {
                ItemStack stack = group.get(i);
                if (stack == null || stack.isEmpty()) {
                    continue;
                }

                int x = startX + i * slotSpacing;
                builder.addSlot(RecipeIngredientRole.INPUT, x, slotY).addItemStack(stack);
                builder.addSlot(RecipeIngredientRole.OUTPUT, x, slotY).addItemStack(stack);
            }
            return;
        }

        if (recipe.isTag()) {
            if (registryAccess == null) {
                return;
            }

            TagKey<Item> tag = recipe.getFocusTag();
            if (tag == null) {
                return;
            }

            List<ItemStack> stacks = stacksForItemTag(registryAccess, tag);
            if (stacks.isEmpty()) {
                return;
            }

            var in = builder.addSlot(RecipeIngredientRole.INPUT, slotX - 10, slotY);
            var out = builder.addSlot(RecipeIngredientRole.OUTPUT, slotX + 10, slotY);

            for (ItemStack stack : stacks) {
                if (stack == null || stack.isEmpty()) {
                    continue;
                }
                in.addItemStack(stack);
                out.addItemStack(stack);
            }
            return;
        }

        ItemStack focus = recipe.getFocusStack();
        if (focus == null || focus.isEmpty()) {
            return;
        }

        builder.addSlot(RecipeIngredientRole.INPUT, slotX, slotY).addItemStack(focus);
        builder.addSlot(RecipeIngredientRole.OUTPUT, slotX, slotY).addItemStack(focus);
    }

    private static List<ItemStack> stacksForItemTag(RegistryAccess registryAccess, TagKey<Item> tag) {
        var items = registryAccess.lookupOrThrow(Registries.ITEM);
        var named = items.get(tag).orElse(null);

        List<ItemStack> stacks = new ArrayList<>();
        if (named == null) {
            return stacks;
        }

        for (var holder : named) {
            stacks.add(new ItemStack(holder.value()));
        }

        return stacks;
    }

}