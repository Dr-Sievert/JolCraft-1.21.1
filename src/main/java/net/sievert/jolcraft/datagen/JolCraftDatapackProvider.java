package net.sievert.jolcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.structure.JolCraftProcessorListProvider;
import net.sievert.jolcraft.datagen.structure.JolCraftStructureProvider;
import net.sievert.jolcraft.datagen.structure.JolCraftStructureSetProvider;
import net.sievert.jolcraft.datagen.structure.JolCraftTemplatePoolProvider;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.damage.JolCraftDamageTypes;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimPatterns;
import net.sievert.jolcraft.world.worldgen.biome.JolCraftBiomeModifiers;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftConfiguredFeatures;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftPlacedFeatures;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class JolCraftDatapackProvider extends DatapackBuiltinEntriesProvider {

    private static final class BuilderSpec {
        private final RegistrySetBuilder builder = new RegistrySetBuilder();
        private final List<ResourceKey<? extends Registry<?>>> keys = new ArrayList<>();

        private <T> @NotNull BuilderSpec add(
                @NotNull ResourceKey<? extends Registry<T>> key,
                @NotNull RegistrySetBuilder.RegistryBootstrap<T> bootstrap
        ) {
            this.builder.add(key, bootstrap);
            this.keys.add(key);
            return this;
        }

        private @NotNull String keyListString() {
            return this.keys.stream()
                    .map(key -> key.location().getPath().toUpperCase())
                    .collect(Collectors.joining(", "));
        }
    }

    private static final BuilderSpec SPEC = new BuilderSpec()
            .add(Registries.TRIM_MATERIAL, JolCraftTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, JolCraftTrimPatterns::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, JolCraftConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, JolCraftPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, JolCraftBiomeModifiers::bootstrap)
            .add(Registries.DAMAGE_TYPE, JolCraftDamageTypes::bootstrap)
            .add(Registries.STRUCTURE, JolCraftStructureProvider::bootstrap)
            .add(Registries.STRUCTURE_SET, JolCraftStructureSetProvider::bootstrap)
            .add(Registries.TEMPLATE_POOL, JolCraftTemplatePoolProvider::bootstrap)
            .add(Registries.PROCESSOR_LIST, JolCraftProcessorListProvider::bootstrap);

    public static final RegistrySetBuilder BUILDER = SPEC.builder;

    public JolCraftDatapackProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, BUILDER, Set.of(JolCraft.MOD_ID));

        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "Registering builtin datapack entries for registries: {}",
                SPEC.keyListString()
        );
    }
}