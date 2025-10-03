package net.sievert.jolcraft.entity.ai.goal.dwarf;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;

public class DwarfBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0F).ignoreLineOfSight();
    protected final AbstractEntityEntity dwarf;
    private final Class<? extends AbstractEntityEntity> partnerClass;
    protected final ServerLevel level;
    @Nullable
    protected AbstractEntityEntity partner;
    private int loveTime;
    private final double speedModifier;

    public DwarfBreedGoal(AbstractEntityEntity dwarf, double speedModifier) {
        this(dwarf, speedModifier, dwarf.getClass());
    }

    public DwarfBreedGoal(AbstractEntityEntity dwarf, double speedModifier, Class<? extends AbstractEntityEntity> partnerClass) {
        this.dwarf = dwarf;
        this.level = getServerLevel(dwarf);
        this.partnerClass = partnerClass;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!this.dwarf.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        assert this.partner != null;
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60 && !this.partner.isPanicking();
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        assert this.partner != null;
        this.dwarf.getLookControl().setLookAt(this.partner, 10.0F, (float)this.dwarf.getMaxHeadXRot());
        this.dwarf.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.dwarf.distanceToSqr(this.partner) < (double)9.0F) {
            this.breed();
        }

    }

    @Nullable
    private AbstractEntityEntity getFreePartner() {
        List<? extends AbstractEntityEntity> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.dwarf, this.dwarf.getBoundingBox().inflate((double)8.0F));
        double d0 = Double.MAX_VALUE;
        AbstractEntityEntity dwarf = null;

        for(AbstractEntityEntity dwarf1 : list) {
            if (this.dwarf.canMate(dwarf1) && !dwarf1.isPanicking() && this.dwarf.distanceToSqr(dwarf1) < d0) {
                dwarf = dwarf1;
                d0 = this.dwarf.distanceToSqr(dwarf1);
            }
        }

        return dwarf;
    }

    protected void breed() {
        this.dwarf.spawnChildFromBreeding(this.level, this.partner);
    }
}
