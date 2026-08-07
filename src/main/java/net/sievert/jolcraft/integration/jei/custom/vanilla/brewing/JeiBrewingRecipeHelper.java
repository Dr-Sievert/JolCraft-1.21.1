package net.sievert.jolcraft.integration.jei.custom.vanilla.brewing;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.event.game.recipe.brewing.JolCraftBrewingEventHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.ArrayList;
import java.util.List;

public final class JeiBrewingRecipeHelper {

    private JeiBrewingRecipeHelper() {}

    private static final List<ItemStack> FIRST_STEP_INPUTS = List.of(
            PotionContents.createItemStack(Items.POTION, Potions.WATER)
    );

    public static List<IJeiBrewingRecipe> getRecipes(
            IRecipeRegistration registration
    ) {
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

            registration.getVanillaRecipeFactory().createBrewingRecipe(
                    ingredients,
                    FIRST_STEP_INPUTS,
                    output,
                    JolCraft.location(
                            JolCraftStrings.slashed(
                                    JolCraftDictionary.JEI,
                                    JolCraftDictionary.BREWING,
                                    JolCraftDictionary.START,
                                    potionId
                            )
                    )
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
