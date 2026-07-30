package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record SpannerHandInteractions(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public SpannerHandInteractions(
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
        return JolCraftDictionary.SPANNER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.GENERAL_SALVAGE,
                SoundEvents.ITEM_BREAK,
                0.75F,
                1.25F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.TEXTILE_SALVAGE,
                SoundEvents.WOOL_BREAK,
                0.75F,
                1.25F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.REDSTONE_SALVAGE,
                SoundEvents.ITEM_BREAK,
                0.75F,
                1.45F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.IRON_SALVAGE,
                SoundEvents.METAL_BREAK,
                0.75F,
                1.60F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.DEEPSLATE_SALVAGE,
                SoundEvents.DEEPSLATE_BREAK,
                0.75F,
                1.25F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.GOLD_SALVAGE,
                SoundEvents.METAL_BREAK,
                0.75F,
                1.70F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.MITHRIL_SALVAGE,
                SoundEvents.NETHERITE_BLOCK_BREAK,
                0.75F,
                1.25F
        );
    }

    private void salvagePool(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull TagKey<Item> salvageTag,
            @NotNull SoundEvent successEvent,
            float successVolume,
            float successPitch
    ) {
        emit(
                output,
                tracking,
                HandInteractionRecipeBuilder.create()
                        .id(recipeId(salvageTag))
                        .ingredientA(
                                ItemInput.tag(
                                        JolCraftTags.Items.SPANNERS
                                )
                        )
                        .actionA(
                                new ItemInputAction(
                                        ItemInputAction.Type.DAMAGE,
                                        1
                                )
                        )
                        .ingredientB(
                                ItemInput.tag(
                                        salvageTag
                                )
                        )
                        .actionB(
                                new ItemInputAction(
                                        ItemInputAction.Type.CONSUME,
                                        1
                                )
                        )
                        .output(
                                salvageOutput(salvageTag)
                        )
                        .successSound(
                                SoundOutput.sound(
                                        successEvent,
                                        SoundSource.PLAYERS,
                                        ConstantValue.exactly(successVolume),
                                        ConstantValue.exactly(successPitch)
                                )
                        )
                        .failSound(
                                SoundOutput.sound(
                                        SoundEvents.BOOK_PUT,
                                        SoundSource.PLAYERS,
                                        ConstantValue.exactly(1.0F),
                                        ConstantValue.exactly(1.0F)
                                )
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }

    private static @NotNull ItemOutput salvageOutput(
            @NotNull TagKey<Item> salvageTag
    ) {
        if (salvageTag.equals(
                JolCraftTags.Items.GENERAL_SALVAGE
        )) {
            return itemOutput(
                    1,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1
                    ),
                    empty(1)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.TEXTILE_SALVAGE
        )) {
            return itemOutput(
                    2,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            2,
                            60
                    ),
                    entry(
                            Items.STRING,
                            35
                    ),
                    entry(
                            Items.LEATHER,
                            15
                    ),
                    empty(1)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.REDSTONE_SALVAGE
        )) {
            return itemOutput(
                    2,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            3,
                            60
                    ),
                    entry(
                            Items.REDSTONE,
                            30
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            5
                    ),
                    empty(1)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.IRON_SALVAGE
        )) {
            return itemOutput(
                    3,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            4,
                            60
                    ),
                    entry(
                            Items.IRON_NUGGET,
                            2,
                            4,
                            50
                    ),
                    entry(
                            Items.IRON_INGOT,
                            15
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            10
                    ),
                    empty(1)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.DEEPSLATE_SALVAGE
        )) {
            return itemOutput(
                    2,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            4,
                            60
                    ),
                    entry(
                            JolCraftItems.DEEPSLATE_PLATE.get(),
                            15
                    ),
                    empty(1)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.GOLD_SALVAGE
        )) {
            return itemOutput(
                    3,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            5,
                            60
                    ),
                    entry(
                            Items.GOLD_NUGGET,
                            2,
                            4,
                            50
                    ),
                    entry(
                            Items.GOLD_INGOT,
                            15
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            20
                    ),
                    empty(1)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.MITHRIL_SALVAGE
        )) {
            return itemOutput(
                    3,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            10,
                            60
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            30
                    ),
                    entry(
                            JolCraftItems.MITHRIL_NUGGET.get(),
                            1,
                            4,
                            15
                    )
            );
        }

        throw new IllegalArgumentException(
                "Unsupported salvage tag: "
                        + salvageTag.location()
        );
    }

    private static @NotNull ItemOutput itemOutput(
            int rolls,
            LootPoolSingletonContainer.Builder<?>... entries
    ) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(rolls));

        for (LootPoolSingletonContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }

        return ItemOutput.of(pool);
    }

    private static @NotNull LootPoolSingletonContainer.Builder<?> entry(
            @NotNull ItemLike item,
            int weight
    ) {
        return LootItem.lootTableItem(item)
                .setWeight(weight);
    }

    private static @NotNull LootPoolSingletonContainer.Builder<?> entry(
            @NotNull ItemLike item,
            int minCount,
            int maxCount,
            int weight
    ) {
        LootPoolSingletonContainer.Builder<?> entry =
                LootItem.lootTableItem(item)
                        .setWeight(weight);

        entry.apply(
                SetItemCountFunction.setCount(
                        minCount == maxCount
                                ? ConstantValue.exactly(minCount)
                                : UniformGenerator.between(
                                minCount,
                                maxCount
                        )
                )
        );

        return entry;
    }

    private static @NotNull LootPoolSingletonContainer.Builder<?> empty(
            int weight
    ) {
        return  LootItem.lootTableItem(ItemStack.EMPTY.getItem()).setWeight(weight);
    }

    private static @NotNull String recipeId(
            @NotNull TagKey<Item> salvageTag
    ) {
        return salvageTag.location()
                .getPath()
                .replace('/', '_');
    }
}