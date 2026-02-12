package net.sievert.jolcraft.integration.jei.custom.trade;

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
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfessionEntityTypes;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;
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

    public static IRecipeType<JeiDwarfTrade> recipeTypeFor(DwarfProfession prof) {
        return TYPES.computeIfAbsent(prof, p ->
                IRecipeType.create(
                        JolCraft.MOD_ID,
                        JolCraftStrings.underscored(JolCraftJeiIds.DWARF_TRADE, p.getId()),
                        JeiDwarfTrade.class
                ));
    }

    private static final Map<DwarfProfession, LivingEntity> DWARF_RENDER_CACHE =
            new EnumMap<>(DwarfProfession.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final DwarfProfession profession;

    private static final ResourceLocation ARROW_TEXTURE =
            JolCraft.location("textures/gui/jei/sprites/arrow_right.png");
    private static final ResourceLocation PLUS_TEXTURE =
            JolCraft.location("textures/gui/jei/sprites/plus.png");

    public JeiDwarfTradeCategory(IGuiHelper guiHelper, DwarfProfession profession) {
        this.profession = profession;
        this.background = guiHelper.createBlankDrawable(150, 60);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(JeiDwarfTradeHelper.getSpawnEggForProfession(profession).get())
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

    @Override public int getWidth() { return 150; }
    @Override public int getHeight() { return 60; }

    @Override
    public void draw(JeiDwarfTrade entry, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {

        background.draw(graphics, 0, 0);

        Font font = Minecraft.getInstance().font;

        int level = entry.level();

        Component levelComponent =
                Component.translatable(DwarfMerchantData.Level.langKeyFromId(level));
        Component professionComponent =
                entry.profession().getDisplayName();

        String levelStr = levelComponent.getString();
        String profStr = professionComponent.getString();

        int levelX = 50 - (font.width(levelStr) / 2);
        graphics.drawString(font, levelStr, levelX, 2, 0x888888, false);

        int profX = 50 - (font.width(profStr) / 2);
        graphics.drawString(font, profStr, profX, 12, 0x888888, false);

        ItemStack inputB = entry.inputBExample();
        boolean hasB = inputB != null && !inputB.isEmpty();

        if (hasB) {
            graphics.blit(RenderType.GUI_TEXTURED, PLUS_TEXTURE,
                    16, 27, 0, 0, 12, 12, 12, 12);
        }

        graphics.blit(RenderType.GUI_TEXTURED, ARROW_TEXTURE,
                hasB ? 45 : 21, 24,
                0, 0, 22, 18, 22, 18);

        LivingEntity dwarf = getOrCreateDwarf(entry.profession());
        if (dwarf != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    100, -6,
                    160, 74,
                    32, 0.0F,
                    (float) mouseX, (float) mouseY,
                    dwarf
            );
        }

        // -----------------------------------------------------------------
        // Min/max count overlays (data-driven via TradeAmount)
        // -----------------------------------------------------------------
        final int slotAX = 2;
        final int slotBX = 28;
        final int outputX = hasB ? 68 : 45;

        drawAmountOverlay(graphics, font, entry.inputAmountA(), slotAX);

        DwarfTradeRecipe.TradeAmount bAmt = entry.inputAmountB();
        if (hasB && bAmt != null) {
            drawAmountOverlay(graphics, font, bAmt, slotBX);
        }

        drawAmountOverlay(graphics, font, entry.outputAmount(), outputX);
    }

    private static void drawAmountOverlay(
            GuiGraphics graphics,
            Font font,
            DwarfTradeRecipe.TradeAmount amount,
            int slotX
    ) {
        if (amount.min() == 1 && amount.max() == 1) return;

        String text = (amount.min() == amount.max())
                ? String.valueOf(amount.min())
                : (amount.min() + "-" + amount.max());

        int strW = font.width(text);
        float centerX = slotX + (18 / 2f) - (strW * (float) 0.75 / 2f);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, 43, 0);
        graphics.pose().scale((float) 0.75, (float) 0.75, 1.0f);
        graphics.drawString(font, text, 0, 0, 0x888888, false);
        graphics.pose().popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                          JeiDwarfTrade entry,
                          IFocusGroup focuses) {

        int slotY = 25;

        ItemStack egg = new ItemStack(
                JeiDwarfTradeHelper.getSpawnEggForProfession(entry.profession()).get());

        builder.addSlot(RecipeIngredientRole.INPUT, 95, 42).add(egg);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 42).add(egg);

        ItemStack inputA = entry.inputAExample();
        @Nullable ItemStack inputB = entry.inputBExample();
        ItemStack output =
                entry.outputExample(JeiDwarfTradeHelper.getClientRegistryAccess());

        if (inputA.is(JolCraftItems.GOLD_COIN.get())) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, slotY)
                    .add(new ItemStack(JolCraftItems.GOLD_COIN.get()))
                    .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, slotY).add(inputA);
        }

        if (inputB != null && !inputB.isEmpty()) {
            if (inputB.is(JolCraftItems.GOLD_COIN.get())) {
                builder.addSlot(RecipeIngredientRole.INPUT, 28, slotY)
                        .add(new ItemStack(JolCraftItems.GOLD_COIN.get()))
                        .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, 28, slotY).add(inputB);
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, 68, slotY).add(output);
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 45, slotY).add(output);
        }
    }

    private static @Nullable LivingEntity getOrCreateDwarf(DwarfProfession profession) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        LivingEntity cached = DWARF_RENDER_CACHE.get(profession);
        if (cached != null) return cached;

        DwarfEntity dwarf = new DwarfEntity(DwarfProfessionEntityTypes.get(profession), mc.level);
        dwarf.getEntityData().set(AbstractDwarfEntity.PROFESSION, profession.getId());

        DWARF_RENDER_CACHE.put(profession, dwarf);
        return dwarf;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}