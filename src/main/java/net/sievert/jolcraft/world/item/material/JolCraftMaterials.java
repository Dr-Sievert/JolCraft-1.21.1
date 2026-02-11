package net.sievert.jolcraft.world.item.material;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Locale;

public final class JolCraftMaterials {

    private JolCraftMaterials() {}

    /**
     * Canonical material codes for JolCraft base materials.
     * Single source of truth for ids and cross-system keys.
     */
    public enum Material {
        DEEPSLATE,
        MITHRIL;

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        public ResourceKey<TrimMaterial> trimKey() {
            return ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location(id()));
        }

        public ResourceKey<EquipmentAsset> equipmentAssetKey() {
            return ResourceKey.create(equipmentAssetRegistryKey(), JolCraft.location(id()));
        }

        public String darkerTrimName() {
            return JolCraftStrings.underscored(id(), JolCraftDictionary.DARKER);
        }
    }

    /**
     * The EquipmentAsset registry key. Centralized here so materials can derive keys
     * without depending on other classes’ constants.
     */
    public static ResourceKey<Registry<EquipmentAsset>> equipmentAssetRegistryKey() {
        return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(JolCraftDictionary.EQUIPMENT_ASSET));
    }
}
