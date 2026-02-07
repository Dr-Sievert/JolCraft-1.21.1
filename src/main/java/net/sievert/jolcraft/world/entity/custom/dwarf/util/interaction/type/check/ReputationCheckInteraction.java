package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.type.check;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.datagen.language.subprovider.ReputationLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteraction;
import net.sievert.jolcraft.world.sound.util.PlaySound;

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
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_LOCKED, requiredTier)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
