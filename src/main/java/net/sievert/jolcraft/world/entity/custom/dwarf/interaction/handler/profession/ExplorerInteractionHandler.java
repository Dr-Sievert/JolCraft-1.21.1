package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachmentHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ExplorerInteractionHandler
        implements DwarfInteractions.ProfessionInteraction,
        DwarfInteractions.DwarfInteractionHooks {

    @Override
    public void preCore(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();

        int discoveryScore =
                DiscoveredStructuresAttachmentHelper
                        .getDiscoveryScore(
                                (ServerPlayer) player
                        );

        if (discoveryScore <= dwarf.getDwarfXp()) {
            return;
        }

        dwarf.overrideXp(discoveryScore);

        if (AbstractTradingEntity.triggerLevelUp(dwarf) > 0) {
            PlaySound.dwarfYes(dwarf);
        }
    }

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        PlaySound.dwarfNo(ctx.dwarf());

        return DwarfInteractionOutcome.failed();
    }
}