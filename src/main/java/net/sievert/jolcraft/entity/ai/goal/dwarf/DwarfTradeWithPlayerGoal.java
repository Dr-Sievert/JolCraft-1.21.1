package net.sievert.jolcraft.entity.ai.goal.dwarf;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;

public class DwarfTradeWithPlayerGoal extends Goal {
    private final AbstractDwarfEntity mob;

    public DwarfTradeWithPlayerGoal(AbstractDwarfEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        } else if (this.mob.isInWater()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else if (this.mob.hurtMarked) {
            return false;
        } else {
            Player player = this.mob.getTradingPlayer();
            if (player == null) {
                return false;
            } else {
                return !(this.mob.distanceToSqr(player) > 16.0);
            }
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setTradingPlayer(null);
    }
}
