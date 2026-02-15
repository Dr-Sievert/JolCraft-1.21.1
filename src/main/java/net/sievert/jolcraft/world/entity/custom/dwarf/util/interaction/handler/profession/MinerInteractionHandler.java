package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MinerInteractionHandler extends AbstractBountyProfessionInteractionHandler {

    public MinerInteractionHandler() {
        super(BountyType.MINER);
    }
}