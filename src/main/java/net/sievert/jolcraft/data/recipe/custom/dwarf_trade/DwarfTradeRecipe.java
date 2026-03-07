package net.sievert.jolcraft.data.recipe.custom.dwarf_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipeValidation;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomRecipe;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ComponentTransform;
import net.sievert.jolcraft.data.recipe.param.quantity.WeightParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record DwarfTradeRecipe(
        DwarfProfession profession,
        DwarfMerchantData.Level merchantLevel,
        TradePoolEntry pool,
        int order,
        ItemInput costA,
        ItemInput costB,
        ItemOutput result,
        TradeStats stats
) implements CustomRecipe<DwarfTradeRecipeInput> {

    public static final TagKey<Item> COINS_TAG = JolCraftTags.Items.COINS;

    public static final ItemLike GOLD_COIN = JolCraftItems.GOLD_COIN.get();

    public static final String KEY_POOL = JolCraftDictionary.POOL;
    public static final String KEY_GROUP = JolCraftDictionary.GROUP;
    public static final String KEY_WEIGHT = JolCraftDictionary.WEIGHT;

    public static final String SOURCE_COST_A =
            JolCraftStrings.underscored(JolCraftDictionary.COST, "a");

    public static final String SOURCE_COST_B =
            JolCraftStrings.underscored(JolCraftDictionary.COST, "b");

    public enum TradeGroup {
        MAIN,
        GLOBAL_POOL,
        CUMULATIVE_POOL,
        EXACT_LEVEL_POOL;

        public String serializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static @NotNull DataResult<TradeGroup> fromSerialized(@NotNull String s) {
            try {
                return DataResult.success(valueOf(s.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                return DataResult.error(() -> "unknown group '" + s + "'");
            }
        }

        public @Nullable DwarfProfessionConfig.PoolType poolType() {
            return switch (this) {
                case MAIN -> null;
                case GLOBAL_POOL -> DwarfProfessionConfig.PoolType.GLOBAL;
                case CUMULATIVE_POOL -> DwarfProfessionConfig.PoolType.CUMULATIVE;
                case EXACT_LEVEL_POOL -> DwarfProfessionConfig.PoolType.EXACT_LEVEL;
            };
        }
    }

    public record TradePoolEntry(
            TradeGroup group,
            WeightParam weight
    ) {
        public static final TradePoolEntry MAIN =
                new TradePoolEntry(TradeGroup.MAIN, WeightParam.ONE);

        private record RawTradePoolEntry(
                TradeGroup group,
                Optional<WeightParam> weight
        ) {}

        private static final Codec<TradeGroup> GROUP_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            if (s == null) {
                                return DataResult.error(() -> "group is null");
                            }
                            return TradeGroup.fromSerialized(s);
                        },
                        TradeGroup::serializedName
                );

        private static final MapCodec<RawTradePoolEntry> RAW_MAP_CODEC =
                RecordCodecBuilder.mapCodec(
                        (RecordCodecBuilder.Instance<RawTradePoolEntry> inst) -> inst.group(
                                GROUP_CODEC.optionalFieldOf(KEY_GROUP, TradeGroup.MAIN)
                                        .forGetter(RawTradePoolEntry::group),

                                WeightParam.CODEC.optionalFieldOf(KEY_WEIGHT)
                                        .forGetter(RawTradePoolEntry::weight)

                        ).apply(inst, RawTradePoolEntry::new)
                );

        public static final MapCodec<TradePoolEntry> MAP_CODEC =
                RAW_MAP_CODEC.flatXmap(
                        TradePoolEntry::decodeValidated,
                        TradePoolEntry::encodeValidated
                );

        public static final Codec<TradePoolEntry> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, TradePoolEntry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, entry) -> {
                            buf.writeUtf(entry.group().name());
                            WeightParam.STREAM_CODEC.encode(buf, entry.weight());
                        },
                        buf -> {
                            TradeGroup group;
                            try {
                                group = TradeGroup.valueOf(buf.readUtf().trim().toUpperCase(Locale.ROOT));
                            } catch (IllegalArgumentException e) {
                                group = TradeGroup.MAIN;
                            }

                            WeightParam weight = WeightParam.STREAM_CODEC.decode(buf);
                            return new TradePoolEntry(group, weight);
                        }
                );

        public TradePoolEntry {
            group = group != null ? group : TradeGroup.MAIN;
            weight = weight != null ? weight : WeightParam.ONE;
        }

        private static @NotNull DataResult<TradePoolEntry> decodeValidated(RawTradePoolEntry raw) {
            TradeGroup group = raw.group() != null ? raw.group() : TradeGroup.MAIN;
            Optional<WeightParam> weightOpt = raw.weight() != null ? raw.weight() : Optional.empty();

            if (group == TradeGroup.MAIN && weightOpt.isPresent()) {
                return DataResult.error(() -> "pool.weight must not be provided for main trades");
            }

            if (weightOpt.isPresent()) {
                DataResult<WeightParam> wv = weightOpt.get().validate();
                var wErr = wv.error();
                if (wErr.isPresent()) {
                    return DataResult.error(() -> "pool.weight invalid: " + wErr.get().message());
                }
            }

            return DataResult.success(new TradePoolEntry(
                    group,
                    weightOpt.orElse(WeightParam.ONE)
            ));
        }

        private static @NotNull DataResult<RawTradePoolEntry> encodeValidated(TradePoolEntry entry) {
            DataResult<TradePoolEntry> validated = validate(entry);
            var err = validated.error();
            if (err.isPresent()) {
                return DataResult.error(() -> err.get().message());
            }

            Optional<WeightParam> weightOpt =
                    entry.group() == TradeGroup.MAIN
                            ? Optional.empty()
                            : Optional.of(entry.weight());

            return DataResult.success(new RawTradePoolEntry(
                    entry.group(),
                    weightOpt
            ));
        }

        public static @NotNull DataResult<TradePoolEntry> validate(TradePoolEntry entry) {
            if (entry.group() == null) {
                return DataResult.error(() -> "pool.group is required");
            }

            if (entry.weight() == null) {
                return DataResult.error(() -> "pool.weight is required");
            }

            {
                DataResult<WeightParam> wv = entry.weight().validate();
                var wErr = wv.error();
                if (wErr.isPresent()) {
                    return DataResult.error(() -> "pool.weight invalid: " + wErr.get().message());
                }
            }

            if (entry.group() == TradeGroup.MAIN && entry.weight().safe() != 1) {
                return DataResult.error(() -> "main trades must use default pool weight");
            }

            return DataResult.success(entry);
        }
    }

    public static final DwarfTradeRecipe EMPTY =
            new DwarfTradeRecipe(
                    DwarfProfession.NONE,
                    DwarfMerchantData.Level.NOVICE,
                    TradePoolEntry.MAIN,
                    0,
                    ItemInput.EMPTY,
                    ItemInput.EMPTY,
                    ItemOutput.EMPTY,
                    TradeStats.DEFAULT
            );

    public DwarfTradeRecipe {
        profession = profession != null ? profession : DwarfProfession.NONE;
        merchantLevel = merchantLevel != null ? merchantLevel : DwarfMerchantData.Level.NOVICE;
        pool = pool != null ? pool : TradePoolEntry.MAIN;

        order = Math.max(0, order);

        costA = costA != null ? costA : ItemInput.EMPTY;
        costB = costB != null ? costB : ItemInput.EMPTY;

        result = result != null ? result : ItemOutput.EMPTY;

        stats = stats != null ? stats : TradeStats.DEFAULT;
    }

    public static final int DEFAULT_MAX_USES = 5;
    public static final int DEFAULT_DWARF_XP = 0;
    public static final float DEFAULT_PRICE_MULTIPLIER = 0.05F;

    public record TradeStats(
            int maxUses,
            int dwarfXp,
            float priceMultiplier
    ) {
        public static final TradeStats DEFAULT =
                new TradeStats(
                        DEFAULT_MAX_USES,
                        DEFAULT_DWARF_XP,
                        DEFAULT_PRICE_MULTIPLIER
                );
    }

    @Override
    public boolean matches(@NotNull DwarfTradeRecipeInput in, Level level) {
        if (level.isClientSide) return false;
        if (in.profession() != profession) return false;
        if (in.merchantLevel().getId() < merchantLevel.getId()) return false;

        WorldContext ctx = in.ctx();

        if (!costA.matches(ctx, in.costA())) return false;

        if (costB != ItemInput.EMPTY) {
            return costB.matches(ctx, in.costB());
        }

        return in.costB().isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull DwarfTradeRecipeInput in, @NotNull HolderLookup.Provider registries) {
        WorldContext ctx = in.ctx();
        if (ctx.level().isClientSide) return ItemStack.EMPTY;

        if (!matches(in, ctx.level())) return ItemStack.EMPTY;

        ItemOutput out = result;
        if (out == null || out == ItemOutput.EMPTY) return ItemStack.EMPTY;

        List<Output> generated = out.generateResolved(ctx, in);
        if (generated.isEmpty()) return ItemStack.EMPTY;

        for (Output o : generated) {
            if (o instanceof Output.Items items) {
                List<ItemStack> stacks = items.stacksSafe();
                if (!stacks.isEmpty()) {
                    ItemStack stack = stacks.getFirst();
                    return stack.isEmpty() ? ItemStack.EMPTY : stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<DwarfTradeRecipeInput>> getSerializer() {
        return JolCraftRecipes.DWARF_TRADE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<DwarfTradeRecipeInput>> getType() {
        return JolCraftRecipes.DWARF_TRADE_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<DwarfTradeRecipe> {

        private static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> RESULT_STREAM_CODEC =
                StreamCodec.of(
                        OutputDispatch.STREAM_CODEC::encode,
                        buf -> {
                            OutputParam op = OutputDispatch.STREAM_CODEC.decode(buf);
                            OutputParam leaf = OutputParam.unwrap(op);
                            return (leaf instanceof ItemOutput io) ? io : ItemOutput.EMPTY;
                        }
                );

        private static final Codec<DwarfProfession> PROFESSION_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            DwarfProfession p = DwarfProfession.fromProfessionName(s);
                            if (p == DwarfProfession.NONE) {
                                return DataResult.error(() -> "unknown profession '" + s + "'");
                            }
                            return DataResult.success(p);
                        },
                        DwarfProfession::professionName
                );

        private static final Codec<DwarfMerchantData.Level> LEVEL_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            if (s == null) return DataResult.error(() -> "level is null");
                            try {
                                return DataResult.success(
                                        DwarfMerchantData.Level.valueOf(s.trim().toUpperCase(Locale.ROOT))
                                );
                            } catch (IllegalArgumentException e) {
                                return DataResult.error(() -> "unknown level '" + s + "'");
                            }
                        },
                        lvl -> lvl.name().toLowerCase(Locale.ROOT)
                );

        private static final Codec<TradeStats> STATS_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Codec.INT.optionalFieldOf(
                                        JolCraftStrings.underscored(
                                                JolCraftDictionary.MAX,
                                                JolCraftStrings.plural(JolCraftDictionary.USE)),
                                        DEFAULT_MAX_USES)
                                .forGetter(TradeStats::maxUses),

                        Codec.INT.optionalFieldOf(
                                        JolCraftStrings.underscored(
                                                JolCraftDwarfIds.DWARF,
                                                JolCraftDictionary.XP),
                                        DEFAULT_DWARF_XP)
                                .forGetter(TradeStats::dwarfXp),

                        Codec.FLOAT.optionalFieldOf(
                                        JolCraftStrings.underscored(
                                                JolCraftDictionary.PRICE,
                                                JolCraftDictionary.MULTIPLIER),
                                        DEFAULT_PRICE_MULTIPLIER)
                                .forGetter(TradeStats::priceMultiplier)

                ).apply(inst, TradeStats::new));

        public static final MapCodec<DwarfTradeRecipe> CODEC =
                RecordCodecBuilder.mapCodec(
                        (RecordCodecBuilder.Instance<DwarfTradeRecipe> inst) -> inst.group(

                                PROFESSION_CODEC.fieldOf(JolCraftDictionary.PROFESSION)
                                        .forGetter(DwarfTradeRecipe::profession),

                                LEVEL_CODEC.fieldOf(JolCraftDictionary.LEVEL)
                                        .forGetter(DwarfTradeRecipe::merchantLevel),

                                TradePoolEntry.CODEC.optionalFieldOf(KEY_POOL, TradePoolEntry.MAIN)
                                        .forGetter(DwarfTradeRecipe::pool),

                                Codec.INT.optionalFieldOf(JolCraftDictionary.ORDER, 0)
                                        .forGetter(DwarfTradeRecipe::order),

                                ItemInput.CODEC.fieldOf(
                                                JolCraftStrings.underscored(JolCraftDictionary.COST, "a"))
                                        .forGetter(DwarfTradeRecipe::costA),

                                ItemInput.CODEC.optionalFieldOf(
                                                JolCraftStrings.underscored(JolCraftDictionary.COST, "b"),
                                                ItemInput.EMPTY)
                                        .forGetter(DwarfTradeRecipe::costB),

                                OutputDispatch.CODEC
                                        .fieldOf(JolCraftDictionary.RESULT)
                                        .xmap(
                                                op -> {
                                                    OutputParam leaf = OutputParam.unwrap(op);
                                                    return (leaf instanceof ItemOutput io) ? io : ItemOutput.EMPTY;
                                                },
                                                io -> io != null ? io : ItemOutput.EMPTY
                                        )
                                        .forGetter(DwarfTradeRecipe::result),

                                STATS_CODEC.optionalFieldOf(
                                                JolCraftStrings.plural(JolCraftDictionary.STAT),
                                                TradeStats.DEFAULT)
                                        .forGetter(DwarfTradeRecipe::stats)

                        ).apply(inst, DwarfTradeRecipe::new)
                ).validate(DwarfTradeRecipe::validateRecipe);

        public static final StreamCodec<RegistryFriendlyByteBuf, DwarfTradeRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            buf.writeUtf(recipe.profession().getId());
                            buf.writeVarInt(recipe.merchantLevel().getId());
                            TradePoolEntry.STREAM_CODEC.encode(buf, recipe.pool());
                            buf.writeVarInt(recipe.order());
                            ItemInput.STREAM_CODEC.encode(buf, recipe.costA());
                            ItemInput.STREAM_CODEC.encode(buf, recipe.costB());
                            RESULT_STREAM_CODEC.encode(buf, recipe.result());

                            TradeStats stats = recipe.stats();
                            buf.writeVarInt(stats.maxUses());
                            buf.writeVarInt(stats.dwarfXp());
                            buf.writeFloat(stats.priceMultiplier());
                        },
                        buf -> {
                            DwarfProfession profession = DwarfProfession.byId(buf.readUtf());
                            DwarfMerchantData.Level merchantLevel = DwarfMerchantData.Level.fromId(buf.readVarInt());
                            TradePoolEntry pool = TradePoolEntry.STREAM_CODEC.decode(buf);
                            int order = buf.readVarInt();
                            ItemInput costA = ItemInput.STREAM_CODEC.decode(buf);
                            ItemInput costB = ItemInput.STREAM_CODEC.decode(buf);
                            ItemOutput result = RESULT_STREAM_CODEC.decode(buf);

                            TradeStats stats = new TradeStats(
                                    buf.readVarInt(),
                                    buf.readVarInt(),
                                    buf.readFloat()
                            );

                            return new DwarfTradeRecipe(
                                    profession,
                                    merchantLevel,
                                    pool,
                                    order,
                                    costA,
                                    costB,
                                    result,
                                    stats
                            );
                        }
                );

        @Override
        public @NotNull MapCodec<DwarfTradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, DwarfTradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    private static boolean supportsSource(@NotNull String source) {
        return SOURCE_COST_A.equals(source) || SOURCE_COST_B.equals(source);
    }

    public static @NotNull DataResult<DwarfTradeRecipe> validateRecipe(DwarfTradeRecipe r) {
        DataResult<DwarfTradeRecipe> rr = JolCraftRecipeValidation.requireRecipe(r);
        var rrErr = rr.error();
        if (rrErr.isPresent()) {
            String msg = rrErr.map(DataResult.Error::message).orElse("recipe is null");
            return DataResult.error(() -> msg);
        }

        DwarfTradeRecipe recipe = rr.result().orElse(null);
        if (recipe == null) {
            return DataResult.error(() -> "recipe is null");
        }

        if (recipe.profession() == null)
            return DataResult.error(() -> "profession is required");

        if (recipe.merchantLevel() == null)
            return DataResult.error(() -> "level is required");

        if (recipe.pool() == null)
            return DataResult.error(() -> "pool is required");

        {
            DataResult<TradePoolEntry> poolValidation = TradePoolEntry.validate(recipe.pool());
            var poolErr = poolValidation.error();
            if (poolErr.isPresent()) {
                return DataResult.error(() -> poolErr.get().message());
            }
        }

        if (recipe.order() < 0)
            return DataResult.error(() -> "order must be >= 0");

        if (recipe.stats() == null)
            return DataResult.error(() -> "stats is required");

        if (recipe.stats().maxUses() < 1)
            return DataResult.error(() -> "max_uses must be >= 1");

        if (recipe.stats().dwarfXp() < 0)
            return DataResult.error(() -> "dwarf_xp must be >= 0");

        if (recipe.stats().priceMultiplier() < 0.0F)
            return DataResult.error(() -> "price_multiplier must be >= 0");

        if (!recipe.costA().exactlyOneConcrete(Registries.ITEM) && !recipe.costA().exactlyOneTag(Registries.ITEM)) {
            return DataResult.error(() -> "cost_a must be a specific item or single tag");
        }

        if (recipe.costB() != ItemInput.EMPTY
                && !recipe.costB().exactlyOneConcrete(Registries.ITEM)
                && !recipe.costB().exactlyOneTag(Registries.ITEM)) {
            return DataResult.error(() -> "cost_b must be a specific item or single tag");
        }

        ItemOutput out = recipe.result();
        if (out == null || out == ItemOutput.EMPTY)
            return DataResult.error(() -> "result is required");

        var outErr = out.validate().error();
        if (outErr.isPresent()) {
            String msg = outErr.map(e -> "result invalid: " + e.message()).orElse("result invalid");
            return DataResult.error(() -> msg);
        }

        ItemSpec spec = out.result();
        if (spec == null || spec == ItemSpec.EMPTY) {
            return DataResult.error(() -> "result.result is required");
        }

        ItemProducer producer = spec.producer();
        if (producer == null) {
            return DataResult.error(() -> "result.result.producer is required");
        }

        if (!producer.isItemSelection()) {
            return DataResult.error(() -> "result.result.producer must be item-based for dwarf trades");
        }

        List<ComponentTransform> components = out.transforms().components();
        for (ComponentTransform transform : components) {
            if (!(transform instanceof ComponentTransform.Config c)) continue;

            String source = c.source();
            if (source == null) continue;

            if (!supportsSource(source)) {
                return DataResult.error(() ->
                        "unsupported component transform source for dwarf trade: '" + source + "'"
                );
            }
        }

        Holder<Item> costAH = recipe.costA().singleConcrete(Registries.ITEM).orElse(null);
        Holder<Item> costBH = recipe.costB() != ItemInput.EMPTY
                ? recipe.costB().singleConcrete(Registries.ITEM).orElse(null)
                : null;

        boolean anyCostIsCoins =
                (costAH != null && costAH.is(COINS_TAG)) ||
                        (costBH != null && costBH.is(COINS_TAG));

        if (anyCostIsCoins) {
            Optional<Holder<Item>> resultOpt = out.singleConcrete(Registries.ITEM);
            //noinspection deprecation
            if (resultOpt.isPresent() && resultOpt.get().is(GOLD_COIN.asItem().builtInRegistryHolder())) {
                return DataResult.error(() ->
                        "invalid trade: cost contains coins-tag but result is gold_coin"
                );
            }
        }

        return DataResult.success(recipe);
    }
}