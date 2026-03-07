package net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

public class DwarfBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0F).ignoreLineOfSight();
    protected final AbstractDwarfEntity dwarf;
    private final Class<? extends AbstractDwarfEntity> partnerClass;
    protected final ServerLevel level;
    @Nullable
    protected AbstractDwarfEntity partner;
    private int loveTime;
    private final double speedModifier;

    public DwarfBreedGoal(AbstractDwarfEntity dwarf, double speedModifier, Class<? extends AbstractDwarfEntity> partnerClass) {
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

    @Override
    public boolean canContinueToUse() {
        if (this.partner == null) return false;

        return this.partner.isAlive()
                && this.partner.isInLove()
                && this.loveTime < 60
                && !this.partner.isPanicking();
    }

    @Override
    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void tick() {
        if (this.partner == null) return;

        this.dwarf.getLookControl().setLookAt(this.partner, 10.0F, (float) this.dwarf.getMaxHeadXRot());
        this.dwarf.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;

        if (this.loveTime >= this.adjustedTickDelay(60)
                && this.dwarf.distanceToSqr(this.partner) < 9.0D) {
            this.breed();
        }
    }

    @Nullable
    private AbstractDwarfEntity getFreePartner() {
        List<? extends AbstractDwarfEntity> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.dwarf, this.dwarf.getBoundingBox().inflate(8.0F));
        double d0 = Double.MAX_VALUE;
        AbstractDwarfEntity dwarf = null;

        for(AbstractDwarfEntity dwarf1 : list) {
            if (this.dwarf.canMate(dwarf1) && !dwarf1.isPanicking() && this.dwarf.distanceToSqr(dwarf1) < d0) {
                dwarf = dwarf1;
                d0 = this.dwarf.distanceToSqr(dwarf1);
            }
        }

        return dwarf;
    }

    protected void breed() {
        if (this.level == null || this.partner == null) return;
        this.dwarf.spawnChildFromBreeding(this.level, this.partner);
    }
}
