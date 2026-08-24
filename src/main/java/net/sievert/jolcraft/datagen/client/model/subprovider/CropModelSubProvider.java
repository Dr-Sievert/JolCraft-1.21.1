package net.sievert.jolcraft.datagen.client.model.subprovider;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelBuilder;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelSubProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.plant.crop.BarleyCropBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.FesterlingCropBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.HopsCropBottomBlock;
import net.sievert.jolcraft.world.block.custom.plant.crop.HopsCropTopBlock;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public record CropModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.CROP;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.handheldItem(JolCraftItems.BARLEY.get());

        builder.flatItem(JolCraftItems.ASGARNIAN_HOPS.get());
        builder.flatItem(JolCraftItems.DUSKHOLD_HOPS.get());
        builder.flatItem(JolCraftItems.KRANDONIAN_HOPS.get());
        builder.flatItem(JolCraftItems.YANILLIAN_HOPS.get());

        builder.flatItem(JolCraftItems.DEEPSLATE_BULBS.get());

        createVerdantFarmland(builder);
        builder.cubeAllWithItem(JolCraftBlocks.VERDANT_SOIL.get());

        builder.rotatedPillarWithHorizontalVariantAndItem(JolCraftBlocks.BARLEY_BLOCK.get());
        builder.createCropBlock(
                JolCraftBlocks.BARLEY_CROP.get(),
                BarleyCropBlock.AGE,
                0, 1, 2, 3, 4, 5, 6, 7
        );

        builder.createPlantWithSeparateItem(
                JolCraftBlocks.BLOODROOT.get(),
                JolCraftItems.BLOODROOT.get()
        );

        builder.createPlantWithDefaultItem(
                JolCraftBlocks.DUSKCAP.get(),
                JolCraftBlocks.POTTED_DUSKCAP.get()
        );

        builder.createPlantWithDefaultItem(
                JolCraftBlocks.CYANELLA.get(),
                JolCraftBlocks.POTTED_CYANELLA.get()
        );

        builder.createPlantWithDefaultItem(
                JolCraftBlocks.SKYBELL.get(),
                JolCraftBlocks.POTTED_SKYBELL.get()
        );

        createMushroomBlocks(
                builder,
                JolCraftBlocks.DUSKCAP_BLOCK.get(),
                JolCraftBlocks.DUSKCAP_STEM.get(),
                JolCraft.location("block/duskcap_block"),
                JolCraft.location("block/duskcap_block_inside"),
                JolCraft.location("block/duskcap_stem")
        );

        createFesterlingCrop(builder);

        builder.createPlantWithDefaultItem(
                JolCraftBlocks.FESTERLING.get(),
                JolCraftBlocks.POTTED_FESTERLING.get()
        );

        createMushroomBlocks(
                builder,
                JolCraftBlocks.FESTERLING_BLOCK.get(),
                JolCraftBlocks.FESTERLING_STEM.get(),
                JolCraft.location("block/festerling_block"),
                JolCraft.location("block/festerling_block_inside"),
                JolCraft.location("block/festerling_stem")
        );

        createTopCropBlock(builder, JolCraftBlocks.ASGARNIAN_CROP_TOP.get(), 0, 1, 2, 3, 4);
        builder.createCropBlock(
                JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(),
                HopsCropBottomBlock.AGE,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        );

        createTopCropBlock(builder, JolCraftBlocks.DUSKHOLD_CROP_TOP.get(), 0, 1, 2, 3, 4);
        builder.createCropBlock(
                JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(),
                HopsCropBottomBlock.AGE,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        );

        createTopCropBlock(builder, JolCraftBlocks.KRANDONIAN_CROP_TOP.get(), 0, 1, 2, 3, 4);
        builder.createCropBlock(
                JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(),
                HopsCropBottomBlock.AGE,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        );

        createTopCropBlock(builder, JolCraftBlocks.YANILLIAN_CROP_TOP.get(), 0, 1, 2, 3, 4);
        builder.createCropBlock(
                JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(),
                HopsCropBottomBlock.AGE,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        );

        builder.addBlockState(new BlockStateGenerator() {
            @Override
            public JsonObject get() {
                JsonObject root = new JsonObject();
                JsonObject variants = new JsonObject();
                for (int age = 0; age <= 9; age++) {
                    variants.add("age=" + age, modelObj("block/deepslate_bulbs/deepslate_bulbs_crop_stage" + age));
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

    private static void createTopCropBlock(
            @NotNull JolCraftModelBuilder builder,
            @NotNull Block block,
            int... ageToVisualStageMapping
    ) {
        if (HopsCropTopBlock.AGE.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException("Mismatch between age property values and visual stage mapping!");
        }

        Int2ObjectMap<ResourceLocation> visualStageModels = new Int2ObjectOpenHashMap<>();

        PropertyDispatch dispatch = PropertyDispatch.property(HopsCropTopBlock.AGE).generate(ageValue -> {
            int visualStage = ageToVisualStageMapping[ageValue];
            ResourceLocation modelId = visualStageModels.computeIfAbsent(
                    visualStage,
                    i -> builder.createSuffixedVariant(block, "_stage" + i, ModelTemplates.CROP, TextureMapping::crop)
            );
            return Variant.variant().with(VariantProperties.MODEL, modelId);
        });

        builder.addBlockState(MultiVariantGenerator.multiVariant(block).with(dispatch));
    }

    private static void createFesterlingCrop(@NotNull JolCraftModelBuilder builder) {
        builder.addBlockState(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.FESTERLING_CROP.get())
                        .with(
                                PropertyDispatch.property(FesterlingCropBlock.AGE)
                                        .generate(age -> Variant.variant().with(
                                                VariantProperties.MODEL,
                                                builder.createSuffixedVariant(
                                                        JolCraftBlocks.FESTERLING_CROP.get(),
                                                        "_stage" + age,
                                                        ModelTemplates.CROSS,
                                                        TextureMapping::cross
                                                )
                                        ))
                        )
        );
    }

    private static void createVerdantFarmland(@NotNull JolCraftModelBuilder builder) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(JolCraftBlocks.VERDANT_SOIL.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(JolCraftBlocks.VERDANT_FARMLAND.get()));

        ResourceLocation model = ModelTemplates.FARMLAND.create(
                ModelLocationUtils.getModelLocation(JolCraftBlocks.VERDANT_FARMLAND.get()),
                mapping,
                builder::addModel
        );

        builder.addBlockState(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.VERDANT_FARMLAND.get())
                        .with(JolCraftModelBuilder.createEmptyOrFullDispatch(
                                BlockStateProperties.MOISTURE,
                                7,
                                model,
                                model
                        ))
        );

        builder.delegateItemToBlockModel(JolCraftBlocks.VERDANT_FARMLAND.get());
    }

    private static @NotNull JsonObject modelObj(@NotNull String path) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", JolCraft.MOD_ID + ":" + path);
        return obj;
    }

    private static void createMushroomBlocks(
            @NotNull JolCraftModelBuilder builder,
            @NotNull Block capBlock,
            @NotNull Block stemBlock,
            @NotNull ResourceLocation capTexture,
            @NotNull ResourceLocation insideTexture,
            @NotNull ResourceLocation stemTexture
    ) {
        createMushroomBlock(
                builder,
                capBlock,
                capTexture,
                insideTexture
        );

        createMushroomBlock(
                builder,
                stemBlock,
                stemTexture,
                insideTexture
        );
    }

    private static void createMushroomBlock(
            @NotNull JolCraftModelBuilder builder,
            @NotNull Block block,
            @NotNull ResourceLocation outsideTexture,
            @NotNull ResourceLocation insideTexture
    ) {
        ResourceLocation outsideModel = ModelTemplates.SINGLE_FACE.create(
                ModelLocationUtils.getModelLocation(block),
                TextureMapping.defaultTexture(outsideTexture),
                builder::addModel
        );

        ResourceLocation insideModel = ModelTemplates.SINGLE_FACE.create(
                ModelLocationUtils.getModelLocation(block, "_inside"),
                TextureMapping.defaultTexture(insideTexture),
                builder::addModel
        );

        builder.addBlockState(
                MultiPartGenerator.multiPart(block)
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.NORTH, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, outsideModel)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.EAST, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, outsideModel)
                                        .with(
                                                VariantProperties.Y_ROT,
                                                VariantProperties.Rotation.R90
                                        )
                                        .with(VariantProperties.UV_LOCK, true)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.SOUTH, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, outsideModel)
                                        .with(
                                                VariantProperties.Y_ROT,
                                                VariantProperties.Rotation.R180
                                        )
                                        .with(VariantProperties.UV_LOCK, true)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.WEST, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, outsideModel)
                                        .with(
                                                VariantProperties.Y_ROT,
                                                VariantProperties.Rotation.R270
                                        )
                                        .with(VariantProperties.UV_LOCK, true)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.UP, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, outsideModel)
                                        .with(
                                                VariantProperties.X_ROT,
                                                VariantProperties.Rotation.R270
                                        )
                                        .with(VariantProperties.UV_LOCK, true)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.DOWN, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, outsideModel)
                                        .with(
                                                VariantProperties.X_ROT,
                                                VariantProperties.Rotation.R90
                                        )
                                        .with(VariantProperties.UV_LOCK, true)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.NORTH, false),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, insideModel)
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.EAST, false),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, insideModel)
                                        .with(
                                                VariantProperties.Y_ROT,
                                                VariantProperties.Rotation.R90
                                        )
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.SOUTH, false),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, insideModel)
                                        .with(
                                                VariantProperties.Y_ROT,
                                                VariantProperties.Rotation.R180
                                        )
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.WEST, false),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, insideModel)
                                        .with(
                                                VariantProperties.Y_ROT,
                                                VariantProperties.Rotation.R270
                                        )
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.UP, false),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, insideModel)
                                        .with(
                                                VariantProperties.X_ROT,
                                                VariantProperties.Rotation.R270
                                        )
                        )
                        .with(
                                Condition.condition()
                                        .term(BlockStateProperties.DOWN, false),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, insideModel)
                                        .with(
                                                VariantProperties.X_ROT,
                                                VariantProperties.Rotation.R90
                                        )
                        )
        );

        ResourceLocation inventoryModel = ModelTemplates.CUBE_ALL.createWithSuffix(
                block,
                "_inventory",
                TextureMapping.cube(outsideTexture),
                builder::addModel
        );

        builder.delegateItemModel(
                block.asItem(),
                inventoryModel
        );
    }
}