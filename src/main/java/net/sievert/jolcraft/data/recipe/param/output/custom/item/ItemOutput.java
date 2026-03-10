package net.sievert.jolcraft.data.recipe.param.output.custom.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ItemOutput(
        @NotNull ItemSpec result,
        @NotNull ItemTransforms transforms
) implements OutputParam, ResolvedOutputParam, SelfValidating<ItemOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftDictionary.OUTPUT));

    public static final byte DISC = 2;

    private record CanonicalRaw(
            Optional<Holder<Item>> item,
            Optional<TagKey<Item>> tag,
            Optional<ItemProducer.MapData> map,
            IntRange count,
            ItemTransforms transforms
    ) {
        private CanonicalRaw {
            item = item != null ? item : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            map = map != null ? map : Optional.empty();
            count = count != null ? count : IntRange.ONE;
            transforms = transforms != null ? transforms : ItemTransforms.EMPTY;
        }
    }

    private record VerboseRaw(
            ItemSpec result,
            ItemTransforms transforms
    ) {
        private VerboseRaw {
            transforms = transforms != null ? transforms : ItemTransforms.EMPTY;
        }
    }

    public static @NotNull DataResult<ItemOutput> one(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return DataResult.error(() -> "stack must not be empty");
        }

        return ItemSpec.of(stack).map(spec ->
                new ItemOutput(spec, ItemTransforms.EMPTY)
        );
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
                            .forGetter(CanonicalRaw::count),

                    ItemTransforms.CODEC
                            .optionalFieldOf(JolCraftParameterIds.TRANSFORMS, ItemTransforms.EMPTY)
                            .forGetter(CanonicalRaw::transforms)
            ).apply(instance, CanonicalRaw::new));

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ItemSpec.CODEC
                            .fieldOf(JolCraftParameterIds.RESULT)
                            .forGetter(VerboseRaw::result),

                    ItemTransforms.CODEC
                            .optionalFieldOf(JolCraftParameterIds.TRANSFORMS, ItemTransforms.EMPTY)
                            .forGetter(VerboseRaw::transforms)
            ).apply(instance, VerboseRaw::new));

    private static final Codec<Either<CanonicalRaw, VerboseRaw>> RAW_CODEC =
            Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC);

    public static final Codec<ItemOutput> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    ItemOutput::fromRaw,
                    ItemOutput::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> STREAM_CODEC =
            StreamCodec.composite(
                    ItemSpec.STREAM_CODEC, ItemOutput::result,
                    ItemTransforms.STREAM_CODEC, ItemOutput::transforms,
                    ItemOutput::new
            );

    public ItemOutput {
        Objects.requireNonNull(result, JolCraftParameterIds.RESULT);
    }

    public static final ParamTypeDef<OutputParam> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static @NotNull DataResult<ItemOutput> fromRaw(@NotNull Either<CanonicalRaw, VerboseRaw> raw) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();

            return ItemSpec.fromSelection(
                    canonical.item(),
                    canonical.tag(),
                    canonical.map(),
                    canonical.count()
            ).map(spec -> new ItemOutput(
                    spec,
                    canonical.transforms()
            ));
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        if (verbose.result() == null) {
            return DataResult.error(() -> JolCraftParameterIds.RESULT + " is required");
        }

        return DataResult.success(new ItemOutput(
                verbose.result(),
                verbose.transforms()
        ));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull ItemOutput output) {
        ItemSpec spec = output.result();
        ItemProducer producer = spec.producer();

        return Either.left(new CanonicalRaw(
                producer.itemHolderOpt(),
                producer.tagOpt(),
                producer.mapDataOpt(),
                spec.count(),
                output.transforms()
        ));
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return generateResolved(ctx, null);
    }

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        ItemStack stack = result.create(ctx);
        if (stack.isEmpty()) return List.of();

        if (resolver != null) {
            transforms.apply(ctx, resolver, stack);
        } else {
            transforms.apply(ctx, stack);
        }

        if (stack.isEmpty()) return List.of();

        return List.of(new Output.Items(List.of(stack)));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(List.of(
                result,
                transforms
        ));
    }

    @Override
    public @NotNull DataResult<ItemOutput> validate() {
        DataResult<ItemSpec> resultValidation = result.validate();
        if (resultValidation.error().isPresent()) {
            return SelfValidating.invalid(
                    JolCraftParameterIds.RESULT + " invalid: " +
                            resultValidation.error().map(DataResult.Error::message).orElse("")
            );
        }

        DataResult<ItemTransforms> transformsValidation = transforms.validate();
        if (transformsValidation.error().isPresent()) {
            return SelfValidating.invalid(
                    JolCraftParameterIds.TRANSFORMS + " invalid: " +
                            transformsValidation.error().map(DataResult.Error::message).orElse("")
            );
        }

        return SelfValidating.ok(this);
    }
}