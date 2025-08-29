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
import net.sievert.jolcraft.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfTradeCategory implements IRecipeCategory<DwarfTradeRecipe> {
    public static final IRecipeType<DwarfTradeRecipe> RECIPE_TYPE = IRecipeType.create(JolCraft.MOD_ID, "dwarf_trades", DwarfTradeRecipe.class);
    private static final java.util.Map<String, LivingEntity> DWARF_RENDER_CACHE = new java.util.HashMap<>();
    private final IDrawable background;
    private final IDrawable icon;
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/jei/sprites/arrow_right.png");
    private static final ResourceLocation PLUS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/jei/sprites/plus.png");


    public DwarfTradeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(JolCraftItems.GOLD_COIN.get()));
    }

    @Override
    public IRecipeType<DwarfTradeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.jolcraft.dwarf_trades");
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
    public void draw(DwarfTradeRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);

        // === Profession and level merged, e.g. "Master Dwarf" ===
        int textY = 12;        // vertical position (tweak here)
        int offsetX = -19;       // horizontal offset (tweak here)

        // Get level string (e.g., "Master")
        int level = recipe.getLevel();
        String levelKey = "merchant.level." + level;
        String levelStr = Component.translatable(levelKey).getString();

        // Merge with profession, e.g. "Master Dwarf"
        String profText = recipe.getProfession();
        String displayStr = levelStr + " " + profText;

        // Center the full string
        int x = ((getWidth() - Minecraft.getInstance().font.width(displayStr)) / 2) + offsetX;

        // Draw merged string
        graphics.drawString(Minecraft.getInstance().font, displayStr, x+3, textY, 0x888888, false);

        // Input slots: always A, maybe B
        boolean hasB = recipe.getInputB() != null && !recipe.getInputB().isEmpty();
        int plusX = 16, plusY = 27;
        int arrowX = hasB ? 45 : 21;
        int arrowY = 24;

        // Draw plus if two inputs
        if (hasB) {
            graphics.blit(
                    RenderType.GUI_TEXTURED,
                    PLUS_TEXTURE,
                    plusX, plusY,
                    0, 0,
                    12, 12,
                    12, 12
            );
        }

        // Draw arrow
        graphics.blit(
                RenderType.GUI_TEXTURED,
                ARROW_TEXTURE,
                arrowX, arrowY,
                0, 0,
                22, 18,
                22, 18
        );

        LivingEntity dwarf = getOrCreateDwarf(recipe);
        {
            int offsetY = -16;
            int minX = 100, minY = 10 + offsetY, maxX = 160, maxY = 90 + offsetY;
            int scale = 32;
            float yOffset = 0.0F;

            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    minX, minY, maxX, maxY,
                    scale,
                    yOffset,
                    (float) mouseX, (float) mouseY,
                    dwarf
            );
        }

        // --- DRAW THE COUNT/RANGE OVERLAYS ---
        float scale = 0.75f;
        int overlayY = 43; // y under item slot, tweak as needed

// Slot X and width definitions (slot width is always 18 for JEI slots)
        final int slotWidth = 18;
        final int slotAX = 2;
        final int slotBX = 28;
        final int outputX = (recipe.getInputB() != null && !recipe.getInputB().isEmpty()) ? 68 : 45;

