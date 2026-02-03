package net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.datagen.language.subprovider.BountyLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.PlaySound;

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
            PlaySound.dwarfNo(dwarf);
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_BOUNTY_CRATE_WRONG_TYPE).withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (complete == null || !complete) {
            PlaySound.dwarfNo(dwarf);
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE).withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.BOUNTY_CRATE, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }
}
