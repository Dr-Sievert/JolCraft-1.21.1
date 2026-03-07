package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipeValidation;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomRecipe;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
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
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record BountyTaskRecipe(
        BountyType bountyType,
        BountyTier tier,
        ItemOutput bounty,
        Outputs objective,
        SoundOutput sound1,
        SoundOutput sound2
) implements CustomRecipe<BountyRecipeInput> {

    // ---------------------------------------------------------------------
    // Recipe implementation
    // ---------------------------------------------------------------------

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
        if (outParam.transforms() != ItemTransforms.EMPTY) return ItemStack.EMPTY;

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

        for (Output o :  pools.generate(ctx)) {
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

    // ---------------------------------------------------------------------
    // TASK RESULT CODEC
    // ---------------------------------------------------------------------

    private static final Codec<ItemOutput> TASK_RESULT_CODEC =
            Codec.either(
                    ItemOutput.CODEC,
                    RegistryFixedCodec.create(Registries.ITEM)
            ).xmap(
                    either -> either.map(
                            o -> o,
                            itemHolder -> ItemOutput.one(new ItemStack(itemHolder.value(), 1))
                    ),
                    out -> {
                        if (out.transforms() != ItemTransforms.EMPTY) return Either.left(out);

                        ItemSpec res = out.result();
                        var count = res.count();
                        if (count.min() != 1 || count.max() != 1) return Either.left(out);

                        var producer = res.producer();
                        Optional<Holder<Item>> holderOpt = producer.itemHolderOpt();
                        if (holderOpt.isEmpty()) return Either.left(out);

                        Item item = holderOpt.get().value();
                        if (item != JolCraftItems.BOUNTY.get() && item != JolCraftItems.BOUNTY_CRATE.get()) {
                            return Either.left(out);
                        }

                        return Either.right(holderOpt.get());
                    }
            );

    // ---------------------------------------------------------------------
    // OBJECTIVE
    // ---------------------------------------------------------------------

    private static final Codec<Outputs> OBJECTIVE_CODEC =
            Outputs.codecShorthand(OutputDispatch.CODEC);

    // ---------------------------------------------------------------------
    // SERIALIZER
    // ---------------------------------------------------------------------

    public static final class Serializer implements RecipeSerializer<BountyTaskRecipe> {

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
                        StreamCodec.of(
                                (buf, t) -> buf.writeUtf(t.getId()),
                                buf -> {
                                    BountyType t = BountyType.fromString(buf.readUtf());
                                    return t == null ? BountyType.UNKNOWN : t;
                                }
                        ), BountyTaskRecipe::bountyType,

                        StreamCodec.of(
                                (buf, t) -> buf.writeVarInt(t.getId()),
                                buf -> {
                                    BountyTier t = BountyTier.fromValue(buf.readVarInt());
                                    return t == null ? BountyTier.UNKNOWN : t;
                                }
                        ), BountyTaskRecipe::tier,

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

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    public static @NotNull DataResult<BountyTaskRecipe> validateRecipe(BountyTaskRecipe r) {

        var sound1Key = JolCraftStrings.underscored(JolCraftDictionary.SOUND, "1");
        var sound2Key = JolCraftStrings.underscored(JolCraftDictionary.SOUND, "2");

        var base = JolCraftRecipeValidation.validate(r)
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
        if (out.transforms() != ItemTransforms.EMPTY) {
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
        if (poolList.size() != 1) {
            return DataResult.error(() -> "objective must contain exactly 1 pool (got " + poolList.size() + ")");
        }

        Pool pool = poolList.getFirst();

        if (!pool.isSingleRoll()) {
            return DataResult.error(() -> "objective.pool.rolls must be exactly 1");
        }

        List<PoolEntry> entries = pool.entries();
        if (entries.isEmpty()) return DataResult.error(() -> "objective.pool.entries must not be empty");

        for (int ei = 0; ei < entries.size(); ei++) {
            PoolEntry entry = entries.get(ei);

            if (!entry.isSinglePick()) {
                int eIdx = ei;
                return DataResult.error(() ->
                        "objective.pool.entries[" + eIdx + "].pool.rolls must be exactly 1"
                );
            }

            OutputParam raw = entry.output();
            OutputParam op = OutputParam.unwrap(raw);

            var tid = op.typeId();
            if (!tid.equals(ItemOutput.TYPE_ID) && !tid.equals(EntityOutput.TYPE_ID)) {
                int eIdx = ei;
                return DataResult.error(() ->
                        "objective output must be item_output or entity_output; got " + tid +
                                " at entries[" + eIdx + "]"
                );
            }

            if (op instanceof ItemOutput io) {
                if (io.transforms() != ItemTransforms.EMPTY) {
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