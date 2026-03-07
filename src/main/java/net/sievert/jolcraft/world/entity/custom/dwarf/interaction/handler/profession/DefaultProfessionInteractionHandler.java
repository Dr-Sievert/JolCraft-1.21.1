package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DefaultProfessionInteractionHandler implements DwarfInteractions.ProfessionInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        PlaySound.dwarfNo(ctx.dwarf());
        return InteractionResult.FAIL;
    }
}