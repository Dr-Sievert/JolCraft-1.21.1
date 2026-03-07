package net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class VanillaRecipeBuilder {

    private VanillaRecipeBuilder() {}

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
                    JolCraftStrings.underscored(JolCraftDictionary.HAS, item.asItem().builtInRegistryHolder().key().location().getPath()),
                    InventoryChangeTrigger.TriggerInstance.hasItems(item)
            );
            return this;
        }

        public void save(RecipeOutput out) {
            builder.save(out);
        }

        public void save(RecipeOutput out, ResourceKey<Recipe<?>> id) {
            builder.save(out, id);
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
                    JolCraftStrings.underscored(
                            JolCraftDictionary.HAS,
                            item.asItem().builtInRegistryHolder().key().location().getPath()
                    ),
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

        public void save(RecipeOutput out, ResourceKey<Recipe<?>> id) {
            builder.save(out, id);
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
                ResourceKey<Recipe<?>> packedId,
                ResourceKey<Recipe<?>> unpackedId
        ) {

            VanillaRecipeBuilder.shapeless(
                            ShapelessRecipeBuilder.shapeless(items, unpackedCategory, unpacked, 9)
                    )
                    .requires(packed)
                    .unlockedByHas(packed)
                    .save(out, unpackedId);

            VanillaRecipeBuilder.shaped(
                            ShapedRecipeBuilder.shaped(items, packedCategory, packed)
                    )
                    .define('#', unpacked)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedByHas(unpacked)
                    .save(out, packedId);
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
                    JolCraftStrings.underscored(
                            JolCraftDictionary.HAS,
                            item.asItem().builtInRegistryHolder().key().location().getPath()
                    ),
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
                    ResourceKey.create(
                            Registries.RECIPE,
                            ResourceLocation.parse(
                                    result.asItem().builtInRegistryHolder().key().location().getPath()
                                            + suffix
                                            + "_"
                                            + input.asItem().builtInRegistryHolder().key().location().getPath()
                            )
                    )
            );
        }

        public void save(RecipeOutput out, ResourceKey<Recipe<?>> id) {
            builder.save(out, id);
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
}