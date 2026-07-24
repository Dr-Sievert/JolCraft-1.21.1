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
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.FermentingCauldronRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
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
                0x40B14A
        );

        fermentingExtract(
                output,
                tracking,
                Items.GLASS_BOTTLE,
                Items.SUGAR,
                JolCraftItems.YEAST.get()
        );

        fermenting(
                output,
                tracking,
                JolCraftItems.BARLEY_MALT.get(),
                null,
                20,
                5,
                0x805D37
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.ASGARNIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x91706E,
                MobEffects.HEALTH_BOOST,
                600,
                0
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.DUSKHOLD_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x817788,
                MobEffects.NIGHT_VISION,
                600,
                0
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.KRANDONIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x6E918F,
                MobEffects.DAMAGE_BOOST,
                600,
                0
        );

        fermentingEffect(
                output,
                tracking,
                JolCraftItems.YANILLIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x54832E,
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
                0x9A652B
        );

        fermentingExtract(
                output,
                tracking,
                JolCraftItems.GLASS_MUG.get(),
                JolCraftItems.YEAST.get(),
                JolCraftItems.DWARVEN_BREW.get()
        );
    }

    private void fermenting(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike ingredient,
            @Nullable ItemLike lastIngredient,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        FermentingCauldronRecipeBuilder builder =
                FermentingCauldronRecipeBuilder.create()
                        .id(recipeId(
                                ingredient,
                                "ferment",
                                lastIngredient
                        ))
                        .ingredient(ItemInput.item(ingredient))
                        .brewTicks(brewTicks)
                        .bubbleTicks(bubbleTicks)
                        .brewColor(argb(colorRgb))
                        .finalizeBrew(false)
                        .noEffect()
                        .noExtract();

        if (lastIngredient != null) {
            builder.lastIngredient(
                    ItemInput.item(lastIngredient)
            );
        } else {
            builder.noLastIngredient();
        }

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
            int colorRgb
    ) {
        FermentingCauldronRecipeBuilder builder =
                FermentingCauldronRecipeBuilder.create()
                        .id(recipeId(
                                ingredient,
                                "finalize",
                                lastIngredientTag
                        ))
                        .ingredient(ItemInput.item(ingredient))
                        .brewTicks(brewTicks)
                        .bubbleTicks(bubbleTicks)
                        .brewColor(argb(colorRgb))
                        .finalizeBrew(true)
                        .noEffect()
                        .noExtract();

        if (lastIngredientTag != null) {
            builder.lastIngredient(
                    ItemInput.tag(lastIngredientTag)
            );
        } else {
            builder.noLastIngredient();
        }

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
            int colorRgb,
            @NotNull Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        FermentingCauldronRecipeBuilder builder =
                FermentingCauldronRecipeBuilder.create()
                        .id(recipeId(
                                ingredient,
                                "effect",
                                lastIngredientTag
                        ))
                        .ingredient(ItemInput.item(ingredient))
                        .brewTicks(brewTicks)
                        .bubbleTicks(bubbleTicks)
                        .brewColor(argb(colorRgb))
                        .finalizeBrew(false)
                        .effect(effect(
                                effect,
                                duration,
                                amplifier
                        ))
                        .noExtract();

        if (lastIngredientTag != null) {
            builder.lastIngredient(
                    ItemInput.tag(lastIngredientTag)
            );
        } else {
            builder.noLastIngredient();
        }

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private void fermentingExtract(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike extractor,
            @Nullable ItemLike lastIngredient,
            @NotNull ItemLike result
    ) {
        FermentingCauldronRecipeBuilder builder =
                FermentingCauldronRecipeBuilder.create()
                        .id(recipeId(
                                extractor,
                                "extract",
                                result
                        ))
                        .ingredient(ItemInput.item(extractor))
                        .finalizeBrew(false)
                        .noEffect()
                        .noBrewColor()
                        .extract(itemResult(result));

        if (lastIngredient != null) {
            builder.lastIngredient(
                    ItemInput.item(lastIngredient)
            );
        } else {
            builder.noLastIngredient();
        }

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private static int argb(int colorRgb) {
        return 0xFF000000
                | (colorRgb & 0xFFFFFF);
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

    private static ItemOutput itemResult(
            ItemLike item
    ) {
        return ItemOutput.item(
                LootItem.lootTableItem(item)
        );
    }

    private static String recipeId(
            ItemLike ingredient,
            String operation,
            @Nullable ItemLike relatedItem
    ) {
        String suffix = relatedItem == null
                ? "empty"
                : itemPath(relatedItem);

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
        String suffix = relatedTag == null
                ? "empty"
                : relatedTag.location()
                .getPath()
                .replace('/', '_');

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
        return itemPath(ingredient)
                + "_"
                + operation
                + "_"
                + suffix;
    }

    private static String itemPath(
            ItemLike item
    ) {
        return BuiltInRegistries.ITEM
                .getKey(item.asItem())
                .getPath()
                .replace('/', '_');
    }
}