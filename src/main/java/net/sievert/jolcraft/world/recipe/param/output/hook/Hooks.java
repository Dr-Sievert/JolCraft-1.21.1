package net.sievert.jolcraft.world.recipe.param.output.hook;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.world.recipe.param.output.hook.custom.DeepslateCompassHook;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class Hooks {

    @FunctionalInterface
    public interface Operation {
        void apply(
                @NotNull WorldContext ctx,
                @NotNull ItemTransformSourceResolver resolver,
                @NotNull List<Output> outputs
        );
    }

    public static final ResourceLocation DEEPSLATE_COMPASS_ID =
            JolCraft.location(JolCraftRecipeHookIds.DEEPSLATE_COMPASS);

    private static final Map<ResourceLocation, Operation> REGISTRY = Map.of(
            DEEPSLATE_COMPASS_ID, DeepslateCompassHook::apply
    );

    private Hooks() {}

    public static boolean isRegistered(@NotNull ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    public static @NotNull Set<ResourceLocation> ids() {
        return Set.copyOf(REGISTRY.keySet());
    }

    public static @NotNull String knownIds() {
        return REGISTRY.keySet().stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.toCollection(TreeSet::new))
                .toString();
    }

    public static @NotNull String unknownHookError(@NotNull ResourceLocation id) {
        return "unknown hook name '" + id + "', expected one of: " + knownIds();
    }

    public static @NotNull Operation resolve(@NotNull ResourceLocation id) {
        Operation operation = REGISTRY.get(id);
        if (operation == null) {
            throw new IllegalArgumentException(unknownHookError(id));
        }
        return operation;
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