package net.sievert.jolcraft.integration.jei.custom.vanilla.brewing;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;

import java.util.ArrayList;
import java.util.List;

public final class JeiBrewingRecipeHelper {

    private JeiBrewingRecipeHelper() {}

    private static final List<ItemStack> FIRST_STEP_INPUTS = List.of(
            PotionContents.createItemStack(Items.POTION, Potions.WATER)
    );

    private static final List<BrewingDefinition> DEFINITIONS = List.of(
            new BrewingDefinition(
                    List.of(new ItemStack(JolCraftItems.VERDANITE_DUST.get())),
                    List.of(new ItemStack(Items.HONEY_BOTTLE)),
                    JolCraftPotions.POISON_RESISTANCE
            ),
            new BrewingDefinition(
                    List.of(new ItemStack(JolCraftItems.DEEPMARROW_DUST.get())),
                    List.of(new ItemStack(Items.EXPERIENCE_BOTTLE)),
                    JolCraftPotions.WISDOM
            )
    );

    public static List<IJeiBrewingRecipe> getRecipes(
            IRecipeRegistration registration
    ) {
        List<IJeiBrewingRecipe> recipes = new ArrayList<>();

        for (BrewingDefinition definition : DEFINITIONS) {
            String potionId = definition.potion()
                    .unwrapKey()
                    .orElseThrow()
                    .location()
                    .getPath();

            ItemStack output = PotionContents.createItemStack(
                    Items.POTION,
                    definition.potion()
            );

            registration.getVanillaRecipeFactory().createBrewingRecipe(
                    definition.ingredients(),
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
                            definition.ingredients(),
                            definition.inputs(),
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

    private record BrewingDefinition(
            List<ItemStack> ingredients,
            List<ItemStack> inputs,
            Holder<Potion> potion
    ) {}
}