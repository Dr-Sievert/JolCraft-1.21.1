package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;

import java.util.EnumMap;
import java.util.Map;

public final class JolCraftTrimMaterials {

    private JolCraftTrimMaterials() {}

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

    public enum Attribute implements JolCraftEnumHelper.StringId {
        AEGISCORE(JolCraftTrimIds.AEGISCORE),
        ASHFANG(JolCraftTrimIds.ASHFANG),
        DEEPMARROW(JolCraftTrimIds.DEEPMARROW),
        EARTHBLOOD(JolCraftTrimIds.EARTHBLOOD),
        EMBERGLASS(JolCraftTrimIds.EMBERGLASS),
        FROSTVEIN(JolCraftTrimIds.FROSTVEIN),
        GRIMSTONE(JolCraftTrimIds.GRIMSTONE),
        IRONHEART(JolCraftTrimIds.IRONHEART),
        LUMIERE(JolCraftTrimIds.LUMIERE),
        MOONSHARD(JolCraftTrimIds.MOONSHARD),
        RUSTAGATE(JolCraftTrimIds.RUSTAGATE),
        SKYBURROW(JolCraftTrimIds.SKYBURROW),
        SUNGLEAM(JolCraftTrimIds.SUNGLEAM),
        VERDANITE(JolCraftTrimIds.VERDANITE),
        WOECRYSTAL(JolCraftTrimIds.WOECRYSTAL);

        private final String id;

        Attribute(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public ResourceKey<TrimMaterial> key() {
            return ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location(id));
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

    // -------------------------------------------------------------------------
    // Bootstrap
    // -------------------------------------------------------------------------

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        JolCraftVanillaTrimMaterials.bootstrap(context);
        JolCraftAttributeTrimMaterials.bootstrap(context);
    }
}