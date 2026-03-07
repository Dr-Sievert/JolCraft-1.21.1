package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Atomic condition: biome gate.
 *
 * Exactly one of biome/tag.
 * Fail-closed runtime.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record BiomeCondition(
        Optional<Holder<Biome>> biome,
        Optional<TagKey<Biome>> tag,
        boolean invert
) implements Condition {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    public static final Codec<BiomeCondition> CODEC =
            Raw.CODEC.flatXmap(
                    Raw::toCondition,
                    c -> DataResult.success(Raw.fromCondition(c))
            );

    private record Raw(Optional<Holder<Biome>> biome, Optional<TagKey<Biome>> tag, boolean invert) {

        private static final Codec<Holder<Biome>> BIOME_HOLDER_CODEC =
                RegistryFixedCodec.create(Registries.BIOME);

        private static final Codec<Raw> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BIOME_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.BIOME).forGetter(Raw::biome),
                TagKey.codec(Registries.BIOME).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag),
                Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(Raw::invert)
        ).apply(instance, Raw::new));

        DataResult<BiomeCondition> toCondition() {
            Optional<Holder<Biome>> safeBiome = biome != null ? biome : Optional.empty();
            Optional<TagKey<Biome>> safeTag = tag != null ? tag : Optional.empty();

            boolean hasBiome = safeBiome.isPresent();
            boolean hasTag = safeTag.isPresent();

            if (hasBiome == hasTag) {
                return DataResult.error(() ->
                        "BiomeCondition requires exactly one of '" + JolCraftParameterIds.BIOME + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            return DataResult.success(new BiomeCondition(safeBiome, safeTag, invert));
        }

        static Raw fromCondition(BiomeCondition c) {
            Optional<Holder<Biome>> safeBiome = (c != null && c.biome() != null) ? c.biome() : Optional.empty();
            Optional<TagKey<Biome>> safeTag = (c != null && c.tag() != null) ? c.tag() : Optional.empty();
            boolean inv = c != null && c.invert();
            return new Raw(safeBiome, safeTag, inv);
        }
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<ResourceLocation>> OPTIONAL_RL_STREAM =
            StreamCodec.of(
                    (buf, opt) -> {
                        buf.writeBoolean(opt.isPresent());
                        opt.ifPresent(buf::writeResourceLocation);
                    },
                    buf -> buf.readBoolean() ? Optional.of(buf.readResourceLocation()) : Optional.empty()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, BiomeCondition> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.BIOME)), BiomeCondition::biomeSafe,
                    OPTIONAL_RL_STREAM, BiomeCondition::tagLocation,
                    ByteBufCodecs.BOOL, BiomeCondition::invert,
                    BiomeCondition::fromStreamFields
            );

    private static BiomeCondition fromStreamFields(
            Optional<Holder<Biome>> biome,
            Optional<ResourceLocation> tagLoc,
            boolean invert
    ) {
        Optional<Holder<Biome>> safeBiome = biome != null ? biome : Optional.empty();
        Optional<TagKey<Biome>> safeTag = (tagLoc != null ? tagLoc : Optional.<ResourceLocation>empty())
                .map(loc -> TagKey.create(Registries.BIOME, loc));

        return new BiomeCondition(safeBiome, safeTag, invert);
    }

    private Optional<Holder<Biome>> biomeSafe() {
        return biome != null ? biome : Optional.empty();
    }

    private Optional<ResourceLocation> tagLocation() {
        Optional<TagKey<Biome>> safe = tag != null ? tag : Optional.empty();
        return safe.map(TagKey::location);
    }

    // ---------------------------------------------------------------------
    // DISPATCH
    // ---------------------------------------------------------------------

    @Override
    public ResourceLocation typeId() {
        return ConditionTypes.TYPE_BIOME;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        Optional<Holder<Biome>> b = (biome != null) ? biome : Optional.empty();
        Optional<TagKey<Biome>> t = (tag != null) ? tag : Optional.empty();
        return fromConcreteOrTag(Registries.BIOME, b, t);
    }

    // ---------------------------------------------------------------------
    // TEST
    // ---------------------------------------------------------------------

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        boolean pass = biomeOrTagMatches(ctx);
        return invert != pass;
    }

    private boolean biomeOrTagMatches(@NotNull WorldContext ctx) {
        Holder<Biome> holder = ctx.level().getBiome(ctx.player().blockPosition());

        Optional<ResourceLocation> hereKey =
                holder.unwrapKey().map(ResourceKey::location);

        Optional<Holder<Biome>> safeBiome = biomeSafe();
        if (safeBiome.isPresent()) {
            Optional<ResourceLocation> wantedKey =
                    safeBiome.flatMap(h -> h.unwrapKey().map(ResourceKey::location));

            return wantedKey.flatMap(wk -> hereKey.map(hk -> hk.equals(wk))).orElse(false);
        }

        Optional<TagKey<Biome>> safeTag = (tag != null) ? tag : Optional.empty();
        return safeTag.map(holder::is).orElse(false);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> validate() {
        Optional<Holder<Biome>> safeBiome = biomeSafe();
        Optional<TagKey<Biome>> safeTag = (tag != null) ? tag : Optional.empty();

        boolean hasBiome = safeBiome.isPresent();
        boolean hasTag = safeTag.isPresent();

        if (hasBiome == hasTag) {
            return DataResult.error(() ->
                    "BiomeCondition requires exactly one of '" + JolCraftParameterIds.BIOME + "' or '" + JolCraftParameterIds.TAG + "'"
            );
        }

        return DataResult.success(this);
    }
}