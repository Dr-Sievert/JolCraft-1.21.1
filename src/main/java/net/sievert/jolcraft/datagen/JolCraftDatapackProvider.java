package net.sievert.jolcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.trim.JolCraftTrimPatterns;
import net.sievert.jolcraft.world.worldgen.biome.JolCraftBiomeModifiers;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftConfiguredFeatures;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class JolCraftDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, JolCraftTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, JolCraftTrimPatterns::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, JolCraftConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, JolCraftPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, JolCraftBiomeModifiers::bootstrap);

    public JolCraftDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(JolCraft.MOD_ID));
    }
}
