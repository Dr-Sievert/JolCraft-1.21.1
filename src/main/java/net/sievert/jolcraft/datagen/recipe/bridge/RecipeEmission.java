package net.sievert.jolcraft.datagen.recipe.bridge;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Datagen bridge payload: "one recipe to be emitted later".
 *
 * Holds:
 * - type (logical recipe type, used for counting/statistics)
 * - validated JSON file name (base name only; no folder; no extension)
 * - deferred save action (requires output + id binding)
 *
 * Does NOT hold:
 * - folder/path (owned by execution layer)
 * - namespace (always JolCraft)
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record RecipeEmission(
        String type,
        String fileName,
        RecipeSaveAction saveAction
) {

    public RecipeEmission {
        Objects.requireNonNull(type, JolCraftParameterIds.TYPE);
        Objects.requireNonNull(fileName, JolCraftDictionary.NAME);
        Objects.requireNonNull(saveAction, JolCraftDictionary.SAVE);
    }

    // ---------------------------------------------------------------------
    // Factories
    // ---------------------------------------------------------------------

    /**
     * Create from raw file name (validated here).
     */
    public static @NotNull DataResult<RecipeEmission> of(
            String type,
            String fileName,
            RecipeSaveAction saveAction
    ) {
        Objects.requireNonNull(type, JolCraftParameterIds.TYPE);
        Objects.requireNonNull(saveAction, JolCraftDictionary.SAVE);

        return validateFileName(fileName)
                .map(n -> new RecipeEmission(type, n, saveAction));
    }

    /**
     * Create from a DataResult file name (e.g. from RecipeFileNameBuilder.build()).
     */
    public static @NotNull DataResult<RecipeEmission> of(
            String type,
            DataResult<String> fileNameValidated,
            RecipeSaveAction saveAction
    ) {
        Objects.requireNonNull(type, JolCraftParameterIds.TYPE);
        Objects.requireNonNull(fileNameValidated, JolCraftDictionary.NAME);
        Objects.requireNonNull(saveAction, JolCraftDictionary.SAVE);

        return fileNameValidated
                .flatMap(n -> validateFileName(n)
                        .map(nn -> new RecipeEmission(type, nn, saveAction)));
    }

    // ---------------------------------------------------------------------
    // Renaming
    // ---------------------------------------------------------------------

    /**
     * Create a copy of this emission with a different file name (validated here).
     *
     * Used by subproviders/execution layer to override the builder's file name
     * without touching the recipe payload or save logic.
     */
    public @NotNull DataResult<RecipeEmission> renamed(@NotNull String newFileName) {
        Objects.requireNonNull(newFileName, JolCraftDictionary.NAME);
        return validateFileName(newFileName)
                .map(n -> new RecipeEmission(this.type, n, this.saveAction));
    }

    /**
     * Rename an emission wrapped in a DataResult.
     */
    public static @NotNull DataResult<RecipeEmission> renamed(
            @NotNull DataResult<RecipeEmission> emissionValidated,
            @NotNull String newFileName
    ) {
        Objects.requireNonNull(emissionValidated, "emissionValidated");
        Objects.requireNonNull(newFileName, JolCraftDictionary.NAME);
        return emissionValidated.flatMap(e -> e.renamed(newFileName));
    }

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    /**
     * File name rules (datagen):
     * - not null/blank
     * - must NOT contain '/' or '\\'
     * - must NOT end with ".json"
     */
    public static @NotNull DataResult<String> validateFileName(String raw) {

        Objects.requireNonNull(raw, JolCraftDictionary.NAME);

        String n = raw.trim();

        if (n.isEmpty()) {
            return DataResult.error(() -> "recipe fileName is blank");
        }

        if (n.indexOf('/') >= 0 || n.indexOf('\\') >= 0) {
            return DataResult.error(
                    () -> "recipe fileName must not contain path separators: '" + n + "'",
                    n
            );
        }

        if (n.endsWith(".json")) {
            return DataResult.error(
                    () -> "recipe fileName must not include .json extension: '" + n + "'",
                    n
            );
        }

        return DataResult.success(n);
    }

    // ---------------------------------------------------------------------
    // Save contract
    // ---------------------------------------------------------------------

    /**
     * Deferred save behavior.
     *
     * Emission does NOT control:
     * - folder
     * - namespace
     * - id binding
     */
    @FunctionalInterface
    public interface RecipeSaveAction {
        void save(RecipeOutput output, ResourceKey<Recipe<?>> id);
    }
}