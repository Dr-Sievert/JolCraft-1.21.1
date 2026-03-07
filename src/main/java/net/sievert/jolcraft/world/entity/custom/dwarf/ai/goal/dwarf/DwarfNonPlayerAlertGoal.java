package net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

public class DwarfNonPlayerAlertGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final int ALERT_RANGE_Y = 10;
    private boolean alertSameType;

    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    @Nullable
    private Class<?>[] toIgnoreAlert;

    public DwarfNonPlayerAlertGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, true);
        this.toIgnoreDamage = toIgnoreDamage;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {

            if (livingentity instanceof Player) {
                return false;
            }

            if (livingentity.getType() == EntityType.PLAYER && getServerLevel(this.mob).getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                for (Class<?> oclass : this.toIgnoreDamage) {
                    if (oclass.isAssignableFrom(livingentity.getClass())) {
                        return false;
                    }
                }

                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
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
        double d0 = this.getFollowDistance();
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0, d0);
        List<AbstractDwarfEntity> list = this.mob.level().getEntitiesOfClass(AbstractDwarfEntity.class, aabb, EntitySelector.NO_SPECTATORS);
        Iterator iterator = list.iterator();

        while (true) {
            Mob mob;
            while (true) {
                if (!iterator.hasNext()) {
                    return;
                }

                mob = (Mob)iterator.next();
                if (this.mob != mob
                        && mob.getTarget() == null
                        && (!(this.mob instanceof TamableAnimal) || ((TamableAnimal)this.mob).getOwner() == ((TamableAnimal)mob).getOwner())
                        && !mob.isAlliedTo(this.mob.getLastHurtByMob())) {
                    if (this.toIgnoreAlert == null) {
                        break;
                    }

                    boolean flag = false;

                    for (Class<?> oclass : this.toIgnoreAlert) {
                        if (mob.getClass() == oclass) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        break;
                    }
                }
            }

            this.alertOther(mob, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        mob.setTarget(target);
    }
}
