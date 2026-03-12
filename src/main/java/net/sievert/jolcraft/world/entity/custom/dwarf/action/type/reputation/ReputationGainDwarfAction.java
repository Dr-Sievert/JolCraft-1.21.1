package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.reputation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

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
            JolCraftSoundHelper.entity(dwarf, SoundEvents.VILLAGER_WORK_CARTOGRAPHER, 1.2F, 0.6F);
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

        if (!dwarf.level().isClientSide) {
            JolCraftLogs.info(
                    JolCraftLogTags.PLAYER,
                    "{} at {} in {} increased dwarven reputation for {} to {}",
                    DwarfProfession.getDisplayName(dwarf).getString(),
                    JolCraftLogs.roundedPos(dwarf),
                    dwarf.level().dimension().location(),
                    player.getDisplayName().getString(),
                    Component.translatable(DwarvenReputationHelper.getTierLangKey(newRep)).getString()
            );
        }

        player.displayClientMessage(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_REPUTATION_LEVEL_UP).withStyle(ChatFormatting.DARK_PURPLE), true);

        ItemStack nextTablet = switch (newRep) {
            case 1 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_1.get());
            case 2 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_2.get());
            case 3 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_3.get());
            case 4 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_4.get());
            default -> ItemStack.EMPTY;
        };

        nextTablet.set(JolCraftDataComponents.REPUTATION_ENDORSEMENTS.get(), DwarvenReputationHelper.getEndorsementCount(player));
        nextTablet.set(JolCraftDataComponents.REPUTATION_TIER.get(), newRep);
        nextTablet.set(JolCraftDataComponents.REPUTATION_OWNER.get(), player.getName().getString());
        throwItem(dwarf, player, nextTablet);
        PlaySound.levelUp(player);
        dwarf.spawnColoredParticles(0.4F, 0.0F, 0.5F, 1.25F, 64, 2.5D);
    }
}
