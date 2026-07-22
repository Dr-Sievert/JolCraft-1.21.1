package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.armor.DeepslateArmorItem;
import net.sievert.jolcraft.world.item.custom.armor.MithrilArmorItem;
import net.sievert.jolcraft.world.item.equipment.JolCraftArmorItemSet;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimPatterns;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftArmorItems {

    private JolCraftArmorItems() {}

    // -------------------------------------------------------------------------
    // DEEPSLATE
    // -------------------------------------------------------------------------

    public static final JolCraftArmorItemSet DEEPSLATE = JolCraftArmorItemSet.of(
            registerDeepslate(JolCraftItemIds.DEEPSLATE_HELMET, ArmorItem.Type.HELMET),
            registerDeepslate(JolCraftItemIds.DEEPSLATE_CHESTPLATE, ArmorItem.Type.CHESTPLATE),
            registerDeepslate(JolCraftItemIds.DEEPSLATE_LEGGINGS, ArmorItem.Type.LEGGINGS),
            registerDeepslate(JolCraftItemIds.DEEPSLATE_BOOTS, ArmorItem.Type.BOOTS)
    );

    // -------------------------------------------------------------------------
    // MITHRIL
    // -------------------------------------------------------------------------

    public static final JolCraftArmorItemSet MITHRIL = JolCraftArmorItemSet.of(
            registerMithril(JolCraftItemIds.MITHRIL_HELMET, ArmorItem.Type.HELMET),
            registerMithril(JolCraftItemIds.MITHRIL_CHESTPLATE, ArmorItem.Type.CHESTPLATE),
            registerMithril(JolCraftItemIds.MITHRIL_LEGGINGS, ArmorItem.Type.LEGGINGS),
            registerMithril(JolCraftItemIds.MITHRIL_BOOTS, ArmorItem.Type.BOOTS)
    );

    // -------------------------------------------------------------------------
    // Armor Trim Templates
    // -------------------------------------------------------------------------

    public static DeferredItem<Item> registerForgeArmorTrimTemplate() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE,
                props -> SmithingTemplateItem.createArmorTrimTemplate(
                        JolCraftTrimPatterns.FORGE
                )
        );
    }
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DeferredItem<Item> registerDeepslate(String id, ArmorItem.Type type) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new DeepslateArmorItem(
                        JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.DEEPSLATE),
                        type,
                        props.durability(JolCraftArmorMaterials.durability(JolCraftMaterials.Material.DEEPSLATE, type))
                )
        );
    }

    private static DeferredItem<Item> registerMithril(String id, ArmorItem.Type type) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new MithrilArmorItem(
                        JolCraftArmorMaterials.armorMaterial(JolCraftMaterials.Material.MITHRIL),
                        type,
                        JolCraftItemRegistryHelper.mithrilProperties(
                                props.durability(JolCraftArmorMaterials.durability(JolCraftMaterials.Material.MITHRIL, type)))
                )
        );
    }
}