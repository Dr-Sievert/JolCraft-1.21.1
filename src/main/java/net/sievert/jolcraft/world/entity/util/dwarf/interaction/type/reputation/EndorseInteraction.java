package net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.reputation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.datagen.language.subprovider.ReputationLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfGuildmasterEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class EndorseInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftTags.Items.REPUTATION_TABLETS) || dwarf instanceof DwarfGuildmasterEntity) {
            return InteractionResult.FAIL;
        }

        DwarfProfession profession = dwarf.getProfession();
        boolean hasEndorsement = DwarvenReputationHelper.hasEndorsementBypassCreative(player, profession);

        if (dwarf.neverEndorse()) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_NEVER_ENDORSE).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (hasEndorsement) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_ALREADY_ENDORSED).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (!dwarf.canEndorse()) {
            player.displayClientMessage(
                    Component.translatable(ReputationLangSubProvider.TOOLTIP_REPUTATION_CANNOT_ENDORSE).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable(DwarfLangSubProvider.TOOLTIP_DWARF_NOT_PAID).withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.ENDORSE, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
