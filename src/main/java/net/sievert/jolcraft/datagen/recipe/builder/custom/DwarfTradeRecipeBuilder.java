package net.sievert.jolcraft.datagen.recipe.builder.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.lore.util.LoreHelper;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradePoolEntry;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeStats;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.OrderedBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeFileNameBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector.ItemIngredientBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ComponentTransformBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ItemTransformsBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("UnusedReturnValue")
public final class DwarfTradeRecipeBuilder implements OrderedBuilder {

    private final List<String> errors = new ArrayList<>();

    private @Nullable DwarfProfession profession;
    private @Nullable DwarfMerchantData.Level merchantLevel;
    private TradePoolEntry pool = TradePoolEntry.MAIN;
    private int order = 0;

    private @Nullable ItemInput costA;
    private @Nullable ItemInput costB;
    private @Nullable ItemOutput result;

    private TradeStats stats = TradeStats.DEFAULT;

    @Nullable
    private String fileNameOverride;

    private DwarfTradeRecipeBuilder() {}

    public static @NotNull DwarfTradeRecipeBuilder create() {
        return new DwarfTradeRecipeBuilder();
    }

    private static @NotNull ItemTransforms noTransforms() {
        return new ItemTransforms(List.of(), List.of());
    }

    private boolean isGlobalPool() {
        return pool.group() == TradeGroup.GLOBAL_POOL;
    }

    @Override
    public int order() {
        return this.order;
    }

    @Override
    public void setOrder(int order) {
        this.order = Math.max(0, order);
    }

    @Override
    public @NotNull String orderKey() {
        DwarfProfession prof = profession != null ? profession : DwarfProfession.NONE;
        if (isGlobalPool()) {
            return "dwarf_trade:" + prof.getId() + ":global_pool";
        }

        DwarfMerchantData.Level lvl = merchantLevel != null ? merchantLevel : DwarfMerchantData.Level.NOVICE;
        return "dwarf_trade:" + prof.getId() + ":" + pool.group().name().toLowerCase(Locale.ROOT) + ":" + lvl.getId();
    }

