package net.sievert.jolcraft.world.recipe.output.hook;

import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;

public interface RecipeHook {

    boolean apply(
            LootContext context,
            Object generatedOutput,
            RecipeInput input
    );
}