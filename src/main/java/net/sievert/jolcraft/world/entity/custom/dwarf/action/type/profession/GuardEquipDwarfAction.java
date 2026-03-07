package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public class GuardEquipDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;

    public GuardEquipDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.GUARD_EQUIP;}

    @Override
    public void start() {
        this.ticksRemaining = 40;
        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        EquipmentSlot slot = JolCraftEquipmentHelper.slotIfMatches(itemstack, JolCraftItems.DEEPSLATE_ARMOR_SET);
        if (slot == null) {
            if (dwarf.level().isClientSide()) {
                dwarf.setItemSlot(EquipmentSlot.MAINHAND, previousMainHandItem);
                this.previousMainHandItem = ItemStack.EMPTY;
            }
            return;
        }

        dwarf.setItemSlot(slot, itemstack);
        JolCraftSoundHelper.entity(dwarf, JolCraftSounds.ARMOR_EQUIP_DEEPSLATE.get());
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

        if (dwarf.level().isClientSide()) {
            dwarf.setItemSlot(EquipmentSlot.MAINHAND, previousMainHandItem);
            this.previousMainHandItem = ItemStack.EMPTY;
            return;
        }

        dwarf.increaseMerchantCareer();
        dwarf.updateMerchantTimer = 40;

        if (player != null) {
            int newLevel = dwarf.getMerchantLevel();
            Component rank = Component.translatable(DwarfMerchantData.Level.langKeyFromId(newLevel));
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARF_GUARD_PROMOTION, rank)
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
                    true
            );
        }
    }

}
