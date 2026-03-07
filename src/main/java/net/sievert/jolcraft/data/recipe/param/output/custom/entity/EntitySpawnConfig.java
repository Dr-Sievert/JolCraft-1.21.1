package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Optional spawn metadata for EntityOutput.
 *
 * Kept minimal and total-safe. Conditions are handled by outer param system.
 */
public record EntitySpawnConfig(
        WorldAnchor anchor,
        int offsetX,
        int offsetY,
        int offsetZ,
        int radius,
        boolean forced,
        boolean persistent,
        boolean noAi
) implements SelfValidating<EntitySpawnConfig> {

    public static final EntitySpawnConfig EMPTY =
            new EntitySpawnConfig(WorldAnchor.PLAYER, 0, 0, 0, 0, false, false, false);

    private static final Codec<EntitySpawnConfig> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    WorldAnchor.CODEC
                            .optionalFieldOf(JolCraftDictionary.ANCHOR, WorldAnchor.PLAYER)
                            .forGetter(EntitySpawnConfig::anchor),

                    Codec.INT.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "x"), 0).forGetter(EntitySpawnConfig::offsetX),
                    Codec.INT.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "y"), 0).forGetter(EntitySpawnConfig::offsetY),
                    Codec.INT.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.OFFSET, "z"), 0).forGetter(EntitySpawnConfig::offsetZ),

                    Codec.INT.optionalFieldOf(JolCraftDictionary.RADIUS, 0).forGetter(EntitySpawnConfig::radius),

                    Codec.BOOL.optionalFieldOf(JolCraftDictionary.FORCED, false).forGetter(EntitySpawnConfig::forced),
                    Codec.BOOL.optionalFieldOf(JolCraftDictionary.PERSISTENT, false).forGetter(EntitySpawnConfig::persistent),
                    Codec.BOOL.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.NO, JolCraftDictionary.AI), false).forGetter(EntitySpawnConfig::noAi)
            ).apply(inst, EntitySpawnConfig::new));

    public static final Codec<EntitySpawnConfig> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Integer> VAR_INT =
            StreamCodec.of(RegistryFriendlyByteBuf::writeVarInt, RegistryFriendlyByteBuf::readVarInt);

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySpawnConfig> STREAM_CODEC =
            StreamCodec.composite(
                    WorldAnchor.STREAM_CODEC, EntitySpawnConfig::anchor,
                    VAR_INT, EntitySpawnConfig::offsetX,
                    VAR_INT, EntitySpawnConfig::offsetY,
                    VAR_INT, EntitySpawnConfig::offsetZ,
                    VAR_INT, EntitySpawnConfig::radius,
                    StreamCodec.of(RegistryFriendlyByteBuf::writeBoolean, RegistryFriendlyByteBuf::readBoolean), EntitySpawnConfig::forced,
                    StreamCodec.of(RegistryFriendlyByteBuf::writeBoolean, RegistryFriendlyByteBuf::readBoolean), EntitySpawnConfig::persistent,
                    StreamCodec.of(RegistryFriendlyByteBuf::writeBoolean, RegistryFriendlyByteBuf::readBoolean), EntitySpawnConfig::noAi,
                    EntitySpawnConfig::new
            );

    public EntitySpawnConfig {
        anchor = anchor != null ? anchor : WorldAnchor.PLAYER;
    }

    @Override
    public @NotNull DataResult<EntitySpawnConfig> validate() {
        if (radius < 0) {
            return DataResult.error(() -> "radius must be >= 0 (got " + radius + ")");
        }
        if (anchor == null) {
            return DataResult.error(() -> "anchor is required");
        }
        return DataResult.success(this);
    }

    public static @Nullable EntitySpawnConfig normalize(@Nullable EntitySpawnConfig s) {
        if (s == null) return null;
        return s.equals(EMPTY) ? null : s;
    }
}