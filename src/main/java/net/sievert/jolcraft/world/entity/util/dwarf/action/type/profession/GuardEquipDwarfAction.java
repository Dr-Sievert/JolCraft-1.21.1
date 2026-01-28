package net.sievert.jolcraft.world.entity.util.dwarf.action.type.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.profession.GuardEquipInteraction;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

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
        EquipmentSlot slot = GuardEquipInteraction.getSlotForArmor(itemstack);
        assert slot != null;
        dwarf.setItemSlot(slot, itemstack);
        dwarf.level().playSound(
                null,
                dwarf.blockPosition(),
                JolCraftSounds.ARMOR_EQUIP_DEEPSLATE.get(),
                SoundSource.NEUTRAL,
                1.0F,
                1.05F
        );
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

        if (dwarf.level().isClientSide()) {
            dwarf.setItemSlot(EquipmentSlot.MAINHAND, previousMainHandItem);
            this.previousMainHandItem = ItemStack.EMPTY;
            return;
        }

        dwarf.increaseMerchantCareer();
        dwarf.updateMerchantTimer = 40;

        if (player != null) {
            int newLevel = dwarf.getVillagerData().getLevel();
            Component rank = Component.translatable("merchant.level." + newLevel);
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_GUARD_PROMOTION, rank)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            if (!player.isCreative()) {
                player.getItemInHand(hand).shrink(1);
            }
        }

        dwarf.setItemSlot(EquipmentSlot.MAINHAND, previousMainHandItem);
        this.previousMainHandItem = ItemStack.EMPTY;
    }
}
