package net.sievert.jolcraft.entity.util.dwarf.interaction.type.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.type.InspectInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

import java.util.Set;

public class PromoteInteraction extends InspectInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {

        if (itemstack == null || !itemstack.is(JolCraftTags.Items.PROFESSION_CONTRACTS)) {
            return InteractionResult.FAIL;
        }

        if (!canPromoteToProfession(dwarf)) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.dwarf.cannot_promote").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (dwarf.needsPay()) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.PROMOTE, player, hand, itemstack);
        return InteractionResult.SUCCESS;
    }

    public static final Set<EntityType<?>> PROMOTABLE_DWARF_TYPES = Set.of(JolCraftEntities.DWARF.get());

    public boolean canPromoteToProfession(AbstractDwarfEntity dwarf) {
        return PROMOTABLE_DWARF_TYPES.contains(dwarf.getType()) && dwarf.isAlive() && !dwarf.isBaby();
    }
}
