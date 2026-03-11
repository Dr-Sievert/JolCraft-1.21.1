package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.datafixers.util.Either;
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

public record ChanceCondition(double chance, boolean invert) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.CHANCE);
    public static final byte DISC = 1;

    private record Raw(double chance, boolean invert) {}

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    Codec.DOUBLE,
                    RecordCodecBuilder.<Raw>create(inst -> inst.group(
                            Codec.DOUBLE.fieldOf(JolCraftParameterIds.CHANCE).forGetter(Raw::chance),
                            Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(Raw::invert)
                    ).apply(inst, Raw::new))
            ).xmap(
                    either -> either.map(
                            chance -> new Raw(chance, false),
                            raw -> raw
                    ),
                    raw -> !raw.invert()
                            ? Either.left(raw.chance())
                            : Either.right(raw)
            );

    public static final Codec<ChanceCondition> CODEC =
            RAW_CODEC.flatXmap(
                    ChanceCondition::fromRaw,
                    value -> DataResult.success(ChanceCondition.toRaw(value))
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ChanceCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        buf.writeDouble(c.chance());
                        buf.writeBoolean(c.invert());
                    },
                    buf -> new ChanceCondition(buf.readDouble(), buf.readBoolean())
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static @NotNull DataResult<ChanceCondition> fromRaw(@NotNull Raw raw) {
        return validateDecoded(new ChanceCondition(raw.chance(), raw.invert()));
    }

    private static @NotNull Raw toRaw(@NotNull ChanceCondition c) {
        return new Raw(c.chance(), c.invert());
    }

    private static @NotNull DataResult<ChanceCondition> validateDecoded(@NotNull ChanceCondition c) {
        double v = c.chance();
        if (Double.isNaN(v)) return DataResult.error(() -> "chance must not be NaN");
        if (Double.isInfinite(v)) return DataResult.error(() -> "chance must be finite");
        if (v < 0.0D || v > 1.0D) {
            return DataResult.error(() -> "chance must be in range [0.0, 1.0] (got " + v + ")");
        }
        return DataResult.success(c);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        boolean pass = ctx.random().nextDouble() < chance;
        return invert != pass;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}