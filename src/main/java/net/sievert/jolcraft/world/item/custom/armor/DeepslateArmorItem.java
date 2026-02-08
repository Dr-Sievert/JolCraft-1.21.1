package net.sievert.jolcraft.world.item.custom.armor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public final class DeepslateArmorItem extends ArmorSetItem {

    private static final List<MobEffectInstance> EFFECTS = List.of(
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 0, 0, false, false),
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 0, 0, false, false)
    );

    public DeepslateArmorItem(
            ArmorMaterial material,
            ArmorType armorType,
            Properties properties
    ) {
        super(material, armorType, properties);
    }

    @Override
    protected JolCraftMaterials.@NotNull Material targetMaterial() {
        return JolCraftMaterials.Material.DEEPSLATE;
    }

    @Override
    protected @NotNull List<MobEffectInstance> effects() {
        return EFFECTS;
    }
}