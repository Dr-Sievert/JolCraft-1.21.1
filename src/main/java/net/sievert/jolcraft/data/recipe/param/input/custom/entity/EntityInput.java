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
import java.util.Objects;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record EntityInput(
        @Nullable Conditions conditions,
        @NotNull EntitySelector selector,
        @NotNull IntRange count,
        @Nullable EntityRequirements requirements
) implements InputParam<EntityInput, Entity>, HasCount, ConditionGate, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ENTITY, JolCraftParameterIds.INPUT));
    public static final byte DISC = 2;

    private static final Codec<EntityInput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS)
                            .forGetter(value -> Optional.ofNullable(value.rawConditions())),
                    EntitySelector.CODEC.fieldOf(JolCraftParameterIds.SELECTOR)
                            .forGetter(EntityInput::selector),
                    IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(EntityInput::count),
                    EntityRequirements.CODEC.optionalFieldOf(JolCraftParameterIds.REQUIREMENTS)
                            .forGetter(value -> Optional.ofNullable(value.rawRequirements()))
            ).apply(instance, (conditions, selector, count, requirements) ->
                    new EntityInput(
                            conditions.orElse(null),
                            selector,
                            count,
                            requirements.orElse(null)
                    )));

    public static final Codec<EntityInput> CODEC = ParamCodecs.validated(RAW_CODEC);

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

                        return new EntityInput(
                                conditions,
                                selector,
                                count,
                                requirements
                        );
                    }
            );

    public static final ParamTypeDef<InputParam<?, ?>> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public EntityInput {
        Objects.requireNonNull(selector, JolCraftDictionary.SELECTOR);
        Objects.requireNonNull(count, JolCraftDictionary.COUNT);
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

        return hasValidCountRange();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospectionSource> src = new ArrayList<>(2);
        src.add(selector);

        EntityRequirements req = requirementsOrNull();
        if (req instanceof RegistryIntrospectionSource ris) {
            src.add(ris);
        }

        return RegistryIntrospectionSource.mergeByRegistry(src);
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

        if (!hasValidCountRange()) {
            return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": invalid count range");
        }

        return SelfValidating.ok(this);
    }
}