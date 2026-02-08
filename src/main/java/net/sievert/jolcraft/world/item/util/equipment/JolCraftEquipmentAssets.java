package net.sievert.jolcraft.world.item.util.equipment;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;

import java.util.EnumMap;
import java.util.Map;

public final class JolCraftEquipmentAssets {

    public static final DeferredRegister<EquipmentAsset> EQUIPMENT_ASSETS =
            DeferredRegister.create(JolCraftMaterials.equipmentAssetRegistryKey(), JolCraft.MOD_ID);

    private static final Map<JolCraftMaterials.Material, DeferredHolder<EquipmentAsset, EquipmentAsset>> BY_MATERIAL =
            registerAll();

    private static Map<JolCraftMaterials.Material, DeferredHolder<EquipmentAsset, EquipmentAsset>> registerAll() {
        Map<JolCraftMaterials.Material, DeferredHolder<EquipmentAsset, EquipmentAsset>> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            DeferredHolder<EquipmentAsset, EquipmentAsset> holder =
                    EQUIPMENT_ASSETS.register(material.id(), EquipmentAsset::new);
            out.put(material, holder);
        }

        return Map.copyOf(out);
    }

    public static DeferredHolder<EquipmentAsset, EquipmentAsset> get(JolCraftMaterials.Material material) {
        DeferredHolder<EquipmentAsset, EquipmentAsset> holder = BY_MATERIAL.get(material);
        if (holder == null) {
            throw new IllegalStateException("Missing EquipmentAsset holder for material: " + material);
        }
        return holder;
    }

    public static Map<JolCraftMaterials.Material, DeferredHolder<EquipmentAsset, EquipmentAsset>> all() {
        return BY_MATERIAL;
    }

    /**
     * Convenience for callers that only need the ResourceKey (e.g. trim override maps).
     */
    public static ResourceKey<EquipmentAsset> key(JolCraftMaterials.Material material) {
        return material.equipmentAssetKey();
    }

    public static void register(IEventBus eventBus) {
        EQUIPMENT_ASSETS.register(eventBus);
    }
}