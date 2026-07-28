package net.sievert.jolcraft.integration.jade.provider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.integration.jade.util.JolCraftJadeBrewingTooltipHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum FermentingCauldronComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(
                    JolCraft.MOD_ID,
                    JolCraftBlockIds.FERMENTING_CAULDRON
            );

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (!(accessor.getBlockEntity() instanceof FermentingCauldronBlockEntity cauldron)) {
            return;
        }

        FluidStack brew = cauldron.getJadeBrewFluid();

        if (brew.isEmpty()) {
            return;
        }

        JolCraftJadeBrewingTooltipHelper.addBrewInfo(
                tooltip,
                brew
        );
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}