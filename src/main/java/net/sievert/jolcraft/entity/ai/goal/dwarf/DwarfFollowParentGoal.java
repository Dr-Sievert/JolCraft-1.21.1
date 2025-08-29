package net.sievert.jolcraft.entity.ai.goal.dwarf;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.world.entity.ai.goal.Goal;
import net.sievert.jolcraft.entity.custom.dwarf.DwarfEntity;

public class DwarfFollowParentGoal extends Goal {
    private final DwarfEntity dwarf;
    @Nullable
    private DwarfEntity parent;
    private final double speedModifier;
    private int timeToRecalcPath;

    public DwarfFollowParentGoal(DwarfEntity dwarf, double speedModifier) {
        this.dwarf = dwarf;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        if (this.dwarf.getAge() >= 0) {
            return false;
        } else {
            List<? extends DwarfEntity> list = this.dwarf
                    .level()
                    .getEntitiesOfClass((Class<? extends DwarfEntity>)this.dwarf.getClass(), this.dwarf.getBoundingBox().inflate(8.0, 4.0, 8.0));
            DwarfEntity dwarf = null;
            double d0 = Double.MAX_VALUE;

            for (DwarfEntity dwarf1 : list) {
                if (dwarf1.getAge() >= 0) {
                    double d1 = this.dwarf.distanceToSqr(dwarf1);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        dwarf = dwarf1;
                    }
                }
            }

            if (dwarf == null) {
                return false;
            } else if (d0 < 9.0) {
                return false;
            } else {
                this.parent = dwarf;
                return true;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.dwarf.getAge() >= 0) {
            return false;
        } else {
            assert this.parent != null;
            if (!this.parent.isAlive()) {
                return false;
            } else {
                double d0 = this.dwarf.distanceToSqr(this.parent);
                return !(d0 < 9.0) && !(d0 > 256.0);
            }
        }
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
            assert this.parent != null;
            this.dwarf.getNavigation().moveTo(this.parent, this.speedModifier);
        }
    }
}
