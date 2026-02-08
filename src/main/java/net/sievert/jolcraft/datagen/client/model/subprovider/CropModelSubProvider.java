package net.sievert.jolcraft.datagen.client.model.subprovider;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.crop.BarleyCropBlock;
import net.sievert.jolcraft.world.block.custom.crop.FesterlingCropBlock;
import net.sievert.jolcraft.world.block.custom.crop.HopsCropBottomBlock;
import net.sievert.jolcraft.world.block.custom.crop.HopsCropTopBlock;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public final class CropModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        // -------- Items --------
        items.generateFlatItem(JolCraftItems.BARLEY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        items.generateFlatItem(JolCraftItems.ASGARNIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.DUSKHOLD_HOPS.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.KRANDONIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.YANILLIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);

        items.generateFlatItem(JolCraftItems.DEEPSLATE_BULBS.get(), ModelTemplates.FLAT_ITEM);

        // -------- Blocks --------
        createVerdantFarmland(blocks);
        blocks.createTrivialCube(JolCraftBlocks.VERDANT_SOIL.get());

        blocks.createCropBlock(JolCraftBlocks.BARLEY_CROP.get(), BarleyCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
        blocks.createRotatedPillarWithHorizontalVariant(
                JolCraftBlocks.BARLEY_BLOCK.get(),
                TexturedModel.COLUMN,
                TexturedModel.COLUMN_HORIZONTAL
        );

        blocks.createPlantWithDefaultItem(
                JolCraftBlocks.DUSKCAP.get(),
                JolCraftBlocks.POTTED_DUSKCAP.get(),
                BlockModelGenerators.PlantType.NOT_TINTED
        );

        createFesterlingCrop(blocks);
        blocks.createPlantWithDefaultItem(
                JolCraftBlocks.FESTERLING.get(),
                JolCraftBlocks.POTTED_FESTERLING.get(),
                BlockModelGenerators.PlantType.NOT_TINTED
        );

        createTopCropBlock(blocks, JolCraftBlocks.ASGARNIAN_CROP_TOP.get(), 0, 1, 2, 3, 4);
        blocks.createCropBlock(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        createTopCropBlock(blocks, JolCraftBlocks.DUSKHOLD_CROP_TOP.get(), 0, 1, 2, 3, 4);
        blocks.createCropBlock(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        createTopCropBlock(blocks, JolCraftBlocks.KRANDONIAN_CROP_TOP.get(), 0, 1, 2, 3, 4);
        blocks.createCropBlock(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        createTopCropBlock(blocks, JolCraftBlocks.YANILLIAN_CROP_TOP.get(), 0, 1, 2, 3, 4);
        blocks.createCropBlock(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        blocks.blockStateOutput.accept(new BlockStateGenerator() {
            @Override
            public JsonObject get() {
                JsonObject root = new JsonObject();
                JsonObject variants = new JsonObject();
                for (int age = 0; age <= 9; age++) {
                    variants.add("age=" + age, modelObj("block/deepslate_bulbs_crop_stage" + age));
                }
                root.add("variants", variants);
                return root;
            }

            @Override
            public @NotNull Block getBlock() {
                return JolCraftBlocks.DEEPSLATE_BULBS_CROP.get();
            }
        });
    }

    private static void createTopCropBlock(BlockModelGenerators blockModels, Block block, int... ageToVisualStageMapping) {
        if (HopsCropTopBlock.TOP_AGE.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException("Mismatch between age property values and visual stage mapping!");
        }

        Int2ObjectMap<ResourceLocation> visualStageModels = new Int2ObjectOpenHashMap<>();

        PropertyDispatch dispatch = PropertyDispatch.property(HopsCropTopBlock.TOP_AGE).generate(ageValue -> {
            int visualStage = ageToVisualStageMapping[ageValue];
            ResourceLocation modelId = visualStageModels.computeIfAbsent(
                    visualStage,
                    i -> blockModels.createSuffixedVariant(block, "_stage" + i, ModelTemplates.CROP, TextureMapping::crop)
            );
            return Variant.variant().with(VariantProperties.MODEL, modelId);
        });

        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(dispatch));
    }

    private static void createFesterlingCrop(BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.FESTERLING_CROP.get())
                        .with(
                                PropertyDispatch.property(FesterlingCropBlock.AGE)
                                        .generate(age -> Variant.variant()
                                                .with(
                                                        VariantProperties.MODEL,
                                                        blockModels.createSuffixedVariant(
                                                                JolCraftBlocks.FESTERLING_CROP.get(),
                                                                "_stage" + age,
                                                                ModelTemplates.CROSS,
                                                                TextureMapping::cross
                                                        )
                                                )
                                        )
                        )
        );
    }

    private static void createVerdantFarmland(BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(JolCraftBlocks.VERDANT_SOIL.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(JolCraftBlocks.VERDANT_FARMLAND.get()));

        ResourceLocation model = ModelTemplates.FARMLAND.create(
                JolCraftBlocks.VERDANT_FARMLAND.get(),
                mapping,
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.VERDANT_FARMLAND.get())
                        .with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, model, model))
        );
    }

    private static JsonObject modelObj(String path) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", JolCraft.MOD_ID + ":" + path);
        return obj;
    }
}