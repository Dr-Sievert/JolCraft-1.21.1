package net.sievert.jolcraft.data.recipe.param.output.hook;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final Map<ResourceLocation, Operation> REGISTRY = new ConcurrentHashMap<>();

    private Hooks() {}

    public static boolean register(ResourceLocation id, Operation operation) {
        if (id == null || operation == null) {
            return false;
        }
        return REGISTRY.putIfAbsent(id, operation) == null;
    }

    public static Operation resolve(ResourceLocation id) {
        if (id == null) {
            return NOOP;
        }
        Operation op = REGISTRY.get(id);
        return op != null ? op : NOOP;
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