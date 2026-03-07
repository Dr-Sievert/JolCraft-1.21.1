package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JeiDwarfTradeCategory implements IRecipeCategory<JeiDwarfTrade> {

    private static final Map<DwarfProfession, IRecipeType<JeiDwarfTrade>> TYPES =
            new EnumMap<>(DwarfProfession.class);

    public static IRecipeType<JeiDwarfTrade> recipeTypeFor(DwarfProfession profession) {
        return TYPES.computeIfAbsent(profession, p ->
                IRecipeType.create(
                        JolCraft.MOD_ID,
                        JolCraftStrings.underscored(JolCraftJeiIds.DWARF_TRADE, p.getId()),
                        JeiDwarfTrade.class
                ));
    }

    private static final Map<DwarfProfession, LivingEntity> DWARF_RENDER_CACHE =
            new EnumMap<>(DwarfProfession.class);

    private static final ResourceLocation ARROW_TEXTURE =
            JolCraftTextures.jeiRl(JolCraftTextures.jei(
                    JolCraftStrings.underscored(JolCraftDictionary.RECIPE, JolCraftDictionary.ARROW)
            ));

    private static final ResourceLocation PLUS_TEXTURE =
            JolCraftTextures.jeiRl(JolCraftTextures.jei(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.RECIPE,
                            JolCraftDictionary.PLUS,
                            JolCraftDictionary.SIGN
                    )
            ));

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_Y = 25;
    private static final int SLOT_A_X = 2;
    private static final int SLOT_B_X = 28;
    private static final int HAS_B_SHIFT = 7;
    private static final int PLUS_W = 13;
    private static final int PLUS_H = 13;
    private static final int ARROW_W = 22;
    private static final int ARROW_H = 16;

    private final IDrawable background;
    private final IDrawable icon;
    private final DwarfProfession profession;

    public JeiDwarfTradeCategory(IGuiHelper guiHelper, DwarfProfession profession) {
        this.profession = profession;
        this.background = guiHelper.createBlankDrawable(150, 60);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(DwarfProfessionHelper.getSpawnEgg(profession).get())
        );
    }

    @Override
    public IRecipeType<JeiDwarfTrade> getRecipeType() {
        return recipeTypeFor(profession);
    }

    @Override
    public Component getTitle() {
        return Component.translatable(JolCraftLanguageKeys.JEI_CATEGORY_DWARF_TRADES)
                .append(" — ")
                .append(profession.getDisplayName());
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
        background.draw(graphics, 0, 0);

        Font font = Minecraft.getInstance().font;

        int levelId = entry.level().getId();

        Component levelComponent =
                Component.translatable(DwarfMerchantData.Level.langKeyFromId(levelId));
        Component professionComponent = entry.profession().getDisplayName();

        String levelStr = levelComponent.getString();
        String professionStr = professionComponent.getString();

        int levelX = 50 - (font.width(levelStr) / 2);
        graphics.drawString(font, levelStr, levelX, 2, 0x888888, false);

        int professionX = 50 - (font.width(professionStr) / 2);
        graphics.drawString(font, professionStr, professionX, 12, 0x888888, false);

        @Nullable ItemStack inputB = entry.inputBExample();
        boolean hasB = inputB != null && !inputB.isEmpty();

        int shift = hasB ? HAS_B_SHIFT : 0;

        int slotAX = SLOT_A_X;
        int slotBX = SLOT_B_X + shift;
        int outputX = hasB ? (68 + shift) : 45;

        if (hasB) {
            int aRight = slotAX + SLOT_SIZE;
            int gap = slotBX - aRight;
            int plusX = aRight + (gap - PLUS_W) / 2;

            graphics.blit(
                    RenderType.GUI_TEXTURED,
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

        int arrowX = hasB
                ? (slotBX + SLOT_SIZE - 1)
                : (slotAX + SLOT_SIZE + 1);

        graphics.blit(
                RenderType.GUI_TEXTURED,
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

        LivingEntity dwarf = getOrCreateDwarf(entry.profession());
        if (dwarf != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    100,
                    -6,
                    160,
                    74,
                    32,
                    0.0F,
                    (float) mouseX,
                    (float) mouseY,
                    dwarf
            );
        }

        drawAmountOverlay(graphics, font, entry.inputAmountA(), slotAX);

        IntRange inputAmountB = entry.inputAmountB();
        if (hasB && inputAmountB != null) {
            drawAmountOverlay(graphics, font, inputAmountB, slotBX);
        }

        drawAmountOverlay(graphics, font, entry.outputAmount(), outputX);
    }

    private static void drawAmountOverlay(
            GuiGraphics graphics,
            Font font,
            IntRange amount,
            int slotX
    ) {
        if (amount.min() == 1 && amount.max() == 1) {
            return;
        }

        String text = (amount.min() == amount.max())
                ? String.valueOf(amount.min())
                : (amount.min() + "-" + amount.max());

        int strW = font.width(text);
        float centerX = slotX + (SLOT_SIZE / 2f) - (strW * 0.75f / 2f);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, 43, 0);
        graphics.pose().scale(0.75f, 0.75f, 1.0f);
        graphics.drawString(font, text, 0, 0, 0x888888, false);
        graphics.pose().popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JeiDwarfTrade entry, IFocusGroup focuses) {
        int slotY = SLOT_Y;

        ItemStack egg = new ItemStack(DwarfProfessionHelper.getSpawnEgg(entry.profession()).get());

        @Nullable ItemStack inputB = entry.inputBExample();
        boolean hasB = inputB != null && !inputB.isEmpty();
        int shift = hasB ? HAS_B_SHIFT : 0;

        builder.addSlot(RecipeIngredientRole.INPUT, 95, 42).add(egg);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 42).add(egg);

        ItemStack inputA = entry.inputAExample();
        ItemStack output = entry.outputExample();

        if (entry.costAItemIs(JolCraftTags.Items.COINS)) {
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT_A_X, slotY)
                    .add(inputA)
                    .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT_A_X, slotY).add(inputA);
        }

        if (hasB) {
            int slotBX = SLOT_B_X + shift;

            if (entry.costBItemIs(JolCraftTags.Items.COINS)) {
                builder.addSlot(RecipeIngredientRole.INPUT, slotBX, slotY)
                        .add(inputB)
                        .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, slotBX, slotY).add(inputB);
            }

            builder.addSlot(RecipeIngredientRole.OUTPUT, 68 + shift, slotY).add(output);
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 45, slotY).add(output);
        }
    }

    private static @Nullable LivingEntity getOrCreateDwarf(DwarfProfession profession) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }

        LivingEntity cached = DWARF_RENDER_CACHE.get(profession);
        if (cached != null) {
            return cached;
        }

        DwarfEntity dwarf = new DwarfEntity(DwarfProfessionHelper.getEntityType(profession), mc.level);
        dwarf.getEntityData().set(AbstractDwarfEntity.PROFESSION, profession.getId());

        DWARF_RENDER_CACHE.put(profession, dwarf);
        return dwarf;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}