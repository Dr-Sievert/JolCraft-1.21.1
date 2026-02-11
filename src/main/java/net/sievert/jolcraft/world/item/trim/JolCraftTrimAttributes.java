package net.sievert.jolcraft.world.item.trim;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class JolCraftTrimAttributes {

    private JolCraftTrimAttributes(){}

    private static final Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> ATTRIBUTES =
            buildAttributes();

    private static Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> buildAttributes() {
        Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> out =
                new EnumMap<>(JolCraftTrimMaterials.Attribute.class);

        out.put(JolCraftTrimMaterials.Attribute.AEGISCORE,
                allSlots(Attributes.ARMOR_TOUGHNESS, 0.5, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.ASHFANG,
                allSlots(JolCraftAttributes.ATTACK_DAMAGE_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.DEEPMARROW,
                allSlots(JolCraftAttributes.XP_INCREASE, 0.125, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.EARTHBLOOD,
                allSlots(Attributes.MINING_EFFICIENCY, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        out.put(JolCraftTrimMaterials.Attribute.EMBERGLASS,
                allSlots(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.FROSTVEIN,
                allSlots(JolCraftAttributes.SLOW_RESISTANCE, 0.2, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.GRIMSTONE,
                allSlots(Attributes.ATTACK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        out.put(JolCraftTrimMaterials.Attribute.IRONHEART,
                allSlots(JolCraftAttributes.ARMOR_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.LUMIERE,
                allSlots(JolCraftAttributes.RADIANT, 0.25, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.MOONSHARD,
                allSlots(JolCraftAttributes.MOVEMENT_SPEED_NIGHT_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.RUSTAGATE,
                allSlots(JolCraftAttributes.ARMOR_UNBREAKING, 0.075, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.SKYBURROW,
                allSlots(JolCraftAttributes.MOVEMENT_SPEED_DAY_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.SUNGLEAM,
                allSlots(JolCraftAttributes.CHEST_LOOT_INCREASE, 0.1, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.VERDANITE,
                allSlots(JolCraftAttributes.CROP_LOOT_INCREASE, 0.25, AttributeModifier.Operation.ADD_VALUE));
        out.put(JolCraftTrimMaterials.Attribute.WOECRYSTAL,
                allSlots(JolCraftAttributes.MAGIC_RESISTANCE, 0.1, AttributeModifier.Operation.ADD_VALUE));

        return Map.copyOf(out);
    }

    public static void applyAttribute(ItemStack stack, ArmorTrim trim) {
        JolCraftTrimMaterials.Attribute trimAttribute = resolveAttribute(trim);
        if (trimAttribute == null) return;

        List<TrimAttribute> attributes = ATTRIBUTES.get(trimAttribute);
        if (attributes == null || attributes.isEmpty()) return;

        EquipmentSlot slot = getSlotForArmor(stack);
        if (slot == null) return;

        ItemAttributeModifiers oldModifiers = stack.getOrDefault(
                DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY
        );

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        for (ItemAttributeModifiers.Entry entry : oldModifiers.modifiers()) {
            ResourceLocation id = entry.modifier().id();
            if (!JolCraft.MOD_ID.equals(id.getNamespace())) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }

        for (TrimAttribute attr : attributes) {
            if (attr.slot() != slot) continue;

            ResourceLocation modifierId = JolCraft.location(
                    "trim/%s/%s/%s".formatted(
                            trimAttribute.id(),
                            attributePath(attr.attribute()),
                            slot.getName()
                    )
            );

            builder.add(
                    attr.attribute(),
                    new AttributeModifier(modifierId, attr.amount(), attr.operation()),
                    EquipmentSlotGroup.bySlot(slot)
            );
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private static JolCraftTrimMaterials.Attribute resolveAttribute(ArmorTrim trim) {
        String path = trim.material().unwrapKey()
                .map(k -> k.location().getPath())
                .orElse("");

        if (path.isEmpty()) return null;

        try {
            return JolCraftTrimMaterials.Attribute.valueOf(path.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Returns the armor slot if (and only if) this stack is equippable armor.
     */
    public static EquipmentSlot getSlotForArmor(ItemStack stack) {
        return JolCraftEquipmentHelper.armorSlot(stack);
    }

    private static List<TrimAttribute> allSlots(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation
    ) {
        return Arrays.stream(JolCraftEquipmentHelper.ArmorPiece.values())
                .map(piece -> new TrimAttribute(attribute, amount, operation, piece.slot()))
                .toList();
    }

    private static String attributePath(Holder<Attribute> attribute) {
        return attribute.unwrapKey()
                .map(k -> k.location().getPath())
                .orElse(JolCraftDictionary.UNKNOWN);
    }

    public record TrimAttribute(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation,
            EquipmentSlot slot
    ) {
    }
}