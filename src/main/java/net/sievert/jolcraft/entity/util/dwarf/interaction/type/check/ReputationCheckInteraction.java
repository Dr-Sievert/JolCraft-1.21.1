package net.sievert.jolcraft.entity.util.dwarf.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class ReputationCheckInteraction implements DwarfInteraction {

    private final int requiredTier;

    public ReputationCheckInteraction(int requiredTier) {
        this.requiredTier = requiredTier;
    }

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player) {
        boolean hasTier = DwarvenReputationHelper.hasTier(player, requiredTier);

        if (!hasTier) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.reputation.locked", requiredTier)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
