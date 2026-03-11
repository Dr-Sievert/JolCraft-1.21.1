package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.ComponentPreservingShapelessRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.DyeColorRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.base.OutputsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ComponentTransformBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ItemTransformsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.hook.HookBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CompassRecipesSubProvider implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftItemIds.DEEPSLATE_COMPASS;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        // =========================================================
        // EMPTY DEEPSLATE COMPASS
        // =========================================================

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                )
                .pattern(" B ")
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output);

        for (var r : DyeColorRecipeBuilder.buildAll(
                CraftingBookCategory.MISC,
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                Ingredient.of(Items.WATER_BUCKET)
        )) {
            r.builder().save(output, recipeKey(r.file()));
        }



        // =========================================================
        // DEEPSLATE COMPASS
        // =========================================================

        for (var r : DyeColorRecipeBuilder.buildAll(
                CraftingBookCategory.MISC,
                JolCraftItems.DEEPSLATE_COMPASS.get(),
                Ingredient.of(Items.WATER_BUCKET)
        )) {
            r.builder().save(output, recipeKey(r.file()));
        }

        ComponentPreservingShapelessRecipeBuilder.create(
                        CraftingBookCategory.MISC,
                        Ingredient.of(JolCraftItems.DEEPSLATE_COMPASS.get())
                )
                .result(new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                .keep(DataComponents.DYED_COLOR)
                .save(
                        output,
                        recipeKey(
                                JolCraftStrings.underscored(
                                        JolCraftItemIds.DEEPSLATE_COMPASS,
                                        JolCraftDictionary.REMOVE,
                                        JolCraftDictionary.DIAL
                                )
                        )
                );

        executor.emit(
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .item(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .item(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(
                                OutputsBuilder.create()
                                        .wrapSingle(
                                                ItemOutputBuilder.create()
                                                        .result(JolCraftItems.DEEPSLATE_COMPASS.get().asItem(), 1)
                                                        .transforms(
                                                                ItemTransformsBuilder.create()
                                                                        .component(
                                                                                ComponentTransformBuilder.create()
                                                                                        .source(HandInteractionRecipe.SOURCE_INGREDIENT_A)
                                                                                        .removeAll(true)
                                                                                        .keep(BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(DataComponents.DYED_COLOR))
                                                                        )
                                                                        .build()
                                                        )
                                                        .build()
                                                        .withHooks(List.of(
                                                                HookBuilder.create()
                                                                        .id(JolCraft.location(JolCraftRecipeHookIds.DEEPSLATE_COMPASS))
                                                                        .build()
                                                        ))
                                        )
                                        .build()
                        )
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.METAL_HIT)
                                        .volume(1.0F)
                                        .pitch(1.4F)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.METAL_HIT)
                                        .volume(0.4F)
                                        .pitch(1.6F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }

    private ResourceKey<Recipe<?>> recipeKey(String file) {
        return ResourceKey.create(
                Registries.RECIPE,
                JolCraft.location(
                        JolCraftStrings.slashed(folder(), file)
                )
        );
    }
}