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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JolCraftTrimAttributes {

    private JolCraftTrimAttributes() {}

    private static final Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> ATTRIBUTES = buildAttributes();

    private static Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> buildAttributes() {
        Map<JolCraftTrimMaterials.Attribute, List<TrimAttribute>> out =
                new EnumMap<>(JolCraftTrimMaterials.Attribute.class);

        out.put(
                JolCraftTrimMaterials.Attribute.AEGISCORE,
                attributes(
                        new TrimAttribute(
                                Attributes.ARMOR,
                                1.25D,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        new TrimAttribute(
                                Attributes.ARMOR_TOUGHNESS,
                                0.5D,
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        );

        out.put(JolCraftTrimMaterials.Attribute.ASHFANG,
                attributes(Attributes.ATTACK_DAMAGE, 0.05D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        out.put(JolCraftTrimMaterials.Attribute.DEEPMARROW,
                attributes(JolCraftAttributes.EXPERIENCE_INCREASE, 0.125D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.EARTHBLOOD,
                attributes(Attributes.MINING_EFFICIENCY, 2.0D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.EMBERGLASS,
                attributes(Attributes.MAX_HEALTH, 2.0D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.FROSTVEIN,
                attributes(JolCraftAttributes.SLOW_RESISTANCE, 0.20D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.GRIMSTONE,
                attributes(Attributes.ATTACK_SPEED, 0.05D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        out.put(JolCraftTrimMaterials.Attribute.IRONHEART,
                attributes(
                        new TrimAttribute(
                                Attributes.ARMOR,
                                0.05D,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        ), new TrimAttribute(
                                Attributes.ARMOR_TOUGHNESS,
                                0.05D,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                )
        );

        out.put(JolCraftTrimMaterials.Attribute.LUMIERE,
                attributes(JolCraftAttributes.RADIANT, 1.0D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.MOONSHARD,
                attributes(JolCraftAttributes.MOON_SHIELD, 1.0D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.RUSTAGATE,
                attributes(JolCraftAttributes.ARMOR_PENETRATION, 0.20D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.SKYBURROW,
                attributes(JolCraftAttributes.ITEM_USE_SPEED, 0.20D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.SUNGLEAM,
                attributes(JolCraftAttributes.CONTAINER_LOOT_INCREASE, 0.10D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.VERDANITE,
                attributes(JolCraftAttributes.CROP_LOOT_INCREASE, 0.125D, AttributeModifier.Operation.ADD_VALUE));

        out.put(JolCraftTrimMaterials.Attribute.WOECRYSTAL,
                attributes(JolCraftAttributes.MAGIC_RESISTANCE, 0.10D, AttributeModifier.Operation.ADD_VALUE));

        return Map.copyOf(out);
    }

    public static void applyAttribute(@NotNull ItemStack stack, @NotNull ArmorTrim trim) {
        List<TrimAttribute> attributes = getTrimAttributes(getAttributeTrim(trim));

        EquipmentSlot slot = getSlotForArmor(stack);
        if (slot == null) {
            throw new IllegalStateException("Item is not valid armor for trim attribute: " + stack);
        }

        ItemAttributeModifiers modifiers = stack.getOrDefault(
                DataComponents.ATTRIBUTE_MODIFIERS,
                stack.getItem().getDefaultAttributeModifiers(stack)
        );

        modifiers = removeOldTrimModifiers(modifiers);

        for (int i = 0; i < attributes.size(); i++) {
            TrimAttribute attribute = attributes.get(i);

            modifiers = modifiers.withModifierAdded(
                    attribute.attribute(),
                    new AttributeModifier(
                            trimModifierId(slot, i),
                            attribute.amount(),
                            attribute.operation()
                    ),
                    EquipmentSlotGroup.bySlot(slot)
            );
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }

    private static JolCraftTrimMaterials.Attribute getAttributeTrim(@NotNull ArmorTrim trim) {
        ResourceLocation id = trim.material().unwrapKey()
                .map(ResourceKey::location)
                .orElseThrow(() -> new IllegalStateException("Trim material has no registry key: " + trim));

        return Arrays.stream(JolCraftTrimMaterials.Attribute.values())
                .filter(attribute -> attribute.getId().equals(id.getPath()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown attribute trim material: " + id));
    }

    public static List<TrimAttribute> getTrimAttributes(@NotNull JolCraftTrimMaterials.Attribute trim) {
        List<TrimAttribute> attributes = ATTRIBUTES.get(trim);

        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalStateException("Missing attribute definition for: " + trim);
        }

        return attributes;
    }

    private static ItemAttributeModifiers removeOldTrimModifiers(@NotNull ItemAttributeModifiers modifiers) {
        Set<ResourceLocation> legacyIds = new HashSet<>();

        for (JolCraftTrimMaterials.Attribute attribute : JolCraftTrimMaterials.Attribute.values()) {
            legacyIds.add(JolCraft.location(attribute.getId()));

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                legacyIds.add(JolCraft.location(attribute.getId() + "_" + slot.getName()));
            }
        }

        return new ItemAttributeModifiers(
                modifiers.modifiers().stream()
                        .filter(entry -> !isTrimModifier(entry.modifier().id(), legacyIds))
                        .toList(),
                modifiers.showInTooltip()
        );
    }

    private static boolean isTrimModifier(
            @NotNull ResourceLocation id,
            @NotNull Set<ResourceLocation> legacyIds
    ) {
        if (legacyIds.contains(id)) {
            return true;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (id.getNamespace().equals(JolCraft.MOD_ID)
                    && id.getPath().startsWith("attribute_trim_" + slot.getName())) {
                return true;
            }
        }

        return false;
    }

    private static @NotNull ResourceLocation trimModifierId(
            @NotNull EquipmentSlot slot,
            int index
    ) {
        return JolCraft.location("attribute_trim_" + slot.getName() + "_" + index);
    }

    public static EquipmentSlot getSlotForArmor(ItemStack stack) {
        ArmorItem.Type type = JolCraftEquipmentHelper.armorType(stack);
        return type == null ? null : type.getSlot();
    }

    private static List<TrimAttribute> attributes(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation
    ) {
        return List.of(new TrimAttribute(attribute, amount, operation));
    }

    private static List<TrimAttribute> attributes(TrimAttribute... attributes) {
        return List.of(attributes);
    }

    public record TrimAttribute(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation
    ) {}
}