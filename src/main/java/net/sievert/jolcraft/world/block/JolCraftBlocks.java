package net.sievert.jolcraft.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingCauldronBlock;
import net.sievert.jolcraft.world.block.custom.crop.DuskcapBlock;
import net.sievert.jolcraft.world.block.custom.crop.FesterlingBlock;
import net.sievert.jolcraft.world.block.registry.JolCraftCropBlocks;
import net.sievert.jolcraft.world.block.registry.JolCraftMaterialBlocks;
import net.sievert.jolcraft.world.block.registry.JolCraftPlantBlocks;
import net.sievert.jolcraft.world.block.registry.JolCraftStationBlocks;
import net.sievert.jolcraft.world.block.registry.JolCraftUtilityBlocks;

public final class JolCraftBlocks {

    private JolCraftBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JolCraft.MOD_ID);

    public static final DeferredBlock<Block> MANAGED_LIGHT = JolCraftUtilityBlocks.registerManagedLight();

    public static final DeferredBlock<Block> DEEPSLATE_MORTAR = JolCraftStationBlocks.registerDeepslateMortar();
    public static final DeferredBlock<Block> LAPIDARY_BENCH = JolCraftStationBlocks.registerLapidaryBench();
    public static final DeferredBlock<Block> STRONGBOX = JolCraftStationBlocks.registerStrongbox();
    public static final DeferredBlock<Block> HEARTH = JolCraftStationBlocks.registerHearth();
    public static final DeferredBlock<FermentingCauldronBlock> FERMENTING_CAULDRON = JolCraftStationBlocks.registerFermentingCauldron();
    public static final DeferredBlock<FermentingBarrelBlock> FERMENTING_BARREL = JolCraftStationBlocks.registerFermentingBarrel();

    public static final DeferredBlock<Block> GEODE_BLOCK = JolCraftMaterialBlocks.registerGeodeBlock();
    public static final DeferredBlock<Block> DEEPSLATE_MITHRIL_ORE = JolCraftMaterialBlocks.registerDeepslateMithrilOre();
    public static final DeferredBlock<Block> PURE_MITHRIL_BLOCK = JolCraftMaterialBlocks.registerPureMithrilBlock();
    public static final DeferredBlock<Block> MITHRIL_BLOCK = JolCraftMaterialBlocks.registerMithrilBlock();
    public static final DeferredBlock<Block> DEEPSLATE_PLATE_BLOCK = JolCraftMaterialBlocks.registerDeepslatePlateBlock();
    public static final DeferredBlock<HayBlock> BARLEY_BLOCK = JolCraftMaterialBlocks.registerBarleyBlock();
    public static final DeferredBlock<Block> MUFFHORN_FUR_BLOCK = JolCraftMaterialBlocks.registerMuffhornFurBlock();

    public static final DeferredBlock<Block> VERDANT_SOIL = JolCraftPlantBlocks.registerVerdantSoil();
    public static final DeferredBlock<Block> VERDANT_FARMLAND = JolCraftPlantBlocks.registerVerdantFarmland();
    public static final DeferredBlock<DuskcapBlock> DUSKCAP = JolCraftPlantBlocks.registerDuskcap();
    public static final DeferredBlock<FlowerPotBlock> POTTED_DUSKCAP = JolCraftPlantBlocks.registerPottedDuskcap(DUSKCAP);
    public static final DeferredBlock<HugeMushroomBlock> DUSKCAP_BLOCK = JolCraftPlantBlocks.registerDuskcapBlock();
    public static final DeferredBlock<HugeMushroomBlock> DUSKCAP_STEM = JolCraftPlantBlocks.registerDuskcapStem();
    public static final DeferredBlock<FesterlingBlock> FESTERLING = JolCraftPlantBlocks.registerFesterling();
    public static final DeferredBlock<FlowerPotBlock> POTTED_FESTERLING = JolCraftPlantBlocks.registerPottedFesterling(FESTERLING);
    public static final DeferredBlock<HugeMushroomBlock> FESTERLING_BLOCK = JolCraftPlantBlocks.registerFesterlingBlock();
    public static final DeferredBlock<HugeMushroomBlock> FESTERLING_STEM = JolCraftPlantBlocks.registerFesterlingStem();

    public static final DeferredBlock<Block> FESTERLING_CROP = JolCraftCropBlocks.registerFesterlingCrop();
    public static final DeferredBlock<Block> BARLEY_CROP = JolCraftCropBlocks.registerBarleyCrop();
    public static final DeferredBlock<Block> DEEPSLATE_BULBS_CROP = JolCraftCropBlocks.registerDeepslateBulbsCrop();

    public static final DeferredBlock<Block> ASGARNIAN_CROP_TOP = JolCraftCropBlocks.registerAsgarnianCropTop();
    public static final DeferredBlock<Block> ASGARNIAN_CROP_BOTTOM = JolCraftCropBlocks.registerAsgarnianCropBottom(ASGARNIAN_CROP_TOP);

    public static final DeferredBlock<Block> DUSKHOLD_CROP_TOP = JolCraftCropBlocks.registerDuskholdCropTop();
    public static final DeferredBlock<Block> DUSKHOLD_CROP_BOTTOM = JolCraftCropBlocks.registerDuskholdCropBottom(DUSKHOLD_CROP_TOP);

    public static final DeferredBlock<Block> KRANDONIAN_CROP_TOP = JolCraftCropBlocks.registerKrandonianCropTop();
    public static final DeferredBlock<Block> KRANDONIAN_CROP_BOTTOM = JolCraftCropBlocks.registerKrandonianCropBottom(KRANDONIAN_CROP_TOP);

    public static final DeferredBlock<Block> YANILLIAN_CROP_TOP = JolCraftCropBlocks.registerYanillianCropTop();
    public static final DeferredBlock<Block> YANILLIAN_CROP_BOTTOM = JolCraftCropBlocks.registerYanillianCropBottom(YANILLIAN_CROP_TOP);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} blocks",
                BLOCKS.getEntries().size()
        );
    }
}