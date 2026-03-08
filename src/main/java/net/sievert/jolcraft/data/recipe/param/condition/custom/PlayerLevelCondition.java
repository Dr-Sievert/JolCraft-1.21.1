package net.sievert.jolcraft.data.recipe.param.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PlayerLevelCondition(int minLevel, Optional<Integer> maxLevel, boolean invert) implements Condition {

    public static final ResourceLocation TYPE_ID = JolCraft.location(
            JolCraftStrings.underscored(JolCraftDictionary.PLAYER, JolCraftDictionary.LEVEL)
    );
    public static final byte DISC = 6;

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
            RAW_CODEC.flatXmap(PlayerLevelCondition::validateDecoded, DataResult::success);

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerLevelCondition> STREAM_CODEC =
            StreamCodec.of(
                    (buf, c) -> {
                        Optional<Integer> max = c.maxLevelSafe();
                        buf.writeVarInt(c.minLevel);
                        buf.writeBoolean(max.isPresent());
                        max.ifPresent(buf::writeVarInt);
                        buf.writeBoolean(c.invert);
                    },
                    buf -> {
                        int min = buf.readVarInt();
                        Optional<Integer> max = buf.readBoolean()
                                ? Optional.of(buf.readVarInt())
                                : Optional.empty();
                        boolean inv = buf.readBoolean();
                        return new PlayerLevelCondition(min, max, inv);
                    }
            );

    public static final ParamTypeDef<Condition> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    private static DataResult<PlayerLevelCondition> validateDecoded(PlayerLevelCondition c) {
        if (c.minLevel < 0) {
            return DataResult.error(() ->
                    "player_level." + JolCraftParameterIds.MIN_LEVEL + " must be >= 0 (got " + c.minLevel + ")"
            );
        }

        Optional<Integer> maxOpt = c.maxLevelSafe();
        if (maxOpt.isPresent()) {
            int max = maxOpt.get();

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

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean test(@NotNull WorldContext ctx) {
        if (minLevel < 0) return false;

        Player player = ctx.player();
        if (player == null) return false;

        int lvl = player.experienceLevel;

        boolean pass = maxLevelSafe()
                .map(max -> max >= 0 && lvl >= minLevel && lvl <= max)
                .orElse(lvl >= minLevel);

        return invert != pass;
    }

    @Override
    public @NotNull DataResult<Condition> validate() {
        return validateDecoded(this).map(c -> c);
    }

    private Optional<Integer> maxLevelSafe() {
        return maxLevel != null ? maxLevel : Optional.empty();
    }
}