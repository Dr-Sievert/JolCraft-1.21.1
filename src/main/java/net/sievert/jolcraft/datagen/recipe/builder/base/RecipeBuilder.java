package net.sievert.jolcraft.datagen.recipe.builder.base;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;

/**
 * Datagen-only contract for recipe builders.
 *
 * Contract:
 * - never throws (returns DataResult)
 * - never saves
 * - returns a RecipeEmission (name + deferred save action)
 */
public interface RecipeBuilder extends ValidatedBuilder<RecipeEmission> {

    @Override
    DataResult<RecipeEmission> buildValidated();
}