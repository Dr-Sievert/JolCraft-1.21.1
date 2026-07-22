package net.sievert.jolcraft.integration.jei.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.mixin.LootItemAccessor;
import net.sievert.jolcraft.mixin.LootPoolAccessor;
import net.sievert.jolcraft.mixin.LootPoolSingletonContainerAccessor;
import net.sievert.jolcraft.mixin.SetComponentsFunctionAccessor;
import net.sievert.jolcraft.mixin.SetItemCountFunctionAccessor;
import net.sievert.jolcraft.mixin.UniformGeneratorAccessor;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ItemOutputJeiTranslator {

    private ItemOutputJeiTranslator() {
    }

    public static @NotNull List<JeiItemOutcome> translate(
            @NotNull ItemOutput output
    ) {
        return translate(
                output.pool()
        );
    }

    public static @NotNull List<JeiItemOutcome> translate(
            @NotNull LootPool pool
    ) {
        LootPoolAccessor poolAccessor =
                (LootPoolAccessor) pool;

        List<LootPoolEntryContainer> entries =
                poolAccessor.jolcraft$getEntries();

        if (entries.isEmpty()) {
            return List.of();
        }

        int rolls =
                readConstantInt(
                        pool.getRolls(),
                        "loot-pool rolls"
                );

        List<LootPoolSingletonContainer> singletonEntries =
                new ArrayList<>(
                        entries.size()
                );

        int totalWeight = 0;

        for (LootPoolEntryContainer entry : entries) {
            if (!(entry instanceof LootPoolSingletonContainer singleton)) {
                throw unsupportedEntry(
                        entry
                );
            }

            if (!(singleton instanceof LootItem)) {
                throw unsupportedEntry(
                        singleton
                );
            }

            int weight =
                    ((LootPoolSingletonContainerAccessor) singleton)
                            .jolcraft$getWeight();

            if (weight <= 0) {
                continue;
            }

            singletonEntries.add(
                    singleton
            );

            totalWeight +=
                    weight;
        }

        if (totalWeight <= 0) {
            return List.of();
        }

        List<LootItemFunction> poolFunctions =
                poolAccessor.jolcraft$getFunctions();

        List<JeiItemOutcome> outcomes =
                new ArrayList<>(
                        singletonEntries.size()
                );

        for (LootPoolSingletonContainer singleton : singletonEntries) {
            LootItem lootItem =
                    (LootItem) singleton;

            LootPoolSingletonContainerAccessor singletonAccessor =
                    (LootPoolSingletonContainerAccessor) singleton;

            int weight =
                    singletonAccessor.jolcraft$getWeight();

            Holder<Item> item =
                    ((LootItemAccessor) lootItem)
                            .jolcraft$getItem();

            List<LootItemFunction> entryFunctions =
                    singletonAccessor.jolcraft$getFunctions();

            CountRange count =
                    applyCountFunctions(
                            new CountRange(
                                    1,
                                    1
                            ),
                            entryFunctions
                    );

            /*
             * Vanilla applies entry-level functions first,
             * followed by pool-level functions.
             */
            count =
                    applyCountFunctions(
                            count,
                            poolFunctions
                    );

            ItemStack displayStack =
                    new ItemStack(
                            item.value()
                    );

            displayStack =
                    applyDisplayFunctions(
                            displayStack,
                            entryFunctions
                    );

            displayStack =
                    applyDisplayFunctions(
                            displayStack,
                            poolFunctions
                    );

            /*
             * The display stack represents the output item and its components.
             * Quantity is displayed separately through minCount/maxCount.
             */
            displayStack.setCount(
                    1
            );

            outcomes.add(
                    new JeiItemOutcome(
                            displayStack,
                            count.min(),
                            count.max(),
                            weight,
                            totalWeight,
                            rolls
                    )
            );
        }

        return List.copyOf(
                outcomes
        );
    }

    private static @NotNull CountRange applyCountFunctions(
            @NotNull CountRange current,
            @NotNull List<LootItemFunction> functions
    ) {
        CountRange result =
                current;

        for (LootItemFunction function : functions) {
            if (!(function instanceof SetItemCountFunction setCount)) {
                continue;
            }

            SetItemCountFunctionAccessor accessor =
                    (SetItemCountFunctionAccessor) setCount;

            CountRange value =
                    readNumberRange(
                            accessor.jolcraft$getValue()
                    );

            if (accessor.jolcraft$isAdd()) {
                result =
                        new CountRange(
                                result.min()
                                        + value.min(),
                                result.max()
                                        + value.max()
                        );
            } else {
                result =
                        value;
            }
        }

        return result;
    }

    private static @NotNull ItemStack applyDisplayFunctions(
            @NotNull ItemStack stack,
            @NotNull List<LootItemFunction> functions
    ) {
        ItemStack result =
                stack;

        for (LootItemFunction function : functions) {
            if (function instanceof SetItemCountFunction) {
                continue;
            }

            if (function instanceof SetComponentsFunction setComponents) {
                DataComponentPatch components =
                        ((SetComponentsFunctionAccessor) setComponents)
                                .jolcraft$getComponents();

                result.applyComponentsAndValidate(
                        components
                );

                continue;
            }

            throw new IllegalArgumentException(
                    "Unsupported display loot function for JEI translation: "
                            + function.getClass()
                            .getName()
            );
        }

        return result;
    }

    private static @NotNull CountRange readNumberRange(
            @NotNull NumberProvider provider
    ) {
        if (provider instanceof ConstantValue constant) {
            int value =
                    readConstantInt(
                            constant,
                            "item count"
                    );

            return new CountRange(
                    value,
                    value
            );
        }

        if (provider instanceof UniformGenerator uniform) {
            UniformGeneratorAccessor accessor =
                    (UniformGeneratorAccessor) (Object) uniform;

            int min =
                    readConstantInt(
                            accessor.jolcraft$getMin(),
                            "uniform minimum"
                    );

            int max =
                    readConstantInt(
                            accessor.jolcraft$getMax(),
                            "uniform maximum"
                    );

            return new CountRange(
                    min,
                    max
            );
        }

        throw new IllegalArgumentException(
                "Unsupported number provider for JEI translation: "
                        + provider.getClass()
                        .getName()
        );
    }

    private static int readConstantInt(
            @NotNull NumberProvider provider,
            @NotNull String description
    ) {
        if (!(provider instanceof ConstantValue(float value))) {
            throw new IllegalArgumentException(
                    "JEI translation currently requires constant "
                            + description
                            + ", found "
                            + provider.getClass()
                            .getName()
            );
        }

        if (value != Math.floor(
                value
        )) {
            throw new IllegalArgumentException(
                    "Expected an integer "
                            + description
                            + ", found "
                            + value
            );
        }

        return (int) value;
    }

    private static @NotNull IllegalArgumentException unsupportedEntry(
            @NotNull LootPoolEntryContainer entry
    ) {
        return new IllegalArgumentException(
                "Unsupported loot entry for JEI translation: "
                        + entry.getClass()
                        .getName()
        );
    }

    private record CountRange(
            int min,
            int max
    ) {

        private CountRange {
            if (min < 0
                    || max < min) {
                throw new IllegalArgumentException(
                        "Invalid count range: "
                                + min
                                + "-"
                                + max
                );
            }
        }
    }
}