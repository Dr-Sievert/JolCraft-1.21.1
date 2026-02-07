package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PayCoinInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (stack.is(JolCraftItems.GOLD_COIN.get()) && dwarf.canBePaid()) {
            dwarf.setPaid(player);
            JolCraftSoundHelper.entity(dwarf, SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.4F);
            dwarf.usePlayerItem(player, hand, stack);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
