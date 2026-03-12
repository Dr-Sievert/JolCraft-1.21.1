package net.sievert.jolcraft.datagen.recipe.bridge;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.builder.base.OrderedBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RecipeEmissionExecutor {

    private final RecipeOutput output;
    private final String folder;
    private final Map<String, Integer> orderedCounters;

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public RecipeEmissionExecutor(@NotNull RecipeOutput output) {
        this(output, "", new HashMap<>());
    }

    private RecipeEmissionExecutor(
            @NotNull RecipeOutput output,
            @NotNull String folder,
            @NotNull Map<String, Integer> orderedCounters
    ) {
        this.output = Objects.requireNonNull(output, "output");
        this.folder = Objects.requireNonNull(folder, "folder");
        this.orderedCounters = Objects.requireNonNull(orderedCounters, "orderedCounters");
    }

    // ---------------------------------------------------------------------
    // Folder scoping
    // ---------------------------------------------------------------------

    public @NotNull RecipeEmissionExecutor scoped(@NotNull String segment) {
        Objects.requireNonNull(segment, "segment");

        if (segment.isBlank()) {
            return this;
        }

        String combined = this.folder.isBlank()
                ? segment
                : JolCraftStrings.slashed(this.folder, segment);

        return new RecipeEmissionExecutor(output, combined, orderedCounters);
    }

    // ---------------------------------------------------------------------
    // Emit
    // ---------------------------------------------------------------------

    public void emit(@NotNull DataResult<RecipeEmission> result) {
        Objects.requireNonNull(result, JolCraftParameterIds.RESULT);

        RecipeEmission emission = result.getOrThrow(IllegalStateException::new);
        ResourceKey<Recipe<?>> id = RecipeIdBuilder.build(folder, emission.fileName());
        emission.saveAction().save(output, id);
    }

    public void emitOrdered(@NotNull OrderedBuilder builder) {
        Objects.requireNonNull(builder, JolCraftDictionary.BUILDER);

        if (builder.order() == 0) {
            String key = builder.orderKey();
            int next = orderedCounters.merge(key, 1, Integer::sum);
            builder.setOrder(next);
        }

        emit(builder.buildValidated());
    }
}