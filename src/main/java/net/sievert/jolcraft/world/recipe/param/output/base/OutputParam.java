package net.sievert.jolcraft.world.recipe.param.output.base;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.Param;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeRegistry;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.TextOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.world.recipe.param.output.custom.particle.ParticleOutput;
import net.sievert.jolcraft.world.recipe.param.output.hook.Hook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface OutputParam extends Param {

    Set<String> DECORATION_RESERVED_KEYS = Set.of(
            JolCraftParameterIds.CONDITIONS,
            JolCraftParameterIds.HOOKS
    );

    ParamTypeRegistry<OutputParam> REGISTRY =
            ParamTypeRegistry.<OutputParam>builder()
                    .add(ItemOutput.TYPE_DEF)
                    .add(SoundOutput.TYPE_DEF)
                    .add(EffectOutput.TYPE_DEF)
                    .add(TextOutput.TYPE_DEF)
                    .add(ParticleOutput.TYPE_DEF)
                    .add(EntityOutput.TYPE_DEF)
                    .build();

    Codec<OutputParam> BASE_CODEC =
            REGISTRY.codec(JolCraftParameterIds.TYPE, OutputParam::typeId);

    StreamCodec<RegistryFriendlyByteBuf, OutputParam> BASE_STREAM_CODEC =
            REGISTRY.streamCodec(OutputParam::typeId);

    Codec<OutputParam> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<OutputParam, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Conditions> explicitConditions = decodeExplicitConditions(ops, input);
            if (explicitConditions.error().isPresent()) {
                return DataResult.error(() ->
                        explicitConditions.error().map(DataResult.Error::message).orElse("invalid output param conditions")
                );
            }

            DataResult<List<Hook>> hooksResult = decodeHooks(ops, input);
            if (hooksResult.error().isPresent()) {
                return DataResult.error(() ->
                        hooksResult.error().map(DataResult.Error::message).orElse("invalid output param hooks")
                );
            }

            T baseInput = stripDecorationFields(ops, input);

            return BASE_CODEC.decode(ops, baseInput).flatMap(basePair ->
                    attachDecorations(
                            basePair.getFirst(),
                            explicitConditions.result().orElse(Conditions.EMPTY),
                            hooksResult.result().orElse(List.of())
                    ).map(wrapped -> Pair.of(wrapped, basePair.getSecond()))
            );
        }

        @Override
        public <T> DataResult<T> encode(OutputParam input, DynamicOps<T> ops, T prefix) {
            if (input == null) {
                return DataResult.error(() -> "missing required output param");
            }

            OutputParam base = unwrap(input);
            if (base == null) {
                return DataResult.error(() -> "missing required output param");
            }

            Conditions conditions = sanitizeConditions(input.conditions());
            List<Hook> hooks = sanitizeHooks(input.hooks());

            DataResult<T> encodedBase = BASE_CODEC.encode(base, ops, prefix);
            if (encodedBase.error().isPresent()) {
                return encodedBase;
            }

            T result = encodedBase.result().orElse(prefix);

            if (!hooks.isEmpty()) {
                DataResult<T> hooksValue = Hook.CODEC.listOf().encodeStart(ops, hooks);
                if (hooksValue.error().isPresent()) {
                    return DataResult.error(() ->
                            hooksValue.error().map(DataResult.Error::message).orElse("invalid hooks")
                    );
                }

                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.HOOKS),
                        hooksValue.result().orElseThrow()
                ).result().orElse(result);
            }

            if (!conditions.isEmpty()) {
                DataResult<T> explicitConditions = Conditions.CODEC.encodeStart(ops, conditions);
                if (explicitConditions.error().isPresent()) {
                    return DataResult.error(() ->
                            explicitConditions.error().map(DataResult.Error::message).orElse("invalid output param conditions")
                    );
                }

                result = ops.mergeToMap(
                        result,
                        ops.createString(JolCraftParameterIds.CONDITIONS),
                        explicitConditions.result().orElseThrow()
                ).result().orElse(result);
            }

            return DataResult.success(result);
        }
    };

    StreamCodec<RegistryFriendlyByteBuf, OutputParam> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        OutputParam base = unwrap(value);
                        if (base == null) {
                            throw new IllegalArgumentException("missing required output param");
                        }

                        BASE_STREAM_CODEC.encode(buf, base);
                        Conditions.STREAM_CODEC.encode(buf, sanitizeConditions(value.conditions()));

                        List<Hook> hooks = sanitizeHooks(value.hooks());
                        int size = hooks.size();
                        if (size > 256) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.HOOKS + " size exceeds max 256 (got " + size + ")"
                            );
                        }

                        buf.writeVarInt(size);
                        for (Hook hook : hooks) {
                            Hook.STREAM_CODEC.encode(buf, hook);
                        }
                    },
                    buf -> {
                        OutputParam base = BASE_STREAM_CODEC.decode(buf);
                        Conditions conditions = Conditions.STREAM_CODEC.decode(buf);

                        int size = buf.readVarInt();
                        if (size < 0) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.HOOKS + " size must be >= 0 (got " + size + ")"
                            );
                        }
                        if (size > 256) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.HOOKS + " size exceeds max 256 (got " + size + ")"
                            );
                        }

                        ArrayList<Hook> hooks = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            hooks.add(Hook.STREAM_CODEC.decode(buf));
                        }

                        return attachDecorations(base, conditions, List.copyOf(hooks))
                                .getOrThrow(IllegalArgumentException::new);
                    }
            );

    @NotNull ResourceLocation typeId();

    @NotNull List<Output> generate(@NotNull WorldContext ctx);

    default @NotNull Conditions conditions() {
        return Conditions.EMPTY;
    }

    default @NotNull List<Hook> hooks() {
        return List.of();
    }

    default @NotNull OutputParam withConditions(@Nullable Conditions conditions) {
        Conditions safe = sanitizeConditions(conditions);
        if (safe.isEmpty()) {
            return this;
        }

        if (this instanceof Decorated(OutputParam base, Conditions c, List<Hook> hooks)) {
            Conditions merged = Conditions.merge(c, safe);
            return new Decorated(base, merged, hooks);
        }

        return new Decorated(this, safe, List.of());
    }

    default @NotNull OutputParam withHooks(@Nullable List<Hook> hooks) {
        List<Hook> safe = sanitizeHooks(hooks);
        if (safe.isEmpty()) {
            return this;
        }

        if (this instanceof Decorated(OutputParam base, Conditions conditions, List<Hook> existing)) {
            if (existing.isEmpty()) {
                return new Decorated(base, conditions, safe);
            }

            ArrayList<Hook> merged = new ArrayList<>(existing.size() + safe.size());
            merged.addAll(existing);
            merged.addAll(safe);
            return new Decorated(base, conditions, List.copyOf(merged));
        }

        return new Decorated(this, Conditions.EMPTY, safe);
    }

    static @Nullable OutputParam unwrap(@Nullable OutputParam p) {
        OutputParam cur = p;
        while (cur instanceof Decorated decorated) {
            cur = decorated.base;
        }
        return cur;
    }

    private static @NotNull Conditions sanitizeConditions(@Nullable Conditions in) {
        return in != null ? in : Conditions.EMPTY;
    }

    private static @NotNull List<Hook> sanitizeHooks(@Nullable List<Hook> in) {
        if (in == null || in.isEmpty()) {
            return List.of();
        }

        ArrayList<Hook> safe = new ArrayList<>(in.size());
        for (Hook hook : in) {
            if (hook != null) {
                safe.add(hook);
            }
        }

        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    private static <T> @NotNull DataResult<Conditions> decodeExplicitConditions(
            @NotNull DynamicOps<T> ops,
            T input
    ) {
        return ops.getMap(input).result()
                .map(mapLike -> {
                    T value = mapLike.get(ops.createString(JolCraftParameterIds.CONDITIONS));
                    if (value == null) {
                        return DataResult.success(Conditions.EMPTY);
                    }
                    return Conditions.CODEC.parse(ops, value);
                })
                .orElse(DataResult.success(Conditions.EMPTY));
    }

    private static <T> @NotNull DataResult<List<Hook>> decodeHooks(
            @NotNull DynamicOps<T> ops,
            T input
    ) {
        return ops.getMap(input).result()
                .map(mapLike -> {
                    T value = mapLike.get(ops.createString(JolCraftParameterIds.HOOKS));
                    if (value == null) {
                        return DataResult.success(List.<Hook>of());
                    }

                    return Hook.CODEC.listOf()
                            .parse(ops, value)
                            .map(OutputParam::sanitizeHooks);
                })
                .orElse(DataResult.success(List.<Hook>of()));
    }

    private static <T> T stripDecorationFields(@NotNull DynamicOps<T> ops, T input) {
        T stripped = ops.remove(input, JolCraftParameterIds.CONDITIONS);
        return ops.remove(stripped, JolCraftParameterIds.HOOKS);
    }

    private static @NotNull DataResult<OutputParam> attachDecorations(
            @NotNull OutputParam base,
            @NotNull Conditions conditions,
            @NotNull List<Hook> hooks
    ) {
        OutputParam wrapped = base;

        try {
            if (!conditions.isEmpty()) {
                wrapped = wrapped.withConditions(conditions);
            }
            if (!hooks.isEmpty()) {
                wrapped = wrapped.withHooks(hooks);
            }
        } catch (Exception e) {
            return DataResult.error(() -> "output param invalid: decoration attach threw: " + e.getMessage());
        }

        DataResult<?> validation;
        try {
            validation = wrapped.validate();
        } catch (Exception e) {
            return DataResult.error(() -> "output param invalid: validate threw: " + e.getMessage());
        }

        if (validation.error().isPresent()) {
            String msg = validation.error().map(DataResult.Error::message).orElse("invalid");
            return DataResult.error(() -> "output param invalid: " + msg);
        }

        return DataResult.success(wrapped);
    }

    @Override
    default @NotNull DataResult<?> validate() {
        return DataResult.success(this);
    }

    record Decorated(
            OutputParam base,
            Conditions conditions,
            List<Hook> hooks
    ) implements OutputParam, ResolvedOutputParam {

        public Decorated {
            if (base == null) {
                throw new IllegalArgumentException("decorated output base cannot be null");
            }
            conditions = sanitizeConditions(conditions);
            hooks = sanitizeHooks(hooks);
        }

        private static @NotNull List<Output> sanitizeOutputs(@Nullable List<Output> in) {
            if (in == null || in.isEmpty()) {
                return List.of();
            }

            ArrayList<Output> safe = new ArrayList<>(in.size());
            for (Output output : in) {
                if (output != null) {
                    safe.add(output);
                }
            }

            return safe.isEmpty() ? List.of() : List.copyOf(safe);
        }

        private static boolean hasProducedOutput(@NotNull List<Output> out) {
            for (Output output : out) {
                if (!(output instanceof Output.Empty)) {
                    return true;
                }
            }
            return false;
        }

        private @NotNull List<Output> applyHooks(
                @NotNull WorldContext ctx,
                @Nullable ItemTransformSourceResolver resolver,
                @Nullable List<Output> out
        ) {
            List<Output> safeOut = sanitizeOutputs(out);
            if (safeOut.isEmpty()) {
                return List.of();
            }

            if (!hasProducedOutput(safeOut)) {
                return List.of();
            }

            if (resolver == null || hooks.isEmpty()) {
                return safeOut;
            }

            for (Hook hook : hooks) {
                hook.apply(ctx, resolver, safeOut);
            }

            return safeOut;
        }

        @Override
        public @NotNull ResourceLocation typeId() {
            return base.typeId();
        }

        @Override
        public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
            return generateResolved(ctx, null);
        }

        @Override
        public @NotNull List<Output> generateResolved(
                @NotNull WorldContext ctx,
                @Nullable ItemTransformSourceResolver resolver
        ) {
            if (!conditions.test(ctx)) {
                return List.of();
            }

            List<Output> out;
            try {
                out = base instanceof ResolvedOutputParam resolved
                        ? resolved.generateResolved(ctx, resolver)
                        : base.generate(ctx);
            } catch (Exception ignored) {
                return List.of();
            }

            return applyHooks(ctx, resolver, out);
        }

        @Override
        public @NotNull Conditions conditions() {
            return conditions;
        }

        @Override
        public @NotNull List<Hook> hooks() {
            return hooks;
        }

        @Override
        public @NotNull OutputParam withConditions(@Nullable Conditions additionalConditions) {
            Conditions safeAdditional = sanitizeConditions(additionalConditions);
            if (safeAdditional.isEmpty()) {
                return this;
            }

            return new Decorated(base, Conditions.merge(conditions, safeAdditional), hooks);
        }

        @Override
        public @NotNull OutputParam withHooks(@Nullable List<Hook> additionalHooks) {
            List<Hook> safeAdditional = sanitizeHooks(additionalHooks);
            if (safeAdditional.isEmpty()) {
                return this;
            }

            if (hooks.isEmpty()) {
                return new Decorated(base, conditions, safeAdditional);
            }

            ArrayList<Hook> merged = new ArrayList<>(hooks.size() + safeAdditional.size());
            merged.addAll(hooks);
            merged.addAll(safeAdditional);
            return new Decorated(base, conditions, List.copyOf(merged));
        }

        @Override
        public @NotNull DataResult<?> validate() {
            DataResult<?> baseValidation;
            try {
                baseValidation = base.validate();
            } catch (Exception e) {
                return DataResult.error(() -> "base output validation threw: " + e.getMessage());
            }

            if (baseValidation.error().isPresent()) {
                return baseValidation;
            }

            DataResult<Conditions> conditionsValidation;
            try {
                conditionsValidation = conditions.validate();
            } catch (Exception e) {
                return DataResult.error(() -> "output conditions validation threw: " + e.getMessage());
            }

            if (conditionsValidation.error().isPresent()) {
                return conditionsValidation;
            }

            for (Hook hook : hooks) {
                DataResult<?> hookValidation;
                try {
                    hookValidation = hook.validate();
                } catch (Exception e) {
                    return DataResult.error(() -> "hook validation threw: " + e.getMessage());
                }

                if (hookValidation.error().isPresent()) {
                    return hookValidation;
                }
            }

            return DataResult.success(this);
        }
    }
}