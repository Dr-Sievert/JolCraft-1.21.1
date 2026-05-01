package net.sievert.jolcraft.world.entity.custom.dwarf.trade;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolsConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradePoolType;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradePoolEntry;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipeInput;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class DwarfTrades {

    private DwarfTrades() {}

    public enum RefreshMode {
        FULL,
        RESTOCK,
        REROLL
    }

    public static final class RecipeListing {
        private final DwarfTradeRecipe recipe;

        public RecipeListing(DwarfTradeRecipe recipe) {
            this.recipe = recipe;
        }

        @Nullable
        public DwarfMerchantOffer getOffer(AbstractTradingEntity trader) {
            if (recipe == null) {
                return null;
            }

            if (!(trader.level() instanceof ServerLevel serverLevel)) {
                return null;
            }

            WorldContext ctx = new WorldContext(serverLevel, null, trader);

            ItemStack costAStack = materializeCost(recipe.costA(), ctx);
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

            ItemStack costBConcrete = ItemStack.EMPTY;
            Optional<ItemStack> costBStack = Optional.empty();

            if (recipe.costB() != null) {
                costBConcrete = materializeCost(recipe.costB(), ctx);
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
                costBStack = Optional.of(costBConcrete);
            }

            DwarfMerchantData.Level previewLevel = recipe.merchantLevel() != null
                    ? recipe.merchantLevel()
                    : DwarfMerchantData.Level.NOVICE;

            DwarfTradeRecipeInput input = new DwarfTradeRecipeInput(
                    ctx,
                    trader.getTradeProfession(),
                    previewLevel,
                    costAStack.copy(),
                    costBConcrete.copy()
            );

            ItemStack out = recipe.assemble(input, serverLevel.registryAccess());
            if (out.isEmpty()) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade offer failed: assembled result empty for recipe profession={} level={} order={} group={} traderProfession={} traderLevel={}",
                        recipe.profession(),
                        recipe.merchantLevel(),
                        recipe.order(),
                        groupOf(recipe),
                        trader.getTradeProfession(),
                        trader.getMerchantLevel()
                );
                return null;
            }

            DwarfTradeRecipe.TradeStats stats = recipe.stats();

            DwarfItemCost costA = new DwarfItemCost(costAStack.getItem(), costAStack.getCount());
            Optional<DwarfItemCost> costB = costBStack.map(s -> new DwarfItemCost(s.getItem(), s.getCount()));

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    out,
                    0,
                    stats.maxUses(),
                    stats.dwarfXp(),
                    stats.priceMultiplier(),
                    0,
                    groupOf(recipe)
            );
        }

        private static @NotNull ItemStack materializeCost(@Nullable ItemInput in, @NotNull WorldContext ctx) {
            if (in == null) {
                return ItemStack.EMPTY;
            }

            Holder<Item> holder = resolveCostItem(in, ctx);
            if (holder == null) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade cost materialization failed: no resolvable item"
                );
                return ItemStack.EMPTY;
            }

            int rolled = 1;
            if (in.count() != null) {
                rolled = in.count().roll(ctx.random());
            }

            if (rolled < 1) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade cost materialization failed: rolled < 1 for item {}",
                        holder.getRegisteredName()
                );
                return ItemStack.EMPTY;
            }

            ItemStack stack = new ItemStack(holder.value(), rolled);
            if (!in.matches(ctx, stack)) {
                JolCraftLogs.warn(
                        JolCraftLogTags.ENTITY,
                        "Dwarf trade cost materialization failed: generated stack {} x{} does not match ItemInput",
                        stack.getItem(),
                        stack.getCount()
                );
                return ItemStack.EMPTY;
            }

            return stack;
        }

        @Nullable
        private static Holder<Item> resolveCostItem(@NotNull ItemInput in, @NotNull WorldContext ctx) {
            Optional<Holder<Item>> concrete = in.singleConcrete(Registries.ITEM);
            if (concrete.isPresent()) {
                return concrete.get();
            }

            var lookup = ctx.level().registryAccess().lookupOrThrow(Registries.ITEM);

            for (var introspection : in.introspections()) {
                if (!Registries.ITEM.equals(introspection.registryKey())) {
                    continue;
                }

                var tagOpt = introspection.singleTagOpt();
                if (tagOpt.isEmpty()) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                var tag = (net.minecraft.tags.TagKey<Item>) tagOpt.get();

                var namedOpt = lookup.get(tag);
                if (namedOpt.isEmpty()) {
                    continue;
                }

                var named = namedOpt.get();
                if (named.size() == 0) {
                    continue;
                }

                return named.get(0);
            }

            return null;
        }
    }

    public static @NotNull List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesForMode(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel,
            RandomSource random,
            RefreshMode mode
    ) {
        if (!(level instanceof ServerLevel)) {
            return List.of();
        }

        DwarfProfession prof = profession != null ? profession : DwarfProfession.NONE;
        DwarfMerchantData.Level lvl = merchantLevel != null ? merchantLevel : DwarfMerchantData.Level.NOVICE;
        RandomSource rng = random != null ? random : RandomSource.create();
        RefreshMode refreshMode = mode != null ? mode : RefreshMode.FULL;

        List<RecipeHolder<DwarfTradeRecipe>> all = getAllTradeRecipes(level);
        if (all.isEmpty()) {
            return List.of();
        }

        List<RecipeHolder<DwarfTradeRecipe>> main = new ArrayList<>();
        List<RecipeHolder<DwarfTradeRecipe>> exactLevelPool = new ArrayList<>();
        List<RecipeHolder<DwarfTradeRecipe>> cumulativePool = new ArrayList<>();
        List<RecipeHolder<DwarfTradeRecipe>> globalPool = new ArrayList<>();

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe recipe = holder.value();
            if (recipe.profession() != prof) {
                continue;
            }

            TradeGroup group = groupOf(recipe);

            switch (group) {
                case MAIN -> {
                    if (includeMainForMode(refreshMode) && isUnlocked(recipe, lvl)) {
                        main.add(holder);
                    }
                }
                case EXACT_LEVEL_POOL -> {
                    if (includePoolForMode(prof, TradePoolType.EXACT_LEVEL, refreshMode)
                            && isExactLevel(recipe, lvl)) {
                        exactLevelPool.add(holder);
                    }
                }
                case CUMULATIVE_POOL -> {
                    if (includePoolForMode(prof, TradePoolType.CUMULATIVE, refreshMode)
                            && isUnlocked(recipe, lvl)) {
                        cumulativePool.add(holder);
                    }
                }
                case GLOBAL_POOL -> {
                    if (includePoolForMode(prof, TradePoolType.GLOBAL, refreshMode)) {
                        globalPool.add(holder);
                    }
                }
            }
        }

        DwarfProfessionConfig cfg = DwarfProfessionConfigManager.INSTANCE.get(prof);
        DwarfProfessionTradePoolsConfig pools = cfg.tradePools();

        int exactLevelRolls = rollsForMode(pools, TradePoolType.EXACT_LEVEL, lvl, refreshMode);
        int cumulativeRolls = rollsForMode(pools, TradePoolType.CUMULATIVE, lvl, refreshMode);
        int globalRolls = rollsForMode(pools, TradePoolType.GLOBAL, lvl, refreshMode);

        List<RecipeHolder<DwarfTradeRecipe>> selectedExactLevel =
                pickWeightedWithoutReplacement(exactLevelPool, exactLevelRolls, rng);

        List<RecipeHolder<DwarfTradeRecipe>> selectedCumulative =
                pickWeightedWithoutReplacement(cumulativePool, cumulativeRolls, rng);

        List<RecipeHolder<DwarfTradeRecipe>> selectedGlobal =
                pickWeightedWithoutReplacement(globalPool, globalRolls, rng);

        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>(
                main.size() + selectedExactLevel.size() + selectedCumulative.size() + selectedGlobal.size()
        );

        out.addAll(main);
        out.addAll(selectedExactLevel);
        out.addAll(selectedCumulative);
        out.addAll(selectedGlobal);

        sortByPoolThenOrderThenId(out);
        return List.copyOf(out);
    }

    public static @NotNull List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesAtLevel(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel
    ) {
        return findTradeRecipesAtLevel(level, profession, merchantLevel);
    }

    private static boolean includeMainForMode(@NotNull RefreshMode mode) {
        return mode == RefreshMode.FULL || mode == RefreshMode.REROLL;
    }

    private static boolean includePoolForMode(
            @Nullable DwarfProfession profession,
            @Nullable TradePoolType type,
            @NotNull RefreshMode mode
    ) {
        if (mode == RefreshMode.FULL || mode == RefreshMode.REROLL) {
            return true;
        }

        if (mode != RefreshMode.RESTOCK || profession == null || type == null) {
            return false;
        }

        DwarfProfessionConfig cfg = DwarfProfessionConfigManager.INSTANCE.get(profession);
        return cfg.tradePools()
                .get(type)
                .map(DwarfProfessionTradePoolConfig::rerollsOnRestock)
                .orElse(false);
    }

    private static int rollsForMode(
            @Nullable DwarfProfessionTradePoolsConfig pools,
            @Nullable TradePoolType type,
            @Nullable DwarfMerchantData.Level level,
            @NotNull RefreshMode mode
    ) {
        if (pools == null || type == null || level == null) {
            return 0;
        }

        return switch (mode) {
            case FULL, REROLL -> pools.get(type)
                    .map(cfg -> cfg.rollsFor(type, level))
                    .orElse(0);
            case RESTOCK -> pools.get(type)
                    .filter(DwarfProfessionTradePoolConfig::rerollsOnRestock)
                    .map(cfg -> cfg.rollsFor(type, level))
                    .orElse(0);
        };
    }

    private static @NotNull TradeGroup groupOf(@NotNull DwarfTradeRecipe recipe) {
        TradePoolEntry pool = recipe.pool();
        if (pool == null || pool.group() == null) {
            return TradeGroup.MAIN;
        }
        return pool.group();
    }

    private static boolean isUnlocked(@NotNull DwarfTradeRecipe recipe, @Nullable DwarfMerchantData.Level merchantLevel) {
        if (recipe.merchantLevel() == null) {
            return false;
        }

        int currentId = merchantLevel != null ? merchantLevel.getId() : 0;
        return recipe.merchantLevel().getId() <= currentId;
    }

    private static boolean isExactLevel(@NotNull DwarfTradeRecipe recipe, @Nullable DwarfMerchantData.Level merchantLevel) {
        if (recipe.merchantLevel() == null) {
            return false;
        }

        int currentId = merchantLevel != null ? merchantLevel.getId() : 0;
        return recipe.merchantLevel().getId() == currentId;
    }

    private static @NotNull List<RecipeHolder<DwarfTradeRecipe>> pickWeightedWithoutReplacement(
            @NotNull List<RecipeHolder<DwarfTradeRecipe>> candidates,
            int rolls,
            @Nullable RandomSource random
    ) {
        if (candidates.isEmpty() || rolls <= 0) {
            return List.of();
        }

        RandomSource rng = random != null ? random : RandomSource.create();

        List<RecipeHolder<DwarfTradeRecipe>> pool = new ArrayList<>(candidates);
        List<RecipeHolder<DwarfTradeRecipe>> selected = new ArrayList<>(Math.min(rolls, pool.size()));

        int maxRolls = Math.min(rolls, pool.size());
        for (int i = 0; i < maxRolls; i++) {
            int totalWeight = totalWeight(pool);
            if (totalWeight <= 0) {
                break;
            }

            int pick = rng.nextInt(totalWeight);
            int cursor = 0;
            int chosenIndex = -1;

            for (int idx = 0; idx < pool.size(); idx++) {
                RecipeHolder<DwarfTradeRecipe> holder = pool.get(idx);
                int weight = safeWeight(holder.value());
                if (weight <= 0) {
                    continue;
                }

                cursor += weight;
                if (pick < cursor) {
                    chosenIndex = idx;
                    break;
                }
            }

            if (chosenIndex < 0) {
                break;
            }

            selected.add(pool.remove(chosenIndex));
        }

        sortByPoolThenOrderThenId(selected);
        return List.copyOf(selected);
    }

    private static int totalWeight(@NotNull List<RecipeHolder<DwarfTradeRecipe>> recipes) {
        int total = 0;
        for (RecipeHolder<DwarfTradeRecipe> holder : recipes) {
            total += safeWeight(holder.value());
        }
        return Math.max(0, total);
    }

    private static int safeWeight(@Nullable DwarfTradeRecipe recipe) {
        if (recipe == null) {
            return 1;
        }

        TradePoolEntry pool = recipe.pool();
        if (pool == null || pool.weight() == null) {
            return 1;
        }

        return Math.max(0, pool.weight().value());
    }

    private static @NotNull List<RecipeHolder<DwarfTradeRecipe>> findTradeRecipesAtLevel(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> all = getAllTradeRecipes(level);
        if (all.isEmpty()) {
            return List.of();
        }

        List<RecipeHolder<DwarfTradeRecipe>> filtered = new ArrayList<>();
        int want = merchantLevel != null ? merchantLevel.getId() : 0;

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe recipe = holder.value();

            if (recipe.profession() != profession) {
                continue;
            }

            if (recipe.merchantLevel() == null) {
                continue;
            }

            if (recipe.merchantLevel().getId() != want) {
                continue;
            }

            filtered.add(holder);
        }

        sortByPoolThenOrderThenId(filtered);
        return List.copyOf(filtered);
    }

    private static int poolPriority(@NotNull DwarfTradeRecipe recipe) {
        return switch (groupOf(recipe)) {
            case MAIN -> 0;
            case EXACT_LEVEL_POOL -> 1;
            case CUMULATIVE_POOL -> 2;
            case GLOBAL_POOL -> 3;
        };
    }

    private static int levelSortKey(@NotNull DwarfTradeRecipe recipe) {
        if (groupOf(recipe) == TradeGroup.GLOBAL_POOL) {
            return 0;
        }

        DwarfMerchantData.Level level = recipe.merchantLevel();
        return level != null ? level.getId() : Integer.MAX_VALUE;
    }

    private static void sortByPoolThenOrderThenId(@NotNull List<RecipeHolder<DwarfTradeRecipe>> recipes) {
        recipes.sort(
                Comparator
                        .comparingInt((RecipeHolder<DwarfTradeRecipe> holder) -> poolPriority(holder.value()))
                        .thenComparingInt(holder -> levelSortKey(holder.value()))
                        .thenComparingInt(holder -> holder.value().order())
                        .thenComparing(RecipeHolder::id)
        );
    }

    private static @NotNull List<RecipeHolder<DwarfTradeRecipe>> getAllTradeRecipes(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        var type = JolCraftRecipes.DWARF_TRADE_TYPE.get();
        Collection<RecipeHolder<?>> all = serverLevel.getServer().getRecipeManager().getRecipes();

        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : all) {
            if (holder.value().getType() != type) {
                continue;
            }

            if (!(holder.value() instanceof DwarfTradeRecipe trade)) {
                continue;
            }

            out.add(new RecipeHolder<>(holder.id(), trade));
        }

        return List.copyOf(out);
    }
}