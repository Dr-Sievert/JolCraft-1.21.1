package net.sievert.jolcraft.world.block.registry;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.biome.Biome;
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
import net.sievert.jolcraft.world.block.registry.util.JolCraftBlockRegistryHelper;

@SuppressWarnings("deprecation")
public final class JolCraftStationBlocks {

    private JolCraftStationBlocks() {}

    public static DeferredBlock<Block> registerDeepslateMortar() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.DEEPSLATE_MORTAR,
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

    public static DeferredBlock<Block> registerStrongboxDummy() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.STRONGBOX_DUMMY,
                props -> new StrongboxBlock(props
                        .mapColor(MapColor.DEEPSLATE)
                        .strength(5.0F, 1200.0F)
                        .instrument(NoteBlockInstrument.BASS)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.DEEPSLATE_TILES)
                        .noOcclusion()
                )
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
                props -> new FermentingCauldronBlock(
                        Biome.Precipitation.NONE,
                        CauldronInteraction.EMPTY,
                        props
                ),
                BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(2.0F)
                        .noOcclusion()
        );
    }
}