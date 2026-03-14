package net.sievert.jolcraft.data.recipe.param.condition;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeRegistry;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.custom.BiomeCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.ChanceCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.DimensionCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.PlayerLevelCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.TimeCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.WeatherCondition;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Atomic runtime condition.
 *
 * Strict polymorphic dispatch:
 * - no sentinels
 * - unknown type ids fail decode
 * - unknown stream discriminators fail decode
 *
 * Also owns flattened condition field support used by container codecs
 * (Outputs / Pool / PoolEntry).
 */
public interface Condition extends SelfValidating<Condition>, RegistryIntrospectionSource {

    String KEY_CHANCE = JolCraftParameterIds.CHANCE;
    String KEY_WEATHER = JolCraftDictionary.WEATHER;
    String KEY_TIME = JolCraftDictionary.TIME;
    String KEY_DIMENSION = JolCraftDictionary.DIMENSION;
    String KEY_BIOME = JolCraftParameterIds.BIOME;
    String KEY_PLAYER_LEVEL = JolCraftStrings.underscored(JolCraftParameterIds.PLAYER, JolCraftParameterIds.LEVEL);

    ParamTypeRegistry<Condition> REGISTRY =
            ParamTypeRegistry.<Condition>builder()
                    .add(ChanceCondition.TYPE_DEF)
                    .add(WeatherCondition.TYPE_DEF)
                    .add(TimeCondition.TYPE_DEF)
                    .add(DimensionCondition.TYPE_DEF)
                    .add(BiomeCondition.TYPE_DEF)
                    .add(PlayerLevelCondition.TYPE_DEF)
                    .build();

    Codec<Condition> CODEC =
            REGISTRY.codec(JolCraftParameterIds.TYPE, Condition::typeId);

    StreamCodec<RegistryFriendlyByteBuf, Condition> STREAM_CODEC =
            REGISTRY.streamCodec(Condition::typeId);

    Set<String> FLAT_KEYS = Set.copyOf(new LinkedHashSet<>(List.of(
            KEY_CHANCE,
            KEY_WEATHER,
            KEY_TIME,
            KEY_DIMENSION,
            KEY_BIOME,
            KEY_PLAYER_LEVEL
    )));

    @NotNull ResourceLocation typeId();

    boolean test(@NotNull WorldContext ctx);

    default boolean invert() {
        return false;
    }

    static boolean isInlineConditionKey(@NotNull String key) {
        return FLAT_KEYS.contains(key);
    }

    static @NotNull Set<String> inlineConditionKeys() {
        return FLAT_KEYS;
    }

    static <T> @NotNull DataResult<Condition> decodeInlineField(
            @NotNull DynamicOps<T> ops,
            @NotNull String key,
            T value
    ) {
        switch (key) {
            case KEY_CHANCE -> {
                if (ops.getMap(value).result().isPresent()) {
                    return ChanceCondition.CODEC.parse(ops, value).map(c -> c);
                }
                return ChanceCondition.CODEC.parse(
                        ops,
                        ops.createMap(Stream.of(
                                Pair.of(ops.createString(KEY_CHANCE), value)
                        ))
                ).map(c -> c);
            }
            case KEY_BIOME -> {
                if (ops.getMap(value).result().isPresent()) {
                    return BiomeCondition.CODEC.parse(ops, value).map(c -> c);
                }
                return BiomeCondition.CODEC.parse(
                        ops,
                        ops.createMap(Stream.of(
                                Pair.of(ops.createString(KEY_BIOME), value)
                        ))
                ).map(c -> c);
            }
            case KEY_DIMENSION -> {
                if (ops.getMap(value).result().isPresent()) {
                    return DimensionCondition.CODEC.parse(ops, value).map(c -> c);
                }
                return DimensionCondition.CODEC.parse(
                        ops,
                        ops.createMap(Stream.of(
                                Pair.of(ops.createString(JolCraftParameterIds.ID), value)
                        ))
                ).map(c -> c);
            }
            case KEY_WEATHER -> {
                return WeatherCondition.CODEC.parse(ops, value).map(c -> c);
            }
            case KEY_TIME -> {
                return TimeCondition.CODEC.parse(ops, value).map(c -> c);
            }
        }

        if (KEY_PLAYER_LEVEL.equals(key)) {
            if (ops.getMap(value).result().isPresent()) {
                return PlayerLevelCondition.CODEC.parse(ops, value).map(c -> c);
            }
            return PlayerLevelCondition.CODEC.parse(
                    ops,
                    ops.createMap(Stream.of(
                            Pair.of(ops.createString(JolCraftParameterIds.MIN_LEVEL), value)
                    ))
            ).map(c -> c);
        }

        return DataResult.error(() ->
                "unknown inline condition key '" + key + "'"
        );
    }

    static <T> @NotNull DataResult<T> encodeInlineField(
            @NotNull DynamicOps<T> ops,
            @NotNull Condition condition
    ) {
        switch (condition) {
            case ChanceCondition c -> {
                if (!c.invert()) {
                    return Codec.DOUBLE.encodeStart(ops, c.chance());
                }
                return ChanceCondition.CODEC.encodeStart(ops, c);
            }
            case BiomeCondition c -> {
                if (c.biome().isPresent() && !c.invert()) {
                    return ResourceLocation.CODEC.encodeStart(
                            ops,
                            c.biome().orElseThrow().unwrapKey().orElseThrow().location()
                    );
                }
                return BiomeCondition.CODEC.encodeStart(ops, c);
            }
            case DimensionCondition c -> {
                if (c.dimension().isPresent() && !c.invert()) {
                    return ResourceLocation.CODEC.encodeStart(
                            ops,
                            c.dimension().orElseThrow().location()
                    );
                }
                return DimensionCondition.CODEC.encodeStart(ops, c);
            }
            case WeatherCondition c -> {
                return WeatherCondition.CODEC.encodeStart(ops, c);
            }
            case TimeCondition c -> {
                return TimeCondition.CODEC.encodeStart(ops, c);
            }
            case PlayerLevelCondition c -> {
                if (!c.invert() && c.maxLevel().isEmpty()) {
                    return Codec.INT.encodeStart(ops, c.minLevel());
                }
                return PlayerLevelCondition.CODEC.encodeStart(ops, c);
            }
            default -> {
            }
        }

        return DataResult.error(() ->
                "condition '" + condition.typeId() + "' is not inline-encodable"
        );
    }

    static @NotNull String inlineKey(@NotNull Condition condition) {
        return switch (condition) {
            case ChanceCondition chanceCondition -> KEY_CHANCE;
            case WeatherCondition weatherCondition -> KEY_WEATHER;
            case TimeCondition timeCondition -> KEY_TIME;
            case DimensionCondition dimensionCondition -> KEY_DIMENSION;
            case BiomeCondition biomeCondition -> KEY_BIOME;
            case PlayerLevelCondition playerLevelCondition -> KEY_PLAYER_LEVEL;
            default -> condition.typeId().getPath();
        };
    }

    @Override
    default @NotNull List<RegistryIntrospection> introspections() {
        return List.of();
    }

    @Override
    default @NotNull DataResult<Condition> validate() {
        return SelfValidating.ok(this);
    }
}