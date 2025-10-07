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
        boolean client = dwarf.level().isClientSide;
        assert itemstack != null;
        if (itemstack.is(JolCraftTags.Items.PROFESSION_CONTRACTS)) {
            if (!canPromoteToProfession(dwarf)) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.dwarf.cannot_promote").withStyle(ChatFormatting.GRAY), true
                );
                JolCraftSoundHelper.playDwarfNo(dwarf);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            if (dwarf.needsPay()) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY), true
                );
                JolCraftSoundHelper.playDwarfNo(dwarf);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.PROMOTE, player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public static final Set<EntityType<?>> PROMOTABLE_DWARF_TYPES = Set.of(JolCraftEntities.DWARF.get());

    public boolean canPromoteToProfession(AbstractDwarfEntity dwarf) {
        return PROMOTABLE_DWARF_TYPES.contains(dwarf.getType()) && dwarf.isAlive() && !dwarf.isBaby();
    }
}
