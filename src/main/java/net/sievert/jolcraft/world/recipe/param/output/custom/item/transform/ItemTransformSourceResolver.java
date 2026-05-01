package net.sievert.jolcraft.world.recipe.param.output.custom.item.transform;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemTransformSourceResolver {
    @NotNull ItemStack resolveItemTransformSource(@NotNull String source);
}