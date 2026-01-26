package net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

import java.util.EnumSet;
import java.util.Objects;


public class DwarfRevengeGoal extends Goal
{
    private final AbstractDwarfEntity dwarf;

    public DwarfRevengeGoal(AbstractDwarfEntity entity)
    {
        this.dwarf = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        return this.dwarf.getLastHurtByMob() != null && this.dwarf.getLastHurtByMob().isAlive() && this.dwarf.distanceTo(this.dwarf.getLastHurtByMob()) <= 10.0F && (!(this.dwarf.getLastHurtByMob() instanceof Player) || !((Player)this.dwarf.getLastHurtByMob()).isCreative());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        LivingEntity revengeTarget = this.dwarf.getLastHurtByMob();
        if (revengeTarget != null && this.dwarf.getTradingPlayer() == null && revengeTarget instanceof Player) {
            this.dwarf.getLookControl().setLookAt(revengeTarget, 10.0F, (float) this.dwarf.getHeadRotSpeed());
            if (this.dwarf.distanceTo(revengeTarget) >= 1.5D) {
                this.dwarf.getNavigation().moveTo(revengeTarget, 1.3F);
            } else {
                // Make sure the attack damage attribute is correct right before the attack:
                this.dwarf.setCustomAttackDamage(this.dwarf.getAttackDamage());
                revengeTarget.hurt(this.dwarf.damageSources().mobAttack(this.dwarf), (float) Objects.requireNonNull(this.dwarf.getAttribute(Attributes.ATTACK_DAMAGE)).getValue());
                DwarfAttackGoal.chooseAndSetAttackAction(dwarf, dwarf.getActionHelper());
                this.dwarf.setLastHurtByMob(null);
            }
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        return this.dwarf.getLastHurtByMob() != null && this.dwarf.getLastHurtByMob().isAlive() && this.dwarf.distanceTo(this.dwarf.getLastHurtByMob()) <= 10.0F && this.dwarf.getTradingPlayer() == null;
    }

    @Override
    public void stop()
    {
        this.dwarf.getActionHelper().stopAction(dwarf);
        this.dwarf.setLastHurtByMob(null);
    }
}