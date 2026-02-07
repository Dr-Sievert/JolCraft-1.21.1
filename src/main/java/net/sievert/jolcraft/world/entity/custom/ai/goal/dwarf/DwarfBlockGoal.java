package net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf;

import net.minecraft.world.entity.ai.goal.Goal;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;

public class DwarfBlockGoal extends Goal {

    private final AbstractDwarfEntity dwarf;

    public DwarfBlockGoal(AbstractDwarfEntity dwarf) {
        this.dwarf = dwarf;
    }

    @Override
    public boolean canUse() {
        return dwarf.shouldBlock;
    }

    @Override
    public void start() {
        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.BLOCK);
        dwarf.shouldBlock = false;
    }
}