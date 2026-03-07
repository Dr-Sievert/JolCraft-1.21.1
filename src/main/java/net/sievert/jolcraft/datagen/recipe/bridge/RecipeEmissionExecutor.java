package net.sievert.jolcraft.datagen.recipe.bridge;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.datagen.recipe.build.base.OrderedBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class RecipeEmissionExecutor {

    private final RecipeOutput output;
    private final String folder;

    private final Map<String, Integer> perType;
    private final Map<String, Integer> orderedCounters;
    private int total;

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public RecipeEmissionExecutor(@NotNull RecipeOutput output) {
        this(output, "", new HashMap<>(), new HashMap<>(), 0);
    }

    private RecipeEmissionExecutor(
            @NotNull RecipeOutput output,
            @NotNull String folder,
            Map<String, Integer> perType,
            Map<String, Integer> orderedCounters,
            int total
    ) {
        this.output = output;
        this.folder = folder;
        this.perType = perType;
        this.orderedCounters = orderedCounters;
        this.total = total;
    }

    // ---------------------------------------------------------------------
    // Folder scoping
    // ---------------------------------------------------------------------

    public @NotNull RecipeEmissionExecutor scoped(@NotNull String segment) {

        if (segment.isBlank()) {
            return this;
        }

        String combined = this.folder.isBlank()
                ? segment
                : JolCraftStrings.slashed(this.folder, segment);

        return new RecipeEmissionExecutor(output, combined, perType, orderedCounters, total);
    }

    // ---------------------------------------------------------------------
    // Emit
    // ---------------------------------------------------------------------

    public void emit(@NotNull DataResult<RecipeEmission> result) {

        RecipeEmission emission = result.getOrThrow(IllegalStateException::new);

        ResourceKey<Recipe<?>> id = RecipeIdBuilder.build(folder, emission.fileName());

        emission.saveAction().save(output, id);

        perType.merge(emission.type(), 1, Integer::sum);
        total++;
    }

    public void emitOrdered(@NotNull OrderedBuilder builder) {

        if (builder.order() == 0) {
            String key = builder.orderKey();
            int next = orderedCounters.merge(key, 1, Integer::sum);
            builder.setOrder(next);
        }

        emit(builder.buildValidated());
    }

    // ---------------------------------------------------------------------
    // Stats
    // ---------------------------------------------------------------------

    public int total() {
        return total;
    }

    public Map<String, Integer> counts() {
        return Map.copyOf(perType);
    }
}