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
                3,
                6,
                0.75F,
                1.25F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.TEXTILE_SALVAGE,
                SoundEvents.WOOL_BREAK,
                1,
                2,
                0.75F,
                1.25F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.REDSTONE_SALVAGE,
                SoundEvents.ITEM_BREAK,
                6,
                9,
                0.75F,
                1.45F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.IRON_SALVAGE,
                SoundEvents.METAL_BREAK,
                9,
                15,
                0.75F,
                1.60F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.DEEPSLATE_SALVAGE,
                SoundEvents.DEEPSLATE_BREAK,
                12,
                18,
                0.75F,
                1.25F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.GOLD_SALVAGE,
                SoundEvents.METAL_BREAK,
                6,
                12,
                0.75F,
                1.70F
        );

        salvagePool(
                output,
                tracking,
                JolCraftTags.Items.MITHRIL_SALVAGE,
                SoundEvents.NETHERITE_BLOCK_BREAK,
                36,
                50,
                0.75F,
                1.25F
        );
    }

    private void salvagePool(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull TagKey<Item> salvageTag,
            @NotNull SoundEvent successEvent,
            int minDamage,
            int maxDamage,
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
                                        minDamage, maxDamage
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
                    empty(19)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.TEXTILE_SALVAGE
        )) {
            return itemOutput(
                    1,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            2,
                            1
                    ),
                    entry(
                            Items.STRING,
                            4
                    ),
                    empty(15)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.REDSTONE_SALVAGE
        )) {
            return itemOutput(
                    1,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            2,
                            2
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            1
                    ),
                    entry(
                            Items.REDSTONE,
                            5
                    ),
                    empty(12)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.IRON_SALVAGE
        )) {
            return itemOutput(
                    2,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            3,
                            2
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            1
                    ),
                    entry(
                            Items.IRON_NUGGET,
                            2,
                            4,
                            2
                    ),
                    entry(
                            Items.IRON_INGOT,
                            1
                    ),
                    empty(14)
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
                            3,
                            2
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            1
                    ),
                    entry(
                            JolCraftItems.DEEPSLATE_PLATE.get(),
                            1
                    ),
                    empty(16)
            );
        }

        if (salvageTag.equals(
                JolCraftTags.Items.GOLD_SALVAGE
        )) {
            return itemOutput(
                    2,
                    entry(
                            JolCraftItems.SCRAP.get(),
                            1,
                            3,
                            2
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            1
                    ),
                    entry(
                            Items.GOLD_NUGGET,
                            2,
                            4,
                            2
                    ),
                    entry(
                            Items.GOLD_INGOT,
                            1
                    ),
                    empty(14)
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
                            5,
                            3
                    ),
                    entry(
                            JolCraftItems.SCRAP_HEAP.get(),
                            2
                    ),
                    entry(
                            JolCraftItems.MITHRIL_NUGGET.get(),
                            1,
                            4,
                            1
                    ),
                    empty(14)
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