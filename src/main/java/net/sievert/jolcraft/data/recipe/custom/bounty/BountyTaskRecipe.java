package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomRecipe;
import net.sievert.jolcraft.data.recipe.custom.base.RecipeValidation;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record BountyTaskRecipe(
        @NotNull DwarfProfession bountyType,
        @NotNull DwarfMerchantData.Level tier,
        @NotNull ItemOutput bounty,
        @NotNull Outputs objective,
        @NotNull SoundOutput sound1,
        @NotNull SoundOutput sound2
) implements CustomRecipe<BountyRecipeInput> {

    private static final Codec<Holder<Item>> ITEM_HOLDER_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder<Item>, T>> decode(
                com.mojang.serialization.DynamicOps<T> ops,
                T input
        ) {
            return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                ResourceLocation id = pair.getFirst();
                T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> registryOps)) {
                    return DataResult.error(() ->
                            "bounty task recipe requires RegistryOps for '" + Registries.ITEM.location() + "'"
                    );
                }

                var lookupOpt = registryOps.lookupProvider.lookup(Registries.ITEM);
                if (lookupOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" + Registries.ITEM.location() + "'"
                    );
                }

                ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
                var holderOpt = lookupOpt.get().getter().get(key);

                return holderOpt.<DataResult<Pair<Holder<Item>, T>>>map(itemReference ->
                        DataResult.success(Pair.of(itemReference, rest))).orElseGet(() -> DataResult.error(() -> "unknown item '" + id + "'"));

            });
        }

        @Override
        public <T> DataResult<T> encode(
                Holder<Item> input,
                com.mojang.serialization.DynamicOps<T> ops,
                T prefix
        ) {
            if (input == null) {
                return DataResult.error(() -> "item holder cannot be null");
            }

            return input.unwrapKey()
                    .map(ResourceKey::location)
                    .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                    .orElseGet(() -> DataResult.error(() -> "unkeyed item holder"));
        }
    };

    @Override
    public boolean matches(@NotNull BountyRecipeInput in, Level level) {
        if (level.isClientSide) return false;

        ItemStack base = in.redeemStack();
        if (base.isEmpty()) return false;

        if (!base.is(JolCraftItems.BOUNTY.get()) && !base.is(JolCraftItems.BOUNTY_CRATE.get())) {
            return false;
        }

        return in.type() == bountyType && in.tier() == tier;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BountyRecipeInput in, HolderLookup.@NotNull Provider registries) {
        ItemOutput outParam = bounty;
        if (!hasNoTransforms(outParam.transforms())) return ItemStack.EMPTY;

        ItemSpec spec = outParam.result();
        var producer = spec.producer();

        Optional<Holder<Item>> holderOpt = producer.itemHolderOpt();
        if (holderOpt.isEmpty()) return ItemStack.EMPTY;

        Item item = holderOpt.get().value();
        Item bountyItem = JolCraftItems.BOUNTY.get();
        Item crateItem = JolCraftItems.BOUNTY_CRATE.get();
        if (item != bountyItem && item != crateItem) return ItemStack.EMPTY;

        WorldContext ctx = in.ctx();
        Pools pools = objective.pools();

        BountyData.BountyObjective resolved = null;

        for (Output o : pools.generate(ctx)) {
            if (o instanceof Output.Items items) {
                var stacks = items.stacksSafe();
                if (!stacks.isEmpty()) {
                    ItemStack s = stacks.getFirst();
                    if (!s.isEmpty()) {
                        resolved = new BountyData.BountyObjective.ItemObjective(
                                s.getItemHolder(),
                                Math.max(1, s.getCount())
                        );
                        break;
                    }
                }
            } else if (o instanceof Output.Entities ents) {
                var list = ents.entitiesSafe();
                if (!list.isEmpty()) {
                    Output.EntitySpec es = list.getFirst();
                    if (es.type() != null) {
                        resolved = new BountyData.BountyObjective.EntityObjective(
                                es.type(),
                                Math.max(1, es.count())
                        );
                        break;
                    }
                }
            }
        }

        if (resolved == null) return ItemStack.EMPTY;

        ItemStack out = new ItemStack(item);
        BountyRecipe.setType(out, bountyType);
        BountyRecipe.setTier(out, tier);
        out.set(JolCraftDataComponents.BOUNTY_DATA.get(), new BountyData(resolved));
        return out;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<BountyRecipeInput>> getSerializer() {
        return JolCraftRecipes.BOUNTY_TASK_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<BountyRecipeInput>> getType() {
        return JolCraftRecipes.BOUNTY_TASK_TYPE.get();
    }

    public static boolean isTaskBountyStack(ItemStack stack) {
        if (!BountyRecipe.isValidBountyStack(stack)) return false;
        if (stack.has(JolCraftDataComponents.BOUNTY_DATA.get())) return false;
        if (stack.has(JolCraftDataComponents.BOUNTY_FILL.get())) return false;
        return !stack.has(JolCraftDataComponents.BOUNTY_COMPLETE.get());
    }

    private static final Codec<ItemOutput> TASK_RESULT_CODEC =
            Codec.either(
                    ItemOutput.CODEC,
                    ITEM_HOLDER_CODEC
            ).comapFlatMap(
                    either -> either.map(
                            DataResult::success,
                            itemHolder -> ItemOutput.one(new ItemStack(itemHolder.value(), 1))
                    ),
                    BountyTaskRecipe::encodeTaskResult
            );

    private static @NotNull Either<ItemOutput, Holder<Item>> encodeTaskResult(@NotNull ItemOutput out) {
        if (!hasNoTransforms(out.transforms())) {
            return Either.left(out);
        }

        ItemSpec res = out.result();

        var count = res.count();
        if (count.min() != 1 || count.max() != 1) {
            return Either.left(out);
        }

        var producer = res.producer();
        Optional<Holder<Item>> holderOpt = producer.itemHolderOpt();
        if (holderOpt.isEmpty()) {
            return Either.left(out);
        }

        Item item = holderOpt.get().value();
        if (item != JolCraftItems.BOUNTY.get() && item != JolCraftItems.BOUNTY_CRATE.get()) {
            return Either.left(out);
        }

        return Either.right(holderOpt.get());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasNoTransforms(@NotNull ItemTransforms transforms) {
        return transforms.enchantments().isEmpty() && transforms.components().isEmpty();
    }

    private static final Codec<Outputs> OBJECTIVE_CODEC =
            Outputs.codecShorthand(OutputParam.CODEC);

    public static final class Serializer implements RecipeSerializer<BountyTaskRecipe> {

        private static final StreamCodec<RegistryFriendlyByteBuf, DwarfProfession> BOUNTY_TYPE_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> buf.writeUtf(value.professionName()),
                        buf -> {
                            String raw = buf.readUtf();
                            DwarfProfession type = BountyRecipe.parseType(raw);
                            if (type == null) {
                                throw new IllegalArgumentException("unknown bounty type '" + raw + "'");
                            }
                            return type;
                        }
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, DwarfMerchantData.Level> BOUNTY_TIER_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> buf.writeVarInt(value.getId()),
                        buf -> {
                            int raw = buf.readVarInt();
                            DwarfMerchantData.Level tier = BountyRecipe.parseTier(raw);
                            if (tier == null) {
                                throw new IllegalArgumentException("unknown bounty tier id " + raw);
                            }
                            return tier;
                        }
                );

        public static final MapCodec<BountyTaskRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<BountyTaskRecipe> inst) -> inst.group(
                        BountyRecipe.BOUNTY_TYPE_CODEC
                                .fieldOf(BountyRecipe.TYPE_KEY)
                                .forGetter(BountyTaskRecipe::bountyType),

                        BountyRecipe.BOUNTY_TIER_CODEC
                                .fieldOf(BountyRecipe.TIER_KEY)
                                .forGetter(BountyTaskRecipe::tier),

                        TASK_RESULT_CODEC
                                .fieldOf(JolCraftDictionary.RESULT)
                                .forGetter(BountyTaskRecipe::bounty),

                        OBJECTIVE_CODEC
                                .fieldOf(JolCraftDictionary.OBJECTIVE)
                                .forGetter(BountyTaskRecipe::objective),

                        SoundOutput.CODEC
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.SOUND, "1"))
                                .forGetter(BountyTaskRecipe::sound1),

                        SoundOutput.CODEC
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.SOUND, "2"))
                                .forGetter(BountyTaskRecipe::sound2)
                ).apply(inst, BountyTaskRecipe::new)).validate(BountyTaskRecipe::validateRecipe);

        public static final StreamCodec<RegistryFriendlyByteBuf, BountyTaskRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        BOUNTY_TYPE_STREAM_CODEC, BountyTaskRecipe::bountyType,
                        BOUNTY_TIER_STREAM_CODEC, BountyTaskRecipe::tier,
                        ItemOutput.STREAM_CODEC, BountyTaskRecipe::bounty,
                        Outputs.STREAM_CODEC, BountyTaskRecipe::objective,
                        SoundOutput.STREAM_CODEC, BountyTaskRecipe::sound1,
                        SoundOutput.STREAM_CODEC, BountyTaskRecipe::sound2,
                        BountyTaskRecipe::new
                );

        @Override
        public @NotNull MapCodec<BountyTaskRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BountyTaskRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static @NotNull DataResult<BountyTaskRecipe> validateRecipe(BountyTaskRecipe r) {
        var sound1Key = JolCraftStrings.underscored(JolCraftDictionary.SOUND, "1");
        var sound2Key = JolCraftStrings.underscored(JolCraftDictionary.SOUND, "2");

        var base = RecipeValidation.validate(r)
                .require(r.bountyType(), BountyRecipe.TYPE_KEY)
                .require(r.tier(), BountyRecipe.TIER_KEY)
                .requireValid(r.bounty(), JolCraftParameterIds.RESULT)
                .requireValid(r.objective(), JolCraftDictionary.OBJECTIVE)
                .requireValid(r.sound1(), sound1Key)
                .requireValid(r.sound2(), sound2Key)
                .done();

        if (base.error().isPresent()) return base;

        var infoRes = BountyRecipe.validateInfo(r.bountyType(), r.tier());
        if (infoRes.error().isPresent()) {
            String msg = infoRes.error().map(DataResult.Error::message).orElse("invalid bounty");
            return DataResult.error(() -> msg);
        }

        ItemOutput out = r.bounty();
        if (!hasNoTransforms(out.transforms())) {
            return DataResult.error(() -> "result.transforms must be empty for bounty tasks");
        }

        if (out.transforms().requiresInputSource()) {
            return DataResult.error(() ->
                    "this recipe type does not support input-sourced component transforms");
        }

        ItemSpec res = out.result();
        var count = res.count();
        if (count.min() != 1 || count.max() != 1) {
            return DataResult.error(() -> "result.result.count must be exactly 1 for bounty tasks");
        }

        var producer = res.producer();
        Optional<Holder<Item>> itemHolderOpt = producer.itemHolderOpt();
        if (itemHolderOpt.isEmpty()) {
            return DataResult.error(() -> "result.result.producer must be a direct item for bounty tasks");
        }

        Item item = itemHolderOpt.get().value();
        if (item == Items.AIR) return DataResult.error(() -> "result item must not be air");

        if (item != JolCraftItems.BOUNTY.get() && item != JolCraftItems.BOUNTY_CRATE.get()) {
            return DataResult.error(() -> "result must be jolcraft:bounty or jolcraft:bounty_crate");
        }

        Outputs obj = r.objective();
        Pools pools = obj.pools();

        List<Pool> poolList = pools.pools();
        Pool pool = poolList.getFirst();

        if (!pool.isSingleRoll()) {
            return DataResult.error(() -> "objective.pool.rolls must be exactly 1");
        }

        List<PoolEntry> entries = pool.entries();
        if (entries.isEmpty()) {
            return DataResult.error(() -> "objective.pool.entries must not be empty");
        }

        for (int ei = 0; ei < entries.size(); ei++) {
            PoolEntry entry = entries.get(ei);

            if (!entry.isSinglePick()) {
                int eIdx = ei;
                return DataResult.error(() ->
                        "objective.pool.entries[" + eIdx + "].pool.rolls must be exactly 1"
                );
            }

            OutputParam op = entry.output();

            var tid = op.typeId();
            if (!tid.equals(ItemOutput.TYPE_ID) && !tid.equals(EntityOutput.TYPE_ID)) {
                int eIdx = ei;
                return DataResult.error(() ->
                        "objective output must be item_output or entity_output; got " + tid +
                                " at entries[" + eIdx + "]"
                );
            }

            if (op instanceof ItemOutput io) {
                if (!hasNoTransforms(io.transforms())) {
                    int eIdx = ei;
                    return DataResult.error(() ->
                            "objective item_output.transforms must be empty (entries[" + eIdx + "])"
                    );
                }
            }
        }

        return DataResult.success(r);
    }
}