package net.sievert.jolcraft.world.entity.util.dwarf.action.type;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class InspectDwarfAction implements DwarfAction {

    protected final AbstractDwarfEntity dwarf;
    protected final Player player;
    protected final InteractionHand hand;
    protected final ItemStack itemstack;
    protected ItemStack previousMainHandItem = ItemStack.EMPTY;


    public InspectDwarfAction (AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack){
        this.dwarf = dwarf;
        this.player = player;
        this.hand = hand;
        this.itemstack = itemstack;
    }

    @Override public DwarfActionType getType() { return DwarfActionType.INSPECT; }

    protected void startInspect(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        dwarf.usePlayerItem(player, hand, itemstack);
        PlaySound.dwarfYes(dwarf);
        previousMainHandItem = dwarf.getMainHandItem().copy();
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
    }

    protected void throwItem(AbstractDwarfEntity dwarf, Player player, ItemStack thrownItem) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        Vec3 start = dwarf.position().add(0.0, dwarf.getEyeHeight(), 0.0);
        Vec3 target = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
        Vec3 velocity = target.subtract(start).normalize().scale(0.4);
        ItemEntity thrown = new ItemEntity(dwarf.level(), start.x, start.y, start.z, thrownItem);
        thrown.setDeltaMovement(velocity);
        thrown.setPickUpDelay(10);
        dwarf.level().addFreshEntity(thrown);
        JolCraftSoundHelper.entity(
                dwarf,
                SoundEvents.SNOWBALL_THROW,
                0.5F,
                0.8F
        );
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, previousMainHandItem);
        previousMainHandItem = ItemStack.EMPTY;
    }


}
