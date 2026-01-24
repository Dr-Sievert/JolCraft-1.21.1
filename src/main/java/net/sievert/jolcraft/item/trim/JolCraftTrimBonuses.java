package net.sievert.jolcraft.item.trim;

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
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftAttributes;

import java.util.List;
import java.util.Map;

public final class JolCraftTrimBonuses {

    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    );

    public static final Map<String, List<TrimAttributeBonus>> TRIM_BONUSES = Map.ofEntries(
            Map.entry("aegiscore", allSlots(Attributes.ARMOR_TOUGHNESS, 0.5, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("ashfang", allSlots(JolCraftAttributes.ATTACK_DAMAGE_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("deepmarrow", allSlots(JolCraftAttributes.XP_BOOST, 0.125, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("earthblood", allSlots(Attributes.MINING_EFFICIENCY, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
            Map.entry("emberglass", allSlots(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("frostvein", allSlots(JolCraftAttributes.SLOW_RESIST, 0.2, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("grimstone", allSlots(Attributes.ATTACK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
            Map.entry("ironheart", allSlots(JolCraftAttributes.ARMOR_INCREASE, 0.05, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("lumiere", allSlots(JolCraftAttributes.RADIANT, 0.25, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("moonshard", allSlots(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT, 0.05, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("rustagate", allSlots(JolCraftAttributes.ARMOR_UNBREAKING, 0.075, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("skyburrow", allSlots(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY, 0.05, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("sungleam", allSlots(JolCraftAttributes.EXTRA_CHEST_LOOT, 0.1, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("verdanite", allSlots(JolCraftAttributes.EXTRA_CROP, 0.25, AttributeModifier.Operation.ADD_VALUE)),
            Map.entry("woecrystal", allSlots(JolCraftAttributes.MAGIC_RESISTANCE, 0.1, AttributeModifier.Operation.ADD_VALUE))
    );

    private JolCraftTrimBonuses() {}

    public static void applyBonus(ItemStack stack, ArmorTrim trim) {
        String materialKey = trim.material().unwrapKey()
                .map(k -> k.location().getPath())
                .orElse("");

        List<TrimAttributeBonus> bonuses = TRIM_BONUSES.get(materialKey);
        if (bonuses == null || bonuses.isEmpty()) return;

        EquipmentSlot thisSlot = getSlotForArmor(stack);
        if (thisSlot == null) return;

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

        for (TrimAttributeBonus bonus : bonuses) {
            if (bonus.slot != thisSlot) continue;

            ResourceLocation modifierId = JolCraft.location(
                    "trim/%s/%s/%s".formatted(
                            materialKey,
                            attributePath(bonus.attribute),
                            thisSlot.getName()
                    )
            );

            builder.add(
                    bonus.attribute,
                    new AttributeModifier(modifierId, bonus.amount, bonus.operation),
                    EquipmentSlotGroup.bySlot(thisSlot)
            );
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    public static EquipmentSlot getSlotForArmor(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null ? equippable.slot() : null;
    }

    private static List<TrimAttributeBonus> allSlots(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation
    ) {
        return ARMOR_SLOTS.stream()
                .map(slot -> new TrimAttributeBonus(attribute, amount, operation, slot))
                .toList();
    }

    private static String attributePath(Holder<Attribute> attribute) {
        return attribute.unwrapKey()
                .map(k -> k.location().getPath())
                .orElse("unknown");
    }

    public record TrimAttributeBonus(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation,
            EquipmentSlot slot
    ) {}
}