package net.sievert.jolcraft.integration.jei.util.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
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
import net.sievert.jolcraft.mixin.SetComponentsFunctionAccessor;
import net.sievert.jolcraft.mixin.SetItemCountFunctionAccessor;
import net.sievert.jolcraft.mixin.TagEntryAccessor;
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
        return translate(
                pool,
                List.of()
        );
    }

    public static @NotNull List<JeiItemOutcome> translate(
            @NotNull LootPool pool,
            @NotNull List<LootItemFunction> tableFunctions
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

        int rolls =
                JeiNumberRangeTranslator.requireConstantInt(
                        pool.getRolls(),
                        "loot-pool rolls"
                );

        if (rolls <= 0) {
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

        List<TranslatedEntry> translatedEntries =
                new ArrayList<>();

        int totalWeight = 0;

        for (LootPoolEntryContainer entry : entries) {
            requireNoConditions(
                    ((LootPoolEntryContainerAccessor) entry)
                            .jolcraft$getConditions(),
                    "loot entry"
            );

            if (!(entry instanceof LootPoolSingletonContainer singleton)) {
                throw unsupportedEntry(
                        entry
                );
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
                    resolveItems(
                            singleton
                    );

            for (Item item : items) {
                translatedEntries.add(
                        new TranslatedEntry(
                                item,
                                entryFunctions,
                                weight
                        )
                );

                totalWeight +=
                        weight;
            }
        }

        if (translatedEntries.isEmpty()
                || totalWeight <= 0) {
            return List.of();
        }

        List<JeiItemOutcome> outcomes =
                new ArrayList<>(
                        translatedEntries.size()
                );

        for (TranslatedEntry entry : translatedEntries) {
            CountRange count =
                    applyCountFunctions(
                            new CountRange(
                                    1,
                                    1
                            ),
                            entry.functions()
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

            count =
                    applyCountFunctions(
                            count,
                            tableFunctions
                    );

            ItemStack displayStack =
                    new ItemStack(
                            entry.item()
                    );

            applyDisplayFunctions(
                    displayStack,
                    entry.functions()
            );

            applyDisplayFunctions(
                    displayStack,
                    poolFunctions
            );

            applyDisplayFunctions(
                    displayStack,
                    tableFunctions
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
                            entry.weight(),
                            totalWeight,
                            rolls
                    )
            );
        }

        return List.copyOf(
                outcomes
        );
    }

    private static @NotNull List<Item> resolveItems(
            @NotNull LootPoolSingletonContainer entry
    ) {
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
                            .getTag(
                                    tag
                            )
                            .orElseThrow(
                                    () -> new IllegalArgumentException(
                                            "Unknown or empty item tag for JEI translation: "
                                                    + tag.location()
                                    )
                            );

            List<Item> resolved =
                    items.stream()
                            .map(
                                    Holder::value
                            )
                            .toList();

            if (resolved.isEmpty()) {
                throw new IllegalArgumentException(
                        "Item tag produced no JEI outputs: "
                                + tag.location()
                );
            }

            return resolved;
        }

        throw unsupportedEntry(
                entry
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

                stack.applyComponentsAndValidate(
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

    private static @NotNull IllegalArgumentException unsupportedEntry(
            @NotNull LootPoolEntryContainer entry
    ) {
        return new IllegalArgumentException(
                "Unsupported loot entry for JEI translation: "
                        + entry.getClass()
                        .getName()
        );
    }

    private record TranslatedEntry(
            @NotNull Item item,
            @NotNull List<LootItemFunction> functions,
            int weight
    ) {
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
