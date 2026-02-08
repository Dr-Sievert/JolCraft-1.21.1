package net.sievert.jolcraft.world.item.custom.armor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ArmorSetItem extends ArmorItem {

    private static final int DEFAULT_EFFECT_DURATION_TICKS = 300;

    protected ArmorSetItem(
            ArmorMaterial material,
            ArmorType armorType,
            Properties properties
    ) {
        super(material, armorType, material.humanoidProperties(properties, armorType));
    }

    /**
     * The JolCraft material that must be worn as a full suit for effects to apply.
     */
    protected abstract @NotNull JolCraftMaterials.Material targetMaterial();

    /**
     * Effects to apply while the full suit is worn.
     * The duration field is ignored; {@link #effectDurationTicks()} is used.
     */
    protected abstract @NotNull List<MobEffectInstance> effects();

    /**
     * Duration for effects applied by this set.
     */
    protected int effectDurationTicks() {
        return DEFAULT_EFFECT_DURATION_TICKS;
    }

    @Override
    public final void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        if (fullSuitMaterial(player) != targetMaterial()) return;

        applyEffects(player);
    }

    private void applyEffects(Player player) {
        int duration = effectDurationTicks();

        for (MobEffectInstance template : effects()) {
            if (player.hasEffect(template.getEffect())) continue;

            player.addEffect(new MobEffectInstance(
                    template.getEffect(),
                    duration,
                    template.getAmplifier(),
                    template.isAmbient(),
                    template.isVisible()
            ));
        }
    }

    /**
     * Returns the JolCraft material iff the player is wearing a full suit of that material,
     * based on EQUIPPABLE.assetId (EquipmentAsset key). Otherwise returns null.
     */
    protected static @Nullable JolCraftMaterials.Material fullSuitMaterial(Player player) {
        JolCraftMaterials.Material head = materialFromStack(player.getItemBySlot(EquipmentSlot.HEAD));
        if (head == null) return null;

        JolCraftMaterials.Material chest = materialFromStack(player.getItemBySlot(EquipmentSlot.CHEST));
        if (chest != head) return null;

        JolCraftMaterials.Material legs = materialFromStack(player.getItemBySlot(EquipmentSlot.LEGS));
        if (legs != head) return null;

        JolCraftMaterials.Material feet = materialFromStack(player.getItemBySlot(EquipmentSlot.FEET));
        if (feet != head) return null;

        return head;
    }

    protected static @Nullable JolCraftMaterials.Material materialFromStack(ItemStack stack) {
        if (stack.isEmpty()) return null;

        if (!JolCraftEquipmentHelper.isArmor(stack)) return null;

        Equippable equip = stack.get(DataComponents.EQUIPPABLE);
        if (equip == null) return null;

        if (equip.assetId().isEmpty()) return null;

        Object assetId = equip.assetId().get();

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            if (assetId.equals(material.equipmentAssetKey())) {
                return material;
            }
        }

        return null;
    }
}