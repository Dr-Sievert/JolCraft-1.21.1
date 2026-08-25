package net.sievert.jolcraft.world.block.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.*;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingCauldronBlock;
import net.sievert.jolcraft.world.block.registry.util.JolCraftBlockRegistryHelper;

@SuppressWarnings("deprecation")
public final class JolCraftStationBlocks {

    private JolCraftStationBlocks() {}

    public static DeferredBlock<Block> registerMortar() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.MORTAR,
                props -> new MortarBlock(props
                        .mapColor(MapColor.DEEPSLATE)
                        .sound(SoundType.POLISHED_DEEPSLATE)
                        .strength(2.5F, 6.0F)
                        .requiresCorrectToolForDrops()
                ),
                BlockBehaviour.Properties.of(),
                false
        );
    }

    public static DeferredBlock<Block> registerLapidaryBench() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.LAPIDARY_BENCH,
                props -> new LapidaryBenchBlock(props
                        .mapColor(MapColor.DEEPSLATE)
                        .sound(SoundType.POLISHED_DEEPSLATE)
                        .strength(4.5F, 6.0F)
                        .requiresCorrectToolForDrops()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<Block> registerStrongbox() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.STRONGBOX,
                props -> new StrongboxBlock(props
                        .mapColor(MapColor.DEEPSLATE)
                        .strength(5.0F, 1200.0F)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.DEEPSLATE_TILES)
                        .noOcclusion()
                        .randomTicks()
                ),
                BlockBehaviour.Properties.of(),
                false
        );
    }

    public static DeferredBlock<Block> registerHearth() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.HEARTH,
                props -> new HearthBlock(props
                        .mapColor(MapColor.DEEPSLATE)
                        .strength(4.5F, 3.0F)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.DEEPSLATE_TILES)
                        .lightLevel(JolCraftBlockRegistryHelper.litBlockEmission(13))
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<FermentingCauldronBlock> registerFermentingCauldron() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.FERMENTING_CAULDRON,
                FermentingCauldronBlock::new,
                BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(2.0F)
                        .noOcclusion()
        );
    }

    public static DeferredBlock<FermentingBarrelBlock> registerFermentingBarrel() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.FERMENTING_BARREL,
                props -> new FermentingBarrelBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.WOOD)
                                .instrument(NoteBlockInstrument.BASS)
                                .strength(2.5F)
                                .sound(SoundType.WOOD)
                                .ignitedByLava()
                )
        );
    }
}