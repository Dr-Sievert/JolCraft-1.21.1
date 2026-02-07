package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BreedInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!dwarf.isFood(stack)) {
            return InteractionResult.PASS;
        }

        int age = dwarf.getAge();

        if (age == 0 && dwarf.canFallInLove()) {
            dwarf.usePlayerItem(player, hand, stack);
            dwarf.setInLove(player);
            dwarf.playEatingSound();
            return InteractionResult.SUCCESS_SERVER;
        }

        if (dwarf.isBaby()) {
            dwarf.usePlayerItem(player, hand, stack);
            dwarf.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-age), true);
            dwarf.playEatingSound();
            return InteractionResult.SUCCESS;
        }

        PlaySound.dwarfNo(dwarf);
        return InteractionResult.FAIL;
    }
}
