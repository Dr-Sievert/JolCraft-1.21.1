package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MerchantInteractionHandler extends AbstractBountyProfessionInteractionHandler {

    public MerchantInteractionHandler() {
        super(BountyType.MERCHANT);
    }
}
