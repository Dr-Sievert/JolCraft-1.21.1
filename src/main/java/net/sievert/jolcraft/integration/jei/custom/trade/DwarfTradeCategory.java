package net.sievert.jolcraft.integration.jei.custom.trade;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.custom.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.profession.*;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfTradeCategory implements IRecipeCategory<DwarfTradeRecipe> {

    private static final Map<DwarfProfession, IRecipeType<DwarfTradeRecipe>> TYPES = new EnumMap<>(DwarfProfession.class);
    public static IRecipeType<DwarfTradeRecipe> recipeTypeFor(DwarfProfession prof) {
        return TYPES.computeIfAbsent(prof, p ->
                IRecipeType.create(JolCraft.MOD_ID, "dwarf_trades/" + p.getId(), DwarfTradeRecipe.class)
        );
    }

    private static final java.util.Map<String, LivingEntity> DWARF_RENDER_CACHE = new java.util.HashMap<>();
    private final IDrawable background;
    private final IDrawable icon;
    private final DwarfProfession profession;

    private static final ResourceLocation ARROW_TEXTURE = JolCraft.location("textures/gui/jei/sprites/arrow_right.png");
    private static final ResourceLocation PLUS_TEXTURE = JolCraft.location("textures/gui/jei/sprites/plus.png");

    public DwarfTradeCategory(IGuiHelper guiHelper, DwarfProfession profession) {
        this.profession = profession;
        this.background = guiHelper.createBlankDrawable(150, 60);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(DwarfTradeJeiHelper.getSpawnEggForProfession(profession).get())
        );
    }

    @Override
    public IRecipeType<DwarfTradeRecipe> getRecipeType() {
        return recipeTypeFor(profession);
    }

    @Override
    public Component getTitle() {
        // Title like: "Dwarf Trades — Guildmaster"
        return Component.literal(Component.translatable("jei.jolcraft.dwarf_trades").getString() +
                " — " + DwarfTradeJeiHelper.getDisplayName(profession));
    }

    @Override public int getWidth() { return 150; }
    @Override public int getHeight() { return 60; }

    @Override
    public void draw(DwarfTradeRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);

        int textY = 12;
        int offsetX = -19;
        int level = recipe.level();

        String levelKey = "merchant.level." + level;
        String levelStr = Component.translatable(levelKey).getString();
        String profText = DwarfTradeJeiHelper.getDisplayName(recipe.profession());
        String displayStr = levelStr + " " + profText;

        int x = ((getWidth() - Minecraft.getInstance().font.width(displayStr)) / 2) + offsetX;
        graphics.drawString(Minecraft.getInstance().font, displayStr, x + 3, textY, 0x888888, false);

        boolean hasB = recipe.inputB() != null && !recipe.inputB().isEmpty();
        int plusX = 16, plusY = 27;
        int arrowX = hasB ? 45 : 21;
        int arrowY = 24;

        if (hasB) {
            graphics.blit(RenderType.GUI_TEXTURED, PLUS_TEXTURE, plusX, plusY, 0, 0, 12, 12, 12, 12);
        }
        graphics.blit(RenderType.GUI_TEXTURED, ARROW_TEXTURE, arrowX, arrowY, 0, 0, 22, 18, 22, 18);

        LivingEntity dwarf = getOrCreateDwarf(recipe);
        {
            int offsetY = -16;
            int minX = 100, minY = 10 + offsetY, maxX = 160, maxY = 90 + offsetY;
            int scale = 32;
            float yOffset = 0.0F;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics, minX, minY, maxX, maxY, scale, yOffset, (float) mouseX, (float) mouseY, dwarf
            );
        }

        float scale = 0.75f;
        int overlayY = 43;

        final int slotWidth = 18;
        final int slotAX = 2;
        final int slotBX = 28;
        final int outputX = (recipe.inputB() != null && !recipe.inputB().isEmpty()) ? 68 : 45;

        if (recipe.inputAMin() != 1 || recipe.inputAMax() != 1) {
            String aText = (recipe.inputAMin() == recipe.inputAMax())
                    ? String.valueOf(recipe.inputAMin())
                    : (recipe.inputAMin() + "-" + recipe.inputAMax());
            int strW = Minecraft.getInstance().font.width(aText);
            float centerX = slotAX + (slotWidth / 2f) - (strW * scale / 2f);

            graphics.pose().pushPose();
            graphics.pose().translate(centerX, overlayY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(Minecraft.getInstance().font, aText, 0, 0, 0x888888, false);
            graphics.pose().popPose();
        }

        if (recipe.inputB() != null && !recipe.inputB().isEmpty()
                && (recipe.inputBMin() != 1 || recipe.inputBMax() != 1)) {
            String bText = (recipe.inputBMin() == recipe.inputBMax())
                    ? String.valueOf(recipe.inputBMin())
                    : (recipe.inputBMin() + "-" + recipe.inputBMax());
            int strW = Minecraft.getInstance().font.width(bText);
            float centerX = slotBX + (slotWidth / 2f) - (strW * scale / 2f);

            graphics.pose().pushPose();
            graphics.pose().translate(centerX, overlayY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(Minecraft.getInstance().font, bText, 0, 0, 0x888888, false);
            graphics.pose().popPose();
        }

        if (recipe.outputMin() != 1 || recipe.outputMax() != 1) {
            String oText = (recipe.outputMin() == recipe.outputMax())
                    ? String.valueOf(recipe.outputMin())
                    : (recipe.outputMin() + "-" + recipe.outputMax());
            int strW = Minecraft.getInstance().font.width(oText);
            float centerX = (float) outputX + (slotWidth / 2f) - (strW * scale / 2f);

            graphics.pose().pushPose();
            graphics.pose().translate(centerX, overlayY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(Minecraft.getInstance().font, oText, 0, 0, 0x888888, false);
            graphics.pose().popPose();
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DwarfTradeRecipe recipe, IFocusGroup focuses) {
        int slotY = 25;

        ItemStack egg = new ItemStack(DwarfTradeJeiHelper.getSpawnEggForProfession(recipe.profession()).get());
        builder.addSlot(RecipeIngredientRole.INPUT, 95, 42).add(egg);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 42).add(egg);

        if (recipe.inputA().is(JolCraftItems.GOLD_COIN.get())) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, slotY)
                    .add(new ItemStack(JolCraftItems.GOLD_COIN.get()))
                    .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, slotY).add(recipe.inputA());
        }

        if (recipe.inputB() != null && !recipe.inputB().isEmpty()) {
            if (recipe.inputB().is(JolCraftItems.GOLD_COIN.get())) {
                builder.addSlot(RecipeIngredientRole.INPUT, 28, slotY)
                        .add(new ItemStack(JolCraftItems.GOLD_COIN.get()))
                        .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, 28, slotY).add(recipe.inputB());
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, 68, slotY).add(recipe.output());
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 45, slotY).add(recipe.output());
        }
    }

    private static LivingEntity getOrCreateDwarf(DwarfTradeRecipe recipe) {
        DwarfProfession profession = recipe.profession();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || profession == null) return null;

        String key = profession.name();
        LivingEntity cached = DWARF_RENDER_CACHE.get(key);
        if (cached != null) return cached;

        LivingEntity entity = switch (profession) {
            case NONE       -> new DwarfEntity(JolCraftEntities.DWARF.get(), mc.level);
            case GUILDMASTER -> new DwarfGuildmasterEntity(JolCraftEntities.DWARF_GUILDMASTER.get(), mc.level);
            case HISTORIAN  -> new DwarfHistorianEntity(JolCraftEntities.DWARF_HISTORIAN.get(), mc.level);
            case MERCHANT   -> new DwarfMerchantEntity(JolCraftEntities.DWARF_MERCHANT.get(), mc.level);
            case SCRAPPER   -> new DwarfScrapperEntity(JolCraftEntities.DWARF_SCRAPPER.get(), mc.level);
            case BREWMASTER -> new DwarfBrewmasterEntity(JolCraftEntities.DWARF_BREWMASTER.get(), mc.level);
            case GUARD      -> new DwarfGuardEntity(JolCraftEntities.DWARF_GUARD.get(), mc.level);
            case KEEPER     -> new DwarfKeeperEntity(JolCraftEntities.DWARF_KEEPER.get(), mc.level);
            case ARTISAN    -> new DwarfArtisanEntity(JolCraftEntities.DWARF_ARTISAN.get(), mc.level);
            case EXPLORER   -> new DwarfExplorerEntity(JolCraftEntities.DWARF_EXPLORER.get(), mc.level);
            case MINER      -> new DwarfMinerEntity(JolCraftEntities.DWARF_MINER.get(), mc.level);
            case ALCHEMIST  -> new DwarfAlchemistEntity(JolCraftEntities.DWARF_ALCHEMIST.get(), mc.level);
            case ARCANIST   -> new DwarfArcanistEntity(JolCraftEntities.DWARF_ARCANIST.get(), mc.level);
            case PRIEST     -> new DwarfPriestEntity(JolCraftEntities.DWARF_PRIEST.get(), mc.level);
        };

        DWARF_RENDER_CACHE.put(key, entity);
        return entity;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}
