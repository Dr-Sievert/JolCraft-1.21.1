package net.sievert.jolcraft.event.game.recipe.brewing;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftBrewingRecipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JolCraftBrewingEventHelper {

    private static final Map<Holder<Potion>, SpecialBrewingRecipe> SPECIAL_RECIPES =
            new LinkedHashMap<>();

    private JolCraftBrewingEventHelper() {}

    public static int addStartMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {
        builder.addStartMix(ingredient.asItem(), potion);
        return 2;
    }

    public static int addStartMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion,
            Holder<Potion> longPotion
    ) {
        return addStartMix(builder, ingredient, potion)
                + addLong(builder, potion, longPotion);
    }

    public static int addStartMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion,
            Holder<Potion> longPotion,
            Holder<Potion> strongPotion
    ) {
        return addStartMix(builder, ingredient, potion)
                + addLongStrong(builder, potion, longPotion, strongPotion);
    }

    public static int addStartMix(
            PotionBrewing.Builder builder,
            List<? extends ItemLike> ingredients,
            Holder<Potion> potion,
            Holder<Potion> longPotion,
            Holder<Potion> strongPotion
    ) {
        int recipes = 0;

        for (ItemLike ingredient : ingredients) {
            recipes += addStartMix(builder, ingredient, potion);
        }

        return recipes + addLongStrong(builder, potion, longPotion, strongPotion);
    }

    public static int addStartStrongMix(
            PotionBrewing.Builder builder,
            ItemLike ingredient,
            Holder<Potion> potion,
            Holder<Potion> strongPotion
    ) {
        return addStartMix(builder, ingredient, potion)
                + addStrong(builder, potion, strongPotion);
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

    public static int addMix(
            PotionBrewing.Builder builder,
            Holder<Potion> input,
            ItemLike ingredient,
            Holder<Potion> potion,
            Holder<Potion> longPotion,
            Holder<Potion> strongPotion
    ) {
        return addMix(builder, input, ingredient, potion)
                + addLongStrong(builder, potion, longPotion, strongPotion);
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
                        PotionContents.createItemStack(Items.POTION, result)
                )
        );

        SPECIAL_RECIPES.put(
                result,
                new SpecialBrewingRecipe(input, ingredient, result)
        );

        return 1;
    }

    public static int addMix(
            PotionBrewing.Builder builder,
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> potion,
            Holder<Potion> longPotion,
            Holder<Potion> strongPotion
    ) {
        return addMix(builder, input, ingredient, potion)
                + addLongStrong(builder, potion, longPotion, strongPotion);
    }

    public static int addLong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> longPotion
    ) {
        builder.addMix(potion, Items.REDSTONE, longPotion);
        return 1;
    }

    public static int addStrong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> strongPotion
    ) {
        builder.addMix(potion, Items.GLOWSTONE_DUST, strongPotion);
        return 1;
    }

    public static int addLongStrong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> longPotion,
            Holder<Potion> strongPotion
    ) {
        builder.addMix(potion, Items.REDSTONE, longPotion);
        builder.addMix(potion, Items.GLOWSTONE_DUST, strongPotion);
        return 2;
    }

    public static List<SpecialBrewingRecipe> getSpecialRecipes() {
        return List.copyOf(SPECIAL_RECIPES.values());
    }

    public record SpecialBrewingRecipe(
            ItemLike input,
            ItemLike ingredient,
            Holder<Potion> potion
    ) {}
}
