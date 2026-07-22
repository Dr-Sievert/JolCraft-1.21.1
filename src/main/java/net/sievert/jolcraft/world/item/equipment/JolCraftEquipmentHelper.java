package net.sievert.jolcraft.world.item.equipment;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftEquipmentHelper {

    private JolCraftEquipmentHelper() {}

    public static final List<ArmorItem.Type> PLAYER_ARMOR_TYPES = List.of(
            ArmorItem.Type.HELMET,
            ArmorItem.Type.CHESTPLATE,
            ArmorItem.Type.LEGGINGS,
            ArmorItem.Type.BOOTS
    );

    public static @Nullable ArmorItem.Type armorType(ItemStack stack) {
        if (stack.isEmpty()) return null;

        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getType();
        }

        return null;
    }

    public static @Nullable Holder<ArmorMaterial> armorMaterial(ItemStack stack) {
        if (stack.isEmpty()) return null;

        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getMaterial();
        }

        return null;
    }

    public static boolean isWeapon(ItemStack stack) {
        return isMeleeWeapon(stack) || isRangedWeapon(stack);
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(Tags.Items.MELEE_WEAPON_TOOLS);
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(Tags.Items.RANGED_WEAPON_TOOLS) || stack.getItem() instanceof ProjectileWeaponItem;
    }
}