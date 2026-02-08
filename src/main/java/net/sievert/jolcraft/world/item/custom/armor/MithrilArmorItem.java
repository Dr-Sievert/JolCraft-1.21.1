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
public final class MithrilArmorItem extends ArmorSetItem {

    private static final List<MobEffectInstance> EFFECTS = List.of(
            new MobEffectInstance(MobEffects.GLOWING, 0, 0, false, false),
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 0, 0, false, false)
    );

    public MithrilArmorItem(
            ArmorMaterial material,
            ArmorType armorType,
            Properties properties
    ) {
        super(material, armorType, properties);
    }

    @Override
    protected JolCraftMaterials.@NotNull Material targetMaterial() {
        return JolCraftMaterials.Material.MITHRIL;
    }

    @Override
    protected @NotNull List<MobEffectInstance> effects() {
        return EFFECTS;
    }
}