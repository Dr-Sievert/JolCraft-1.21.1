package net.sievert.jolcraft.world.recipe.param.input.custom.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.input.base.InputParam;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.requirement.EntityRequirements;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.selector.EntitySelector;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record EntityInput(
        @Nullable Conditions conditions,
        @NotNull EntitySelector selector,
        @NotNull IntRange count,
        @Nullable EntityRequirements requirements
) implements InputParam<EntityInput, Entity>, ConditionGate, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ENTITY, JolCraftParameterIds.INPUT));
    public static final byte DISC = 2;

    private static final EntityRequirements EMPTY_REQUIREMENTS = EntityRequirements.EMPTY;

    private record FullRaw(
            Conditions conditions,
            EntitySelector selector,
            IntRange count,
            EntityRequirements requirements
    ) {}

    private static final Codec<FullRaw> FULL_CODEC =
            RecordCodecBuilder.<FullRaw>create(instance -> instance.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(FullRaw::conditions),
                    EntitySelector.CODEC.fieldOf(JolCraftParameterIds.SELECTOR).forGetter(FullRaw::selector),
                    IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE).forGetter(FullRaw::count),
                    EntityRequirements.CODEC.optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS).forGetter(FullRaw::requirements)
            ).apply(instance, FullRaw::new));

    public static final Codec<EntityInput> CODEC =
            ParamCodecContract.create(
                    Codec.either(EntitySelector.CODEC, FULL_CODEC),
                    EntityInput::fromRaw,
                    EntityInput::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityInput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeBoolean(value.rawConditions() != null);
                        if (value.rawConditions() != null) {
                            Conditions.STREAM_CODEC.encode(buf, value.rawConditions());
                        }

                        EntitySelector.STREAM_CODEC.encode(buf, value.selector());
                        IntRange.STREAM_CODEC.encode(buf, value.count());

                        buf.writeBoolean(value.rawRequirements() != null);
                        if (value.rawRequirements() != null) {
                            EntityRequirements.STREAM_CODEC.encode(buf, value.rawRequirements());
                        }
                    },
                    buf -> {
                        Conditions conditions = buf.readBoolean()
                                ? Conditions.STREAM_CODEC.decode(buf)
                                : null;

                        EntitySelector selector = EntitySelector.STREAM_CODEC.decode(buf);
                        IntRange count = IntRange.STREAM_CODEC.decode(buf);

                        EntityRequirements requirements = buf.readBoolean()
                                ? EntityRequirements.STREAM_CODEC.decode(buf)
                                : null;

                        return new EntityInput(conditions, selector, count, requirements);
                    }
            );

    public static final ParamTypeDef<InputParam<?, ?>> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public EntityInput {
        Objects.requireNonNull(selector, JolCraftDictionary.SELECTOR);
        Objects.requireNonNull(count, JolCraftDictionary.COUNT);
    }

    private static @NotNull DataResult<EntityInput> fromRaw(@NotNull Either<EntitySelector, FullRaw> raw) {
        return DataResult.success(raw.map(
                selector -> new EntityInput(null, selector, IntRange.ONE, null),
                full -> new EntityInput(
                        full.conditions() == Conditions.EMPTY ? null : full.conditions(),
                        full.selector(),
                        full.count(),
                        full.requirements() == EMPTY_REQUIREMENTS ? null : full.requirements()
                )
        )).flatMap(EntityInput::validate);
    }

    private static @NotNull Either<EntitySelector, FullRaw> toRaw(@NotNull EntityInput input) {
        if (input.conditions() == Conditions.EMPTY
                && input.count().equals(IntRange.ONE)
                && (input.requirements == null || input.requirements.equals(EMPTY_REQUIREMENTS))) {
            return Either.left(input.selector());
        }

        return Either.right(new FullRaw(
                input.conditions(),
                input.selector(),
                input.count(),
                input.requirements == null ? EMPTY_REQUIREMENTS : input.requirements
        ));
    }

    private @Nullable Conditions rawConditions() {
        return conditions;
    }

    private @Nullable EntityRequirements rawRequirements() {
        return requirements;
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    private @Nullable EntityRequirements requirementsOrNull() {
        return requirements;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @Nullable Entity subject) {
        if (subject == null) return false;
        if (!gatePasses(ctx)) return false;
        if (!selector.matches(ctx, subject)) return false;

        EntityRequirements req = requirementsOrNull();
        if (req != null && !req.matches(ctx, subject)) {
            return false;
        }

        return count.isPositiveRange();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(
                requirementsOrNull() == null
                        ? List.of(selector)
                        : List.of(selector, requirementsOrNull())
        );
    }

    @Override
    public @NotNull DataResult<EntityInput> validate() {
        if (conditions != null) {
            DataResult<Conditions> cv = conditions.validate();
            if (cv.error().isPresent()) {
                return SelfValidating.invalid(
                        JolCraftParameterIds.CONDITIONS + ": " +
                                cv.error().map(DataResult.Error::message).orElse("")
                );
            }
        }

        DataResult<EntitySelector> sv = selector.validate();
        if (sv.error().isPresent()) {
            return SelfValidating.invalid(
                    JolCraftParameterIds.SELECTOR + ": " +
                            sv.error().map(DataResult.Error::message).orElse("")
            );
        }

        DataResult<IntRange> countRes = IntRange.validateRange(count);
        if (countRes.error().isPresent()) {
            return SelfValidating.invalid(
                    JolCraftParameterIds.COUNT + ": " +
                            countRes.error().map(DataResult.Error::message).orElse("")
            );
        }

        if (requirements != null) {
            DataResult<EntityRequirements> reqRes = requirements.validate();
            if (reqRes.error().isPresent()) {
                return SelfValidating.invalid(
                        JolCraftParameterIds.REQUIREMENTS + ": " +
                                reqRes.error().map(DataResult.Error::message).orElse("")
                );
            }
        }

        if (!count.isPositiveRange()) {
            return ParamValidations.invalid(
                    JolCraftParameterIds.COUNT + ": invalid count range"
            );
        }

        return SelfValidating.ok(this);
    }
}