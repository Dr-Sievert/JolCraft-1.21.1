package net.sievert.jolcraft.world.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.condition.Condition;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record DimensionCondition(
        Optional<ResourceKey<Level>> dimension,
        Optional<TagKey<Level>> tag,
        boolean invert
) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.DIMENSION);
    public static final byte DISC = 4;

    private static final Codec<ResourceKey<Level>> DIMENSION_KEY_CODEC =
            ResourceKey.codec(Registries.DIMENSION);

    private static final Codec<TagKey<Level>> DIMENSION_TAG_CODEC =
            TagKey.codec(Registries.DIMENSION);

    private record Raw(
            Optional<ResourceKey<Level>> dimension,
            Optional<TagKey<Level>> tag,
            boolean invert
    ) {
        private Raw {
            dimension = dimension != null ? dimension : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
        }
    }

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    DIMENSION_KEY_CODEC.optionalFieldOf(JolCraftParameterIds.ID).forGetter(Raw::dimension),
                    DIMENSION_TAG_CODEC.optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(Raw::invert)
            ).apply(inst, Raw::new));

    public static final Codec<DimensionCondition> CODEC =
            RAW_CODEC.flatXmap(
                    DimensionCondition::fromRaw,
                    value -> DataResult.success(toRaw(value))
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, ResourceKey<Level>> DIMENSION_KEY_STREAM =
            ResourceKey.streamCodec(Registries.DIMENSION).cast();

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Level>> DIMENSION_TAG_STREAM =
            StreamCodec.of(
                    (buf, tag) -> buf.writeResourceLocation(tag.location()),
                    buf -> TagKey.create(Registries.DIMENSION, buf.readResourceLocation())
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeBoolean(value.dimension().isPresent());
                        value.dimension().ifPresent(key -> DIMENSION_KEY_STREAM.encode(buf, key));

                        buf.writeBoolean(value.tag().isPresent());
                        value.tag().ifPresent(t -> DIMENSION_TAG_STREAM.encode(buf, t));

                        buf.writeBoolean(value.invert());
                    },
                    buf -> {
                        Optional<ResourceKey<Level>> dimension = buf.readBoolean()
                                ? Optional.of(DIMENSION_KEY_STREAM.decode(buf))
                                : Optional.empty();

                        Optional<TagKey<Level>> tag = buf.readBoolean()
                                ? Optional.of(DIMENSION_TAG_STREAM.decode(buf))
                                : Optional.empty();

                        return new DimensionCondition(dimension, tag, buf.readBoolean());
                    }
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public DimensionCondition {
        dimension = dimension != null ? dimension : Optional.empty();
        tag = tag != null ? tag : Optional.empty();
    }

    private static @NotNull DataResult<DimensionCondition> fromRaw(@NotNull Raw raw) {
        return validateDecoded(new DimensionCondition(raw.dimension(), raw.tag(), raw.invert()));
    }

    private static @NotNull Raw toRaw(@NotNull DimensionCondition value) {
        return new Raw(value.dimension(), value.tag(), value.invert());
    }

    private static @NotNull DataResult<DimensionCondition> validateDecoded(@NotNull DimensionCondition value) {
        boolean hasDimension = value.dimension().isPresent();
        boolean hasTag = value.tag().isPresent();

        if (hasDimension == hasTag) {
            return DataResult.error(() ->
                    "dimension condition must specify exactly one of '" +
                            JolCraftParameterIds.ID + "' or '" + JolCraftParameterIds.TAG + "'"
            );
        }

        return DataResult.success(value);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return fromKeyOrTag(Registries.DIMENSION, dimension, tag);
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        if (dimension.isPresent() == tag.isPresent()) {
            return false;
        }

        Level level = ctx.level();
        ResourceKey<Level> current = level.dimension();

        boolean pass = dimension.map(current::equals).orElseGet(() -> level.registryAccess()
                .lookup(Registries.DIMENSION)
                .flatMap(lookup -> lookup.get(current))
                .map(holder -> holder.is(tag.orElseThrow()))
                .orElse(false));

        return invert != pass;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}