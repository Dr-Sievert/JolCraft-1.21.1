package net.sievert.jolcraft.entity.util.dwarf.action.type;

import net.sievert.jolcraft.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

public class InspectDwarfAction implements DwarfAction {

    @Override public DwarfActionType getType() { return DwarfActionType.INSPECT; }

}
