package net.sievert.jolcraft.param.base;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class ParamCodecs {

    private ParamCodecs() {}

    public static <T extends ParamData<T>> Codec<T> validated(Codec<T> codec) {
        return validated(codec, ParamData::validate);
    }

    public static <T> Codec<T> validated(Codec<T> codec, Function<T, DataResult<T>> validator) {
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(validator, "validator");
        return codec.flatXmap(validator, validator);
    }

    public static <B, T> StreamCodec<B, T> validatedStream(
            StreamCodec<B, T> codec,
            Function<T, DataResult<T>> validator
    ) {
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(validator, "validator");

        return StreamCodec.of(
                (buf, value) -> codec.encode(
                        buf,
                        requireDecoded(validator.apply(value), "Invalid stream value")
                ),
                buf -> requireDecoded(validator.apply(codec.decode(buf)), "Invalid stream value")
        );
    }

    public static <S, O, T> Codec<T> either(
            Codec<S> simple,
            Codec<O> object,
            Function<Either<S, O>, DataResult<T>> decoder,
            Function<T, DataResult<Either<S, O>>> encoder
    ) {
        Objects.requireNonNull(simple, "simple");
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(decoder, "decoder");
        Objects.requireNonNull(encoder, "encoder");

        return Codec.either(simple, object).flatXmap(decoder, encoder);
    }

    public static <T> Codec<List<T>> single(Codec<T> element) {
        Objects.requireNonNull(element, "element");

        return Codec.either(element, element.listOf()).flatXmap(
                value -> ParamValidations.ok(List.copyOf(value.map(List::of, Function.identity()))),
                values -> {
                    if (values == null) {
                        return ParamValidations.invalid("List cannot be null");
                    }

                    List<T> copy = List.copyOf(values);
                    return copy.size() == 1
                            ? ParamValidations.ok(Either.left(copy.getFirst()))
                            : ParamValidations.ok(Either.right(copy));
                }
        );
    }

    public static <T> Codec<Either<Holder<T>, TagKey<T>>> registryTarget(
            ResourceKey<? extends Registry<T>> registry
    ) {
        Objects.requireNonNull(registry, "registry");

        return new Codec<>() {
            @Override
            public <O> DataResult<Pair<Either<Holder<T>, TagKey<T>>, O>> decode(DynamicOps<O> ops, O input) {
                return Codec.STRING.decode(ops, input).flatMap(pair -> {
                    String value = pair.getFirst();

                    if (value == null || value.isBlank()) {
                        return ParamValidations.invalid("Empty registry target");
                    }

                    boolean tag = value.startsWith("#");
                    String raw = tag ? value.substring(1) : value;

                    if (raw.isBlank()) {
                        return ParamValidations.invalid("Empty registry id");
                    }

                    ResourceLocation id = ResourceLocation.tryParse(raw);
                    if (id == null) {
                        return ParamValidations.invalid("Invalid registry id: " + raw);
                    }

                    if (tag) {
                        return ParamValidations.ok(Pair.of(
                                Either.right(TagKey.create(registry, id)),
                                pair.getSecond()
                        ));
                    }

                    return resolveHolder(ops, registry, id)
                            .map(holder -> Pair.of(Either.left(holder), pair.getSecond()));
                });
            }

            @Override
            public <O> DataResult<O> encode(Either<Holder<T>, TagKey<T>> input, DynamicOps<O> ops, O prefix) {
                return targetId(input).flatMap(id -> Codec.STRING.encode(id, ops, prefix));
            }
        };
    }

    public static <T> Codec<RegistryTarget<T>> registryTargetValue(
            ResourceKey<? extends Registry<T>> registry
    ) {
        return registryTarget(registry).flatXmap(
                value -> new RegistryTarget<>(value).validate(),
                target -> ParamValidations.ok(target.value())
        );
    }

    public static <T> Codec<List<Holder<T>>> holderList(ResourceKey<? extends Registry<T>> registry) {
        return single(registryTarget(registry)).flatXmap(
                values -> {
                    List<Holder<T>> out = new ArrayList<>(values.size());

                    for (Either<Holder<T>, TagKey<T>> value : values) {
                        DataResult<Holder<T>> holder = requireHolder(value);
                        if (holder.error().isPresent()) {
                            return holder.map(List::of);
                        }

                        out.add(requireDecoded(holder, "Invalid holder target"));
                    }

                    return ParamValidations.ok(List.copyOf(out));
                },
                holders -> {
                    if (holders == null) {
                        return ParamValidations.invalid("Holder list cannot be null");
                    }

                    return ParamValidations.ok(holders.stream()
                            .map(Either::<Holder<T>, TagKey<T>>left)
                            .toList());
                }
        );
    }

    public record MultiCodecEntry<T>(String key, Codec<? extends T> codec) {}

    public static <T> MultiCodecEntry<T> entry(String key, Codec<? extends T> codec) {
        return new MultiCodecEntry<>(key, codec);
    }

    public static <T> Codec<List<T>> multi(
            List<MultiCodecEntry<T>> entries,
            Function<T, String> keyGetter
    ) {
        Objects.requireNonNull(keyGetter, "keyGetter");

        Map<String, Codec<? extends T>> codecs = codecMap(entries);

        return Codec.unboundedMap(Codec.STRING, Codec.PASSTHROUGH).flatXmap(
                raw -> {
                    List<T> out = new ArrayList<>(raw.size());

                    for (var entry : raw.entrySet()) {
                        String key = entry.getKey();
                        Codec<? extends T> codec = codecs.get(key);

                        if (codec == null) {
                            return ParamValidations.invalid("Unknown key: " + key);
                        }

                        DataResult<? extends T> parsed = unwrap(codec.parse(entry.getValue()), key, "failed to parse");
                        if (parsed.error().isPresent()) {
                            return parsed.map(List::of);
                        }

                        out.add(requireDecoded(parsed, key + ": failed to parse"));
                    }

                    return ParamValidations.ok(List.copyOf(out));
                },
                values -> {
                    if (values == null) {
                        return ParamValidations.invalid("Multi value list cannot be null");
                    }

                    Map<String, Dynamic<?>> out = new LinkedHashMap<>();

                    for (T value : values) {
                        String key = keyGetter.apply(value);

                        if (key == null || key.isBlank()) {
                            return ParamValidations.invalid("Multi value key cannot be blank");
                        }

                        Codec<? extends T> codec = codecs.get(key);
                        if (codec == null) {
                            return ParamValidations.invalid("Unknown key: " + key);
                        }

                        if (out.containsKey(key)) {
                            return ParamValidations.invalid("Duplicate key: " + key);
                        }

                        @SuppressWarnings("unchecked")
                        Codec<T> typed = (Codec<T>) codec;

                        DataResult<Dynamic<?>> encoded = unwrap(
                                typed.encodeStart(JsonOps.INSTANCE, value)
                                        .map(json -> new Dynamic<>(JsonOps.INSTANCE, json)),
                                key,
                                "failed to encode"
                        );

                        if (encoded.error().isPresent()) {
                            return encoded.map(v -> out);
                        }

                        out.put(key, requireDecoded(encoded, key + ": failed to encode"));
                    }

                    return ParamValidations.ok(Map.copyOf(out));
                }
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, Either<Holder<T>, TagKey<T>>> registryTargetStream(
            ResourceKey<? extends Registry<T>> registry
    ) {
        Objects.requireNonNull(registry, "registry");

        var holderStream = ByteBufCodecs.holderRegistry(registry);

        final int KIND_HOLDER = 0;
        final int KIND_TAG = 1;

        return StreamCodec.of(
                (buf, value) -> value.ifLeft(holder -> {
                    buf.writeByte(KIND_HOLDER);
                    holderStream.encode(buf, holder);
                }).ifRight(tag -> {
                    buf.writeByte(KIND_TAG);
                    buf.writeResourceLocation(tag.location());
                }),
                buf -> switch (buf.readUnsignedByte()) {
                    case KIND_HOLDER -> Either.left(holderStream.decode(buf));
                    case KIND_TAG -> Either.right(TagKey.create(registry, buf.readResourceLocation()));
                    default -> throw new IllegalArgumentException("Unknown registry target kind");
                }
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, RegistryTarget<T>> registryTargetValueStream(
            ResourceKey<? extends Registry<T>> registry
    ) {
        return validatedStream(
                registryTargetStream(registry).map(RegistryTarget::new, RegistryTarget::value),
                RegistryTarget::validate
        );
    }

    public record MultiStreamCodecEntry<T>(
            String key,
            StreamCodec<RegistryFriendlyByteBuf, ? extends T> codec
    ) {}

    public static <T> MultiStreamCodecEntry<T> streamEntry(
            String key,
            StreamCodec<RegistryFriendlyByteBuf, ? extends T> codec
    ) {
        return new MultiStreamCodecEntry<>(key, codec);
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, List<T>> multiStream(
            List<MultiStreamCodecEntry<T>> entries,
            Function<T, String> keyGetter
    ) {
        Objects.requireNonNull(keyGetter, "keyGetter");

        Map<String, StreamCodec<RegistryFriendlyByteBuf, ? extends T>> codecs = streamCodecMap(entries);

        return StreamCodec.of(
                (buf, values) -> {

                    buf.writeVarInt(values.size());

                    for (T value : values) {
                        String key = keyGetter.apply(value);

                        if (key == null || key.isBlank()) {
                            throw new IllegalArgumentException("Multi stream key cannot be blank");
                        }

                        StreamCodec<RegistryFriendlyByteBuf, ? extends T> codec = codecs.get(key);
                        if (codec == null) {
                            throw new IllegalArgumentException("Unknown key: " + key);
                        }

                        buf.writeUtf(key);

                        @SuppressWarnings("unchecked")
                        StreamCodec<RegistryFriendlyByteBuf, T> typed =
                                (StreamCodec<RegistryFriendlyByteBuf, T>) codec;

                        typed.encode(buf, value);
                    }
                },
                buf -> {
                    int size = buf.readVarInt();
                    if (size < 0) {
                        throw new IllegalArgumentException("Multi stream size must be >= 0");
                    }

                    List<T> out = new ArrayList<>(size);

                    for (int i = 0; i < size; i++) {
                        String key = buf.readUtf();
                        StreamCodec<RegistryFriendlyByteBuf, ? extends T> codec = codecs.get(key);

                        if (codec == null) {
                            throw new IllegalArgumentException("Unknown key: " + key);
                        }

                        out.add(codec.decode(buf));
                    }

                    return List.copyOf(out);
                }
        );
    }

    private static <T, O> DataResult<Holder<T>> resolveHolder(
            DynamicOps<O> ops,
            ResourceKey<? extends Registry<T>> registry,
            ResourceLocation id
    ) {
        if (!(ops instanceof RegistryOps<O> registryOps)) {
            return ParamValidations.invalid("Registry target requires RegistryOps for " + registry.location());
        }

        var lookup = registryOps.lookupProvider.lookup(registry);
        if (lookup.isEmpty()) {
            return ParamValidations.invalid("Missing registry: " + registry.location());
        }

        var holder = lookup.get().getter().get(ResourceKey.create(registry, id));
        return holder.<DataResult<Holder<T>>>map(ParamValidations::ok)
                .orElseGet(() -> ParamValidations.invalid("Unknown registry value: " + id));
    }

    private static <T> DataResult<String> targetId(Either<Holder<T>, TagKey<T>> target) {
        if (target == null) {
            return ParamValidations.invalid("Registry target cannot be null");
        }

        String value = target.map(
                holder -> holder.unwrapKey().map(key -> key.location().toString()).orElse(""),
                tag -> "#" + tag.location()
        );

        return value.isEmpty()
                ? ParamValidations.invalid("Unkeyed registry holder")
                : ParamValidations.ok(value);
    }

    private static <T> DataResult<Holder<T>> requireHolder(Either<Holder<T>, TagKey<T>> target) {
        if (target == null) {
            return ParamValidations.invalid("Registry target cannot be null");
        }

        return target.map(
                ParamValidations::ok,
                tag -> ParamValidations.invalid("Tags are not allowed here: #" + tag.location())
        );
    }

    private static <T> DataResult<T> unwrap(DataResult<T> result, String key, String fallback) {
        var error = result.error();
        if (error.isPresent()) {
            return ParamValidations.invalid(key + ": " + error.map(DataResult.Error::message).orElse(fallback));
        }

        return result.result()
                .map(ParamValidations::ok)
                .orElseGet(() -> ParamValidations.invalid(key + ": " + fallback));
    }

    private static <T> T requireDecoded(DataResult<? extends T> result, String fallback) {
        return result.result().orElseThrow(() -> new IllegalArgumentException(
                result.error().map(DataResult.Error::message).orElse(fallback)
        ));
    }

    private static <T> Map<String, Codec<? extends T>> codecMap(List<MultiCodecEntry<T>> entries) {
        if (entries == null || entries.isEmpty()) {
            throw new IllegalArgumentException("Multi codec entries cannot be empty");
        }

        Map<String, Codec<? extends T>> codecs = new LinkedHashMap<>();

        for (MultiCodecEntry<T> entry : entries) {
            if (entry == null) {
                throw new IllegalArgumentException("Multi codec entry cannot be null");
            }

            if (entry.key() == null || entry.key().isBlank()) {
                throw new IllegalArgumentException("Multi codec key cannot be blank");
            }

            if (entry.codec() == null) {
                throw new IllegalArgumentException("Multi codec cannot be null for key: " + entry.key());
            }

            if (codecs.putIfAbsent(entry.key(), entry.codec()) != null) {
                throw new IllegalArgumentException("Duplicate multi codec key: " + entry.key());
            }
        }

        return Map.copyOf(codecs);
    }

    private static <T> Map<String, StreamCodec<RegistryFriendlyByteBuf, ? extends T>> streamCodecMap(
            List<MultiStreamCodecEntry<T>> entries
    ) {
        if (entries == null || entries.isEmpty()) {
            throw new IllegalArgumentException("Multi stream codec entries cannot be empty");
        }

        Map<String, StreamCodec<RegistryFriendlyByteBuf, ? extends T>> codecs = new LinkedHashMap<>();

        for (MultiStreamCodecEntry<T> entry : entries) {
            if (entry == null) {
                throw new IllegalArgumentException("Multi stream codec entry cannot be null");
            }

            if (entry.key() == null || entry.key().isBlank()) {
                throw new IllegalArgumentException("Multi stream codec key cannot be blank");
            }

            if (entry.codec() == null) {
                throw new IllegalArgumentException("Multi stream codec cannot be null for key: " + entry.key());
            }

            if (codecs.putIfAbsent(entry.key(), entry.codec()) != null) {
                throw new IllegalArgumentException("Duplicate multi stream codec key: " + entry.key());
            }
        }

        return Map.copyOf(codecs);
    }
}