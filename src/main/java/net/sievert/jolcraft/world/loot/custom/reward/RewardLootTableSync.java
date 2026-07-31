package net.sievert.jolcraft.world.loot.custom.reward;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.component.custom.RewardCrateSource;
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
 * Collects only direct loot tables needed to preview reward-crate dwarf trades.
 */
public final class RewardLootTableSync {

    private RewardLootTableSync() {}

    public static @NotNull Map<ResourceKey<LootTable>, LootTable> collect(
            @NotNull MinecraftServer server
    ) {
        Set<ResourceKey<LootTable>> referenced = new LinkedHashSet<>();

        for (RecipeHolder<DwarfTradeRecipe> holder :
                server.getRecipeManager()
                        .getAllRecipesFor(
                                JolCraftRecipes.DWARF_TRADE_TYPE.get()
                        )) {
            for (RewardCrateSource source :
                    RewardCrateSourceFinder.find(
                            holder.value().result()
                    )) {
                if (source instanceof RewardCrateSource.LootTableSource direct) {
                    referenced.add(direct.lootTable());
                }
            }
        }

        List<ResourceKey<LootTable>> ordered =
                new ArrayList<>(referenced);

        ordered.sort(
                Comparator.comparing(key ->
                        key.location().toString()
                )
        );

        Map<ResourceKey<LootTable>, LootTable> tables =
                new LinkedHashMap<>();

        for (ResourceKey<LootTable> key : ordered) {
            LootTable table =
                    server.reloadableRegistries()
                            .getLootTable(key);

            if (table == LootTable.EMPTY) {
                JolCraftLogs.warn(
                        JolCraftLogTags.RECIPE,
                        "Skipping unresolved reward loot table {} during client sync",
                        key.location()
                );
                continue;
            }

            tables.put(key, table);
        }

        return Map.copyOf(tables);
    }
}
