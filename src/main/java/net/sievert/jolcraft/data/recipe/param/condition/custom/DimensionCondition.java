package net.sievert.jolcraft.data.recipe.param.condition.custom;

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
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Atomic condition: dimension gate.
 *
 * Exactly one of id/tag.
 * Invalid state -> false at runtime.
 */
public record DimensionCondition(
        Optional<ResourceKey<Level>> dimension,
        Optional<TagKey<Level>> tag,
        boolean invert
) implements Condition {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<ResourceKey<Level>> DIMENSION_KEY_CODEC =
            ResourceKey.codec(Registries.DIMENSION);

    private static final Codec<TagKey<Level>> DIMENSION_TAG_CODEC =
            TagKey.codec(Registries.DIMENSION);

    private static final Codec<DimensionCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    DIMENSION_KEY_CODEC.optionalFieldOf(JolCraftParameterIds.ID)
                            .forGetter(DimensionCondition::dimensionSafe),
                    DIMENSION_TAG_CODEC.optionalFieldOf(JolCraftParameterIds.TAG)
                            .forGetter(DimensionCondition::tagSafe),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false)
                            .forGetter(DimensionCondition::invert)
            ).apply(inst, DimensionCondition::new));

    public static final Codec<DimensionCondition> CODEC =
            RAW_CODEC.flatXmap(
                    DimensionCondition::validateDecoded,
                    DataResult::success
            );

    private static DataResult<DimensionCondition> validateDecoded(DimensionCondition c) {
        if (c == null) {
            return DataResult.error(() -> "dimension condition is null");
        }

        boolean hasId = c.dimensionSafe().isPresent();
        boolean hasTag = c.tagSafe().isPresent();

        if (hasId == hasTag) {
            return DataResult.error(() ->
                    "dimension condition must specify exactly one of '" +
                            JolCraftParameterIds.ID + "' or '" +
                            JolCraftParameterIds.TAG + "'"
            );
        }

        return DataResult.success(c);
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<RegistryFriendlyByteBuf, ResourceKey<Level>> DIMENSION_KEY_STREAM =
            ResourceKey.streamCodec(Registries.DIMENSION).cast();

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Level>> DIMENSION_TAG_STREAM =
            TagKey.streamCodec(Registries.DIMENSION).cast();

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        Optional<ResourceKey<Level>> dim = c.dimension != null ? c.dimension : Optional.empty();
                        Optional<TagKey<Level>> tg = c.tag != null ? c.tag : Optional.empty();

                        buf.writeBoolean(dim.isPresent());
                        dim.ifPresent(key -> DIMENSION_KEY_STREAM.encode(buf, key));

                        buf.writeBoolean(tg.isPresent());
                        tg.ifPresent(t -> DIMENSION_TAG_STREAM.encode(buf, t));

                        buf.writeBoolean(c.invert);
                    },
                    buf -> {
                        Optional<ResourceKey<Level>> dim = buf.readBoolean()
                                ? Optional.of(DIMENSION_KEY_STREAM.decode(buf))
                                : Optional.empty();

                        Optional<TagKey<Level>> tg = buf.readBoolean()
                                ? Optional.of(DIMENSION_TAG_STREAM.decode(buf))
                                : Optional.empty();

                        boolean inv = buf.readBoolean();
                        return new DimensionCondition(dim, tg, inv);
                    }
            );

    // ---------------------------------------------------------------------
    // DISPATCH
    // ---------------------------------------------------------------------

    @Override
    public ResourceLocation typeId() {
        return ConditionTypes.TYPE_DIMENSION;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        Optional<ResourceKey<Level>> d = (dimension != null) ? dimension : Optional.empty();
        Optional<TagKey<Level>> t = (tag != null) ? tag : Optional.empty();
        return fromKeyOrTag(Registries.DIMENSION, d, t);
    }

    // ---------------------------------------------------------------------
    // TEST
    // ---------------------------------------------------------------------

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        Optional<ResourceKey<Level>> dim = dimensionSafe();
        Optional<TagKey<Level>> tg = tagSafe();

        if (dim.isPresent() == tg.isPresent()) {
            return false;
        }

        Level level = ctx.level();
        ResourceKey<Level> current = level.dimension();

        boolean pass;
        if (dim.isPresent()) {
            pass = current.equals(dim.get());
        } else {
            TagKey<Level> tagKey = tg.get();

            pass = level.registryAccess()
                    .lookup(Registries.DIMENSION)
                    .flatMap(lookup -> lookup.get(current))
                    .map(holder -> holder.is(tagKey))
                    .orElse(false);
        }

        return invert != pass;
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(c -> c);
    }

    // ---------------------------------------------------------------------
    // INTERNAL SAFE GETTERS
    // ---------------------------------------------------------------------

    private Optional<ResourceKey<Level>> dimensionSafe() {
        return dimension != null ? dimension : Optional.empty();
    }

    private Optional<TagKey<Level>> tagSafe() {
        return tag != null ? tag : Optional.empty();
    }
}