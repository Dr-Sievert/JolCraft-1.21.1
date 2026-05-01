package net.sievert.jolcraft.world.recipe.param.quantity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record DrawRule(
        IntRange rolls,
        Conditions conditions
) implements SelfValidating<DrawRule>, ConditionGate, RegistryIntrospectionSource {

    private static final Set<String> RESERVED_KEYS = Set.of(
            JolCraftParameterIds.ROLLS,
            JolCraftParameterIds.CONDITIONS
    );

    private record Raw(IntRange rolls, Conditions conditions) {}

    public DrawRule {
        rolls = (rolls != null) ? rolls : IntRange.ONE;
        conditions = (conditions != null) ? conditions : Conditions.EMPTY;
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    public boolean isIdentity() {
        return rolls.isOne() && conditions.isEmpty();
    }

    public @NotNull DrawRule withMergedConditions(@NotNull Conditions extra) {
        if (extra.isEmpty()) {
            return this;
        }
        return new DrawRule(rolls, Conditions.merge(conditions, extra));
    }

    public @NotNull DrawRule withoutConditions() {
        return conditions.isEmpty() ? this : new DrawRule(rolls, Conditions.EMPTY);
    }

    private static final Codec<Raw> FULL_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE)
                            .forGetter(Raw::rolls),
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Raw::conditions)
            ).apply(instance, Raw::new));

    private static final Codec<Raw> OBJECT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Raw, T>> decode(DynamicOps<T> ops, T input) {
            return Conditions.extractInlineConditions(ops, input, RESERVED_KEYS).flatMap(extracted ->
                    FULL_CODEC.decode(ops, extracted.strippedInput()).flatMap(pair ->
                            Conditions.mergeExplicitAndInline(pair.getFirst().conditions(), extracted.conditions())
                                    .map(merged -> Pair.of(
                                            new Raw(pair.getFirst().rolls(), merged),
                                            pair.getSecond()
                                    ))
                    )
            );
        }

        @Override
        public <T> DataResult<T> encode(Raw input, DynamicOps<T> ops, T prefix) {
            T base = FULL_CODEC.encodeStart(ops, input).result().orElse(prefix);

            if (input.conditions() == null || input.conditions().isEmpty()) {
                return DataResult.success(base);
            }

            T noExplicit = ops.remove(base, JolCraftParameterIds.CONDITIONS);
            DataResult<T> flattened = Conditions.encodeInlineConditions(ops, input.conditions(), noExplicit, RESERVED_KEYS);
            return flattened.error().isPresent() ? DataResult.success(base) : flattened;
        }
    };

    private static final Codec<DrawRule> RAW_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<DrawRule, T>> decode(DynamicOps<T> ops, T input) {
            return Codec.either(Codec.INT, OBJECT_CODEC).decode(ops, input).map(pair -> Pair.of(
                    pair.getFirst().map(
                            i -> new DrawRule(IntRange.fixed(i), Conditions.EMPTY),
                            raw -> new DrawRule(raw.rolls(), raw.conditions())
                    ),
                    pair.getSecond()
            ));
        }

        @Override
        public <T> DataResult<T> encode(DrawRule input, DynamicOps<T> ops, T prefix) {
            if (input.conditions().isEmpty() && input.rolls().isFixed()) {
                return Codec.INT.encode(input.rolls().min(), ops, prefix);
            }

            return OBJECT_CODEC.encode(
                    new Raw(input.rolls(), input.conditions()),
                    ops,
                    prefix
            );
        }
    };

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
        return conditions.introspections();
    }
}