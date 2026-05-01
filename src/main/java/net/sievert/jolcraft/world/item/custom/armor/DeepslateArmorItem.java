package net.sievert.jolcraft.world.item.custom.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public final class DeepslateArmorItem extends ArmorSetItem {

    private static final double MOVEMENT_SPEED_MODIFIER = -0.05;

    public DeepslateArmorItem(
            Holder<ArmorMaterial> material,
            ArmorItem.Type type,
            Properties properties
    ) {
        super(material, type, properties);
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers() {
        return super.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                        JolCraft.location(JolCraftStrings.underscored(
                                JolCraftDictionary.DEEPSLATE,
                                JolCraftDictionary.SPEED,
                                this.getType().getName()
                        )),
                        MOVEMENT_SPEED_MODIFIER,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ),
                EquipmentSlotGroup.bySlot(this.getType().getSlot())
        );
    }

    @Override
    protected JolCraftMaterials.@NotNull Material material() {
        return JolCraftMaterials.Material.DEEPSLATE;
    }

    @Override
    protected @NotNull List<ArmorSetEffect> effects() {
        return List.of(new ArmorSetEffect(MobEffects.DAMAGE_RESISTANCE, 0));
    }
}