package net.sievert.jolcraft.data.recipe.param.output.base;

import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.Param;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.output.hook.Hook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Polymorphic output contract (data-layer).
 *
 * Semantics:
 * - Represents "what to produce" at runtime.
 * - Does NOT apply side-effects directly (e.g. sounds, particles) unless explicitly implemented.
 * - May be wrapped to attach hook sidecars.
 *
 * Runtime contract:
 * - WorldContext is always non-null and fully populated (server-only execution).
 * - Implementations must be total and must NEVER throw.
 * - Fail-closed: if generation cannot produce output, return an empty list.
 * - Returned list must be immutable or treated as immutable by callers.
 * - Never return null.
 *
 * Validation contract:
 * - Decoding may produce an instance even if structurally invalid.
 * - validate() is invoked at dispatch boundaries.
 * - Implementations with constraints should override validate().
 * - Default implementation assumes the param is structurally valid.
 *
 * Dispatch:
 * - Instances are codec'd and stream-encoded via OutputDispatch.
 * - typeId() must be stable and globally unique within the dispatch table.
 */
public interface OutputParam extends Param {

    @NotNull ResourceLocation typeId();

    @NotNull List<Output> generate(@NotNull WorldContext ctx);

    default @NotNull List<Hook> hooks() {
        return List.of();
    }

    default @NotNull OutputParam withHooks(@Nullable List<Hook> hooks) {
        List<Hook> safe = sanitizeHooks(hooks);
        if (safe.isEmpty()) return this;

        if (this instanceof Hooked hooked) {
            OutputParam base = hooked.baseSafe();

            List<Hook> existing = sanitizeHooks(hooked.hooks);
            if (existing.isEmpty()) {
                return new Hooked(base, safe);
            }

            ArrayList<Hook> merged = new ArrayList<>(existing.size() + safe.size());
            merged.addAll(existing);
            merged.addAll(safe);
            return new Hooked(base, List.copyOf(merged));
        }

        return new Hooked(this, safe);
    }

    static @NotNull OutputParam unwrap(@Nullable OutputParam p) {
        OutputParam cur = (p != null) ? p : OutputDispatch.None.INSTANCE;

        while (cur instanceof Hooked h) {
            cur = h.baseSafe();
        }

        return cur;
    }

    private static @NotNull List<Hook> sanitizeHooks(@Nullable List<Hook> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<Hook> safe = new ArrayList<>(in.size());
        for (Hook h : in) if (h != null) safe.add(h);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    @Override
    default DataResult<?> validate() {
        return DataResult.success(this);
    }

    record Hooked(OutputParam base, List<Hook> hooks) implements OutputParam, ResolvedOutputParam {

        private OutputParam baseSafe() {
            return base != null ? base : OutputDispatch.None.INSTANCE;
        }

        private @NotNull List<Output> applyHooks(
                @NotNull WorldContext ctx,
                @Nullable ItemTransformSourceResolver resolver,
                @NotNull List<Output> out
        ) {
            if (out.isEmpty()) return List.of();

            boolean produced = false;
            for (Output o : out) {
                if (o != null && !(o instanceof Output.Empty)) {
                    produced = true;
                    break;
                }
            }

            if (!produced) return List.of();

            List<Hook> hs = hooks != null ? hooks : List.of();
            for (Hook h : hs) {
                if (h != null && resolver != null) {
                    h.apply(ctx, resolver, out);
                }
            }

            return out;
        }

        @Override
        public @NotNull ResourceLocation typeId() {
            return baseSafe().typeId();
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
            OutputParam b = baseSafe();

            List<Output> out = (b instanceof ResolvedOutputParam resolved)
                    ? resolved.generateResolved(ctx, resolver)
                    : b.generate(ctx);

            return applyHooks(ctx, resolver, out);
        }

        @Override
        public @NotNull List<Hook> hooks() {
            return hooks != null ? hooks : List.of();
        }

        @Override
        public DataResult<?> validate() {
            return baseSafe().validate();
        }
    }
}