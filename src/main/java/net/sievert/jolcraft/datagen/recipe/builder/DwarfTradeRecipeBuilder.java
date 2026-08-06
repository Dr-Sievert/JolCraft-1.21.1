package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftOrderedEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.datagen.base.output.JolCraftFileNameBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeCost;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradePoolEntry;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeStats;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"UnusedReturnValue", "deprecation"})
public final class DwarfTradeRecipeBuilder implements JolCraftOrderedEmissionBuilder<RecipeOutput> {

    private final List<String> errors =
            new ArrayList<>();

    private @Nullable DwarfProfession profession;
    private @Nullable DwarfMerchantData.Level merchantLevel;

    private TradePoolEntry pool =
            TradePoolEntry.MAIN;

    private int order;

    private @Nullable TradeCost costA;
    private @Nullable TradeCost costB;

    private @Nullable ItemOutput result;

    private TradeStats stats =
            TradeStats.DEFAULT;

    private @Nullable String fileNameOverride;
    private @Nullable String fileNameSuffix;

    /*
     * Naming metadata is stored by the builder rather than inferred from the
     * serialized loot objects.
     */
    private @Nullable String costAName;
    private @Nullable String costBName;
    private @Nullable String resultName;

    private boolean costAIsCoins;
    private boolean costBIsCoins;
    private boolean resultIsCoins;

    private DwarfTradeRecipeBuilder() {}

    public static @NotNull DwarfTradeRecipeBuilder create() {
        return new DwarfTradeRecipeBuilder();
    }

    // -------------------------------------------------------------------------
    // Ordered emission
    // -------------------------------------------------------------------------

