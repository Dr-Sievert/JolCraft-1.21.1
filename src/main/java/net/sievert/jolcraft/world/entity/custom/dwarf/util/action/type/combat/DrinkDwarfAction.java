package net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.combat;

import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;

public class DrinkDwarfAction implements DwarfAction {

    @Override public DwarfActionType getType() { return DwarfActionType.DRINK; }
}
