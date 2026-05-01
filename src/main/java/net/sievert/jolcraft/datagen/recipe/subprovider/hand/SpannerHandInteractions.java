package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector.ItemIngredientBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.base.OutputsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolEntryBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record SpannerHandInteractions(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public SpannerHandInteractions(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return JolCraftDictionary.SPANNER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        salvagePool(output, tracking, JolCraftTags.Items.GENERAL_SALVAGE, SoundEvents.ITEM_BREAK, 0.75F, 1.25F);
        salvagePool(output, tracking, JolCraftTags.Items.TEXTILE_SALVAGE, SoundEvents.WOOL_BREAK, 0.75F, 1.25F);
        salvagePool(output, tracking, JolCraftTags.Items.REDSTONE_SALVAGE, SoundEvents.ITEM_BREAK, 0.75F, 1.45F);
        salvagePool(output, tracking, JolCraftTags.Items.IRON_SALVAGE, SoundEvents.METAL_BREAK, 0.75F, 1.60F);
        salvagePool(output, tracking, JolCraftTags.Items.DEEPSLATE_SALVAGE, SoundEvents.DEEPSLATE_BREAK, 0.75F, 1.25F);
        salvagePool(output, tracking, JolCraftTags.Items.GOLD_SALVAGE, SoundEvents.METAL_BREAK, 0.75F, 1.70F);
        salvagePool(output, tracking, JolCraftTags.Items.MITHRIL_SALVAGE, SoundEvents.NETHERITE_BLOCK_BREAK, 0.75F, 1.25F);
    }

    private void salvagePool(
            @NotNull RecipeOutput output,
            JolCraftDataTracking tracking,
            @NotNull TagKey<Item> salvageTag,
            @NotNull SoundEvent successEvent,
            float successVolume,
            float successPitch
    ) {
        emit(output, tracking,
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .selector(ItemIngredientBuilder.create().tag(JolCraftTags.Items.SPANNERS))
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.DAMAGE, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .selector(ItemIngredientBuilder.create().tag(salvageTag))
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(salvageOutputs(salvageTag))
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(successEvent)
                                        .volume(successVolume)
                                        .pitch(successPitch)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.BOOK_PUT)
                                        .volume(1.0F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }

    private static @NotNull Outputs salvageOutputs(@NotNull TagKey<Item> salvageTag) {
        if (salvageTag.equals(JolCraftTags.Items.GENERAL_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(1)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(1)
                                    )
                    )
                    .build();
        }

        if (salvageTag.equals(JolCraftTags.Items.TEXTILE_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(2)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(100)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1, 2)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(60)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.STRING, 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(35)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.LEATHER, 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(15)
                                    )
                    )
                    .build();
        }

        if (salvageTag.equals(JolCraftTags.Items.REDSTONE_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(2)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(100)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1, 3)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(60)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.REDSTONE, 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(30)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP_HEAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(5)
                                    )
                    )
                    .build();
        }

        if (salvageTag.equals(JolCraftTags.Items.IRON_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(3)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(100)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1, 4)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(60)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.IRON_NUGGET, 2, 4)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(50)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.IRON_INGOT, 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(15)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP_HEAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(10)
                                    )
                    )
                    .build();
        }

        if (salvageTag.equals(JolCraftTags.Items.DEEPSLATE_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(2)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(100)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1, 4)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(60)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.DEEPSLATE_PLATE.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(15)
                                    )
                    )
                    .build();
        }

        if (salvageTag.equals(JolCraftTags.Items.GOLD_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(3)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(100)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1, 5)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(60)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.GOLD_NUGGET, 2, 4)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(50)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(Items.GOLD_INGOT, 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(15)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP_HEAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(20)
                                    )
                    )
                    .build();
        }

        if (salvageTag.equals(JolCraftTags.Items.MITHRIL_SALVAGE)) {
            return OutputsBuilder.create()
                    .pool(
                            PoolBuilder.create()
                                    .rollsFixed(3)
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(100)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP.get().asItem(), 1, 10)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(60)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.SCRAP_HEAP.get().asItem(), 1)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(30)
                                    )
                                    .entry(
                                            PoolEntryBuilder.create()
                                                    .output(
                                                            ItemOutputBuilder.create()
                                                                    .result(JolCraftItems.MITHRIL_NUGGET.get().asItem(), 1, 4)
                                                                    .transforms(ItemTransforms.EMPTY)
                                                    )
                                                    .weight(15)
                                    )
                    )
                    .build();
        }

        return Outputs.EMPTY;
    }
}