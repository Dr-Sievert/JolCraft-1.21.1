package net.sievert.jolcraft.data.recipe.param.base;

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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Generic registry for one polymorphic param family.
 *
 * Owns:
 * - registered concrete type definitions
 * - lookup by type id
 * - lookup by stream discriminator
 * - strict JSON dispatch codec
 * - strict stream dispatch codec
 *
 * Rules:
 * - only real variants may be registered
 * - duplicate type ids are forbidden
 * - duplicate discriminators are forbidden
 * - missing/unknown/malformed type ids are decode errors
 * - unknown stream discriminators are decode failures
 */
public final class ParamTypeRegistry<T> {

    private final List<ParamTypeDef<T>> types;
    private final Map<ResourceLocation, ParamTypeDef<T>> byTypeId;
    private final Map<Byte, ParamTypeDef<T>> byDiscriminator;

    private ParamTypeRegistry(List<ParamTypeDef<T>> defs) {
        this.types = List.copyOf(defs);
        this.byTypeId = buildTypeIdMap(defs);
        this.byDiscriminator = buildDiscriminatorMap(defs);
    }

    public static <T> @NotNull Builder<T> builder() {
        return new Builder<>();
    }

    public @NotNull List<ParamTypeDef<T>> types() {
        return types;
    }

    public @NotNull Codec<T> codec(
            @NotNull String dispatchKey,
            @NotNull Function<T, ResourceLocation> typeIdFn
    ) {
        Encoder<T> encoder = new Encoder<>() {
            @Override
            public <O> DataResult<O> encode(T input, DynamicOps<O> ops, O prefix) {
                if (input == null) {
                    return DataResult.error(() -> "ParamTypeRegistry JSON encode failed: value is null");
                }

                ResourceLocation typeId = typeIdFn.apply(input);
                if (typeId == null) {
                    return DataResult.error(() -> "ParamTypeRegistry JSON encode failed: resolved null type id");
                }

                ParamTypeDef<T> def = byTypeId.get(typeId);
                if (def == null) {
                    return DataResult.error(() -> "ParamTypeRegistry JSON encode failed: unregistered type id=" + typeId);
                }

                @SuppressWarnings("unchecked")
                Codec<T> cast = (Codec<T>) def.codec();

                return cast.encodeStart(ops, input).flatMap(encodedValue ->
                        ops.getMap(encodedValue).flatMap(encodedMap -> {
                            RecordBuilder<O> builder = ops.mapBuilder();
                            encodedMap.entries().forEach(pair -> builder.add(pair.getFirst(), pair.getSecond()));

                            return ResourceLocation.CODEC.encodeStart(ops, def.typeId()).flatMap(typeElement -> {
                                builder.add(ops.createString(dispatchKey), typeElement);
                                return builder.build(prefix);
                            });
                        }).mapError(error ->
                                "ParamTypeRegistry JSON encode failed for type id=" + def.typeId() + ": " + error
                        )
                );
            }
        };

        Decoder<T> decoder = new Decoder<>() {
            @Override
            public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
                return ops.getMap(input).flatMap(map -> {
                    O typeValue = map.get(ops.createString(dispatchKey));
                    if (typeValue == null) {
                        return DataResult.error(() ->
                                "ParamTypeRegistry JSON decode failed: missing required field '" + dispatchKey + "'"
                        );
                    }

                    return ResourceLocation.CODEC.parse(ops, typeValue).flatMap(typeId -> {
                        ParamTypeDef<T> def = byTypeId.get(typeId);
                        if (def == null) {
                            return DataResult.error(() ->
                                    "ParamTypeRegistry JSON decode failed: unknown type id=" + typeId
                            );
                        }

                        @SuppressWarnings("unchecked")
                        Codec<T> cast = (Codec<T>) def.codec();

                        return cast.decode(ops, input).mapError(error ->
                                "ParamTypeRegistry JSON decode failed for type id=" + typeId + ": " + error
                        );
                    });
                });
            }
        };