// --- Input A ---
        if (recipe.getInputAMin() != 1 || recipe.getInputAMax() != 1) {
            String aText = (recipe.getInputAMin() == recipe.getInputAMax())
                    ? String.valueOf(recipe.getInputAMin())
                    : (recipe.getInputAMin() + "-" + recipe.getInputAMax());
            int strW = Minecraft.getInstance().font.width(aText);
            // Center under slot: (slot center) - (half string width at scale)
            float centerX = slotAX + (slotWidth / 2f) - (strW * scale / 2f);

            graphics.pose().pushPose();
            graphics.pose().translate(centerX, overlayY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(Minecraft.getInstance().font, aText, 0, 0, 0x888888, false);
            graphics.pose().popPose();
        }

// --- Input B ---
        if (recipe.getInputB() != null && !recipe.getInputB().isEmpty()
                && (recipe.getInputBMin() != 1 || recipe.getInputBMax() != 1)) {
            String bText = (recipe.getInputBMin() == recipe.getInputBMax())
                    ? String.valueOf(recipe.getInputBMin())
                    : (recipe.getInputBMin() + "-" + recipe.getInputBMax());
            int strW = Minecraft.getInstance().font.width(bText);
            float centerX = slotBX + (slotWidth / 2f) - (strW * scale / 2f);

            graphics.pose().pushPose();
            graphics.pose().translate(centerX, overlayY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(Minecraft.getInstance().font, bText, 0, 0, 0x888888, false);
            graphics.pose().popPose();
        }

// --- Output ---
        if (recipe.getOutputMin() != 1 || recipe.getOutputMax() != 1) {
            String oText = (recipe.getOutputMin() == recipe.getOutputMax())
                    ? String.valueOf(recipe.getOutputMin())
                    : (recipe.getOutputMin() + "-" + recipe.getOutputMax());
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
        int sloty = 25;
        // Profession egg slot (always first)
        builder.addSlot(RecipeIngredientRole.INPUT, 95, 42)
                .add(recipe.getSpawnEgg());
        if(recipe.getInputA().is(JolCraftItems.GOLD_COIN.get())){
            // Input A (coin pouch or gold coin)
            builder.addSlot(RecipeIngredientRole.INPUT, 2, sloty).add(new ItemStack(JolCraftItems.GOLD_COIN.get())).add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
        }else{
            builder.addSlot(RecipeIngredientRole.INPUT, 2, sloty).add(recipe.getInputA());
        }

        // Input B if present (for recipes requiring two inputs)
        if (recipe.getInputB() != null && !recipe.getInputB().isEmpty()) {
            if(recipe.getInputB().is(JolCraftItems.GOLD_COIN.get())){
                // Input B (coin pouch or gold coin)
                builder.addSlot(RecipeIngredientRole.INPUT, 28, sloty).add(new ItemStack(JolCraftItems.GOLD_COIN.get())).add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
            }else{
                builder.addSlot(RecipeIngredientRole.INPUT, 28, sloty).add(recipe.getInputB());
            }
            // Output farther right for double input
            builder.addSlot(RecipeIngredientRole.OUTPUT, 68, sloty).add(recipe.getOutput());
        } else {
            // Output closer for single input
            builder.addSlot(RecipeIngredientRole.OUTPUT, 45, sloty).add(recipe.getOutput());
        }
    }


    public static LivingEntity getOrCreateDwarf(DwarfTradeRecipe recipe) {
        String profession = recipe.getProfession();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || profession == null) return null;

        String key = profession.toLowerCase();
        LivingEntity cached = DWARF_RENDER_CACHE.get(key);
        if (cached != null) return cached;

        LivingEntity entity = null;
        switch (key) {
            case "dwarf" -> entity = new DwarfEntity(JolCraftEntities.DWARF.get(), mc.level);
            case "guildmaster" -> entity = new DwarfGuildmasterEntity(JolCraftEntities.DWARF_GUILDMASTER.get(), mc.level);
            case "historian" -> entity = new DwarfHistorianEntity(JolCraftEntities.DWARF_HISTORIAN.get(), mc.level);
            case "merchant" -> entity = new DwarfMerchantEntity(JolCraftEntities.DWARF_MERCHANT.get(), mc.level);
            case "scrapper" -> entity = new DwarfScrapperEntity(JolCraftEntities.DWARF_SCRAPPER.get(), mc.level);
            case "brewmaster" -> entity = new DwarfBrewmasterEntity(JolCraftEntities.DWARF_BREWMASTER.get(), mc.level);
            case "guard" -> entity = new DwarfGuardEntity(JolCraftEntities.DWARF_GUARD.get(), mc.level);
            case "keeper" -> entity = new DwarfKeeperEntity(JolCraftEntities.DWARF_KEEPER.get(), mc.level);
            case "artisan" -> entity = new DwarfArtisanEntity(JolCraftEntities.DWARF_ARTISAN.get(), mc.level);
            case "explorer" -> entity = new DwarfExplorerEntity(JolCraftEntities.DWARF_EXPLORER.get(), mc.level);
            case "miner" -> entity = new DwarfMinerEntity(JolCraftEntities.DWARF_MINER.get(), mc.level);
            default -> entity = new DwarfEntity(JolCraftEntities.DWARF.get(), mc.level); // fallback
        }

        DWARF_RENDER_CACHE.put(key, entity);
        return entity;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}
