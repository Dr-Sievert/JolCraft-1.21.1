package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DevKeyInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        Player player = ctx.player();
        ItemStack heldStack = player.getItemInHand(ctx.hand());

        if (!heldStack.is(JolCraftItems.DEV_KEY.get())) {
            return DwarfInteractionOutcome.pass();
        }

        AbstractTradingEntity dwarf = ctx.dwarf();

        if (!dwarf.level().isClientSide) {
            int level = dwarf.getMerchantLevel();
            if (DwarfMerchantData.canLevelUp(level)) {
                dwarf.overrideXp(DwarfMerchantData.getMaxXpPerLevel(level));
                AbstractTradingEntity.triggerLevelUp(dwarf);
            }
        }

        return DwarfInteractionOutcome.handled();
    }
}