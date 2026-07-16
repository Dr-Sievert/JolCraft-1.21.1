package net.sievert.jolcraft.world.recipe.custom.dwarf_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradePoolType;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record DwarfTradeRecipe(
        DwarfProfession profession,
        @Nullable DwarfMerchantData.Level merchantLevel,
        TradePoolEntry pool,
        int order,
        TradeCost costA,
        @Nullable TradeCost costB,
        ItemOutput result,
        TradeStats stats
) implements CustomRecipe<DwarfTradeRecipeInput> {

    public static final TagKey<Item> COINS_TAG =
            JolCraftTags.Items.COINS;

    public static final Item GOLD_COIN =
            JolCraftItems.GOLD_COIN.get();

    public static final String KEY_POOL =
            JolCraftDictionary.POOL;

    public static final String KEY_GROUP =
            JolCraftDictionary.GROUP;

    public static final String KEY_WEIGHT =
            JolCraftDictionary.WEIGHT;

    public static final String KEY_COUNT =
            JolCraftDictionary.COUNT;

    public static final String KEY_INGREDIENT =
            JolCraftDictionary.INGREDIENT;

    public static final String KEY_MAX_USES =
            JolCraftStrings.underscored(
                    JolCraftDictionary.MAX,
                    JolCraftStrings.plural(
                            JolCraftDictionary.USE
                    )
            );

    public static final String KEY_DWARF_XP =
            JolCraftStrings.underscored(
                    JolCraftDwarfIds.DWARF,
                    JolCraftDictionary.XP
            );

    public static final String KEY_PRICE_MULTIPLIER =
            JolCraftStrings.underscored(
                    JolCraftDictionary.PRICE,
                    JolCraftDictionary.MULTIPLIER
            );

    public static final String SOURCE_COST_A =
            JolCraftStrings.underscored(
                    JolCraftDictionary.COST,
                    "a"
            );

    public static final String SOURCE_COST_B =
            JolCraftStrings.underscored(
                    JolCraftDictionary.COST,
                    "b"
            );

    public static final int DEFAULT_MAX_USES = 5;
    public static final int DEFAULT_DWARF_XP = 0;

    public static final float DEFAULT_PRICE_MULTIPLIER = 0.05F;

    private static final LootContextParamSet RESULT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    public DwarfTradeRecipe {
        if (profession == null) {
            throw new IllegalArgumentException(
                    "profession is required"
            );
        }

        pool = pool != null
                ? pool
                : TradePoolEntry.MAIN;

        if (order < 0) {
            throw new IllegalArgumentException(
                    "order must be >= 0"
            );
        }

        if (costA == null) {
            throw new IllegalArgumentException(
                    SOURCE_COST_A + " is required"
            );
        }

        if (result == null) {
            throw new IllegalArgumentException(
                    JolCraftDictionary.RESULT
                            + " is required"
            );
        }

        stats = stats != null
                ? stats
                : TradeStats.DEFAULT;
    }

    // -------------------------------------------------------------------------
    // Dynamic trade costs
    // -------------------------------------------------------------------------

    /**
     * Recipe-side description of a dwarf trade cost.
     *
     * The ingredient determines which item may be used. The count provider is
     * rolled once when the merchant offer is created. The resulting concrete
     * item and count are then stored in DwarfItemCost.
     */
    public record TradeCost(
            Ingredient ingredient,
            NumberProvider count
    ) {

        public static final NumberProvider DEFAULT_COUNT =
                ConstantValue.exactly(1.0F);

        public static final MapCodec<TradeCost> MAP_CODEC =
                RecordCodecBuilder.<TradeCost>mapCodec(instance ->
                        instance.group(
                                Ingredient.CODEC
                                        .fieldOf(KEY_INGREDIENT)
                                        .forGetter(TradeCost::ingredient),

                                NumberProviders.CODEC
                                        .optionalFieldOf(
                                                KEY_COUNT,
                                                DEFAULT_COUNT
                                        )
                                        .forGetter(TradeCost::count)
                        ).apply(
                                instance,
                                TradeCost::new
                        )
                ).flatXmap(
                        TradeCost::validate,
                        DataResult::success
                );

        public static final Codec<TradeCost> CODEC =
                MAP_CODEC.codec();

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                TradeCost
                > STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.fromCodecWithRegistries(
                                Ingredient.CODEC
                        ),
                        TradeCost::ingredient,

                        ByteBufCodecs.fromCodecWithRegistries(
                                NumberProviders.CODEC
                        ),
                        TradeCost::count,

                        TradeCost::new
                );

        public TradeCost {
            if (ingredient == null) {
                throw new IllegalArgumentException(
                        KEY_INGREDIENT + " is required"
                );
            }

            if (count == null) {
                throw new IllegalArgumentException(
                        KEY_COUNT + " is required"
                );
            }
        }

        /**
         * Tests only the item identity/components represented by the
         * ingredient.
         *
         * The count cannot be rerolled here because the offer already contains
         * the concrete count rolled during offer construction.
         */
        public boolean test(
                @Nullable ItemStack stack
        ) {
            return stack != null
                    && !stack.isEmpty()
                    && ingredient.test(stack);
        }

        /**
         * Rolls the concrete cost count exactly once.
         *
         * Returns zero for an invalid result so callers can fail closed instead
         * of silently converting malformed recipe data into a valid trade.
         */
        public int resolveCount(
                @NotNull LootContext context
        ) {
            int resolved =
                    count.getInt(context);

            return resolved >= 1
                    ? resolved
                    : 0;
        }

        public @NotNull ItemStack[] candidateItems() {
            return ingredient.getItems();
        }

        public boolean contains(
                @NotNull TagKey<Item> tag
        ) {
            return Arrays.stream(
                            ingredient.getItems()
                    )
                    .anyMatch(stack ->
                            !stack.isEmpty()
                                    && stack.is(tag)
                    );
        }

        public static @NotNull DataResult<TradeCost> validate(
                @Nullable TradeCost cost
        ) {
            if (cost == null) {
                return DataResult.error(() ->
                        "trade cost is required"
                );
            }

            if (cost.ingredient() == null) {
                return DataResult.error(() ->
                        KEY_INGREDIENT + " is required"
                );
            }

            if (cost.ingredient().isEmpty()) {
                return DataResult.error(() ->
                        KEY_INGREDIENT + " must not be empty"
                );
            }

            if (cost.count() == null) {
                return DataResult.error(() ->
                        KEY_COUNT + " is required"
                );
            }

            return DataResult.success(cost);
        }
    }

    // -------------------------------------------------------------------------
    // Trade grouping
    // -------------------------------------------------------------------------

    public enum TradeGroup {
        MAIN,
        GLOBAL_POOL,
        CUMULATIVE_POOL,
        EXACT_LEVEL_POOL;

        public String serializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static @NotNull DataResult<TradeGroup> fromSerialized(
                @Nullable String raw
        ) {
            if (raw == null) {
                return DataResult.error(() ->
                        KEY_GROUP + " is required"
                );
            }

            if (raw.isBlank()) {
                return DataResult.error(() ->
                        KEY_GROUP + " must not be empty"
                );
            }

            try {
                return DataResult.success(
                        valueOf(
                                raw.trim()
                                        .toUpperCase(Locale.ROOT)
                        )
                );
            } catch (IllegalArgumentException exception) {
                return DataResult.error(() ->
                        "unknown "
                                + KEY_GROUP
                                + " '"
                                + raw
                                + "'"
                );
            }
        }

        public @Nullable TradePoolType poolType() {
            return switch (this) {
                case MAIN -> null;
                case GLOBAL_POOL ->
                        TradePoolType.GLOBAL;
                case CUMULATIVE_POOL ->
                        TradePoolType.CUMULATIVE;
                case EXACT_LEVEL_POOL ->
                        TradePoolType.EXACT_LEVEL;
            };
        }
    }

    public record TradePoolEntry(
            TradeGroup group,
            int weight
    ) {

        public static final int DEFAULT_WEIGHT = 1;

        public static final TradePoolEntry MAIN =
                new TradePoolEntry(
                        TradeGroup.MAIN,
                        DEFAULT_WEIGHT
                );

        private static final Codec<TradeGroup> GROUP_CODEC =
                Codec.STRING.comapFlatMap(
                        TradeGroup::fromSerialized,
                        TradeGroup::serializedName
                );

        public static final MapCodec<TradePoolEntry> MAP_CODEC =
                RecordCodecBuilder.mapCodec(
                        (
                                RecordCodecBuilder.Instance<
                                        TradePoolEntry
                                        > instance
                        ) ->
                                instance.group(
                                        GROUP_CODEC
                                                .optionalFieldOf(
                                                        KEY_GROUP,
                                                        TradeGroup.MAIN
                                                )
                                                .forGetter(
                                                        TradePoolEntry::group
                                                ),

                                        Codec.INT
                                                .optionalFieldOf(
                                                        KEY_WEIGHT,
                                                        DEFAULT_WEIGHT
                                                )
                                                .forGetter(
                                                        TradePoolEntry::weight
                                                )
                                ).apply(
                                        instance,
                                        TradePoolEntry::new
                                )
                ).flatXmap(
                        TradePoolEntry::validate,
                        DataResult::success
                );

        public static final Codec<TradePoolEntry> CODEC =
                MAP_CODEC.codec();

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                TradePoolEntry
                > STREAM_CODEC =
                StreamCodec.of(
                        (buffer, entry) -> {
                            buffer.writeUtf(
                                    entry.group()
                                            .serializedName()
                            );

                            buffer.writeVarInt(
                                    entry.weight()
                            );
                        },
                        buffer -> {
                            String rawGroup =
                                    buffer.readUtf();

                            TradeGroup group =
                                    TradeGroup
                                            .fromSerialized(rawGroup)
                                            .result()
                                            .orElseThrow(() ->
                                                    new IllegalArgumentException(
                                                            "unknown trade group '"
                                                                    + rawGroup
                                                                    + "'"
                                                    )
                                            );

                            int weight =
                                    buffer.readVarInt();

                            DataResult<TradePoolEntry> result =
                                    validate(
                                            new TradePoolEntry(
                                                    group,
                                                    weight
                                            )
                                    );

                            String error =
                                    result.error()
                                            .map(
                                                    DataResult.Error::message
                                            )
                                            .orElse(
                                                    "invalid trade pool entry"
                                            );

                            return result.result()
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(
                                                    error
                                            )
                                    );
                        }
                );

        public TradePoolEntry {
            group = group != null
                    ? group
                    : TradeGroup.MAIN;
        }

        public static @NotNull DataResult<TradePoolEntry> validate(
                @Nullable TradePoolEntry entry
        ) {
            if (entry == null) {
                return DataResult.error(() ->
                        KEY_POOL + " is required"
                );
            }

            TradeGroup group =
                    entry.group() != null
                            ? entry.group()
                            : TradeGroup.MAIN;

            int weight =
                    entry.weight();

            if (weight < 1) {
                return DataResult.error(() ->
                        KEY_POOL
                                + "."
                                + KEY_WEIGHT
                                + " must be >= 1"
                );
            }

            if (group == TradeGroup.MAIN
                    && weight != DEFAULT_WEIGHT) {
                return DataResult.error(() ->
                        "main trades must use default pool weight"
                );
            }

            return DataResult.success(
                    new TradePoolEntry(
                            group,
                            weight
                    )
            );
        }
    }

    // -------------------------------------------------------------------------
    // Trade statistics
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Matching
    // -------------------------------------------------------------------------

    @Override
    public boolean matches(
            @NotNull DwarfTradeRecipeInput input,
            @NotNull Level level
    ) {
        if (level.isClientSide) {
            return false;
        }

        return matchesInput(input);
    }

    public boolean matchesInput(
            @NotNull DwarfTradeRecipeInput input
    ) {
        if (input.profession() != profession) {
            return false;
        }

        if (!passesLevelRequirement(
                input.merchantLevel()
        )) {
            return false;
        }

        if (!costA.test(input.costA())) {
            return false;
        }

        if (costB != null) {
            return costB.test(
                    input.costB()
            );
        }

        return input.costB().isEmpty();
    }

    // -------------------------------------------------------------------------
    // Result generation
    // -------------------------------------------------------------------------

    /**
     * Resolves the trade result using the supplied runtime context.
     *
     * A dwarf trade must resolve to exactly one non-empty stack. Results which
     * generate zero or multiple stacks fail closed.
     */
    public @NotNull ItemStack resolveResult(
            @NotNull LootContext context,
            @NotNull DwarfTradeRecipeInput input
    ) {
        if (!matchesInput(input)) {
            return ItemStack.EMPTY;
        }

        List<ItemStack> generated =
                new ArrayList<>();

        result.generate(
                context,
                input,
                stack -> {
                    if (!stack.isEmpty()) {
                        generated.add(
                                stack.copy()
                        );
                    }
                }
        );

        if (generated.size() != 1) {
            return ItemStack.EMPTY;
        }

        ItemStack resolved =
                generated.getFirst();

        if (hasCoinCost()
                && resolved.is(GOLD_COIN)) {
            return ItemStack.EMPTY;
        }

        return resolved;
    }

    /**
     * Generates the result without imposing the single-stack merchant-offer
     * restriction.
     */
    public void generateResult(
            @NotNull LootContext context,
            @NotNull DwarfTradeRecipeInput input,
            @NotNull java.util.function.Consumer<ItemStack> output
    ) {
        result.generate(
                context,
                input,
                output
        );
    }

    // -------------------------------------------------------------------------
    // Recipe metadata
    // -------------------------------------------------------------------------

    @Override
    public @NotNull RecipeSerializer<
            ? extends Recipe<DwarfTradeRecipeInput>
            > getSerializer() {
        return JolCraftRecipes
                .DWARF_TRADE_SERIALIZER
                .get();
    }

    @Override
    public @NotNull RecipeType<
            ? extends Recipe<DwarfTradeRecipeInput>
            > getType() {
        return JolCraftRecipes
                .DWARF_TRADE_TYPE
                .get();
    }

    public @NotNull TradeGroup tradeGroup() {
        return pool.group();
    }

    public boolean requiresLevel() {
        return tradeGroup()
                != TradeGroup.GLOBAL_POOL;
    }

    private boolean passesLevelRequirement(
            @Nullable DwarfMerchantData.Level inputLevel
    ) {
        if (!requiresLevel()) {
            return true;
        }

        if (merchantLevel == null
                || inputLevel == null) {
            return false;
        }

        return switch (tradeGroup()) {
            case GLOBAL_POOL ->
                    true;

            case EXACT_LEVEL_POOL ->
                    inputLevel == merchantLevel;

            case MAIN, CUMULATIVE_POOL ->
                    inputLevel.getId()
                            >= merchantLevel.getId();
        };
    }

    private boolean hasCoinCost() {
        return costA.contains(COINS_TAG)
                || (
                costB != null
                        && costB.contains(COINS_TAG)
        );
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    public static final class Serializer
            implements RecipeSerializer<DwarfTradeRecipe> {

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                ItemOutput
                > RESULT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        ItemOutput.CODEC.codec()
                );

        private static final Codec<DwarfProfession>
                PROFESSION_CODEC =
                Codec.STRING.comapFlatMap(
                        Serializer::decodeProfessionResult,
                        DwarfProfession::professionName
                );

        private static final Codec<DwarfMerchantData.Level>
                LEVEL_CODEC =
                Codec.STRING.comapFlatMap(
                        Serializer::decodeMerchantLevelResult,
                        level -> level.name()
                                .toLowerCase(Locale.ROOT)
                );

        public static final MapCodec<DwarfTradeRecipe> CODEC =
                RecordCodecBuilder
                        .<DwarfTradeRecipe>mapCodec(instance ->
                                instance.group(
                                        PROFESSION_CODEC
                                                .fieldOf(
                                                        JolCraftDictionary.PROFESSION
                                                )
                                                .forGetter(
                                                        DwarfTradeRecipe::profession
                                                ),

                                        LEVEL_CODEC
                                                .optionalFieldOf(
                                                        JolCraftDictionary.LEVEL
                                                )
                                                .forGetter(recipe ->
                                                        Optional.ofNullable(
                                                                recipe.merchantLevel()
                                                        )
                                                ),

                                        TradePoolEntry.CODEC
                                                .optionalFieldOf(
                                                        KEY_POOL,
                                                        TradePoolEntry.MAIN
                                                )
                                                .forGetter(
                                                        DwarfTradeRecipe::pool
                                                ),

                                        Codec.INT
                                                .optionalFieldOf(
                                                        JolCraftDictionary.ORDER,
                                                        0
                                                )
                                                .forGetter(
                                                        DwarfTradeRecipe::order
                                                ),

                                        TradeCost.CODEC
                                                .fieldOf(
                                                        SOURCE_COST_A
                                                )
                                                .forGetter(
                                                        DwarfTradeRecipe::costA
                                                ),

                                        TradeCost.CODEC
                                                .optionalFieldOf(
                                                        SOURCE_COST_B
                                                )
                                                .forGetter(recipe ->
                                                        Optional.ofNullable(
                                                                recipe.costB()
                                                        )
                                                ),

                                        ItemOutput.CODEC
                                                .codec()
                                                .fieldOf(
                                                        JolCraftDictionary.RESULT
                                                )
                                                .forGetter(
                                                        DwarfTradeRecipe::result
                                                ),

                                        Codec.INT
                                                .optionalFieldOf(
                                                        KEY_MAX_USES,
                                                        DEFAULT_MAX_USES
                                                )
                                                .forGetter(recipe ->
                                                        recipe.stats()
                                                                .maxUses()
                                                ),

                                        Codec.INT
                                                .optionalFieldOf(
                                                        KEY_DWARF_XP,
                                                        DEFAULT_DWARF_XP
                                                )
                                                .forGetter(recipe ->
                                                        recipe.stats()
                                                                .dwarfXp()
                                                ),

                                        Codec.FLOAT
                                                .optionalFieldOf(
                                                        KEY_PRICE_MULTIPLIER,
                                                        DEFAULT_PRICE_MULTIPLIER
                                                )
                                                .forGetter(recipe ->
                                                        recipe.stats()
                                                                .priceMultiplier()
                                                )
                                ).apply(
                                        instance,
                                        (
                                                profession,
                                                merchantLevel,
                                                pool,
                                                order,
                                                costA,
                                                costB,
                                                result,
                                                maxUses,
                                                dwarfXp,
                                                priceMultiplier
                                        ) -> new DwarfTradeRecipe(
                                                profession,
                                                merchantLevel.orElse(null),
                                                pool,
                                                order,
                                                costA,
                                                costB.orElse(null),
                                                result,
                                                new TradeStats(
                                                        maxUses,
                                                        dwarfXp,
                                                        priceMultiplier
                                                )
                                        )
                                )
                        )
                        .flatXmap(
                                Serializer::validate,
                                DataResult::success
                        );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                DwarfTradeRecipe
                > STREAM_CODEC =
                StreamCodec.of(
                        Serializer::encode,
                        Serializer::decode
                );

        @Override
        public @NotNull MapCodec<DwarfTradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                DwarfTradeRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        private static void encode(
                RegistryFriendlyByteBuf buffer,
                DwarfTradeRecipe recipe
        ) {
            buffer.writeUtf(
                    recipe.profession()
                            .professionName()
            );

            buffer.writeBoolean(
                    recipe.merchantLevel() != null
            );

            if (recipe.merchantLevel() != null) {
                buffer.writeVarInt(
                        recipe.merchantLevel()
                                .getId()
                );
            }

            TradePoolEntry.STREAM_CODEC.encode(
                    buffer,
                    recipe.pool()
            );

            buffer.writeVarInt(
                    recipe.order()
            );

            TradeCost.STREAM_CODEC.encode(
                    buffer,
                    recipe.costA()
            );

            buffer.writeBoolean(
                    recipe.costB() != null
            );

            if (recipe.costB() != null) {
                TradeCost.STREAM_CODEC.encode(
                        buffer,
                        recipe.costB()
                );
            }

            RESULT_STREAM_CODEC.encode(
                    buffer,
                    recipe.result()
            );

            TradeStats stats =
                    recipe.stats();

            buffer.writeVarInt(
                    stats.maxUses()
            );

            buffer.writeVarInt(
                    stats.dwarfXp()
            );

            buffer.writeFloat(
                    stats.priceMultiplier()
            );
        }

        private static DwarfTradeRecipe decode(
                RegistryFriendlyByteBuf buffer
        ) {
            DwarfProfession profession =
                    decodeProfession(
                            buffer.readUtf()
                    );

            DwarfMerchantData.Level merchantLevel =
                    null;

            if (buffer.readBoolean()) {
                merchantLevel =
                        decodeMerchantLevel(
                                buffer.readVarInt()
                        );
            }

            TradePoolEntry pool =
                    TradePoolEntry.STREAM_CODEC.decode(
                            buffer
                    );

            int order =
                    buffer.readVarInt();

            TradeCost costA =
                    TradeCost.STREAM_CODEC.decode(
                            buffer
                    );

            TradeCost costB =
                    buffer.readBoolean()
                            ? TradeCost.STREAM_CODEC.decode(
                            buffer
                    )
                            : null;

            ItemOutput result =
                    RESULT_STREAM_CODEC.decode(
                            buffer
                    );

            TradeStats stats =
                    new TradeStats(
                            buffer.readVarInt(),
                            buffer.readVarInt(),
                            buffer.readFloat()
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

        public static @NotNull DataResult<DwarfTradeRecipe> validate(
                @Nullable DwarfTradeRecipe recipe
        ) {
            DataResult<DwarfTradeRecipe> base =
                    RecipeValidation.validate(recipe)
                            .require(
                                    recipe != null
                                            ? recipe.profession()
                                            : null,
                                    JolCraftDictionary.PROFESSION
                            )
                            .require(
                                    recipe != null
                                            ? recipe.pool()
                                            : null,
                                    KEY_POOL
                            )
                            .require(
                                    recipe != null
                                            ? recipe.costA()
                                            : null,
                                    SOURCE_COST_A
                            )
                            .require(
                                    recipe != null
                                            ? recipe.result()
                                            : null,
                                    JolCraftDictionary.RESULT
                            )
                            .require(
                                    recipe != null
                                            ? recipe.stats()
                                            : null,
                                    "stats"
                            )
                            .done();

            if (base.error().isPresent()) {
                return base;
            }

            if (recipe == null) {
                return DataResult.error(() ->
                        "recipe is null"
                );
            }

            DataResult<TradePoolEntry> poolValidation =
                    TradePoolEntry.validate(
                            recipe.pool()
                    );

            if (poolValidation.error().isPresent()) {
                String message =
                        poolValidation.error()
                                .map(
                                        DataResult.Error::message
                                )
                                .orElse(
                                        "invalid trade pool"
                                );

                return DataResult.error(() ->
                        message
                );
            }

            if (recipe.requiresLevel()) {
                if (recipe.merchantLevel() == null) {
                    return DataResult.error(() ->
                            JolCraftDictionary.LEVEL
                                    + " is required for non-global dwarf trades"
                    );
                }
            } else if (recipe.merchantLevel() != null) {
                return DataResult.error(() ->
                        "global_pool trades must not define level"
                );
            }

            if (recipe.order() < 0) {
                return DataResult.error(() ->
                        JolCraftDictionary.ORDER
                                + " must be >= 0"
                );
            }

            DataResult<TradeCost> costAValidation =
                    TradeCost.validate(
                            recipe.costA()
                    );

            if (costAValidation.error().isPresent()) {
                String message =
                        costAValidation.error()
                                .map(
                                        DataResult.Error::message
                                )
                                .orElse(
                                        "invalid cost_a"
                                );

                return DataResult.error(() ->
                        SOURCE_COST_A
                                + ": "
                                + message
                );
            }

            if (recipe.costB() != null) {
                DataResult<TradeCost> costBValidation =
                        TradeCost.validate(
                                recipe.costB()
                        );

                if (costBValidation.error().isPresent()) {
                    String message =
                            costBValidation.error()
                                    .map(
                                            DataResult.Error::message
                                    )
                                    .orElse(
                                            "invalid cost_b"
                                    );

                    return DataResult.error(() ->
                            SOURCE_COST_B
                                    + ": "
                                    + message
                    );
                }
            }

            TradeStats stats =
                    recipe.stats();

            if (stats.maxUses() < 1) {
                return DataResult.error(() ->
                        KEY_MAX_USES
                                + " must be >= 1"
                );
            }

            if (stats.dwarfXp() < 0) {
                return DataResult.error(() ->
                        KEY_DWARF_XP
                                + " must be >= 0"
                );
            }

            if (stats.priceMultiplier() < 0.0F) {
                return DataResult.error(() ->
                        KEY_PRICE_MULTIPLIER
                                + " must be >= 0"
                );
            }

            DataResult<Void> resultValidation =
                    RecipeValidation.validateOutput(
                            recipe.result(),
                            RESULT_CONTEXT_PARAMS
                    );

            if (resultValidation.error().isPresent()) {
                String message =
                        resultValidation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid trade result");

                return DataResult.error(() ->
                        JolCraftDictionary.RESULT
                                + ": "
                                + message
                );
            }

            return DataResult.success(
                    recipe
            );
        }

        private static @NotNull DataResult<DwarfProfession>
        decodeProfessionResult(
                @Nullable String raw
        ) {
            if (raw == null) {
                return DataResult.error(() ->
                        JolCraftDictionary.PROFESSION
                                + " is required"
                );
            }

            if (!isValidProfessionSerializedName(raw)) {
                return DataResult.error(() ->
                        "unknown profession '"
                                + raw
                                + "'"
                );
            }

            return DataResult.success(
                    decodeProfession(raw)
            );
        }

        private static @NotNull DwarfProfession decodeProfession(
                @NotNull String raw
        ) {
            String normalized =
                    raw.trim();

            DwarfProfession byId =
                    DwarfProfession.byId(
                            normalized
                    );

            if (byId != DwarfProfession.NONE
                    || normalized.equalsIgnoreCase(
                    JolCraftDictionary.NONE
            )
                    || normalized.equalsIgnoreCase(
                    JolCraftDwarfIds.DWARF
            )) {
                return byId;
            }

            return DwarfProfession.fromProfessionName(
                    normalized
            );
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private static boolean isValidProfessionSerializedName(
                @Nullable String raw
        ) {
            if (raw == null || raw.isBlank()) {
                return false;
            }

            String normalized =
                    raw.trim();

            if (normalized.equalsIgnoreCase(
                    JolCraftDictionary.NONE
            )) {
                return true;
            }

            if (normalized.equalsIgnoreCase(
                    JolCraftDwarfIds.DWARF
            )) {
                return true;
            }

            DwarfProfession byName =
                    DwarfProfession.fromProfessionName(
                            normalized
                    );

            if (byName != DwarfProfession.NONE) {
                return true;
            }

            return DwarfProfession.byId(
                    normalized
            ) != DwarfProfession.NONE;
        }

        private static @NotNull DataResult<
                DwarfMerchantData.Level
                > decodeMerchantLevelResult(
                @Nullable String raw
        ) {
            if (raw == null) {
                return DataResult.error(() ->
                        JolCraftDictionary.LEVEL
                                + " is required"
                );
            }

            if (raw.isBlank()) {
                return DataResult.error(() ->
                        JolCraftDictionary.LEVEL
                                + " must not be empty"
                );
            }

            try {
                return DataResult.success(
                        DwarfMerchantData.Level.valueOf(
                                raw.trim()
                                        .toUpperCase(Locale.ROOT)
                        )
                );
            } catch (IllegalArgumentException exception) {
                return DataResult.error(() ->
                        "unknown level '"
                                + raw
                                + "'"
                );
            }
        }

        private static @NotNull DwarfMerchantData.Level
        decodeMerchantLevel(
                int id
        ) {
            if (id < DwarfMerchantData.MIN_MERCHANT_LEVEL
                    || id > DwarfMerchantData.MAX_MERCHANT_LEVEL) {
                throw new IllegalArgumentException(
                        "unknown merchant level '"
                                + id
                                + "'"
                );
            }

            return DwarfMerchantData.Level.fromId(
                    id
            );
        }
    }
}