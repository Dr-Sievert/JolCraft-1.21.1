package net.sievert.jolcraft.world.entity.custom.util.dwarf.action.type.combat;

import net.sievert.jolcraft.world.entity.custom.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.action.DwarfActionType;

public class DrinkDwarfAction implements DwarfAction {

    @Override public DwarfActionType getType() { return DwarfActionType.DRINK; }
}
