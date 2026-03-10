package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class EntityProducer
        implements SelfValidating<EntityProducer>, RegistryIntrospectable, RegistryIntrospectionSource {

    static final String ENTITY = JolCraftParameterIds.ENTITY;
    static final String TAG = JolCraftParameterIds.TAG;

    private static final byte DISC_ENTITY = 1;
    private static final byte DISC_TAG = 2;

    static final Codec<Holder<EntityType<?>>> ENTITY_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.ENTITY_TYPE);

    static final Codec<TagKey<EntityType<?>>> ENTITY_TAG_CODEC =
            TagKey.codec(Registries.ENTITY_TYPE);

    private sealed interface Target permits EntityTarget, TagTarget {}

    private record EntityTarget(@NotNull Holder<EntityType<?>> entity) implements Target {
        private EntityTarget {
            Objects.requireNonNull(entity, ENTITY);
        }
    }

    private record TagTarget(@NotNull TagKey<EntityType<?>> tag) implements Target {
        private TagTarget {
            Objects.requireNonNull(tag, TAG);
        }
    }

    private record RawCodecData(
            Optional<Holder<EntityType<?>>> entity,
            Optional<TagKey<EntityType<?>>> tag
    ) {
        private RawCodecData {
            entity = entity != null ? entity : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
        }
    }

    private static final Codec<RawCodecData> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ENTITY_HOLDER_CODEC.optionalFieldOf(ENTITY).forGetter(RawCodecData::entity),
                    ENTITY_TAG_CODEC.optionalFieldOf(TAG).forGetter(RawCodecData::tag)
            ).apply(instance, RawCodecData::new));

    public static final Codec<EntityProducer> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    EntityProducer::fromRaw,
                    EntityProducer::toRaw
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<EntityType<?>>> ENTITY_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<EntityType<?>>> TAG_STREAM =
            ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.ENTITY_TYPE));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityProducer> STREAM_CODEC =
            StreamCodec.of(EntityProducer::encodeStream, EntityProducer::decodeStream);

    private final @NotNull Target target;

    private EntityProducer(@NotNull Target target) {
        this.target = Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
    }

    public static @NotNull DataResult<EntityProducer> fromSelection(
            @NotNull Optional<Holder<EntityType<?>>> entity,
            @NotNull Optional<TagKey<EntityType<?>>> tag
    ) {
        return fromRaw(new RawCodecData(entity, tag));
    }

    private static @NotNull DataResult<EntityProducer> fromRaw(@NotNull RawCodecData raw) {
        boolean hasEntity = raw.entity().isPresent();
        boolean hasTag = raw.tag().isPresent();

        if (hasEntity == hasTag) {
            return DataResult.error(() ->
                    "entity producer requires exactly one of '" + ENTITY + "' or '" + TAG + "'");
        }

        if (hasEntity) {
            return DataResult.success(new EntityProducer(new EntityTarget(raw.entity().orElseThrow())));
        }

        return DataResult.success(new EntityProducer(new TagTarget(raw.tag().orElseThrow())));
    }

    private static @NotNull RawCodecData toRaw(@NotNull EntityProducer producer) {
        return switch (producer.target) {
            case EntityTarget(Holder<EntityType<?>> entity) ->
                    new RawCodecData(Optional.of(entity), Optional.empty());
            case TagTarget(TagKey<EntityType<?>> tag) ->
                    new RawCodecData(Optional.empty(), Optional.of(tag));
        };
    }

    private static void encodeStream(@NotNull RegistryFriendlyByteBuf buf, @NotNull EntityProducer value) {
        switch (value.target) {
            case EntityTarget(Holder<EntityType<?>> entity) -> {
                buf.writeByte(DISC_ENTITY);
                ENTITY_STREAM.encode(buf, entity);
            }
            case TagTarget(TagKey<EntityType<?>> tag) -> {
                buf.writeByte(DISC_TAG);
                TAG_STREAM.encode(buf, tag);
            }
        }
    }

    private static @NotNull EntityProducer decodeStream(@NotNull RegistryFriendlyByteBuf buf) {
        byte disc = buf.readByte();

        return switch (disc) {
            case DISC_ENTITY -> new EntityProducer(new EntityTarget(ENTITY_STREAM.decode(buf)));
            case DISC_TAG -> new EntityProducer(new TagTarget(TAG_STREAM.decode(buf)));
            default -> throw new IllegalArgumentException("unknown entity producer discriminator: " + disc);
        };
    }

    @Override
    public @NotNull DataResult<EntityProducer> validate() {
        return SelfValidating.ok(this);
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return switch (target) {
            case EntityTarget(Holder<EntityType<?>> entity) ->
                    RegistryIntrospection.single(Registries.ENTITY_TYPE, entity);
            case TagTarget(TagKey<EntityType<?>> tag) ->
                    RegistryIntrospection.singleTag(Registries.ENTITY_TYPE, tag);
        };
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return List.of(introspection());
    }

    public @NotNull Optional<Holder<EntityType<?>>> select(@NotNull WorldContext ctx) {
        return switch (target) {
            case EntityTarget(Holder<EntityType<?>> entity) -> Optional.of(entity);
            case TagTarget(TagKey<EntityType<?>> tag) -> selectFromTag(ctx, tag);
        };
    }

    private static @NotNull Optional<Holder<EntityType<?>>> selectFromTag(
            @NotNull WorldContext ctx,
            @NotNull TagKey<EntityType<?>> tag
    ) {
        var registryLookup = ctx.level().registryAccess().lookup(Registries.ENTITY_TYPE);
        if (registryLookup.isEmpty()) return Optional.empty();

        var namedSet = registryLookup.get().get(tag);
        if (namedSet.isEmpty()) return Optional.empty();

        var holders = namedSet.get();
        int size = holders.size();
        if (size <= 0) return Optional.empty();

        return Optional.of(holders.get(ctx.random().nextInt(size)));
    }

    public static @NotNull EntityProducer entity(@NotNull Holder<EntityType<?>> entity) {
        return new EntityProducer(new EntityTarget(entity));
    }

    public static @NotNull EntityProducer tag(@NotNull TagKey<EntityType<?>> tag) {
        return new EntityProducer(new TagTarget(tag));
    }

    public @NotNull Optional<Holder<EntityType<?>>> entityOpt() {
        return target instanceof EntityTarget(Holder<EntityType<?>> entity) ? Optional.of(entity) : Optional.empty();
    }

    public @NotNull Optional<TagKey<EntityType<?>>> tagOpt() {
        return target instanceof TagTarget(TagKey<EntityType<?>> tag) ? Optional.of(tag) : Optional.empty();
    }
}