package net.sievert.jolcraft.datagen.recipe.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.recipe.builder.JolCraftRecipeBuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractRecipeProvider extends RecipeProvider {

    protected AbstractRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public interface RecipeSubProvider {
        void addRecipes(AbstractRecipeProvider p);
    }

    protected final void runAll(List<? extends RecipeSubProvider> subs) {
        for (RecipeSubProvider sub : subs) {
            sub.addRecipes(this);
        }
    }

    public final RecipeOutput out() {
        return this.output;
    }

    public final String inFolder(String folder, ItemLike out) {
        return normalizeFolder(folder) + itemName(out);
    }

    public final String inFolder(String folder, String idPath) {
        return normalizeFolder(folder) + idPath;
    }

    private static String normalizeFolder(String folder) {
        if (folder.isBlank()) return "";
        return folder.endsWith("/") ? folder : folder + "/";
    }

    @SuppressWarnings("deprecation")
    private static String itemPath(ItemLike item) {
        return item.asItem().builtInRegistryHolder().key().location().getPath();
    }

    public final Criterion<?> hasItem(ItemLike item) {
        return this.has(item);
    }

    public final Criterion<?> hasTag(TagKey<Item> tag) {
        return this.has(tag);
    }

    public final String hasName(ItemLike item) {
        return getHasName(item);
    }

    public final String itemName(ItemLike item) {
        return getItemName(item);
    }

    public final Ingredient tagIngredient(TagKey<Item> tag) {
        return tag(tag);
    }

    public ShapedRecipeBuilder createShapedBuilder(RecipeCategory category, ItemLike result, int count) {
        return shaped(category, result, count);
    }

    public final JolCraftRecipeBuilder modShaped(RecipeCategory category, ItemLike result, int count) {
        return new JolCraftRecipeBuilder(createShapedBuilder(category, result, count), JolCraft.MOD_ID);
    }

    public final JolCraftRecipeBuilder modShaped(RecipeCategory category, ItemLike result) {
        return modShaped(category, result, 1);
    }

    public final JolCraftRecipeBuilder modShapeless(RecipeCategory category, ItemLike result, int count) {
        return new JolCraftRecipeBuilder(shapeless(category, result, count), JolCraft.MOD_ID);
    }

    public final JolCraftRecipeBuilder modShapeless(RecipeCategory category, ItemLike result) {
        return modShapeless(category, result, 1);
    }

    @Override
    public void oreSmelting(List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                            float experience, int cookingTime, String name) {
        for (ItemLike ingredient : ingredients) {
            String idPath = !name.isBlank()
                    ? ingredients.size() == 1 ? name : name + "_from_" + getItemName(ingredient)
                    : getItemName(result) + "_from_smelting_" + getItemName(ingredient);

            SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), category, result, experience, cookingTime)
                    .group(name)
                    .unlockedBy(getHasName(ingredient), this.has(ingredient))
                    .save(out(), ResourceKey.create(Registries.RECIPE, JolCraft.location(idPath)));
        }
    }

    @Override
    public void oreBlasting(List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                            float experience, int cookingTime, String name) {
        for (ItemLike ingredient : ingredients) {
            String idPath = !name.isBlank()
                    ? ingredients.size() == 1 ? name : name + "_from_" + getItemName(ingredient)
                    : getItemName(result) + "_from_blasting_" + getItemName(ingredient);

            SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), category, result, experience, cookingTime)
                    .group(name)
                    .unlockedBy(getHasName(ingredient), this.has(ingredient))
                    .save(out(), ResourceKey.create(Registries.RECIPE, JolCraft.location(idPath)));
        }
    }

    @SuppressWarnings("deprecation")
    public final void nineBlockStorageRecipesAuto(
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed,
            @Nullable String packedGroup,
            @Nullable String unpackedGroup
    ) {
        String unpackedName = unpacked.asItem().builtInRegistryHolder().key().location().getPath();
        String packedName = packed.asItem().builtInRegistryHolder().key().location().getPath();

        String unpackedIdPath = unpackedName + "_from_" + packedName;

        nineBlockStorageRecipes(
                unpackedCategory,
                unpacked,
                packedCategory,
                packed,
                packedName,
                packedGroup,
                unpackedIdPath,
                unpackedGroup
        );
    }

    public final void nineBlockStorageRecipesAuto(
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed
    ) {
        nineBlockStorageRecipesAuto(unpackedCategory, unpacked, packedCategory, packed, null, null);
    }

    public static final List<Item> DYES = List.of(
            Items.BLACK_DYE,
            Items.BLUE_DYE,
            Items.BROWN_DYE,
            Items.CYAN_DYE,
            Items.GRAY_DYE,
            Items.GREEN_DYE,
            Items.LIGHT_BLUE_DYE,
            Items.LIGHT_GRAY_DYE,
            Items.LIME_DYE,
            Items.MAGENTA_DYE,
            Items.ORANGE_DYE,
            Items.PINK_DYE,
            Items.PURPLE_DYE,
            Items.RED_DYE,
            Items.YELLOW_DYE,
            Items.WHITE_DYE
    );
}