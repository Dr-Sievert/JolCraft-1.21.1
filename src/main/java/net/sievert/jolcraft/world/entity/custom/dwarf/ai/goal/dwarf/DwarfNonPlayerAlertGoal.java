package net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

public class DwarfNonPlayerAlertGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING =
            TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final int ALERT_RANGE_Y = 10;

    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;

    @Nullable
    private Class<?>[] toIgnoreAlert;

    public DwarfNonPlayerAlertGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, true);
        this.toIgnoreDamage = toIgnoreDamage;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingEntity = this.mob.getLastHurtByMob();

        if (i == this.timestamp || livingEntity == null) {
            return false;
        }

        if (livingEntity instanceof Player) {
            return false;
        }

        for (Class<?> ignoredClass : this.toIgnoreDamage) {
            if (ignoredClass.isAssignableFrom(livingEntity.getClass())) {
                return false;
            }
        }

        return this.canAttack(livingEntity, HURT_BY_TARGETING);
    }

    public DwarfNonPlayerAlertGoal setAlertOthers(Class<?>... reinforcementTypes) {
        this.alertSameType = true;
        this.toIgnoreAlert = reinforcementTypes;
        return this;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;

        if (this.alertSameType) {
            this.alertOthers();
        }

        super.start();
    }

    protected void alertOthers() {
        double followDistance = this.getFollowDistance();
        AABB box = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(followDistance, ALERT_RANGE_Y, followDistance);

        List<AbstractDwarfEntity> list =
                this.mob.level().getEntitiesOfClass(AbstractDwarfEntity.class, box, EntitySelector.NO_SPECTATORS);

        LivingEntity attacker = this.mob.getLastHurtByMob();
        if (attacker == null) {
            return;
        }

        for (AbstractDwarfEntity other : list) {
            if (other == this.mob) {
                continue;
            }

            if (other.getTarget() != null) {
                continue;
            }

            if (other.isAlliedTo(attacker)) {
                continue;
            }

            if (this.toIgnoreAlert != null) {
                boolean ignored = false;

                for (Class<?> ignoredClass : this.toIgnoreAlert) {
                    if (other.getClass() == ignoredClass) {
                        ignored = true;
                        break;
                    }
                }

                if (ignored) {
                    continue;
                }
            }

            this.alertOther(other, attacker);
        }
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        mob.setTarget(target);
    }
}