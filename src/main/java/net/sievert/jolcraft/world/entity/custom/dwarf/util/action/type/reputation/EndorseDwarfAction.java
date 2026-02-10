package net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.reputation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputation;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

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
            JolCraftSoundHelper.entity(dwarf, SoundEvents.VILLAGER_WORK_CARTOGRAPHER, 1.2F, 0.6F);
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        if (dwarf.level().isClientSide) return;

        DwarfProfession profession = dwarf.getProfession();

        DwarvenReputationHelper.addEndorsement(player, profession);

        int total = player.getData(JolCraftAttachments.DWARVEN_REP.get()).getEndorsementCount();

        JolCraftLogs.info(
                JolCraftLogTags.PLAYER,
                "{} at {} in {} endorsed {}, they now have {} {}",
                DwarfProfession.getDisplayName(dwarf).getString(),
                JolCraftLogs.roundedPos(dwarf),
                dwarf.level().dimension().location(),
                player.getDisplayName().getString(),
                total,
                total <= 1 ? "endorsement" : "endorsements"
        );

        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftCriteriaTriggers.ENDORSEMENT_GAIN.trigger(serverPlayer, profession);
        }

        ItemStack updatedTablet = tablet;
        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        updatedTablet.set(JolCraftDataComponents.REP_ENDORSEMENTS.get(), rep.getEndorsementCount());
        updatedTablet.set(JolCraftDataComponents.REP_TIER.get(), rep.getTierId());
        updatedTablet.set(JolCraftDataComponents.REP_OWNER.get(), player.getName().getString());

        throwItem(dwarf, player, updatedTablet);
        tablet = ItemStack.EMPTY;
    }

}
