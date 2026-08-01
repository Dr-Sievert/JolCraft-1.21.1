package net.sievert.jolcraft.world.loot.custom.reward;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.sievert.jolcraft.mixin.LootPoolAccessor;
import net.sievert.jolcraft.mixin.LootTableAccessor;
import net.sievert.jolcraft.mixin.NestedLootTableAccessor;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collects all loot tables needed to preview reward-crate dwarf trades,
 * including transitively referenced nested loot tables.
 */
public final class RewardLootTableSync {

    private RewardLootTableSync() {}

    public static @NotNull Map<ResourceKey<LootTable>, LootTable> collect(
            @NotNull MinecraftServer server
    ) {
        Set<ResourceKey<LootTable>> roots =
                new LinkedHashSet<>();

        for (RecipeHolder<DwarfTradeRecipe> holder :
                server.getRecipeManager()
                        .getAllRecipesFor(
                                JolCraftRecipes.DWARF_TRADE_TYPE.get()
                        )) {
            for (RewardCrateSource source :
                    RewardCrateSourceFinder.find(
                            holder.value().result()
                    )) {
                if (source instanceof RewardCrateSource.LootTableSource(
                        ResourceKey<LootTable> lootTable
                )) {
                    roots.add(lootTable);
                }
            }
        }

        List<ResourceKey<LootTable>> orderedRoots =
                new ArrayList<>(roots);

        orderedRoots.sort(
                Comparator.comparing(
                        key -> key.location().toString()
                )
        );

        Map<ResourceKey<LootTable>, LootTable> collected =
                new LinkedHashMap<>();

        Set<ResourceKey<LootTable>> visiting =
                new LinkedHashSet<>();

        for (ResourceKey<LootTable> root : orderedRoots) {
            collectTable(
                    server,
                    root,
                    collected,
                    visiting
            );
        }

        List<ResourceKey<LootTable>> orderedKeys =
                new ArrayList<>(collected.keySet());

        orderedKeys.sort(
                Comparator.comparing(
                        key -> key.location().toString()
                )
        );

        Map<ResourceKey<LootTable>, LootTable> result =
                new LinkedHashMap<>();

        for (ResourceKey<LootTable> key : orderedKeys) {
            result.put(
                    key,
                    collected.get(key)
            );
        }

        return Map.copyOf(result);
    }

    private static void collectTable(
            @NotNull MinecraftServer server,
            @NotNull ResourceKey<LootTable> key,
            @NotNull Map<ResourceKey<LootTable>, LootTable> collected,
            @NotNull Set<ResourceKey<LootTable>> visiting
    ) {
        if (collected.containsKey(key)) {
            return;
        }

        if (!visiting.add(key)) {
            JolCraftLogs.warn(
                    JolCraftLogTags.RECIPE,
                    "Skipping recursive reward loot-table reference {} during client sync",
                    key.location()
            );
            return;
        }

        try {
            LootTable table =
                    server.reloadableRegistries()
                            .getLootTable(key);

            if (table == LootTable.EMPTY) {
                JolCraftLogs.warn(
                        JolCraftLogTags.RECIPE,
                        "Skipping unresolved reward loot table {} during client sync",
                        key.location()
                );
                return;
            }

            collectNestedReferences(
                    server,
                    table,
                    collected,
                    visiting
            );

            collected.put(
                    key,
                    table
            );
        } finally {
            visiting.remove(key);
        }
    }

    private static void collectNestedReferences(
            @NotNull MinecraftServer server,
            @NotNull LootTable table,
            @NotNull Map<ResourceKey<LootTable>, LootTable> collected,
            @NotNull Set<ResourceKey<LootTable>> visiting
    ) {
        LootTableAccessor tableAccessor =
                (LootTableAccessor) table;

        for (LootPool pool :
                tableAccessor.jolcraft$getPools()) {
            LootPoolAccessor poolAccessor =
                    (LootPoolAccessor) pool;

            for (LootPoolEntryContainer entry :
                    poolAccessor.jolcraft$getEntries()) {
                if (!(entry instanceof NestedLootTable nested)) {
                    continue;
                }

                ((NestedLootTableAccessor) nested)
                        .jolcraft$getContents()
                        .map(
                                nestedKey -> {
                                    collectTable(
                                            server,
                                            nestedKey,
                                            collected,
                                            visiting
                                    );
                                    return Boolean.TRUE;
                                },
                                inlineTable -> {
                                    collectNestedReferences(
                                            server,
                                            inlineTable,
                                            collected,
                                            visiting
                                    );
                                    return Boolean.TRUE;
                                }
                        );
            }
        }
    }
}