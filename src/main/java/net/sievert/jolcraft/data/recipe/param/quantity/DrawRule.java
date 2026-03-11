package net.sievert.jolcraft.data.recipe.param.quantity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DrawRule(
        IntRange rolls,
        Conditions conditions
) implements SelfValidating<DrawRule>, ConditionGate, RegistryIntrospectionSource {

    private record Raw(IntRange rolls, Conditions conditions) {}

    public DrawRule {
        rolls = (rolls != null) ? rolls : IntRange.ONE;
        conditions = (conditions != null) ? conditions : Conditions.EMPTY;
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    Codec.INT,
                    RecordCodecBuilder.<Raw>create(instance -> instance.group(
                            IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE).forGetter(Raw::rolls),
                            Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(Raw::conditions)
                    ).apply(instance, Raw::new))
            ).xmap(
                    either -> either.map(
                            i -> new Raw(IntRange.fixed(i), Conditions.EMPTY),
                            raw -> raw
                    ),
                    raw -> (raw.conditions() == Conditions.EMPTY && raw.rolls().isFixed())
                            ? Either.left(raw.rolls().min())
                            : Either.right(raw)
            );

    public static final Codec<DrawRule> CODEC =
            ParamCodecContract.<Raw, DrawRule>create(
                    RAW_CODEC,
                    raw -> DataResult.success(new DrawRule(raw.rolls(), raw.conditions())),
                    rule -> new Raw(rule.rolls(), rule.conditions())
            );

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
        return conditions.introspections();
    }
}