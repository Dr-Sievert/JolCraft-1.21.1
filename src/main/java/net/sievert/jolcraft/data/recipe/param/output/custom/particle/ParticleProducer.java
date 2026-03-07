package net.sievert.jolcraft.data.recipe.param.output.custom.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Structural particle type handle.
 *
 * - Introspectable: reports PARTICLE_TYPE registry usage.
 * - Runtime creation of ParticleOptions is owned by ParticleSpec (payload may vary per type).
 */
public final class ParticleProducer implements SelfValidating<ParticleProducer>, RegistryIntrospectable {

    private static final ResourceLocation INVALID_ID =
            JolCraft.location(JolCraftDictionary.INVALID);

    public static final ParticleProducer EMPTY =
            new ParticleProducer(null, INVALID_ID);

    private static final Codec<Holder<ParticleType<?>>> PARTICLE_TYPE_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.PARTICLE_TYPE);

    private static final Codec<ParticleProducer> RAW_CODEC =
            PARTICLE_TYPE_HOLDER_CODEC
                    .xmap(ParticleProducer::of, p -> p.type);

    public static final Codec<ParticleProducer> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<ParticleType<?>>> PARTICLE_TYPE_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.PARTICLE_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleProducer> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        Holder<ParticleType<?>> h = v.type;
                        buf.writeBoolean(h != null);
                        if (h != null) {
                            PARTICLE_TYPE_HOLDER_STREAM.encode(buf, h);
                        } else {
                            buf.writeResourceLocation(v.invalidReasonId);
                        }
                    },
                    buf -> {
                        boolean present = buf.readBoolean();
                        if (present) {
                            Holder<ParticleType<?>> h = PARTICLE_TYPE_HOLDER_STREAM.decode(buf);
                            return of(h);
                        }
                        ResourceLocation reason = buf.readResourceLocation();
                        return invalid(reason);
                    }
            );

    private final @Nullable Holder<ParticleType<?>> type;
    private final @NotNull ResourceLocation invalidReasonId;

    private ParticleProducer(@Nullable Holder<ParticleType<?>> type, @NotNull ResourceLocation invalidReasonId) {
        this.type = type;
        this.invalidReasonId = invalidReasonId;
    }

    public static @NotNull ParticleProducer of(@Nullable Holder<ParticleType<?>> type) {
        return type == null ? EMPTY : new ParticleProducer(type, INVALID_ID);
    }

    public static @NotNull ParticleProducer invalid(@Nullable ResourceLocation reasonId) {
        return new ParticleProducer(null, reasonId != null ? reasonId : INVALID_ID);
    }

    @Override
    public @NotNull DataResult<ParticleProducer> validate() {
        if (type == null) {
            return DataResult.error(() -> "invalid particle producer (reason=" + invalidReasonId + ")");
        }
        return DataResult.success(this);
    }

    public @Nullable Holder<ParticleType<?>> typeHolder() {
        return type;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        if (type == null) {
            return RegistryIntrospection.mixed(Registries.PARTICLE_TYPE, 0, false);
        }
        return RegistryIntrospection.single(Registries.PARTICLE_TYPE, type);
    }
}