    public @NotNull DwarfTradeRecipeBuilder profession(@Nullable DwarfProfession p) {
        if (p == null) {
            errors.add("profession is null");
            this.profession = null;
            return this;
        }
        this.profession = p;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder merchantLevel(@Nullable DwarfMerchantData.Level lvl) {
        this.merchantLevel = lvl;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder noMerchantLevel() {
        this.merchantLevel = null;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder pool(@Nullable TradePoolEntry pool) {
        if (pool == null) {
            errors.add("pool is null");
            this.pool = TradePoolEntry.MAIN;
            return this;
        }
        this.pool = pool;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder tradeGroup(@Nullable TradeGroup group) {
        if (group == null) {
            errors.add("trade_group is null");
            this.pool = new TradePoolEntry(TradeGroup.MAIN, pool.weight());
            return this;
        }

        this.pool = new TradePoolEntry(group, pool.weight());
        if (group == TradeGroup.GLOBAL_POOL) {
            this.merchantLevel = null;
        }
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder weight(@Nullable WeightParam weight) {
        if (weight == null) {
            errors.add("weight is null");
            this.pool = new TradePoolEntry(pool.group(), WeightParam.ONE);
            return this;
        }

        this.pool = new TradePoolEntry(pool.group(), weight);
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder weight(int value) {
        if (value < 0) {
            errors.add("weight must be >= 0");
            this.pool = new TradePoolEntry(pool.group(), WeightParam.ONE);
            return this;
        }

        this.pool = new TradePoolEntry(pool.group(), new WeightParam(value));
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder order(int order) {
        if (order < 0) {
            errors.add("order must be >= 0");
            this.order = 0;
            return this;
        }
        this.order = order;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costA(@Nullable ItemInput input) {
        if (input == null) {
            errors.add("cost_a is null");
            this.costA = null;
            return this;
        }
        this.costA = input;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costA(@Nullable ItemLike item, int min, int max) {
        if (item == null) {
            errors.add("cost_a item is null");
            this.costA = null;
            return this;
        }

        int lo = Math.max(1, Math.min(min, max));
        int hi = Math.max(lo, Math.max(min, max));

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count((lo == hi) ? IntRange.fixed(lo) : new IntRange(lo, hi))
                .build();

        return costA(built);
    }

    public @NotNull DwarfTradeRecipeBuilder costA(@Nullable ItemLike item, int count) {
        if (item == null) {
            errors.add("cost_a item is null");
            this.costA = null;
            return this;
        }

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.fixed(Math.max(1, count)))
                .build();

        return costA(built);
    }

    public @NotNull DwarfTradeRecipeBuilder costA(@Nullable ItemLike item) {
        if (item == null) {
            errors.add("cost_a item is null");
            this.costA = null;
            return this;
        }

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.ONE)
                .build();

        return costA(built);
    }

    public @NotNull DwarfTradeRecipeBuilder costB(@Nullable ItemInput input) {
        this.costB = input;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder costB(@Nullable ItemLike item, int min, int max) {
        if (item == null) {
            errors.add("cost_b item is null");
            this.costB = null;
            return this;
        }

        int lo = Math.max(1, Math.min(min, max));
        int hi = Math.max(lo, Math.max(min, max));

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count((lo == hi) ? IntRange.fixed(lo) : new IntRange(lo, hi))
                .build();

        return costB(built);
    }

    public @NotNull DwarfTradeRecipeBuilder costB(@Nullable ItemLike item, int count) {
        if (item == null) {
            errors.add("cost_b item is null");
            this.costB = null;
            return this;
        }

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.fixed(Math.max(1, count)))
                .build();

        return costB(built);
    }

    public @NotNull DwarfTradeRecipeBuilder costB(@Nullable ItemLike item) {
        if (item == null) {
            errors.add("cost_b item is null");
            this.costB = null;
            return this;
        }

        ItemInput built = ItemInputBuilder.create()
                .item(item)
                .count(IntRange.ONE)
                .build();

        return costB(built);
    }

    public @NotNull DwarfTradeRecipeBuilder noCostB() {
        this.costB = null;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder result(@Nullable ItemOutput out) {
        if (out == null) {
            errors.add("result is null");
            this.result = null;
            return this;
        }
        this.result = out;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder result(@Nullable ItemLike item, int min, int max) {
        if (item == null) {
            errors.add("result item is null");
            this.result = null;
            return this;
        }

        ItemOutput built = ItemOutputBuilder.create()
                .result(item.asItem(), min, max)
                .transforms(noTransforms())
                .build();

        return result(built);
    }

    public @NotNull DwarfTradeRecipeBuilder result(@Nullable ItemLike item, int fixedCount) {
        return result(item, fixedCount, fixedCount);
    }

    public @NotNull DwarfTradeRecipeBuilder result(@Nullable ItemLike item) {
        return result(item, 1, 1);
    }

    public static @NotNull ItemInput coinsAsCost(int min, int max) {
        int lo = Math.max(1, Math.min(min, max));
        int hi = Math.max(lo, Math.max(min, max));

        return ItemInputBuilder.create()
                .selector(ItemIngredientBuilder.create().tag(DwarfTradeRecipe.COINS_TAG))
                .count((lo == hi) ? IntRange.fixed(lo) : new IntRange(lo, hi))
                .build();
    }

    public @NotNull DwarfTradeRecipeBuilder costACoins(int min, int max) {
        return costA(coinsAsCost(min, max));
    }

    public @NotNull DwarfTradeRecipeBuilder costACoins(int fixed) {
        return costACoins(fixed, fixed);
    }

    public @NotNull DwarfTradeRecipeBuilder costBCoins(int min, int max) {
        return costB(coinsAsCost(min, max));
    }

    public @NotNull DwarfTradeRecipeBuilder costBCoins(int fixed) {
        return costBCoins(fixed, fixed);
    }

    public static @NotNull ItemOutput coinsAsResult(int min, int max) {
        int lo = Math.max(1, Math.min(min, max));
        int hi = Math.max(lo, Math.max(min, max));

        return ItemOutputBuilder.create()
                .result(DwarfTradeRecipe.GOLD_COIN.asItem(), lo, hi)
                .transforms(noTransforms())
                .build();
    }

    public @NotNull DwarfTradeRecipeBuilder coinsResult(int min, int max) {
        return result(coinsAsResult(min, max));
    }

    public @NotNull DwarfTradeRecipeBuilder coinsResult(int fixed) {
        return coinsResult(fixed, fixed);
    }

    public static @NotNull DwarfTradeRecipeBuilder buyLegendaryLoreTome(
            @Nullable DwarfMerchantData.Level level,
            @Nullable DwarfProfession profession,
            @Nullable DwarfLoreKey loreKey,
            @Nullable IntRange legendaryPagesCost,
            @Nullable IntRange coinsCost
    ) {
        DwarfMerchantData.Level lvl = level != null ? level : DwarfMerchantData.Level.NOVICE;
        DwarfProfession prof = profession != null ? profession : DwarfProfession.NONE;

        IntRange pagesRange = legendaryPagesCost != null ? legendaryPagesCost : IntRange.ONE;
        IntRange coinsRange = coinsCost != null ? coinsCost : IntRange.ONE;

        ItemInput pagesIn = ItemInputBuilder.create()
                .item(JolCraftItems.LEGENDARY_PAGE)
                .count(pagesRange)
                .build();

        ItemInput coinsIn = ItemInputBuilder.create()
                .selector(ItemIngredientBuilder.create().tag(DwarfTradeRecipe.COINS_TAG))
                .count(coinsRange)
                .build();

        ItemOutput tomeOut = ItemOutputBuilder.create()
                .result(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.asItem())
                .transforms(
                        ItemTransformsBuilder.create()
                                .component(
                                        ComponentTransformBuilder.create()
                                                .set(
                                                        JolCraftDataComponents.DWARF_LORE_KEY.get(),
                                                        LoreHelper.toLoreKeyString(loreKey)
                                                )
                                )
                                .build()
                )
                .build();

        String override = RecipeFileNameBuilder.create()
                .word(lvl.name().toLowerCase(Locale.ROOT))
                .word(JolCraftDictionary.BUY)
                .word(LoreHelper.toLoreKeyString(loreKey))
                .word(JolCraftDictionary.TOME)
                .build()
                .result()
                .orElse(null);

        return DwarfTradeRecipeBuilder.create()
                .profession(prof)
                .merchantLevel(lvl)
                .pool(TradePoolEntry.MAIN)
                .costA(pagesIn)
                .costB(coinsIn)
                .result(tomeOut)
                .stats(new TradeStats(1, 1, 0.0F))
                .fileNameOverride(override);
    }

    public static void addBountyTrades(
            @NotNull RecipeEmissionExecutor executor,
            @Nullable DwarfProfession profession
    ) {
        DwarfProfession prof = profession != null ? profession : DwarfProfession.NONE;
        if (prof == DwarfProfession.NONE) {
            return;
        }

        String bountyType = prof.professionName();
        if (bountyType == null || bountyType.isBlank() || bountyType.equals(JolCraftDictionary.NONE)) {
            return;
        }

        for (DwarfMerchantData.Level level : DwarfMerchantData.Level.values()) {
            ItemOutput bountyOut = ItemOutputBuilder.create()
                    .result(JolCraftItems.BOUNTY.asItem())
                    .transforms(
                            ItemTransformsBuilder.create()
                                    .component(
                                            ComponentTransformBuilder.create()
                                                    .set(JolCraftDataComponents.BOUNTY_TYPE.get(), bountyType)
                                                    .set(JolCraftDataComponents.BOUNTY_TIER.get(), level.getId())
                                    )
                                    .build()
                    )
                    .build();

            String override = RecipeFileNameBuilder.create()
                    .word(level.name().toLowerCase(Locale.ROOT))
                    .word(Objects.requireNonNull(profession).professionName())
                    .word(JolCraftDictionary.BOUNTY)
                    .build()
                    .result()
                    .orElse(null);

            executor.emitOrdered(
                    DwarfTradeRecipeBuilder.create()
                            .profession(prof)
                            .merchantLevel(level)
                            .pool(TradePoolEntry.MAIN)
                            .costA(JolCraftItems.PARCHMENT.get())
                            .noCostB()
                            .result(bountyOut)
                            .stats(new TradeStats(1, 0, 0.0F))
                            .fileNameOverride(override)
            );
        }
    }

    public @NotNull DwarfTradeRecipeBuilder stats(@Nullable TradeStats stats) {
        if (stats == null) {
            errors.add("stats is null");
            this.stats = TradeStats.DEFAULT;
            return this;
        }
        this.stats = stats;
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder maxUses(int maxUses) {
        TradeStats s = stats;
        this.stats = new TradeStats(maxUses, s.dwarfXp(), s.priceMultiplier());
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder dwarfXp(int dwarfXp) {
        TradeStats s = stats;
        this.stats = new TradeStats(s.maxUses(), dwarfXp, s.priceMultiplier());
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder priceMultiplier(float priceMultiplier) {
        TradeStats s = stats;
        this.stats = new TradeStats(s.maxUses(), s.dwarfXp(), priceMultiplier);
        return this;
    }

    public @NotNull DwarfTradeRecipeBuilder fileNameOverride(@Nullable String fileName) {
        if (fileName == null || fileName.isBlank()) {
            this.fileNameOverride = null;
            return this;
        }
        this.fileNameOverride = fileName;
        return this;
    }

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {
        if (profession == null) {
            errors.add("profession is required");
        }

        if (!isGlobalPool() && merchantLevel == null) {
            errors.add("merchantLevel is required for non-global trades");
        }

        if (isGlobalPool() && merchantLevel != null) {
            errors.add("global_pool trades must not define merchantLevel");
        }

        if (costA == null) {
            errors.add("cost_a is required");
        }

        if (result == null) {
            errors.add("result is required");
        }

        TradeKind kind = determineKindFailClosed();

        String lvl = levelNameSafe();

        String a = tokenFromCostFailClosed(
                costA,
                JolCraftStrings.underscored(JolCraftDictionary.COST, "a")
        );

        String b = (costB != null)
                ? tokenFromCostFailClosed(
                costB,
                JolCraftStrings.underscored(JolCraftDictionary.COST, "b")
        )
                : null;

        RecipeFileNameBuilder nb = RecipeFileNameBuilder.create()
                .word(lvl);

        if (kind == TradeKind.BUY) {
            String res = tokenFromResultFailClosed(result);

            nb.word(JolCraftDictionary.BUY)
                    .word(res)
                    .word(JolCraftDictionary.FOR)
                    .word(a);

            if (b != null) {
                nb.word(JolCraftDictionary.AND).word(b);
            }
        } else if (kind == TradeKind.SELL) {
            nb.word(JolCraftDictionary.SELL)
                    .word(a);

            if (b != null) {
                nb.word(JolCraftDictionary.AND).word(b);
            }
        } else {
            String res = tokenFromResultFailClosed(result);

            nb.word(a);
            if (b != null) {
                nb.word(JolCraftDictionary.AND).word(b);
            }
            nb.word(JolCraftDictionary.FOR).word(res);
        }

        DataResult<String> nameBuilt = fileNameOverride != null
                ? DataResult.success(fileNameOverride)
                : nb.build();

        if (!errors.isEmpty()) {
            String partial = nameBuilt.result().orElse("");
            String msg = "recipeName: " + String.join("; ", errors) +
                    (nameBuilt.error().isPresent() ? ("; " + nameBuilt.error().get().message()) : "");
            nameBuilt = DataResult.error(() -> msg, partial);
        }

        if (profession == null || costA == null || result == null) {
            return nameBuilt.flatMap(name ->
                    DataResult.error(() -> "builder: missing required fields")
            );
        }

        DwarfTradeRecipe recipe = new DwarfTradeRecipe(
                profession,
                merchantLevel,
                pool,
                Math.max(0, order),
                costA,
                costB,
                result,
                stats
        );

        DataResult<DwarfTradeRecipe> validated = DwarfTradeRecipe.validateRecipe(recipe);

        DataResult<DwarfTradeRecipe> recipeResult =
                (!errors.isEmpty() && validated.error().isEmpty())
                        ? DataResult.error(() -> "builder: " + String.join("; ", errors), recipe)
                        : validated;

        return nameBuilt.flatMap(name ->
                recipeResult.flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.DWARF_TRADE,
                                name,
                                (RecipeOutput outAccept, ResourceKey<Recipe<?>> id) ->
                                        outAccept.accept(id, validRecipe, null)
                        )
                )
        );
    }

    private enum TradeKind {
        BUY,
        SELL,
        TRADE
    }

    private TradeKind determineKindFailClosed() {
        boolean costHasCoins = costHasCoins(costA) || costHasCoins(costB);
        if (costHasCoins) {
            return TradeKind.BUY;
        }

        boolean resultHasCoins = resultHasCoins(result);
        if (resultHasCoins) {
            return TradeKind.SELL;
        }

        return TradeKind.TRADE;
    }

    private boolean costHasCoins(@Nullable ItemInput in) {
        if (in == null) {
            return false;
        }

        Optional<Holder<Item>> h = in.singleConcrete(Registries.ITEM);
        return h.isPresent() && h.get().is(DwarfTradeRecipe.COINS_TAG);
    }

    private boolean resultHasCoins(@Nullable ItemOutput out) {
        if (out == null) {
            return false;
        }

        Optional<Holder<Item>> h = out.singleConcrete(Registries.ITEM);
        return h.isPresent() && h.get().is(DwarfTradeRecipe.COINS_TAG);
    }

    private String levelNameSafe() {
        if (isGlobalPool()) {
            return JolCraftDictionary.GLOBAL;
        }
        if (merchantLevel == null) {
            return JolCraftDictionary.UNKNOWN;
        }
        return merchantLevel.name().toLowerCase(Locale.ROOT);
    }

    private String tokenFromCostFailClosed(@Nullable ItemInput in, @NotNull String label) {
        if (in == null) {
            errors.add(label + " is missing");
            return JolCraftDictionary.UNKNOWN;
        }

        Optional<Holder<Item>> concrete = in.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            ResourceLocation id = concrete.get()
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            if (id == null) {
                errors.add(label + " has no registry key (for naming)");
                return JolCraftDictionary.UNKNOWN;
            }

            return id.getPath();
        }

        var tag = in.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            if (tag.get().equals(DwarfTradeRecipe.COINS_TAG)) {
                return JolCraftStrings.plural(JolCraftDictionary.COIN);
            }

            return tag.get().location().getPath();
        }

        errors.add(label + " must be a specific item or single tag (for naming)");
        return JolCraftDictionary.UNKNOWN;
    }

    private String tokenFromResultFailClosed(@Nullable ItemOutput out) {
        if (out == null) {
            errors.add("result is missing (for naming)");
            return JolCraftDictionary.UNKNOWN;
        }

        ItemSpec spec = out.result();
        ItemProducer producer = spec.producer();
        Optional<String> tok = producer.mapFileNameTokenOpt();
        if (tok.isPresent()) {
            return tok.get();
        }

        Optional<Holder<Item>> h = out.singleConcrete(Registries.ITEM);
        if (h.isPresent()) {
            ResourceLocation id = h.get()
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            if (id == null) {
                errors.add("result has no registry key (for naming)");
                return JolCraftDictionary.UNKNOWN;
            }

            return id.getPath();
        }

        errors.add("result must be a direct item or map producer (for naming)");
        return JolCraftDictionary.UNKNOWN;
    }
}