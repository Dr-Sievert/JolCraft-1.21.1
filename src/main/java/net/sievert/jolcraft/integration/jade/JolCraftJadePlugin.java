package net.sievert.jolcraft.integration.jade;

import net.sievert.jolcraft.integration.jade.provider.block.FermentingBarrelComponentProvider;
import net.sievert.jolcraft.integration.jade.provider.block.FermentingCauldronComponentProvider;
import net.sievert.jolcraft.integration.jade.provider.block.StrongboxComponentProvider;
import net.sievert.jolcraft.integration.jade.provider.entity.DwarfComponentProvider;
import net.sievert.jolcraft.world.block.custom.StrongboxBlock;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingCauldronBlock;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class JolCraftJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {

        // Blocks

        registration.registerBlockComponent(
                FermentingBarrelComponentProvider.INSTANCE,
                FermentingBarrelBlock.class
        );

        registration.registerBlockComponent(
                FermentingCauldronComponentProvider.INSTANCE,
                FermentingCauldronBlock.class
        );

        registration.registerBlockComponent(
                StrongboxComponentProvider.INSTANCE,
                StrongboxBlock.class
        );

        // Entities

        registration.registerEntityComponent(
                DwarfComponentProvider.INSTANCE,
                AbstractDwarfEntity.class
        );
    }
}