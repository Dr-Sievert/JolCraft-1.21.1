package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

/**
 * Optional spawn metadata for EntityOutput.
 *
 * Kept minimal and total-safe. Conditions are handled by outer param system.
 */
public record EntitySpawnConfig(
        @NotNull BlockPos pos,
        int offsetX,
        int offsetY,
        int offsetZ,
        int radius,
        boolean forced,
        boolean persistent,
        boolean noAi
) implements SelfValidating<EntitySpawnConfig> {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<EntitySpawnConfig> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    BlockPos.CODEC
                            .fieldOf(JolCraftParameterIds.POSITION)
                            .forGetter(EntitySpawnConfig::pos),

                    Codec.INT.optionalFieldOf(
                            JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "x"), 0
                    ).forGetter(EntitySpawnConfig::offsetX),

                    Codec.INT.optionalFieldOf(
                            JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "y"), 0
                    ).forGetter(EntitySpawnConfig::offsetY),

                    Codec.INT.optionalFieldOf(
                            JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "z"), 0
                    ).forGetter(EntitySpawnConfig::offsetZ),

                    Codec.INT.optionalFieldOf(JolCraftDictionary.RADIUS, 0)
                            .forGetter(EntitySpawnConfig::radius),

                    Codec.BOOL.optionalFieldOf(JolCraftDictionary.FORCED, false)
                            .forGetter(EntitySpawnConfig::forced),

                    Codec.BOOL.optionalFieldOf(JolCraftDictionary.PERSISTENT, false)
                            .forGetter(EntitySpawnConfig::persistent),

                    Codec.BOOL.optionalFieldOf(
                            JolCraftStrings.underscored(JolCraftDictionary.NO, JolCraftDictionary.AI),
                            false
                    ).forGetter(EntitySpawnConfig::noAi)
            ).apply(inst, EntitySpawnConfig::new));

    public static final Codec<EntitySpawnConfig> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<RegistryFriendlyByteBuf, Integer> VAR_INT =
            StreamCodec.of(
                    RegistryFriendlyByteBuf::writeVarInt,
                    RegistryFriendlyByteBuf::readVarInt
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySpawnConfig> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, EntitySpawnConfig::pos,
                    VAR_INT, EntitySpawnConfig::offsetX,
                    VAR_INT, EntitySpawnConfig::offsetY,
                    VAR_INT, EntitySpawnConfig::offsetZ,
                    VAR_INT, EntitySpawnConfig::radius,
                    StreamCodec.of(
                            RegistryFriendlyByteBuf::writeBoolean,
                            RegistryFriendlyByteBuf::readBoolean
                    ), EntitySpawnConfig::forced,
                    StreamCodec.of(
                            RegistryFriendlyByteBuf::writeBoolean,
                            RegistryFriendlyByteBuf::readBoolean
                    ), EntitySpawnConfig::persistent,
                    StreamCodec.of(
                            RegistryFriendlyByteBuf::writeBoolean,
                            RegistryFriendlyByteBuf::readBoolean
                    ), EntitySpawnConfig::noAi,
                    EntitySpawnConfig::new
            );

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EntitySpawnConfig> validate() {

        if (radius < 0) {
            return DataResult.error(() ->
                    JolCraftDictionary.RADIUS + " must be >= 0 (got " + radius + ")");
        }

        return DataResult.success(this);
    }
}