package net.sievert.jolcraft.world.block.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.world.block.custom.crop.DuskcapBlock;
import net.sievert.jolcraft.world.block.custom.crop.FesterlingBlock;
import net.sievert.jolcraft.world.block.custom.crop.VerdantFarmBlock;
import net.sievert.jolcraft.world.block.custom.crop.VerdantSoilBlock;
import net.sievert.jolcraft.world.block.registry.util.JolCraftBlockRegistryHelper;

@SuppressWarnings("deprecation")
public final class JolCraftPlantBlocks {

    private JolCraftPlantBlocks() {}

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
}