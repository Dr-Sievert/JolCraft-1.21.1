package net.sievert.jolcraft.data.recipe.param.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeRegistry;
import net.sievert.jolcraft.data.recipe.param.condition.custom.BiomeCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.ChanceCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.DimensionCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.PlayerLevelCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.TimeCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.WeatherCondition;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Atomic runtime condition.
 *
 * Strict polymorphic dispatch:
 * - no sentinels
 * - unknown type ids fail decode
 * - unknown stream discriminators fail decode
 */
public interface Condition extends SelfValidating<Condition>, RegistryIntrospectionSource {

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

    @NotNull ResourceLocation typeId();

    boolean test(@NotNull WorldContext ctx);

    default boolean invert() {
        return false;
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