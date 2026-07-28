package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.FermentingCauldronRecipeBuilder;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public record FermentingCauldronRecipesSubProvider(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public FermentingCauldronRecipesSubProvider(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
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
        return JolCraftBlockIds.FERMENTING_CAULDRON;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        fermentingFinalize(
                output,
                tracking,
                Items.SUGAR,
                null,
                1200,
                3,
                BrewingColors.YEAST,
                FermentingCauldronRecipe.OutputFluid.YEAST
        );

        fermenting(
                output,
                tracking,
                JolCraftItems.BARLEY_MALT.get(),
                null,
                5,
                5,
                BrewingColors.UNFINISHED_DWARVEN_BREW
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.ASGARNIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                5,
                5,
                BrewingColors.ASGARNIAN_HOPS,
                MobEffects.HEALTH_BOOST,
                600,
                0
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.DUSKHOLD_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                5,
                5,
                BrewingColors.DUSKHOLD_HOPS,
                MobEffects.ABSORPTION,
                600,
                0
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.KRANDONIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                5,
                5,
                BrewingColors.KRANDONIAN_HOPS,
                MobEffects.DAMAGE_BOOST,
                600,
                0
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.YANILLIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                5,
                5,
                BrewingColors.YANILLIAN_HOPS,
                MobEffects.DAMAGE_RESISTANCE,
                600,
                0
        );

        fermentingFinalize(
                output,
                tracking,
                JolCraftItems.YEAST.get(),
                JolCraftTags.Items.HOPS,
                6000,
                60,
                BrewingColors.DWARVEN_BREW,
                FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW
        );
    }

    private void fermenting(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @Nullable ItemLike lastIngredient,
            int brewTicks,
            int bubbleTicks,
            int brewColor
    ) {
        FermentingCauldronRecipeBuilder builder =
                applyLastIngredient(
                        baseBuilder(
                                ingredient,
                                recipeId(
                                        ingredient,
                                        "ferment",
                                        lastIngredient
                                ),
                                brewTicks,
                                bubbleTicks,
                                brewColor
                        )
                                .dwarvenBrew()
                                .finalizeBrew(
                                        false
                                )
                                .noEffect(),
                        lastIngredient == null
                                ? null
                                : ItemInput.item(
                                lastIngredient
                        )
                );

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private void fermentingFinalize(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @Nullable TagKey<Item> lastIngredientTag,
            int brewTicks,
            int bubbleTicks,
            int brewColor,
            @NotNull FermentingCauldronRecipe.OutputFluid outputFluid
    ) {
        FermentingCauldronRecipeBuilder builder =
                applyLastIngredient(
                        baseBuilder(
                                ingredient,
                                recipeId(
                                        ingredient,
                                        "finalize",
                                        lastIngredientTag
                                ),
                                brewTicks,
                                bubbleTicks,
                                brewColor
                        )
                                .outputFluid(
                                        outputFluid
                                )
                                .finalizeBrew(
                                        true
                                )
                                .noEffect(),
                        lastIngredientTag == null
                                ? null
                                : ItemInput.tag(
                                lastIngredientTag
                        )
                );

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private void fermentingEffect(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @Nullable TagKey<Item> lastIngredientTag,
            int brewTicks,
            int bubbleTicks,
            int brewColor,
            @NotNull Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        FermentingCauldronRecipeBuilder builder =
                applyLastIngredient(
                        baseBuilder(
                                ingredient,
                                recipeId(
                                        ingredient,
                                        "effect",
                                        lastIngredientTag
                                ),
                                brewTicks,
                                bubbleTicks,
                                brewColor
                        )
                                .dwarvenBrew()
                                .finalizeBrew(
                                        false
                                )
                                .effect(
                                        effect(
                                                effect,
                                                duration,
                                                amplifier
                                        )
                                ),
                        lastIngredientTag == null
                                ? null
                                : ItemInput.tag(
                                lastIngredientTag
                        )
                );

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private static FermentingCauldronRecipeBuilder baseBuilder(
            ItemLike ingredient,
            String id,
            int brewTicks,
            int bubbleTicks,
            int brewColor
    ) {
        return FermentingCauldronRecipeBuilder.create()
                .id(
                        id
                )
                .ingredient(
                        ItemInput.item(
                                ingredient
                        )
                )
                .brewTicks(
                        brewTicks
                )
                .bubbleTicks(
                        bubbleTicks
                )
                .brewColor(
                        brewColor
                );
    }

    private static FermentingCauldronRecipeBuilder applyLastIngredient(
            FermentingCauldronRecipeBuilder builder,
            @Nullable ItemInput lastIngredient
    ) {
        return lastIngredient == null
                ? builder.noLastIngredient()
                : builder.lastIngredient(
                lastIngredient
        );
    }

    private static EffectOutput effect(
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        return EffectOutput.of(
                new MobEffectInstance(
                        effect,
                        duration,
                        amplifier
                )
        );
    }

    private static String recipeId(
            ItemLike ingredient,
            String operation,
            @Nullable ItemLike relatedItem
    ) {
        String suffix =
                relatedItem == null
                        ? "empty"
                        : itemPath(
                        relatedItem
                );

        return recipeId(
                ingredient,
                operation,
                suffix
        );
    }

    private static String recipeId(
            ItemLike ingredient,
            String operation,
            @Nullable TagKey<Item> relatedTag
    ) {
        String suffix =
                relatedTag == null
                        ? "empty"
                        : relatedTag.location()
                        .getPath()
                        .replace(
                                '/',
                                '_'
                        );

        return recipeId(
                ingredient,
                operation,
                suffix
        );
    }

    private static String recipeId(
            ItemLike ingredient,
            String operation,
            String suffix
    ) {
        return itemPath(
                ingredient
        )
                + "_"
                + operation
                + "_"
                + suffix;
    }

    private static String itemPath(
            ItemLike item
    ) {
        return BuiltInRegistries.ITEM
                .getKey(
                        item.asItem()
                )
                .getPath()
                .replace(
                        '/',
                        '_'
                );
    }
}