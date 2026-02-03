package net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf;

import net.minecraft.world.entity.ai.goal.Goal;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfGuardEntity;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.action.DwarfActionType;

public class DwarfBlockGoal extends Goal {

    private final DwarfGuardEntity dwarf;

    public DwarfBlockGoal(DwarfGuardEntity dwarf) {
        this.dwarf = dwarf;
    }

    @Override
    public boolean canUse() {
        return dwarf instanceof DwarfGuardEntity && dwarf.shouldBlock;
    }

    @Override
    public void start() {
        this.dwarf.getActionHelper().setAction(dwarf, DwarfActionType.BLOCK);
        dwarf.shouldBlock = false;
    }
}
