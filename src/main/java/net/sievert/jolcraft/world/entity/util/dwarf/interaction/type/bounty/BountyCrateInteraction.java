package net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public class BountyCrateInteraction extends InspectInteraction {

    private final BountyType type;

    public BountyCrateInteraction(BountyType type) {
        this.type = type;
    }

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftItems.BOUNTY_CRATE.get())) {
            return InteractionResult.FAIL;
        }

        BountyType requiredType = BountyHelper.getBountyType(itemstack);
        Boolean complete = itemstack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get());

        if (requiredType == null || requiredType != type) {
            JolCraftSoundHelper.playDwarfNo(dwarf);
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.bounty_crate.wrong_type").withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (complete == null || !complete) {
            JolCraftSoundHelper.playDwarfNo(dwarf);
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.bounty_crate.not_complete").withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.BOUNTY_CRATE, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
