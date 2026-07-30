package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolsConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradePoolType;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.IntToDoubleFunction;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {
    }

    public static @NotNull List<JeiDwarfTradeRecipe> getRecipes(
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

        DwarfProfessionTradePoolsConfig tradePools =
                DwarfProfessionConfigManager.INSTANCE
                        .get(profession)
                        .tradePools();

        TradePoolCounts poolCounts =
                TradePoolCounts.create(
                        matching
                );

        TradeSelectionChances selectionChances =
                TradeSelectionChances.create(
                        poolCounts,
                        tradePools
                );

        List<JeiDwarfTradeRecipe> result =
                JeiRecipeAccess.translate(
                        matching,
                        holder -> {
                            DwarfTradeRecipe recipe =
                                    holder.value();

                            List<JeiItemOutcome> outcomes =
                                    ItemOutputJeiTranslator.translate(
                                            recipe.result()
                                    );

                            double tradeSelectionChance =
                                    selectionChances.chanceFor(
                                            recipe
                                    );

                            return JeiDwarfTradeRecipe.create(
                                    recipe,
                                    spawnEgg,
                                    tradeSelectionChance,
                                    outcomes
                            );
                        }
                );

        return List.copyOf(
                result
        );
    }

    private static @NotNull Map<SelectionChanceKey, Double>
    exactLevelSelectionChances(
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > candidateCounts,
            @NotNull DwarfProfessionTradePoolConfig poolConfig
    ) {
        Map<SelectionChanceKey, Double> chances =
                new HashMap<>();

        for (DwarfMerchantData.Level level :
                DwarfMerchantData.Level.values()) {
            Map<Integer, Integer> levelCounts =
                    candidateCounts.getOrDefault(
                            level,
                            Map.of()
                    );

            if (levelCounts.isEmpty()) {
                continue;
            }

            int rolls =
                    poolConfig.rollsFor(
                            TradePoolType.EXACT_LEVEL,
                            level
                    );

            chances.putAll(
                    fixedPoolSelectionChances(
                            level,
                            levelCounts,
                            rolls
                    )
            );
        }

        return Map.copyOf(
                chances
        );
    }

    private static @NotNull Map<SelectionChanceKey, Double>
    fixedPoolSelectionChances(
            @NotNull DwarfMerchantData.Level level,
            @NotNull Map<Integer, Integer> candidateCounts,
            int rolls
    ) {
        int candidateCount =
                totalCount(
                        candidateCounts
                );

        int weightCount =
                selectableWeightCount(
                        candidateCounts
                );

        long totalWeight =
                totalWeight(
                        candidateCounts
                );

        if (candidateCount <= 0
                || weightCount <= 0) {
            return Map.of();
        }

        if (rolls <= 0
                || totalWeight <= 0L) {
            return directFixedPoolSelectionChances(
                    level,
                    candidateCounts,
                    ignored -> 0.0D
            );
        }

        if (rolls >= candidateCount) {
            return directFixedPoolSelectionChances(
                    level,
                    candidateCounts,
                    ignored -> 1.0D
            );
        }

        if (rolls == 1) {
            return directFixedPoolSelectionChances(
                    level,
                    candidateCounts,
                    weight ->
                            Math.clamp(
                                    (double) weight
                                            / totalWeight,
                                    0.0D,
                                    1.0D
                            )
            );
        }

        if (weightCount == 1) {
            double chance =
                    Math.clamp(
                            (double) rolls
                                    / candidateCount,
                            0.0D,
                            1.0D
                    );

            return directFixedPoolSelectionChances(
                    level,
                    candidateCounts,
                    ignored -> chance
            );
        }

        EnumMap<
                DwarfMerchantData.Level,
                Map<Integer, Integer>
                > stagedCounts =
                new EnumMap<>(
                        DwarfMerchantData.Level.class
                );

        stagedCounts.put(
                level,
                candidateCounts
        );

        EnumMap<DwarfMerchantData.Level, Integer> stagedRolls =
                new EnumMap<>(
                        DwarfMerchantData.Level.class
                );

        stagedRolls.put(
                level,
                rolls
        );

        return stagedSelectionChances(
                TradeGroup.EXACT_LEVEL_POOL,
                stagedCounts,
                stagedRolls,
                level
        );
    }

    private static @NotNull Map<SelectionChanceKey, Double>
    directFixedPoolSelectionChances(
            @NotNull DwarfMerchantData.Level level,
            @NotNull Map<Integer, Integer> candidateCounts,
            @NotNull IntToDoubleFunction chanceForWeight
    ) {
        Map<SelectionChanceKey, Double> chances =
                new HashMap<>();

        for (Map.Entry<Integer, Integer> entry :
                candidateCounts.entrySet()) {
            int weight =
                    entry.getKey();

            if (weight <= 0
                    || entry.getValue() <= 0) {
                continue;
            }

            chances.put(
                    new SelectionChanceKey(
                            TradeGroup.EXACT_LEVEL_POOL,
                            level,
                            weight
                    ),
                    chanceForWeight.applyAsDouble(
                            weight
                    )
            );
        }

        return Map.copyOf(
                chances
        );
    }

    private static @NotNull Map<SelectionChanceKey, Double>
    cumulativePoolSelectionChances(
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > candidateCounts,
            @NotNull DwarfProfessionTradePoolConfig poolConfig
    ) {
        EnumMap<DwarfMerchantData.Level, Integer> stagedRolls =
                new EnumMap<>(
                        DwarfMerchantData.Level.class
                );

        for (DwarfMerchantData.Level level :
                DwarfMerchantData.Level.values()) {
            int rolls =
                    poolConfig.rolls()
                            .rollsFor(
                                    level
                            );

            if (rolls > 0) {
                stagedRolls.put(
                        level,
                        rolls
                );
            }
        }

        return stagedSelectionChances(
                TradeGroup.CUMULATIVE_POOL,
                candidateCounts,
                stagedRolls,
                DwarfMerchantData.Level.MASTER
        );
    }

    /**
     * Calculates every recipe cohort's exact inclusion chance with one shared
     * weighted-without-replacement state traversal.
     *
     * The state only tracks total removals per weight. Alongside each state,
     * probability-weighted remaining counts are propagated for every
     * level/weight cohort. Candidates of the same weight are exchangeable once
     * unlocked, so each recipe's inclusion chance is exactly one minus its
     * cohort's expected remaining fraction.
     */
    private static @NotNull Map<SelectionChanceKey, Double>
    stagedSelectionChances(
            @NotNull TradeGroup group,
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > candidateCounts,
            @NotNull Map<DwarfMerchantData.Level, Integer> stagedRolls,
            @NotNull DwarfMerchantData.Level displayLevel
    ) {
        int[] weights =
                selectableWeights(
                        candidateCounts,
                        displayLevel
                );

        if (weights.length == 0) {
            return Map.of();
        }

        List<SelectionCohort> cohorts =
                selectionCohorts(
                        group,
                        candidateCounts,
                        displayLevel,
                        weights
                );

        if (cohorts.isEmpty()) {
            return Map.of();
        }

        EnumMap<DwarfMerchantData.Level, List<Integer>> cohortsByLevel =
                cohortIndexesByLevel(
                        cohorts
                );

        int[][] cohortsByWeight =
                cohortIndexesByWeight(
                        cohorts,
                        weights.length
                );

        int[] availableCounts =
                new int[weights.length];

        Map<SelectionState, SelectionMass> states =
                new HashMap<>();

        states.put(
                SelectionState.empty(
                        weights.length
                ),
                SelectionMass.initial(
                        cohorts.size()
                )
        );

        for (DwarfMerchantData.Level level :
                DwarfMerchantData.Level.values()) {
            if (level.getId()
                    > displayLevel.getId()) {
                break;
            }

            for (int cohortIndex :
                    cohortsByLevel.getOrDefault(
                            level,
                            List.of()
                    )) {
                SelectionCohort cohort =
                        cohorts.get(
                                cohortIndex
                        );

                availableCounts[cohort.weightIndex()] +=
                        cohort.count();

                for (SelectionMass mass :
                        states.values()) {
                    mass.addCandidates(
                            cohortIndex,
                            cohort.count()
                    );
                }
            }

            int rolls =
                    Math.max(
                            0,
                            stagedRolls.getOrDefault(
                                    level,
                                    0
                            )
                    );

            for (int roll = 0;
                 roll < rolls;
                 roll++) {
                states =
                        advanceSelectionStates(
                                states,
                                availableCounts,
                                weights,
                                cohortsByWeight,
                                cohorts.size()
                        );
            }
        }

        double[] expectedRemaining =
                new double[cohorts.size()];

        for (SelectionMass mass :
                states.values()) {
            mass.addRemainingTo(
                    expectedRemaining
            );
        }

        Map<SelectionChanceKey, Double> chances = getSelectionChanceKeyDoubleMap(cohorts, expectedRemaining);

        return Map.copyOf(
                chances
        );
    }

    private static @NotNull Map<SelectionChanceKey, Double> getSelectionChanceKeyDoubleMap(List<SelectionCohort> cohorts, double[] expectedRemaining) {
        Map<SelectionChanceKey, Double> chances =
                new HashMap<>(
                        cohorts.size()
                );

        for (int index = 0;
             index < cohorts.size();
             index++) {
            SelectionCohort cohort =
                    cohorts.get(
                            index
                    );

            double chance =
                    1.0D
                            - expectedRemaining[index]
                            / cohort.count();

            chances.put(
                    cohort.key(),
                    Math.clamp(
                            chance,
                            0.0D,
                            1.0D
                    )
            );
        }
        return chances;
    }

    private static @NotNull Map<SelectionState, SelectionMass>
    advanceSelectionStates(
            @NotNull Map<SelectionState, SelectionMass> currentStates,
            int[] availableCounts,
            int[] weights,
            int[][] cohortsByWeight,
            int cohortCount
    ) {
        Map<SelectionState, SelectionMass> nextStates =
                new HashMap<>(
                        Math.max(
                                16,
                                currentStates.size()
                                        * 3
                        )
                );

        for (Map.Entry<SelectionState, SelectionMass> entry :
                currentStates.entrySet()) {
            SelectionState state =
                    entry.getKey();

            SelectionMass mass =
                    entry.getValue();

            long totalWeight = getTotalWeight(availableCounts, weights, state);

            if (totalWeight <= 0L) {
                nextStates.computeIfAbsent(
                                state,
                                ignored ->
                                        new SelectionMass(
                                                cohortCount
                                        )
                        )
                        .addScaled(
                                mass,
                                1.0D
                        );

                continue;
            }

            for (int weightIndex = 0;
                 weightIndex < weights.length;
                 weightIndex++) {
                int remaining =
                        availableCounts[weightIndex]
                                - state.selectedAt(
                                weightIndex
                        );

                if (remaining <= 0) {
                    continue;
                }

                int weight =
                        weights[weightIndex];

                double branchFactor =
                        (double) remaining
                                * weight
                                / totalWeight;

                SelectionMass nextMass =
                        nextStates.computeIfAbsent(
                                state.incremented(
                                        weightIndex
                                ),
                                ignored ->
                                        new SelectionMass(
                                                cohortCount
                                        )
                        );

                nextMass.addScaled(
                        mass,
                        branchFactor
                );

                double selectedFactor =
                        (double) weight
                                / totalWeight;

                nextMass.subtractSelectedCandidates(
                        mass,
                        cohortsByWeight[weightIndex],
                        selectedFactor
                );
            }
        }

        return nextStates;
    }

    private static long getTotalWeight(int[] availableCounts, int[] weights, SelectionState state) {
        long totalWeight =
                0L;

        for (int weightIndex = 0;
             weightIndex < weights.length;
             weightIndex++) {
            int remaining =
                    availableCounts[weightIndex]
                            - state.selectedAt(
                            weightIndex
                    );

            if (remaining > 0) {
                totalWeight +=
                        (long) remaining
                                * weights[weightIndex];
            }
        }
        return totalWeight;
    }

    private static @NotNull List<SelectionCohort> selectionCohorts(
            @NotNull TradeGroup group,
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > candidateCounts,
            @NotNull DwarfMerchantData.Level displayLevel,
            int[] weights
    ) {
        List<SelectionCohort> cohorts =
                new ArrayList<>();

        for (DwarfMerchantData.Level level :
                DwarfMerchantData.Level.values()) {
            if (level.getId()
                    > displayLevel.getId()) {
                break;
            }

            Map<Integer, Integer> levelCounts =
                    candidateCounts.getOrDefault(
                            level,
                            Map.of()
                    );

            for (int weightIndex = 0;
                 weightIndex < weights.length;
                 weightIndex++) {
                int weight =
                        weights[weightIndex];

                int count =
                        levelCounts.getOrDefault(
                                weight,
                                0
                        );

                if (count <= 0) {
                    continue;
                }

                cohorts.add(
                        new SelectionCohort(
                                new SelectionChanceKey(
                                        group,
                                        level,
                                        weight
                                ),
                                count,
                                weightIndex
                        )
                );
            }
        }

        return List.copyOf(
                cohorts
        );
    }

    private static @NotNull EnumMap<
            DwarfMerchantData.Level,
            List<Integer>
            > cohortIndexesByLevel(
            @NotNull List<SelectionCohort> cohorts
    ) {
        EnumMap<DwarfMerchantData.Level, List<Integer>> byLevel =
                new EnumMap<>(
                        DwarfMerchantData.Level.class
                );

        for (int index = 0;
             index < cohorts.size();
             index++) {
            DwarfMerchantData.Level level =
                    cohorts.get(
                                    index
                            )
                            .key()
                            .merchantLevel();

            byLevel.computeIfAbsent(
                            level,
                            ignored -> new ArrayList<>()
                    )
                    .add(
                            index
                    );
        }

        return byLevel;
    }

    private static int[][] cohortIndexesByWeight(
            @NotNull List<SelectionCohort> cohorts,
            int weightCount
    ) {
        List<List<Integer>> grouped =
                new ArrayList<>(
                        weightCount
                );

        for (int index = 0;
             index < weightCount;
             index++) {
            grouped.add(
                    new ArrayList<>()
            );
        }

        for (int cohortIndex = 0;
             cohortIndex < cohorts.size();
             cohortIndex++) {
            grouped.get(
                            cohorts.get(
                                            cohortIndex
                                    )
                                    .weightIndex()
                    )
                    .add(
                            cohortIndex
                    );
        }

        int[][] indexes =
                new int[weightCount][];

        for (int weightIndex = 0;
             weightIndex < weightCount;
             weightIndex++) {
            indexes[weightIndex] =
                    grouped.get(
                                    weightIndex
                            )
                            .stream()
                            .mapToInt(
                                    Integer::intValue
                            )
                            .toArray();
        }

        return indexes;
    }

    private static int[] selectableWeights(
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > candidateCounts,
            @NotNull DwarfMerchantData.Level displayLevel
    ) {
        TreeSet<Integer> weights =
                new TreeSet<>();

        for (Map.Entry<
                DwarfMerchantData.Level,
                Map<Integer, Integer>
                > entry : candidateCounts.entrySet()) {
            if (entry.getKey().getId()
                    > displayLevel.getId()) {
                continue;
            }

            for (Map.Entry<Integer, Integer> countEntry :
                    entry.getValue().entrySet()) {
                if (countEntry.getKey() > 0
                        && countEntry.getValue() > 0) {
                    weights.add(
                            countEntry.getKey()
                    );
                }
            }
        }

        return weights.stream()
                .mapToInt(
                        Integer::intValue
                )
                .toArray();
    }

    private static int selectableWeightCount(
            @NotNull Map<Integer, Integer> candidateCounts
    ) {
        int count =
                0;

        for (Map.Entry<Integer, Integer> entry :
                candidateCounts.entrySet()) {
            if (entry.getKey() > 0
                    && entry.getValue() > 0) {
                count++;
            }
        }

        return count;
    }

    private static int totalCount(
            @NotNull Map<Integer, Integer> candidateCounts
    ) {
        int total =
                0;

        for (Map.Entry<Integer, Integer> entry :
                candidateCounts.entrySet()) {
            if (entry.getKey() <= 0) {
                continue;
            }

            total +=
                    Math.max(
                            0,
                            entry.getValue()
                    );
        }

        return total;
    }

    private static long totalWeight(
            @NotNull Map<Integer, Integer> candidateCounts
    ) {
        long totalWeight =
                0L;

        for (Map.Entry<Integer, Integer> entry :
                candidateCounts.entrySet()) {
            int weight =
                    Math.max(
                            0,
                            entry.getKey()
                    );

            int count =
                    Math.max(
                            0,
                            entry.getValue()
                    );

            totalWeight +=
                    (long) weight
                            * count;
        }

        return totalWeight;
    }

    private static double globalPoolPickChance(
            @NotNull Map<Integer, Integer> candidateCounts,
            int targetWeight
    ) {
        if (targetWeight <= 0
                || candidateCounts.getOrDefault(
                targetWeight,
                0
        ) <= 0) {
            return 0.0D;
        }

        long totalWeight =
                totalWeight(
                        candidateCounts
                );

        if (totalWeight <= 0L) {
            return 0.0D;
        }

        return Math.clamp(
                (double) targetWeight
                        / totalWeight,
                0.0D,
                1.0D
        );
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
        return switch (groupOf(recipe)) {
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

    private record TradeSelectionChances(
            @NotNull Map<SelectionChanceKey, Double> leveled,
            @NotNull Map<Integer, Double> global
    ) {

        private static @NotNull TradeSelectionChances create(
                @NotNull TradePoolCounts poolCounts,
                @NotNull DwarfProfessionTradePoolsConfig tradePools
        ) {
            Map<SelectionChanceKey, Double> leveled =
                    new HashMap<>();

            tradePools.get(
                            TradePoolType.EXACT_LEVEL
                    )
                    .ifPresent(poolConfig ->
                            leveled.putAll(
                                    exactLevelSelectionChances(
                                            poolCounts.exactLevelCounts(),
                                            poolConfig
                                    )
                            )
                    );

            tradePools.get(
                            TradePoolType.CUMULATIVE
                    )
                    .ifPresent(poolConfig ->
                            leveled.putAll(
                                    cumulativePoolSelectionChances(
                                            poolCounts.cumulativeCounts(),
                                            poolConfig
                                    )
                            )
                    );

            Map<Integer, Double> global =
                    new HashMap<>();

            for (int weight :
                    poolCounts.globalCounts()
                            .keySet()) {
                global.put(
                        weight,
                        globalPoolPickChance(
                                poolCounts.globalCounts(),
                                weight
                        )
                );
            }

            return new TradeSelectionChances(
                    Map.copyOf(
                            leveled
                    ),
                    Map.copyOf(
                            global
                    )
            );
        }

        private double chanceFor(
                @NotNull DwarfTradeRecipe recipe
        ) {
            TradeGroup group =
                    groupOf(
                            recipe
                    );

            if (group == TradeGroup.MAIN) {
                return 1.0D;
            }

            int weight =
                    safeWeight(
                            recipe
                    );

            if (weight <= 0) {
                return 0.0D;
            }

            if (group == TradeGroup.GLOBAL_POOL) {
                return global.getOrDefault(
                        weight,
                        0.0D
                );
            }

            DwarfMerchantData.Level merchantLevel =
                    recipe.merchantLevel();

            if (merchantLevel == null) {
                return 0.0D;
            }

            return leveled.getOrDefault(
                    new SelectionChanceKey(
                            group,
                            merchantLevel,
                            weight
                    ),
                    0.0D
            );
        }
    }

    private record SelectionChanceKey(
            @NotNull TradeGroup group,
            @NotNull DwarfMerchantData.Level merchantLevel,
            int weight
    ) {
    }

    private record SelectionCohort(
            @NotNull SelectionChanceKey key,
            int count,
            int weightIndex
    ) {
    }

    private record TradePoolCounts(
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > exactLevelCounts,
            @NotNull Map<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > cumulativeCounts,
            @NotNull Map<Integer, Integer> globalCounts
    ) {

        private static @NotNull TradePoolCounts create(
                @NotNull List<RecipeHolder<DwarfTradeRecipe>> recipes
        ) {
            EnumMap<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > exactLevelCounts =
                    new EnumMap<>(
                            DwarfMerchantData.Level.class
                    );

            EnumMap<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > cumulativeCounts =
                    new EnumMap<>(
                            DwarfMerchantData.Level.class
                    );

            Map<Integer, Integer> globalCounts =
                    new HashMap<>();

            for (RecipeHolder<DwarfTradeRecipe> holder : recipes) {
                DwarfTradeRecipe recipe =
                        holder.value();

                int weight =
                        safeWeight(
                                recipe
                        );

                switch (groupOf(recipe)) {
                    case MAIN -> {
                    }

                    case EXACT_LEVEL_POOL ->
                            addLevelCount(
                                    exactLevelCounts,
                                    recipe.merchantLevel(),
                                    weight
                            );

                    case CUMULATIVE_POOL ->
                            addLevelCount(
                                    cumulativeCounts,
                                    recipe.merchantLevel(),
                                    weight
                            );

                    case GLOBAL_POOL ->
                            globalCounts.merge(
                                    weight,
                                    1,
                                    Integer::sum
                            );
                }
            }

            return new TradePoolCounts(
                    immutableLevelCounts(
                            exactLevelCounts
                    ),
                    immutableLevelCounts(
                            cumulativeCounts
                    ),
                    Map.copyOf(
                            globalCounts
                    )
            );
        }

        private static void addLevelCount(
                @NotNull EnumMap<
                        DwarfMerchantData.Level,
                        Map<Integer, Integer>
                        > counts,
                @Nullable DwarfMerchantData.Level level,
                int weight
        ) {
            if (level == null) {
                return;
            }

            counts.computeIfAbsent(
                            level,
                            ignored -> new HashMap<>()
                    )
                    .merge(
                            weight,
                            1,
                            Integer::sum
                    );
        }

        private static @NotNull Map<
                DwarfMerchantData.Level,
                Map<Integer, Integer>
                > immutableLevelCounts(
                @NotNull EnumMap<
                        DwarfMerchantData.Level,
                        Map<Integer, Integer>
                        > counts
        ) {
            EnumMap<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > copy =
                    new EnumMap<>(
                            DwarfMerchantData.Level.class
                    );

            for (Map.Entry<
                    DwarfMerchantData.Level,
                    Map<Integer, Integer>
                    > entry : counts.entrySet()) {
                copy.put(
                        entry.getKey(),
                        Map.copyOf(
                                entry.getValue()
                        )
                );
            }

            return Map.copyOf(
                    copy
            );
        }
    }

    private static final class SelectionState {

        private final int[] selectedCounts;
        private final int hashCode;

        private SelectionState(
                int[] selectedCounts
        ) {
            this.selectedCounts =
                    selectedCounts;

            this.hashCode =
                    Arrays.hashCode(
                            selectedCounts
                    );
        }

        private static @NotNull SelectionState empty(
                int size
        ) {
            return new SelectionState(
                    new int[size]
            );
        }

        private int selectedAt(
                int index
        ) {
            return selectedCounts[index];
        }

        private @NotNull SelectionState incremented(
                int index
        ) {
            int[] copy =
                    selectedCounts.clone();

            copy[index]++;

            return new SelectionState(
                    copy
            );
        }

        @Override
        public boolean equals(
                Object object
        ) {
            if (this == object) {
                return true;
            }

            if (!(object instanceof SelectionState other)) {
                return false;
            }

            return Arrays.equals(
                    selectedCounts,
                    other.selectedCounts
            );
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static final class SelectionMass {

        private double probability;
        private final double[] remainingMass;

        private SelectionMass(
                int cohortCount
        ) {
            this(
                    0.0D,
                    new double[cohortCount]
            );
        }

        private SelectionMass(
                double probability,
                double[] remainingMass
        ) {
            this.probability =
                    probability;

            this.remainingMass =
                    remainingMass;
        }

        private static @NotNull SelectionMass initial(
                int cohortCount
        ) {
            return new SelectionMass(
                    1.0D,
                    new double[cohortCount]
            );
        }

        private void addCandidates(
                int cohortIndex,
                int count
        ) {
            remainingMass[cohortIndex] +=
                    probability
                            * count;
        }

        private void addScaled(
                @NotNull SelectionMass source,
                double factor
        ) {
            probability +=
                    source.probability
                            * factor;

            for (int index = 0;
                 index < remainingMass.length;
                 index++) {
                remainingMass[index] +=
                        source.remainingMass[index]
                                * factor;
            }
        }

        private void subtractSelectedCandidates(
                @NotNull SelectionMass source,
                int[] cohortIndexes,
                double factor
        ) {
            for (int cohortIndex :
                    cohortIndexes) {
                remainingMass[cohortIndex] -=
                        source.remainingMass[cohortIndex]
                                * factor;
            }
        }

        private void addRemainingTo(
                double[] destination
        ) {
            for (int index = 0;
                 index < remainingMass.length;
                 index++) {
                destination[index] +=
                        remainingMass[index];
            }
        }
    }
}