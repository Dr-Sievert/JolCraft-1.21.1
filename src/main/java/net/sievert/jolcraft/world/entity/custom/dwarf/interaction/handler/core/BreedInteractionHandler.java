package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.AgeableMob;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BreedInteractionHandler
        implements DwarfInteractions.CoreInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var stack = ctx.stack();

        if (!dwarf.isFood(stack)) {
            return DwarfInteractionOutcome.pass();
        }

        int age = dwarf.getAge();

        if (age == 0 && dwarf.canFallInLove()) {
            dwarf.setInLove(player);
            dwarf.playEatingSound();

            return DwarfInteractionOutcome.consumeOne();
        }

        if (dwarf.isBaby()) {
            dwarf.ageUp(
                    AgeableMob.getSpeedUpSecondsWhenFeeding(-age),
                    true
            );

            dwarf.playEatingSound();

            return DwarfInteractionOutcome.consumeOne();
        }

        PlaySound.dwarfNo(dwarf);

        return DwarfInteractionOutcome.failed();
    }
}