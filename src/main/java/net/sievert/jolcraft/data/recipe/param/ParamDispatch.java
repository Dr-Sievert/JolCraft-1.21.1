package net.sievert.jolcraft.data.recipe.param;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Shared "dispatched union" plumbing for params.
 *
 * Provides:
 * - JSON dispatch via a stable "type" field (or any key you pass in)
 * - Stream dispatch via stable [disc][payloadLen][payloadBytes] framing
 *
 * Contract:
 * - Decode never hard-fails for unknown/missing/invalid type; it returns an Invalid sentinel via invalidFactory.
 * - Encode always injects the dispatch key with the resolved entry.typeId.
 * - Stream decode is total and never desyncs: it always consumes exactly payloadLen bytes.
 */
public final class ParamDispatch {

    private ParamDispatch() {}

    public record Entry<T>(
            ResourceLocation typeId,
            byte discriminator,
            Codec<? extends T> codec,
            StreamCodec<RegistryFriendlyByteBuf, ? extends T> streamCodec
    ) {}

    /**
     * Build a dispatched JSON Codec:
     * - Reads dispatchKey to select a variant codec from byTypeId.
     * - Missing/unknown/parse-error -> invalidFactory.apply(typeId)
     * - Encode injects dispatchKey with the chosen entry.typeId.
     */
    public static <T> Codec<T> codec(
            String dispatchKey,
            Supplier<T> defaultValue,
            Function<T, ResourceLocation> typeIdFn,
            Map<ResourceLocation, Entry<T>> byTypeId,
            Entry<T> invalidEntry,
            Function<ResourceLocation, T> invalidFactory,
            ResourceLocation typeMissing,
            ResourceLocation typeInvalid
    ) {
        Encoder<T> encoder = new Encoder<>() {
            @Override
            public <O> DataResult<O> encode(T input, DynamicOps<O> ops, O prefix) {
                T safe = (input == null) ? defaultValue.get() : input;

                ResourceLocation gotType = typeIdFn.apply(safe);
                if (gotType == null) gotType = typeInvalid;

                Entry<T> resolved = byTypeId.get(gotType);
                if (resolved == null) {
                    resolved = invalidEntry;
                    safe = invalidFactory.apply(gotType);
                }

                final Entry<T> entry = resolved;
                final T finalSafe = safe;

                @SuppressWarnings("unchecked")
                Codec<T> cast = (Codec<T>) entry.codec();

                return cast.encodeStart(ops, finalSafe).flatMap(encodedValue ->
                        ops.getMap(encodedValue).flatMap(encodedMap -> {
                            RecordBuilder<O> builder = ops.mapBuilder();
                            encodedMap.entries().forEach(p -> builder.add(p.getFirst(), p.getSecond()));

                            return ResourceLocation.CODEC.encodeStart(ops, entry.typeId()).flatMap(te -> {
                                builder.add(ops.createString(dispatchKey), te);
                                return builder.build(prefix);
                            });
                        })
                );
            }
        };

        Decoder<T> decoder = new Decoder<>() {
            @Override
            public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
                return ops.getMap(input).flatMap(map -> {
                    O key = ops.createString(dispatchKey);
                    O typeValue = map.get(key);
                    if (typeValue == null) {
                        return DataResult.success(Pair.of(invalidFactory.apply(typeMissing), input));
                    }

                    ResourceLocation typeId =
                            ResourceLocation.CODEC.parse(ops, typeValue).result().orElse(typeInvalid);

                    Entry<T> entry = byTypeId.get(typeId);
                    if (entry == null) {
                        return DataResult.success(Pair.of(invalidFactory.apply(typeId), input));
                    }

                    @SuppressWarnings("unchecked")
                    Codec<T> cast = (Codec<T>) entry.codec();

                    DataResult<T> decoded = cast.parse(ops, input);
                    Optional<DataResult.Error<T>> err = decoded.error();
                    if (err.isPresent()) {
                        return DataResult.success(Pair.of(invalidFactory.apply(typeId), input));
                    }

                    return DataResult.success(Pair.of(decoded.result().orElse(invalidFactory.apply(typeId)), input));
                });
            }
        };

