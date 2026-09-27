package net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf;


import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public class DwarfUseItemGoal<T extends Mob> extends Goal {
    private final T mob;
    private final ItemStack item;
    private final Predicate<? super T> canUseSelector;
    @Nullable
    private final SoundEvent finishUsingSound;
    protected ItemStack previousMainHandItem = ItemStack.EMPTY;
    private final int cooldownTicks;
    private int cooldownTimer = 0;
    private boolean startedUsingItem;

    public DwarfUseItemGoal(T mob, ItemStack item, @Nullable SoundEvent finishUsingSound, Predicate<? super T> canUseSelector, int cooldownTicks) {
        this.mob = mob;
        this.item = item;
        this.finishUsingSound = finishUsingSound;
        this.canUseSelector = canUseSelector;
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public boolean canUse() {
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }
        return this.mob.isAlive() && !this.mob.isUsingItem()
                && (!(this.mob instanceof AbstractDwarfEntity dwarf)
                    || dwarf.getActionHelper().isIdle())
                && this.canUseSelector.test(this.mob);
    }

    @Override
    public boolean canContinueToUse() {
        return this.startedUsingItem && this.mob.isAlive() && this.mob.isUsingItem()
                && (!(this.mob instanceof AbstractDwarfEntity dwarf)
                    || dwarf.getCurrentActionType() == DwarfActionType.DRINK);
    }

    @Override
    public void start() {
        this.startedUsingItem = false;
        if (this.mob instanceof AbstractDwarfEntity dwarf) {
            if (!dwarf.getActionHelper().trySetAction(
                    dwarf, DwarfActionType.DRINK, null, null, null, null)) {
                return;
            }
        }
        this.previousMainHandItem = this.mob.getItemBySlot(EquipmentSlot.MAINHAND).copy();
        this.mob.setItemSlot(EquipmentSlot.MAINHAND, this.item.copy());
        this.mob.startUsingItem(InteractionHand.MAIN_HAND);
        this.startedUsingItem = true;
    }

    @Override
    public void stop() {
        if (!this.startedUsingItem) {
            return;
        }
        this.startedUsingItem = false;
        if (this.mob instanceof AbstractDwarfEntity dwarf
                && dwarf.getCurrentActionType() == DwarfActionType.DRINK) {
            dwarf.getActionHelper().stopAction(dwarf);
        }
        this.mob.stopUsingItem();
        this.mob.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
        if (this.finishUsingSound != null) {
            JolCraftSoundHelper.entity(
                    this.mob,
                    this.finishUsingSound,
                    1.0F,
                    this.mob.getRandom().nextFloat() * 0.2F + 0.9F
            );
        }
        this.cooldownTimer = cooldownTicks;
        this.previousMainHandItem = ItemStack.EMPTY;

    }

}

