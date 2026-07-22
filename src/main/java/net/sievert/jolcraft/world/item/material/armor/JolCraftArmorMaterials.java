package net.sievert.jolcraft.world.item.material.armor;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

import java.util.*;

public final class JolCraftArmorMaterials {

    private JolCraftArmorMaterials() {}

    private static final Map<JolCraftMaterials.Material, Holder<ArmorMaterial>> BY_MATERIAL = buildAll();

    private static Map<JolCraftMaterials.Material, Holder<ArmorMaterial>> buildAll() {
        Map<JolCraftMaterials.Material, Holder<ArmorMaterial>> out = new EnumMap<>(JolCraftMaterials.Material.class);

        put(out, JolCraftMaterials.Material.DEEPSLATE, register(
                JolCraftDictionary.DEEPSLATE,
                Util.make(new EnumMap<>(ArmorItem.Type.class), m -> {
                    m.put(ArmorItem.Type.BOOTS, 2);
                    m.put(ArmorItem.Type.LEGGINGS, 5);
                    m.put(ArmorItem.Type.CHESTPLATE, 6);
                    m.put(ArmorItem.Type.HELMET, 2);
                    m.put(ArmorItem.Type.BODY, 5);
                }),
                10,
                JolCraftSounds.ARMOR_EQUIP_DEEPSLATE,
                1.0F,
                0.1F,
                () -> Ingredient.of(JolCraftTags.Items.REPAIRS_DEEPSLATE),
                layers(JolCraftDictionary.DEEPSLATE)
        ));

        put(out, JolCraftMaterials.Material.MITHRIL, register(
                JolCraftDictionary.MITHRIL,
                Util.make(new EnumMap<>(ArmorItem.Type.class), m -> {
                    m.put(ArmorItem.Type.BOOTS, 3);
                    m.put(ArmorItem.Type.LEGGINGS, 6);
                    m.put(ArmorItem.Type.CHESTPLATE, 8);
                    m.put(ArmorItem.Type.HELMET, 3);
                    m.put(ArmorItem.Type.BODY, 11);
                }),
                20,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                4.0F,
                0.0F,
                () -> Ingredient.of(JolCraftTags.Items.REPAIRS_MITHRIL),
                layers(JolCraftDictionary.MITHRIL)
        ));

        return Map.copyOf(out);
    }

    private static List<ArmorMaterial.Layer> layers(String name) {
        return List.of(new ArmorMaterial.Layer(JolCraft.location(name)));
    }

    private static Holder<ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<net.minecraft.sounds.SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            java.util.function.Supplier<Ingredient> repairIngredient,
            List<ArmorMaterial.Layer> layers
    ) {
        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                JolCraft.location(name),
                new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }

    private static void put(Map<JolCraftMaterials.Material, Holder<ArmorMaterial>> map,
                            JolCraftMaterials.Material material,
                            Holder<ArmorMaterial> armorMaterial) {
        map.put(material, armorMaterial);
    }

    public static Holder<ArmorMaterial> armorMaterial(JolCraftMaterials.Material material) {
        Holder<ArmorMaterial> holder = BY_MATERIAL.get(material);
        if (holder == null) {
            throw new IllegalStateException("No armor material registered for " + material.getId());
        }
        return holder;
    }

    private static final Map<JolCraftMaterials.Material, Integer> DURABILITY_MULTIPLIER = Map.of(
            JolCraftMaterials.Material.DEEPSLATE, 24,
            JolCraftMaterials.Material.MITHRIL, 100
    );

    public static int durability(JolCraftMaterials.Material material, ArmorItem.Type type) {
        Integer multiplier = DURABILITY_MULTIPLIER.get(material);
        if (multiplier == null) {
            throw new IllegalStateException("Missing armor durability multiplier for " + material.getId());
        }
        return type.getDurability(multiplier);
    }
}
