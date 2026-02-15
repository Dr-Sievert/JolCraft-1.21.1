package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
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
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyRewardRecipe implements Recipe<BountyRecipeInput> {

    public enum RewardPool {
        MAIN,
        BONUS
    }

    // =====================================================================
    // Amount (int OR {min_count,max_count})
    // =====================================================================

    public record Amount(int min, int max) {

        public static Amount fixed(int value) {
            return new Amount(value, value);
        }

        public int roll(RandomSource random) {
            return (min == max) ? min : (min + random.nextInt(max - min + 1));
        }

        private static final Codec<Amount> OBJECT_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MIN, JolCraftDictionary.COUNT)).forGetter(Amount::min),
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftDictionary.COUNT)).forGetter(Amount::max)
                ).apply(inst, Amount::new));

        public static final Codec<Amount> CODEC =
                Codec.either(Codec.INT, OBJECT_CODEC).xmap(
                        e -> e.map(Amount::fixed, a -> a),
                        a -> (a.min == a.max) ? Either.left(a.min) : Either.right(a)
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, Amount> STREAM_CODEC =
                StreamCodec.of(
                        (buf, a) -> {
                            buf.writeVarInt(a.min);
                            buf.writeVarInt(a.max);
                        },
                        buf -> new Amount(buf.readVarInt(), buf.readVarInt())
                );
    }

    // =====================================================================
    // Redeem ingredient (item OR tag)
    // =====================================================================

    @SuppressWarnings("deprecation")
    public sealed interface RedeemIngredient permits RedeemIngredient.ItemIngredient, RedeemIngredient.TagIngredient {

        record ItemIngredient(Holder<Item> item) implements RedeemIngredient {
            @Override
            public boolean test(ItemStack stack) {
                return stack.is(item);
            }
        }

        record TagIngredient(TagKey<Item> tag) implements RedeemIngredient {
            @Override
            public boolean test(ItemStack stack) {
                return stack.is(tag);
            }
        }

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

        Codec<RedeemIngredient> CODEC =
                RAW_CODEC.codec().flatXmap(
                        raw -> {
                            boolean hasItem = raw.item().isPresent();
                            boolean hasTag = raw.tag().isPresent();

                            if (hasItem == hasTag) {
                                return DataResult.error(() -> "ingredient.ingredient must define exactly one of 'item' or 'tag'");
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
                            return DataResult.error(() -> "Unknown RedeemIngredient variant");
                        }
                );

        boolean test(ItemStack stack);

        StreamCodec<RegistryFriendlyByteBuf, RedeemIngredient> STREAM_CODEC =
                StreamCodec.of(
                        (buf, ing) -> {
                            if (ing instanceof TagIngredient(TagKey<Item> tag)) {
                                buf.writeBoolean(true);
                                buf.writeResourceLocation(tag.location());
                                return;
                            }

                            buf.writeBoolean(false);

                            ItemIngredient ii = (ItemIngredient) ing;
                            ResourceLocation id = ii.item().unwrapKey()
                                    .orElseThrow(() -> new IllegalStateException("Unkeyed item holder in RedeemIngredient"))
                                    .location();

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
                                throw new IllegalStateException("Unknown item id in RedeemIngredient: " + id);
                            }

                            return new ItemIngredient(value.builtInRegistryHolder());
                        }
                );
    }

    // =====================================================================
    // Reward ingredient (item OR tag; tag rolls random item from tag)
    // =====================================================================

    @SuppressWarnings("deprecation")
    public sealed interface RewardIngredient permits RewardIngredient.ItemIngredient, RewardIngredient.TagIngredient {

        record ItemIngredient(Holder<Item> item) implements RewardIngredient {}
        record TagIngredient(TagKey<Item> tag) implements RewardIngredient {}

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

        Codec<RewardIngredient> CODEC =
                RAW_CODEC.codec().flatXmap(
                        raw -> {
                            boolean hasItem = raw.item().isPresent();
                            boolean hasTag = raw.tag().isPresent();

                            if (hasItem == hasTag) {
                                return DataResult.error(() -> "result.ingredient must define exactly one of 'item' or 'tag'");
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
                            return DataResult.error(() -> "Unknown RewardIngredient variant");
                        }
                );

        default ItemStack preview(HolderLookup.Provider registries, int count) {
            if (this instanceof ItemIngredient(Holder<Item> item)) {
                return new ItemStack(item.value(), count);
            }

            TagIngredient ti = (TagIngredient) this;
            var itemRegistry = registries.lookupOrThrow(Registries.ITEM);
            Optional<? extends net.minecraft.core.HolderSet.Named<Item>> holdersOpt = itemRegistry.get(ti.tag());
            if (holdersOpt.isEmpty()) return ItemStack.EMPTY;

            var holders = holdersOpt.get();
            if (holders.size() <= 0) return ItemStack.EMPTY;

            Holder<Item> first = holders.get(0);
            return new ItemStack(first.value(), count);
        }

        default ItemStack roll(HolderLookup.Provider registries, RandomSource random, int count) {
            if (this instanceof ItemIngredient(Holder<Item> item)) {
                return new ItemStack(item.value(), count);
            }

            TagIngredient ti = (TagIngredient) this;
            var itemRegistry = registries.lookupOrThrow(Registries.ITEM);
            Optional<? extends net.minecraft.core.HolderSet.Named<Item>> holdersOpt = itemRegistry.get(ti.tag());
            if (holdersOpt.isEmpty()) return ItemStack.EMPTY;

            var holders = holdersOpt.get();
            int size = holders.size();
            if (size <= 0) return ItemStack.EMPTY;

            Holder<Item> chosen = holders.get(random.nextInt(size));
            return new ItemStack(chosen.value(), count);
        }

        StreamCodec<RegistryFriendlyByteBuf, RewardIngredient> STREAM_CODEC =
                StreamCodec.of(
                        (buf, ing) -> {
                            if (ing instanceof TagIngredient(TagKey<Item> tag)) {
                                buf.writeBoolean(true);
                                buf.writeResourceLocation(tag.location());
                                return;
                            }

                            buf.writeBoolean(false);

                            ItemIngredient ii = (ItemIngredient) ing;
                            ResourceLocation id = ii.item().unwrapKey()
                                    .orElseThrow(() -> new IllegalStateException("Unkeyed item holder in RewardIngredient"))
                                    .location();

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
                                throw new IllegalStateException("Unknown item id in RewardIngredient: " + id);
                            }

                            return new ItemIngredient(value.builtInRegistryHolder());
                        }
                );
    }

    // =====================================================================
    // Trade-style wrappers (amount lives with ingredient/result)
    // =====================================================================

    /**
     * JSON:
     * {
     *   "amount": 1,
     *   "ingredient": { "item": "..."} OR { "tag": "..." }
     * }
     * NOTE: for bounty rewards, ingredient.amount must be FIXED (no range).
     */
    public record RedeemCost(RedeemIngredient ingredient, Amount amount) {

        public boolean test(ItemStack stack) {
            if (!ingredient.test(stack)) return false;
            return stack.getCount() >= amount.min();
        }

        public static final Codec<RedeemCost> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        RedeemIngredient.CODEC
                                .fieldOf(JolCraftDictionary.INGREDIENT)
                                .forGetter(RedeemCost::ingredient),
                        Amount.CODEC
                                .fieldOf(JolCraftDictionary.AMOUNT)
                                .forGetter(RedeemCost::amount)
                ).apply(inst, RedeemCost::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RedeemCost> STREAM_CODEC =
                StreamCodec.of(
                        (buf, c) -> {
                            RedeemIngredient.STREAM_CODEC.encode(buf, c.ingredient);
                            Amount.STREAM_CODEC.encode(buf, c.amount);
                        },
                        buf -> new RedeemCost(
                                RedeemIngredient.STREAM_CODEC.decode(buf),
                                Amount.STREAM_CODEC.decode(buf)
                        )
                );
    }

    /**
     * JSON:
     * {
     *   "amount": 4 OR { "min_count":4, "max_count":6 },
     *   "ingredient": { "item": "..."} OR { "tag": "..." }
     * }
     */
    public record RewardResult(RewardIngredient ingredient, Amount amount) {

        public ItemStack preview(HolderLookup.Provider registries) {
            return ingredient.preview(registries, amount.min());
        }

        public ItemStack roll(HolderLookup.Provider registries, RandomSource random) {
            int count = amount.roll(random);
            return ingredient.roll(registries, random, count);
        }

        public static final Codec<RewardResult> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        RewardIngredient.CODEC
                                .fieldOf(JolCraftDictionary.INGREDIENT)
                                .forGetter(RewardResult::ingredient),
                        Amount.CODEC
                                .fieldOf(JolCraftDictionary.AMOUNT)
                                .forGetter(RewardResult::amount)
                ).apply(inst, RewardResult::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RewardResult> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            RewardIngredient.STREAM_CODEC.encode(buf, r.ingredient);
                            Amount.STREAM_CODEC.encode(buf, r.amount);
                        },
                        buf -> new RewardResult(
                                RewardIngredient.STREAM_CODEC.decode(buf),
                                Amount.STREAM_CODEC.decode(buf)
                        )
                );
    }

    // =====================================================================
    // Recipe fields
    // =====================================================================

    private final RedeemCost ingredient;
    private final BountyType bountyType;
    private final int tier; // 1..5
    private final RewardPool pool;
    private final int weight;
    private final RewardResult result;

    public BountyRewardRecipe(
            RedeemCost ingredient,
            BountyType bountyType,
            int tier,
            RewardPool pool,
            int weight,
            RewardResult result
    ) {
        this.ingredient = ingredient;
        this.bountyType = bountyType;
        this.tier = tier;
        this.pool = pool;
        this.weight = weight;
        this.result = result;
    }

    public RedeemCost ingredient() { return ingredient; }
    public BountyType bountyType() { return bountyType; }
    public int tier() { return tier; }
    public RewardPool pool() { return pool; }
    public int weight() { return weight; }
    public RewardResult result() { return result; }

    public ItemStack preview(HolderLookup.Provider registries) {
        return result.preview(registries);
    }

    public ItemStack rollResult(HolderLookup.Provider registries, RandomSource random) {
        return result.roll(registries, random);
    }

    // =====================================================================
    // Recipe implementation
    // =====================================================================

    @Override
    public boolean matches(BountyRecipeInput in, Level level) {
        if (level.isClientSide) return false;

        ItemStack redeem = in.redeemStack();
        if (redeem.isEmpty()) return false;

        if (!ingredient.test(redeem)) return false;
        if (!isCompletedBountyStack(redeem)) return false;

        return in.type() == bountyType
                && in.tier().getValue() == tier;
    }

    @Override
    public ItemStack assemble(BountyRecipeInput in, HolderLookup.Provider registries) {
        return preview(registries);
    }

    @Override
    public RecipeSerializer<? extends Recipe<BountyRecipeInput>> getSerializer() {
        return JolCraftRecipes.BOUNTY_REWARD_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<BountyRecipeInput>> getType() {
        return JolCraftRecipes.BOUNTY_REWARD_TYPE.get();
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isCompletedBountyStack(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!stack.has(JolCraftDataComponents.BOUNTY_TYPE.get())) return false;
        if (!stack.has(JolCraftDataComponents.BOUNTY_TIER.get())) return false;
        if (!stack.has(JolCraftDataComponents.BOUNTY_DATA.get())) return false;

        BountyType type = BountyRecipe.readType(stack);
        if (type == BountyType.UNKNOWN) return false;

        BountyTier tier = BountyRecipe.readTier(stack);
        if (tier == BountyTier.UNKNOWN) return false;

        return Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()));
    }

    // =====================================================================
    // Serializer (CODEC + STREAM_CODEC)
    // =====================================================================

    public static final class Serializer implements RecipeSerializer<BountyRewardRecipe> {

        private static final Codec<RewardPool> POOL_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            String key = s.trim().toUpperCase().replace('-', '_').replace(' ', '_');
                            try {
                                return DataResult.success(RewardPool.valueOf(key));
                            } catch (IllegalArgumentException ex) {
                                return DataResult.error(() -> "Unknown pool '" + s + "'. Valid: main, bonus");
                            }
                        },
                        p -> p.name().toLowerCase()
                );

        public static final MapCodec<BountyRewardRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<BountyRewardRecipe> inst) -> inst.group(
                        RedeemCost.CODEC
                                .fieldOf(JolCraftDictionary.INGREDIENT)
                                .forGetter(BountyRewardRecipe::ingredient),

                        BountyRecipe.BOUNTY_TYPE_CODEC
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.BOUNTY, JolCraftDictionary.TYPE))
                                .forGetter(BountyRewardRecipe::bountyType),

                        Codec.INT
                                .fieldOf(JolCraftDictionary.TIER)
                                .forGetter(BountyRewardRecipe::tier),

                        POOL_CODEC
                                .optionalFieldOf(JolCraftDictionary.POOL, RewardPool.MAIN)
                                .forGetter(BountyRewardRecipe::pool),

                        Codec.INT
                                .fieldOf(JolCraftDictionary.WEIGHT)
                                .forGetter(BountyRewardRecipe::weight),

                        RewardResult.CODEC
                                .fieldOf(JolCraftDictionary.RESULT)
                                .forGetter(BountyRewardRecipe::result)
                ).apply(inst, BountyRewardRecipe::new)).validate(Serializer::validate);

        public static final StreamCodec<RegistryFriendlyByteBuf, BountyRewardRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<BountyRewardRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BountyRewardRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static DataResult<BountyRewardRecipe> validate(BountyRewardRecipe r) {
            if (r.ingredient == null) {
                return DataResult.error(() -> "ingredient is required");
            }
            if (r.ingredient.ingredient() instanceof RedeemIngredient.ItemIngredient(Holder<Item> item) && item.value() == Items.AIR) {
                return DataResult.error(() -> "ingredient.ingredient.item must not be air");
            }

            if (r.ingredient.amount().min() < 1 || r.ingredient.amount().max() < r.ingredient.amount().min()) {
                return DataResult.error(() -> "ingredient.amount must be >= 1 and max>=min");
            }
            if (r.ingredient.amount().min() != r.ingredient.amount().max()) {
                return DataResult.error(() -> "ingredient.amount must be a fixed int (no range) for bounty rewards");
            }

            var typeOk = BountyRecipe.validateType(r.bountyType);
            var typeError = typeOk.error();
            if (typeError.isPresent()) {
                return DataResult.error(typeError.get()::message);
            }

            var tierOk = BountyRecipe.validateTier(r.tier);
            var tierError = tierOk.error();
            if (tierError.isPresent()) {
                return DataResult.error(tierError.get()::message);
            }

            if (r.pool == null) {
                return DataResult.error(() -> "pool must be set (main or bonus)");
            }

            if (r.weight < 1) {
                return DataResult.error(() -> "weight must be >= 1 (got " + r.weight + ")");
            }

            if (r.result == null) {
                return DataResult.error(() -> "result is required");
            }
            if (r.result.ingredient() instanceof RewardIngredient.ItemIngredient(Holder<Item> item) && item.value() == Items.AIR) {
                return DataResult.error(() -> "result.ingredient.item must not be air");
            }

            if (r.result.amount().min() < 1 || r.result.amount().max() < r.result.amount().min()) {
                return DataResult.error(() -> "result.amount must be >= 1 and max>=min");
            }

            return DataResult.success(r);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BountyRewardRecipe r) {
            buf.writeUtf(r.bountyType.getId());
            buf.writeVarInt(r.tier);
            buf.writeEnum(r.pool);
            buf.writeVarInt(r.weight);

            RedeemCost.STREAM_CODEC.encode(buf, r.ingredient);
            RewardResult.STREAM_CODEC.encode(buf, r.result);
        }

        private static BountyRewardRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            BountyType type = BountyType.fromString(buf.readUtf());
            int tier = buf.readVarInt();
            RewardPool pool = buf.readEnum(RewardPool.class);
            int weight = buf.readVarInt();

            RedeemCost ingredient = RedeemCost.STREAM_CODEC.decode(buf);
            RewardResult result = RewardResult.STREAM_CODEC.decode(buf);

            if (type == null) type = BountyType.UNKNOWN;

            return new BountyRewardRecipe(
                    ingredient,
                    type,
                    tier,
                    pool,
                    weight,
                    result
            );
        }
    }
}