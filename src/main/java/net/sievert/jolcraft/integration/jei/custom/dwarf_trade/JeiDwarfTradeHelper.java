package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradePoolEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {
    }

    public static @NotNull List<JeiDwarfTrade> getRecipes(
            @NotNull DwarfProfession profession
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> matching =
                new ArrayList<>(
                        JeiRecipeAccess.getSortedMatching(
                                JolCraftRecipes
                                        .DWARF_TRADE_TYPE
                                        .get(),
                                recipe ->
                                        recipe.profession()
                                                == profession
                        )
                );

        matching.sort(
                Comparator
                        .<RecipeHolder<DwarfTradeRecipe>>comparingInt(
                                holder ->
                                        groupPriority(
                                                holder.value()
                                        )
                        )
                        .thenComparingInt(
                                holder ->
                                        levelPriority(
                                                holder.value()
                                        )
                        )
                        .thenComparingInt(
                                holder ->
                                        holder.value().order()
                        )
                        .thenComparing(
                                RecipeHolder::id
                        )
        );

        DeferredItem<Item> spawnEgg =
                DwarfProfessionHelper.getSpawnEgg(
                        profession
                );

        List<JeiDwarfTrade> result =
                JeiRecipeAccess.translate(
                        matching,
                        holder -> {
                            DwarfTradeRecipe recipe =
                                    holder.value();

                            List<JeiItemOutcome> outcomes =
                                    ItemOutputJeiTranslator.translate(
                                            recipe.result()
                                    );

                            return JeiDwarfTrade.create(
                                    recipe,
                                    spawnEgg,
                                    tradeChancePerRoll(
                                            recipe,
                                            matching
                                    ),
                                    outcomes
                            );
                        }
                );

        return List.copyOf(result);
    }

    private static double tradeChancePerRoll(
            @NotNull DwarfTradeRecipe recipe,
            @NotNull List<RecipeHolder<DwarfTradeRecipe>> matching
    ) {
        TradeGroup group =
                groupOf(
                        recipe
                );

        if (group == TradeGroup.MAIN) {
            return 1.0D;
        }

        int totalWeight = 0;

        for (RecipeHolder<DwarfTradeRecipe> holder : matching) {
            DwarfTradeRecipe candidate =
                    holder.value();

            if (!sharesSelectionPool(
                    recipe,
                    candidate,
                    group
            )) {
                continue;
            }

            totalWeight +=
                    safeWeight(
                            candidate
                    );
        }

        if (totalWeight <= 0) {
            return 0.0D;
        }

        return (double) safeWeight(
                recipe
        ) / totalWeight;
    }

    private static boolean sharesSelectionPool(
            @NotNull DwarfTradeRecipe recipe,
            @NotNull DwarfTradeRecipe candidate,
            @NotNull TradeGroup group
    ) {
        if (groupOf(candidate) != group) {
            return false;
        }

        DwarfMerchantData.Level recipeLevel =
                recipe.merchantLevel();

        DwarfMerchantData.Level candidateLevel =
                candidate.merchantLevel();

        return switch (group) {
            case MAIN ->
                    false;

            case EXACT_LEVEL_POOL ->
                    candidateLevel == recipeLevel;

            case CUMULATIVE_POOL ->
                    recipeLevel != null
                            && candidateLevel != null
                            && candidateLevel.getId()
                            <= recipeLevel.getId();

            case GLOBAL_POOL ->
                    true;
        };
    }

    private static int safeWeight(
            @NotNull DwarfTradeRecipe recipe
    ) {
        TradePoolEntry pool =
                recipe.pool();

        return Math.max(
                0,
                pool != null
                        ? pool.weight()
                        : TradePoolEntry.DEFAULT_WEIGHT
        );
    }

    private static @NotNull TradeGroup groupOf(
            @NotNull DwarfTradeRecipe recipe
    ) {
        TradePoolEntry pool =
                recipe.pool();

        return pool != null
                && pool.group() != null
                ? pool.group()
                : TradeGroup.MAIN;
    }

    private static int groupPriority(
            @NotNull DwarfTradeRecipe recipe
    ) {
        TradeGroup group =
                groupOf(
                        recipe
                );

        return switch (group) {
            case MAIN -> 0;
            case EXACT_LEVEL_POOL -> 1;
            case CUMULATIVE_POOL -> 2;
            case GLOBAL_POOL -> 3;
        };
    }

    private static int levelPriority(
            @NotNull DwarfTradeRecipe recipe
    ) {
        return recipe.merchantLevel() == null
                ? Integer.MAX_VALUE
                : recipe.merchantLevel().getId();
    }
}