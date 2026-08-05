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
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.FermentingCauldronRecipeBuilder;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
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
        yeast(
                output,
                tracking,
                JolCraftItems.YEAST_CULTURE.get()
        );

        finalizeTag(
                output,
                tracking,
                Items.SUGAR,
                JolCraftTags.Items.YEAST_BREW,
                1200,
                3,
                BrewingColors.YEAST,
                FermentingCauldronRecipe.OutputFluid.YEAST
        );

        tannin(
                output,
                tracking,
                Items.COCOA_BEANS,
                DwarvenBrewAge.MATURED
        );

        tannin(
                output,
                tracking,
                Items.CHORUS_FRUIT,
                DwarvenBrewAge.VINTAGE
        );

        finalizeTag(
                output,
                tracking,
                Items.HONEY_BOTTLE,
                JolCraftTags.Items.TANNIN_BREW,
                1200,
                3,
                BrewingColors.TANNIN,
                FermentingCauldronRecipe.OutputFluid.TANNIN
        );

        brew(
                output,
                tracking,
                JolCraftItems.BARLEY_MALT.get(),
                null,
                5,
                5,
                BrewingColors.UNFINISHED_DWARVEN_BREW
        );

        brewTannin(
                output,
                tracking
        );

        brewEffect(
                output,
                tracking,
                JolCraftItems.ASGARNIAN_HOPS.get(),
                JolCraftTags.Items.UNFINISHED_BREW,
                BrewingColors.ASGARNIAN_HOPS,
                MobEffects.HEALTH_BOOST,
                600,
                0
        );

        brewEffect(
                output,
                tracking,
                JolCraftItems.DUSKHOLD_HOPS.get(),
                JolCraftTags.Items.UNFINISHED_BREW,
                BrewingColors.DUSKHOLD_HOPS,
                MobEffects.ABSORPTION,
                600,
                0
        );

        brewEffect(
                output,
                tracking,
                JolCraftItems.KRANDONIAN_HOPS.get(),
                JolCraftTags.Items.UNFINISHED_BREW,
                BrewingColors.KRANDONIAN_HOPS,
                MobEffects.DAMAGE_BOOST,
                600,
                0
        );

        brewEffect(
                output,
                tracking,
                JolCraftItems.YANILLIAN_HOPS.get(),
                JolCraftTags.Items.UNFINISHED_BREW,
                BrewingColors.YANILLIAN_HOPS,
                JolCraftEffects.BULWARK,
                600,
                0
        );

        finalizeTag(
                output,
                tracking,
                JolCraftItems.YEAST.get(),
                JolCraftTags.Items.UNFINISHED_BREW,
                6000,
                60,
                BrewingColors.DWARVEN_BREW,
                FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW
        );
    }

    private void yeast(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient
    ) {
        fermenting(
                output,
                tracking,
                recipeId(
                        ingredient,
                        JolCraftDictionary.FERMENT,
                        (ItemLike) null
                ),
                ingredient,
                null,
                5,
                5,
                BrewingColors.UNFINISHED_YEAST,
                FermentingCauldronRecipe.OutputFluid.YEAST,
                false,
                null,
                null,
                null
        );
    }

    private void tannin(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @NotNull DwarvenBrewAge maxBrewAge
    ) {
        fermenting(
                output,
                tracking,
                recipeId(
                        ingredient,
                        JolCraftDictionary.FERMENT,
                        (ItemLike) null
                ),
                ingredient,
                null,
                5,
                5,
                BrewingColors.UNFINISHED_TANNIN,
                FermentingCauldronRecipe.OutputFluid.TANNIN,
                false,
                null,
                maxBrewAge,
                null
        );
    }

    private void brew(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @Nullable ItemLike lastIngredient,
            int brewTicks,
            int bubbleTicks,
            int brewColor
    ) {
        fermenting(
                output,
                tracking,
                recipeId(
                        ingredient,
                        JolCraftDictionary.FERMENT,
                        lastIngredient
                ),
                ingredient,
                itemInput(lastIngredient),
                brewTicks,
                bubbleTicks,
                brewColor,
                FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW,
                false,
                null,
                null,
                null
        );
    }

    private void brewTannin(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking
    ) {
        fermenting(
                output,
                tracking,
                recipeId(
                        JolCraftItems.TANNIN.get(),
                        JolCraftDictionary.FERMENT,
                        JolCraftTags.Items.UNFINISHED_BREW
                ),
                JolCraftItems.TANNIN.get(),
                ItemInput.tag(
                        JolCraftTags.Items.UNFINISHED_BREW
                ),
                5,
                5,
                BrewingColors.TANNIN,
                FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW,
                false,
                null,
                null,
                null
        );
    }

    private void brewEffect(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @NotNull TagKey<Item> lastIngredient,
            int brewColor,
            @NotNull Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        fermenting(
                output,
                tracking,
                recipeId(
                        ingredient,
                        JolCraftDictionary.EFFECT,
                        lastIngredient
                ),
                ingredient,
                ItemInput.tag(lastIngredient),
                5,
                5,
                brewColor,
                FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW,
                false,
                null,
                null,
                effect(
                        effect,
                        duration,
                        amplifier
                )
        );
    }

    private void finalizeTag(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @NotNull TagKey<Item> lastIngredient,
            int brewTicks,
            int bubbleTicks,
            int brewColor,
            @NotNull FermentingCauldronRecipe.OutputFluid outputFluid
    ) {
        fermenting(
                output,
                tracking,
                recipeId(
                        ingredient,
                        JolCraftDictionary.FINALIZE,
                        lastIngredient
                ),
                ingredient,
                ItemInput.tag(lastIngredient),
                brewTicks,
                bubbleTicks,
                brewColor,
                outputFluid,
                true,
                null,
                null,
                null
        );
    }

    private void fermenting(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull String id,
            @NotNull ItemLike ingredient,
            @Nullable ItemInput lastIngredient,
            int brewTicks,
            int bubbleTicks,
            int brewColor,
            @NotNull FermentingCauldronRecipe.OutputFluid outputFluid,
            boolean finalizeBrew,
            @Nullable Float brewingSpeed,
            @Nullable DwarvenBrewAge maxBrewAge,
            @Nullable EffectOutput effect
    ) {
        FermentingCauldronRecipeBuilder builder =
                FermentingCauldronRecipeBuilder.create()
                        .id(id)
                        .ingredient(
                                ItemInput.item(ingredient)
                        )
                        .brewTicks(brewTicks)
                        .bubbleTicks(bubbleTicks)
                        .brewColor(brewColor)
                        .outputFluid(outputFluid)
                        .finalizeBrew(finalizeBrew);

        if (lastIngredient == null) {
            builder.noLastIngredient();
        } else {
            builder.lastIngredient(lastIngredient);
        }

        if (brewingSpeed != null) {
            builder.brewingSpeed(brewingSpeed);
        }

        if (maxBrewAge != null) {
            builder.maxBrewAge(maxBrewAge);
        }

        if (effect == null) {
            builder.noEffect();
        } else {
            builder.effect(effect);
        }

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private static @Nullable ItemInput itemInput(
            @Nullable ItemLike item
    ) {
        return item == null
                ? null
                : ItemInput.item(item);
    }

    private static EffectOutput effect(
            @NotNull Holder<MobEffect> effect,
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
            @NotNull ItemLike ingredient,
            @NotNull String operation,
            @Nullable ItemLike relatedItem
    ) {
        return recipeId(
                ingredient,
                operation,
                relatedItem == null
                        ? "empty"
                        : itemPath(relatedItem)
        );
    }

    private static String recipeId(
            @NotNull ItemLike ingredient,
            @NotNull String operation,
            @NotNull TagKey<Item> relatedTag
    ) {
        return recipeId(
                ingredient,
                operation,
                relatedTag.location()
                        .getPath()
                        .replace('/', '_')
        );
    }

    private static String recipeId(
            @NotNull ItemLike ingredient,
            @NotNull String operation,
            @NotNull String suffix
    ) {
        return itemPath(ingredient)
                + "_"
                + operation
                + "_"
                + suffix;
    }

    private static String itemPath(
            @NotNull ItemLike item
    ) {
        return BuiltInRegistries.ITEM
                .getKey(item.asItem())
                .getPath()
                .replace('/', '_');
    }
}
