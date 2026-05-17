package net.sievert.jolcraft.param.custom.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.base.ConditionParam;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Optional;

public record PlayerLevelCondition(
        int minLevel,
        Optional<Integer> maxLevel
) implements ConditionParam, ParamData<PlayerLevelCondition> {

    public static final String KEY = JolCraftStrings.underscored(JolCraftParameterIds.PLAYER, JolCraftParameterIds.LEVEL);

    public PlayerLevelCondition {
        maxLevel = maxLevel != null ? maxLevel : Optional.empty();
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public boolean matches(WorldContext ctx) {
        Player player = ctx.player();
        if (player == null) return false;
        if (player.isCreative()) return true;

        int level = player.experienceLevel;

        return maxLevel
                .map(max -> level >= minLevel && level <= max)
                .orElse(level >= minLevel);
    }

    @Override
    public Codec<PlayerLevelCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PlayerLevelCondition> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<PlayerLevelCondition> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.nonNegative(this, minLevel, KEY + "." + JolCraftParameterIds.MIN_LEVEL),
                () -> maxLevel
                        .map(max -> ParamValidations.nonNegative(this, max, KEY + "." + JolCraftParameterIds.MAX_LEVEL))
                        .orElseGet(() -> ParamValidations.ok(this)),
                () -> maxLevel
                        .map(max -> ParamValidations.minMax(this, minLevel, max, KEY))
                        .orElseGet(() -> ParamValidations.ok(this))
        );
    }

    public static final Codec<PlayerLevelCondition> CODEC =
            ParamCodecs.validated(
                    RecordCodecBuilder.create(inst -> inst.group(
                            Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_LEVEL, 0)
                                    .forGetter(PlayerLevelCondition::minLevel),
                            Codec.INT.optionalFieldOf(JolCraftParameterIds.MAX_LEVEL)
                                    .forGetter(PlayerLevelCondition::maxLevel)
                    ).apply(inst, PlayerLevelCondition::new)),
                    PlayerLevelCondition::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerLevelCondition> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, c) -> {
                        buf.writeVarInt(c.minLevel());
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT).encode(buf, c.maxLevel());
                    },
                    buf -> new PlayerLevelCondition(
                            buf.readVarInt(),
                            ByteBufCodecs.optional(ByteBufCodecs.VAR_INT).decode(buf)
                    )
            ), PlayerLevelCondition::validate);
}