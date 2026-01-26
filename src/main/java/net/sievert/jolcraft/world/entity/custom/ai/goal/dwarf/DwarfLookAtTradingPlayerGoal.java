package net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf;

import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

public class DwarfLookAtTradingPlayerGoal extends LookAtPlayerGoal {
    private final AbstractDwarfEntity dwarf;

    public DwarfLookAtTradingPlayerGoal(AbstractDwarfEntity dwarf) {
        super(dwarf, Player.class, 8.0F);
        this.dwarf = dwarf;
    }

    @Override
    public boolean canUse() {
        if (this.dwarf.isTrading()) {
            this.lookAt = this.dwarf.getTradingPlayer();
            return true;
        } else {
            return false;
        }
    }
}