        return Codec.of(encoder, decoder);
    }

    public @NotNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(
            @NotNull Function<T, ResourceLocation> typeIdFn
    ) {
        return StreamCodec.of(
                (buf, value) -> encodeStream(buf, value, typeIdFn),
                this::decodeStream
        );
    }

    private void encodeStream(
            @NotNull RegistryFriendlyByteBuf buf,
            T value,
            @NotNull Function<T, ResourceLocation> typeIdFn
    ) {
        if (value == null) {
            throw new IllegalStateException("ParamTypeRegistry stream encode failed: value is null");
        }

        ResourceLocation typeId = typeIdFn.apply(value);
        if (typeId == null) {
            throw new IllegalStateException("ParamTypeRegistry stream encode failed: resolved null type id");
        }

        ParamTypeDef<T> def = byTypeId.get(typeId);
        if (def == null) {
            throw new IllegalStateException("ParamTypeRegistry stream encode failed: unregistered type id=" + typeId);
        }

        buf.writeByte(def.discriminator());

        int lenPos = buf.writerIndex();
        buf.writeInt(0);
        int payloadStart = buf.writerIndex();

        @SuppressWarnings("unchecked")
        StreamCodec<RegistryFriendlyByteBuf, T> cast =
                (StreamCodec<RegistryFriendlyByteBuf, T>) def.streamCodec();

        cast.encode(buf, value);

        int payloadLen = buf.writerIndex() - payloadStart;
        buf.setInt(lenPos, payloadLen);
    }

    private T decodeStream(@NotNull RegistryFriendlyByteBuf buf) {
        byte discriminator = buf.readByte();
        int payloadLen = buf.readInt();

        if (payloadLen < 0) {
            throw new IllegalStateException(
                    "ParamTypeRegistry stream decode failed: negative payload length " + payloadLen
            );
        }

        int payloadStart = buf.readerIndex();
        int payloadEnd = payloadStart + payloadLen;

        ParamTypeDef<T> def = byDiscriminator.get(discriminator);
        if (def == null) {
            buf.readerIndex(payloadEnd);
            throw new IllegalStateException(
                    "ParamTypeRegistry stream decode failed: unknown discriminator " + discriminator
            );
        }

        @SuppressWarnings("unchecked")
        StreamCodec<RegistryFriendlyByteBuf, T> cast =
                (StreamCodec<RegistryFriendlyByteBuf, T>) def.streamCodec();

        try {
            T decoded = cast.decode(buf);

            if (buf.readerIndex() < payloadEnd) {
                buf.readerIndex(payloadEnd);
            } else if (buf.readerIndex() > payloadEnd) {
                throw new IllegalStateException(
                        "ParamTypeRegistry stream decode failed: type " + def.typeId() + " over-read payload"
                );
            }

            return decoded;
        } catch (RuntimeException e) {
            buf.readerIndex(payloadEnd);
            throw new IllegalStateException(
                    "ParamTypeRegistry stream decode failed for type id=" + def.typeId(),
                    e
            );
        }
    }

    private static <T> @NotNull Map<ResourceLocation, ParamTypeDef<T>> buildTypeIdMap(List<ParamTypeDef<T>> defs) {
        LinkedHashMap<ResourceLocation, ParamTypeDef<T>> map = new LinkedHashMap<>();
        for (ParamTypeDef<T> def : defs) {
            ParamTypeDef<T> previous = map.putIfAbsent(def.typeId(), def);
            if (previous != null) {
                throw new IllegalStateException("Duplicate param type id: " + def.typeId());
            }
        }
        return Map.copyOf(map);
    }

    private static <T> @NotNull Map<Byte, ParamTypeDef<T>> buildDiscriminatorMap(List<ParamTypeDef<T>> defs) {
        LinkedHashMap<Byte, ParamTypeDef<T>> map = new LinkedHashMap<>();
        for (ParamTypeDef<T> def : defs) {
            ParamTypeDef<T> previous = map.putIfAbsent(def.discriminator(), def);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate param discriminator: "
                                + def.discriminator()
                                + " for type ids "
                                + previous.typeId()
                                + " and "
                                + def.typeId()
                );
            }
        }
        return Map.copyOf(map);
    }

    public static final class Builder<T> {

        private final List<ParamTypeDef<T>> defs = new ArrayList<>();

        private Builder() {}

        public @NotNull Builder<T> add(@NotNull ParamTypeDef<T> def) {
            defs.add(def);
            return this;
        }

        public @NotNull ParamTypeRegistry<T> build() {
            return new ParamTypeRegistry<>(defs);
        }
    }
}