package net.sievert.jolcraft.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.integration.jei.custom.info.InfoPageCategory;
import net.sievert.jolcraft.integration.jei.custom.info.InfoPageHelper;
import net.sievert.jolcraft.integration.jei.custom.trade.DwarfTradeCategory;
import net.sievert.jolcraft.integration.jei.custom.trade.DwarfTradeJeiHelper;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JolCraftJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = JolCraft.location("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        for (var prof : DwarfProfession.values()) {
            registration.addRecipeCategories(new DwarfTradeCategory(guiHelper, prof));
        }
        registration.addRecipeCategories(new InfoPageCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        for (var prof : DwarfProfession.values()) {
            var recipes = DwarfTradeJeiHelper.getAllDwarfJeiTrades(prof);
            if (!recipes.isEmpty()) {
                registration.addRecipes(DwarfTradeCategory.recipeTypeFor(prof), recipes);
            }
        }
        registration.addRecipes(InfoPageCategory.RECIPE_TYPE, InfoPageHelper.getAllInfoPages());
    }
}
