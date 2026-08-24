package net.sievert.jolcraft.event.game.world.recipe.brewing;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftBrewingRecipe;

import java.util.ArrayList;
import java.util.List;

public final class JolCraftBrewingEventHelper {

    private static final List<SpecialBrewingRecipe> SPECIAL_RECIPES =
            new ArrayList<>();

    private JolCraftBrewingEventHelper() {}

    public static int addStartMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        builder.addStartMix(ingredient.asItem(), potion);

        return 2 + addVariants(builder, potion);
    }

    public static int addStartMix(
            PotionBrewing.Builder builder,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion
    ) {
        int recipes = 0;

        for (ItemLike ingredient : ingredients) {
            builder.addStartMix(ingredient.asItem(), potion);
            recipes += 2;
        }

        return recipes + addVariants(builder, potion);
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            ItemLike ingredient,
            Holder<Potion> result
    ) {
        builder.addMix(potion, ingredient.asItem(), result);
        return 1;
    }

    public static int addFamilyMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addMix(builder, input, ingredient, potion)
                + addVariants(builder, potion);
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> result
    ) {
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

        return 1;
    }

    public static int addFamilyMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        return addMix(builder, input, ingredient, potion)
                + addVariants(builder, potion);
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
    }

    public static List<SpecialBrewingRecipe> getSpecialRecipes() {
        return List.copyOf(SPECIAL_RECIPES);
    }

    private static int addVariants(
            PotionBrewing.Builder builder,
            Holder<Potion> potion
    ) {
        JolCraftPotions.PotionFamily family =
                JolCraftPotions.familyOf(potion);

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
}
