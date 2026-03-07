package net.sievert.jolcraft.data.recipe.param.quantity.draw;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DrawRule(
        IntRange rolls,
        Conditions conditions
) implements SelfValidating<DrawRule>, ConditionGate, RegistryIntrospectionSource {

    public DrawRule {
        rolls = (rolls != null) ? rolls : IntRange.ONE;
        conditions = (conditions != null) ? conditions : Conditions.EMPTY;
    }

    public boolean isSinglePick() {
        return rolls.isOne();
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    private static final Codec<DrawRule> OBJECT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE).forGetter(DrawRule::rolls),
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(DrawRule::conditions)
            ).apply(instance, DrawRule::new));

    private static final Codec<DrawRule> RAW_CODEC =
            Codec.either(Codec.INT, OBJECT_CODEC).xmap(
                    e -> e.map(i -> new DrawRule(IntRange.fixed(i), Conditions.EMPTY), r -> r),
                    r -> (r.conditions == Conditions.EMPTY && r.rolls.isFixed())
                            ? Either.left(r.rolls.min())
                            : Either.right(r)
            );

    public static final Codec<DrawRule> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, DrawRule> STREAM_CODEC =
            StreamCodec.composite(
                    IntRange.STREAM_CODEC, DrawRule::rolls,
                    Conditions.STREAM_CODEC, DrawRule::conditions,
                    DrawRule::new
            );

    @Override
    public @NotNull DataResult<DrawRule> validate() {
        return IntRange.validateRange(rolls)
                .mapError(msg -> JolCraftParameterIds.ROLLS + " invalid: " + msg)
                .flatMap(ignored -> conditions.validate()
                        .mapError(msg -> JolCraftParameterIds.CONDITIONS + " invalid: " + msg)
                        .map(ignored2 -> this));
    }

    public int draws(@NotNull WorldContext ctx) {
        if (!conditions.test(ctx)) return 0;
        return Math.max(0, rolls.roll(ctx.random()));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        Conditions c = (conditions != null) ? conditions : Conditions.EMPTY;
        return c.introspections();
    }
}