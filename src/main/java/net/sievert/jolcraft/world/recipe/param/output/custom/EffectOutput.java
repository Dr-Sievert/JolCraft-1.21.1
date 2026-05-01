package net.sievert.jolcraft.world.recipe.param.output.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.Output.EffectTarget;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record EffectOutput(
        @NotNull Holder<MobEffect> id,
        int duration,
        int amplifier,
        @NotNull EffectTarget target
) implements OutputParam, SelfValidating<EffectOutput>, RegistryIntrospectable {

    private static final Codec<Holder<MobEffect>> EFFECT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder<MobEffect>, T>> decode(
                com.mojang.serialization.DynamicOps<T> ops,
                T input
        ) {
            return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                ResourceLocation id = pair.getFirst();
                T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> registryOps)) {
                    return DataResult.error(() ->
                            "effect output requires RegistryOps for '" + Registries.MOB_EFFECT.location() + "'"
                    );
                }

                var lookupOpt = registryOps.lookupProvider.lookup(Registries.MOB_EFFECT);
                if (lookupOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" + Registries.MOB_EFFECT.location() + "'"
                    );
                }

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);
                var holderOpt = lookupOpt.get().getter().get(key);

                return holderOpt.<DataResult<Pair<Holder<MobEffect>, T>>>map(ref ->
                                DataResult.success(Pair.of(ref, rest)))
                        .orElseGet(() -> DataResult.error(() -> "unknown mob effect '" + id + "'"));
            });
        }

        @Override
        public <T> DataResult<T> encode(
                Holder<MobEffect> input,
                com.mojang.serialization.DynamicOps<T> ops,
                T prefix
        ) {
            if (input == null) {
                return DataResult.error(() -> "mob effect holder cannot be null");
            }

            return input.unwrapKey()
                    .map(ResourceKey::location)
                    .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                    .orElseGet(() -> DataResult.error(() -> "unkeyed mob effect holder"));
        }
    };

    private static final Codec<EffectTarget> TARGET_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> switch (s.toLowerCase()) {
                        case JolCraftParameterIds.PLAYER -> DataResult.success(EffectTarget.PLAYER);
                        case JolCraftParameterIds.ENTITY -> DataResult.success(EffectTarget.ENTITY);
                        default -> DataResult.error(() -> "Unknown effect target: " + s);
                    },
                    EffectTarget::getId
            );

    private static final Codec<EffectOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EFFECT_CODEC
                            .fieldOf(JolCraftDictionary.EFFECT)
                            .forGetter(EffectOutput::id),

                    Codec.INT
                            .fieldOf(JolCraftParameterIds.DURATION)
                            .forGetter(EffectOutput::duration),

                    Codec.INT
                            .optionalFieldOf(JolCraftParameterIds.AMPLIFIER, 0)
                            .forGetter(EffectOutput::amplifier),

                    TARGET_CODEC
                            .fieldOf(JolCraftParameterIds.TARGET)
                            .forGetter(EffectOutput::target)
            ).apply(instance, EffectOutput::new));

    public static final Codec<EffectOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<ByteBuf, EffectTarget> TARGET_STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    EffectTarget::byId,
                    EffectTarget::getId
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> EFFECT_STREAM_CODEC =
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT);

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        EFFECT_STREAM_CODEC.encode(buf, value.id());
                        buf.writeVarInt(value.duration());
                        buf.writeVarInt(value.amplifier());
                        TARGET_STREAM_CODEC.encode(buf, value.target());
                    },
                    buf -> new EffectOutput(
                            EFFECT_STREAM_CODEC.decode(buf),
                            buf.readVarInt(),
                            buf.readVarInt(),
                            TARGET_STREAM_CODEC.decode(buf)
                    )
            );

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.EFFECT,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 4;

    public static final ParamTypeDef<OutputParam> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public EffectOutput {
        Objects.requireNonNull(id, JolCraftDictionary.EFFECT);
        Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return RegistryIntrospection.single(Registries.MOB_EFFECT, id);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return List.of(
                new Output.Effects(
                        List.of(new Output.EffectSpec(id, duration, amplifier, target))
                )
        );
    }

    @Override
    public @NotNull DataResult<EffectOutput> validate() {
        if (duration < 1) {
            return SelfValidating.invalid(
                    "'" + JolCraftParameterIds.DURATION + "' must be >= 1"
            );
        }

        if (amplifier < 0) {
            return SelfValidating.invalid(
                    "'" + JolCraftParameterIds.AMPLIFIER + "' must be >= 0"
            );
        }

        return SelfValidating.ok(this);
    }
}