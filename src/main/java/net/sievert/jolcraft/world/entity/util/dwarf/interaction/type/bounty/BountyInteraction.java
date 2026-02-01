package net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.datagen.language.subprovider.BountyLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class BountyInteraction extends InspectInteraction {

    private final BountyType type;

    public BountyInteraction(BountyType type) {
        this.type = type;
    }

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        if (itemstack == null || !itemstack.is(JolCraftItems.BOUNTY.get())) {
            return InteractionResult.FAIL;
        }

        BountyType requiredType = BountyHelper.getBountyType(itemstack);

        if (requiredType == null || requiredType != type) {
            PlaySound.dwarfNo(dwarf);
            player.displayClientMessage(
                    Component.translatable(BountyLangSubProvider.TOOLTIP_BOUNTY_WRONG_TYPE).withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.BOUNTY, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }

}
