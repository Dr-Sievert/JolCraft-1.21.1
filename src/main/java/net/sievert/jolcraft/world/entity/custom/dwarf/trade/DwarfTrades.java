package net.sievert.jolcraft.world.entity.custom.dwarf.trade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolsConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradePoolType;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeCost;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradePoolEntry;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipeInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class DwarfTrades {

    private static final LootContextParamSet RESULT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    private DwarfTrades() {}

    public enum RefreshMode {
        FULL,
        RESTOCK,
        REROLL
    }

    public static final class RecipeListing {

        private final RecipeHolder<DwarfTradeRecipe> recipeHolder;
        private final DwarfTradeRecipe recipe;

        public RecipeListing(
                @NotNull RecipeHolder<DwarfTradeRecipe> recipeHolder
        ) {
            this.recipeHolder = recipeHolder;
            this.recipe = recipeHolder.value();
        }

        @Nullable
        public DwarfMerchantOffer getOffer(
                AbstractTradingEntity trader
        ) {
            if (recipe == null) {
                return null;
            }

            if (!(trader.level() instanceof ServerLevel serverLevel)) {
                return null;
            }

            LootContext context =
                    createResultContext(
                            serverLevel,
                            trader
                    );

            ItemStack costAStack =
                    materializeCost(
                            recipe.costA(),
                            context
                    );

            if (costAStack.isEmpty()) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade offer failed: costA empty for recipe profession={} level={} order={} group={}",
                        recipe.profession(),
                        recipe.merchantLevel(),
                        recipe.order(),
                        groupOf(recipe)
                );

                return null;
            }

            ItemStack costBConcrete =
                    ItemStack.EMPTY;

            Optional<ItemStack> costBStack =
                    Optional.empty();

            if (recipe.costB() != null) {
                costBConcrete =
                        materializeCost(
                                recipe.costB(),
                                context
                        );

                if (costBConcrete.isEmpty()) {
                    JolCraftLogs.warn(
                            JolCraftLogTags.ENTITY,
                            "Dwarf trade offer failed: costB empty for recipe profession={} level={} order={} group={}",
                            recipe.profession(),
                            recipe.merchantLevel(),
                            recipe.order(),
                            groupOf(recipe)
                    );

                    return null;
                }

                costBStack =
                        Optional.of(
                                costBConcrete
                        );
            }

            /*
             * Global trades do not declare a recipe level, so NOVICE provides
             * the non-null level required by DwarfTradeRecipeInput.
             *
             * Non-global trades use their declared recipe level. This input is
             * only used to validate and generate the concrete offer.
             */
            DwarfMerchantData.Level previewLevel =
                    recipe.merchantLevel() != null
                            ? recipe.merchantLevel()
                            : DwarfMerchantData.Level.NOVICE;

            DwarfTradeRecipeInput input =
                    new DwarfTradeRecipeInput(
                            trader.getTradeProfession(),
                            previewLevel,
                            costAStack.copy(),
                            costBConcrete.copy()
                    );

            ItemStack out =
                    recipe.resolveResult(
                            context,
                            input
                    );

            if (out.isEmpty()) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade offer failed: resolved result empty for recipe profession={} level={} order={} group={} traderProfession={} traderLevel={}",
                        recipe.profession(),
                        recipe.merchantLevel(),
                        recipe.order(),
                        groupOf(recipe),
                        trader.getTradeProfession(),
                        trader.getMerchantLevel()
                );

                return null;
            }

            DwarfTradeRecipe.TradeStats stats =
                    recipe.stats();

            DwarfItemCost costA =
                    new DwarfItemCost(
                            costAStack.getItem(),
                            costAStack.getCount()
                    );

            Optional<DwarfItemCost> costB =
                    costBStack.map(stack ->
                            new DwarfItemCost(
                                    stack.getItem(),
                                    stack.getCount()
                            )
                    );

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    out,
                    0,
                    stats.maxUses(),
                    stats.dwarfXp(),
                    stats.priceMultiplier(),
                    0,
                    recipeHolder.id(),
                    groupOf(recipe)
            );
        }

        /**
         * Materializes one concrete merchant cost from the recipe-side dynamic
         * trade cost.
         *
         * The ingredient determines the concrete item. The NumberProvider is
         * evaluated exactly once here, when the merchant offer is created.
         *
         * The resulting ItemStack and later DwarfItemCost therefore contain a
         * fixed count for the lifetime of that offer.
         */
        private static @NotNull ItemStack materializeCost(
                @Nullable TradeCost cost,
                @NotNull LootContext context
        ) {
            if (cost == null) {
                return ItemStack.EMPTY;
            }

            ItemStack[] candidates =
                    cost.candidateItems();

            if (candidates.length == 0) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade cost materialization failed: ingredient has no available items"
                );

                return ItemStack.EMPTY;
            }

            ItemStack resolved =
                    ItemStack.EMPTY;

            for (ItemStack candidate : candidates) {
                if (candidate == null
                        || candidate.isEmpty()) {
                    continue;
                }

                ItemStack candidateCopy =
                        candidate.copy();

                if (!cost.test(candidateCopy)) {
                    continue;
                }

                resolved =
                        candidateCopy;

                break;
            }

            if (resolved.isEmpty()) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade cost materialization failed: no representative stack satisfies the ingredient"
                );

                return ItemStack.EMPTY;
            }

            int resolvedCount =
                    cost.resolveCount(context);

            if (resolvedCount < 1) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade cost materialization failed: resolved count={} must be >= 1",
                        resolvedCount
                );

                return ItemStack.EMPTY;
            }

            resolved.setCount(
                    resolvedCount
            );

            return resolved;
        }

        private static @NotNull LootContext createResultContext(
                @NotNull ServerLevel level,
                @NotNull AbstractTradingEntity trader
        ) {
            return JolCraftRecipeContexts.create(
                    level,
                    RESULT_CONTEXT_PARAMS,
                    builder -> builder
                            .withParameter(
                                    LootContextParams.THIS_ENTITY,
                                    trader
                            )
                            .withParameter(
                                    LootContextParams.ORIGIN,
                                    trader.position()
                            )
            );
        }
    }

    public static @NotNull List<RecipeHolder<DwarfTradeRecipe>>
    getTradeRecipesForMode(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel,
            RandomSource random,
            RefreshMode mode
    ) {
        if (!(level instanceof ServerLevel)) {
            return List.of();
        }

        DwarfProfession resolvedProfession =
                profession != null
                        ? profession
                        : DwarfProfession.NONE;

        DwarfMerchantData.Level resolvedLevel =
                merchantLevel != null
                        ? merchantLevel
                        : DwarfMerchantData.Level.NOVICE;

        RandomSource resolvedRandom =
                random != null
                        ? random
                        : RandomSource.create();

        RefreshMode resolvedMode =
                mode != null
                        ? mode
                        : RefreshMode.FULL;

        List<RecipeHolder<DwarfTradeRecipe>> all =
                getAllTradeRecipes(level);

        if (all.isEmpty()) {
            return List.of();
        }

        List<RecipeHolder<DwarfTradeRecipe>> main =
                new ArrayList<>();

        List<RecipeHolder<DwarfTradeRecipe>> exactLevelPool =
                new ArrayList<>();

        List<RecipeHolder<DwarfTradeRecipe>> cumulativePool =
                new ArrayList<>();

        List<RecipeHolder<DwarfTradeRecipe>> globalPool =
                new ArrayList<>();

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe recipe =
                    holder.value();

            if (recipe.profession() != resolvedProfession) {
                continue;
            }

            TradeGroup group =
                    groupOf(recipe);

            switch (group) {
                case MAIN -> {
                    if (includeMainForMode(resolvedMode)
                            && isUnlocked(
                            recipe,
                            resolvedLevel
                    )) {
                        main.add(
                                holder
                        );
                    }
                }

                case EXACT_LEVEL_POOL -> {
                    if (includePoolForMode(
                            resolvedProfession,
                            TradePoolType.EXACT_LEVEL,
                            resolvedMode
                    ) && isExactLevel(
                            recipe,
                            resolvedLevel
                    )) {
                        exactLevelPool.add(
                                holder
                        );
                    }
                }

                case CUMULATIVE_POOL -> {
                    if (includePoolForMode(
                            resolvedProfession,
                            TradePoolType.CUMULATIVE,
                            resolvedMode
                    ) && isUnlocked(
                            recipe,
                            resolvedLevel
                    )) {
                        cumulativePool.add(
                                holder
                        );
                    }
                }

                case GLOBAL_POOL -> {
                    if (includePoolForMode(
                            resolvedProfession,
                            TradePoolType.GLOBAL,
                            resolvedMode
                    )) {
                        globalPool.add(
                                holder
                        );
                    }
                }
            }
        }

        DwarfProfessionConfig config =
                DwarfProfessionConfigManager.INSTANCE.get(
                        resolvedProfession
                );

        DwarfProfessionTradePoolsConfig pools =
                config.tradePools();

        int exactLevelRolls =
                rollsForMode(
                        pools,
                        TradePoolType.EXACT_LEVEL,
                        resolvedLevel,
                        resolvedMode
                );

        int cumulativeRolls =
                rollsForMode(
                        pools,
                        TradePoolType.CUMULATIVE,
                        resolvedLevel,
                        resolvedMode
                );

        int globalRolls =
                rollsForMode(
                        pools,
                        TradePoolType.GLOBAL,
                        resolvedLevel,
                        resolvedMode
                );

        List<RecipeHolder<DwarfTradeRecipe>> selectedExactLevel =
                pickWeightedWithoutReplacement(
                        exactLevelPool,
                        exactLevelRolls,
                        resolvedRandom
                );

        List<RecipeHolder<DwarfTradeRecipe>> selectedCumulative =
                pickWeightedWithoutReplacement(
                        cumulativePool,
                        cumulativeRolls,
                        resolvedRandom
                );

        List<RecipeHolder<DwarfTradeRecipe>> selectedGlobal =
                pickWeightedWithoutReplacement(
                        globalPool,
                        globalRolls,
                        resolvedRandom
                );

        List<RecipeHolder<DwarfTradeRecipe>> result =
                new ArrayList<>(
                        main.size()
                                + selectedExactLevel.size()
                                + selectedCumulative.size()
                                + selectedGlobal.size()
                );

        result.addAll(
                main
        );

        result.addAll(
                selectedExactLevel
        );

        result.addAll(
                selectedCumulative
        );

        result.addAll(
                selectedGlobal
        );

        sortByPoolThenOrderThenId(
                result
        );

        return List.copyOf(
                result
        );
    }

    public static @NotNull List<RecipeHolder<DwarfTradeRecipe>>
    getAdditionalGlobalTradeRecipes(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel,
            RandomSource random,
            @NotNull Set<ResourceLocation> excludedRecipeIds
    ) {
        DwarfProfession resolvedProfession =
                profession != null
                        ? profession
                        : DwarfProfession.NONE;

        DwarfMerchantData.Level resolvedLevel =
                merchantLevel != null
                        ? merchantLevel
                        : DwarfMerchantData.Level.NOVICE;

        DwarfProfessionConfig config =
                DwarfProfessionConfigManager.INSTANCE.get(
                        resolvedProfession
                );

        int rolls =
                config.tradePools()
                        .get(TradePoolType.GLOBAL)
                        .map(pool ->
                                pool.rollsFor(
                                        TradePoolType.GLOBAL,
                                        resolvedLevel
                                )
                        )
                        .orElse(0);

        return getGlobalTradeRecipes(
                level,
                resolvedProfession,
                random,
                rolls,
                excludedRecipeIds
        );
    }

    public static @NotNull List<RecipeHolder<DwarfTradeRecipe>>
    getGlobalTradeRecipes(
            Level level,
            DwarfProfession profession,
            RandomSource random,
            int rolls,
            @NotNull Set<ResourceLocation> excludedRecipeIds
    ) {
        if (!(level instanceof ServerLevel)) {
            return List.of();
        }

        if (rolls <= 0) {
            return List.of();
        }

        DwarfProfession resolvedProfession =
                profession != null
                        ? profession
                        : DwarfProfession.NONE;

        RandomSource resolvedRandom =
                random != null
                        ? random
                        : RandomSource.create();

        List<RecipeHolder<DwarfTradeRecipe>> candidates =
                new ArrayList<>();

        for (RecipeHolder<DwarfTradeRecipe> holder :
                getAllTradeRecipes(level)) {

            DwarfTradeRecipe recipe =
                    holder.value();

            if (recipe.profession() != resolvedProfession) {
                continue;
            }

            if (groupOf(recipe) != TradeGroup.GLOBAL_POOL) {
                continue;
            }

            if (excludedRecipeIds.contains(holder.id())) {
                continue;
            }

            candidates.add(holder);
        }

        return pickWeightedWithoutReplacement(
                candidates,
                rolls,
                resolvedRandom
        );
    }

    private static boolean includeMainForMode(
            @NotNull RefreshMode mode
    ) {
        return mode == RefreshMode.FULL
                || mode == RefreshMode.REROLL;
    }

    private static boolean includePoolForMode(
            @Nullable DwarfProfession profession,
            @Nullable TradePoolType type,
            @NotNull RefreshMode mode
    ) {
        if (mode == RefreshMode.FULL
                || mode == RefreshMode.REROLL) {
            return true;
        }

        if (mode != RefreshMode.RESTOCK
                || profession == null
                || type == null) {
            return false;
        }

        DwarfProfessionConfig config =
                DwarfProfessionConfigManager.INSTANCE.get(
                        profession
                );

        return config.tradePools()
                .get(type)
                .map(
                        DwarfProfessionTradePoolConfig
                                ::rerollsOnRestock
                )
                .orElse(false);
    }

    private static int rollsForMode(
            @Nullable DwarfProfessionTradePoolsConfig pools,
            @Nullable TradePoolType type,
            @Nullable DwarfMerchantData.Level level,
            @NotNull RefreshMode mode
    ) {
        if (pools == null
                || type == null
                || level == null) {
            return 0;
        }

        return switch (mode) {
            case FULL, REROLL ->
                    pools.get(type)
                            .map(config ->
                                    config.rollsFor(
                                            type,
                                            level
                                    )
                            )
                            .orElse(0);

            case RESTOCK ->
                    pools.get(type)
                            .filter(
                                    DwarfProfessionTradePoolConfig
                                            ::rerollsOnRestock
                            )
                            .map(config ->
                                    config.rollsFor(
                                            type,
                                            level
                                    )
                            )
                            .orElse(0);
        };
    }

    private static @NotNull TradeGroup groupOf(
            @NotNull DwarfTradeRecipe recipe
    ) {
        TradePoolEntry pool =
                recipe.pool();

        if (pool == null
                || pool.group() == null) {
            return TradeGroup.MAIN;
        }

        return pool.group();
    }

    private static boolean isUnlocked(
            @NotNull DwarfTradeRecipe recipe,
            @Nullable DwarfMerchantData.Level merchantLevel
    ) {
        if (recipe.merchantLevel() == null) {
            return false;
        }

        int currentId =
                merchantLevel != null
                        ? merchantLevel.getId()
                        : 0;

        return recipe.merchantLevel()
                .getId()
                <= currentId;
    }

    private static boolean isExactLevel(
            @NotNull DwarfTradeRecipe recipe,
            @Nullable DwarfMerchantData.Level merchantLevel
    ) {
        if (recipe.merchantLevel() == null) {
            return false;
        }

        int currentId =
                merchantLevel != null
                        ? merchantLevel.getId()
                        : 0;

        return recipe.merchantLevel()
                .getId()
                == currentId;
    }

    private static @NotNull List<RecipeHolder<DwarfTradeRecipe>>
    pickWeightedWithoutReplacement(
            @NotNull List<RecipeHolder<DwarfTradeRecipe>> candidates,
            int rolls,
            @Nullable RandomSource random
    ) {
        if (candidates.isEmpty()
                || rolls <= 0) {
            return List.of();
        }

        RandomSource resolvedRandom =
                random != null
                        ? random
                        : RandomSource.create();

        List<RecipeHolder<DwarfTradeRecipe>> pool =
                new ArrayList<>(
                        candidates
                );

        List<RecipeHolder<DwarfTradeRecipe>> selected =
                new ArrayList<>(
                        Math.min(
                                rolls,
                                pool.size()
                        )
                );

        int maxRolls =
                Math.min(
                        rolls,
                        pool.size()
                );

        for (int index = 0;
             index < maxRolls;
             index++) {

            int totalWeight =
                    totalWeight(
                            pool
                    );

            if (totalWeight <= 0) {
                break;
            }

            int pick =
                    resolvedRandom.nextInt(
                            totalWeight
                    );

            int cursor = 0;
            int chosenIndex = -1;

            for (int candidateIndex = 0;
                 candidateIndex < pool.size();
                 candidateIndex++) {

                RecipeHolder<DwarfTradeRecipe> holder =
                        pool.get(
                                candidateIndex
                        );

                int weight =
                        safeWeight(
                                holder.value()
                        );

                if (weight <= 0) {
                    continue;
                }

                cursor += weight;

                if (pick < cursor) {
                    chosenIndex =
                            candidateIndex;

                    break;
                }
            }

            if (chosenIndex < 0) {
                break;
            }

            selected.add(
                    pool.remove(
                            chosenIndex
                    )
            );
        }

        sortByPoolThenOrderThenId(
                selected
        );

        return List.copyOf(
                selected
        );
    }

    private static int totalWeight(
            @NotNull List<RecipeHolder<DwarfTradeRecipe>> recipes
    ) {
        int total = 0;

        for (RecipeHolder<DwarfTradeRecipe> holder : recipes) {
            total += safeWeight(
                    holder.value()
            );
        }

        return Math.max(
                0,
                total
        );
    }

    private static int safeWeight(
            @Nullable DwarfTradeRecipe recipe
    ) {
        if (recipe == null) {
            return TradePoolEntry.DEFAULT_WEIGHT;
        }

        TradePoolEntry pool =
                recipe.pool();

        if (pool == null) {
            return TradePoolEntry.DEFAULT_WEIGHT;
        }

        return Math.max(
                0,
                pool.weight()
        );
    }

    private static int poolPriority(
            @NotNull DwarfTradeRecipe recipe
    ) {
        return switch (groupOf(recipe)) {
            case MAIN -> 0;
            case EXACT_LEVEL_POOL -> 1;
            case CUMULATIVE_POOL -> 2;
            case GLOBAL_POOL -> 3;
        };
    }

    private static int levelSortKey(
            @NotNull DwarfTradeRecipe recipe
    ) {
        if (groupOf(recipe)
                == TradeGroup.GLOBAL_POOL) {
            return 0;
        }

        DwarfMerchantData.Level level =
                recipe.merchantLevel();

        return level != null
                ? level.getId()
                : Integer.MAX_VALUE;
    }

    private static void sortByPoolThenOrderThenId(
            @NotNull List<RecipeHolder<DwarfTradeRecipe>> recipes
    ) {
        recipes.sort(
                Comparator
                        .comparingInt(
                                (
                                        RecipeHolder<DwarfTradeRecipe> holder
                                ) -> poolPriority(
                                        holder.value()
                                )
                        )
                        .thenComparingInt(holder ->
                                levelSortKey(
                                        holder.value()
                                )
                        )
                        .thenComparingInt(holder ->
                                holder.value()
                                        .order()
                        )
                        .thenComparing(
                                RecipeHolder::id
                        )
        );
    }

    private static @NotNull List<RecipeHolder<DwarfTradeRecipe>>
    getAllTradeRecipes(
            Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        var type =
                JolCraftRecipes
                        .DWARF_TRADE_TYPE
                        .get();

        Collection<RecipeHolder<?>> all =
                serverLevel.getServer()
                        .getRecipeManager()
                        .getRecipes();

        List<RecipeHolder<DwarfTradeRecipe>> result = getRecipeHolders(all, type);

        return List.copyOf(
                result
        );
    }

    private static @NotNull List<RecipeHolder<DwarfTradeRecipe>> getRecipeHolders(Collection<RecipeHolder<?>> all, RecipeType<DwarfTradeRecipe> type) {
        List<RecipeHolder<DwarfTradeRecipe>> result =
                new ArrayList<>();

        for (RecipeHolder<?> holder : all) {
            if (holder.value()
                    .getType() != type) {
                continue;
            }

            if (!(holder.value()
                    instanceof DwarfTradeRecipe trade)) {
                continue;
            }

            result.add(
                    new RecipeHolder<>(
                            holder.id(),
                            trade
                    )
            );
        }
        return result;
    }
}