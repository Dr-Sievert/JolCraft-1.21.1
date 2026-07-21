package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome.HeldItemUse;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class GuardInteractionHandler
        implements DwarfInteractions.ProfessionInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        var dwarf = ctx.dwarf();
        var stack = ctx.stack();

        ArmorItem.Type armorType =
                JolCraftEquipmentHelper.armorType(stack);

        if (armorType == null
                || !stack.is(
                JolCraftItems.DEEPSLATE_ARMOR_SET
                        .get(armorType)
                        .get()
        )) {

            return DwarfInteractionOutcome.pass();
        }

        EquipmentSlot equipmentSlot =
                armorType.getSlot();

        if (!dwarf.getItemBySlot(equipmentSlot).isEmpty()) {
            PlaySound.dwarfNo(dwarf);

            return DwarfInteractionOutcome.failed();
        }

        return DwarfInteractionOutcome.startAction(
                DwarfActionType.Subtype.GUARD_EQUIP,
                HeldItemUse.CONSUME_ONE
        );
    }
}