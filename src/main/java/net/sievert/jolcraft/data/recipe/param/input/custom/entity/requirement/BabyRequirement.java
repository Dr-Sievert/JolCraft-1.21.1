package net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import org.jetbrains.annotations.NotNull;

/**
 * Atomic entity requirement: checks {@link LivingEntity#isBaby()}.
 * JSON:
 * { "value": true }  // require baby
 * { "value": false } // require NOT baby
 */
public record BabyRequirement(boolean value) implements SelfValidating<BabyRequirement> {

    private static final Codec<BabyRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.fieldOf(JolCraftParameterIds.VALUE).forGetter(BabyRequirement::value)
            ).apply(instance, BabyRequirement::new));

    public static final Codec<BabyRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, BabyRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> buf.writeBoolean(req.value),
                    buf -> new BabyRequirement(buf.readBoolean())
            );

    @Override
    public @NotNull DataResult<BabyRequirement> validate() {
        return SelfValidating.ok(this);
    }

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        return living.isBaby() == value;
    }
}