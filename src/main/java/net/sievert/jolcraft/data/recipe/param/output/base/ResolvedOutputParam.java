package net.sievert.jolcraft.data.recipe.param.output.base;

import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ResolvedOutputParam {

    @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    );
}