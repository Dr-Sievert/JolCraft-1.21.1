package net.sievert.jolcraft.item.armor;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;

public class JolCraftEquipmentAssets {

    public static final ResourceKey<Registry<EquipmentAsset>> EQUIPMENT_ASSET_REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("equipment_asset"));

    public static final DeferredRegister<EquipmentAsset> EQUIPMENT_ASSETS =
            DeferredRegister.create(EQUIPMENT_ASSET_REGISTRY, JolCraft.MOD_ID);

    public static final DeferredHolder<EquipmentAsset, EquipmentAsset> DEEPSLATE =
            EQUIPMENT_ASSETS.register("deepslate", EquipmentAsset::new);

    public static final ResourceKey<EquipmentAsset> DEEPSLATE_KEY =
            ResourceKey.create(EQUIPMENT_ASSET_REGISTRY, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "deepslate"));

    public static final DeferredHolder<EquipmentAsset, EquipmentAsset> MITHRIL =
            EQUIPMENT_ASSETS.register("mithril", EquipmentAsset::new);

    public static final ResourceKey<EquipmentAsset> MITHRIL_KEY =
            ResourceKey.create(EQUIPMENT_ASSET_REGISTRY, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "mithril"));

    public static void register(IEventBus eventBus) {
        EQUIPMENT_ASSETS.register(eventBus);
    }

}
