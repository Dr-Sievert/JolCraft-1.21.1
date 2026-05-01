package net.sievert.jolcraft.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageCategory;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageHelper;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTradeCategory;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTradeHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@JeiPlugin
@SuppressWarnings("removal")
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

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {

        registration.registerSubtypeInterpreter(
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(),
                (stack, context) -> {
                    String loreKey = stack.get(JolCraftDataComponents.DWARF_LORE_KEY.get());
                    return loreKey != null ? loreKey.toLowerCase(Locale.ROOT) : JolCraftDictionary.EMPTY;
                }
        );

        registration.registerSubtypeInterpreter(
                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(),
                (stack, context) -> {
                    String group = stack.get(JolCraftDataComponents.STRUCTURE_GROUP.get());
                    if (group == null || group.isEmpty()) {
                        group = JolCraftDictionary.UNKNOWN;
                    } else {
                        group = group.toLowerCase(Locale.ROOT);
                    }

                    DeepslateCompassDialColor compassColor = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get());
                    String rgb = compassColor != null ? Integer.toString(compassColor.color()) : JolCraftDictionary.DEFAULT;

                    return JolCraftStrings.underscored(group, rgb);
                }
        );
    }
}
