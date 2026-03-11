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

import java.util.Optional;

public record WeatherCondition(
        boolean requireRaining,
        boolean raining,
        boolean requireThundering,
        boolean thundering,
        boolean invert
) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(JolCraftDictionary.WEATHER);
    public static final byte DISC = 2;

    private static final Codec<WeatherCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.RAIN)
                            .forGetter(c -> c.requireRaining() ? Optional.of(c.raining()) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.THUNDER)
                            .forGetter(c -> c.requireThundering() ? Optional.of(c.thundering()) : Optional.empty()),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false)
                            .forGetter(WeatherCondition::invert)
            ).apply(inst, (optRain, optThunder, inv) -> new WeatherCondition(
                    optRain.isPresent(),
                    optRain.orElse(false),
                    optThunder.isPresent(),
                    optThunder.orElse(false),
                    inv
            )));

    public static final Codec<WeatherCondition> CODEC =
            RAW_CODEC.flatXmap(WeatherCondition::validateDecoded, DataResult::success);

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        buf.writeBoolean(c.requireRaining());
                        buf.writeBoolean(c.raining());
                        buf.writeBoolean(c.requireThundering());
                        buf.writeBoolean(c.thundering());
                        buf.writeBoolean(c.invert());
                    },
                    buf -> new WeatherCondition(
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static @NotNull DataResult<WeatherCondition> validateDecoded(@NotNull WeatherCondition c) {
        if (!c.requireRaining() && !c.requireThundering()) {
            return DataResult.error(() ->
                    "weather condition must specify at least one of '" +
                            JolCraftParameterIds.RAIN + "' or '" + JolCraftParameterIds.THUNDER + "'"
            );
        }
        return DataResult.success(c);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        if (!requireRaining && !requireThundering) {
            return false;
        }

        var level = ctx.level();
        boolean pass = true;

        if (requireRaining) {
            pass &= (level.isRaining() == raining);
        }
        if (requireThundering) {
            pass &= (level.isThundering() == thundering);
        }

        return invert != pass;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}