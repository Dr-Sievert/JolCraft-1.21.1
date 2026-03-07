package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class GuardInteractionHandler implements DwarfInteractions.ProfessionInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        EquipmentSlot slot = JolCraftEquipmentHelper.slotIfMatches(stack, JolCraftItems.DEEPSLATE_ARMOR_SET);

        if (slot == null) {
            return InteractionResult.PASS;
        }

        if (!dwarf.getItemBySlot(slot).isEmpty()) {
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.FAIL;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.GUARD_EQUIP, player, hand, stack);
        return InteractionResult.SUCCESS;
    }
}