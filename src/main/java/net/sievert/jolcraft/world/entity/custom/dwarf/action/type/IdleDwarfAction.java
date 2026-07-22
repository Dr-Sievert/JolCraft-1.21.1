package net.sievert.jolcraft.world.entity.custom.dwarf.action.type;

import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;

/**
 * A singleton idle action that represents the default, do-nothing state.
 * Always reports not stopped, so remains active until replaced.
 */
public class IdleDwarfAction implements DwarfAction {

    public static final IdleDwarfAction INSTANCE = new IdleDwarfAction();

    @Override
    public DwarfActionType getType() {
        return DwarfActionType.IDLE;
    }
}
