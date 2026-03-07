package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.build.custom.vanilla.AttributeSmithingTrimRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.build.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"deprecation", "SameParameterValue"})
public final class TrimRecipesSubProvider implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.TRIM;

    private static final String TRIM_TEMPLATE_SUFFIX =
            "_" + JolCraftStrings.underscored(
                    JolCraftDictionary.ARMOR,
                    JolCraftDictionary.TRIM,
                    JolCraftDictionary.SMITHING,
                    JolCraftDictionary.TEMPLATE
            );

    private static final String SMITHING_TRIM_SUFFIX =
            "_" + JolCraftStrings.underscored(
                    JolCraftDictionary.SMITHING,
                    JolCraftDictionary.TRIM
            );

    private static final String HAS_SMITHING_TRIM_TEMPLATE =
            JolCraftStrings.underscored(
                    JolCraftDictionary.HAS,
                    JolCraftDictionary.SMITHING,
                    JolCraftDictionary.TRIM,
                    JolCraftDictionary.TEMPLATE
            );

    private static final String ATTRIBUTE_PREFIX =
            JolCraftStrings.underscored(JolCraftDictionary.ATTRIBUTE) + "_";

    private static final String HAS_ATTRIBUTE_TRIM_MATERIAL =
            JolCraftStrings.underscored(
                    JolCraftDictionary.HAS,
                    JolCraftDictionary.ATTRIBUTE,
                    JolCraftDictionary.TRIM,
                    JolCraftDictionary.MATERIAL
            );

    @Override
    public @NotNull String folder() {
        return FOLDER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        templateDuplication(
                output,
                items,
                JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.DIAMOND
        );

        for (Item template : jolcraftTrimTemplates()) {
            ResourceKey<Recipe<?>> normalKey = trimRecipeKey(template);
            ResourceKey<Recipe<?>> attributeKey = attributeTrimRecipeKey(template);

            trimSmithing(output, items, template, normalKey);
            attributeTrimSmithing(output, items, template, attributeKey);
        }

        VanillaRecipeProvider.smithingTrims().forEach(vanillaTrim -> {
            String basePath = vanillaTrim.id().location().getPath();
            attributeTrimSmithing(
                    output,
                    items,
                    vanillaTrim.template(),
                    recipeKey(ATTRIBUTE_PREFIX + basePath)
            );
        });
    }

    private static void templateDuplication(
            RecipeOutput output,
            HolderGetter<Item> items,
            Item template,
            ItemLike materialA,
            ItemLike materialB
    ) {
        String idPath = template.builtInRegistryHolder().key().location().getPath();

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, template, 2)
                )
                .pattern("BXB")
                .pattern("BAB")
                .pattern("BBB")
                .define('B', materialB)
                .define('X', template)
                .define('A', materialA)
                .unlockedBy(hasName(template), hasItem(template))
                .save(output, recipeKey(idPath));
    }

    private static void trimSmithing(
            RecipeOutput output,
            HolderGetter<Item> items,
            Item templateItem,
            ResourceKey<Recipe<?>> key
    ) {
        SmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        Ingredient.of(items.getOrThrow(ItemTags.TRIMMABLE_ARMOR)),
                        Ingredient.of(items.getOrThrow(ItemTags.TRIM_MATERIALS)),
                        RecipeCategory.MISC
                )
                .unlocks(HAS_SMITHING_TRIM_TEMPLATE, hasItem(templateItem))
                .save(output, key);
    }

    private static void attributeTrimSmithing(
            RecipeOutput output,
            HolderGetter<Item> items,
            Item templateItem,
            ResourceKey<Recipe<?>> key
    ) {
        AttributeSmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        Ingredient.of(items.getOrThrow(ItemTags.TRIMMABLE_ARMOR)),
                        Ingredient.of(items.getOrThrow(JolCraftTags.Items.ATTRIBUTE_TRIM_MATERIALS)),
                        RecipeCategory.MISC
                )
                .unlocks(hasName(templateItem), hasItem(templateItem))
                .unlocks(
                        HAS_ATTRIBUTE_TRIM_MATERIAL,
                        hasTag(items)
                )
                .save(output, key);
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(
                Registries.RECIPE,
                JolCraft.location(JolCraftStrings.slashed(FOLDER, path))
        );
    }

    private static ResourceKey<Recipe<?>> trimRecipeKey(Item template) {
        String templatePath = template.builtInRegistryHolder().key().location().getPath();
        return recipeKey(templatePath + SMITHING_TRIM_SUFFIX);
    }

    private static ResourceKey<Recipe<?>> attributeTrimRecipeKey(Item template) {
        String templatePath = template.builtInRegistryHolder().key().location().getPath();
        return recipeKey(ATTRIBUTE_PREFIX + templatePath + SMITHING_TRIM_SUFFIX);
    }

    private static String hasName(ItemLike item) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.HAS,
                item.asItem().builtInRegistryHolder().key().location().getPath()
        );
    }

    private static Criterion<?> hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    private static Criterion<?> hasTag(HolderGetter<Item> items) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, JolCraftTags.Items.ATTRIBUTE_TRIM_MATERIALS)
        );
    }

    private static List<Item> jolcraftTrimTemplates() {
        return JolCraftItems.ITEMS.getEntries().stream()
                .map(h -> (Item) h.get())
                .filter(item ->
                        item.builtInRegistryHolder()
                                .key()
                                .location()
                                .getPath()
                                .endsWith(TRIM_TEMPLATE_SUFFIX)
                )
                .toList();
    }
}