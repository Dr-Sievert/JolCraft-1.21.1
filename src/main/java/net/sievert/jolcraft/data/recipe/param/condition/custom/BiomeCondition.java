package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record BiomeCondition(
        Optional<Holder<Biome>> biome,
        Optional<TagKey<Biome>> tag,
        boolean invert
) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.BIOME);
    public static final byte DISC = 5;

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
                        "BiomeCondition requires exactly one of '" + JolCraftParameterIds.BIOME +
                                "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            return DataResult.success(new BiomeCondition(safeBiome, safeTag, invert));
        }

        static Raw fromCondition(BiomeCondition c) {
            return new Raw(c.biomeSafe(), c.tagSafe(), c.invert());
        }
    }

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

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static BiomeCondition fromStreamFields(
            Optional<Holder<Biome>> biome,
            Optional<ResourceLocation> tagLoc,
            boolean invert
    ) {
        return new BiomeCondition(
                biome != null ? biome : Optional.empty(),
                (tagLoc != null ? tagLoc : Optional.<ResourceLocation>empty())
                        .map(loc -> TagKey.create(Registries.BIOME, loc)),
                invert
        );
    }

    private Optional<Holder<Biome>> biomeSafe() {
        return biome != null ? biome : Optional.empty();
    }

    private Optional<TagKey<Biome>> tagSafe() {
        return tag != null ? tag : Optional.empty();
    }

    private Optional<ResourceLocation> tagLocation() {
        return tagSafe().map(TagKey::location);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return fromConcreteOrTag(Registries.BIOME, biomeSafe(), tagSafe());
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        boolean pass = biomeOrTagMatches(ctx);
        return invert != pass;
    }

    private boolean biomeOrTagMatches(@NotNull WorldContext ctx) {
        BlockPos pos = WorldAnchor.resolve(ctx);
        if (pos == null) {
            return false;
        }

        Holder<Biome> holder = ctx.level().getBiome(pos);

        Optional<ResourceLocation> hereKey = holder.unwrapKey().map(ResourceKey::location);

        Optional<Holder<Biome>> safeBiome = biomeSafe();
        if (safeBiome.isPresent()) {
            Optional<ResourceLocation> wantedKey = safeBiome.flatMap(h -> h.unwrapKey().map(ResourceKey::location));
            return wantedKey.flatMap(wk -> hereKey.map(hk -> hk.equals(wk))).orElse(false);
        }

        return tagSafe().map(holder::is).orElse(false);
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        boolean hasBiome = biomeSafe().isPresent();
        boolean hasTag = tagSafe().isPresent();

        if (hasBiome == hasTag) {
            return DataResult.error(() ->
                    "BiomeCondition requires exactly one of '" + JolCraftParameterIds.BIOME +
                            "' or '" + JolCraftParameterIds.TAG + "'"
            );
        }

        return DataResult.success(this);
    }
}