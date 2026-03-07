package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionTypes;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

/**
 * Atomic condition: time-of-day gate.
 *
 * Schema (mode-based):
 * - Range window (default):
 *   { "type": "jolcraft:time", "mode": "range", "min": 0, "max": 12000, "invert": false }
 *
 * - Presets:
 *   { "type": "jolcraft:time", "mode": "day", "invert": false }
 *   { "type": "jolcraft:time", "mode": "night", "invert": true }
 *
 * Notes:
 * - "type" is the Condition dispatch key (handled by ConditionTypes).
 * - This param introduces "mode" to select range vs preset.
 *
 * Invariants:
 * - mode in {"range","day","night"}
 * - for mode=range: min/max must be in [0, 23999]
 *
 * Runtime:
 * - invalid state -> false
 * - supports wrap-around for mode=range (min > max) spanning midnight
 */
public record TimeCondition(String mode, int min, int max, boolean invert) implements Condition {

    private static final int DAY_TICKS = 24000;

    private static final String MODE = JolCraftParameterIds.MODE;

    public static final String MODE_RANGE = JolCraftParameterIds.RANGE;
    public static final String MODE_DAY = JolCraftDictionary.DAY;
    public static final String MODE_NIGHT = JolCraftDictionary.NIGHT;

    private static final int DAY_MIN = 0;
    private static final int DAY_MAX = 11999;

    private static final int NIGHT_MIN = 12000;
    private static final int NIGHT_MAX = 23999;

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    /**
     * min/max are optional to support presets without noise.
     * For mode=range, validation enforces they were supplied (no sentinel).
     */
    private static final int MISSING = -1;

    private static final Codec<TimeCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.STRING
                            .optionalFieldOf(MODE, MODE_RANGE)
                            .forGetter(TimeCondition::modeSafe),

                    Codec.INT
                            .optionalFieldOf(JolCraftParameterIds.MIN, MISSING)
                            .forGetter(TimeCondition::min),

                    Codec.INT
                            .optionalFieldOf(JolCraftParameterIds.MAX, MISSING)
                            .forGetter(TimeCondition::max),

                    Codec.BOOL
                            .optionalFieldOf(JolCraftParameterIds.INVERT, false)
                            .forGetter(TimeCondition::invert)
            ).apply(inst, TimeCondition::new));

    public static final Codec<TimeCondition> CODEC =
            RAW_CODEC.flatXmap(TimeCondition::validateDecoded, DataResult::success);

    private static DataResult<TimeCondition> validateDecoded(TimeCondition c) {
        if (c == null) {
            return DataResult.error(() -> "time condition is null");
        }

        String m = c.modeSafe();

        if (MODE_RANGE.equals(m)) {
            if (c.min == MISSING) {
                return DataResult.error(() -> "time." + JolCraftParameterIds.MIN + " missing for mode=" + MODE_RANGE);
            }
            if (c.max == MISSING) {
                return DataResult.error(() -> "time." + JolCraftParameterIds.MAX + " missing for mode=" + MODE_RANGE);
            }
            if (invalidTime(c.min)) {
                return DataResult.error(() ->
                        "time." + JolCraftParameterIds.MIN + " must be in range [0, 23999] (got " + c.min + ")"
                );
            }
            if (invalidTime(c.max)) {
                return DataResult.error(() ->
                        "time." + JolCraftParameterIds.MAX + " must be in range [0, 23999] (got " + c.max + ")"
                );
            }
            return DataResult.success(new TimeCondition(MODE_RANGE, c.min, c.max, c.invert));
        }

        if (MODE_DAY.equals(m)) {
            return DataResult.success(new TimeCondition(MODE_DAY, DAY_MIN, DAY_MAX, c.invert));
        }

        if (MODE_NIGHT.equals(m)) {
            return DataResult.success(new TimeCondition(MODE_NIGHT, NIGHT_MIN, NIGHT_MAX, c.invert));
        }

        return DataResult.error(() -> "time." + MODE + " must be one of [" + MODE_RANGE + "," + MODE_DAY + "," + MODE_NIGHT + "] (got " + m + ")");
    }

    private String modeSafe() {
        String m = mode == null ? "" : mode.trim();
        return m.isEmpty() ? MODE_RANGE : m;
    }

    private static boolean invalidTime(int t) {
        return t < 0 || t >= DAY_TICKS;
    }

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final byte KIND_RANGE = 1;
    private static final byte KIND_DAY = 2;
    private static final byte KIND_NIGHT = 3;

    public static final StreamCodec<RegistryFriendlyByteBuf, TimeCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        String m = c.modeSafe();

                        if (MODE_DAY.equals(m)) {
                            buf.writeByte(KIND_DAY);
                            buf.writeBoolean(c.invert);
                            return;
                        }

                        if (MODE_NIGHT.equals(m)) {
                            buf.writeByte(KIND_NIGHT);
                            buf.writeBoolean(c.invert);
                            return;
                        }

                        buf.writeByte(KIND_RANGE);
                        buf.writeVarInt(c.min);
                        buf.writeVarInt(c.max);
                        buf.writeBoolean(c.invert);
                    },
                    buf -> {
                        byte kind = buf.readByte();

                        if (kind == KIND_DAY) {
                            boolean inv = buf.readBoolean();
                            return new TimeCondition(MODE_DAY, DAY_MIN, DAY_MAX, inv);
                        }

                        if (kind == KIND_NIGHT) {
                            boolean inv = buf.readBoolean();
                            return new TimeCondition(MODE_NIGHT, NIGHT_MIN, NIGHT_MAX, inv);
                        }

                        if (kind == KIND_RANGE) {
                            int min = buf.readVarInt();
                            int max = buf.readVarInt();
                            boolean inv = buf.readBoolean();
                            return new TimeCondition(MODE_RANGE, min, max, inv);
                        }

                        return new TimeCondition(MODE_RANGE, MISSING, MISSING, false);
                    }
            );

    // ---------------------------------------------------------------------
    // DISPATCH
    // ---------------------------------------------------------------------

    @Override
    public ResourceLocation typeId() {
        return ConditionTypes.TYPE_TIME;
    }

    // ---------------------------------------------------------------------
    // TEST
    // ---------------------------------------------------------------------

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        String m = modeSafe();

        if (MODE_RANGE.equals(m)) {
            if (min == MISSING || max == MISSING) {
                return false;
            }
            if (invalidTime(min) || invalidTime(max)) {
                return false;
            }

            int t = (int) (ctx.level().getDayTime() % DAY_TICKS);

            boolean pass;
            if (min <= max) {
                pass = t >= min && t <= max;
            } else {
                pass = t >= min || t <= max;
            }

            return invert != pass;
        }

        if (MODE_DAY.equals(m)) {
            int t = (int) (ctx.level().getDayTime() % DAY_TICKS);
            boolean pass = t >= DAY_MIN && t <= DAY_MAX;
            return invert != pass;
        }

        if (MODE_NIGHT.equals(m)) {
            int t = (int) (ctx.level().getDayTime() % DAY_TICKS);
            boolean pass = t >= NIGHT_MIN;
            return invert != pass;
        }

        return false;
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}