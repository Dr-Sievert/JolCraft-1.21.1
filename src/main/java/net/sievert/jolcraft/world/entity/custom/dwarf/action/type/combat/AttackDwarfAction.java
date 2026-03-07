package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.combat;

import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;

public class AttackDwarfAction implements DwarfAction {

    protected final AbstractDwarfEntity dwarf;
    private int ticksRemaining = 0;

    public AttackDwarfAction (AbstractDwarfEntity dwarf){
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

