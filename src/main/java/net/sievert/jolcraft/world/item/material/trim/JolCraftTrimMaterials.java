package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JolCraftTrimMaterials {

    private JolCraftTrimMaterials() {}

    /**
     * In 1.21.1, item trim predicates are clamped to [0, 1].
     * So all custom trim item model indices must stay inside that range.
     *
     * We use one continuous deterministic sequence for all JolCraft trim materials:
     * - base/custom material trims first
     * - attribute trims after that
     *
     * These values must also remain below vanilla's first trim value (0.1),
     * otherwise vanilla trim overrides can be hijacked by later custom entries.
     */
    private static final float CUSTOM_INDEX_BASE = 0.0123F;
    private static final float CUSTOM_INDEX_STEP = 0.0001F;

    // -------------------------------------------------------------------------
    // Vanilla/base trims
    // -------------------------------------------------------------------------

    private static final Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> VANILLA_KEYS =
            buildVanillaKeys();

    private static @NotNull Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> buildVanillaKeys() {
        Map<JolCraftMaterials.Material, ResourceKey<TrimMaterial>> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            out.put(material, material.trimKey());
        }

        return Map.copyOf(out);
    }

    public static @NotNull ResourceKey<TrimMaterial> vanilla(@NotNull JolCraftMaterials.Material material) {
        ResourceKey<TrimMaterial> key = VANILLA_KEYS.get(material);
        if (key == null) {
            throw new IllegalStateException("Missing vanilla trim key for material: " + material);
        }
        return key;
    }

    public static float vanillaItemModelIndex(@NotNull JolCraftMaterials.Material material) {
        return CUSTOM_INDEX_BASE + (material.ordinal() * CUSTOM_INDEX_STEP);
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

        Attribute(@NotNull String id) {
            this.id = id;
        }

        @Override
        public @NotNull String getId() {
            return this.id;
        }

        public @NotNull ResourceKey<TrimMaterial> key() {
            return ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location(this.id));
        }

        public float itemModelIndex() {
            return CUSTOM_INDEX_BASE
                    + ((JolCraftMaterials.Material.values().length + this.ordinal()) * CUSTOM_INDEX_STEP);
        }
    }

    private static final Map<Attribute, ResourceKey<TrimMaterial>> ATTRIBUTE_KEYS =
            buildAttributeKeys();

    private static @NotNull Map<Attribute, ResourceKey<TrimMaterial>> buildAttributeKeys() {
        Map<Attribute, ResourceKey<TrimMaterial>> out =
                new EnumMap<>(Attribute.class);

        for (Attribute attribute : Attribute.values()) {
            out.put(attribute, attribute.key());
        }

        return Map.copyOf(out);
    }

    public static @NotNull ResourceKey<TrimMaterial> attribute(@NotNull Attribute attribute) {
        ResourceKey<TrimMaterial> key = ATTRIBUTE_KEYS.get(attribute);
        if (key == null) {
            throw new IllegalStateException("Missing attribute trim key for: " + attribute);
        }
        return key;
    }

    public static @NotNull Map<Attribute, ResourceKey<TrimMaterial>> attributeAll() {
        return ATTRIBUTE_KEYS;
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    static {
        validateUniqueItemModelIndices();
    }

    private static void validateUniqueItemModelIndices() {
        Set<Float> seen = new HashSet<>();

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            float index = vanillaItemModelIndex(material);

            validateInRange("vanilla trim item model index", material.name(), index);
            validateBelowVanillaThreshold("vanilla trim item model index", material.name(), index);

            if (!seen.add(index)) {
                throw new IllegalStateException("Duplicate vanilla trim item model index: " + index);
            }
        }

        for (Attribute attribute : Attribute.values()) {
            float index = attribute.itemModelIndex();

            validateInRange("attribute trim item model index", attribute.name(), index);
            validateBelowVanillaThreshold("attribute trim item model index", attribute.name(), index);

            if (!seen.add(index)) {
                throw new IllegalStateException("Duplicate attribute trim item model index: " + index);
            }
        }
    }

    private static void validateInRange(@NotNull String label, @NotNull String name, float index) {
        if (index < 0.0F || index > 1.0F) {
            throw new IllegalStateException(label + " out of range [0,1] for " + name + ": " + index);
        }
    }

    private static void validateBelowVanillaThreshold(@NotNull String label, @NotNull String name, float index) {
        if (index >= 0.1F) {
            throw new IllegalStateException(label + " must be below 0.1 for " + name + ": " + index);
        }
    }

    // -------------------------------------------------------------------------
    // Bootstrap
    // -------------------------------------------------------------------------

    public static void bootstrap(@NotNull BootstrapContext<TrimMaterial> context) {
        JolCraftVanillaTrimMaterials.bootstrap(context);
        JolCraftAttributeTrimMaterials.bootstrap(context);
    }
}