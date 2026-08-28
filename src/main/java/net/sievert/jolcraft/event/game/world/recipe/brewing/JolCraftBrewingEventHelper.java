package net.sievert.jolcraft.event.game.world.recipe.brewing;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftBrewingRecipe;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftCorruptedContainerBrewingRecipe;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftCorruptionBrewingRecipe;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftEssenceBrewingRecipe;

import java.util.ArrayList;
import java.util.List;

public final class JolCraftBrewingEventHelper {

    private static final List<SpecialBrewingRecipe> SPECIAL_RECIPES =
            new ArrayList<>();

    private static final List<EssenceBrewingRecipe> ESSENCE_RECIPES =
            new ArrayList<>();

    private JolCraftBrewingEventHelper() {}

    public static int addStartMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addStartMix(
                builder,
                List.of(ingredient),
                potion
        );
    }

    public static int addStartMix(
            PotionBrewing.Builder builder,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        for (ItemLike ingredient : ingredients) {
            builder.addStartMix(
                    ingredient.asItem(),
                    potion
            );
        }

        return ingredients.size() * 2
                + addVariants(builder, potion);
    }

    public static int addInfusedMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addInfusedMix(
                builder,
                List.of(ingredient),
                potion
        );
    }

    public static int addInfusedMix(
            PotionBrewing.Builder builder,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        return addFamilyMix(
                builder,
                JolCraftPotions.INFUSED,
                ingredients,
                potion
        );
    }

    public static int addRefinedMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addRefinedMix(
                builder,
                List.of(ingredient),
                potion
        );
    }

    public static int addRefinedMix(
            PotionBrewing.Builder builder,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        return addFamilyMix(
                builder,
                JolCraftPotions.REFINED,
                ingredients,
                potion
        );
    }

    public static int addExaltedMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addExaltedMix(
                builder,
                List.of(ingredient),
                potion
        );
    }

    public static int addExaltedMix(
            PotionBrewing.Builder builder,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        return addFamilyMix(
                builder,
                JolCraftPotions.EXALTED,
                ingredients,
                potion
        );
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            ItemLike ingredient,
            Holder<Potion> result
    ) {
        return addMix(
                builder,
                input,
                List.of(ingredient),
                result
        );
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            List<? extends ItemLike> ingredients,
            Holder<Potion> result
    ) {
        for (ItemLike ingredient : ingredients) {
            builder.addMix(
                    input,
                    ingredient.asItem(),
                    result
            );
        }

        return ingredients.size();
    }

    public static int addFamilyMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addFamilyMix(
                builder,
                input,
                List.of(ingredient),
                potion
        );
    }

    public static int addFamilyMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        return addMix(
                builder,
                input,
                ingredients,
                potion
        ) + addVariants(
                builder,
                potion
        );
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> result
    ) {
        return addMix(
                builder,
                input,
                List.of(ingredient),
                result
        );
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            List<? extends ItemLike> ingredients,
            Holder<Potion> result
    ) {
        for (ItemLike ingredient : ingredients) {
            builder.addRecipe(
                    new JolCraftBrewingRecipe(
                            Ingredient.of(input),
                            Ingredient.of(ingredient),
                            PotionContents.createItemStack(
                                    Items.POTION,
                                    result
                            )
                    )
            );

            SPECIAL_RECIPES.add(
                    new SpecialBrewingRecipe(
                            input,
                            ingredient,
                            result
                    )
            );
        }

        return ingredients.size();
    }

    public static int addFamilyMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addFamilyMix(
                builder,
                input,
                List.of(ingredient),
                potion
        );
    }

    public static int addFamilyMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        return addMix(
                builder,
                input,
                ingredients,
                potion
        ) + addVariants(
                builder,
                potion
        );
    }

    public static int addEssenceMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            EssenceType essenceType,
            Holder<Potion> output
    ) {
        ItemStack essence =
                JolCraftItems.ESSENCE.get()
                        .createStack(
                                essenceType
                        );

        builder.addRecipe(
                new JolCraftEssenceBrewingRecipe(
                        input,
                        DataComponentIngredient.of(
                                false,
                                essence
                        ),
                        output,
                        JolCraftColors.rgb(
                                essenceType.color()
                        )
                )
        );

        ESSENCE_RECIPES.add(
                new EssenceBrewingRecipe(
                        input,
                        essence.copy(),
                        output,
                        essenceType
                )
        );

        return 1;
    }

    public static int addCorruptionMix(
            PotionBrewing.Builder builder
    ) {
        ItemStack essence =
                JolCraftItems.ESSENCE.get()
                        .createStack(
                                EssenceType.CORRUPTED
                        );

        builder.addRecipe(
                new JolCraftCorruptionBrewingRecipe(
                        DataComponentIngredient.of(
                                false,
                                essence
                        )
                )
        );

        return 1;
    }

    public static int addCorruptedContainerMixes(
            PotionBrewing.Builder builder
    ) {
        builder.addRecipe(
                new JolCraftCorruptedContainerBrewingRecipe()
        );

        return 2;
    }

    public static int addStrong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> strongPotion
    ) {
        builder.addMix(
                potion,
                Items.GLOWSTONE_DUST,
                strongPotion
        );

        return 1;
    }

    public static void clearSpecialRecipes() {
        SPECIAL_RECIPES.clear();
        ESSENCE_RECIPES.clear();
    }

    public static List<SpecialBrewingRecipe> getSpecialRecipes() {
        return List.copyOf(
                SPECIAL_RECIPES
        );
    }

    public static List<EssenceBrewingRecipe> getEssenceRecipes() {
        return List.copyOf(
                ESSENCE_RECIPES
        );
    }

    private static int addVariants(
            PotionBrewing.Builder builder,
            Holder<Potion> potion
    ) {
        JolCraftPotions.PotionFamily family =
                JolCraftPotions.familyOf(
                        potion
                );

        int recipes = 0;

        if (family.longPotion() != null) {
            builder.addMix(
                    family.base(),
                    Items.REDSTONE,
                    family.longPotion()
            );

            recipes++;
        }

        if (family.strongPotion() != null) {
            builder.addMix(
                    family.base(),
                    Items.GLOWSTONE_DUST,
                    family.strongPotion()
            );

            recipes++;
        }

        return recipes;
    }

    public record SpecialBrewingRecipe(
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {}

    public record EssenceBrewingRecipe(
            Holder<Potion> input,
            ItemStack ingredient,
            Holder<Potion> potion,
            EssenceType essenceType
    ) {}
}