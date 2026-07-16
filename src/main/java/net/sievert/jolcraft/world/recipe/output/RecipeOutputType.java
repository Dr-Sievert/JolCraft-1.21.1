package net.sievert.jolcraft.world.recipe.output;

import com.mojang.serialization.MapCodec;

public record RecipeOutputType(
        MapCodec<? extends RecipeOutput> codec
) {}