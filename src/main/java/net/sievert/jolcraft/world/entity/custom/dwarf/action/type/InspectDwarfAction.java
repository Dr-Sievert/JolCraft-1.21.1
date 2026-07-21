package net.sievert.jolcraft.world.entity.custom.dwarf.action.type;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class InspectDwarfAction implements DwarfAction {

    protected final AbstractDwarfEntity dwarf;
    protected final Player player;
    protected final InteractionHand hand;

    /**
     * Detached one-item snapshot created by DwarfActionHelper.
     *
     * This is not the player's live inventory stack.
     */
    protected final ItemStack itemstack;

    protected ItemStack previousMainHandItem =
            ItemStack.EMPTY;

    public InspectDwarfAction(
            AbstractDwarfEntity dwarf,
            Player player,
            InteractionHand hand,
            ItemStack itemstack
    ) {
        this.dwarf = dwarf;
        this.player = player;
        this.hand = hand;
        this.itemstack = itemstack.copyWithCount(1);
    }

    @Override
    public DwarfActionType getType() {
        return DwarfActionType.INSPECT;
    }

    @Override
    public boolean blocksMovement() {
        return true;
    }

    /**
     * Begins the visual inspection sequence.
     *
     * Item consumption is handled centrally by
     * DwarfInteractions.commit().
     */
    protected void startInspect(
            AbstractDwarfEntity dwarf,
            Player player,
            InteractionHand hand,
            ItemStack itemstack
    ) {
        previousMainHandItem =
                dwarf.getMainHandItem().copy();

        dwarf.setItemSlot(
                EquipmentSlot.MAINHAND,
                this.itemstack.copy()
        );

        PlaySound.dwarfYes(dwarf);
    }

    protected static void throwStack(
            ServerLevel level,
            Vec3 start,
            Vec3 velocity,
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return;
        }

        ItemEntity entity =
                new ItemEntity(
                        level,
                        start.x,
                        start.y,
                        start.z,
                        stack
                );

        entity.setDeltaMovement(velocity);
        entity.setPickUpDelay(10);

        level.addFreshEntity(entity);
    }

    protected void throwItem(
            AbstractDwarfEntity dwarf,
            Player player,
            ItemStack thrownItem
    ) {
        if (!(dwarf.level() instanceof ServerLevel level)) {
            return;
        }

        dwarf.setItemSlot(
                EquipmentSlot.MAINHAND,
                ItemStack.EMPTY
        );

        Vec3 start =
                dwarf.position().add(
                        0.0D,
                        dwarf.getEyeHeight(),
                        0.0D
                );

        Vec3 target =
                player.position().add(
                        0.0D,
                        player.getBbHeight() * 0.5D,
                        0.0D
                );

        Vec3 direction =
                target.subtract(start);

        Vec3 velocity =
                direction.lengthSqr() > 0.0D
                        ? direction.normalize().scale(0.4D)
                        : Vec3.ZERO;

        throwStack(
                level,
                start,
                velocity,
                thrownItem
        );

        JolCraftSoundHelper.entity(
                dwarf,
                SoundEvents.SNOWBALL_THROW,
                0.5F,
                0.8F
        );

        dwarf.setItemSlot(
                EquipmentSlot.MAINHAND,
                previousMainHandItem
        );

        previousMainHandItem =
                ItemStack.EMPTY;
    }
}