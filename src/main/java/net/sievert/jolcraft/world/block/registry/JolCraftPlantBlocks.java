package net.sievert.jolcraft.world.block.registry;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.world.block.custom.plant.BloodrootBlock;
import net.sievert.jolcraft.world.block.custom.plant.CyanellaBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.DuskcapBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.FesterlingBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.VerdantFarmBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.VerdantSoilBlock;
import net.sievert.jolcraft.world.block.registry.util.JolCraftBlockRegistryHelper;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

@SuppressWarnings("deprecation")
public final class JolCraftPlantBlocks {

    private JolCraftPlantBlocks() {}

    public static DeferredBlock<BloodrootBlock> registerBloodroot() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.BLOODROOT,
                props -> new BloodrootBlock(props
                        .mapColor(MapColor.DIRT)
                        .replaceable()
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.HANGING_ROOTS)
                        .offsetType(BlockBehaviour.OffsetType.XZ)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
                ),
                BlockBehaviour.Properties.of(),
                false
        );
    }

    public static DeferredBlock<CyanellaBlock> registerCyanella() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.CYANELLA,
                props -> new CyanellaBlock(
                        JolCraftEffects.ALCHEMIST_FOCUS,
                        5.0F,
                        props
                                .mapColor(MapColor.PLANT)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)
                                .offsetType(BlockBehaviour.OffsetType.XZ)
                                .pushReaction(PushReaction.DESTROY)
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<FlowerPotBlock> registerPottedCyanella(DeferredBlock<CyanellaBlock> cyanella) {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.POTTED_CYANELLA,
                props -> new FlowerPotBlock(cyanella.get(), props),
                JolCraftBlockRegistryHelper.flowerPotProperties(),
                false
        );
    }

    public static DeferredBlock<FlowerBlock> registerSkybell() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.SKYBELL,
                props -> new FlowerBlock(
                        JolCraftEffects.MARKSMAN,
                        5.0F,
                        props
                                .mapColor(MapColor.PLANT)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)
                                .offsetType(BlockBehaviour.OffsetType.XZ)
                                .pushReaction(PushReaction.DESTROY)
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<FlowerPotBlock> registerPottedSkybell(DeferredBlock<FlowerBlock> skybell) {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.POTTED_SKYBELL,
                props -> new FlowerPotBlock(skybell.get(), props),
                JolCraftBlockRegistryHelper.flowerPotProperties(),
                false
        );
    }

    public static DeferredBlock<DuskcapBlock> registerDuskcap() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.DUSKCAP,
                props -> new DuskcapBlock(props
                        .mapColor(MapColor.COLOR_MAGENTA)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .pushReaction(PushReaction.DESTROY)
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<FlowerPotBlock> registerPottedDuskcap(DeferredBlock<DuskcapBlock> duskcap) {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.POTTED_DUSKCAP,
                props -> new FlowerPotBlock(duskcap.get(), props),
                JolCraftBlockRegistryHelper.flowerPotProperties(),
                false
        );
    }

    public static DeferredBlock<HugeMushroomBlock> registerDuskcapBlock() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.DUSKCAP_BLOCK,
                props -> new HugeMushroomBlock(props
                        .mapColor(MapColor.COLOR_MAGENTA)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.2F)
                        .sound(SoundType.WOOD)
                        .ignitedByLava()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<HugeMushroomBlock> registerDuskcapStem() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.DUSKCAP_STEM,
                props -> new HugeMushroomBlock(props
                        .mapColor(MapColor.COLOR_PURPLE)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.2F)
                        .sound(SoundType.WOOD)
                        .ignitedByLava()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<FesterlingBlock> registerFesterling() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.FESTERLING,
                props -> new FesterlingBlock(props
                        .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .pushReaction(PushReaction.DESTROY)
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<FlowerPotBlock> registerPottedFesterling(DeferredBlock<FesterlingBlock> festerling) {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.POTTED_FESTERLING,
                props -> new FlowerPotBlock(festerling.get(), props),
                JolCraftBlockRegistryHelper.flowerPotProperties(),
                false
        );
    }

    public static DeferredBlock<HugeMushroomBlock> registerFesterlingBlock() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.FESTERLING_BLOCK,
                props -> new HugeMushroomBlock(props
                        .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.2F)
                        .sound(SoundType.WOOD)
                        .ignitedByLava()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<HugeMushroomBlock> registerFesterlingStem() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.FESTERLING_STEM,
                props -> new HugeMushroomBlock(props
                        .mapColor(MapColor.GOLD)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.2F)
                        .sound(SoundType.WOOD)
                        .ignitedByLava()
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<Block> registerVerdantSoil() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.VERDANT_SOIL,
                props -> new VerdantSoilBlock(props
                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                        .strength(0.5F)
                        .sound(SoundType.MUD)
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }

    public static DeferredBlock<Block> registerVerdantFarmland() {
        return JolCraftBlockRegistryHelper.registerBlock(
                JolCraftBlockIds.VERDANT_FARMLAND,
                props -> new VerdantFarmBlock(props
                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                        .randomTicks()
                        .strength(0.6F)
                        .sound(SoundType.MUD)
                        .isViewBlocking(JolCraftBlockRegistryHelper::always)
                        .isSuffocating(JolCraftBlockRegistryHelper::always)
                ),
                BlockBehaviour.Properties.of(),
                true
        );
    }
}