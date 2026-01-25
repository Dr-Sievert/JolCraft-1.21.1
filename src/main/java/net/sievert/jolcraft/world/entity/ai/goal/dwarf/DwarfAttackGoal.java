package net.sievert.jolcraft.world.entity.ai.goal.dwarf;

import java.util.EnumSet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import org.jetbrains.annotations.NotNull;

public class DwarfAttackGoal extends MeleeAttackGoal {

    protected final AbstractDwarfEntity dwarf;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;
    private final boolean canPenalize = false;

    public DwarfAttackGoal(AbstractDwarfEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.dwarf = mob;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long i = this.dwarf.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            LivingEntity livingentity = this.dwarf.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (canPenalize) {
                    if (--this.ticksUntilNextPathRecalculation <= 0) {
                        this.path = this.dwarf.getNavigation().createPath(livingentity, 0);
                        this.ticksUntilNextPathRecalculation = 4 + this.dwarf.getRandom().nextInt(7);
                        return this.path != null;
                    } else {
                        return true;
                    }
                }
                this.path = this.dwarf.getNavigation().createPath(livingentity, 0);
                return this.path != null || this.dwarf.isWithinMeleeAttackRange(livingentity);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingentity = this.dwarf.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.dwarf.getNavigation().isDone();
        } else {
            return this.dwarf.isWithinRestriction(livingentity.blockPosition()) && (!(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative());
        }
    }

    @Override
    public void start() {
        this.dwarf.getNavigation().moveTo(this.path, this.speedModifier);
        this.dwarf.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingentity = this.dwarf.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            this.dwarf.setTarget(null);
        }
        dwarf.level().playSound(null, dwarf.blockPosition(), JolCraftSounds.DWARF_YES.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        this.dwarf.setAggressive(false);
        this.dwarf.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.dwarf.getTarget();
        if (livingentity != null) {
            this.dwarf.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.dwarf.getSensing().hasLineOfSight(livingentity))
                    && this.ticksUntilNextPathRecalculation <= 0
                    && (
                    this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                            || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                            || this.dwarf.getRandom().nextFloat() < 0.05F
            )) {
                this.pathedTargetX = livingentity.getX();
                this.pathedTargetY = livingentity.getY();
                this.pathedTargetZ = livingentity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.dwarf.getRandom().nextInt(7);
                double d0 = this.dwarf.distanceToSqr(livingentity);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                    if (this.dwarf.getNavigation().getPath() != null) {
                        net.minecraft.world.level.pathfinder.Node finalPathPoint = this.dwarf.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else
                            failedPathFindingPenalty += 10;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }
                if (d0 > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.dwarf.getNavigation().moveTo(livingentity, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            if (canPerformAttack(livingentity)) {
                chooseAndSetAttackAction(dwarf, dwarf.getActionHelper());
                this.dwarf.doHurtTarget(getServerLevel(this.dwarf), livingentity);
                this.resetAttackCooldown();
            }
        }
    }

    public static void chooseAndSetAttackAction(AbstractDwarfEntity dwarf, DwarfActionHelper actionHelper) {
        if (dwarf.getMainHandItem().is(JolCraftItems.DEEPSLATE_WARHAMMER.get()) || dwarf.getMainHandItem().is(JolCraftItems.DEEPSLATE_AXE.get())) {
            actionHelper.setAction(dwarf, DwarfActionType.Subtype.ATTACK_HEAVY);
        } else {
            actionHelper.setAction(dwarf, DwarfActionType.ATTACK);
        }
    }

    @Override
    protected void resetAttackCooldown() {
        if (dwarf.getMainHandItem().is(JolCraftItems.DEEPSLATE_WARHAMMER.get())) {
            this.ticksUntilNextAttack = this.adjustedTickDelay(40);
        } else if (dwarf.getMainHandItem().is(JolCraftItems.DEEPSLATE_AXE.get())) {
            this.ticksUntilNextAttack = this.adjustedTickDelay(22);
        } else {
            this.ticksUntilNextAttack = this.adjustedTickDelay(10);
        }
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(@NotNull LivingEntity entity) {
        return this.isTimeToAttack() && this.dwarf.isWithinMeleeAttackRange(entity) && this.dwarf.getSensing().hasLineOfSight(entity);
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }
}
