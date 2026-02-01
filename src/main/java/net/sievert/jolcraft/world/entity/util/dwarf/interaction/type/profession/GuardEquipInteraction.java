package net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.profession;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.Nullable;
import java.util.Map;

public class GuardEquipInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        EquipmentSlot slot = (itemstack == null) ? null : getSlotForArmor(itemstack);
        if (slot == null || !dwarf.getItemBySlot(slot).isEmpty()) {
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.FAIL;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.GUARD_EQUIP, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }

    private static final Map<Item, EquipmentSlot> ARMOR_SLOTS = Map.of(
            JolCraftItems.DEEPSLATE_HELMET.get(), EquipmentSlot.HEAD,
            JolCraftItems.DEEPSLATE_CHESTPLATE.get(), EquipmentSlot.CHEST,
            JolCraftItems.DEEPSLATE_LEGGINGS.get(), EquipmentSlot.LEGS,
            JolCraftItems.DEEPSLATE_BOOTS.get(), EquipmentSlot.FEET
    );

    @Nullable
    public static EquipmentSlot getSlotForArmor(ItemStack stack) {
        return ARMOR_SLOTS.get(stack.getItem());
    }

}
