package net.sievert.jolcraft.world.entity.util.dwarf.action.type.profession;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public class SignDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;

    public SignDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.CONTRACT_SIGNING;}

    @Override
    public void start() {
        this.ticksRemaining = 40;
        dwarf.resetPaid();
        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
        if (ticksRemaining == 25 || ticksRemaining == 15) {
            JolCraftSoundHelper.entity(dwarf, SoundEvents.VILLAGER_WORK_CARTOGRAPHER, 1.0F, 1.2F);
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        throwItem(dwarf, player, dwarf.getSignedContractItem());
    }
}
