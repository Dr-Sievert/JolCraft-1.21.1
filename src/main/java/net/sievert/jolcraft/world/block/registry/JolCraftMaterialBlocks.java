package net.sievert.jolcraft.world.block.registry;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.world.block.custom.RotatedPillarExperienceBlock;
import net.sievert.jolcraft.world.block.registry.util.JolCraftBlockRegistryHelper;

public final class JolCraftMaterialBlocks {

    private JolCraftMaterialBlocks() {}

    public static DeferredBlock<Block> registerGeodeBlock() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.GEODE_BLOCK,
                props -> new Block(props
                        .mapColor(MapColor.COLOR_BLACK)
                        .sound(SoundType.BASALT)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2F, 5.0F)
                        .requiresCorrectToolForDrops()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<Block> registerDeepslateMithrilOre() {
        return JolCraftBlockRegistryHelper.registerMithrilBlock(
                JolCraftBlockIds.DEEPSLATE_MITHRIL_ORE,
                props -> new RotatedPillarExperienceBlock(
                        UniformInt.of(5, 10),
                        props.mapColor(MapColor.DEEPSLATE)
                                .strength(30.0F, 1200.0F)
                                .sound(SoundType.DEEPSLATE)
                                .instrument(NoteBlockInstrument.BASEDRUM)
                                .requiresCorrectToolForDrops()
                                .lightLevel(state -> 4)
                ),
                BlockBehaviour.Properties.of()
        );
    }

    public static DeferredBlock<Block> registerPureMithrilBlock() {
        return JolCraftBlockRegistryHelper.registerMithrilBlock(
                JolCraftBlockIds.PURE_MITHRIL_BLOCK,
                props -> new Block(props
                        .mapColor(MapColor.DIAMOND)
                        .strength(40.0F, 1200.0F)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .lightLevel(state -> 4)
                ),
                BlockBehaviour.Properties.of()
        );
    }

    public static DeferredBlock<Block> registerMithrilBlock() {
        return JolCraftBlockRegistryHelper.registerMithrilBlock(
                JolCraftBlockIds.MITHRIL_BLOCK,
                props -> new Block(props
                        .mapColor(MapColor.DIAMOND)
                        .strength(50.0F, 1200.0F)
                        .sound(SoundType.NETHERITE_BLOCK)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .lightLevel(state -> 4)
                ),
                BlockBehaviour.Properties.of()
        );
    }

    public static DeferredBlock<Block> registerDeepslatePlateBlock() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.DEEPSLATE_PLATE_BLOCK,
                props -> new Block(props
                        .mapColor(MapColor.DEEPSLATE)
                        .sound(SoundType.DEEPSLATE)
                        .strength(6, 6)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<HayBlock> registerBarleyBlock() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.BARLEY_BLOCK,
                props -> new HayBlock(props
                        .mapColor(MapColor.COLOR_YELLOW)
                        .strength(0.5F)
                        .sound(SoundType.GRASS)
                        .instrument(NoteBlockInstrument.BANJO)
                        .ignitedByLava()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<Block> registerMuffhornFurBlock() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.MUFFHORN_FUR_BLOCK,
                props -> new Block(props
                        .mapColor(MapColor.COLOR_BROWN)
                        .strength(0.8F)
                        .sound(SoundType.WOOL)
                        .instrument(NoteBlockInstrument.GUITAR)
                        .ignitedByLava()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }
}