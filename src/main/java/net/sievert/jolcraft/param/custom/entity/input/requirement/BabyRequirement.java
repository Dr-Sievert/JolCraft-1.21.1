package net.sievert.jolcraft.param.custom.entity.input.requirement;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;

public record BabyRequirement(boolean value) implements ParamData<BabyRequirement> {

    private static final Codec<BabyRequirement> RAW_CODEC =
            ParamCodecs.either(
                    Codec.BOOL,
                    Codec.BOOL.fieldOf(JolCraftParameterIds.VALUE).codec(),
                    either -> ParamValidations.ok(new BabyRequirement(either.map(v -> v, v -> v))),
                    req -> ParamValidations.ok(Either.left(req.value()))
            );

    public static final Codec<BabyRequirement> CODEC =
            ParamCodecs.validated(RAW_CODEC, BabyRequirement::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, BabyRequirement> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, req) -> buf.writeBoolean(req.value()),
                    buf -> new BabyRequirement(buf.readBoolean())
            ), BabyRequirement::validate);

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        return living.isBaby() == value;
    }

    @Override
    public DataResult<BabyRequirement> validate() {
        return ParamValidations.ok(this);
    }

    @Override
    public Codec<BabyRequirement> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BabyRequirement> streamCodec() {
        return STREAM_CODEC;
    }
}