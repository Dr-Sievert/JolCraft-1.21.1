package net.sievert.jolcraft.datagen.recipe.subprovider.trade.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "deprecation"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractDwarfTrades {

    private static final String ROOT_FOLDER = JolCraftRecipeIds.DWARF_TRADE;

    /**
     * Datagen-facing merchant levels.
     * Subclasses can write: NOVICE, APPRENTICE, ...
     */
    protected enum Level {

        NOVICE(DwarfMerchantData.Level.NOVICE),
        APPRENTICE(DwarfMerchantData.Level.APPRENTICE),
        JOURNEYMAN(DwarfMerchantData.Level.JOURNEYMAN),
        EXPERT(DwarfMerchantData.Level.EXPERT),
        MASTER(DwarfMerchantData.Level.MASTER);

        private final DwarfMerchantData.Level backing;

        Level(DwarfMerchantData.Level backing) {
            this.backing = backing;
        }

        public int getId() {
            return backing.getId();
        }
    }

    protected static final Level NOVICE = Level.NOVICE;
    protected static final Level APPRENTICE = Level.APPRENTICE;
    protected static final Level JOURNEYMAN = Level.JOURNEYMAN;
    protected static final Level EXPERT = Level.EXPERT;
    protected static final Level MASTER = Level.MASTER;

    protected abstract @NotNull DwarfProfession profession();

    public abstract void addTrades(AbstractRecipeProvider p);

    protected final String fullFolder() {
        return JolCraftStrings.slashed(ROOT_FOLDER, profession().getId());
    }

    // =====================================================================
    // ORDER (auto, resets per level)
    // =====================================================================

    /**
     * Per-(level,pool) order counters. We assign order only for MAIN trades, but
     * we track per pool to keep the behavior deterministic if you later decide to
     * order pools too.
     */
    private final Map<Level, Map<DwarfTradeRecipe.TradePool, Integer>> nextOrderByLevel = new EnumMap<>(Level.class);

    private int nextOrder(Level level, DwarfTradeRecipe.TradePool pool) {
        Map<DwarfTradeRecipe.TradePool, Integer> byPool =
                nextOrderByLevel.computeIfAbsent(level, __ -> new EnumMap<>(DwarfTradeRecipe.TradePool.class));

        int next = byPool.getOrDefault(pool, 1);
        byPool.put(pool, next + 1);
        return next;
    }

    private OptionalInt autoOrder(Level level, DwarfTradeRecipe.TradePool pool) {
        // Your spec: "order resets per level". We only enforce ordering for MAIN trades.
        if (pool != DwarfTradeRecipe.TradePool.MAIN) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(nextOrder(level, pool));
    }

    // =====================================================================
    // Amount helpers (int OR range)
    // =====================================================================

    protected static DwarfTradeRecipe.TradeAmount amount(int value) {
        return DwarfTradeRecipe.TradeAmount.fixed(value);
    }

    protected static DwarfTradeRecipe.TradeAmount amount(int min, int max) {
        return new DwarfTradeRecipe.TradeAmount(min, max);
    }

    // =====================================================================
    // Hooks (single-result transforms)
    //
    // IMPORTANT: After the centralization refactor, hooks are RECIPE-LEVEL fields
    // (not inside TradeResult). Datagen still wants a "bundle" so call sites stay clean.
    // =====================================================================

    protected record Hooks(
            Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
            Optional<String> stackModifierId,
            Optional<DataComponentPatch> resultPatch
    ) {
        public static final Hooks EMPTY = new Hooks(Optional.empty(), Optional.empty(), Optional.empty());
    }

    protected static Hooks hooksWithPatch(DataComponentPatch patch) {
        return new Hooks(Optional.empty(), Optional.empty(), Optional.of(patch));
    }

    protected static Hooks hooksWithModifier(String stackModifierId) {
        return new Hooks(Optional.empty(), Optional.of(stackModifierId), Optional.empty());
    }

    protected static Hooks hooksWithEnchant(ResourceKey<EnchantmentProvider> providerKey) {
        return new Hooks(Optional.of(providerKey), Optional.empty(), Optional.empty());
    }

    // =====================================================================
    // Core builder (single codepath, explicit pool/weight)
    // NOTE: idPath is LEVEL-LESS (trade() prefixes it with the level)
    // Order is auto-assigned here for MAIN trades, per level, in call order.
    // =====================================================================

    protected final void trade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradePool pool,
            OptionalInt weight,
            boolean exactLevel,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            Hooks hooks,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath,
            Optional<String> suffix
    ) {
        String leveledId = levelId(level) + "_" + idPath;

        String finalId = suffix.filter(s -> !s.isBlank())
                .map(s -> leveledId + "_" + s)
                .orElse(leveledId);

        OptionalInt order = autoOrder(level, pool);

        DwarfTradeRecipe recipe = new DwarfTradeRecipe(
                profession(),
                level.getId(),
                pool,
                weight,
                order,
                exactLevel,
                costA,
                costB,
                result,
                hooks.enchantmentProvider(),
                hooks.stackModifierId(),
                hooks.resultPatch(),
                Math.max(1, maxUses),
                villagerXp,
                priceMultiplier
        );

        save(p, finalId, recipe);
    }

    // =====================================================================
    // TradeCost helpers
    // =====================================================================

    protected static DwarfTradeRecipe.TradeCost cost(ItemLike item, int count) {
        return cost(item.asItem(), amount(count));
    }

    protected static DwarfTradeRecipe.TradeCost cost(ItemLike item, int min, int max) {
        return cost(item.asItem(), amount(min, max));
    }

    protected static DwarfTradeRecipe.TradeCost cost(ItemStack stack, int count) {
        return cost(stack.getItem(), amount(count));
    }

    protected static DwarfTradeRecipe.TradeCost cost(ItemStack stack, int min, int max) {
        return cost(stack.getItem(), amount(min, max));
    }

    protected static DwarfTradeRecipe.TradeCost cost(ItemStack stack, DwarfTradeRecipe.TradeAmount amount) {
        return cost(stack.getItem(), amount);
    }

    protected static DwarfTradeRecipe.TradeCost cost(TagKey<Item> tag, DwarfTradeRecipe.TradeAmount amount) {
        return new DwarfTradeRecipe.TradeCost(
                new DwarfTradeRecipe.TradeCostIngredient.TagIngredient(tag),
                amount
        );
    }

    /**
     * IMPORTANT: Datagen must serialize registry-bound holders.
     */
    protected static DwarfTradeRecipe.TradeCost cost(Item item, DwarfTradeRecipe.TradeAmount amount) {
        return new DwarfTradeRecipe.TradeCost(
                new DwarfTradeRecipe.TradeCostIngredient.ItemIngredient(item.builtInRegistryHolder()),
                amount
        );
    }

    // =====================================================================
    // Currency sugar (no coin item at call sites)
    // =====================================================================

    protected final DwarfTradeRecipe.TradeCost coins(int count) {
        return coins(amount(count));
    }

    protected final DwarfTradeRecipe.TradeCost coins(int min, int max) {
        return coins(amount(min, max));
    }

    protected final DwarfTradeRecipe.TradeCost coins(DwarfTradeRecipe.TradeAmount amount) {
        return cost(JolCraftTags.Items.COINS, amount);
    }

    protected final DwarfTradeRecipe.TradeResult coinsResult(int count) {
        return coinsResult(amount(count));
    }

    protected final DwarfTradeRecipe.TradeResult coinsResult(int min, int max) {
        return coinsResult(amount(min, max));
    }

    protected final DwarfTradeRecipe.TradeResult coinsResult(DwarfTradeRecipe.TradeAmount amount) {
        return new DwarfTradeRecipe.TradeResult.ItemResult(
                coinItem().asItem().builtInRegistryHolder(),
                amount
        );
    }

    protected ItemLike coinItem() {
        return JolCraftItems.GOLD_COIN.get();
    }

    // =====================================================================
    // Result helpers (RESULTS: ITEM or MAP only)
    // =====================================================================

    protected static DwarfTradeRecipe.TradeResult itemResult(ItemLike item, int count) {
        return new DwarfTradeRecipe.TradeResult.ItemResult(
                item.asItem().builtInRegistryHolder(),
                amount(count)
        );
    }

    protected static DwarfTradeRecipe.TradeResult itemResult(ItemLike item, int min, int max) {
        return new DwarfTradeRecipe.TradeResult.ItemResult(
                item.asItem().builtInRegistryHolder(),
                amount(min, max)
        );
    }

    protected static DwarfTradeRecipe.TradeResult itemResult(ItemStack stack, int count) {
        return new DwarfTradeRecipe.TradeResult.ItemResult(
                stack.getItem().builtInRegistryHolder(),
                amount(count)
        );
    }

    protected static DwarfTradeRecipe.TradeResult mapResult(
            TagKey<Structure> destinationStructureTag,
            String mapDisplayNameKey,
            Holder<MapDecorationType> decorationType
    ) {
        ResourceLocation decoId = decorationType.unwrapKey()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Map decoration type is not registered: " + decorationType
                ))
                .location();

        return new DwarfTradeRecipe.TradeResult.MapResult(
                new DwarfTradeRecipe.MapTradeData(destinationStructureTag, mapDisplayNameKey, decoId)
        );
    }

    // =====================================================================
    // High-level helpers (common patterns)
    // =====================================================================

    protected final void mainTrade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            Hooks hooks,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath
    ) {
        trade(
                p,
                level,
                DwarfTradeRecipe.TradePool.MAIN,
                OptionalInt.empty(),
                false,
                costA,
                costB,
                result,
                hooks,
                maxUses,
                villagerXp,
                priceMultiplier,
                idPath,
                Optional.empty()
        );
    }

    protected final void mainTrade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath
    ) {
        mainTrade(
                p,
                level,
                costA,
                costB,
                result,
                Hooks.EMPTY,
                maxUses,
                villagerXp,
                priceMultiplier,
                idPath
        );
    }

    protected final void pooledTrade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradePool pool,
            int weight,
            boolean exactLevel,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            Hooks hooks,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath,
            Optional<String> suffix
    ) {
        if (pool == DwarfTradeRecipe.TradePool.MAIN) {
            throw new IllegalArgumentException("pooledTrade cannot use MAIN pool");
        }

        trade(
                p,
                level,
                pool,
                OptionalInt.of(weight),
                exactLevel,
                costA,
                costB,
                result,
                hooks,
                maxUses,
                villagerXp,
                priceMultiplier,
                idPath,
                suffix
        );
    }

    protected final void pooledTrade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradePool pool,
            int weight,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            Hooks hooks,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath,
            Optional<String> suffix
    ) {
        pooledTrade(
                p,
                level,
                pool,
                weight,
                false,
                costA,
                costB,
                result,
                hooks,
                maxUses,
                villagerXp,
                priceMultiplier,
                idPath,
                suffix
        );
    }

    protected final void pooledTrade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradePool pool,
            int weight,
            boolean exactLevel,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath,
            Optional<String> suffix
    ) {
        pooledTrade(
                p,
                level,
                pool,
                weight,
                exactLevel,
                costA,
                costB,
                result,
                Hooks.EMPTY,
                maxUses,
                villagerXp,
                priceMultiplier,
                idPath,
                suffix
        );
    }

    protected final void pooledTrade(
            AbstractRecipeProvider p,
            Level level,
            DwarfTradeRecipe.TradePool pool,
            int weight,
            DwarfTradeRecipe.TradeCost costA,
            Optional<DwarfTradeRecipe.TradeCost> costB,
            DwarfTradeRecipe.TradeResult result,
            int maxUses,
            int villagerXp,
            float priceMultiplier,
            String idPath,
            Optional<String> suffix
    ) {
        pooledTrade(
                p,
                level,
                pool,
                weight,
                false,
                costA,
                costB,
                result,
                Hooks.EMPTY,
                maxUses,
                villagerXp,
                priceMultiplier,
                idPath,
                suffix
        );
    }

    // =====================================================================
    // Bounty helpers (shared across professions)
    // =====================================================================

    protected final BountyType bountyType() {
        return BountyType.fromString(profession().getId());
    }

    private void bountyTrade(
            AbstractRecipeProvider p,
            Level level
    ) {
        mainTrade(
                p,
                level,
                cost(JolCraftItems.PARCHMENT.get(), 1),
                Optional.empty(),
                itemResult(JolCraftItems.BOUNTY.get(), 1),
                hooksWithPatch(
                        DataComponentPatch.builder()
                                .set(
                                        JolCraftDataComponents.BOUNTY_TIER.get(),
                                        level.getId()
                                )
                                .set(
                                        JolCraftDataComponents.BOUNTY_TYPE.get(),
                                        bountyType().getId()
                                )
                                .build()
                ),
                1,
                0,
                0.0F,
                buyFor(JolCraftItems.PARCHMENT.get(), JolCraftItems.BOUNTY.get())
        );
    }

    protected final void addBountyTrades(AbstractRecipeProvider p) {
        bountyTrade(p, NOVICE);
        bountyTrade(p, APPRENTICE);
        bountyTrade(p, JOURNEYMAN);
        bountyTrade(p, EXPERT);
        bountyTrade(p, MASTER);
    }

    // =====================================================================
    // Save
    // =====================================================================

    void save(AbstractRecipeProvider p, String idPath, DwarfTradeRecipe recipe) {
        ResourceLocation id = JolCraft.location(p.inFolder(fullFolder(), idPath));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        AdvancementHolder advancement = p.out().advancement()
                .addCriterion("has_gold_coin", p.hasItem(JolCraftItems.GOLD_COIN.get()))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .build(JolCraft.location("recipes/" + p.inFolder(fullFolder(), idPath)));

        p.out().accept(key, recipe, advancement);
    }

    // =====================================================================
    // Stable getId helpers (LEVEL-LESS)
    // trade() prefixes: levelId(level) + "_" automatically.
    // =====================================================================

    protected static String buy(ItemLike result) {
        return "buy_" + itemId(result);
    }

    protected static String buyFor(ItemLike input, ItemLike result) {
        return "buy_" + itemId(result) + "_for_" + itemId(input);
    }

    protected static String sell(ItemLike sold) {
        return "sell_" + itemId(sold);
    }

    protected static String tradeFor(ItemLike cost, ItemLike result) {
        return "trade_" + itemId(result) + "_for_" + itemId(cost);
    }

    protected static String map(TagKey<Structure> destinationTag) {
        return "map_" + destinationTag.location().getPath();
    }

    protected static String levelId(Level level) {
        return level.name().toLowerCase();
    }

    protected static String itemId(ItemLike item) {
        return item.asItem()
                .builtInRegistryHolder()
                .key()
                .location()
                .getPath();
    }
}