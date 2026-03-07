package net.sievert.jolcraft.datagen.recipe.bridge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class RecipeIdBuilder {

    private RecipeIdBuilder() {}

    /**
     * Builds the canonical JolCraft recipe id.
     *
     * - fileName must already be validated (no slashes, no ".json")
     * - folderPrefix may be blank
     * - Always uses JolCraft.MOD_ID namespace
     */
    public static @NotNull ResourceKey<Recipe<?>> build(
            @NotNull String folderPrefix,
            @NotNull String fileName
    ) {
        final String path = folderPrefix.isBlank()
                ? fileName
                : JolCraftStrings.slashed(folderPrefix, fileName);

        return ResourceKey.create(
                Registries.RECIPE,
                JolCraft.location(path)
        );
    }
}