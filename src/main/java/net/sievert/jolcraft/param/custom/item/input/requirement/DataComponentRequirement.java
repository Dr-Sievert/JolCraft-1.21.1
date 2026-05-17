package net.sievert.jolcraft.param.custom.item.input.requirement;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public record DataComponentRequirement(
        List<DataComponentPredicate> predicates,
        List<Holder<DataComponentType<?>>> has,
        List<ComponentRangePredicate> ranges
) implements ParamData<DataComponentRequirement>, ParamMatching<ItemStack> {

    private static final Codec<List<Holder<DataComponentType<?>>>> HAS_CODEC =
            ParamCodecs.holderList(Registries.DATA_COMPONENT_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<DataComponentType<?>>> COMPONENT_STREAM =
            ByteBufCodecs.holderRegistry(Registries.DATA_COMPONENT_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<DataComponentPredicate>> PREDICATES_STREAM =
            ByteBufCodecs.collection(ArrayList::new, DataComponentPredicate.STREAM_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Holder<DataComponentType<?>>>> HAS_STREAM =
            ByteBufCodecs.collection(ArrayList::new, COMPONENT_STREAM);

    private static final StreamCodec<RegistryFriendlyByteBuf, ComponentRangePredicate> RANGE_STREAM =
            StreamCodec.of(
                    (buf, range) -> {
                        COMPONENT_STREAM.encode(buf, range.component());
                        buf.writeDouble(range.min());
                        buf.writeDouble(range.max());
                    },
                    buf -> new ComponentRangePredicate(
                            COMPONENT_STREAM.decode(buf),
                            buf.readDouble(),
                            buf.readDouble()
                    )
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<ComponentRangePredicate>> RANGES_STREAM =
            ByteBufCodecs.collection(ArrayList::new, RANGE_STREAM);

    private static final Encoder<DataComponentRequirement> ENCODER =
            new Encoder<>() {
                @Override
                public <T> DataResult<T> encode(DataComponentRequirement input, DynamicOps<T> ops, T prefix) {
                    return input.encodeFlat(ops);
                }
            };

    private static final Decoder<DataComponentRequirement> DECODER =
            new Decoder<>() {
                @Override
                public <T> DataResult<Pair<DataComponentRequirement, T>> decode(DynamicOps<T> ops, T input) {
                    return DataComponentRequirement.decodeFlat(ops, input)
                            .map(requirement -> Pair.of(requirement, input));
                }
            };

    public static final Codec<DataComponentRequirement> CODEC =
            ParamCodecs.validated(
                    Codec.of(ENCODER, DECODER),
                    DataComponentRequirement::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentRequirement> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    PREDICATES_STREAM,
                    DataComponentRequirement::predicates,
                    HAS_STREAM,
                    DataComponentRequirement::has,
                    RANGES_STREAM,
                    DataComponentRequirement::ranges,
                    DataComponentRequirement::new
            ), DataComponentRequirement::validate);

    public DataComponentRequirement {
        predicates = ParamValidations.sanitizeList(predicates);
        has = ParamValidations.sanitizeList(has);
        ranges = ParamValidations.sanitizeList(ranges);
    }

    private static <T> DataResult<DataComponentRequirement> decodeFlat(DynamicOps<T> ops, T input) {
        DataResult<Stream<Pair<T, T>>> mapResult = ops.getMapValues(input);
        if (mapResult.error().isPresent()) {
            return ParamValidations.invalid("data_components must be an object");
        }

        List<DataComponentPredicate> predicates = new ArrayList<>();
        List<Holder<DataComponentType<?>>> has = new ArrayList<>();
        List<ComponentRangePredicate> ranges = new ArrayList<>();

        List<Pair<T, T>> entries = mapResult.result().orElseThrow().toList();

        for (Pair<T, T> entry : entries) {
            DataResult<ResourceLocation> idResult = ResourceLocation.CODEC.parse(ops, entry.getFirst());
            if (idResult.error().isPresent()) {
                return idResult.map(id -> new DataComponentRequirement(List.of(), List.of(), List.of()));
            }

            ResourceLocation id = idResult.result().orElseThrow();

            DataResult<Holder<DataComponentType<?>>> holderResult = resolveComponent(ops, id);
            if (holderResult.error().isPresent()) {
                return holderResult.map(holder -> new DataComponentRequirement(List.of(), List.of(holder), List.of()));
            }

            Holder<DataComponentType<?>> holder = holderResult.result().orElseThrow();
            T value = entry.getSecond();

            if (ops.getBooleanValue(value).result().orElse(false)) {
                has.add(holder);
                continue;
            }

            ComponentRangePredicate range = tryRange(ops, holder, value);
            if (range != null) {
                ranges.add(range);
                continue;
            }

            DataResult<DataComponentPredicate> predicate = parseVanillaPredicate(ops, id, value);
            if (predicate.error().isPresent()) {
                return predicate.map(p -> new DataComponentRequirement(List.of(p), List.of(), List.of()));
            }

            predicates.add(predicate.result().orElseThrow());
        }

        return new DataComponentRequirement(predicates, has, ranges).validate();
    }

    private static <T> DataResult<Holder<DataComponentType<?>>> resolveComponent(
            DynamicOps<T> ops,
            ResourceLocation id
    ) {
        return HAS_CODEC.parse(ops, ops.createString(id.toString())).flatMap(values -> {
            if (values.size() != 1) {
                return ParamValidations.invalid("expected one data component id: " + id);
            }

            return ParamValidations.ok(values.getFirst());
        });
    }

    private static <T> DataResult<DataComponentPredicate> parseVanillaPredicate(
            DynamicOps<T> ops,
            ResourceLocation id,
            T value
    ) {
        Map<T, T> raw = Map.of(ops.createString(id.toString()), value);
        return DataComponentPredicate.CODEC.parse(ops, ops.createMap(raw));
    }

    private static <T> ComponentRangePredicate tryRange(
            DynamicOps<T> ops,
            Holder<DataComponentType<?>> component,
            T value
    ) {
        DataResult<Stream<T>> streamResult = ops.getStream(value);
        if (streamResult.error().isPresent()) return null;

        List<T> values = streamResult.result().orElseThrow().toList();
        if (values.size() != 2) return null;

        DataResult<Number> min = ops.getNumberValue(values.get(0));
        DataResult<Number> max = ops.getNumberValue(values.get(1));

        if (min.error().isPresent() || max.error().isPresent()) return null;

        return new ComponentRangePredicate(
                component,
                min.result().orElseThrow().doubleValue(),
                max.result().orElseThrow().doubleValue()
        );
    }

    private <T> DataResult<T> encodeFlat(DynamicOps<T> ops) {
        Map<T, T> out = new LinkedHashMap<>();

        for (Holder<DataComponentType<?>> holder : has) {
            DataResult<ResourceLocation> idResult = idOf(holder);
            if (idResult.error().isPresent()) {
                return idResult.flatMap(id -> ParamValidations.invalid("unregistered data component holder"));
            }

            out.put(ops.createString(idResult.result().orElseThrow().toString()), ops.createBoolean(true));
        }

        for (ComponentRangePredicate range : ranges) {
            DataResult<ResourceLocation> idResult = idOf(range.component());
            if (idResult.error().isPresent()) {
                return idResult.flatMap(id -> ParamValidations.invalid("unregistered data component holder"));
            }

            out.put(
                    ops.createString(idResult.result().orElseThrow().toString()),
                    ops.createList(Stream.of(
                            ops.createDouble(range.min()),
                            ops.createDouble(range.max())
                    ))
            );
        }

        for (DataComponentPredicate predicate : predicates) {
            DataResult<T> encoded = DataComponentPredicate.CODEC.encodeStart(ops, predicate);
            if (encoded.error().isPresent()) return encoded;

            DataResult<Stream<Pair<T, T>>> values = ops.getMapValues(encoded.result().orElseThrow());
            if (values.error().isPresent()) {
                return ParamValidations.invalid("failed to encode data component predicate as map");
            }

            values.result().orElseThrow().forEach(pair -> out.put(pair.getFirst(), pair.getSecond()));
        }

        return ParamValidations.ok(ops.createMap(out));
    }

    private static DataResult<ResourceLocation> idOf(Holder<DataComponentType<?>> holder) {
        if (holder == null) {
            return ParamValidations.invalid("data component holder is required");
        }

        return holder.unwrapKey()
                .map(key -> ParamValidations.ok(key.location()))
                .orElseGet(() -> ParamValidations.invalid("unregistered data component holder"));
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (has.isEmpty() && predicates.isEmpty() && ranges.isEmpty()) return false;

        for (Holder<DataComponentType<?>> holder : has) {
            if (!stack.has(holder.value())) return false;
        }

        for (DataComponentPredicate predicate : predicates) {
            if (!predicate.test(stack)) return false;
        }

        for (ComponentRangePredicate range : ranges) {
            if (!range.matches(stack)) return false;
        }

        return true;
    }

    @Override
    public DataResult<DataComponentRequirement> validate() {
        if (predicates.isEmpty() && has.isEmpty() && ranges.isEmpty()) {
            return ParamValidations.invalid("data_components must not be empty");
        }

        for (int i = 0; i < has.size(); i++) {
            if (has.get(i) == null) {
                return ParamValidations.invalid("has[" + i + "] is required");
            }
        }

        for (int i = 0; i < predicates.size(); i++) {
            DataComponentPredicate predicate = predicates.get(i);

            if (predicate == null) {
                return ParamValidations.invalid("predicate[" + i + "] is required");
            }

            if (predicate.alwaysMatches()) {
                return ParamValidations.invalid("predicate[" + i + "] must not be empty");
            }
        }

        for (int i = 0; i < ranges.size(); i++) {
            DataResult<DataComponentRequirement> result =
                    ParamValidations.wrap(this, ranges.get(i).validate(), "range[" + i + "]");
            if (result.error().isPresent()) return result;
        }

        return ParamValidations.ok(this);
    }

    @Override
    public Codec<DataComponentRequirement> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DataComponentRequirement> streamCodec() {
        return STREAM_CODEC;
    }

    public record ComponentRangePredicate(
            Holder<DataComponentType<?>> component,
            double min,
            double max
    ) {
        public DataResult<ComponentRangePredicate> validate() {
            return ParamValidations.all(this,
                    () -> ParamValidations.notNull(this, component, JolCraftParameterIds.DATA_COMPONENT),
                    () -> ParamValidations.minMax(this, min, max, JolCraftStrings.underscored(JolCraftParameterIds.DATA_COMPONENT, JolCraftParameterIds.RANGE))
            );
        }

        public boolean matches(ItemStack stack) {
            Object value = stack.get(component.value());

            if (!(value instanceof Number number)) {
                return false;
            }

            double actual = number.doubleValue();
            return actual >= min && actual <= max;
        }
    }
}