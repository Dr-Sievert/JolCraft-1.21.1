package net.sievert.jolcraft.entity.ai.goal.dwarf;

import net.minecraft.world.entity.ai.goal.Goal;
import net.sievert.jolcraft.entity.custom.dwarf.profession.EntityGuardEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

public class DwarfBlockGoal extends Goal {

    private final EntityGuardEntity dwarf;

    public DwarfBlockGoal(EntityGuardEntity dwarf) {
        this.dwarf = dwarf;
    }

    @Override
    public boolean canUse() {
        return dwarf instanceof EntityGuardEntity && dwarf.shouldBlock;
    }

    @Override
    public void start() {
        this.dwarf.getActionHelper().setAction(dwarf, DwarfActionType.BLOCK);
        dwarf.shouldBlock = false;
    }
}
