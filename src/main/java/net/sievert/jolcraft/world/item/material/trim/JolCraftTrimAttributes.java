package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class JolCraftTrimAttributes {

    private JolCraftTrimAttributes(){}

    private static final Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> ATTRIBUTES = buildAttributes();

    private static Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> buildAttributes() {
        Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> out = new EnumMap<>(JolCraftTrimMaterials.Attribute.class);

        out.put(JolCraftTrimMaterials.Attribute.AEGISCORE, allSlots(Attributes.ARMOR_TOUGHNESS, 0.5, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.ASHFANG, allSlots(JolCraftAttributes.ATTACK_DAMAGE_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.DEEPMARROW, allSlots(JolCraftAttributes.EXPERIENCE_INCREASE, 0.125, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.EARTHBLOOD, allSlots(Attributes.MINING_EFFICIENCY, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        out.put(JolCraftTrimMaterials.Attribute.EMBERGLASS, allSlots(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.FROSTVEIN, allSlots(JolCraftAttributes.SLOW_RESISTANCE, 0.2, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.GRIMSTONE, allSlots(Attributes.ATTACK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        out.put(JolCraftTrimMaterials.Attribute.IRONHEART, allSlots(JolCraftAttributes.ARMOR_TOTAL, 0.05, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.LUMIERE, allSlots(JolCraftAttributes.RADIANT, 1, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.MOONSHARD, allSlots(JolCraftAttributes.MOON_SHIELD, 1, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.RUSTAGATE, allSlots(JolCraftAttributes.ARMOR_PENETRATION, 0.15, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.SKYBURROW, allSlots(JolCraftAttributes.ITEM_USE_SPEED, 0.25, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.SUNGLEAM, allSlots(JolCraftAttributes.CHEST_LOOT_INCREASE, 0.1, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.VERDANITE, allSlots(JolCraftAttributes.CROP_LOOT_INCREASE, 0.25, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.WOECRYSTAL, allSlots(JolCraftAttributes.MAGIC_RESISTANCE, 0.1, AttributeModifier.Operation.ADD_VALUE));

        return Map.copyOf(out);
    }

    public static void applyAttribute(@NotNull ItemStack stack, @NotNull ArmorTrim trim) {
        JolCraftTrimMaterials.Attribute match = getAttributeTrim(trim);
        TrimAttribute attr = getTrimAttribute(match);

        EquipmentSlot slot = getSlotForArmor(stack);
        if (slot == null) {
            throw new IllegalStateException("Item is not valid armor for trim attribute: " + stack);
        }

        ItemAttributeModifiers modifiers = stack.getItem().getDefaultAttributeModifiers(stack);

        ItemAttributeModifiers existing = stack.getOrDefault(
                DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY
        );

        existing = removeOldTrimModifiers(existing);

        for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
            modifiers = modifiers.withModifierAdded(
                    entry.attribute(),
                    entry.modifier(),
                    entry.slot()
            );
        }

        ResourceLocation modifierId = JolCraft.location(match.getId() + "_" + slot.getName());

        modifiers = modifiers.withModifierAdded(
                attr.attribute(),
                new AttributeModifier(modifierId, attr.amount(), attr.operation()),
                EquipmentSlotGroup.bySlot(slot)
        );

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }
    private static JolCraftTrimMaterials.Attribute getAttributeTrim(@NotNull ArmorTrim trim) {
        ResourceLocation id = trim.material().unwrapKey()
                .map(ResourceKey::location)
                .orElseThrow(() -> new IllegalStateException("Trim material has no registry key: " + trim));

        return Arrays.stream(JolCraftTrimMaterials.Attribute.values())
                .filter(attr -> attr.getId().equals(id.getPath()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown attribute trim material: " + id));
    }

    public static TrimAttribute getTrimAttribute(@NotNull JolCraftTrimMaterials.Attribute trim) {
        List<TrimAttribute> list = ATTRIBUTES.get(trim);
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("Missing attribute definition for: " + trim);
        }
        return list.getFirst();
    }

    private static ItemAttributeModifiers removeOldTrimModifiers(@NotNull ItemAttributeModifiers modifiers) {
        Set<ResourceLocation> trimIds = Arrays.stream(JolCraftTrimMaterials.Attribute.values())
                .map(attr -> JolCraft.location(attr.getId()))
                .collect(Collectors.toSet());

        return new ItemAttributeModifiers(
                modifiers.modifiers().stream()
                        .filter(entry -> !trimIds.contains(entry.modifier().id()))
                        .toList(),
                modifiers.showInTooltip()
        );
    }

    public static EquipmentSlot getSlotForArmor(ItemStack stack) {
        ArmorItem.Type type = JolCraftEquipmentHelper.armorType(stack);
        return type == null ? null : type.getSlot();
    }

    private static List<TrimAttribute> allSlots(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation
    ) {
        return JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES.stream()
                .map(type -> new TrimAttribute(attribute, amount, operation, type.getSlot()))
                .toList();
    }

    public record TrimAttribute(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation,
            EquipmentSlot slot
    ) {}
}