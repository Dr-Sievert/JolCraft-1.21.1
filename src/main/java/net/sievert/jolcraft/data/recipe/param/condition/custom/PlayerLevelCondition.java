package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Atomic condition: player XP level gate.
 *
 * Schema:
 * { "type": "jolcraft:player_level", "min_level": 10, "max_level": 30, "invert": false }
 *
 * Invariants:
 * - min_level >= 0
 * - max_level (if present) >= 0
 * - if max_level present: min_level <= max_level
 */
public record PlayerLevelCondition(int minLevel, Optional<Integer> maxLevel, boolean invert) implements Condition {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<PlayerLevelCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_LEVEL, 0)
                            .forGetter(PlayerLevelCondition::minLevel),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MAX_LEVEL)
                            .forGetter(PlayerLevelCondition::maxLevelSafe),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false)
                            .forGetter(PlayerLevelCondition::invert)
            ).apply(inst, PlayerLevelCondition::new));

    public static final Codec<PlayerLevelCondition> CODEC =
            RAW_CODEC.flatXmap(
                    PlayerLevelCondition::validateDecoded,
                    DataResult::success
            );

    private static DataResult<PlayerLevelCondition> validateDecoded(PlayerLevelCondition c) {
        if (c == null) {
            return DataResult.error(() -> "player_level condition is null");
        }

        if (c.minLevel < 0) {
            return DataResult.error(() ->
                    "player_level." + JolCraftParameterIds.MIN_LEVEL + " must be >= 0 (got " + c.minLevel + ")"
            );
        }

        Optional<Integer> maxOpt = c.maxLevelSafe();
        if (maxOpt.isPresent()) {
            int max = maxOpt.orElse(-1);

            if (max < 0) {
                return DataResult.error(() ->
                        "player_level." + JolCraftParameterIds.MAX_LEVEL + " must be >= 0 (got " + max + ")"
                );
            }
            if (c.minLevel > max) {
                return DataResult.error(() ->
                        "player_level." + JolCraftParameterIds.MIN_LEVEL + " must be <= " +
                                JolCraftParameterIds.MAX_LEVEL + " (got " + c.minLevel + " > " + max + ")"
                );
            }
        }

        return DataResult.success(c);
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerLevelCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        Optional<Integer> max = c.maxLevel != null ? c.maxLevel : Optional.empty();

                        buf.writeVarInt(c.minLevel);

                        buf.writeBoolean(max.isPresent());
                        max.ifPresent(buf::writeVarInt);

                        buf.writeBoolean(c.invert);
                    },
                    buf -> {
                        int min = buf.readVarInt();

                        Optional<Integer> max = Optional.empty();
                        if (buf.readBoolean()) {
                            max = Optional.of(buf.readVarInt());
                        }

                        boolean inv = buf.readBoolean();
                        return new PlayerLevelCondition(min, max, inv);
                    }
            );

    // ---------------------------------------------------------------------
    // DISPATCH
    // ---------------------------------------------------------------------

    @Override
    public ResourceLocation typeId() {
        return ConditionTypes.TYPE_PLAYER_LEVEL;
    }

    // ---------------------------------------------------------------------
    // TEST
    // ---------------------------------------------------------------------

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        if (minLevel < 0) {
            return false;
        }

        int lvl = ctx.player().experienceLevel;

        boolean pass = maxLevelSafe()
                .map(max -> max >= 0 && lvl >= minLevel && lvl <= max)
                .orElse(lvl >= minLevel);

        return invert != pass;
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(c -> c);
    }

    // ---------------------------------------------------------------------
    // INTERNAL
    // ---------------------------------------------------------------------

    private Optional<Integer> maxLevelSafe() {
        return maxLevel != null ? maxLevel : Optional.empty();
    }
}