package net.sievert.jolcraft.world.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
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
                        BOOL.encode(buf, opt.isPresent());
                        opt.ifPresent(pos -> BlockPos.STREAM_CODEC.encode(buf, pos));
                    },
                    buf -> BOOL.decode(buf)
                            ? Optional.of(BlockPos.STREAM_CODEC.decode(buf))
                            : Optional.empty()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySpawnConfig> STREAM_CODEC =
            StreamCodec.of(
                    (buf, cfg) -> {
                        OPTIONAL_BLOCK_POS.encode(buf, Optional.ofNullable(cfg.pos()));
                        VAR_INT.encode(buf, cfg.offsetX());
                        VAR_INT.encode(buf, cfg.offsetY());
                        VAR_INT.encode(buf, cfg.offsetZ());
                        VAR_INT.encode(buf, cfg.radius());
                        BOOL.encode(buf, cfg.forced());
                        BOOL.encode(buf, cfg.persistent());
                        BOOL.encode(buf, cfg.noAi());
                    },
                    buf -> new EntitySpawnConfig(
                            OPTIONAL_BLOCK_POS.decode(buf).orElse(null),
                            VAR_INT.decode(buf),
                            VAR_INT.decode(buf),
                            VAR_INT.decode(buf),
                            VAR_INT.decode(buf),
                            BOOL.decode(buf),
                            BOOL.decode(buf),
                            BOOL.decode(buf)
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