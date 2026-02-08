package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class JolCraftTrimMaterials {

    private JolCraftTrimMaterials() {
    }

    // -------------------------------------------------------------------------
    // Vanilla/base trims
    // -------------------------------------------------------------------------

    private static final Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> VANILLA_KEYS =
            buildVanillaKeys();

    private static Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> buildVanillaKeys() {
        Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            out.put(material, material.trimKey());
        }

        return Map.copyOf(out);
    }

    public static ResourceKey<TrimMaterial> vanilla(JolCraftMaterials.Material material) {
        ResourceKey<TrimMaterial> key = VANILLA_KEYS.get(material);
        if (key == null) {
            throw new IllegalStateException("Missing vanilla trim key for material: " + material);
        }
        return key;
    }

    public static Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> vanillaAll() {
        return VANILLA_KEYS;
    }

    // -------------------------------------------------------------------------
    // Attribute trims
    // -------------------------------------------------------------------------

    public enum Attribute {
        AEGISCORE,
        ASHFANG,
        DEEPMARROW,
        EARTHBLOOD,
        EMBERGLASS,
        FROSTVEIN,
        GRIMSTONE,
        IRONHEART,
        LUMIERE,
        MOONSHARD,
        RUSTAGATE,
        SKYBURROW,
        SUNGLEAM,
        VERDANITE,
        WOECRYSTAL;

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        public ResourceKey<TrimMaterial> key() {
            return ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location(id()));
        }
    }

    private static final Map<Attribute, ResourceKey<TrimMaterial>> ATTRIBUTE_KEYS =
            buildAttributeKeys();

    private static Map<Attribute, ResourceKey<TrimMaterial>> buildAttributeKeys() {
        Map<Attribute, ResourceKey<TrimMaterial>> out =
                new EnumMap<>(Attribute.class);

        for (Attribute attribute : Attribute.values()) {
            out.put(attribute, attribute.key());
        }

        return Map.copyOf(out);
    }

    public static ResourceKey<TrimMaterial> attribute(Attribute attribute) {
        ResourceKey<TrimMaterial> key = ATTRIBUTE_KEYS.get(attribute);
        if (key == null) {
            throw new IllegalStateException("Missing attribute trim key for: " + attribute);
        }
        return key;
    }

    public static Map<Attribute, ResourceKey<TrimMaterial>> attributeAll() {
        return ATTRIBUTE_KEYS;
    }

    // -------------------------------------------------------------------------
    // Bootstrap
    // -------------------------------------------------------------------------

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        JolCraftVanillaTrimMaterials.bootstrap(context);
        JolCraftAttributeTrimMaterials.bootstrap(context);
    }
}