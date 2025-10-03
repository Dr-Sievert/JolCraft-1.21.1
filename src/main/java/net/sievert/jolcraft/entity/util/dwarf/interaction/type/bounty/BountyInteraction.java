package net.sievert.jolcraft.entity.util.dwarf.interaction.type.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class BountyInteraction extends InspectInteraction {

    private final BountyType type;

    public BountyInteraction(BountyType type) {
        this.type = type;
    }

    @Override
    public InteractionResult handle(AbstractEntityEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        assert itemstack != null;
        if (itemstack.is(JolCraftItems.BOUNTY.get())) {
            BountyType requiredType = BountyHelper.getBountyType(itemstack);
            if(requiredType != type){
                JolCraftSoundHelper.playDwarfNo(dwarf);
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty.wrong_type").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.SUCCESS;
            }
            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.BOUNTY, player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
