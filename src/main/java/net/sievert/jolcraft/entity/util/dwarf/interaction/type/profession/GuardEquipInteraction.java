package net.sievert.jolcraft.entity.util.dwarf.interaction.type.profession;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

import javax.annotation.Nullable;

public class GuardEquipInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        assert itemstack != null;
        EquipmentSlot slot = getSlotForArmor(itemstack);
        if (slot != null && dwarf.getItemBySlot(slot).isEmpty()) {
            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.GUARD_EQUIP, player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }else{
            JolCraftSoundHelper.playDwarfNo(dwarf);
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    public static EquipmentSlot getSlotForArmor(ItemStack stack) {
        if (stack.is(JolCraftItems.DEEPSLATE_HELMET.get())) return EquipmentSlot.HEAD;
        if (stack.is(JolCraftItems.DEEPSLATE_CHESTPLATE.get())) return EquipmentSlot.CHEST;
        if (stack.is(JolCraftItems.DEEPSLATE_LEGGINGS.get())) return EquipmentSlot.LEGS;
        if (stack.is(JolCraftItems.DEEPSLATE_BOOTS.get())) return EquipmentSlot.FEET;
        return null;
    }
}
