package net.sievert.jolcraft.entity.util.dwarf.action.type.combat;

import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

public class AttackHeavyDwarfAction extends AttackDwarfAction {

    public AttackHeavyDwarfAction(AbstractEntityEntity dwarf) {
        super(dwarf);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.ATTACK_HEAVY;
    }
}
