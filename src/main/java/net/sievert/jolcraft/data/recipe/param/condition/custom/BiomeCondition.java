package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
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

    private static final Codec<Holder<Biome>> BIOME_HOLDER_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder<Biome>, T>> decode(
                com.mojang.serialization.DynamicOps<T> ops,
                T input
        ) {
            return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                ResourceLocation id = pair.getFirst();
                T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> registryOps)) {
                    return DataResult.error(() ->
                            "biome condition requires RegistryOps for '" + Registries.BIOME.location() + "'"
                    );
                }

                var lookupOpt = registryOps.lookupProvider.lookup(Registries.BIOME);
                if (lookupOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" + Registries.BIOME.location() + "'"
                    );
                }

                ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, id);
                var holderOpt = lookupOpt.get().getter().get(key);

                return holderOpt.<DataResult<Pair<Holder<Biome>, T>>>map(biomeReference ->
                        DataResult.success(Pair.of(biomeReference, rest))).orElseGet(() -> DataResult.error(() -> "unknown biome '" + id + "'"));

            });
        }

        @Override
        public <T> DataResult<T> encode(
                Holder<Biome> input,
                com.mojang.serialization.DynamicOps<T> ops,
                T prefix
        ) {
            if (input == null) {
                return DataResult.error(() -> "biome holder cannot be null");
            }

            return input.unwrapKey()
                    .map(ResourceKey::location)
                    .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                    .orElseGet(() -> DataResult.error(() -> "unkeyed biome holder"));
        }
    };

    private record Raw(
            Optional<Holder<Biome>> biome,
            Optional<TagKey<Biome>> tag,
            boolean invert
    ) {
        private Raw {
            biome = biome != null ? biome : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
        }
    }

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    BIOME_HOLDER_CODEC,
                    RecordCodecBuilder.<Raw>create(instance -> instance.group(
                            BIOME_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.BIOME).forGetter(Raw::biome),
                            TagKey.codec(Registries.BIOME).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag),
                            Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(Raw::invert)
                    ).apply(instance, Raw::new))
            ).xmap(
                    either -> either.map(
                            biome -> new Raw(Optional.of(biome), Optional.empty(), false),
                            raw -> raw
                    ),
                    raw -> {
                        if (raw.biome().isPresent() && raw.tag().isEmpty() && !raw.invert()) {
                            return Either.left(raw.biome().orElseThrow());
                        }
                        return Either.right(raw);
                    }
            );

    public static final Codec<BiomeCondition> CODEC =
            RAW_CODEC.flatXmap(
                    BiomeCondition::fromRaw,
                    value -> DataResult.success(BiomeCondition.toRaw(value))
            );

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
                    ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.BIOME)), BiomeCondition::biome,
                    OPTIONAL_RL_STREAM, BiomeCondition::tagLocation,
                    ByteBufCodecs.BOOL, BiomeCondition::invert,
                    BiomeCondition::fromStreamFields
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public BiomeCondition {
        biome = biome != null ? biome : Optional.empty();
        tag = tag != null ? tag : Optional.empty();
    }

    private static @NotNull DataResult<BiomeCondition> fromRaw(@NotNull Raw raw) {
        BiomeCondition c = new BiomeCondition(raw.biome(), raw.tag(), raw.invert());
        return validateDecoded(c);
    }

    private static @NotNull Raw toRaw(@NotNull BiomeCondition c) {
        return new Raw(c.biome(), c.tag(), c.invert());
    }

    private static @NotNull DataResult<BiomeCondition> validateDecoded(@NotNull BiomeCondition c) {
        boolean hasBiome = c.biome().isPresent();
        boolean hasTag = c.tag().isPresent();

        if (hasBiome == hasTag) {
            return DataResult.error(() ->
                    "BiomeCondition requires exactly one of '" + JolCraftParameterIds.BIOME +
                            "' or '" + JolCraftParameterIds.TAG + "'"
            );
        }

        return DataResult.success(c);
    }

    private static @NotNull BiomeCondition fromStreamFields(
            @NotNull Optional<Holder<Biome>> biome,
            @NotNull Optional<ResourceLocation> tagLoc,
            boolean invert
    ) {
        return new BiomeCondition(
                biome,
                tagLoc.map(loc -> TagKey.create(Registries.BIOME, loc)),
                invert
        );
    }

    private @NotNull Optional<ResourceLocation> tagLocation() {
        return tag.map(TagKey::location);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return fromConcreteOrTag(Registries.BIOME, biome, tag);
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

        if (biome.isPresent()) {
            Optional<ResourceLocation> wantedKey = biome.flatMap(h -> h.unwrapKey().map(ResourceKey::location));
            return wantedKey.flatMap(wk -> hereKey.map(hk -> hk.equals(wk))).orElse(false);
        }

        return tag.map(holder::is).orElse(false);
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}