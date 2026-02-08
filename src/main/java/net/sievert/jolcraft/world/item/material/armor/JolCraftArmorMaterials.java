package net.sievert.jolcraft.world.item.material.armor;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

import java.util.EnumMap;
import java.util.Map;

public final class JolCraftArmorMaterials {

    private JolCraftArmorMaterials() {
    }

    private static final Map<JolCraftMaterials.Material, ArmorMaterial> BY_MATERIAL = buildAll();

    private static Map<JolCraftMaterials.Material, ArmorMaterial> buildAll() {
        Map<JolCraftMaterials.Material, ArmorMaterial> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        put(out, JolCraftMaterials.Material.DEEPSLATE, new ArmorMaterial(
                24,
                Util.make(new EnumMap<>(ArmorType.class), map -> {
                    map.put(ArmorType.BOOTS, 2);
                    map.put(ArmorType.LEGGINGS, 5);
                    map.put(ArmorType.CHESTPLATE, 6);
                    map.put(ArmorType.HELMET, 2);
                    map.put(ArmorType.BODY, 5);
                }),
                10,
                JolCraftSounds.ARMOR_EQUIP_DEEPSLATE,
                1.0F,
                0.1F,
                JolCraftTags.Items.REPAIRS_DEEPSLATE,
                JolCraftMaterials.Material.DEEPSLATE.equipmentAssetKey()
        ));

        put(out, JolCraftMaterials.Material.MITHRIL, new ArmorMaterial(
                100,
                Util.make(new EnumMap<>(ArmorType.class), map -> {
                    map.put(ArmorType.BOOTS, 3);
                    map.put(ArmorType.LEGGINGS, 6);
                    map.put(ArmorType.CHESTPLATE, 8);
                    map.put(ArmorType.HELMET, 3);
                    map.put(ArmorType.BODY, 11);
                }),
                20,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                4.0F,
                0.0F,
                JolCraftTags.Items.REPAIRS_MITHRIL,
                JolCraftMaterials.Material.MITHRIL.equipmentAssetKey()
        ));

        return Map.copyOf(out);
    }

    private static void put(
            Map<JolCraftMaterials.Material, ArmorMaterial> map,
            JolCraftMaterials.Material material,
            ArmorMaterial armorMaterial
    ) {
        ArmorMaterial previous = map.put(material, armorMaterial);
        if (previous != null) {
            throw new IllegalStateException("Duplicate armor material entry for: " + material);
        }
    }

    public static ArmorMaterial armorMaterial(JolCraftMaterials.Material material) {
        ArmorMaterial mat = BY_MATERIAL.get(material);
        if (mat == null) {
            throw new IllegalStateException("Missing armor material entry for: " + material);
        }
        return mat;
    }

    public static Map<JolCraftMaterials.Material, ArmorMaterial> all() {
        return BY_MATERIAL;
    }
}