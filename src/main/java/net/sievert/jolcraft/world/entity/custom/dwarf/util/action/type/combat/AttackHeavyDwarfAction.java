package net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.combat;

import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;

public class AttackHeavyDwarfAction extends AttackDwarfAction {

    public AttackHeavyDwarfAction(AbstractDwarfEntity dwarf) {
        super(dwarf);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.ATTACK_HEAVY;
    }
}
