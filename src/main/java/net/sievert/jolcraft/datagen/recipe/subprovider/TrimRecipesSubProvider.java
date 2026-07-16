package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.AttributeSmithingTrimRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"deprecation", "SameParameterValue"})
public record TrimRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

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

    public TrimRecipesSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    @Override
    public @NotNull String id() {
        return folder();
    }

    @Override
    public @NotNull String folder() {
        return FOLDER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        templateDuplication(
                output,
                JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.DIAMOND
        );

        for (Item template : jolcraftTrimTemplates()) {
            ResourceLocation normalId = trimRecipeId(template);
            ResourceLocation attributeId = attributeTrimRecipeId(template);

            trimSmithing(output, template, normalId);
            attributeTrimSmithing(output, template, attributeId);
        }

        VanillaRecipeProvider.smithingTrims().forEach(vanillaTrim -> {
            String basePath = vanillaTrim.id().getPath();
            attributeTrimSmithing(
                    output,
                    vanillaTrim.template(),
                    recipeId(ATTRIBUTE_PREFIX + basePath)
            );
        });
    }

    private static void templateDuplication(
            RecipeOutput output,
            Item template,
            ItemLike materialA,
            ItemLike materialB
    ) {
        String idPath = template.builtInRegistryHolder().key().location().getPath();

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template, 2)
                )
                .pattern("BXB")
                .pattern("BAB")
                .pattern("BBB")
                .define('B', materialB)
                .define('X', template)
                .define('A', materialA)
                .unlockedBy(hasName(template), hasItem(template))
                .save(output, recipeId(idPath));
    }

    private static void trimSmithing(
            RecipeOutput output,
            Item templateItem,
            ResourceLocation id
    ) {
        SmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        Ingredient.of(ItemTags.TRIMMABLE_ARMOR),
                        Ingredient.of(ItemTags.TRIM_MATERIALS),
                        RecipeCategory.MISC
                )
                .unlocks(HAS_SMITHING_TRIM_TEMPLATE, hasItem(templateItem))
                .save(output, id);
    }

    private static void attributeTrimSmithing(
            RecipeOutput output,
            Item templateItem,
            ResourceLocation id
    ) {
        AttributeSmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        Ingredient.of(ItemTags.TRIMMABLE_ARMOR),
                        Ingredient.of(JolCraftTags.Items.ATTRIBUTE_TRIM_MATERIALS),
                        RecipeCategory.MISC
                )
                .unlocks(hasName(templateItem), hasItem(templateItem))
                .unlocks(HAS_ATTRIBUTE_TRIM_MATERIAL, hasTag())
                .save(output, id);
    }

    private static ResourceLocation recipeId(String path) {
        return JolCraft.location(JolCraftStrings.slashed(FOLDER, path));
    }

    private static ResourceLocation trimRecipeId(Item template) {
        String templatePath = template.builtInRegistryHolder().key().location().getPath();
        return recipeId(templatePath + SMITHING_TRIM_SUFFIX);
    }

    private static ResourceLocation attributeTrimRecipeId(Item template) {
        String templatePath = template.builtInRegistryHolder().key().location().getPath();
        return recipeId(ATTRIBUTE_PREFIX + templatePath + SMITHING_TRIM_SUFFIX);
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

    private static Criterion<?> hasTag() {
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(JolCraftTags.Items.ATTRIBUTE_TRIM_MATERIALS).build()
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