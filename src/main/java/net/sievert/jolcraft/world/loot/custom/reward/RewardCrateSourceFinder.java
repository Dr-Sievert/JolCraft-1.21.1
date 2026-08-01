package net.sievert.jolcraft.world.loot.custom.reward;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.sievert.jolcraft.mixin.LootItemAccessor;
import net.sievert.jolcraft.mixin.LootPoolAccessor;
import net.sievert.jolcraft.mixin.LootPoolSingletonContainerAccessor;
import net.sievert.jolcraft.mixin.SetComponentsFunctionAccessor;
import net.sievert.jolcraft.mixin.TagEntryAccessor;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads reward-crate source components from a declarative item output without
 * rolling it.
 */
public final class RewardCrateSourceFinder {

    private RewardCrateSourceFinder() {}

    public static @NotNull Set<RewardCrateSource> find(
            @NotNull ItemOutput output
    ) {
        LootPoolAccessor pool =
                (LootPoolAccessor) output.pool();

        Set<RewardCrateSource> sources = new LinkedHashSet<>();

        for (LootPoolEntryContainer entry : pool.jolcraft$getEntries()) {
            if (!(entry instanceof LootPoolSingletonContainer singleton)) {
                continue;
            }

            List<LootItemFunction> entryFunctions =
                    ((LootPoolSingletonContainerAccessor) singleton)
                            .jolcraft$getFunctions();

            for (Item item : resolveItems(singleton)) {
                ItemStack stack = new ItemStack(item);

                applyComponents(stack, entryFunctions);
                applyComponents(stack, pool.jolcraft$getFunctions());

                RewardCrateSource source =
                        stack.get(
                                JolCraftDataComponents.REWARD_CRATE_SOURCE.get()
                        );

                if (source != null) {
                    sources.add(source);
                }
            }
        }

        return Set.copyOf(sources);
    }

    private static @NotNull List<Item> resolveItems(
            @NotNull LootPoolSingletonContainer entry
    ) {
        if (entry instanceof LootItem lootItem) {
            Holder<Item> item =
                    ((LootItemAccessor) lootItem)
                            .jolcraft$getItem();

            return List.of(item.value());
        }

        if (entry instanceof TagEntry tagEntry) {
            TagEntryAccessor accessor =
                    (TagEntryAccessor) tagEntry;

            if (!accessor.jolcraft$isExpanded()) {
                return List.of();
            }

            TagKey<Item> tag = accessor.jolcraft$getTag();
            HolderSet.Named<Item> items =
                    BuiltInRegistries.ITEM
                            .getTag(tag)
                            .orElse(null);

            return items == null
                    ? List.of()
                    : items.stream()
                    .map(Holder::value)
                    .toList();
        }

        return List.of();
    }

    private static void applyComponents(
            @NotNull ItemStack stack,
            @NotNull List<LootItemFunction> functions
    ) {
        for (LootItemFunction function : functions) {
            if (!(function instanceof SetComponentsFunction setComponents)) {
                continue;
            }

            DataComponentPatch components =
                    ((SetComponentsFunctionAccessor) setComponents)
                            .jolcraft$getComponents();

            stack.applyComponentsAndValidate(components);
        }
    }
}
