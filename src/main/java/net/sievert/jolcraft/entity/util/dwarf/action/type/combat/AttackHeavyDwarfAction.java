package net.sievert.jolcraft.entity.util.dwarf.action.type.combat;

import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

public class AttackHeavyDwarfAction extends AttackDwarfAction {

    public AttackHeavyDwarfAction(AbstractDwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.ATTACK_HEAVY;
    }
}
