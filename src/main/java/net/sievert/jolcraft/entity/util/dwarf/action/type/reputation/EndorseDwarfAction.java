package net.sievert.jolcraft.entity.util.dwarf.action.type.reputation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.custom.attachment.rep.DwarvenReputation;
import net.sievert.jolcraft.data.util.attachment.DwarvenReputationHelper;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.action.type.InspectDwarfAction;

public class EndorseDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;
    protected ItemStack tablet = ItemStack.EMPTY;

    public EndorseDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.ENDORSE;}

    @Override
    public void start() {
        this.ticksRemaining = 40;
        dwarf.resetPaid();
        tablet = itemstack.copy();
        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
        if (ticksRemaining == 25 || ticksRemaining == 15) {
            dwarf.level().playSound(null, dwarf.blockPosition(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.NEUTRAL, 1.2F, 0.6F);
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        ResourceLocation profId = dwarf.getProfessionId();
        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        DwarvenReputationHelper.addEndorsement(player, profId);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftCriteriaTriggers.ENDORSEMENT_GAIN.trigger(serverPlayer, profId);
        }
        ItemStack updatedTablet = tablet;
        updatedTablet.set(JolCraftDataComponents.REP_ENDORSEMENTS.get(), rep.getEndorsementCount());
        updatedTablet.set(JolCraftDataComponents.REP_TIER.get(), rep.getTier());
        updatedTablet.set(JolCraftDataComponents.REP_OWNER.get(), player.getName().getString());
        throwItem(dwarf, player, updatedTablet);
        tablet = ItemStack.EMPTY;
    }
}
