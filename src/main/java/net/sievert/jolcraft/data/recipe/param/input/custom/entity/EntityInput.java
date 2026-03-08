package net.sievert.jolcraft.data.recipe.param.input.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.input.base.InputParam;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement.EntityRequirements;
import net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector.EntitySelector;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.quantity.HasCount;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record EntityInput(
        Conditions conditions,
        EntitySelector selector,
        IntRange count,
        EntityRequirements requirements
) implements InputParam<EntityInput, Entity>, HasCount, ConditionGate, RegistryIntrospectionSource {

    public static final EntityInput EMPTY =
            new EntityInput(Conditions.EMPTY, EntitySelector.EMPTY, IntRange.ONE, EntityRequirements.EMPTY);

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ENTITY, JolCraftParameterIds.INPUT));

    public static final byte DISC = 2;

    private Conditions conditionsSafe() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditionsSafe();
    }

    private EntitySelector selectorSafe() {
        return selector != null ? selector : EntitySelector.EMPTY;
    }

    private IntRange countSafe() {
        return count != null ? count : IntRange.ONE;
    }

    private EntityRequirements requirementsSafe() {
        return requirements != null ? requirements : EntityRequirements.EMPTY;
    }

    private static final Codec<EntityInput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(EntityInput::conditionsSafe),
                    EntitySelector.CODEC
                            .fieldOf(JolCraftParameterIds.SELECTOR)
                            .forGetter(EntityInput::selectorSafe),
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(EntityInput::countSafe),
                    EntityRequirements.CODEC
                            .optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EntityRequirements.EMPTY)
                            .forGetter(EntityInput::requirementsSafe)
            ).apply(instance, EntityInput::new));

    public static final Codec<EntityInput> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityInput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        Conditions.STREAM_CODEC.encode(buf, value.conditionsSafe());
                        EntitySelector.STREAM_CODEC.encode(buf, value.selectorSafe());
                        IntRange.STREAM_CODEC.encode(buf, value.countSafe());
                        EntityRequirements.STREAM_CODEC.encode(buf, value.requirementsSafe());
                    },
                    buf -> new EntityInput(
                            Conditions.STREAM_CODEC.decode(buf),
                            EntitySelector.STREAM_CODEC.decode(buf),
                            IntRange.STREAM_CODEC.decode(buf),
                            EntityRequirements.STREAM_CODEC.decode(buf)
                    )
            );

    public static final ParamTypeDef<InputParam<?, ?>> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @Nullable Entity subject) {
        if (subject == null) return false;
        if (!gatePasses(ctx)) return false;
        if (!selectorSafe().matches(ctx, subject)) return false;
        if (!requirementsSafe().matches(ctx, subject)) return false;
        return hasValidCountRange();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(2);
        src.add(selectorSafe());
        src.add(requirementsSafe());
        return RegistryIntrospectionSource.mergeByRegistry(src);
    }

    @Override
    public @NotNull DataResult<EntityInput> validate() {
        if (conditions == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.CONDITIONS + "'");
        }
        if (selector == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }
        if (count == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.COUNT + "'");
        }
        if (requirements == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.REQUIREMENTS + "'");
        }

        DataResult<Conditions> cv = conditionsSafe().validate();
        if (cv.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + ": " +
                    cv.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<EntitySelector> sv = selectorSafe().validate();
        if (sv.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.SELECTOR + ": " +
                    sv.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<IntRange> countRes = IntRange.validateRange(countSafe());
        if (countRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": " +
                    countRes.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<EntityRequirements> reqRes = requirementsSafe().validate();
        if (reqRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.REQUIREMENTS + ": " +
                    reqRes.error().map(DataResult.Error::message).orElse(""));
        }

        if (!hasValidCountRange()) {
            return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": invalid count range");
        }

        return SelfValidating.ok(this);
    }
}
