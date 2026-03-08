package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

public record TimeCondition(String mode, int min, int max, boolean invert) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.TIME);
    public static final byte DISC = 3;

    private static final int DAY_TICKS = 24000;
    private static final String MODE = JolCraftParameterIds.MODE;

    public static final String MODE_RANGE = JolCraftParameterIds.RANGE;
    public static final String MODE_DAY = JolCraftDictionary.DAY;
    public static final String MODE_NIGHT = JolCraftDictionary.NIGHT;

    private static final int DAY_MIN = 0;
    private static final int DAY_MAX = 11999;
    private static final int NIGHT_MIN = 12000;
    private static final int NIGHT_MAX = 23999;

    private static final int MISSING = -1;

    private static final Codec<TimeCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.STRING.optionalFieldOf(MODE, MODE_RANGE).forGetter(TimeCondition::modeSafe),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN, MISSING).forGetter(TimeCondition::min),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MAX, MISSING).forGetter(TimeCondition::max),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(TimeCondition::invert)
            ).apply(inst, TimeCondition::new));

    public static final Codec<TimeCondition> CODEC =
            RAW_CODEC.flatXmap(TimeCondition::validateDecoded, DataResult::success);

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
                            return new TimeCondition(MODE_DAY, DAY_MIN, DAY_MAX, buf.readBoolean());
                        }
                        if (kind == KIND_NIGHT) {
                            return new TimeCondition(MODE_NIGHT, NIGHT_MIN, NIGHT_MAX, buf.readBoolean());
                        }
                        if (kind == KIND_RANGE) {
                            int min = buf.readVarInt();
                            int max = buf.readVarInt();
                            boolean inv = buf.readBoolean();
                            return new TimeCondition(MODE_RANGE, min, max, inv);
                        }

                        throw new IllegalArgumentException("Unknown TimeCondition stream kind: " + kind);
                    }
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static DataResult<TimeCondition> validateDecoded(TimeCondition c) {
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

        return DataResult.error(() ->
                "time." + MODE + " must be one of [" + MODE_RANGE + "," + MODE_DAY + "," + MODE_NIGHT + "] (got " + m + ")"
        );
    }

    private String modeSafe() {
        String m = mode == null ? "" : mode.trim();
        return m.isEmpty() ? MODE_RANGE : m;
    }

    private static boolean invalidTime(int t) {
        return t < 0 || t >= DAY_TICKS;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        String m = modeSafe();
        int t = (int) (ctx.level().getDayTime() % DAY_TICKS);

        if (MODE_RANGE.equals(m)) {
            if (min == MISSING || max == MISSING) return false;
            if (invalidTime(min) || invalidTime(max)) return false;

            boolean pass = (min <= max)
                    ? (t >= min && t <= max)
                    : (t >= min || t <= max);

            return invert != pass;
        }

        if (MODE_DAY.equals(m)) {
            return invert != (t >= DAY_MIN && t <= DAY_MAX);
        }

        if (MODE_NIGHT.equals(m)) {
            return invert != t >= NIGHT_MIN;
        }

        return false;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}