package net.sievert.jolcraft.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageCategory;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageHelper;
import net.sievert.jolcraft.integration.jei.custom.trade.JeiDwarfTradeCategory;
import net.sievert.jolcraft.integration.jei.custom.trade.JeiDwarfTradeHelper;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public final class JolCraftJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = JolCraft.location(JolCraftJeiIds.JEI_PLUGIN);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        for (var prof : DwarfProfession.values()) {
            registration.addRecipeCategories(new JeiDwarfTradeCategory(guiHelper, prof));
        }
        registration.addRecipeCategories(new JeiInfoPageCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        for (var prof : DwarfProfession.values()) {
            var recipes = JeiDwarfTradeHelper.getAllDwarfJeiTrades(prof);
            if (!recipes.isEmpty()) {
                registration.addRecipes(JeiDwarfTradeCategory.recipeTypeFor(prof), recipes);
            }
        }
        registration.addRecipes(JeiInfoPageCategory.RECIPE_TYPE, JeiInfoPageHelper.getAllInfoPages());
    }
}
