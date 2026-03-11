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

public record TimeCondition(TimeCondition.Mode mode, int min, int max, boolean invert) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.TIME);
    public static final byte DISC = 3;

    private static final int DAY_TICKS = 24000;

    private static final int DAY_MIN = 0;
    private static final int DAY_MAX = 11999;
    private static final int NIGHT_MIN = 12000;
    private static final int NIGHT_MAX = 23999;

    public enum Mode {
        RANGE(JolCraftParameterIds.RANGE),
        DAY(JolCraftDictionary.DAY),
        NIGHT(JolCraftDictionary.NIGHT);

        private final String id;

        Mode(String id) {
            this.id = id;
        }

        public @NotNull String id() {
            return id;
        }

        public static @NotNull DataResult<Mode> fromId(@NotNull String id) {
            for (Mode mode : values()) {
                if (mode.id.equals(id)) {
                    return DataResult.success(mode);
                }
            }
            return DataResult.error(() ->
                    "time." + JolCraftParameterIds.MODE + " must be one of ["
                            + JolCraftParameterIds.RANGE + ","
                            + JolCraftDictionary.DAY + ","
                            + JolCraftDictionary.NIGHT + "] (got " + id + ")"
            );
        }
    }

    private static final Codec<Mode> MODE_CODEC =
            Codec.STRING.comapFlatMap(Mode::fromId, Mode::id);

    private static final Codec<TimeCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    MODE_CODEC.optionalFieldOf(JolCraftParameterIds.MODE, Mode.RANGE).forGetter(TimeCondition::mode),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN, DAY_MIN).forGetter(TimeCondition::min),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MAX, DAY_MAX).forGetter(TimeCondition::max),
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
                        if (c.mode() == Mode.DAY) {
                            buf.writeByte(KIND_DAY);
                            buf.writeBoolean(c.invert());
                            return;
                        }

                        if (c.mode() == Mode.NIGHT) {
                            buf.writeByte(KIND_NIGHT);
                            buf.writeBoolean(c.invert());
                            return;
                        }

                        buf.writeByte(KIND_RANGE);
                        buf.writeVarInt(c.min());
                        buf.writeVarInt(c.max());
                        buf.writeBoolean(c.invert());
                    },
                    buf -> {
                        byte kind = buf.readByte();

                        if (kind == KIND_DAY) {
                            return new TimeCondition(Mode.DAY, DAY_MIN, DAY_MAX, buf.readBoolean());
                        }
                        if (kind == KIND_NIGHT) {
                            return new TimeCondition(Mode.NIGHT, NIGHT_MIN, NIGHT_MAX, buf.readBoolean());
                        }
                        if (kind == KIND_RANGE) {
                            int min = buf.readVarInt();
                            int max = buf.readVarInt();
                            boolean inv = buf.readBoolean();
                            return new TimeCondition(Mode.RANGE, min, max, inv);
                        }

                        throw new IllegalArgumentException("Unknown TimeCondition stream kind: " + kind);
                    }
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public TimeCondition {
        mode = mode != null ? mode : Mode.RANGE;
    }

    private static @NotNull DataResult<TimeCondition> validateDecoded(@NotNull TimeCondition c) {
        if (c.mode() == Mode.RANGE) {
            if (invalidTime(c.min())) {
                return DataResult.error(() ->
                        "time." + JolCraftParameterIds.MIN + " must be in range [0, 23999] (got " + c.min() + ")"
                );
            }
            if (invalidTime(c.max())) {
                return DataResult.error(() ->
                        "time." + JolCraftParameterIds.MAX + " must be in range [0, 23999] (got " + c.max() + ")"
                );
            }
            return DataResult.success(c);
        }

        if (c.mode() == Mode.DAY) {
            return DataResult.success(new TimeCondition(Mode.DAY, DAY_MIN, DAY_MAX, c.invert()));
        }

        if (c.mode() == Mode.NIGHT) {
            return DataResult.success(new TimeCondition(Mode.NIGHT, NIGHT_MIN, NIGHT_MAX, c.invert()));
        }

        return DataResult.error(() -> "invalid time mode");
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
        int t = (int) (ctx.level().getDayTime() % DAY_TICKS);

        if (mode == Mode.RANGE) {
            boolean pass = (min <= max)
                    ? (t >= min && t <= max)
                    : (t >= min || t <= max);

            return invert != pass;
        }

        if (mode == Mode.DAY) {
            return invert != (t >= DAY_MIN && t <= DAY_MAX);
        }

        if (mode == Mode.NIGHT) {
            return invert != (t >= NIGHT_MIN);
        }

        return false;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}