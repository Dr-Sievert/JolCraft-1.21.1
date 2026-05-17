package net.sievert.jolcraft.param.custom.quantity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.Conditions;
import net.sievert.jolcraft.param.runtime.WorldContext;

public record DrawRule(
        IntRange rolls,
        Conditions conditions
) implements ParamData<DrawRule> {

    public static final DrawRule ONE = new DrawRule(IntRange.ONE, Conditions.EMPTY);

    private record Raw(
            IntRange rolls,
            Conditions conditions
    ) {
        private Raw {
            rolls = rolls == null ? IntRange.ONE : rolls;
            conditions = conditions == null ? Conditions.EMPTY : conditions;
        }
    }

    public DrawRule {
        rolls = rolls == null ? IntRange.ONE : rolls;
        conditions = conditions == null ? Conditions.EMPTY : conditions;
    }

    public boolean isIdentity() {
        return rolls.isOne() && conditions.isEmpty();
    }

    public DrawRule withConditions(Conditions conditions) {
        return new DrawRule(rolls, conditions);
    }

    public DrawRule withoutConditions() {
        return conditions.isEmpty() ? this : new DrawRule(rolls, Conditions.EMPTY);
    }

    public int draws(WorldContext ctx) {
        if (!conditions.matches(ctx)) return 0;
        return Math.max(0, rolls.roll(ctx.random()));
    }

    @Override
    public DataResult<DrawRule> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.wrap(this, IntRange.validateRange(rolls), JolCraftParameterIds.ROLLS),
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS)
        );
    }

    private static final Codec<Raw> OBJECT_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ROLLS, IntRange.ONE)
                            .forGetter(Raw::rolls),
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Raw::conditions)
            ).apply(inst, Raw::new));

    private static final Codec<DrawRule> RAW_CODEC =
            ParamCodecs.either(
                    Codec.INT,
                    OBJECT_CODEC,
                    either -> ParamValidations.ok(either.map(
                            value -> new DrawRule(IntRange.fixed(value), Conditions.EMPTY),
                            raw -> new DrawRule(raw.rolls(), raw.conditions())
                    )),
                    rule -> ParamValidations.ok(rule.conditions().isEmpty() && rule.rolls().isFixed()
                            ? Either.left(rule.rolls().min())
                            : Either.right(new Raw(rule.rolls(), rule.conditions())))
            );

    public static final Codec<DrawRule> CODEC =
            ParamCodecs.validated(RAW_CODEC, DrawRule::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, DrawRule> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    IntRange.STREAM_CODEC,
                    DrawRule::rolls,
                    Conditions.STREAM_CODEC,
                    DrawRule::conditions,
                    DrawRule::new
            ), DrawRule::validate);

    @Override
    public Codec<DrawRule> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DrawRule> streamCodec() {
        return STREAM_CODEC;
    }
}