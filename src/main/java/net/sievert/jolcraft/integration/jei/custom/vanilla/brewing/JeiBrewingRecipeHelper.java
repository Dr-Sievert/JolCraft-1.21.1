package net.sievert.jolcraft.integration.jei.custom.vanilla.brewing;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.event.game.recipe.brewing.JolCraftBrewingEventHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.ArrayList;
import java.util.List;

public final class JeiBrewingRecipeHelper {

    private JeiBrewingRecipeHelper() {}

    public static List<IJeiBrewingRecipe> getRecipes() {
        List<IJeiBrewingRecipe> recipes = new ArrayList<>();

        for (JolCraftBrewingEventHelper.SpecialBrewingRecipe recipe
                : JolCraftBrewingEventHelper.getSpecialRecipes()) {
            String potionId = recipe.potion()
                    .unwrapKey()
                    .orElseThrow()
                    .location()
                    .getPath();

            List<ItemStack> ingredients = List.of(
                    new ItemStack(recipe.ingredient().asItem())
            );

            List<ItemStack> inputs = List.of(
                    new ItemStack(recipe.input().asItem())
            );

            ItemStack output = PotionContents.createItemStack(
                    Items.POTION,
                    recipe.potion()
            );

            recipes.add(
                    new JolCraftJeiBrewingRecipe(
                            ingredients,
                            inputs,
                            output,
                            JolCraft.location(
                                    JolCraftStrings.slashed(
                                            JolCraftDictionary.BREWING,
                                            potionId
                                    )
                            ),
                            1
                    )
            );
        }

        return List.copyOf(recipes);
    }
}
