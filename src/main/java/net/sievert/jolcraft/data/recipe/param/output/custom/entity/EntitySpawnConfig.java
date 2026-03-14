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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record EntitySpawnConfig(
        @Nullable BlockPos pos,
        int offsetX,
        int offsetY,
        int offsetZ,
        int radius,
        boolean forced,
        boolean persistent,
        boolean noAi
) implements SelfValidating<EntitySpawnConfig> {

    private static final Codec<EntitySpawnConfig> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    BlockPos.CODEC
                            .optionalFieldOf(JolCraftParameterIds.POSITION)
                            .forGetter(cfg -> Optional.ofNullable(cfg.pos())),

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
            ).apply(inst, (pos, offsetX, offsetY, offsetZ, radius, forced, persistent, noAi) ->
                    new EntitySpawnConfig(
                            pos.orElse(null),
                            offsetX,
                            offsetY,
                            offsetZ,
                            radius,
                            forced,
                            persistent,
                            noAi
                    )));

    public static final Codec<EntitySpawnConfig> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Integer> VAR_INT =
            StreamCodec.of(
                    RegistryFriendlyByteBuf::writeVarInt,
                    RegistryFriendlyByteBuf::readVarInt
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Boolean> BOOL =
            StreamCodec.of(
                    RegistryFriendlyByteBuf::writeBoolean,
                    RegistryFriendlyByteBuf::readBoolean
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<BlockPos>> OPTIONAL_BLOCK_POS =
            StreamCodec.of(
                    (buf, opt) -> {
                        buf.writeBoolean(opt.isPresent());
                        opt.ifPresent(pos -> BlockPos.STREAM_CODEC.encode(buf, pos));
                    },
                    buf -> buf.readBoolean()
                            ? Optional.of(BlockPos.STREAM_CODEC.decode(buf))
                            : Optional.empty()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySpawnConfig> STREAM_CODEC =
            StreamCodec.composite(
                    OPTIONAL_BLOCK_POS, cfg -> Optional.ofNullable(cfg.pos()),
                    VAR_INT, EntitySpawnConfig::offsetX,
                    VAR_INT, EntitySpawnConfig::offsetY,
                    VAR_INT, EntitySpawnConfig::offsetZ,
                    VAR_INT, EntitySpawnConfig::radius,
                    BOOL, EntitySpawnConfig::forced,
                    BOOL, EntitySpawnConfig::persistent,
                    BOOL, EntitySpawnConfig::noAi,
                    (pos, offsetX, offsetY, offsetZ, radius, forced, persistent, noAi) ->
                            new EntitySpawnConfig(
                                    pos.orElse(null),
                                    offsetX,
                                    offsetY,
                                    offsetZ,
                                    radius,
                                    forced,
                                    persistent,
                                    noAi
                            )
            );

    @Override
    public @NotNull DataResult<EntitySpawnConfig> validate() {
        if (radius < 0) {
            return DataResult.error(() ->
                    JolCraftDictionary.RADIUS + " must be >= 0 (got " + radius + ")");
        }

        return SelfValidating.ok(this);
    }
}