package net.sievert.jolcraft.datagen.recipe.builder.vanilla;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class VanillaRecipeBuilder {

    private VanillaRecipeBuilder() {}

    private static @NotNull String itemPath(ItemLike item) {
        return item.asItem().builtInRegistryHolder().key().location().getPath();
    }

    private static @NotNull ResourceLocation recipeId(@NotNull String folder, @NotNull String path) {
        return JolCraft.location(JolCraftStrings.slashed(folder, path));
    }

    private static @NotNull ResourceLocation recipeId(@NotNull String path) {
        return JolCraft.location(path);
    }

    private static @NotNull ResourceLocation recipeId(@NotNull String folder, @NotNull ItemLike item) {
        return recipeId(folder, itemPath(item));
    }

    // ---------------------------------------------------------------------
    // SHAPED
    // ---------------------------------------------------------------------

    public static final class Shaped {

        private final ShapedRecipeBuilder builder;

        public Shaped(ShapedRecipeBuilder builder) {
            this.builder = builder;
        }

        public @NotNull Shaped pattern(String row) {
            builder.pattern(row);
            return this;
        }

        public @NotNull Shaped define(char key, ItemLike item) {
            builder.define(key, item);
            return this;
        }

        public @NotNull Shaped define(char key, TagKey<Item> tag) {
            builder.define(key, tag);
            return this;
        }

        public @NotNull Shaped define(char key, Ingredient ingredient) {
            builder.define(key, ingredient);
            return this;
        }

        public @NotNull Shaped group(String group) {
            builder.group(group);
            return this;
        }

        public @NotNull Shaped showNotification(boolean show) {
            builder.showNotification(show);
            return this;
        }

        public @NotNull Shaped unlockedBy(String key, Criterion<?> criterion) {
            builder.unlockedBy(key, criterion);
            return this;
        }

        public @NotNull Shaped unlockedByHas(ItemLike item) {
            builder.unlockedBy(
                    JolCraftStrings.underscored(JolCraftDictionary.HAS, itemPath(item)),
                    InventoryChangeTrigger.TriggerInstance.hasItems(item)
            );
            return this;
        }

        public void save(RecipeOutput out) {
            builder.save(out);
        }

        public void save(RecipeOutput out, ResourceLocation id) {
            builder.save(out, id);
        }

        public void save(RecipeOutput out, String folder, ItemLike result) {
            builder.save(out, recipeId(folder, result));
        }

        public void save(RecipeOutput out, String folder, String path) {
            builder.save(out, recipeId(folder, path));
        }
    }

    // ---------------------------------------------------------------------
    // SHAPELESS
    // ---------------------------------------------------------------------

    public static final class Shapeless {

        private final ShapelessRecipeBuilder builder;

        public Shapeless(ShapelessRecipeBuilder builder) {
            this.builder = builder;
        }

        public @NotNull Shapeless requires(ItemLike item) {
            builder.requires(item);
            return this;
        }

        public @NotNull Shapeless requires(ItemLike item, int count) {
            builder.requires(item, count);
            return this;
        }

        public @NotNull Shapeless requires(TagKey<Item> tag) {
            builder.requires(tag);
            return this;
        }

        public @NotNull Shapeless requires(Ingredient ingredient) {
            builder.requires(ingredient);
            return this;
        }

        public @NotNull Shapeless unlockedBy(String key, Criterion<?> criterion) {
            builder.unlockedBy(key, criterion);
            return this;
        }

        public @NotNull Shapeless unlockedByHas(ItemLike item) {
            builder.unlockedBy(
                    JolCraftStrings.underscored(JolCraftDictionary.HAS, itemPath(item)),
                    InventoryChangeTrigger.TriggerInstance.hasItems(item)
            );
            return this;
        }

        public @NotNull Shapeless group(String group) {
            builder.group(group);
            return this;
        }

        public void save(RecipeOutput out) {
            builder.save(out);
        }

        public void save(RecipeOutput out, ResourceLocation id) {
            builder.save(out, id);
        }

        public void save(RecipeOutput out, String folder, ItemLike result) {
            builder.save(out, recipeId(folder, result));
        }

        public void save(RecipeOutput out, String folder, String path) {
            builder.save(out, recipeId(folder, path));
        }
    }

    // ---------------------------------------------------------------------
    // SMITHING
    // ---------------------------------------------------------------------

    public static final class Smithing {

        private final SmithingTransformRecipeBuilder builder;
        private final ItemLike result;

        private Smithing(
                SmithingTransformRecipeBuilder builder,
                ItemLike result
        ) {
            this.builder = builder;
            this.result = result;
        }

        public @NotNull Smithing unlockedBy(String key, Criterion<?> criterion) {
            builder.unlocks(key, criterion);
            return this;
        }

        public @NotNull Smithing unlockedByHas(ItemLike item) {
            builder.unlocks(
                    JolCraftStrings.underscored(JolCraftDictionary.HAS, itemPath(item)),
                    InventoryChangeTrigger.TriggerInstance.hasItems(item)
            );
            return this;
        }

        public void save(RecipeOutput out) {
            builder.save(
                    out,
                    recipeId(itemPath(result) + "_smithing")
            );
        }

        public void save(RecipeOutput out, ResourceLocation id) {
            builder.save(out, id);
        }

        public void save(RecipeOutput out, String folder) {
            builder.save(
                    out,
                    recipeId(folder, itemPath(result) + "_smithing")
            );
        }

        public void save(RecipeOutput out, String folder, String path) {
            builder.save(out, recipeId(folder, path));
        }
    }

    // ---------------------------------------------------------------------
    // STORAGE
    // ---------------------------------------------------------------------

    public static final class Storage {

        private Storage() {}

        public static void nineBlock(
                HolderGetter<Item> items,
                RecipeOutput out,
                RecipeCategory unpackedCategory,
                ItemLike unpacked,
                RecipeCategory packedCategory,
                ItemLike packed,
                ResourceLocation packedId,
                ResourceLocation unpackedId
        ) {
            VanillaRecipeBuilder.shapeless(
                            ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
                    )
                    .requires(packed)
                    .unlockedByHas(packed)
                    .save(out, unpackedId);

            VanillaRecipeBuilder.shaped(
                            ShapedRecipeBuilder.shaped(packedCategory, packed)
                    )
                    .define('#', unpacked)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedByHas(unpacked)
                    .save(out, packedId);
        }

        public static void nineBlock(
                HolderGetter<Item> items,
                RecipeOutput out,
                String folder,
                RecipeCategory unpackedCategory,
                ItemLike unpacked,
                RecipeCategory packedCategory,
                ItemLike packed
        ) {
            VanillaRecipeBuilder.shapeless(
                            ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
                    )
                    .requires(packed)
                    .unlockedByHas(packed)
                    .save(out, folder, unpacked);

            VanillaRecipeBuilder.shaped(
                            ShapedRecipeBuilder.shaped(packedCategory, packed)
                    )
                    .define('#', unpacked)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedByHas(unpacked)
                    .save(out, folder, packed);
        }
    }

    // ---------------------------------------------------------------------
    // COOKING
    // ---------------------------------------------------------------------

    public static final class Cooking {

        private final SimpleCookingRecipeBuilder builder;
        private final ItemLike input;
        private final ItemLike result;
        private final String suffix;

        private Cooking(
                SimpleCookingRecipeBuilder builder,
                ItemLike input,
                ItemLike result,
                String suffix
        ) {
            this.builder = builder;
            this.input = input;
            this.result = result;
            this.suffix = suffix;
        }

        public static @NotNull Cooking smelting(
                ItemLike input,
                RecipeCategory category,
                ItemLike result,
                float experience,
                int cookingTime
        ) {
            return new Cooking(
                    SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(input),
                            category,
                            result,
                            experience,
                            cookingTime
                    ),
                    input,
                    result,
                    "_from_smelting"
            );
        }

        public static @NotNull Cooking blasting(
                ItemLike input,
                RecipeCategory category,
                ItemLike result,
                float experience,
                int cookingTime
        ) {
            return new Cooking(
                    SimpleCookingRecipeBuilder.blasting(
                            Ingredient.of(input),
                            category,
                            result,
                            experience,
                            cookingTime
                    ),
                    input,
                    result,
                    "_from_blasting"
            );
        }

        public static @NotNull Cooking smoking(
                ItemLike input,
                RecipeCategory category,
                ItemLike result,
                float experience,
                int cookingTime
        ) {
            return new Cooking(
                    SimpleCookingRecipeBuilder.smoking(
                            Ingredient.of(input),
                            category,
                            result,
                            experience,
                            cookingTime
                    ),
                    input,
                    result,
                    "_from_smoking"
            );
        }

        public static @NotNull Cooking campfire(
                ItemLike input,
                RecipeCategory category,
                ItemLike result,
                float experience,
                int cookingTime
        ) {
            return new Cooking(
                    SimpleCookingRecipeBuilder.campfireCooking(
                            Ingredient.of(input),
                            category,
                            result,
                            experience,
                            cookingTime
                    ),
                    input,
                    result,
                    "_from_campfire_cooking"
            );
        }

        public @NotNull Cooking unlockedBy(String key, Criterion<?> criterion) {
            builder.unlockedBy(key, criterion);
            return this;
        }

        public @NotNull Cooking unlockedByHas(ItemLike item) {
            builder.unlockedBy(
                    JolCraftStrings.underscored(JolCraftDictionary.HAS, itemPath(item)),
                    InventoryChangeTrigger.TriggerInstance.hasItems(item)
            );
            return this;
        }

        public @NotNull Cooking group(String group) {
            builder.group(group);
            return this;
        }

        public void save(RecipeOutput out) {
            builder.save(
                    out,
                    recipeId(itemPath(result) + suffix + "_" + itemPath(input))
            );
        }

        public void save(RecipeOutput out, ResourceLocation id) {
            builder.save(out, id);
        }

        public void save(RecipeOutput out, String folder) {
            builder.save(
                    out,
                    recipeId(folder, itemPath(result) + suffix + "_" + itemPath(input))
            );
        }

        public void save(RecipeOutput out, String folder, String path) {
            builder.save(out, recipeId(folder, path));
        }
    }

    // ---------------------------------------------------------------------
    // Factories
    // ---------------------------------------------------------------------

    public static @NotNull Shaped shaped(ShapedRecipeBuilder builder) {
        return new Shaped(builder);
    }

    public static @NotNull Shapeless shapeless(ShapelessRecipeBuilder builder) {
        return new Shapeless(builder);
    }

    public static @NotNull Smithing smithing(
            Ingredient template,
            Ingredient base,
            Ingredient addition,
            RecipeCategory category,
            ItemLike result
    ) {
        return new Smithing(
                SmithingTransformRecipeBuilder.smithing(
                        template,
                        base,
                        addition,
                        category,
                        result.asItem()
                ),
                result
        );
    }

    public static @NotNull Smithing smithing(
            ItemLike template,
            ItemLike base,
            ItemLike addition,
            RecipeCategory category,
            ItemLike result
    ) {
        return smithing(
                Ingredient.of(template),
                Ingredient.of(base),
                Ingredient.of(addition),
                category,
                result
        );
    }
}