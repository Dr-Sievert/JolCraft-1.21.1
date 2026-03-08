package net.sievert.jolcraft.data.recipe.param.output.base;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.Param;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeRegistry;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.TextOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleOutput;
import net.sievert.jolcraft.data.recipe.param.output.hook.Hook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Polymorphic atomic output contract (data-layer).
 *
 * Strict polymorphic dispatch:
 * - no sentinels
 * - unknown JSON type ids fail decode
 * - unknown stream discriminators fail decode
 *
 * IMPORTANT:
 * - This family is leaf-only.
 * - Composite wrappers such as {@link Outputs} are NOT variants of OutputParam.
 *
 * Hooks remain an outer sidecar around the strict base payload.
 */
public interface OutputParam extends Param {

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
            return BASE_CODEC.decode(ops, input).flatMap(basePair -> {
                OutputParam base = basePair.getFirst();
                T rest = basePair.getSecond();
                return attachHooks(base, decodeHooks(ops, input))
                        .map(wrapped -> Pair.of(wrapped, rest));
            });
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

            DataResult<T> baseEncoded = BASE_CODEC.encode(base, ops, prefix);

            List<Hook> hooks = sanitizeHooks(input.hooks());
            if (hooks.isEmpty()) {
                return baseEncoded;
            }

            return baseEncoded.flatMap(obj ->
                    Hook.CODEC.listOf()
                            .encodeStart(ops, hooks)
                            .flatMap(listValue ->
                                    ops.mergeToMap(
                                            obj,
                                            ops.createString(JolCraftParameterIds.HOOKS),
                                            listValue
                                    )
                            )
            );
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

                        int size = buf.readVarInt();
                        if (size < 0) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.HOOKS + " size must be >= 0 (got " + size + ")"
                            );
                        }
                        if (size == 0) {
                            return base;
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

                        return attachHooks(base, List.copyOf(hooks))
                                .getOrThrow(IllegalArgumentException::new);
                    }
            );

    @NotNull ResourceLocation typeId();

    @NotNull List<Output> generate(@NotNull WorldContext ctx);

    default @NotNull List<Hook> hooks() {
        return List.of();
    }

    default @NotNull OutputParam withHooks(@Nullable List<Hook> hooks) {
        List<Hook> safe = sanitizeHooks(hooks);
        if (safe.isEmpty()) {
            return this;
        }

        if (this instanceof Hooked(OutputParam base, List<Hook> hooks1)) {
            if (hooks1.isEmpty()) {
                return new Hooked(base, safe);
            }

            ArrayList<Hook> merged = new ArrayList<>(hooks1.size() + safe.size());
            merged.addAll(hooks1);
            merged.addAll(safe);
            return new Hooked(base, List.copyOf(merged));
        }

        return new Hooked(this, safe);
    }

    static @Nullable OutputParam unwrap(@Nullable OutputParam p) {
        OutputParam cur = p;
        while (cur instanceof Hooked hooked) {
            cur = hooked.base;
        }
        return cur;
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

    private static <T> @NotNull List<Hook> decodeHooks(DynamicOps<T> ops, T input) {
        return ops.getMap(input).result()
                .map(mapLike -> {
                    T value = mapLike.get(ops.createString(JolCraftParameterIds.HOOKS));
                    if (value == null) return List.<Hook>of();

                    return sanitizeHooks(
                            Hook.CODEC.listOf()
                                    .parse(ops, value)
                                    .getOrThrow(IllegalArgumentException::new)
                    );
                })
                .orElse(List.of());
    }

    private static @NotNull DataResult<OutputParam> attachHooks(
            @NotNull OutputParam base,
            @NotNull List<Hook> hooks
    ) {
        OutputParam wrapped;
        try {
            wrapped = base.withHooks(hooks);
        } catch (Exception e) {
            return DataResult.error(() -> "output param invalid: withHooks threw: " + e.getMessage());
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

    record Hooked(OutputParam base, List<Hook> hooks) implements OutputParam, ResolvedOutputParam {

        public Hooked {
            if (base == null) {
                throw new IllegalArgumentException("hooked output base cannot be null");
            }
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
        public @NotNull List<Hook> hooks() {
            return hooks;
        }

        @Override
        public @NotNull OutputParam withHooks(@Nullable List<Hook> additionalHooks) {
            List<Hook> safeAdditional = sanitizeHooks(additionalHooks);
            if (safeAdditional.isEmpty()) {
                return this;
            }

            if (hooks.isEmpty()) {
                return new Hooked(base, safeAdditional);
            }

            ArrayList<Hook> merged = new ArrayList<>(hooks.size() + safeAdditional.size());
            merged.addAll(hooks);
            merged.addAll(safeAdditional);
            return new Hooked(base, List.copyOf(merged));
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