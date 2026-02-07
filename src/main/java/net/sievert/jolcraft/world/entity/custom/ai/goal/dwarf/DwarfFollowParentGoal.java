package net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf;

import net.minecraft.world.entity.ai.goal.Goal;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

import javax.annotation.Nullable;
import java.util.List;

public class DwarfFollowParentGoal extends Goal {

    private final AbstractDwarfEntity dwarf;
    @Nullable
    private AbstractDwarfEntity parent;
    private final double speedModifier;
    private int timeToRecalcPath;

    public DwarfFollowParentGoal(AbstractDwarfEntity dwarf, double speedModifier) {
        this.dwarf = dwarf;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        if (this.dwarf.getAge() >= 0) {
            return false;
        }

        List<? extends AbstractDwarfEntity> list = this.dwarf
                .level()
                .getEntitiesOfClass(
                        this.dwarf.getClass(),
                        this.dwarf.getBoundingBox().inflate(8.0, 4.0, 8.0)
                );

        AbstractDwarfEntity nearest = null;
        double bestDist = Double.MAX_VALUE;

        for (AbstractDwarfEntity other : list) {
            if (other.getAge() >= 0) {
                double d = this.dwarf.distanceToSqr(other);
                if (d < bestDist) {
                    bestDist = d;
                    nearest = other;
                }
            }
        }

        if (nearest == null) return false;
        if (bestDist < 9.0) return false;

        this.parent = nearest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.dwarf.getAge() >= 0) return false;
        if (this.parent == null || !this.parent.isAlive()) return false;

        double d = this.dwarf.distanceToSqr(this.parent);
        return d >= 9.0 && d <= 256.0;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.parent != null) {
                this.dwarf.getNavigation().moveTo(this.parent, this.speedModifier);
            }
        }
    }
}