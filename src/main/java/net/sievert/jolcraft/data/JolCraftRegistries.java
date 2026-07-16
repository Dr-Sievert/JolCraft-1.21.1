package net.sievert.jolcraft.data;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.recipe.output.RecipeOutputType;

public final class JolCraftRegistries {

    public static final ResourceKey<Registry<RecipeOutputType>> RECIPE_OUTPUT_TYPE_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            JolCraft.MOD_ID,
                            "recipe_output_type"
                    )
            );

    public static final Registry<RecipeOutputType> RECIPE_OUTPUT_TYPE =
            new RegistryBuilder<>(RECIPE_OUTPUT_TYPE_KEY)
                    .sync(true)
                    .create();

    private JolCraftRegistries() {}
}