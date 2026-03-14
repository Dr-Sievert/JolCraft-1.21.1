package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PlayerLevelCondition(int minLevel, Optional<Integer> maxLevel, boolean invert) implements Condition {

    public static final String KEY_PLAYER_LEVEL = JolCraftStrings.underscored(JolCraftParameterIds.PLAYER, JolCraftParameterIds.LEVEL);

    public static final ResourceLocation TYPE_ID = JolCraft.location(KEY_PLAYER_LEVEL);
    public static final byte DISC = 6;

    private static final Codec<PlayerLevelCondition> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_LEVEL, 0)
                            .forGetter(PlayerLevelCondition::minLevel),
                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MAX_LEVEL)
                            .forGetter(PlayerLevelCondition::maxLevel),
                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.INVERT, false)
                            .forGetter(PlayerLevelCondition::invert)
            ).apply(inst, PlayerLevelCondition::new));

    public static final Codec<PlayerLevelCondition> CODEC =
            RAW_CODEC.flatXmap(PlayerLevelCondition::validateDecoded, DataResult::success);

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerLevelCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        buf.writeVarInt(c.minLevel());
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT).encode(buf, c.maxLevel());
                        buf.writeBoolean(c.invert());
                    },
                    buf -> new PlayerLevelCondition(
                            buf.readVarInt(),
                            ByteBufCodecs.optional(ByteBufCodecs.VAR_INT).decode(buf),
                            buf.readBoolean()
                    )
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public PlayerLevelCondition {
        maxLevel = maxLevel != null ? maxLevel : Optional.empty();
    }

    private static @NotNull DataResult<PlayerLevelCondition> validateDecoded(@NotNull PlayerLevelCondition c) {
        if (c.minLevel() < 0) {
            return DataResult.error(() ->
                    KEY_PLAYER_LEVEL + "." + JolCraftParameterIds.MIN_LEVEL +
                            " must be >= 0 (got " + c.minLevel() + ")"
            );
        }

        if (c.maxLevel().isPresent()) {
            int max = c.maxLevel().get();

            if (max < 0) {
                return DataResult.error(() ->
                        KEY_PLAYER_LEVEL + "." + JolCraftParameterIds.MAX_LEVEL +
                                " must be >= 0 (got " + max + ")"
                );
            }

            if (c.minLevel() > max) {
                return DataResult.error(() ->
                        KEY_PLAYER_LEVEL + "." + JolCraftParameterIds.MIN_LEVEL +
                                " must be <= " + JolCraftParameterIds.MAX_LEVEL +
                                " (got " + c.minLevel() + " > " + max + ")"
                );
            }
        }

        return DataResult.success(c);
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        Player player = ctx.player();
        if (player == null) return false;
        if (player.isCreative()) return true;

        int lvl = player.experienceLevel;

        boolean pass = maxLevel
                .map(max -> lvl >= minLevel && lvl <= max)
                .orElse(lvl >= minLevel);

        return invert != pass;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(v -> v);
    }
}