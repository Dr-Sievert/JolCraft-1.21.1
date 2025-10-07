package net.sievert.jolcraft.worldgen.biome;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.worldgen.feature.JolCraftPlacedFeatures;

public class JolCraftBiomeModifiers {

    //Vegetation
    public static final ResourceKey<BiomeModifier> ADD_DUSKCAP_PATCH = registerKey("add_duskcap_patch");
    public static final ResourceKey<BiomeModifier> ADD_DEEPSLATE_BULBS_PATCH = registerKey("add_deepslate_bulbs_patch");

    //Ores
    public static final ResourceKey<BiomeModifier> ADD_SMALL_MITHRIL_ORE = registerKey("add_small_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_MEDIUM_MITHRIL_ORE = registerKey("add_medium_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_LARGE_MITHRIL_ORE = registerKey("add_large_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_SPECIAL_MITHRIL_ORE = registerKey("add_special_mithril_ore");

    //Geodes
    public static final ResourceKey<BiomeModifier> ADD_BASALT_GEODE = registerKey("add_basalt_geode");


    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        //Vegetation
        context.register(
                ADD_DUSKCAP_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.DUSKCAP_PATCH_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_DEEPSLATE_BULBS_PATCH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.DEEPSLATE_BULBS_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        //Ores
        context.register(ADD_SMALL_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.SMALL_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_MEDIUM_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.MEDIUM_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_LARGE_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.LARGE_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_SPECIAL_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(JolCraftTags.Biomes.MOUNTAINS_AND_HILLS),
                HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.SPECIAL_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        //Geodes
        context.register(
                ADD_BASALT_GEODE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(JolCraftPlacedFeatures.BASALT_GEODE_PLACED_KEY)),
                        GenerationStep.Decoration.UNDERGROUND_STRUCTURES
                )
        );

    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, JolCraft.location(name));
    }
}
