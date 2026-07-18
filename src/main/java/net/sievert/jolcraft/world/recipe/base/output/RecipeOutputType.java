package net.sievert.jolcraft.world.recipe.base.output;

import com.mojang.serialization.MapCodec;

public record RecipeOutputType(
        MapCodec<? extends RecipeOutput> codec
) {}