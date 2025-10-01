package net.sievert.jolcraft.entity.util.dwarf.action.type.combat;

import net.sievert.jolcraft.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

public class AttackDwarfAction implements DwarfAction {
    private int ticksRemaining = 0;

    @Override
    public DwarfActionType getType() { return DwarfActionType.ATTACK; }

    @Override
    public void start() {
        this.ticksRemaining = 10;
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }
}

