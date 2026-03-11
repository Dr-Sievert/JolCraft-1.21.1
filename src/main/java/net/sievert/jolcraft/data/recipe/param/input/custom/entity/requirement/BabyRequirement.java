package net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import org.jetbrains.annotations.NotNull;

public record BabyRequirement(boolean value) implements SelfValidating<BabyRequirement> {

    private record Raw(boolean value) {}

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    Codec.BOOL,
                    Codec.BOOL.fieldOf(JolCraftParameterIds.VALUE).codec()
            ).xmap(
                    either -> either.map(Raw::new, Raw::new),
                    raw -> Either.left(raw.value())
            );

    public static final Codec<BabyRequirement> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    raw -> DataResult.success(new BabyRequirement(raw.value())),
                    req -> new Raw(req.value())
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, BabyRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> buf.writeBoolean(req.value()),
                    buf -> new BabyRequirement(buf.readBoolean())
            );

    @Override
    public @NotNull DataResult<BabyRequirement> validate() {
        return SelfValidating.ok(this);
    }

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        return living.isBaby() == value;
    }
}