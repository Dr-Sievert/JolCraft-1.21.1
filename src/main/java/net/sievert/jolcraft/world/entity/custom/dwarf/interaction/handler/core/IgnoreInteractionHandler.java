package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class IgnoreInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var stack = ctx.stack();

        if (!dwarf.isAlive() || stack.is(JolCraftTags.Items.DWARF_SPAWN_EGGS)) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}