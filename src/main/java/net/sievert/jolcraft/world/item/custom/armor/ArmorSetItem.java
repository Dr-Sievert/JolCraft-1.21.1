package net.sievert.jolcraft.world.item.custom.armor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.effect.JolCraftOwnedEffectHelper;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ArmorSetItem extends ArmorItem {

    private static final String NBT_ARMOR_SET_EFFECTS =
            JolCraftStrings.underscored(
                    JolCraft.MOD_ID,
                    JolCraftDictionary.ARMOR,
                    JolCraftDictionary.SET,
                    JolCraftStrings.plural(JolCraftDictionary.EFFECT)
            );

    private static final boolean EFFECT_AMBIENT = false;
    private static final boolean EFFECT_PARTICLES = false;
    private static final boolean EFFECT_ICON = true;

    protected ArmorSetItem(
            Holder<ArmorMaterial> material,
            Type type,
            Properties properties
    ) {
        super(material, type, properties);
    }

    protected abstract @NotNull JolCraftMaterials.Material material();
    protected abstract @NotNull List<ArmorSetEffect> effects();

    protected record ArmorSetEffect(
            Holder<MobEffect> effect,
            int amplifier
    ) {
        private String id() {
            return this.effect.unwrapKey()
                    .orElseThrow()
                    .location()
                    .toString();
        }
    }

    @Override
    public final void inventoryTick(
            ItemStack stack,
            Level level,
            Entity entity,
            int slotId,
            boolean isSelected
    ) {
        if (level.isClientSide
                || !(entity instanceof Player player)) {
            return;
        }

        updateSetEffects(
                player,
                hasFullSet(player)
        );
    }

    private boolean hasFullSet(Player player) {
        for (Type type : JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES) {
            ItemStack stack =
                    player.getItemBySlot(type.getSlot());

            if (stack.isEmpty()) return false;

            var armorMaterial =
                    JolCraftEquipmentHelper.armorMaterial(stack);

            if (armorMaterial == null
                    || armorMaterial
                    != JolCraftArmorMaterials.armorMaterial(material())) {
                return false;
            }
        }

        return true;
    }

    private void updateSetEffects(
            Player player,
            boolean hasFullSet
    ) {
        for (ArmorSetEffect effect : effects()) {
            JolCraftOwnedEffectHelper.syncInfinite(
                    player,
                    effect.effect(),
                    effect.amplifier(),
                    NBT_ARMOR_SET_EFFECTS,
                    effect.id(),
                    hasFullSet,
                    EFFECT_AMBIENT,
                    EFFECT_PARTICLES,
                    EFFECT_ICON
            );
        }
    }
}
