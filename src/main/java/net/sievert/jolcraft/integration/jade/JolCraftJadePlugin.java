package net.sievert.jolcraft.integration.jade;

import net.sievert.jolcraft.integration.jade.provider.FermentingBarrelComponentProvider;
import net.sievert.jolcraft.integration.jade.provider.FermentingCauldronComponentProvider;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingCauldronBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class JolCraftJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {

        registration.registerBlockComponent(
                FermentingBarrelComponentProvider.INSTANCE,
                FermentingBarrelBlock.class
        );

        registration.registerBlockComponent(
                FermentingCauldronComponentProvider.INSTANCE,
                FermentingCauldronBlock.class
        );
    }
}