        return Codec.of(encoder, decoder);
    }

    /**
     * Build a dispatched StreamCodec:
     * Stream format:
     *   [byte disc][int payloadLen][payload bytes...]
     *
     * - Unknown discriminator: skip payload and return invalidFactory(typeInvalid)
     * - Decode error: skip payload and return invalidFactory(entry.typeId)
     * - Always consumes exactly payloadLen bytes (no desync)
     */
    public static <T> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(
            Supplier<T> defaultValue,
            Function<T, ResourceLocation> typeIdFn,
            Map<ResourceLocation, Entry<T>> byTypeId,
            Map<Byte, Entry<T>> byDisc,
            Entry<T> invalidEntry,
            Function<ResourceLocation, T> invalidFactory,
            ResourceLocation typeInvalid
    ) {
        return StreamCodec.of(
                (buf, value) -> encodeStream(buf, value, defaultValue, typeIdFn, byTypeId, invalidEntry, invalidFactory, typeInvalid),
                buf -> decodeStream(buf, byDisc, invalidEntry, invalidFactory, typeInvalid)
        );
    }

    private static <T> void encodeStream(
            RegistryFriendlyByteBuf buf,
            T value,
            Supplier<T> defaultValue,
            Function<T, ResourceLocation> typeIdFn,
            Map<ResourceLocation, Entry<T>> byTypeId,
            Entry<T> invalidEntry,
            Function<ResourceLocation, T> invalidFactory,
            ResourceLocation typeInvalid
    ) {
        T safe = (value == null) ? defaultValue.get() : value;

        ResourceLocation gotType = typeIdFn.apply(safe);
        if (gotType == null) gotType = typeInvalid;

        Entry<T> resolved = byTypeId.get(gotType);
        if (resolved == null) {
            resolved = invalidEntry;
            safe = invalidFactory.apply(gotType);
        }

        final Entry<T> entry = resolved;
        final T finalSafe = safe;

        buf.writeByte(entry.discriminator());

        int lenPos = buf.writerIndex();
        buf.writeInt(0);
        int payloadStart = buf.writerIndex();

        @SuppressWarnings("unchecked")
        StreamCodec<RegistryFriendlyByteBuf, T> cast =
                (StreamCodec<RegistryFriendlyByteBuf, T>) entry.streamCodec();

        cast.encode(buf, finalSafe);

        int payloadEnd = buf.writerIndex();
        int payloadLen = Math.max(0, payloadEnd - payloadStart);
        buf.setInt(lenPos, payloadLen);
    }

    private static <T> T decodeStream(
            RegistryFriendlyByteBuf buf,
            Map<Byte, Entry<T>> byDisc,
            Entry<T> invalidEntry,
            Function<ResourceLocation, T> invalidFactory,
            ResourceLocation typeInvalid
    ) {
        byte disc = buf.readByte();
        int payloadLen = buf.readInt();
        if (payloadLen < 0) payloadLen = 0;

        int start = buf.readerIndex();
        int end = start + payloadLen;

        Entry<T> entry = byDisc.get(disc);
        if (entry == null) {
            buf.readerIndex(end);
            return invalidFactory.apply(typeInvalid);
        }

        T decoded;

        @SuppressWarnings("unchecked")
        StreamCodec<RegistryFriendlyByteBuf, T> cast =
                (StreamCodec<RegistryFriendlyByteBuf, T>) entry.streamCodec();

        try {
            int before = buf.readerIndex();
            decoded = cast.decode(buf);

            int consumed = buf.readerIndex() - before;
            int remaining = payloadLen - consumed;

            if (remaining > 0) {
                buf.skipBytes(remaining);
            } else if (remaining < 0) {
                buf.readerIndex(end);
            }
        } catch (RuntimeException ignored) {
            buf.readerIndex(end);
            decoded = invalidFactory.apply(entry.typeId());
        }

        if (buf.readerIndex() != end) {
            buf.readerIndex(end);
        }

        if (decoded == null) {
            return invalidFactory.apply(invalidEntry.typeId());
        }

        return decoded;
    }
}