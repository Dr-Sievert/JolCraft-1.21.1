package net.sievert.jolcraft.integration.jade.provider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.integration.jade.util.JolCraftJadeBrewingTooltipHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum FermentingBarrelComponentProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final ResourceLocation UID = JolCraft.location(JolCraftBlockIds.FERMENTING_BARREL);

    /**
     * Adds brewing information to the Jade tooltip for fermenting barrels.
     */
    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (!(accessor.getBlockEntity() instanceof FermentingBarrelBlockEntity barrel)) {
            return;
        }

        FluidStack brew = barrel.getCurrentBrew();

        if (brew.isEmpty()) {
            return;
        }

        JolCraftJadeBrewingTooltipHelper.addBrewInfo(
                tooltip,
                brew
        );
    }

    /**
     * Returns the unique Jade identifier for this component provider.
     */
    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}