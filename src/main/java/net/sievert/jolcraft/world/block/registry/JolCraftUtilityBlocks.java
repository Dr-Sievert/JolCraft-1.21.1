package net.sievert.jolcraft.world.block.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.world.block.custom.ManagedLightBlock;
import net.sievert.jolcraft.world.block.registry.util.JolCraftBlockRegistryHelper;

public final class JolCraftUtilityBlocks {

    private JolCraftUtilityBlocks() {}

    public static DeferredBlock<Block> registerManagedLight() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.MANAGED_LIGHT,
                props -> new ManagedLightBlock(props
                        .replaceable()
                        .strength(-1.0F, 3600000.8F)
                        .mapColor(MapColor.NONE)
                        .noLootTable()
                        .noOcclusion()
                        .lightLevel(ManagedLightBlock.LIGHT_EMISSION)
                ),
                BlockBehaviour.Properties.of(),
                false
        );
    }
}