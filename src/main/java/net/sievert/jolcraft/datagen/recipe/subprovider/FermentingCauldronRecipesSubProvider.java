package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.FermentingCauldronRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector.ItemIngredientBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("SameParameterValue")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingCauldronRecipesSubProvider implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftBlockIds.FERMENTING_CAULDRON;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {
        fermentingFinalize(
                executor,
                Items.SUGAR,
                null,
                1200,
                3,
                0x40B14A
        );

        fermentingExtract(
                executor,
                Items.GLASS_BOTTLE,
                Items.SUGAR,
                JolCraftItems.YEAST.get()
        );

        fermenting(
                executor,
                JolCraftItems.BARLEY_MALT.get(),
                null,
                20,
                5,
                0x805d37
        );

        fermentingEffect(
                executor,
                JolCraftItems.ASGARNIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x91706e,
                MobEffects.HEALTH_BOOST,
                6000,
                0
        );

        fermentingEffect(
                executor,
                JolCraftItems.DUSKHOLD_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x817788,
                MobEffects.NIGHT_VISION,
                6000,
                0
        );

        fermentingEffect(
                executor,
                JolCraftItems.KRANDONIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x6e918f,
                MobEffects.DAMAGE_BOOST,
                6000,
                0
        );

        fermentingEffect(
                executor,
                JolCraftItems.YANILLIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x54832e,
                MobEffects.MOVEMENT_SPEED,
                6000,
                0
        );

        fermentingFinalize(
                executor,
                JolCraftItems.YEAST.get(),
                JolCraftTags.Items.HOPS,
                6000,
                60,
                0x9A652B
        );

        fermentingExtract(
                executor,
                JolCraftItems.GLASS_MUG.get(),
                JolCraftItems.YEAST.get(),
                JolCraftItems.DWARVEN_BREW.get()
        );
    }

    private static void fermenting(
            RecipeEmissionExecutor executor,
            ItemLike ingredient,
            @Nullable ItemLike lastIngredient,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        FermentingCauldronRecipeBuilder builder = FermentingCauldronRecipeBuilder.create()
                .ingredient(ingredient)
                .brewTicks(brewTicks)
                .bubbleTicks(bubbleTicks)
                .brewColor(argb(colorRgb))
                .finalizeBrew(false)
                .noEffect()
                .noExtract();

        if (lastIngredient != null) {
            builder.lastIngredient(item(lastIngredient));
        } else {
            builder.noLastIngredient();
        }

        executor.emit(builder.buildValidated());
    }

    private static void fermentingFinalize(
            RecipeEmissionExecutor executor,
            ItemLike ingredient,
            @Nullable TagKey<Item> lastIngredientTag,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        FermentingCauldronRecipeBuilder builder = FermentingCauldronRecipeBuilder.create()
                .ingredient(ingredient)
                .brewTicks(brewTicks)
                .bubbleTicks(bubbleTicks)
                .brewColor(argb(colorRgb))
                .finalizeBrew(true)
                .noEffect()
                .noExtract();

        if (lastIngredientTag != null) {
            builder.lastIngredient(tag(lastIngredientTag));
        } else {
            builder.noLastIngredient();
        }

        executor.emit(builder.buildValidated());
    }

    private static void fermentingEffect(
            RecipeEmissionExecutor executor,
            ItemLike ingredient,
            @Nullable TagKey<Item> lastIngredientTag,
            int brewTicks,
            int bubbleTicks,
            int colorRgb,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        FermentingCauldronRecipeBuilder builder = FermentingCauldronRecipeBuilder.create()
                .ingredient(ingredient)
                .brewTicks(brewTicks)
                .bubbleTicks(bubbleTicks)
                .brewColor(argb(colorRgb))
                .finalizeBrew(false)
                .effect(new EffectOutput(effect, duration, amplifier, Output.EffectTarget.PLAYER))
                .noExtract();

        if (lastIngredientTag != null) {
            builder.lastIngredient(tag(lastIngredientTag));
        } else {
            builder.noLastIngredient();
        }

        executor.emit(builder.buildValidated());
    }

    private static void fermentingExtract(
            RecipeEmissionExecutor executor,
            ItemLike extractor,
            @Nullable ItemLike lastIngredient,
            ItemLike result
    ) {
        FermentingCauldronRecipeBuilder builder = FermentingCauldronRecipeBuilder.create()
                .ingredient(extractor)
                .finalizeBrew(false)
                .noEffect()
                .noBrewColor()
                .noBubbleTicks()
                .extract(result, 1);

        if (lastIngredient != null) {
            builder.lastIngredient(item(lastIngredient));
        } else {
            builder.noLastIngredient();
        }

        executor.emit(builder.buildValidated());
    }

    private static int argb(int colorRgb) {
        return 0xFF000000 | (colorRgb & 0xFFFFFF);
    }

    private static ItemSelector tag(TagKey<Item> tag) {
        return ItemSelector.of(
                ItemIngredientBuilder.create()
                        .tag(tag)
                        .build()
        );
    }

    private static ItemSelector item(ItemLike item) {
        return ItemSelector.of(
                ItemIngredientBuilder.create()
                        .item(item)
                        .build()
        );
    }
}