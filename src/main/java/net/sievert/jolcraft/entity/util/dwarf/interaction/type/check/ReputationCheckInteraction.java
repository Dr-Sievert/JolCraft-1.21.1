package net.sievert.jolcraft.entity.util.dwarf.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.util.attachment.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class ReputationCheckInteraction implements DwarfInteraction {

    private final int requiredTier;

    public ReputationCheckInteraction(int requiredTier) {
        this.requiredTier = requiredTier;
    }

    @Override
    public InteractionResult handle(AbstractEntityEntity dwarf, Player player) {
        boolean client = dwarf.level().isClientSide;
        boolean hasTier = DwarvenReputationHelper.hasTier(player, requiredTier);
        if (!hasTier) {
            if(client){return InteractionResult.CONSUME;}
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.reputation.locked", requiredTier).withStyle(ChatFormatting.RED),
                    true);
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }
}
