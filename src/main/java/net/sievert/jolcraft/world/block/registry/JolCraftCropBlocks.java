package net.sievert.jolcraft.world.block.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.crop.*;
import net.sievert.jolcraft.world.item.JolCraftItems;

public final class JolCraftCropBlocks {

    private JolCraftCropBlocks() {}

    public static DeferredBlock<Block> registerFesterlingCrop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.FESTERLING_CROP,
                props -> new FesterlingCropBlock(props
                        .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .pushReaction(PushReaction.DESTROY)
                )
        );
    }

    public static DeferredBlock<Block> registerBarleyCrop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.BARLEY_CROP,
                props -> new BarleyCropBlock(props
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.CROP)
                        .pushReaction(PushReaction.DESTROY)
                )
        );
    }

    public static DeferredBlock<Block> registerDeepslateBulbsCrop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.DEEPSLATE_BULBS_CROP,
                props -> new DeepslateBulbsCropBlock(props
                        .mapColor(MapColor.DEEPSLATE)
                        .noCollission()
                        .randomTicks()
                        .strength(3.5F, 6.0F)
                        .sound(SoundType.DEEPSLATE)
                        .pushReaction(PushReaction.DESTROY)
                )
        );
    }

    public static DeferredBlock<Block> registerAsgarnianCropTop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.ASGARNIAN_CROP_TOP,
                props -> new HopsCropTopBlock(
                        props.mapColor(MapColor.TERRACOTTA_MAGENTA)
                                .noCollission()
                                .instabreak()
                                .randomTicks()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.ASGARNIAN_SEEDS
                )
        );
    }

    public static DeferredBlock<Block> registerAsgarnianCropBottom(DeferredBlock<Block> top) {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.ASGARNIAN_CROP_BOTTOM,
                props -> new HopsCropBottomBlock(
                        props.mapColor(MapColor.TERRACOTTA_MAGENTA)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.ASGARNIAN_SEEDS,
                        top
                )
        );
    }

    public static DeferredBlock<Block> registerDuskholdCropTop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.DUSKHOLD_CROP_TOP,
                props -> new HopsCropTopBlock(
                        props.mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                                .noCollission()
                                .instabreak()
                                .randomTicks()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.DUSKHOLD_SEEDS
                )
        );
    }

    public static DeferredBlock<Block> registerDuskholdCropBottom(DeferredBlock<Block> top) {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.DUSKHOLD_CROP_BOTTOM,
                props -> new HopsCropBottomBlock(
                        props.mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.DUSKHOLD_SEEDS,
                        top
                )
        );
    }

    public static DeferredBlock<Block> registerKrandonianCropTop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.KRANDONIAN_CROP_TOP,
                props -> new HopsCropTopBlock(
                        props.mapColor(MapColor.WARPED_STEM)
                                .noCollission()
                                .instabreak()
                                .randomTicks()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.KRANDONIAN_SEEDS
                )
        );
    }

    public static DeferredBlock<Block> registerKrandonianCropBottom(DeferredBlock<Block> top) {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.KRANDONIAN_CROP_BOTTOM,
                props -> new HopsCropBottomBlock(
                        props.mapColor(MapColor.WARPED_STEM)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.KRANDONIAN_SEEDS,
                        top
                )
        );
    }

    public static DeferredBlock<Block> registerYanillianCropTop() {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.YANILLIAN_CROP_TOP,
                props -> new HopsCropTopBlock(
                        props.mapColor(MapColor.COLOR_GREEN)
                                .noCollission()
                                .instabreak()
                                .randomTicks()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.YANILLIAN_SEEDS
                )
        );
    }

    public static DeferredBlock<Block> registerYanillianCropBottom(DeferredBlock<Block> top) {
        return JolCraftBlocks.BLOCKS.registerBlock(
                JolCraftBlockIds.YANILLIAN_CROP_BOTTOM,
                props -> new HopsCropBottomBlock(
                        props.mapColor(MapColor.COLOR_GREEN)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY),
                        JolCraftItems.YANILLIAN_SEEDS,
                        top
                )
        );
    }
}