    @Override
    public int order() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order =
                Math.max(
                        0,
                        order
                );
    }

    @Override
    public @NotNull String orderKey() {
        DwarfProfession resolvedProfession =
                profession != null
                        ? profession
                        : DwarfProfession.NONE;

        if (isGlobalPool()) {
            return "dwarf_trade:"
                    + resolvedProfession.getId()
                    + ":global_pool";
        }

        DwarfMerchantData.Level resolvedLevel =
                merchantLevel != null
                        ? merchantLevel
                        : DwarfMerchantData.Level.NOVICE;

        return "dwarf_trade:"
                + resolvedProfession.getId()
                + ":"
                + pool.group()
                .serializedName()
                + ":"
                + resolvedLevel.getId();
    }

    // -------------------------------------------------------------------------
    // Profession and level
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder profession(
            @Nullable DwarfProfession profession
    ) {
        if (profession == null) {
            errors.add(
                    "profession is null"
            );

            this.profession =
                    null;

            return this;
        }

        this.profession =
                profession;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder merchantLevel(
            @Nullable DwarfMerchantData.Level merchantLevel
    ) {
        this.merchantLevel =
                merchantLevel;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder noMerchantLevel() {
        this.merchantLevel =
                null;

        return this;
    }

    // -------------------------------------------------------------------------
    // Pool
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder pool(
            @Nullable TradePoolEntry pool
    ) {
        if (pool == null) {
            errors.add(
                    "pool is null"
            );

            this.pool =
                    TradePoolEntry.MAIN;

            return this;
        }

        this.pool =
                pool;

        if (pool.group()
                == TradeGroup.GLOBAL_POOL) {
            merchantLevel =
                    null;
        }

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder tradeGroup(
            @Nullable TradeGroup group
    ) {
        if (group == null) {
            errors.add(
                    "trade group is null"
            );

            this.pool =
                    TradePoolEntry.MAIN;

            return this;
        }

        int weight =
                group == TradeGroup.MAIN
                        ? TradePoolEntry.DEFAULT_WEIGHT
                        : Math.max(
                        TradePoolEntry.DEFAULT_WEIGHT,
                        pool.weight()
                );

        this.pool =
                new TradePoolEntry(
                        group,
                        weight
                );

        if (group == TradeGroup.GLOBAL_POOL) {
            merchantLevel =
                    null;
        }

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder mainPool() {
        return tradeGroup(
                TradeGroup.MAIN
        );
    }

    public @NotNull DwarfTradeRecipeBuilder exactLevelPool() {
        return tradeGroup(
                TradeGroup.EXACT_LEVEL_POOL
        );
    }

    public @NotNull DwarfTradeRecipeBuilder cumulativePool() {
        return tradeGroup(
                TradeGroup.CUMULATIVE_POOL
        );
    }

    public @NotNull DwarfTradeRecipeBuilder globalPool() {
        return tradeGroup(
                TradeGroup.GLOBAL_POOL
        );
    }

    public @NotNull DwarfTradeRecipeBuilder weight(
            int weight
    ) {
        if (weight < 1) {
            errors.add(
                    "weight must be >= 1"
            );

            weight =
                    TradePoolEntry.DEFAULT_WEIGHT;
        }

        if (pool.group()
                == TradeGroup.MAIN
                && weight
                != TradePoolEntry.DEFAULT_WEIGHT) {
            errors.add(
                    "main trades must use default pool weight"
            );

            weight =
                    TradePoolEntry.DEFAULT_WEIGHT;
        }

        this.pool =
                new TradePoolEntry(
                        pool.group(),
                        weight
                );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder order(
            int order
    ) {
        if (order < 0) {
            errors.add(
                    "order must be >= 0"
            );

            this.order =
                    0;

            return this;
        }

        this.order =
                order;

        return this;
    }

    private boolean isGlobalPool() {
        return pool.group()
                == TradeGroup.GLOBAL_POOL;
    }

    // -------------------------------------------------------------------------
    // Cost factories
    // -------------------------------------------------------------------------

    public static @NotNull TradeCost cost(
            @NotNull Ingredient ingredient,
            int min,
            int max
    ) {
        int lower =
                normalizedMin(
                        min,
                        max
                );

        int upper =
                normalizedMax(
                        min,
                        max
                );

        return new TradeCost(
                ingredient,
                countProvider(
                        lower,
                        upper
                )
        );
    }

    public static @NotNull TradeCost cost(
            @NotNull ItemLike item,
            int min,
            int max
    ) {
        return cost(
                Ingredient.of(item),
                min,
                max
        );
    }

    public static @NotNull TradeCost cost(
            @NotNull ItemLike item,
            int count
    ) {
        return cost(
                item,
                count,
                count
        );
    }

    public static @NotNull TradeCost cost(
            @NotNull ItemLike item
    ) {
        return cost(
                item,
                1,
                1
        );
    }

    public static @NotNull TradeCost taggedCost(
            @NotNull TagKey<Item> tag,
            int min,
            int max
    ) {
        return cost(
                Ingredient.of(tag),
                min,
                max
        );
    }

    public static @NotNull TradeCost taggedCost(
            @NotNull TagKey<Item> tag,
            int count
    ) {
        return taggedCost(
                tag,
                count,
                count
        );
    }

    public static @NotNull TradeCost coinsAsCost(
            int min,
            int max
    ) {
        return taggedCost(
                DwarfTradeRecipe.COINS_TAG,
                min,
                max
        );
    }

    public static @NotNull TradeCost coinsAsCost(
            int count
    ) {
        return coinsAsCost(
                count,
                count
        );
    }

    private static @NotNull NumberProvider countProvider(
            int min,
            int max
    ) {
        if (min == max) {
            return ConstantValue.exactly(
                    min
            );
        }

        return UniformGenerator.between(
                min,
                max
        );
    }

    private static int normalizedMin(
            int first,
            int second
    ) {
        return Math.max(
                1,
                Math.min(
                        first,
                        second
                )
        );
    }

    private static int normalizedMax(
            int first,
            int second
    ) {
        int lower =
                normalizedMin(
                        first,
                        second
                );

        return Math.max(
                lower,
                Math.max(
                        first,
                        second
                )
        );
    }

    // -------------------------------------------------------------------------
    // Cost A
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder costA(
            @Nullable TradeCost cost
    ) {
        if (cost == null) {
            errors.add(
                    "cost_a is null"
            );

            this.costA =
                    null;

            this.costAName =
                    null;

            this.costAIsCoins =
                    false;

            return this;
        }

        this.costA =
                cost;

        if (cost.contains(
                DwarfTradeRecipe.COINS_TAG
        )) {
            this.costAName =
                    coinToken();

            this.costAIsCoins =
                    true;
        }

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costA(
            @Nullable ItemLike item,
            int min,
            int max
    ) {
        if (item == null) {
            errors.add(
                    "cost_a item is null"
            );

            this.costA =
                    null;

            this.costAName =
                    null;

            this.costAIsCoins =
                    false;

            return this;
        }

        this.costA =
                cost(
                        item,
                        min,
                        max
                );

        this.costAName =
                itemToken(
                        item
                );

        this.costAIsCoins =
                item.asItem()
                        .builtInRegistryHolder()
                        .is(
                                DwarfTradeRecipe.COINS_TAG
                        );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costA(
            @Nullable ItemLike item,
            int count
    ) {
        return costA(
                item,
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder costA(
            @Nullable ItemLike item
    ) {
        return costA(
                item,
                1,
                1
        );
    }

    public @NotNull DwarfTradeRecipeBuilder costATag(
            @Nullable TagKey<Item> tag,
            int min,
            int max
    ) {
        if (tag == null) {
            errors.add(
                    "cost_a tag is null"
            );

            this.costA =
                    null;

            this.costAName =
                    null;

            this.costAIsCoins =
                    false;

            return this;
        }

        this.costA =
                taggedCost(
                        tag,
                        min,
                        max
                );

        this.costAName =
                tag.equals(
                        DwarfTradeRecipe.COINS_TAG
                )
                        ? coinToken()
                        : tag.location()
                        .getPath();

        this.costAIsCoins =
                tag.equals(
                        DwarfTradeRecipe.COINS_TAG
                );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costATag(
            @Nullable TagKey<Item> tag,
            int count
    ) {
        return costATag(
                tag,
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder costACoins(
            int min,
            int max
    ) {
        this.costA =
                coinsAsCost(
                        min,
                        max
                );

        this.costAName =
                coinToken();

        this.costAIsCoins =
                true;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costACoins(
            int count
    ) {
        return costACoins(
                count,
                count
        );
    }

    // -------------------------------------------------------------------------
    // Cost B
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder costB(
            @Nullable TradeCost cost
    ) {
        this.costB =
                cost;

        this.costBName =
                null;

        this.costBIsCoins =
                false;

        if (cost != null
                && cost.contains(
                DwarfTradeRecipe.COINS_TAG
        )) {
            this.costBName =
                    coinToken();

            this.costBIsCoins =
                    true;
        }

        return this;
    }

    @SuppressWarnings("deprecation")
    public @NotNull DwarfTradeRecipeBuilder costB(
            @Nullable ItemLike item,
            int min,
            int max
    ) {
        if (item == null) {
            errors.add(
                    "cost_b item is null"
            );

            return noCostB();
        }

        this.costB =
                cost(
                        item,
                        min,
                        max
                );

        this.costBName =
                itemToken(
                        item
                );

        this.costBIsCoins =
                item.asItem()
                        .builtInRegistryHolder()
                        .is(
                                DwarfTradeRecipe.COINS_TAG
                        );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costB(
            @Nullable ItemLike item,
            int count
    ) {
        return costB(
                item,
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder costB(
            @Nullable ItemLike item
    ) {
        return costB(
                item,
                1,
                1
        );
    }

    public @NotNull DwarfTradeRecipeBuilder costBTag(
            @Nullable TagKey<Item> tag,
            int min,
            int max
    ) {
        if (tag == null) {
            errors.add(
                    "cost_b tag is null"
            );

            return noCostB();
        }

        this.costB =
                taggedCost(
                        tag,
                        min,
                        max
                );

        this.costBName =
                tag.equals(
                        DwarfTradeRecipe.COINS_TAG
                )
                        ? coinToken()
                        : tag.location()
                        .getPath();

        this.costBIsCoins =
                tag.equals(
                        DwarfTradeRecipe.COINS_TAG
                );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costBTag(
            @Nullable TagKey<Item> tag,
            int count
    ) {
        return costBTag(
                tag,
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder costBCoins(
            int min,
            int max
    ) {
        this.costB =
                coinsAsCost(
                        min,
                        max
                );

        this.costBName =
                coinToken();

        this.costBIsCoins =
                true;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costBCoins(
            int count
    ) {
        return costBCoins(
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder noCostB() {
        this.costB =
                null;

        this.costBName =
                null;

        this.costBIsCoins =
                false;

        return this;
    }

    // -------------------------------------------------------------------------
    // Result
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder result(
            @Nullable ItemOutput result
    ) {
        if (result == null) {
            errors.add(
                    "result is null"
            );

            this.result =
                    null;

            this.resultName =
                    null;

            this.resultIsCoins =
                    false;

            return this;
        }

        this.result =
                result;

        return this;
    }

    /**
     * Sets a custom output while retaining automatic filename generation.
     */
    public @NotNull DwarfTradeRecipeBuilder result(
            @Nullable ItemOutput result,
            @Nullable String nameToken
    ) {
        result(
                result
        );

        if (nameToken == null
                || nameToken.isBlank()) {
            errors.add(
                    "custom result requires a filename token"
            );

            this.resultName =
                    null;
        } else {
            this.resultName =
                    nameToken;
        }

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder result(
            @Nullable ItemLike item,
            int min,
            int max
    ) {
        if (item == null) {
            errors.add(
                    "result item is null"
            );

            this.result =
                    null;

            this.resultName =
                    null;

            this.resultIsCoins =
                    false;

            return this;
        }

        int lower =
                normalizedMin(
                        min,
                        max
                );

        int upper =
                normalizedMax(
                        min,
                        max
                );

        LootPool pool =
                LootPool.lootPool()
                        .add(
                                LootItem.lootTableItem(
                                                item.asItem()
                                        )
                                        .apply(
                                                SetItemCountFunction.setCount(
                                                        countProvider(
                                                                lower,
                                                                upper
                                                        )
                                                )
                                        )
                        )
                        .build();

        this.result =
                ItemOutput.of(
                        LootPool.lootPool()
                                .add(
                                        LootItem.lootTableItem(
                                                        item.asItem()
                                                )
                                                .apply(
                                                        SetItemCountFunction.setCount(
                                                                countProvider(
                                                                        lower,
                                                                        upper
                                                                )
                                                        )
                                                )
                                )
                );

        this.resultName =
                itemToken(
                        item
                );

        this.resultIsCoins =
                item.asItem()
                        .builtInRegistryHolder()
                        .is(
                                DwarfTradeRecipe.COINS_TAG
                        );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder result(
            @Nullable ItemLike item,
            int count
    ) {
        return result(
                item,
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder result(
            @Nullable ItemLike item
    ) {
        return result(
                item,
                1,
                1
        );
    }

    public static @NotNull ItemOutput coinsAsResult(
            int min,
            int max
    ) {
        int lower =
                normalizedMin(
                        min,
                        max
                );

        int upper =
                normalizedMax(
                        min,
                        max
                );

        return ItemOutput.of(
                LootPool.lootPool()
                        .add(
                                LootItem.lootTableItem(
                                                DwarfTradeRecipe.GOLD_COIN
                                        )
                                        .apply(
                                                SetItemCountFunction.setCount(
                                                        countProvider(
                                                                lower,
                                                                upper
                                                        )
                                                )
                                        )
                        )
        );
    }

    public static @NotNull ItemOutput coinsAsResult(
            int count
    ) {
        return coinsAsResult(
                count,
                count
        );
    }

    public @NotNull DwarfTradeRecipeBuilder coinsResult(
            int min,
            int max
    ) {
        this.result =
                coinsAsResult(
                        min,
                        max
                );

        this.resultName =
                coinToken();

        this.resultIsCoins =
                true;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder coinsResult(
            int count
    ) {
        return coinsResult(
                count,
                count
        );
    }

    /**
     * Produces a predefined reward crate.
     */
    public @NotNull DwarfTradeRecipeBuilder rewardCrateResult(
            @NotNull RewardCrateType crate
    ) {
        return rewardCrateResult(
                crate.rarity(),
                crate.lootTable(),
                crate.displayName()
        );
    }

    private @NotNull DwarfTradeRecipeBuilder rewardCrateResult(
            @NotNull Rarity rarity,
            @NotNull ResourceKey<LootTable> lootTable,
            @Nullable Component displayName
    ) {
        LootItem.Builder<?> rewardCrate =
                LootItem.lootTableItem(JolCraftItems.REWARD_CRATE.get())
                        .apply(
                                SetComponentsFunction.setComponent(
                                        DataComponents.RARITY,
                                        rarity
                                )
                        )
                        .apply(
                                SetComponentsFunction.setComponent(
                                        JolCraftDataComponents.REWARD_CRATE_SOURCE.get(),
                                        RewardCrateSource.lootTable(lootTable)
                                )
                        );

        if (displayName != null) {
            rewardCrate.apply(
                    SetComponentsFunction.setComponent(
                            DataComponents.CUSTOM_NAME,
                            displayName
                    )
            );
        }

        return result(
                ItemOutput.item(rewardCrate),
                lootTable.location().getNamespace()
                        + "_"
                        + lootTable.location().getPath().replace('/', '_')
                        + "_"
                        + JolCraftDictionary.REWARD
                        + "_"
                        + JolCraftDictionary.CRATE
        );
    }

    // -------------------------------------------------------------------------
    // Stats
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder stats(
            @Nullable TradeStats stats
    ) {
        if (stats == null) {
            errors.add(
                    "stats is null"
            );

            this.stats =
                    TradeStats.DEFAULT;

            return this;
        }

        this.stats =
                stats;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder maxUses(
            int maxUses
    ) {
        this.stats =
                new TradeStats(
                        maxUses,
                        stats.dwarfXp(),
                        stats.priceMultiplier()
                );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder dwarfXp(
            int dwarfXp
    ) {
        this.stats =
                new TradeStats(
                        stats.maxUses(),
                        dwarfXp,
                        stats.priceMultiplier()
                );

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder priceMultiplier(
            float priceMultiplier
    ) {
        this.stats =
                new TradeStats(
                        stats.maxUses(),
                        stats.dwarfXp(),
                        priceMultiplier
                );

        return this;
    }

    // -------------------------------------------------------------------------
    // Filename
    // -------------------------------------------------------------------------

    public @NotNull DwarfTradeRecipeBuilder fileNameOverride(
            @Nullable String fileName
    ) {
        if (fileName == null
                || fileName.isBlank()) {
            this.fileNameOverride =
                    null;

            return this;
        }

        this.fileNameOverride =
                fileName;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder resultName(
            @Nullable String token
    ) {
        if (token == null
                || token.isBlank()) {
            errors.add(
                    "result filename token is empty"
            );

            this.resultName =
                    null;

            return this;
        }

        this.resultName =
                token;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder fileNameSuffix(
            @Nullable String suffix
    ) {
        this.fileNameSuffix =
                suffix == null || suffix.isBlank()
                        ? null
                        : suffix;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costAName(
            @Nullable String token
    ) {
        if (token == null
                || token.isBlank()) {
            errors.add(
                    "cost_a filename token is empty"
            );

            this.costAName =
                    null;

            return this;
        }

        this.costAName =
                token;

        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costBName(
            @Nullable String token
    ) {
        if (token == null
                || token.isBlank()) {
            errors.add(
                    "cost_b filename token is empty"
            );

            this.costBName =
                    null;

            return this;
        }

        this.costBName =
                token;

        return this;
    }

    // -------------------------------------------------------------------------
    // Build
    // -------------------------------------------------------------------------

    @Override
    public @NotNull DataResult<JolCraftDataEmission<RecipeOutput>>
    buildValidated() {
        validateBuilderState();

        DataResult<String> nameResult =
                fileNameOverride != null
                        ? JolCraftFileNameBuilder
                        .validateFileName(
                                fileNameOverride
                        )
                        : buildAutomaticName();

        if (profession == null
                || costA == null
                || result == null) {
            return DataResult.error(() ->
                    "dwarf trade builder: "
                            + String.join(
                            "; ",
                            errors
                    )
            );
        }

        DwarfTradeRecipe recipe =
                new DwarfTradeRecipe(
                        profession,
                        merchantLevel,
                        pool,
                        order,
                        costA,
                        costB,
                        result,
                        stats
                );

        DataResult<DwarfTradeRecipe> recipeResult =
                DwarfTradeRecipe.Serializer.validate(
                        recipe
                );

        if (!errors.isEmpty()
                && recipeResult.error().isEmpty()) {
            recipeResult =
                    DataResult.error(
                            () -> "dwarf trade builder: "
                                    + String.join(
                                    "; ",
                                    errors
                            ),
                            recipe
                    );
        }

        DataResult<DwarfTradeRecipe> finalRecipeResult =
                recipeResult;

        return nameResult.flatMap(name ->
                finalRecipeResult.map(validRecipe ->
                        new JolCraftDataEmission<>(
                                name,
                                (
                                        output,
                                        path
                                ) -> output.accept(
                                        JolCraft.location(
                                                path
                                        ),
                                        validRecipe,
                                        null
                                )
                        )
                )
        );
    }

    private void validateBuilderState() {
        if (profession == null) {
            errors.add(
                    "profession is required"
            );
        }

        if (isGlobalPool()) {
            if (merchantLevel != null) {
                errors.add(
                        "global_pool trades must not define merchantLevel"
                );
            }
        } else if (merchantLevel == null) {
            errors.add(
                    "merchantLevel is required for non-global trades"
            );
        }

        if (costA == null) {
            errors.add(
                    "cost_a is required"
            );
        }

        if (result == null) {
            errors.add(
                    "result is required"
            );
        }

        if (order < 0) {
            errors.add(
                    "order must be >= 0"
            );
        }

        if (stats.maxUses() < 1) {
            errors.add(
                    "maxUses must be >= 1"
            );
        }

        if (stats.dwarfXp() < 0) {
            errors.add(
                    "dwarfXp must be >= 0"
            );
        }

        if (stats.priceMultiplier() < 0.0F) {
            errors.add(
                    "priceMultiplier must be >= 0"
            );
        }
    }

    private @NotNull DataResult<String> buildAutomaticName() {
        String level =
                levelName();

        String firstCost =
                requiredToken(
                        costAName,
                        "cost_a"
                );

        String secondCost =
                costB != null
                        ? requiredToken(
                        costBName,
                        "cost_b"
                )
                        : null;

        String output =
                requiredToken(
                        resultName,
                        "result"
                );

        JolCraftFileNameBuilder name =
                JolCraftFileNameBuilder.create()
                        .token(
                                level
                        );

        switch (determineTradeKind()) {
            case BUY -> {
                name.token(
                                JolCraftDictionary.BUY
                        )
                        .token(
                                output
                        )
                        .token(
                                JolCraftDictionary.FOR
                        )
                        .token(
                                firstCost
                        );

                if (secondCost != null) {
                    name.token(
                                    JolCraftDictionary.AND
                            )
                            .token(
                                    secondCost
                            );
                }
            }

            case SELL -> {
                name.token(
                                JolCraftDictionary.SELL
                        )
                        .token(
                                firstCost
                        );

                if (secondCost != null) {
                    name.token(
                                    JolCraftDictionary.AND
                            )
                            .token(
                                    secondCost
                            );
                }
            }

            case TRADE -> {
                name.token(
                        firstCost
                );

                if (secondCost != null) {
                    name.token(
                                    JolCraftDictionary.AND
                            )
                            .token(
                                    secondCost
                            );
                }

                name.token(
                                JolCraftDictionary.FOR
                        )
                        .token(
                                output
                        );
            }
        }

        DataResult<String> built = name.build();

        if (fileNameSuffix != null) {
            built = built.map(fileName ->
                    fileName + "_" + fileNameSuffix
            );
        }

        if (errors.isEmpty()) {
            return built;
        }

        String partial =
                built.result()
                        .orElse("");

        String message =
                "recipe name: "
                        + String.join(
                        "; ",
                        errors
                );

        if (built.error().isPresent()) {
            message += "; "
                    + built.error()
                    .get()
                    .message();
        }

        String finalMessage =
                message;

        return DataResult.error(
                () -> finalMessage,
                partial
        );
    }

    private enum TradeKind {
        BUY,
        SELL,
        TRADE
    }

    private @NotNull TradeKind determineTradeKind() {
        if (costAIsCoins
                || costBIsCoins) {
            return TradeKind.BUY;
        }

        if (resultIsCoins) {
            return TradeKind.SELL;
        }

        return TradeKind.TRADE;
    }

    private @NotNull String levelName() {
        if (isGlobalPool()) {
            return JolCraftDictionary.GLOBAL;
        }

        if (merchantLevel == null) {
            errors.add(
                    "merchantLevel missing for filename"
            );

            return JolCraftDictionary.UNKNOWN;
        }

        return merchantLevel.name()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private @NotNull String requiredToken(
            @Nullable String token,
            @NotNull String source
    ) {
        if (token == null
                || token.isBlank()) {
            errors.add(
                    source
                            + " has no filename token"
            );

            return JolCraftDictionary.UNKNOWN;
        }

        return token;
    }

    private static @NotNull String itemToken(
            @NotNull ItemLike item
    ) {
        ResourceLocation id =
                BuiltInRegistries.ITEM.getKey(
                        item.asItem()
                );

        return id.getPath();
    }

    private static @NotNull String coinToken() {
        return JolCraftDictionary.COIN + "s";
    }

    public static @NotNull DwarfTradeRecipeBuilder buyLegendaryLoreTome(
            @Nullable DwarfProfession profession,
            @Nullable DwarfMerchantData.Level level,
            @Nullable DwarfLoreKey loreKey,
            int legendaryPagesMin,
            int legendaryPagesMax,
            int coinsMin,
            int coinsMax
    ) {
        DwarfMerchantData.Level resolvedLevel =
                level != null
                        ? level
                        : DwarfMerchantData.Level.NOVICE;

        DwarfProfession resolvedProfession =
                profession != null
                        ? profession
                        : DwarfProfession.NONE;

        if (loreKey == null) {
            return DwarfTradeRecipeBuilder.create()
                    .profession(resolvedProfession)
                    .merchantLevel(resolvedLevel)
                    .costA(
                            JolCraftItems.LEGENDARY_PAGE.get(),
                            legendaryPagesMin,
                            legendaryPagesMax
                    )
                    .costBCoins(
                            coinsMin,
                            coinsMax
                    )
                    .resultName(
                            JolCraftDictionary.TOME
                    );
        }

        String loreName =
                LoreHelper.toLoreKeyString(
                        loreKey
                );

        ItemOutput tomeOutput =
                ItemOutput.of(
                        LootPool.lootPool()
                                .add(
                                        LootItem.lootTableItem(
                                                        JolCraftItems
                                                                .ANCIENT_DWARVEN_TOME_LEGENDARY
                                                                .get()
                                                )
                                                .apply(
                                                        SetComponentsFunction
                                                                .setComponent(
                                                                        JolCraftDataComponents
                                                                                .DWARF_LORE_KEY
                                                                                .get(),
                                                                        loreName
                                                                )
                                                )
                                )
                );

        return DwarfTradeRecipeBuilder.create()
                .profession(resolvedProfession)
                .merchantLevel(resolvedLevel)
                .mainPool()
                .costA(
                        JolCraftItems.LEGENDARY_PAGE.get(),
                        legendaryPagesMin,
                        legendaryPagesMax
                )
                .costBCoins(
                        coinsMin,
                        coinsMax
                )
                .result(
                        tomeOutput,
                        loreName
                                + "_"
                                + JolCraftDictionary.TOME
                )
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.0F)
                .fileNameOverride(
                        resolvedLevel.name()
                                .toLowerCase(Locale.ROOT)
                                + "_"
                                + JolCraftDictionary.BUY
                                + "_"
                                + loreName
                                + "_"
                                + JolCraftDictionary.TOME
                );
    }

    public static @NotNull List<DwarfTradeRecipeBuilder> bountyTrades(
            @Nullable DwarfProfession profession
    ) {
        DwarfProfession resolvedProfession =
                profession != null
                        ? profession
                        : DwarfProfession.NONE;

        if (resolvedProfession == DwarfProfession.NONE) {
            return List.of();
        }

        String bountyType =
                resolvedProfession.professionName();

        if (bountyType == null
                || bountyType.isBlank()
                || bountyType.equals(
                JolCraftDictionary.NONE
        )) {
            return List.of();
        }

        List<DwarfTradeRecipeBuilder> builders =
                new ArrayList<>();

        for (DwarfMerchantData.Level level
                : DwarfMerchantData.Level.values()) {

            ItemOutput bountyOutput =
                    ItemOutput.of(
                            LootPool.lootPool()
                                    .add(
                                            LootItem.lootTableItem(
                                                            JolCraftItems.BOUNTY.get()
                                                    )
                                                    .apply(
                                                            SetComponentsFunction
                                                                    .setComponent(
                                                                            JolCraftDataComponents
                                                                                    .BOUNTY_TYPE
                                                                                    .get(),
                                                                            bountyType
                                                                    )
                                                    )
                                                    .apply(
                                                            SetComponentsFunction
                                                                    .setComponent(
                                                                            JolCraftDataComponents
                                                                                    .BOUNTY_TIER
                                                                                    .get(),
                                                                            level.getId()
                                                                    )
                                                    )
                                    )
                    );

            String fileName =
                    level.name()
                            .toLowerCase(Locale.ROOT)
                            + "_"
                            + bountyType
                            + "_"
                            + JolCraftDictionary.BOUNTY;

            builders.add(
                    DwarfTradeRecipeBuilder.create()
                            .profession(
                                    resolvedProfession
                            )
                            .merchantLevel(
                                    level
                            )
                            .pool(
                                    TradePoolEntry.MAIN
                            )
                            .costA(
                                    JolCraftItems.PARCHMENT.get(),
                                    1
                            )
                            .noCostB()
                            .result(
                                    bountyOutput,
                                    bountyType
                                            + "_"
                                            + JolCraftDictionary.BOUNTY
                            )
                            .maxUses(1)
                            .dwarfXp(0)
                            .priceMultiplier(0.0F)
                            .fileNameOverride(
                                    fileName
                            )
            );
        }

        return List.copyOf(
                builders
        );
    }
}