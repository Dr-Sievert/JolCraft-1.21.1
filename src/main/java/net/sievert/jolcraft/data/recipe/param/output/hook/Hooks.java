package net.sievert.jolcraft.data.recipe.param.output.hook;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.output.hook.custom.DeepslateCompassHook;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Strict immutable hook dispatch table.
 *
 * Rules:
 * - no runtime registration
 * - no mutable global state
 * - unknown hook ids are ignored (no-op)
 * - all known hooks are declared here as static entries
 */
public final class Hooks {

    @FunctionalInterface
    public interface Operation {
        void apply(
                @NotNull WorldContext ctx,
                @NotNull ItemTransformSourceResolver resolver,
                @NotNull List<Output> outputs
        );
    }

    private static final Operation NOOP = (ctx, resolver, outputs) -> {};

    public static final ResourceLocation DEEPSLATE_COMPASS_ID =
            JolCraft.location(JolCraftRecipeHookIds.DEEPSLATE_COMPASS);

    private static final Map<ResourceLocation, Operation> REGISTRY = Map.of(
            DEEPSLATE_COMPASS_ID, DeepslateCompassHook::apply
    );

    private Hooks() {}

    public static @NotNull Operation resolve(ResourceLocation id) {
        if (id == null) {
            return NOOP;
        }
        return REGISTRY.getOrDefault(id, NOOP);
    }

    public static void apply(
            @NotNull ResourceLocation id,
            @NotNull WorldContext ctx,
            @NotNull ItemTransformSourceResolver resolver,
            @NotNull List<Output> outputs
    ) {
        resolve(id).apply(ctx, resolver, outputs);
    }
}