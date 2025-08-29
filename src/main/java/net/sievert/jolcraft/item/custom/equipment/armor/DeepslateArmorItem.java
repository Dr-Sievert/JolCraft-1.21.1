package net.sievert.jolcraft.item.custom.equipment.armor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.item.armor.JolCraftArmorMaterials;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class DeepslateArmorItem extends ArmorItem {

    private static final Map<ArmorMaterial, List<MobEffectInstance>> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<ArmorMaterial, List<MobEffectInstance>>())
                    .put(JolCraftArmorMaterials.DEEPSLATE_ARMOR_MATERIAL,
                            List.of(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false),
                                    new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 0, false, false)))
                    .build();

    public DeepslateArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(material, type, material.humanoidProperties(properties, type));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player && hasFullSuitOfArmorOn(player)) {
            evaluateArmorEffects(player);
        }
    }

    private void evaluateArmorEffects(Player player) {
        for (Map.Entry<ArmorMaterial, List<MobEffectInstance>> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            ArmorMaterial material = entry.getKey();
            List<MobEffectInstance> effects = entry.getValue();
            if(hasPlayerCorrectArmorOn(material, player)) {
                addEffectToPlayer(player, effects);
            }
        }
    }

    private void addEffectToPlayer(Player player, List<MobEffectInstance> mapEffect) {
        boolean hasPlayerEffect = mapEffect.stream().allMatch(effect -> player.hasEffect(effect.getEffect()));

        if(!hasPlayerEffect) {
            for (MobEffectInstance effect : mapEffect) {
                player.addEffect(new MobEffectInstance(effect.getEffect(),
                        effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
            }
        }
    }

    private boolean hasPlayerCorrectArmorOn(ArmorMaterial targetMaterial, Player player) {
        return isMatching(player, EquipmentSlot.HEAD, targetMaterial) &&
                isMatching(player, EquipmentSlot.CHEST, targetMaterial) &&
                isMatching(player, EquipmentSlot.LEGS, targetMaterial) &&
                isMatching(player, EquipmentSlot.FEET, targetMaterial);
    }

    private boolean isMatching(Player player, EquipmentSlot slot, ArmorMaterial targetMaterial) {
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.isEmpty()) return false;

        Equippable equip = stack.getComponents().get(DataComponents.EQUIPPABLE);
        return equip != null && equip.assetId().isPresent() && equip.assetId().get().equals(targetMaterial.assetId());
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        return !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() &&
                !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() &&
                !player.getItemBySlot(EquipmentSlot.LEGS).isEmpty() &&
                !player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }
}
