package net.sievert.jolcraft.world.recipe.param.output.custom.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.world.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ComponentTransform;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.EnchantmentTransform;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
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

    private static final String SET = JolCraftDictionary.SET;
    private static final String REMOVE_ALL =
            JolCraftStrings.underscored(JolCraftParameterIds.REMOVE, JolCraftDictionary.ALL);

    private record CanonicalRaw(
            Optional<Holder<Item>> item,
            Optional<TagKey<Item>> tag,
            Optional<ItemProducer.MapData> map,
            IntRange count,
            Optional<String> source,
            boolean removeAll,
            List<Holder<DataComponentType<?>>> keep,
            List<Holder<DataComponentType<?>>> remove,
            DataComponentPatch set,
            List<EnchantmentTransform> enchantments
    ) {
        private CanonicalRaw {
            item = item != null ? item : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            map = map != null ? map : Optional.empty();
            count = count != null ? count : IntRange.ONE;
            source = source != null ? source : Optional.empty();
            keep = keep != null ? keep : List.of();
            remove = remove != null ? remove : List.of();
            set = set != null ? set : DataComponentPatch.EMPTY;
            enchantments = enchantments != null ? enchantments : List.of();
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

    private record FlatComponentConfig(
            @Nullable String source,
            boolean removeAll,
            List<Holder<DataComponentType<?>>> keep,
            List<Holder<DataComponentType<?>>> remove,
            DataComponentPatch set
    ) {}

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

                    Codec.STRING
                            .optionalFieldOf(JolCraftParameterIds.SOURCE)
                            .forGetter(CanonicalRaw::source),

                    Codec.BOOL
                            .optionalFieldOf(REMOVE_ALL, false)
                            .forGetter(CanonicalRaw::removeAll),

                    ComponentTransform.COMPONENT_TYPE_HOLDER_CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.KEEP, List.of())
                            .forGetter(CanonicalRaw::keep),

                    ComponentTransform.COMPONENT_TYPE_HOLDER_CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.REMOVE, List.of())
                            .forGetter(CanonicalRaw::remove),

                    DataComponentPatch.CODEC
                            .optionalFieldOf(SET, DataComponentPatch.EMPTY)
                            .forGetter(CanonicalRaw::set),

                    EnchantmentTransform.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.ENCHANTMENTS, List.of())
                            .forGetter(CanonicalRaw::enchantments)
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

            ItemTransforms transforms = buildCanonicalTransforms(canonical);

            return ItemSpec.fromSelection(
                    canonical.item(),
                    canonical.tag(),
                    canonical.map(),
                    canonical.count()
            ).map(spec -> new ItemOutput(spec, transforms));
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

    private static @NotNull ItemTransforms buildCanonicalTransforms(@NotNull CanonicalRaw raw) {
        ComponentTransform.Config component = new ComponentTransform.Config(
                raw.source().orElse(null),
                raw.removeAll(),
                raw.keep(),
                raw.remove(),
                raw.set()
        );

        boolean hasComponentData =
                raw.source().isPresent()
                        || raw.removeAll()
                        || !raw.keep().isEmpty()
                        || !raw.remove().isEmpty()
                        || !raw.set().isEmpty();

        List<ComponentTransform> components = hasComponentData ? List.of(component) : List.of();

        return new ItemTransforms(raw.enchantments(), components);
    }

    private static @Nullable FlatComponentConfig tryFlattenComponents(@NotNull ItemTransforms transforms) {
        List<ComponentTransform> components = transforms.components();
        if (components.isEmpty()) {
            return new FlatComponentConfig(null, false, List.of(), List.of(), DataComponentPatch.EMPTY);
        }
        if (components.size() != 1) {
            return null;
        }
        if (!(components.getFirst() instanceof ComponentTransform.Config(
                String source,
                boolean removeAll,
                List<Holder<DataComponentType<?>>> keep,
                List<Holder<DataComponentType<?>>> remove,
                DataComponentPatch set
        ))) {
            return null;
        }

        return new FlatComponentConfig(source, removeAll, keep, remove, set);
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull ItemOutput output) {
        ItemSpec spec = output.result();
        ItemProducer producer = spec.producer();

        FlatComponentConfig flat = tryFlattenComponents(output.transforms());
        if (flat != null) {
            return Either.left(new CanonicalRaw(
                    producer.itemHolderOpt(),
                    producer.tagOpt(),
                    producer.mapDataOpt(),
                    spec.count(),
                    Optional.ofNullable(flat.source()),
                    flat.removeAll(),
                    flat.keep(),
                    flat.remove(),
                    flat.set(),
                    output.transforms().enchantments()
            ));
        }

        return Either.right(new VerboseRaw(
                spec,
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
        if (stack.isEmpty()) {
            return List.of();
        }

        if (resolver != null) {
            transforms.apply(ctx, resolver, stack);
        } else {
            transforms.apply(ctx, stack);
        }

        if (stack.isEmpty()) {
            return List.of();
        }

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