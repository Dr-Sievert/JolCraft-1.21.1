package net.sievert.jolcraft.world.recipe.param.output.custom.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record ItemSpec(
        @NotNull ItemProducer producer,
        @NotNull IntRange count
) implements SelfValidating<ItemSpec>, RegistryIntrospectionSource {

    private record CanonicalRaw(
            Optional<Holder<Item>> item,
            Optional<TagKey<Item>> tag,
            Optional<ItemProducer.MapData> map,
            IntRange count
    ) {
        private CanonicalRaw {
            item = item != null ? item : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            map = map != null ? map : Optional.empty();
            count = count != null ? count : IntRange.ONE;
        }
    }

    private record VerboseRaw(
            ItemProducer producer,
            IntRange count
    ) {
        private VerboseRaw {
            count = count != null ? count : IntRange.ONE;
        }
    }

    private static final Codec<CanonicalRaw> CANONICAL_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ItemProducer.ITEM_HOLDER_CODEC
                            .optionalFieldOf(ItemProducer.ITEM)
                            .forGetter(CanonicalRaw::item),

                    ItemProducer.ITEM_TAG_CODEC
                            .optionalFieldOf(ItemProducer.TAG)
                            .forGetter(CanonicalRaw::tag),

                    ItemProducer.MapData.CODEC
                            .optionalFieldOf(ItemProducer.MAP)
                            .forGetter(CanonicalRaw::map),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(CanonicalRaw::count)
            ).apply(instance, CanonicalRaw::new));

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ItemProducer.CODEC
                            .fieldOf(JolCraftParameterIds.PRODUCER)
                            .forGetter(VerboseRaw::producer),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(VerboseRaw::count)
            ).apply(instance, VerboseRaw::new));

    public static final Codec<ItemSpec> CODEC =
            ParamCodecContract.create(
                    Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC),
                    ItemSpec::fromRaw,
                    ItemSpec::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSpec> STREAM_CODEC =
            StreamCodec.composite(
                    ItemProducer.STREAM_CODEC, ItemSpec::producer,
                    IntRange.STREAM_CODEC, ItemSpec::count,
                    ItemSpec::new
            );

    public ItemSpec {
        Objects.requireNonNull(producer, JolCraftParameterIds.PRODUCER);
    }

    public static @NotNull DataResult<ItemSpec> fromSelection(
            @NotNull Optional<Holder<Item>> item,
            @NotNull Optional<TagKey<Item>> tag,
            @NotNull Optional<ItemProducer.MapData> map,
            @NotNull IntRange count
    ) {
        return ItemProducer.fromSelection(item, tag, map)
                .map(producer -> new ItemSpec(producer, count));
    }

    private static @NotNull DataResult<ItemSpec> fromRaw(
            @NotNull Either<CanonicalRaw, VerboseRaw> raw
    ) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();
            return fromSelection(
                    canonical.item(),
                    canonical.tag(),
                    canonical.map(),
                    canonical.count()
            );
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        if (verbose.producer() == null) {
            return DataResult.error(() -> JolCraftParameterIds.PRODUCER + " is required");
        }

        return DataResult.success(new ItemSpec(
                verbose.producer(),
                verbose.count()
        ));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull ItemSpec spec) {
        return Either.left(new CanonicalRaw(
                spec.producer().itemHolderOpt(),
                spec.producer().tagOpt(),
                spec.producer().mapDataOpt(),
                spec.count()
        ));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return producer.introspections();
    }

    @Override
    public @NotNull DataResult<ItemSpec> validate() {
        DataResult<ItemProducer> pv = producer.validate();
        if (pv.error().isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.PRODUCER + " invalid: " +
                            pv.error().map(DataResult.Error::message).orElse("")
            );
        }

        DataResult<IntRange> cv = IntRange.validateRange(count);
        if (cv.error().isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + " invalid: " +
                            cv.error().map(DataResult.Error::message).orElse("")
            );
        }

        return DataResult.success(this);
    }

    public @NotNull ItemStack create(@NotNull WorldContext ctx) {
        ItemStack stack = producer.create(ctx);
        if (stack.isEmpty()) {
            return stack;
        }

        int rolled = count.roll(ctx.random());
        if (rolled <= 0) {
            return ItemStack.EMPTY;
        }

        int max = Math.max(1, stack.getMaxStackSize());
        stack.setCount(Math.min(rolled, max));
        return stack;
    }

    public static @NotNull DataResult<ItemSpec> of(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return DataResult.error(() -> "stack must not be empty");
        }

        return DataResult.success(new ItemSpec(
                ItemProducer.holder(stack.getItemHolder()),
                IntRange.ONE
        ));
    }
}