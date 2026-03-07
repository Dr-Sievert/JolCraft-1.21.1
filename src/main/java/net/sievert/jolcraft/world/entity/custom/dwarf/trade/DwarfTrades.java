package net.sievert.jolcraft.world.entity.custom.dwarf.trade;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradePoolEntry;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
            if (recipe == null || recipe == DwarfTradeRecipe.EMPTY) return null;
            if (!(trader.level() instanceof ServerLevel serverLevel)) return null;

            Player player = trader.getTradingPlayer();
            if (player == null) return null;

            WorldContext ctx = new WorldContext(
                    serverLevel,
                    player,
                    trader
            );

            ItemStack costAStack = materializeCost(recipe.costA(), ctx);
            if (costAStack.isEmpty()) return null;

            Optional<ItemStack> costBStack = Optional.empty();
            if (recipe.costB() != null && recipe.costB() != ItemInput.EMPTY) {
                ItemStack b = materializeCost(recipe.costB(), ctx);
                if (b.isEmpty()) return null;
                costBStack = Optional.of(b);
            }

            ItemStack out = materializeResult(recipe.result(), ctx);
            if (out.isEmpty()) return null;

            DwarfTradeRecipe.TradeStats stats = recipe.stats();
            if (stats == null) stats = DwarfTradeRecipe.TradeStats.DEFAULT;

            DwarfItemCost costA = new DwarfItemCost(costAStack.getItem(), costAStack.getCount());
            Optional<DwarfItemCost> costB = costBStack.map(s -> new DwarfItemCost(s.getItem(), s.getCount()));

            TradePoolEntry pool = recipe.pool();
            TradeGroup group = pool != null && pool.group() != null ? pool.group() : TradeGroup.MAIN;

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    out,
                    0,
                    stats.maxUses(),
                    stats.dwarfXp(),
                    stats.priceMultiplier(),
                    0,
                    group
            );
        }

        private static ItemStack materializeCost(ItemInput in, WorldContext ctx) {
            if (in == null || in == ItemInput.EMPTY) return ItemStack.EMPTY;

            Optional<Holder<Item>> itemOpt = in.singleConcrete(Registries.ITEM);
            if (itemOpt.isEmpty()) return ItemStack.EMPTY;

            int rolled = 1;
            if (in.count() != null) {
                rolled = in.count().roll(ctx.random());
            }
            if (rolled < 1) return ItemStack.EMPTY;

            ItemStack stack = new ItemStack(itemOpt.get().value(), rolled);
            return in.matches(ctx, stack) ? stack : ItemStack.EMPTY;
        }

        private static ItemStack materializeResult(ItemOutput out, WorldContext ctx) {
            if (out == null || out == ItemOutput.EMPTY) return ItemStack.EMPTY;

            ItemSpec spec = out.result();
            if (spec == null || spec == ItemSpec.EMPTY) return ItemStack.EMPTY;

            ItemStack stack = spec.create(ctx);
            if (stack.isEmpty()) return ItemStack.EMPTY;

            out.transforms().apply(ctx, stack);
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }
    }

    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesForMode(
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
        List<RecipeHolder<DwarfTradeRecipe>> globalPool = new ArrayList<>();
        List<RecipeHolder<DwarfTradeRecipe>> cumulativePool = new ArrayList<>();
        List<RecipeHolder<DwarfTradeRecipe>> exactLevelPool = new ArrayList<>();

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe recipe = holder.value();
            if (recipe.profession() != prof) continue;

            TradePoolEntry pool = recipe.pool();
            TradeGroup group = pool != null ? pool.group() : TradeGroup.MAIN;
            if (group == null) {
                group = TradeGroup.MAIN;
            }

            switch (group) {
                case MAIN -> {
                    if (includeMainForMode(refreshMode) && isUnlocked(recipe, lvl)) {
                        main.add(holder);
                    }
                }
                case GLOBAL_POOL -> {
                    if (includePoolForMode(prof, DwarfProfessionConfig.PoolType.GLOBAL, refreshMode)) {
                        globalPool.add(holder);
                    }
                }
                case CUMULATIVE_POOL -> {
                    if (includePoolForMode(prof, DwarfProfessionConfig.PoolType.CUMULATIVE, refreshMode)
                            && isUnlocked(recipe, lvl)) {
                        cumulativePool.add(holder);
                    }
                }
                case EXACT_LEVEL_POOL -> {
                    if (includePoolForMode(prof, DwarfProfessionConfig.PoolType.EXACT_LEVEL, refreshMode)
                            && isExactLevel(recipe, lvl)) {
                        exactLevelPool.add(holder);
                    }
                }
            }
        }

        sortByOrderThenId(main);

        DwarfProfessionConfig cfg = DwarfProfessionConfigManager.INSTANCE.get(prof);
        DwarfProfessionConfig.TradePools pools = cfg.tradePools();

        int globalRolls = rollsForMode(pools, DwarfProfessionConfig.PoolType.GLOBAL, lvl, refreshMode);
        int cumulativeRolls = rollsForMode(pools, DwarfProfessionConfig.PoolType.CUMULATIVE, lvl, refreshMode);
        int exactLevelRolls = rollsForMode(pools, DwarfProfessionConfig.PoolType.EXACT_LEVEL, lvl, refreshMode);

        List<RecipeHolder<DwarfTradeRecipe>> selectedGlobal =
                pickWeightedWithoutReplacement(globalPool, globalRolls, rng);

        List<RecipeHolder<DwarfTradeRecipe>> selectedCumulative =
                pickWeightedWithoutReplacement(cumulativePool, cumulativeRolls, rng);

        List<RecipeHolder<DwarfTradeRecipe>> selectedExactLevel =
                pickWeightedWithoutReplacement(exactLevelPool, exactLevelRolls, rng);

        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>(
                main.size() + selectedGlobal.size() + selectedCumulative.size() + selectedExactLevel.size()
        );

        out.addAll(main);
        out.addAll(selectedGlobal);
        out.addAll(selectedCumulative);
        out.addAll(selectedExactLevel);

        sortByOrderThenId(out);
        return List.copyOf(out);
    }

    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesAtLevel(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel
    ) {
        return findTradeRecipesAtLevel(level, profession, merchantLevel);
    }

    private static boolean includeMainForMode(RefreshMode mode) {
        return mode == RefreshMode.FULL || mode == RefreshMode.REROLL;
    }

    private static boolean includePoolForMode(
            DwarfProfession profession,
            DwarfProfessionConfig.PoolType type,
            RefreshMode mode
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
                .map(DwarfProfessionConfig.PoolConfig::rerollsOnRestock)
                .orElse(false);
    }

    private static int rollsForMode(
            DwarfProfessionConfig.TradePools pools,
            DwarfProfessionConfig.PoolType type,
            DwarfMerchantData.Level level,
            RefreshMode mode
    ) {
        if (pools == null || type == null) {
            return 0;
        }

        return switch (mode) {
            case FULL, REROLL -> pools.get(type).map(cfg -> cfg.rolls().rollsFor(level)).orElse(0);
            case RESTOCK -> pools.get(type)
                    .filter(DwarfProfessionConfig.PoolConfig::rerollsOnRestock)
                    .map(cfg -> cfg.rolls().rollsFor(level))
                    .orElse(0);
        };
    }

    private static boolean isUnlocked(DwarfTradeRecipe recipe, DwarfMerchantData.Level merchantLevel) {
        int want = merchantLevel != null ? merchantLevel.getId() : 0;
        DwarfMerchantData.Level req = recipe.merchantLevel();
        int reqId = req != null ? req.getId() : 0;
        return reqId <= want;
    }

    private static boolean isExactLevel(DwarfTradeRecipe recipe, DwarfMerchantData.Level merchantLevel) {
        int want = merchantLevel != null ? merchantLevel.getId() : 0;
        DwarfMerchantData.Level req = recipe.merchantLevel();
        int reqId = req != null ? req.getId() : 0;
        return reqId == want;
    }

    private static List<RecipeHolder<DwarfTradeRecipe>> pickWeightedWithoutReplacement(
            List<RecipeHolder<DwarfTradeRecipe>> candidates,
            int rolls,
            RandomSource random
    ) {
        if (candidates.isEmpty() || rolls <= 0) {
            return List.of();
        }

        List<RecipeHolder<DwarfTradeRecipe>> pool = new ArrayList<>(candidates);
        List<RecipeHolder<DwarfTradeRecipe>> selected = new ArrayList<>(Math.min(rolls, pool.size()));

        int maxRolls = Math.min(rolls, pool.size());
        for (int i = 0; i < maxRolls; i++) {
            int totalWeight = totalWeight(pool);
            if (totalWeight <= 0) {
                break;
            }

            int pick = random.nextInt(totalWeight);
            int cursor = 0;
            int chosenIndex = -1;

            for (int idx = 0; idx < pool.size(); idx++) {
                RecipeHolder<DwarfTradeRecipe> holder = pool.get(idx);
                int weight = safeWeight(holder.value());
                if (weight <= 0) continue;

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

        sortByOrderThenId(selected);
        return List.copyOf(selected);
    }

    private static int totalWeight(List<RecipeHolder<DwarfTradeRecipe>> recipes) {
        int total = 0;
        for (RecipeHolder<DwarfTradeRecipe> holder : recipes) {
            total += safeWeight(holder.value());
        }
        return Math.max(0, total);
    }

    private static int safeWeight(DwarfTradeRecipe recipe) {
        if (recipe == null || recipe.pool() == null || recipe.pool().weight() == null) {
            return 1;
        }
        return Math.max(0, recipe.pool().weight().safe());
    }

    private static List<RecipeHolder<DwarfTradeRecipe>> findTradeRecipesAtLevel(
            Level level,
            DwarfProfession profession,
            DwarfMerchantData.Level merchantLevel
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> all = getAllTradeRecipes(level);
        List<RecipeHolder<DwarfTradeRecipe>> filtered = new ArrayList<>();

        int want = merchantLevel != null ? merchantLevel.getId() : 0;

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe r = holder.value();

            if (r.profession() != profession) continue;

            DwarfMerchantData.Level req = r.merchantLevel();
            int reqId = req != null ? req.getId() : 0;

            if (reqId != want) continue;

            filtered.add(holder);
        }

        sortByOrderThenId(filtered);
        return List.copyOf(filtered);
    }

    private static void sortByOrderThenId(List<RecipeHolder<DwarfTradeRecipe>> recipes) {
        recipes.sort(
                Comparator
                        .comparingInt((RecipeHolder<DwarfTradeRecipe> h) -> {
                            DwarfMerchantData.Level lvl = h.value().merchantLevel();
                            return (lvl != null) ? lvl.getId() : 0;
                        })
                        .thenComparingInt(h -> h.value().order())
                        .thenComparing(RecipeHolder::id)
        );
    }

    private static List<RecipeHolder<DwarfTradeRecipe>> getAllTradeRecipes(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        var type = JolCraftRecipes.DWARF_TRADE_TYPE.get();
        var all = serverLevel.getServer().getRecipeManager().getRecipes();

        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : all) {
            if (holder.value().getType() != type) continue;

            DwarfTradeRecipe trade = (DwarfTradeRecipe) holder.value();
            out.add(new RecipeHolder<>(holder.id(), trade));
        }

        return List.copyOf(out);
    }
}