package net.sievert.jolcraft.integration.jei.custom.vanilla.brewing;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.event.game.world.recipe.brewing.JolCraftBrewingEventHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftColors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class JeiBrewingRecipeHelper {

    private JeiBrewingRecipeHelper() {}

    public static List<IJeiBrewingRecipe> getRecipes(
            IVanillaRecipeFactory recipeFactory
    ) {
        addSpecialRecipeStepRoots(recipeFactory);

        List<IJeiBrewingRecipe> recipes = new ArrayList<>();

        for (JolCraftBrewingEventHelper.SpecialBrewingRecipe recipe
                : JolCraftBrewingEventHelper.getSpecialRecipes()) {
            ResourceLocation potionId = recipe.potion()
                    .unwrapKey()
                    .orElseThrow()
                    .location();

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
                                            potionId.getPath()
                                    )
                            ),
                            1
                    )
            );
        }

        for (JolCraftBrewingEventHelper.EssenceBrewingRecipe recipe
                : JolCraftBrewingEventHelper.getEssenceRecipes()) {
            ResourceLocation potionId = recipe.potion()
                    .unwrapKey()
                    .orElseThrow()
                    .location();

            ItemStack input = PotionContents.createItemStack(
                    Items.POTION,
                    recipe.input()
            );

            ItemStack output = new ItemStack(
                    Items.POTION
            );

            output.set(
                    DataComponents.POTION_CONTENTS,
                    new PotionContents(
                            Optional.of(recipe.potion()),
                            Optional.of(
                                    JolCraftColors.rgb(
                                            recipe.essenceType().color()
                                    )
                            ),
                            List.of()
                    )
            );

            recipes.add(
                    recipeFactory.createBrewingRecipe(
                            List.of(
                                    recipe.ingredient().copy()
                            ),
                            input,
                            output,
                            JolCraft.location(
                                    JolCraftStrings.slashed(
                                            JolCraftDictionary.BREWING,
                                            potionId.getPath()
                                    )
                            )
                    )
            );
        }

        return List.copyOf(recipes);
    }

    private static void addSpecialRecipeStepRoots(
            IVanillaRecipeFactory recipeFactory
    ) {
        ItemStack waterBottle = PotionContents.createItemStack(
                Items.POTION,
                Potions.WATER
        );

        Set<ResourceLocation> addedRoots = new HashSet<>();

        for (JolCraftBrewingEventHelper.SpecialBrewingRecipe recipe
                : JolCraftBrewingEventHelper.getSpecialRecipes()) {
            ResourceLocation potionId = recipe.potion()
                    .unwrapKey()
                    .orElseThrow()
                    .location();

            if (!addedRoots.add(potionId)) {
                continue;
            }

            recipeFactory.createBrewingRecipe(
                    List.of(
                            new ItemStack(
                                    recipe.ingredient().asItem()
                            )
                    ),
                    waterBottle,
                    PotionContents.createItemStack(
                            Items.POTION,
                            recipe.potion()
                    ),
                    JolCraft.location(
                            JolCraftStrings.slashed(
                                    JolCraftDictionary.BREWING,
                                    JolCraftDictionary.ROOT,
                                    potionId.getNamespace(),
                                    potionId.getPath()
                            )
                    )
            );
        }
    }
}