package net.sievert.jolcraft.world.item.material;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftMaterialIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftMaterials {

    private JolCraftMaterials() {}

    public enum Material implements JolCraftEnumHelper.StringId {
        DEEPSLATE(JolCraftMaterialIds.DEEPSLATE),
        MITHRIL(JolCraftMaterialIds.MITHRIL);

        private final String id;

        Material(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public ResourceKey<TrimMaterial> trimKey() {
            return ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location(id));
        }

        public ResourceKey<EquipmentAsset> equipmentAssetKey() {
            return ResourceKey.create(equipmentAssetRegistryKey(), JolCraft.location(id));
        }

        public String darkerTrimName() {
            return JolCraftStrings.underscored(id, JolCraftDictionary.DARKER);
        }
    }

    public static ResourceKey<Registry<EquipmentAsset>> equipmentAssetRegistryKey() {
        return ResourceKey.createRegistryKey(
                ResourceLocation.withDefaultNamespace(
                        JolCraftStrings.underscored(JolCraftDictionary.EQUIPMENT, JolCraftDictionary.ASSET)
                )
        );
    }
}