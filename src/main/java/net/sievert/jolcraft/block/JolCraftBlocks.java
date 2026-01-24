package net.sievert.jolcraft.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.custom.*;
import net.sievert.jolcraft.block.custom.crop.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.custom.tooltip.SimpleTooltipBlockItem;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class JolCraftBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(JolCraft.MOD_ID);

    public static final DeferredBlock<Block> DEEPSLATE_MORTAR = registerBlock("deepslate_mortar",
            (properties) -> new MortarBlock(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .sound(SoundType.POLISHED_DEEPSLATE)
                    .strength(2.5F, 6.0F)
                    .requiresCorrectToolForDrops()
            ),
            BlockBehaviour.Properties.of(), false
    );

    public static final DeferredBlock<Block> GEODE_BLOCK = registerBlock("geode_block",
            (properties) -> new Block(properties
                    .mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.BASALT)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(2F, 5.0F)
                    .requiresCorrectToolForDrops()
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> LAPIDARY_BENCH = registerBlock("lapidary_bench",
            (properties) -> new LapidaryBenchBlock(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .sound(SoundType.POLISHED_DEEPSLATE)
                    .strength(4.5F, 6.0F)
                    .requiresCorrectToolForDrops()
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> DEEPSLATE_MITHRIL_ORE = registerMithrilBlock("deepslate_mithril_ore",
            (properties) -> new RotatedPillarExperienceBlock(UniformInt.of(5, 10), properties
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(30.0F, 1200.0F)
                    .sound(SoundType.DEEPSLATE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .lightLevel((state) -> 4)
            ),
            BlockBehaviour.Properties.of()
    );

    public static final DeferredBlock<Block> PURE_MITHRIL_BLOCK = registerMithrilBlock("pure_mithril_block",
            (properties) -> new Block(properties
                    .mapColor(MapColor.DIAMOND)
                    .strength(40.0F, 1200.0F)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .lightLevel((state) -> 4)
            ),
            BlockBehaviour.Properties.of()
    );

    public static final DeferredBlock<Block> MITHRIL_BLOCK = registerMithrilBlock("mithril_block",
            (properties) -> new Block(properties
                    .mapColor(MapColor.DIAMOND)
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .lightLevel((state) -> 4)
            ),
            BlockBehaviour.Properties.of()
    );

    public static final DeferredBlock<Block> DEEPSLATE_PLATE_BLOCK = registerBlock("deepslate_plate_block",
            (properties) -> new Block(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6, 6)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> STRONGBOX = registerBlock(
            "strongbox",
            (properties) -> new StrongboxBlock(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(5.0F, 1200.0F)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE_TILES)
                    .noOcclusion()
                    .randomTicks()
            ),
            BlockBehaviour.Properties.of(), false
    );

    public static final DeferredBlock<Block> STRONGBOX_DUMMY = BLOCKS.registerBlock("strongbox_dummy",
            (properties) -> new StrongboxBlock(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(5.0F, 1200.0F)
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE_TILES)
                    .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> HEARTH = registerBlock(
            "hearth",
            (properties) -> new HearthBlock(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE_TILES)
                    .lightLevel(litBlockEmission(13))
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> VERDANT_SOIL = registerBlock("verdant_soil",
            (properties) -> new VerdantSoilBlock(properties
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.5F)
                    .sound(SoundType.MUD)
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> VERDANT_FARMLAND = registerBlock("verdant_farmland",
            (properties) -> new VerdantFarmBlock(properties
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .randomTicks()
                    .strength(0.6F)
                    .sound(SoundType.MUD)
                    .isViewBlocking(JolCraftBlocks::always)
                    .isSuffocating(JolCraftBlocks::always)
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<DuskcapBlock> DUSKCAP = registerBlock(
            "duskcap",
            (properties) -> new DuskcapBlock(
                    properties
                            .mapColor(MapColor.COLOR_MAGENTA)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .pushReaction(PushReaction.DESTROY)
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<FlowerPotBlock> POTTED_DUSKCAP = registerBlock(
            "potted_duskcap",
            (properties) -> new FlowerPotBlock(DUSKCAP.get(), properties),
            flowerPotProperties(), false
    );

    public static final DeferredBlock<Block> FESTERLING_CROP = BLOCKS.registerBlock("festerling_crop",
            (properties) -> new FesterlingCropBlock(properties
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<FesterlingBlock> FESTERLING = registerBlock(
            "festerling",
            (properties) -> new FesterlingBlock(
                    properties
                            .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .pushReaction(PushReaction.DESTROY)
            ),
            BlockBehaviour.Properties.of(), true
    );


    public static final DeferredBlock<FlowerPotBlock> POTTED_FESTERLING = registerBlock(
            "potted_festerling",
            (properties) -> new FlowerPotBlock(FESTERLING.get(), properties),
            flowerPotProperties(), false
    );

    public static final DeferredBlock<HayBlock> BARLEY_BLOCK = registerBlock("barley_block",
            (properties) -> new HayBlock(properties
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.5F)
                    .sound(SoundType.GRASS)
                    .instrument(NoteBlockInstrument.BANJO)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> MUFFHORN_FUR_BLOCK = registerBlock("muffhorn_fur_block",
            (properties) -> new Block(properties
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.8F)
                    .sound(SoundType.WOOL)
                    .instrument(NoteBlockInstrument.GUITAR)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(), true
    );

    public static final DeferredBlock<Block> BARLEY_CROP = BLOCKS.registerBlock("barley_crop",
            (properties) -> new BarleyCropBlock(properties
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<Block> DEEPSLATE_BULBS_CROP = BLOCKS.registerBlock("deepslate_bulbs_crop",
            (properties) -> new DeepslateBulbsCropBlock(properties
                    .mapColor(MapColor.DEEPSLATE)
                    .noCollission()
                    .randomTicks()
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.DEEPSLATE)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<Block> ASGARNIAN_CROP_TOP = BLOCKS.registerBlock("asgarnian_crop_top",
            (properties) -> new HopsCropTopBlock(
                    properties
                            .mapColor(MapColor.TERRACOTTA_MAGENTA)
                            .noCollission()
                            .instabreak()
                            .randomTicks()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.ASGARNIAN_SEEDS
            )
    );

    public static final DeferredBlock<Block> ASGARNIAN_CROP_BOTTOM = BLOCKS.registerBlock("asgarnian_crop_bottom",
            (properties) -> new HopsCropBottomBlock(
                    properties
                            .mapColor(MapColor.TERRACOTTA_MAGENTA)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.ASGARNIAN_SEEDS,
                    JolCraftBlocks.ASGARNIAN_CROP_TOP
            )
    );

    public static final DeferredBlock<Block> DUSKHOLD_CROP_TOP = BLOCKS.registerBlock("duskhold_crop_top",
            (properties) -> new HopsCropTopBlock(
                    properties
                            .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                            .noCollission()
                            .instabreak()
                            .randomTicks()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.DUSKHOLD_SEEDS
            )
    );

    public static final DeferredBlock<Block> DUSKHOLD_CROP_BOTTOM = BLOCKS.registerBlock("duskhold_crop_bottom",
            (properties) -> new HopsCropBottomBlock(
                    properties
                            .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.DUSKHOLD_SEEDS,
                    JolCraftBlocks.DUSKHOLD_CROP_TOP
            )
    );

    public static final DeferredBlock<Block> KRANDONIAN_CROP_TOP = BLOCKS.registerBlock("krandonian_crop_top",
            (properties) -> new HopsCropTopBlock(
                    properties
                            .mapColor(MapColor.WARPED_STEM)
                            .noCollission()
                            .instabreak()
                            .randomTicks()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.KRANDONIAN_SEEDS
            )
    );

    public static final DeferredBlock<Block> KRANDONIAN_CROP_BOTTOM = BLOCKS.registerBlock("krandonian_crop_bottom",
            (properties) -> new HopsCropBottomBlock(
                    properties
                            .mapColor(MapColor.WARPED_STEM)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.KRANDONIAN_SEEDS,
                    JolCraftBlocks.KRANDONIAN_CROP_TOP
            )
    );


    public static final DeferredBlock<Block> YANILLIAN_CROP_TOP = BLOCKS.registerBlock("yanillian_crop_top",
            (properties) -> new HopsCropTopBlock(
                    properties
                            .mapColor(MapColor.COLOR_GREEN)
                            .noCollission()
                            .instabreak()
                            .randomTicks()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.YANILLIAN_SEEDS
            )
    );

    public static final DeferredBlock<Block> YANILLIAN_CROP_BOTTOM = BLOCKS.registerBlock("yanillian_crop_bottom",
            (properties) -> new HopsCropBottomBlock(
                    properties
                            .mapColor(MapColor.COLOR_GREEN)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY),
                    JolCraftItems.YANILLIAN_SEEDS,
                    JolCraftBlocks.YANILLIAN_CROP_TOP
            )
    );

    public static final DeferredBlock<FermentingCauldronBlock> FERMENTING_CAULDRON = BLOCKS.registerBlock(
            "fermenting_cauldron",
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

    private static <B extends Block> DeferredBlock<B> registerBlock(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> builder,
            BlockBehaviour.Properties properties,
            boolean registerItem
    ) {
        DeferredBlock<B> block = BLOCKS.registerBlock(name, builder);
        if (registerItem) {
            registerBlockItem(name, block);
        }
        return block;
    }

    private static <B extends Block> void registerBlockItem(String name, DeferredBlock<B> block) {
        JolCraftItems.ITEMS.registerItem(name, props -> new BlockItem(block.get(), props.useBlockDescriptionPrefix()));
    }

    private static <B extends Block> DeferredBlock<B> registerBlockWithTooltip(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> builder,
            BlockBehaviour.Properties properties,
            String tooltipKey
    ) {
        DeferredBlock<B> block = BLOCKS.registerBlock(name, builder);
        JolCraftItems.ITEMS.registerItem(name, props ->
                new SimpleTooltipBlockItem(block.get(), props.useBlockDescriptionPrefix(), tooltipKey));
        return block;
    }

    private static <B extends Block> DeferredBlock<B> registerMithrilBlock(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> builder,
            BlockBehaviour.Properties properties
    ) {
        DeferredBlock<B> block = BLOCKS.registerBlock(name, builder);
        registerMithrilBlockItem(name, block);
        return block;
    }

    private static <B extends Block> void registerMithrilBlockItem(String name, DeferredBlock<B> block) {
        JolCraftItems.ITEMS.registerItem(name, props ->
                new BlockItem(block.get(), props.fireResistant().rarity(Rarity.RARE).useBlockDescriptionPrefix())
        );
    }

    private static BlockBehaviour.Properties flowerPotProperties() {
        return BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return p_50763_ -> p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
