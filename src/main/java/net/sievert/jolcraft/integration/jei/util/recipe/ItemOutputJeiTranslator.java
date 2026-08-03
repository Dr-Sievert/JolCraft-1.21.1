package net.sievert.jolcraft.integration.jei.util.recipe;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.sievert.jolcraft.mixin.LootItemAccessor;
import net.sievert.jolcraft.mixin.LootItemConditionalFunctionAccessor;
import net.sievert.jolcraft.mixin.LootPoolAccessor;
import net.sievert.jolcraft.mixin.LootPoolEntryContainerAccessor;
import net.sievert.jolcraft.mixin.LootPoolSingletonContainerAccessor;
import net.sievert.jolcraft.mixin.LootTableAccessor;
import net.sievert.jolcraft.mixin.NestedLootTableAccessor;
import net.sievert.jolcraft.mixin.SetComponentsFunctionAccessor;
import net.sievert.jolcraft.mixin.SetItemCountFunctionAccessor;
import net.sievert.jolcraft.mixin.TagEntryAccessor;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ItemOutputJeiTranslator {

    private ItemOutputJeiTranslator() {}

    @FunctionalInterface
    public interface LootTableResolver {

        @NotNull LootTable resolve(
                @NotNull ResourceKey<LootTable> key
        );
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
        return translate(
                pool,
                List.of()
        );
    }

    public static @NotNull List<JeiItemOutcome> translate(
            @NotNull LootPool pool,
            @NotNull List<LootItemFunction> tableFunctions
    ) {
        return translatePool(
                pool,
                tableFunctions,
                null,
                new LinkedHashSet<>()
        );
    }

    public static @NotNull List<JeiItemOutcome> translate(
            @NotNull LootTable table,
            @NotNull LootTableResolver resolver
    ) {
        Objects.requireNonNull(
                table,
                "table"
        );

        Objects.requireNonNull(
                resolver,
                "resolver"
        );

        return translateTable(
                table,
                resolver,
                new LinkedHashSet<>()
        );
    }

    private static @NotNull List<JeiItemOutcome> translateTable(
            @NotNull LootTable table,
            @NotNull LootTableResolver resolver,
            @NotNull Set<ResourceKey<LootTable>> activeReferences
    ) {
        LootTableAccessor accessor =
                (LootTableAccessor) table;

        List<JeiItemOutcome> outcomes =
                new ArrayList<>();

        for (LootPool pool :
                accessor.jolcraft$getPools()) {
            outcomes.addAll(
                    translatePool(
                            pool,
                            accessor.jolcraft$getFunctions(),
                            resolver,
                            activeReferences
                    )
            );
        }

        return List.copyOf(outcomes);
    }

    private static @NotNull List<JeiItemOutcome> translatePool(
            @NotNull LootPool pool,
            @NotNull List<LootItemFunction> tableFunctions,
            @Nullable LootTableResolver resolver,
            @NotNull Set<ResourceKey<LootTable>> activeReferences
    ) {
        LootPoolAccessor poolAccessor =
                (LootPoolAccessor) pool;

        requireNoConditions(
                poolAccessor.jolcraft$getConditions(),
                "loot pool"
        );

        int bonusRolls =
                JeiNumberRangeTranslator.requireConstantInt(
                        poolAccessor.jolcraft$getBonusRolls(),
                        "loot-pool bonus rolls"
                );

        if (bonusRolls != 0) {
            throw new IllegalArgumentException(
                    "JEI translation does not support loot-pool bonus rolls"
            );
        }

        List<LootPoolEntryContainer> entries =
                poolAccessor.jolcraft$getEntries();

        if (entries.isEmpty()) {
            return List.of();
        }

        JeiNumberRangeTranslator.NumberRange rolls =
                JeiNumberRangeTranslator.translate(
                        pool.getRolls()
                );

        if (rolls.min() <= 0) {
            throw new IllegalArgumentException(
                    "loot-pool rolls must be positive"
            );
        }

        List<LootItemFunction> poolFunctions =
                poolAccessor.jolcraft$getFunctions();

        requireUnconditionalFunctions(
                poolFunctions,
                "loot pool"
        );

        requireUnconditionalFunctions(
                tableFunctions,
                "loot table"
        );

        List<PreparedEntry> preparedEntries =
                new ArrayList<>();

        int totalWeight = 0;

        for (LootPoolEntryContainer entry : entries) {
            requireNoConditions(
                    ((LootPoolEntryContainerAccessor) entry)
                            .jolcraft$getConditions(),
                    "loot entry"
            );

            if (!(entry instanceof LootPoolSingletonContainer singleton)) {
                throw unsupportedEntry(entry);
            }

            LootPoolSingletonContainerAccessor singletonAccessor =
                    (LootPoolSingletonContainerAccessor) singleton;

            if (singletonAccessor.jolcraft$getQuality() != 0) {
                throw new IllegalArgumentException(
                        "JEI translation does not support luck-dependent loot-entry quality"
                );
            }

            List<LootItemFunction> entryFunctions =
                    singletonAccessor.jolcraft$getFunctions();

            requireUnconditionalFunctions(
                    entryFunctions,
                    "loot entry"
            );

            int weight =
                    singletonAccessor.jolcraft$getWeight();

            if (weight <= 0) {
                continue;
            }

            List<Item> items =
                    resolveDirectItems(singleton);

            int selectionCount =
                    items.isEmpty()
                            ? 1
                            : items.size();

            totalWeight =
                    addExactWeight(
                            totalWeight,
                            multiplyExactWeight(
                                    weight,
                                    selectionCount
                            )
                    );

            preparedEntries.add(
                    new PreparedEntry(
                            singleton,
                            entryFunctions,
                            weight,
                            items
                    )
            );
        }

        if (preparedEntries.isEmpty()
                || totalWeight <= 0) {
            return List.of();
        }

        List<JeiItemOutcome> outcomes =
                new ArrayList<>();

        for (PreparedEntry prepared : preparedEntries) {
            LootPoolSingletonContainer entry =
                    prepared.entry();

            if (entry instanceof EmptyLootItem) {
                continue;
            }

            if (entry instanceof NestedLootTable nested) {
                if (resolver == null) {
                    throw new IllegalArgumentException(
                            "JEI translation requires a loot-table resolver for nested loot entries"
                    );
                }

                List<JeiItemOutcome> nestedOutcomes =
                        translateNested(
                                nested,
                                resolver,
                                activeReferences
                        );

                for (JeiItemOutcome nestedOutcome :
                        nestedOutcomes) {
                    CountRange count =
                            applyAllCountFunctions(
                                    new CountRange(
                                            nestedOutcome.minCount(),
                                            nestedOutcome.maxCount()
                                    ),
                                    prepared.functions(),
                                    poolFunctions,
                                    tableFunctions
                            );

                    ItemStack displayStack =
                            nestedOutcome.stack().copy();

                    applyAllDisplayFunctions(
                            displayStack,
                            prepared.functions(),
                            poolFunctions,
                            tableFunctions
                    );

                    displayStack.setCount(1);

                    WeightRatio combinedWeight =
                            combineWeightRatios(
                                    prepared.weight(),
                                    totalWeight,
                                    nestedOutcome.weight(),
                                    nestedOutcome.totalWeight()
                            );

                    outcomes.add(
                            new JeiItemOutcome(
                                    displayStack,
                                    count.min(),
                                    count.max(),
                                    combinedWeight.weight(),
                                    combinedWeight.totalWeight(),
                                    multiplyRolls(
                                            rolls.min(),
                                            nestedOutcome.minRolls()
                                    ),
                                    multiplyRolls(
                                            rolls.max(),
                                            nestedOutcome.maxRolls()
                                    )
                            )
                    );
                }

                continue;
            }

            for (Item item : prepared.items()) {
                CountRange count =
                        applyAllCountFunctions(
                                new CountRange(1, 1),
                                prepared.functions(),
                                poolFunctions,
                                tableFunctions
                        );

                ItemStack displayStack =
                        new ItemStack(item);

                applyAllDisplayFunctions(
                        displayStack,
                        prepared.functions(),
                        poolFunctions,
                        tableFunctions
                );

                displayStack.setCount(1);

                outcomes.add(
                        new JeiItemOutcome(
                                displayStack,
                                count.min(),
                                count.max(),
                                prepared.weight(),
                                totalWeight,
                                rolls.min(),
                                rolls.max()
                        )
                );
            }
        }

        return List.copyOf(outcomes);
    }

    private static @NotNull List<JeiItemOutcome> translateNested(
            @NotNull NestedLootTable nested,
            @NotNull LootTableResolver resolver,
            @NotNull Set<ResourceKey<LootTable>> activeReferences
    ) {
        Either<ResourceKey<LootTable>, LootTable> contents =
                ((NestedLootTableAccessor) nested)
                        .jolcraft$getContents();

        return contents.map(
                key -> {
                    if (!activeReferences.add(key)) {
                        throw new IllegalArgumentException(
                                "Recursive loot-table reference during JEI translation: "
                                        + key.location()
                        );
                    }

                    try {
                        LootTable table =
                                Objects.requireNonNull(
                                        resolver.resolve(key),
                                        "Loot-table resolver returned null for "
                                                + key.location()
                                );

                        return translateTable(
                                table,
                                resolver,
                                activeReferences
                        );
                    } finally {
                        activeReferences.remove(key);
                    }
                },
                inlineTable ->
                        translateTable(
                                inlineTable,
                                resolver,
                                activeReferences
                        )
        );
    }

    private static @NotNull List<Item> resolveDirectItems(
            @NotNull LootPoolSingletonContainer entry
    ) {
        if (entry instanceof EmptyLootItem
                || entry instanceof NestedLootTable) {
            return List.of();
        }

        if (entry instanceof LootItem lootItem) {
            Holder<Item> item =
                    ((LootItemAccessor) lootItem)
                            .jolcraft$getItem();

            return List.of(
                    item.value()
            );
        }

        if (entry instanceof TagEntry tagEntry) {
            TagEntryAccessor accessor =
                    (TagEntryAccessor) tagEntry;

            if (!accessor.jolcraft$isExpanded()) {
                throw new IllegalArgumentException(
                        "JEI translation requires item-tag loot entries to be expanded"
                );
            }

            TagKey<Item> tag =
                    accessor.jolcraft$getTag();

            HolderSet.Named<Item> items =
                    BuiltInRegistries.ITEM
                            .getTag(tag)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Unknown or empty item tag for JEI translation: "
                                                    + tag.location()
                                    )
                            );

            List<Item> resolved =
                    items.stream()
                            .map(Holder::value)
                            .toList();

            if (resolved.isEmpty()) {
                throw new IllegalArgumentException(
                        "Item tag produced no JEI outputs: "
                                + tag.location()
                );
            }

            return resolved;
        }

        throw unsupportedEntry(entry);
    }

    private static @NotNull CountRange applyAllCountFunctions(
            @NotNull CountRange current,
            @NotNull List<LootItemFunction> entryFunctions,
            @NotNull List<LootItemFunction> poolFunctions,
            @NotNull List<LootItemFunction> tableFunctions
    ) {
        CountRange result =
                applyCountFunctions(
                        current,
                        entryFunctions
                );

        result =
                applyCountFunctions(
                        result,
                        poolFunctions
                );

        return applyCountFunctions(
                result,
                tableFunctions
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
                                result.min() + value.min(),
                                result.max() + value.max()
                        );
            } else {
                result = value;
            }
        }

        return result;
    }

    private static void applyAllDisplayFunctions(
            @NotNull ItemStack stack,
            @NotNull List<LootItemFunction> entryFunctions,
            @NotNull List<LootItemFunction> poolFunctions,
            @NotNull List<LootItemFunction> tableFunctions
    ) {
        applyDisplayFunctions(
                stack,
                entryFunctions
        );

        applyDisplayFunctions(
                stack,
                poolFunctions
        );

        applyDisplayFunctions(
                stack,
                tableFunctions
        );
    }

    private static void applyDisplayFunctions(
            @NotNull ItemStack stack,
            @NotNull List<LootItemFunction> functions
    ) {
        for (LootItemFunction function : functions) {
            if (function instanceof SetItemCountFunction) {
                continue;
            }

            if (function instanceof SetComponentsFunction setComponents) {
                DataComponentPatch components =
                        ((SetComponentsFunctionAccessor) setComponents)
                                .jolcraft$getComponents();

                stack.applyComponentsAndValidate(components);
            }
        }
    }

    private static void requireUnconditionalFunctions(
            @NotNull List<LootItemFunction> functions,
            @NotNull String owner
    ) {
        for (LootItemFunction function : functions) {
            if (!(function instanceof LootItemConditionalFunction conditional)) {
                continue;
            }

            List<?> predicates =
                    ((LootItemConditionalFunctionAccessor) conditional)
                            .jolcraft$getPredicates();

            if (!predicates.isEmpty()) {
                throw new IllegalArgumentException(
                        "JEI translation does not support conditional "
                                + owner
                                + " loot functions"
                );
            }
        }
    }

    private static void requireNoConditions(
            @NotNull List<?> conditions,
            @NotNull String owner
    ) {
        if (!conditions.isEmpty()) {
            throw new IllegalArgumentException(
                    "JEI translation does not support conditional "
                            + owner
            );
        }
    }

    private static @NotNull CountRange readNumberRange(
            @NotNull NumberProvider provider
    ) {
        JeiNumberRangeTranslator.NumberRange range =
                JeiNumberRangeTranslator.translate(
                        provider
                );

        return new CountRange(
                range.min(),
                range.max()
        );
    }

    private static @NotNull WeightRatio combineWeightRatios(
            int outerWeight,
            int outerTotalWeight,
            int innerWeight,
            int innerTotalWeight
    ) {
        long weight =
                (long) outerWeight
                        * innerWeight;

        long totalWeight =
                (long) outerTotalWeight
                        * innerTotalWeight;

        long divisor =
                greatestCommonDivisor(
                        weight,
                        totalWeight
                );

        weight /= divisor;
        totalWeight /= divisor;

        if (weight > Integer.MAX_VALUE
                || totalWeight > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Combined JEI loot probability exceeds supported integer range"
            );
        }

        return new WeightRatio(
                (int) weight,
                (int) totalWeight
        );
    }

    private static long greatestCommonDivisor(
            long first,
            long second
    ) {
        long a = first;
        long b = second;

        while (b != 0L) {
            long remainder =
                    a % b;

            a = b;
            b = remainder;
        }

        return a;
    }

    private static int multiplyRolls(
            int first,
            int second
    ) {
        try {
            return Math.multiplyExact(
                    first,
                    second
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Combined JEI loot rolls exceed the supported integer range",
                    exception
            );
        }
    }

    private static int addExactWeight(
            int first,
            int second
    ) {
        try {
            return Math.addExact(
                    first,
                    second
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "JEI loot total weight exceeds the supported integer range",
                    exception
            );
        }
    }

    private static int multiplyExactWeight(
            int first,
            int second
    ) {
        try {
            return Math.multiplyExact(
                    first,
                    second
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "JEI loot entry weight exceeds the supported integer range",
                    exception
            );
        }
    }

    private static @NotNull IllegalArgumentException unsupportedEntry(
            @NotNull LootPoolEntryContainer entry
    ) {
        return new IllegalArgumentException(
                "Unsupported loot entry for JEI translation: "
                        + entry.getClass().getName()
        );
    }

    private record PreparedEntry(
            @NotNull LootPoolSingletonContainer entry,
            @NotNull List<LootItemFunction> functions,
            int weight,
            @NotNull List<Item> items
    ) {}

    private record WeightRatio(
            int weight,
            int totalWeight
    ) {}

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