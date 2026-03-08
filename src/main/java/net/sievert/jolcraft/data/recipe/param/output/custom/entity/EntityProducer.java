package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class EntityProducer
        implements SelfValidating<EntityProducer>, RegistryIntrospectable, RegistryIntrospectionSource {

    private static final String KEY_ENTITY = JolCraftParameterIds.ENTITY;
    private static final String KEY_TAG = JolCraftParameterIds.TAG;

    private static final byte DISC_INVALID = 0;
    private static final byte DISC_ENTITY = 1;
    private static final byte DISC_TAG = 2;

    private static final ResourceLocation INVALID_ID =
            JolCraft.location(JolCraftDictionary.INVALID);

    public static final EntityProducer EMPTY =
            new EntityProducer(new InvalidTarget(INVALID_ID));

    private static final Codec<Holder<EntityType<?>>> ENTITY_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.ENTITY_TYPE);

    private static final Codec<TagKey<EntityType<?>>> ENTITY_TAG_CODEC =
            TagKey.codec(Registries.ENTITY_TYPE);

    private static final Decoder<EntityProducer> DECODER = new Decoder<>() {
        @Override
        public <T> DataResult<Pair<EntityProducer, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(map -> {
                T entityVal = map.get(KEY_ENTITY);
                T tagVal = map.get(KEY_TAG);

                boolean hasEntity = entityVal != null;
                boolean hasTag = tagVal != null;

                if ((hasEntity ? 1 : 0) + (hasTag ? 1 : 0) != 1) {
                    return DataResult.error(() ->
                            "EntityProducer must contain exactly one of '" + KEY_ENTITY + "' or '" + KEY_TAG + "'");
                }

                if (hasEntity) {
                    return ENTITY_HOLDER_CODEC.parse(ops, entityVal)
                            .map(h -> Pair.of(EntityProducer.entity(h), input));
                }

                return ENTITY_TAG_CODEC.parse(ops, tagVal)
                        .map(tag -> Pair.of(EntityProducer.tag(tag), input));
            });
        }
    };

    private static final Encoder<EntityProducer> ENCODER = new Encoder<>() {
        @Override
        public <T> DataResult<T> encode(EntityProducer input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> b = ops.mapBuilder();

            Target t = (input != null) ? input.target : null;

            if (t instanceof EntityTarget(Holder<EntityType<?>> entity)) {
                if (entity == null) return DataResult.error(() -> KEY_ENTITY + " cannot be null");
                return ENTITY_HOLDER_CODEC.encodeStart(ops, entity).flatMap(v -> {
                    b.add(ops.createString(KEY_ENTITY), v);
                    return b.build(prefix);
                });
            }

            if (t instanceof TagTarget(TagKey<EntityType<?>> tag)) {
                if (tag == null) return DataResult.error(() -> KEY_TAG + " cannot be null");
                return ENTITY_TAG_CODEC.encodeStart(ops, tag).flatMap(v -> {
                    b.add(ops.createString(KEY_TAG), v);
                    return b.build(prefix);
                });
            }

            TagKey<EntityType<?>> impossible = TagKey.create(Registries.ENTITY_TYPE, INVALID_ID);
            return ENTITY_TAG_CODEC.encodeStart(ops, impossible).flatMap(v -> {
                b.add(ops.createString(KEY_TAG), v);
                return b.build(prefix);
            });
        }
    };

    private static final Codec<EntityProducer> RAW_CODEC = Codec.of(ENCODER, DECODER);
    public static final Codec<EntityProducer> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<EntityType<?>>> ENTITY_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<EntityType<?>>> TAG_STREAM =
            ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.ENTITY_TYPE));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityProducer> STREAM_CODEC =
            StreamCodec.of(EntityProducer::encodeStream, EntityProducer::decodeStream);

    private static void encodeStream(RegistryFriendlyByteBuf buf, EntityProducer value) {

        byte disc = DISC_INVALID;
        Target t = (value != null) ? value.target : null;

        if (t instanceof EntityTarget) disc = DISC_ENTITY;
        else if (t instanceof TagTarget) disc = DISC_TAG;

        buf.writeByte(disc);

        int lenPos = buf.writerIndex();
        buf.writeInt(0);
        int payloadStart = buf.writerIndex();

        if (disc == DISC_ENTITY) {
            Holder<EntityType<?>> h = ((EntityTarget) t).entity();
            ENTITY_STREAM.encode(buf, h);
        } else if (disc == DISC_TAG) {
            TagKey<EntityType<?>> tag = ((TagTarget) t).tag();
            TAG_STREAM.encode(buf, tag);
        } else {
            ResourceLocation id = (t instanceof InvalidTarget(ResourceLocation reasonId) && reasonId != null)
                    ? reasonId
                    : INVALID_ID;
            buf.writeResourceLocation(id);
        }

        int payloadEnd = buf.writerIndex();
        int payloadLen = Math.max(0, payloadEnd - payloadStart);
        buf.setInt(lenPos, payloadLen);
    }

    private static EntityProducer decodeStream(RegistryFriendlyByteBuf buf) {
        byte disc = buf.readByte();
        int payloadLen = buf.readInt();
        if (payloadLen < 0) payloadLen = 0;

        int start = buf.readerIndex();
        int end = start + payloadLen;

        EntityProducer decoded = EMPTY;

        try {
            if (disc == DISC_ENTITY) {
                Holder<EntityType<?>> entity = ENTITY_STREAM.decode(buf);
                decoded = EntityProducer.entity(entity);
            } else if (disc == DISC_TAG) {
                TagKey<EntityType<?>> tag = TAG_STREAM.decode(buf);
                decoded = EntityProducer.tag(tag);
            } else {
                ResourceLocation id = buf.readResourceLocation();
                decoded = EntityProducer.invalid(id);
            }
        } catch (RuntimeException ignored) {
        }

        if (buf.readerIndex() != end) {
            buf.readerIndex(end);
        }

        return decoded;
    }

    private sealed interface Target permits EntityTarget, TagTarget, InvalidTarget {}
    private record EntityTarget(Holder<EntityType<?>> entity) implements Target {}
    private record TagTarget(TagKey<EntityType<?>> tag) implements Target {}
    private record InvalidTarget(ResourceLocation reasonId) implements Target {}

    private final Target target;

    private EntityProducer(Target target) {
        this.target = (target != null) ? target : new InvalidTarget(INVALID_ID);
    }

    @Override
    public @NotNull DataResult<EntityProducer> validate() {
        return switch (target) {
            case InvalidTarget(ResourceLocation reasonId) -> {
                ResourceLocation got = (reasonId != null) ? reasonId : INVALID_ID;
                yield DataResult.error(() -> "invalid producer (reason=" + got + ")");
            }
            case EntityTarget(Holder<EntityType<?>> entity) -> {
                if (entity == null) yield DataResult.error(() -> KEY_ENTITY + " cannot be null");
                yield DataResult.success(this);
            }
            case TagTarget(TagKey<EntityType<?>> tag) -> {
                if (tag == null) yield DataResult.error(() -> KEY_TAG + " cannot be null");
                yield DataResult.success(this);
            }
        };
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return switch (target) {
            case EntityTarget(Holder<EntityType<?>> entity) ->
                    (entity != null)
                            ? RegistryIntrospection.single(Registries.ENTITY_TYPE, entity)
                            : RegistryIntrospection.mixed(Registries.ENTITY_TYPE, 0, false);

            case TagTarget(TagKey<EntityType<?>> tag) ->
                    (tag != null)
                            ? RegistryIntrospection.singleTag(Registries.ENTITY_TYPE, tag)
                            : RegistryIntrospection.mixed(Registries.ENTITY_TYPE, 0, false);

            case InvalidTarget ignored ->
                    RegistryIntrospection.mixed(Registries.ENTITY_TYPE, 0, false);
        };
    }

    // ---------------------------------------------------------------------
    // RUNTIME
    // ---------------------------------------------------------------------

    public Optional<Holder<EntityType<?>>> select(@NotNull WorldContext ctx) {
        return switch (target) {
            case EntityTarget(Holder<EntityType<?>> entity) -> Optional.ofNullable(entity);
            case TagTarget(TagKey<EntityType<?>> tag) -> selectFromTag(ctx, tag);
            case InvalidTarget ignored -> Optional.empty();
        };
    }

    private static Optional<Holder<EntityType<?>>> selectFromTag(
            @NotNull WorldContext ctx,
            @NotNull TagKey<EntityType<?>> tag
    ) {
        var registryAccess = ctx.level().registryAccess();
        var random = ctx.random();

        var regOpt = registryAccess.lookup(Registries.ENTITY_TYPE);
        if (regOpt.isEmpty()) return Optional.empty();

        var namedOpt = regOpt.get().get(tag);
        if (namedOpt.isEmpty()) return Optional.empty();

        var named = namedOpt.get();
        int size = named.size();
        if (size <= 0) return Optional.empty();

        return Optional.of(named.get(random.nextInt(size)));
    }

    // ---------------------------------------------------------------------
    // FACTORIES
    // ---------------------------------------------------------------------

    public static EntityProducer entity(Holder<EntityType<?>> entity) {
        if (entity == null) return EMPTY;
        return new EntityProducer(new EntityTarget(entity));
    }

    public static EntityProducer tag(TagKey<EntityType<?>> tag) {
        if (tag == null) return EMPTY;
        return new EntityProducer(new TagTarget(tag));
    }

    public static EntityProducer invalid(ResourceLocation reasonId) {
        return new EntityProducer(new InvalidTarget(reasonId != null ? reasonId : INVALID_ID));
    }
}