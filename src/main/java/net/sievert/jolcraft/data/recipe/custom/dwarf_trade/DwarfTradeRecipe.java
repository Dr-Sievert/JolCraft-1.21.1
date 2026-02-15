package net.sievert.jolcraft.data.recipe.custom.dwarf_trade;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfTradeRecipe implements Recipe<DwarfTradeRecipeInput> {

    public enum TradePool {
        MAIN,
        POOL,
        RESTOCK_POOL
    }

    /**
     * Unified internal representation.
     * Codec accepts either an int or an object {min,max}.
     * IMPORTANT: no throwing invariants here (codec must not crash datapacks).
     * Validation happens in Serializer.validate(...).
     */
    public record TradeAmount(int min, int max) {

        public static TradeAmount fixed(int value) {
            return new TradeAmount(value, value);
        }

        public int roll(RandomSource random) {
            return (min == max) ? min : (min + random.nextInt(max - min + 1));
        }

        // ---- CODEC ----

        private static final Codec<TradeAmount> OBJECT_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MIN, JolCraftDictionary.COUNT)).forGetter(TradeAmount::min),
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftDictionary.COUNT)).forGetter(TradeAmount::max)
                ).apply(inst, TradeAmount::new));

        public static final Codec<TradeAmount> CODEC =
                Codec.either(Codec.INT, OBJECT_CODEC).xmap(
                        e -> e.map(TradeAmount::fixed, a -> a),
                        a -> (a.min == a.max) ? Either.left(a.min) : Either.right(a)
                );

        // ---- STREAM ----

        public static final StreamCodec<RegistryFriendlyByteBuf, TradeAmount> STREAM_CODEC =
                StreamCodec.of(
                        (buf, a) -> {
                            buf.writeVarInt(a.min);
                            buf.writeVarInt(a.max);
                        },
                        buf -> new TradeAmount(buf.readVarInt(), buf.readVarInt())
                );
    }

    // =====================================================================
    // TradeCost (ITEM or TAG)
    // =====================================================================

    /**
     * Cost ingredient for a trade: either a single item or an item tag.
     * IMPORTANT: no throwing invariants here (codec must not crash datapacks).
     * Validation happens in Serializer.validate(...).
     */
    public sealed interface TradeCostIngredient permits TradeCostIngredient.ItemIngredient, TradeCostIngredient.TagIngredient {

        record ItemIngredient(Holder<Item> item) implements TradeCostIngredient {}

        record TagIngredient(TagKey<Item> tag) implements TradeCostIngredient {}

        // Raw form for codec decoding.
        record Raw(Optional<Holder<Item>> item, Optional<TagKey<Item>> tag) {}

        MapCodec<Raw> RAW_CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        RegistryFixedCodec.create(Registries.ITEM)
                                .optionalFieldOf(JolCraftDictionary.ITEM)
                                .forGetter(Raw::item),
                        TagKey.codec(Registries.ITEM)
                                .optionalFieldOf(JolCraftDictionary.TAG)
                                .forGetter(Raw::tag)
                ).apply(inst, Raw::new));


        /**
         * Accepts exactly one of { item, tag }.
         */
        Codec<TradeCostIngredient> CODEC =
                RAW_CODEC.codec().flatXmap(
                        raw -> {
                            boolean hasItem = raw.item().isPresent();
                            boolean hasTag = raw.tag().isPresent();

                            if (hasItem == hasTag) {
                                return DataResult.error(() -> "TradeCost ingredient must define exactly one of 'item' or 'tag'");
                            }

                            return hasItem
                                    ? DataResult.success(new ItemIngredient(raw.item().get()))
                                    : DataResult.success(new TagIngredient(raw.tag().get()));
                        },
                        ing -> {
                            if (ing instanceof ItemIngredient(Holder<Item> item)) {
                                return DataResult.success(new Raw(Optional.of(item), Optional.empty()));
                            }
                            if (ing instanceof TagIngredient(TagKey<Item> tag)) {
                                return DataResult.success(new Raw(Optional.empty(), Optional.of(tag)));
                            }
                            return DataResult.error(() -> "Unknown TradeCostIngredient variant");
                        }
                );

        /**
         * Returns a representative item stack for display/preview.
         * For tags: pick the first entry deterministically from tag contents.
         */
        default ItemStack preview(HolderLookup.Provider registries, int count) {
            if (this instanceof ItemIngredient(Holder<Item> item)) {
                return new ItemStack(item.value(), count);
            }

            TagIngredient ti = (TagIngredient) this;

            var itemRegistry = registries.lookupOrThrow(Registries.ITEM);
            var holdersOpt = itemRegistry.get(ti.tag());

            if (holdersOpt.isEmpty()) return ItemStack.EMPTY;

            Optional<Holder<Item>> first = holdersOpt.get().stream().findFirst();
            return first.map(itemHolder -> new ItemStack(itemHolder.value(), count)).orElse(ItemStack.EMPTY);
        }

        // ---- STREAM ----
        // boolean isTag
        // if false: item ResourceLocation
        // if true : tag ResourceLocation
        StreamCodec<RegistryFriendlyByteBuf, TradeCostIngredient> STREAM_CODEC =
                StreamCodec.of(
                        (buf, ing) -> {
                            if (ing instanceof TagIngredient(TagKey<Item> tag)) {
                                buf.writeBoolean(true);
                                buf.writeResourceLocation(tag.location());
                                return;
                            }

                            buf.writeBoolean(false);

                            ItemIngredient ii = (ItemIngredient) ing;

                            Registry<Item> items = buf.registryAccess().lookupOrThrow(Registries.ITEM);

                            Item value = ii.item().value();
                            ResourceLocation id = items.getKey(value);
                            if (id == null) {
                                throw new IllegalStateException("Unregistered item in TradeCostIngredient: " + value);
                            }

                            buf.writeResourceLocation(id);
                        },
                        buf -> {
                            boolean isTag = buf.readBoolean();
                            if (isTag) {
                                return new TagIngredient(TagKey.create(Registries.ITEM, buf.readResourceLocation()));
                            }

                            Registry<Item> items = buf.registryAccess().lookupOrThrow(Registries.ITEM);

                            ResourceLocation id = buf.readResourceLocation();
                            Item value = items.getValue(id);
                            if (value == null) {
                                throw new IllegalStateException("Unknown item id in TradeCostIngredient: " + id);
                            }

                            return new ItemIngredient(Holder.direct(value));
                        }
                );
    }

    /**
     * IMPORTANT: no throwing invariants here (codec must not crash datapacks).
     * Validation happens in Serializer.validate(...).
     * JSON supports:
     *  { "ingredient": { "item": "namespace:id" }, "amount": 4 }
     *  { "ingredient": { "tag":  "namespace:tag" }, "amount": 4 }
     */
    public record TradeCost(TradeCostIngredient ingredient, TradeAmount amount) {

        public ItemStack roll(HolderLookup.Provider registries, RandomSource random) {
            int count = amount.roll(random);
            return ingredient.preview(registries, count);
        }

        public static final Codec<TradeCost> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        TradeCostIngredient.CODEC
                                .fieldOf(JolCraftDictionary.INGREDIENT)
                                .forGetter(TradeCost::ingredient),
                        TradeAmount.CODEC
                                .fieldOf(JolCraftDictionary.AMOUNT)
                                .forGetter(TradeCost::amount)
                ).apply(inst, TradeCost::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TradeCost> STREAM_CODEC =
                StreamCodec.of(
                        (buf, c) -> {
                            TradeCostIngredient.STREAM_CODEC.encode(buf, c.ingredient);
                            TradeAmount.STREAM_CODEC.encode(buf, c.amount);
                        },
                        buf -> {
                            TradeCostIngredient ing = TradeCostIngredient.STREAM_CODEC.decode(buf);
                            TradeAmount amt = TradeAmount.STREAM_CODEC.decode(buf);
                            return new TradeCost(ing, amt);
                        }
                );
    }

    // =====================================================================
    // MapTradeData / TradeResult (unchanged)
    // =====================================================================

    /**
     * IMPORTANT: no throwing invariants here (codec must not crash datapacks).
     * Validation happens in Serializer.validate(...).
     */
    public record MapTradeData(
            TagKey<Structure> destinationStructureTag,
            String mapDisplayNameKey,
            ResourceLocation mapDecorationTypeId
    ) {
        public static final Codec<MapTradeData> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        TagKey.codec(Registries.STRUCTURE)
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.DESTINATION, JolCraftDictionary.STRUCTURE, JolCraftDictionary.TAG))
                                .forGetter(MapTradeData::destinationStructureTag),
                        Codec.STRING
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAP, JolCraftDictionary.DISPLAY, JolCraftDictionary.NAME))
                                .forGetter(MapTradeData::mapDisplayNameKey),
                        ResourceLocation.CODEC
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAP, JolCraftDictionary.DECORATION, JolCraftDictionary.TYPE))
                                .forGetter(MapTradeData::mapDecorationTypeId)
                ).apply(inst, MapTradeData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MapTradeData> STREAM_CODEC =
                StreamCodec.of(
                        (buf, d) -> {
                            buf.writeResourceLocation(d.destinationStructureTag.location());
                            buf.writeUtf(d.mapDisplayNameKey);
                            buf.writeResourceLocation(d.mapDecorationTypeId);
                        },
                        buf -> new MapTradeData(
                                TagKey.create(Registries.STRUCTURE, buf.readResourceLocation()),
                                buf.readUtf(),
                                buf.readResourceLocation()
                        )
                );
    }

    public sealed interface TradeResult permits TradeResult.ItemResult, TradeResult.MapResult {

        enum Type { ITEM, MAP }

        Type type();

        ItemStack preview(HolderLookup.Provider registries);

        ItemStack roll(HolderLookup.Provider registries, RandomSource random);

        record ItemResult(
                Holder<Item> item,
                TradeAmount amount
        ) implements TradeResult {

            @Override
            public Type type() {
                return Type.ITEM;
            }

            @Override
            public ItemStack preview(HolderLookup.Provider registries) {
                return new ItemStack(item.value(), amount.min());
            }

            @Override
            public ItemStack roll(HolderLookup.Provider registries, RandomSource random) {
                return new ItemStack(item.value(), amount.roll(random));
            }
        }

        record MapResult(
                MapTradeData mapData
        ) implements TradeResult {

            @Override public Type type() { return Type.MAP; }

            @Override
            public ItemStack preview(HolderLookup.Provider registries) {
                return new ItemStack(Items.FILLED_MAP, 1);
            }

            @Override
            public ItemStack roll(HolderLookup.Provider registries, RandomSource random) {
                return new ItemStack(Items.FILLED_MAP, 1);
            }
        }
    }

    // =====================================================================
    // Recipe fields
    // =====================================================================

    private final DwarfProfession profession;
    private final int merchantLevel; // 1..5

    private final TradePool pool;
    private final OptionalInt weight; // relevant only for POOL/RESTOCK_POOL

    private final OptionalInt order;

    private final boolean exactLevel;

    private final TradeCost costA;
    private final Optional<TradeCost> costB;

    private final TradeResult result;

    private final Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider;
    private final Optional<String> stackModifierId;
    private final Optional<DataComponentPatch> resultPatch;

    private final int maxUses;
    private final int dwarfXp;
    private final float priceMultiplier;

    public DwarfTradeRecipe(
            DwarfProfession profession,
            int merchantLevel,
            TradePool pool,
            OptionalInt weight,
            OptionalInt order,
            boolean exactLevel,
            TradeCost costA,
            Optional<TradeCost> costB,
            TradeResult result,
            Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
            Optional<String> stackModifierId,
            Optional<DataComponentPatch> resultPatch,
            int maxUses,
            int dwarfXp,
            float priceMultiplier
    ) {
        this.profession = profession;
        this.merchantLevel = merchantLevel;
        this.pool = pool;
        this.weight = (weight.isEmpty()) ? OptionalInt.empty() : weight;
        this.order = (order.isEmpty()) ? OptionalInt.empty() : order;
        this.exactLevel = exactLevel;
        this.costA = costA;
        this.costB = costB;
        this.result = result;
        this.enchantmentProvider = enchantmentProvider;
        this.stackModifierId = stackModifierId;
        this.resultPatch = resultPatch;
        this.maxUses = maxUses;
        this.dwarfXp = dwarfXp;
        this.priceMultiplier = priceMultiplier;
    }

    // =====================================================================
    // Accessors
    // =====================================================================

    public DwarfProfession profession() { return profession; }
    public int merchantLevel() { return merchantLevel; }

    public TradePool pool() { return pool; }
    public OptionalInt weight() { return weight; }

    public OptionalInt order() { return order; }

    public boolean exactLevel() { return exactLevel; }

    public TradeCost costA() { return costA; }
    public Optional<TradeCost> costB() { return costB; }

    public TradeResult result() { return result; }

    public Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider() { return enchantmentProvider; }
    public Optional<String> stackModifierId() { return stackModifierId; }
    public Optional<DataComponentPatch> resultPatch() { return resultPatch; }

    public int maxUses() { return maxUses; }
    public int dwarfXp() { return dwarfXp; }
    public float priceMultiplier() { return priceMultiplier; }

    // =====================================================================
    // Rolls / resolve helpers (used by your trade engine)
    // =====================================================================

    public ItemStack rollCostA(HolderLookup.Provider registries, RandomSource random) {
        return costA.roll(registries, random);
    }

    public Optional<ItemStack> rollCostB(HolderLookup.Provider registries, RandomSource random) {
        return costB.map(c -> c.roll(registries, random));
    }

    /**
     * IMPORTANT: This returns the BASE result only (no enchant/mod/patch).
     * The trade engine must apply transforms in order:
     * base -> enchant -> stackModifier -> patch
     */
    public ItemStack rollResultBase(HolderLookup.Provider registries, RandomSource random) {
        return result.roll(registries, random);
    }

    // =====================================================================
    // Recipe implementation
    // =====================================================================

    @Override
    public boolean matches(DwarfTradeRecipeInput in, Level level) {
        if (level.isClientSide) return false;
        return in.profession() == profession && in.merchantLevel() == merchantLevel;
    }

    @Override
    public ItemStack assemble(DwarfTradeRecipeInput in, HolderLookup.Provider registries) {
        return result.preview(registries);
    }

    @Override
    public RecipeSerializer<? extends Recipe<DwarfTradeRecipeInput>> getSerializer() {
        return JolCraftRecipes.DWARF_TRADE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<DwarfTradeRecipeInput>> getType() {
        return JolCraftRecipes.DWARF_TRADE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    // =====================================================================
    // Serializer (CODEC + STREAM_CODEC)
    // =====================================================================

    public static final class Serializer implements RecipeSerializer<DwarfTradeRecipe> {

        private static final Codec<DwarfProfession> PROFESSION_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            String id = s.trim().toLowerCase();

                            if (id.equals(DwarfProfession.NONE.getId())) {
                                return DataResult.success(DwarfProfession.NONE);
                            }

                            DwarfProfession p = DwarfProfession.byId(id);
                            if (p == DwarfProfession.NONE) {
                                return DataResult.error(() -> "Unknown profession '" + s + "'");
                            }
                            return DataResult.success(p);
                        },
                        DwarfProfession::getId
                );

        private static final Codec<TradePool> POOL_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            String key = s.trim()
                                    .toUpperCase()
                                    .replace('-', '_')
                                    .replace(' ', '_');
                            try {
                                return DataResult.success(TradePool.valueOf(key));
                            } catch (IllegalArgumentException ex) {
                                return DataResult.error(() -> "Unknown pool '" + s + "'. Valid: main, pool, restock_pool");
                            }
                        },
                        p -> p.name().toLowerCase()
                );

        private static final Codec<ResourceKey<EnchantmentProvider>> ENCHANT_PROVIDER_CODEC =
                ResourceLocation.CODEC.xmap(
                        id -> ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, id),
                        ResourceKey::location
                );

        private static final MapCodec<OptionalInt> WEIGHT_FIELD =
                Codec.INT.optionalFieldOf(JolCraftDictionary.WEIGHT).xmap(
                        opt -> opt.map(OptionalInt::of).orElse(OptionalInt.empty()),
                        oi -> oi.isPresent() ? Optional.of(oi.getAsInt()) : Optional.empty()
                );

        private static final MapCodec<OptionalInt> ORDER_FIELD =
                Codec.INT.optionalFieldOf(JolCraftDictionary.ORDER).xmap(
                        opt -> opt.map(OptionalInt::of).orElse(OptionalInt.empty()),
                        oi -> oi.isPresent() ? Optional.of(oi.getAsInt()) : Optional.empty()
                );

        private static final MapCodec<Boolean> EXACT_LEVEL_FIELD =
                Codec.BOOL.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.EXACT, JolCraftDictionary.LEVEL), false);

        // ---------------- TradeResult CODEC ----------------

        private static final Codec<TradeResult.Type> RESULT_TYPE_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            String key = s.trim().toUpperCase().replace('-', '_').replace(' ', '_');
                            try {
                                return DataResult.success(TradeResult.Type.valueOf(key));
                            } catch (IllegalArgumentException ex) {
                                return DataResult.error(() -> "Unknown result type '" + s + "'. Valid: item, map");
                            }
                        },
                        t -> t.name().toLowerCase()
                );

        private static final MapCodec<TradeResult.ItemResult> ITEM_RESULT_CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        RegistryFixedCodec.create(Registries.ITEM)
                                .fieldOf(JolCraftDictionary.ITEM)
                                .forGetter(TradeResult.ItemResult::item),
                        TradeAmount.CODEC
                                .fieldOf(JolCraftDictionary.AMOUNT)
                                .forGetter(TradeResult.ItemResult::amount)
                ).apply(inst, TradeResult.ItemResult::new));

        private static final MapCodec<TradeResult.MapResult> MAP_RESULT_CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        MapTradeData.CODEC.fieldOf(JolCraftDictionary.MAP).forGetter(TradeResult.MapResult::mapData)
                ).apply(inst, TradeResult.MapResult::new));

        private static final MapCodec<TradeResult> TRADE_RESULT_CODEC =
                RESULT_TYPE_CODEC.dispatchMap(
                        JolCraftDictionary.TYPE,
                        TradeResult::type,
                        type -> switch (type) {
                            case ITEM -> ITEM_RESULT_CODEC.xmap(r -> (TradeResult) r, r -> (TradeResult.ItemResult) r);
                            case MAP  -> MAP_RESULT_CODEC.xmap(r -> (TradeResult) r, r -> (TradeResult.MapResult) r);
                        }
                );

        // ---------------- Recipe CODEC ----------------
        public static final MapCodec<DwarfTradeRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<DwarfTradeRecipe> inst) -> inst.group(
                        PROFESSION_CODEC.fieldOf(JolCraftDictionary.PROFESSION).forGetter(DwarfTradeRecipe::profession),
                        Codec.INT.fieldOf(JolCraftDictionary.LEVEL).forGetter(DwarfTradeRecipe::merchantLevel),

                        POOL_CODEC.optionalFieldOf(JolCraftDictionary.POOL, TradePool.MAIN).forGetter(DwarfTradeRecipe::pool),
                        WEIGHT_FIELD.forGetter(DwarfTradeRecipe::weight),
                        ORDER_FIELD.forGetter(DwarfTradeRecipe::order),
                        EXACT_LEVEL_FIELD.forGetter(DwarfTradeRecipe::exactLevel),

                        TradeCost.CODEC.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.COST, "a")).forGetter(DwarfTradeRecipe::costA),
                        TradeCost.CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.COST, "b")).forGetter(DwarfTradeRecipe::costB),

                        TRADE_RESULT_CODEC.fieldOf(JolCraftDictionary.RESULT).forGetter(DwarfTradeRecipe::result),

                        ENCHANT_PROVIDER_CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.ENCHANTMENT, JolCraftDictionary.PROVIDER)).forGetter(DwarfTradeRecipe::enchantmentProvider),
                        Codec.STRING.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.STACK, JolCraftDictionary.MODIFIER)).forGetter(DwarfTradeRecipe::stackModifierId),
                        DataComponentPatch.CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.RESULT, JolCraftDictionary.PATCH)).forGetter(DwarfTradeRecipe::resultPatch),

                        Codec.INT.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftStrings.plural(JolCraftDictionary.USE)), 12).forGetter(DwarfTradeRecipe::maxUses),
                        Codec.INT.optionalFieldOf(JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.XP), 1).forGetter(DwarfTradeRecipe::dwarfXp),
                        Codec.FLOAT.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.PRICE, JolCraftDictionary.MULTIPLIER), 0.05F).forGetter(DwarfTradeRecipe::priceMultiplier)
                ).apply(inst, DwarfTradeRecipe::new)).flatXmap(
                        Serializer::validate,
                        DataResult::success
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, DwarfTradeRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<DwarfTradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DwarfTradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static DataResult<DwarfTradeRecipe> validate(DwarfTradeRecipe r) {
            // --- merchant level
            if (r.merchantLevel < 1 || r.merchantLevel > 5) {
                return DataResult.error(() -> "level must be 1..5 (got " + r.merchantLevel + ")");
            }

            // --- order
            if (r.order.isPresent() && r.order.getAsInt() < 1) {
                return DataResult.error(() -> "order must be >= 1 (got " + r.order.getAsInt() + ")");
            }

            // --- basic ints
            if (r.maxUses < 1) {
                return DataResult.error(() -> "max_uses must be >= 1 (got " + r.maxUses + ")");
            }
            if (r.dwarfXp < 0) {
                return DataResult.error(() -> "dwarf_xp must be >= 0 (got " + r.dwarfXp + ")");
            }
            if (r.priceMultiplier < 0.0F) {
                return DataResult.error(() -> "price_multiplier must be >= 0 (got " + r.priceMultiplier + ")");
            }

            // --- pool + weight rules (tight)
            if (r.pool == null) {
                return DataResult.error(() -> "pool must be set (main, pool, restock_pool)");
            }
            if (r.pool == TradePool.MAIN) {
                if (r.weight.isPresent()) {
                    return DataResult.error(() -> "weight is not allowed for MAIN trades");
                }
            } else {
                if (r.weight.isEmpty()) {
                    return DataResult.error(() -> "weight is required for " + r.pool.name().toLowerCase() + " trades");
                }
                if (r.weight.getAsInt() < 1) {
                    return DataResult.error(() -> "weight must be >= 1 (got " + r.weight.getAsInt() + ")");
                }
            }

            // --- costs
            if (r.costA == null) {
                return DataResult.error(() -> "cost_a is required");
            }
            if (r.costA.amount().min() < 1 || r.costA.amount().max() < r.costA.amount().min()) {
                return DataResult.error(() -> "cost_a.amount must be >= 1 and max>=min");
            }

            if (r.costB.isPresent()) {
                TradeCost b = r.costB.get();
                if (b.amount().min() < 1 || b.amount().max() < b.amount().min()) {
                    return DataResult.error(() -> "cost_b.amount must be >= 1 and max>=min");
                }
            }

            // --- result
            if (r.result == null) {
                return DataResult.error(() -> "result is required");
            }

            switch (r.result.type()) {
                case ITEM -> {
                    var ir = (TradeResult.ItemResult) r.result;
                    if (ir.item().value() == Items.AIR) {
                        return DataResult.error(() -> "result.item must not be air");
                    }
                    if (ir.amount().min() < 1 || ir.amount().max() < ir.amount().min()) {
                        return DataResult.error(() -> "result.amount must be >= 1 and max>=min");
                    }
                }
                case MAP -> {
                    var mr = (TradeResult.MapResult) r.result;
                    MapTradeData d = mr.mapData();
                    if (d.mapDisplayNameKey().isBlank()) {
                        return DataResult.error(() -> "result.map.map_display_name must not be blank");
                    }
                }
            }

            // --- stack modifier id (datapack safety)
            if (r.stackModifierId.isPresent()) {
                String raw = r.stackModifierId.get().trim();
                if (raw.isEmpty()) {
                    return DataResult.error(() -> "stack_modifier must not be blank when present");
                }
                if (ResourceLocation.tryParse(raw) == null) {
                    return DataResult.error(() -> "stack_modifier must be a valid resource location (got '" + raw + "')");
                }
            }

            return DataResult.success(r);
        }

        // ---------------- STREAM ----------------

        private static void toNetwork(RegistryFriendlyByteBuf buf, DwarfTradeRecipe r) {
            buf.writeUtf(r.profession.getId());
            buf.writeVarInt(r.merchantLevel);

            buf.writeEnum(r.pool);

            buf.writeBoolean(r.weight.isPresent());
            if (r.weight.isPresent()) buf.writeVarInt(r.weight.getAsInt());

            buf.writeBoolean(r.order.isPresent());
            if (r.order.isPresent()) buf.writeVarInt(r.order.getAsInt());

            buf.writeBoolean(r.exactLevel);

            TradeCost.STREAM_CODEC.encode(buf, r.costA);
            writeOptionalTradeCost(buf, r.costB);

            writeTradeResult(buf, r.result);

            writeHooks(buf, r.enchantmentProvider, r.stackModifierId, r.resultPatch);

            buf.writeVarInt(r.maxUses);
            buf.writeVarInt(r.dwarfXp);
            buf.writeFloat(r.priceMultiplier);
        }

        private static DwarfTradeRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            DwarfProfession profession = DwarfProfession.byId(buf.readUtf());
            int level = buf.readVarInt();

            TradePool pool = buf.readEnum(TradePool.class);

            OptionalInt weight = buf.readBoolean() ? OptionalInt.of(buf.readVarInt()) : OptionalInt.empty();
            OptionalInt order = buf.readBoolean() ? OptionalInt.of(buf.readVarInt()) : OptionalInt.empty();

            boolean exactLevel = buf.readBoolean();

            TradeCost costA = TradeCost.STREAM_CODEC.decode(buf);
            Optional<TradeCost> costB = readOptionalTradeCost(buf);

            TradeResult result = readTradeResult(buf);

            Hooks hooks = readHooks(buf);

            int maxUses = buf.readVarInt();
            int dwarfXp = buf.readVarInt();
            float priceMultiplier = buf.readFloat();

            return new DwarfTradeRecipe(
                    profession,
                    level,
                    pool,
                    weight,
                    order,
                    exactLevel,
                    costA,
                    costB,
                    result,
                    hooks.enchant,
                    hooks.stackMod,
                    hooks.patch,
                    maxUses,
                    dwarfXp,
                    priceMultiplier
            );
        }

        private static void writeOptionalTradeCost(RegistryFriendlyByteBuf buf, Optional<TradeCost> opt) {
            buf.writeBoolean(opt.isPresent());
            opt.ifPresent(v -> TradeCost.STREAM_CODEC.encode(buf, v));
        }

        private static Optional<TradeCost> readOptionalTradeCost(RegistryFriendlyByteBuf buf) {
            return buf.readBoolean() ? Optional.of(TradeCost.STREAM_CODEC.decode(buf)) : Optional.empty();
        }

        private static void writeTradeResult(RegistryFriendlyByteBuf buf, TradeResult r) {
            buf.writeEnum(r.type());

            switch (r.type()) {
                case ITEM -> {
                    var itemRes = (TradeResult.ItemResult) r;

                    Registry<Item> items = buf.registryAccess().lookupOrThrow(Registries.ITEM);
                    Item value = itemRes.item().value();

                    ResourceLocation id = items.getKey(value);
                    if (id == null) {
                        throw new IllegalStateException("Unregistered item in TradeResult.ItemResult: " + value);
                    }

                    buf.writeResourceLocation(id);
                    TradeAmount.STREAM_CODEC.encode(buf, itemRes.amount());
                }
                case MAP -> {
                    var mapRes = (TradeResult.MapResult) r;
                    MapTradeData.STREAM_CODEC.encode(buf, mapRes.mapData());
                }
            }
        }

        private static TradeResult readTradeResult(RegistryFriendlyByteBuf buf) {
            TradeResult.Type type = buf.readEnum(TradeResult.Type.class);

            return switch (type) {
                case ITEM -> {
                    Registry<Item> items = buf.registryAccess().lookupOrThrow(Registries.ITEM);

                    ResourceLocation id = buf.readResourceLocation();
                    Item value = items.getValue(id);
                    if (value == null) {
                        throw new IllegalStateException("Unknown item id in TradeResult.ItemResult: " + id);
                    }

                    TradeAmount amount = TradeAmount.STREAM_CODEC.decode(buf);
                    yield new TradeResult.ItemResult(Holder.direct(value), amount);
                }
                case MAP -> {
                    MapTradeData map = MapTradeData.STREAM_CODEC.decode(buf);
                    yield new TradeResult.MapResult(map);
                }
            };
        }

        private record Hooks(
                Optional<ResourceKey<EnchantmentProvider>> enchant,
                Optional<String> stackMod,
                Optional<DataComponentPatch> patch
        ) {}

        private static void writeHooks(
                RegistryFriendlyByteBuf buf,
                Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
                Optional<String> stackModifierId,
                Optional<DataComponentPatch> resultPatch
        ) {
            buf.writeBoolean(enchantmentProvider.isPresent());
            enchantmentProvider.ifPresent(k -> buf.writeResourceLocation(k.location()));

            buf.writeBoolean(stackModifierId.isPresent());
            stackModifierId.ifPresent(buf::writeUtf);

            buf.writeBoolean(resultPatch.isPresent());
            resultPatch.ifPresent(p -> DataComponentPatch.STREAM_CODEC.encode(buf, p));
        }

        private static Hooks readHooks(RegistryFriendlyByteBuf buf) {
            Optional<ResourceKey<EnchantmentProvider>> enchant =
                    buf.readBoolean()
                            ? Optional.of(ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, buf.readResourceLocation()))
                            : Optional.empty();

            Optional<String> stackMod = buf.readBoolean() ? Optional.of(buf.readUtf()) : Optional.empty();

            Optional<DataComponentPatch> patch =
                    buf.readBoolean()
                            ? Optional.of(DataComponentPatch.STREAM_CODEC.decode(buf))
                            : Optional.empty();

            return new Hooks(enchant, stackMod, patch);
        }
    }
}