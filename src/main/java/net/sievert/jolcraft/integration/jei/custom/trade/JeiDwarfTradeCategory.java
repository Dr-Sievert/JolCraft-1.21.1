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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.language.subprovider.JeiLangSubProvider;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JeiDwarfTradeCategory implements IRecipeCategory<JeiDwarfTrade> {

    private static final Map<DwarfProfession, IRecipeType<JeiDwarfTrade>> TYPES = new EnumMap<>(DwarfProfession.class);

    public static IRecipeType<JeiDwarfTrade> recipeTypeFor(DwarfProfession prof) {
        return TYPES.computeIfAbsent(prof, p ->
                IRecipeType.create(JolCraft.MOD_ID, "dwarf_trades/" + p.getId(), JeiDwarfTrade.class)
        );
    }

    private static final Map<DwarfProfession, LivingEntity> DWARF_RENDER_CACHE = new EnumMap<>(DwarfProfession.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final DwarfProfession profession;

    private static final ResourceLocation ARROW_TEXTURE = JolCraft.location("textures/gui/jei/sprites/arrow_right.png");
    private static final ResourceLocation PLUS_TEXTURE = JolCraft.location("textures/gui/jei/sprites/plus.png");

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
        return Component.literal(
                Component.translatable(JeiLangSubProvider.JEI_CATEGORY_DWARF_TRADES).getString()
                        + " — "
                        + JeiDwarfTradeHelper.getDisplayName(profession)
        );
    }

    @Override public int getWidth() { return 150; }
    @Override public int getHeight() { return 60; }

    @Override
    public void draw(JeiDwarfTrade entry, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);

        int level = entry.level();

        String levelKey = "merchant.level." + level;
        String levelStr = Component.translatable(levelKey).getString();
        String profText = JeiDwarfTradeHelper.getDisplayName(entry.profession());

        int levelX = 50 - (Minecraft.getInstance().font.width(levelStr) / 2);
        graphics.drawString(Minecraft.getInstance().font, levelStr, levelX, 2, 0x888888, false);

        int profX = 50 - (Minecraft.getInstance().font.width(profText) / 2);
        graphics.drawString(Minecraft.getInstance().font, profText, profX, 12, 0x888888, false);

        ItemStack inputB = entry.inputBExample();
        boolean hasB = inputB != null && !inputB.isEmpty();

        int plusX = 16, plusY = 27;
        int arrowX = hasB ? 45 : 21;
        int arrowY = 24;

        if (hasB) {
            graphics.blit(RenderType.GUI_TEXTURED, PLUS_TEXTURE, plusX, plusY, 0, 0, 12, 12, 12, 12);
        }
        graphics.blit(RenderType.GUI_TEXTURED, ARROW_TEXTURE, arrowX, arrowY, 0, 0, 22, 18, 22, 18);

        LivingEntity dwarf = getOrCreateDwarf(entry.profession());
        if (dwarf != null) {
            int offsetY = -16;
            int minX = 100, minY = 10 + offsetY, maxX = 160, maxY = 90 + offsetY;
            int scale = 32;
            float yOffset = 0.0F;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics, minX, minY, maxX, maxY, scale, yOffset, (float) mouseX, (float) mouseY, dwarf
            );
        }

        // Count overlays are now computed from the real recipe amounts.
        // If you still want min/max overlays, you must expose those via DwarfTradeRecipe's TradeAmount API.
        // For now: no overlays (correct + non-legacy).
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JeiDwarfTrade entry, IFocusGroup focuses) {
        int slotY = 25;

        ItemStack egg = new ItemStack(JeiDwarfTradeHelper.getSpawnEggForProfession(entry.profession()).get());
        builder.addSlot(RecipeIngredientRole.INPUT, 95, 42).add(egg);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 42).add(egg);

        ItemStack inputA = entry.inputAExample();
        @Nullable ItemStack inputB = entry.inputBExample();
        ItemStack output = entry.outputExample(JeiDwarfTradeHelper.getClientRegistryAccess());

        // Input A (special display: coin OR pouch)
        if (inputA.is(JolCraftItems.GOLD_COIN.get())) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, slotY)
                    .add(new ItemStack(JolCraftItems.GOLD_COIN.get()))
                    .add(new ItemStack(JolCraftItems.COIN_POUCH.get()));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, slotY).add(inputA);
        }

        // Input B + output positioning
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

        var type = switch (profession) {
            case GUILDMASTER -> JolCraftEntities.DWARF_GUILDMASTER.get();
            case HISTORIAN   -> JolCraftEntities.DWARF_HISTORIAN.get();
            case MERCHANT    -> JolCraftEntities.DWARF_MERCHANT.get();
            case SCRAPPER    -> JolCraftEntities.DWARF_SCRAPPER.get();
            case BREWMASTER  -> JolCraftEntities.DWARF_BREWMASTER.get();
            case GUARD       -> JolCraftEntities.DWARF_GUARD.get();
            case KEEPER      -> JolCraftEntities.DWARF_KEEPER.get();
            case ARTISAN     -> JolCraftEntities.DWARF_ARTISAN.get();
            case EXPLORER    -> JolCraftEntities.DWARF_EXPLORER.get();
            case MINER       -> JolCraftEntities.DWARF_MINER.get();
            case ALCHEMIST   -> JolCraftEntities.DWARF_ALCHEMIST.get();
            case ARCANIST    -> JolCraftEntities.DWARF_ARCANIST.get();
            case PRIEST      -> JolCraftEntities.DWARF_PRIEST.get();
            case NONE        -> JolCraftEntities.DWARF.get();
        };

        DwarfEntity dwarf = new DwarfEntity(type, mc.level);

        dwarf.getEntityData().set(AbstractDwarfEntity.PROFESSION, profession.getId());

        DWARF_RENDER_CACHE.put(profession, dwarf);
        return dwarf;
    }


    @Override
    public IDrawable getIcon() {
        return icon;
    }
}