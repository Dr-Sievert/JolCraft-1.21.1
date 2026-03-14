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

public record ChanceCondition(double chance, boolean invert) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.CHANCE);
    public static final byte DISC = 1;

    private record Raw(double chance, boolean invert) {}

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.CHANCE).forGetter(Raw::chance),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false).forGetter(Raw::invert)
            ).apply(inst, Raw::new));

    public static final Codec<ChanceCondition> CODEC =
            RAW_CODEC.flatXmap(
                    ChanceCondition::fromRaw,
                    value -> DataResult.success(toRaw(value))
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ChanceCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeDouble(value.chance());
                        buf.writeBoolean(value.invert());
                    },
                    buf -> new ChanceCondition(buf.readDouble(), buf.readBoolean())
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static @NotNull DataResult<ChanceCondition> fromRaw(@NotNull Raw raw) {
        return validateDecoded(new ChanceCondition(raw.chance(), raw.invert()));
    }

    private static @NotNull Raw toRaw(@NotNull ChanceCondition value) {
        return new Raw(value.chance(), value.invert());
    }

    private static @NotNull DataResult<ChanceCondition> validateDecoded(@NotNull ChanceCondition value) {
        double chance = value.chance();
        if (Double.isNaN(chance)) {
            return DataResult.error(() -> "chance must not be NaN");
        }
        if (Double.isInfinite(chance)) {
            return DataResult.error(() -> "chance must be finite");
        }
        if (chance < 0.0D || chance > 1.0D) {
            return DataResult.error(() -> "chance must be in range [0.0, 1.0] (got " + chance + ")");
        }
        return DataResult.success(value);
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