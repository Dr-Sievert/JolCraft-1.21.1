package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.datagen.recipe.builder.JolSmithingTrimRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.StreamSupport;

public final class TrimRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    private static final String FOLDER = "trim";
    private static final String TRIM_TEMPLATE_SUFFIX = "_armor_trim_smithing_template";

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        templateDuplication(
                p,
                JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.DIAMOND
        );

        for (Item template : jolcraftTrimTemplates()) {
            ResourceKey<Recipe<?>> normalKey = trimRecipeKey(p, template);
            ResourceKey<Recipe<?>> bonusKey = bonusTrimRecipeKey(p, template);

            trimSmithing(p, template, normalKey);
            bonusTrimSmithing(p, template, bonusKey);
        }

        VanillaRecipeProvider.smithingTrims().forEach(vanillaTrim -> {
            String basePath = vanillaTrim.id().location().getPath();
            bonusTrimSmithing(
                    p,
                    vanillaTrim.template(),
                    recipeKey(p, "bonus_" + basePath)
            );
        });
    }

    private static void templateDuplication(
            AbstractRecipeProvider p,
            Item template,
            ItemLike materialA,
            ItemLike materialB
    ) {
        ItemLike templateLike = () -> template;
        String idPath = BuiltInRegistries.ITEM.getKey(template).getPath();

        p.modShaped(RecipeCategory.MISC, template, 2)
                .pattern("BXB")
                .pattern("BAB")
                .pattern("BBB")
                .define('B', materialB)
                .define('X', template)
                .define('A', materialA)
                .unlockedBy(p.hasName(templateLike), p.hasItem(templateLike))
                .save(p.out(), p.inFolder(FOLDER, idPath));
    }

    private static void trimSmithing(AbstractRecipeProvider p, Item templateItem, ResourceKey<Recipe<?>> key) {
        ItemLike templateLike = () -> templateItem;

        JolSmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        p.tagIngredient(ItemTags.TRIMMABLE_ARMOR),
                        p.tagIngredient(ItemTags.TRIM_MATERIALS),
                        RecipeCategory.MISC
                )
                .unlocks(p.hasName(templateLike), p.hasItem(templateLike))
                .save(p.out(), key);
    }

    private static void bonusTrimSmithing(AbstractRecipeProvider p, Item templateItem, ResourceKey<Recipe<?>> key) {
        ItemLike templateLike = () -> templateItem;

        JolSmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        p.tagIngredient(ItemTags.TRIMMABLE_ARMOR),
                        p.tagIngredient(JolCraftTags.Items.BONUS_TRIM_MATERIALS),
                        RecipeCategory.MISC
                )
                .unlocks(p.hasName(templateLike), p.hasItem(templateLike))
                .unlocks("has_bonus_trim_material", p.hasTag(JolCraftTags.Items.BONUS_TRIM_MATERIALS))
                .save(p.out(), key);
    }

    private static ResourceKey<Recipe<?>> recipeKey(AbstractRecipeProvider p, String path) {
        return ResourceKey.create(Registries.RECIPE, JolCraft.location(p.inFolder(FOLDER, path)));
    }

    private static ResourceKey<Recipe<?>> trimRecipeKey(AbstractRecipeProvider p, Item template) {
        String templatePath = BuiltInRegistries.ITEM.getKey(template).getPath();
        return recipeKey(p, templatePath + "_smithing_trim");
    }

    private static ResourceKey<Recipe<?>> bonusTrimRecipeKey(AbstractRecipeProvider p, Item template) {
        String templatePath = BuiltInRegistries.ITEM.getKey(template).getPath();
        return recipeKey(p, "bonus_" + templatePath + "_smithing_trim");
    }

    private static List<Item> jolcraftTrimTemplates() {
        return StreamSupport.stream(BuiltInRegistries.ITEM.spliterator(), false)
                .filter(item -> {
                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                    return id.getNamespace().equals(JolCraft.MOD_ID)
                            && id.getPath().endsWith(TRIM_TEMPLATE_SUFFIX);
                })
                .toList();
    }
}