package net.sievert.jolcraft.entity.util.dwarf.action.type.reputation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.JolCraftSounds;

public class ReputationGainDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;

    public ReputationGainDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.REPUTATION_GAIN;}

    @Override
    public void start() {
        this.ticksRemaining = 40;
        dwarf.resetPaid();
        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
        if (ticksRemaining == 20) {
            dwarf.level().playSound(null, dwarf.blockPosition(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.NEUTRAL, 1.2F, 0.6F);
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {

        int rep = DwarvenReputationHelper.getTier(player);

        if (player instanceof ServerPlayer serverPlayer) {
            DwarvenReputationHelper.setReputationTier(serverPlayer, rep + 1);
            JolCraftCriteriaTriggers.REPUTATION_GAIN.trigger(serverPlayer);
        }

        int newRep = DwarvenReputationHelper.getTier(player);

        player.displayClientMessage(Component.translatable("tooltip.jolcraft.reputation.level_up").withStyle(ChatFormatting.DARK_PURPLE), true);

        ItemStack nextTablet = switch (newRep) {
            case 1 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_1.get());
            case 2 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_2.get());
            case 3 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_3.get());
            case 4 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_4.get());
            default -> ItemStack.EMPTY;
        };

        nextTablet.set(JolCraftDataComponents.REP_ENDORSEMENTS.get(), DwarvenReputationHelper.getEndorsementCount(player));
        nextTablet.set(JolCraftDataComponents.REP_TIER.get(), newRep);
        nextTablet.set(JolCraftDataComponents.REP_OWNER.get(), player.getName().getString());
        throwItem(dwarf, player, nextTablet);

        dwarf.level().playSound(null, dwarf.blockPosition(), JolCraftSounds.LEVEL_UP.get(), SoundSource.NEUTRAL, 1.2F, 1.0F);
        dwarf.spawnColoredParticles(0.4F, 0.0F, 0.5F, 1.25F, 64, 2.5D);
    }
}
