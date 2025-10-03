package net.sievert.jolcraft.entity.util.dwarf.action.type.combat;

import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

public class AttackDwarfAction implements DwarfAction {

    protected AbstractEntityEntity dwarf;
    private int ticksRemaining = 0;

    public AttackDwarfAction (AbstractEntityEntity dwarf){
        this.dwarf = dwarf;
    }

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

