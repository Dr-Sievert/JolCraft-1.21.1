package net.sievert.jolcraft.datagen.client.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class JolCraftModelBuilder {

    private static final String ITEM_PREFIX = "item/";
    private static final String BLOCK_PREFIX = "block/";

    private static final String GENERATED_ITEM_PARENT = "minecraft:item/generated";
    private static final String HANDHELD_ITEM_PARENT = "minecraft:item/handheld";

    private static final int COMPASS_FRAME_COUNT = 32;
    private static final int COMPASS_BASE_FRAME = COMPASS_FRAME_COUNT / 2;

    private final @NotNull JolCraftModelProvider provider;
    private final @NotNull PackOutput.PathProvider blockStatePathProvider;
    private final @NotNull PackOutput.PathProvider modelPathProvider;

    private final @NotNull Map<Block, BlockStateGenerator> blockStates = Maps.newLinkedHashMap();
    private final @NotNull Map<ResourceLocation, Supplier<JsonElement>> models = Maps.newLinkedHashMap();

    private final @NotNull BlockModelGenerators blocks;
    private final @NotNull ItemModelGenerators items;

    public JolCraftModelBuilder(@NotNull JolCraftModelProvider provider) {
        this.provider = provider;
        this.blockStatePathProvider = provider.packOutput().createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = provider.packOutput().createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");

        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = this::addModel;

        this.blocks = new BlockModelGenerators(
                this::addBlockState,
                modelOutput,
                block -> {
                }
        );
        this.items = new ItemModelGenerators(modelOutput);
    }

    public @NotNull JolCraftModelProvider provider() {
        return provider;
    }

    public @NotNull BlockModelGenerators blocks() {
        return blocks;
    }

    public @NotNull ItemModelGenerators items() {
        return items;
    }

    public int blockStateCount() {
        return blockStates.size();
    }

    public int modelCount() {
        return models.size();
    }

    public int totalCount() {
        return blockStateCount() + modelCount();
    }

    public void addBlockState(@NotNull BlockStateGenerator generator) {
        Block block = generator.getBlock();
        BlockStateGenerator previous = blockStates.put(block, generator);
        if (previous != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + block);
        }
    }

    public void addModel(
            @NotNull ResourceLocation location,
            @NotNull Supplier<JsonElement> json
    ) {
        Supplier<JsonElement> previous = models.put(location, json);
        if (previous != null) {
            throw new IllegalStateException("Duplicate model definition for " + location);
        }
    }

    public @NotNull CompletableFuture<?> save(@NotNull CachedOutput cachedOutput) {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[blockStates.size() + models.size()];

        int index = 0;

        for (Map.Entry<Block, BlockStateGenerator> entry : blockStates.entrySet()) {
            Path path = blockStatePathProvider.json(entry.getKey().builtInRegistryHolder().key().location());
            futures[index++] = DataProvider.saveStable(cachedOutput, entry.getValue().get(), path);
        }

        for (Map.Entry<ResourceLocation, Supplier<JsonElement>> entry : models.entrySet()) {
            Path path = modelPathProvider.json(entry.getKey());
            futures[index++] = DataProvider.saveStable(cachedOutput, entry.getValue().get(), path);
        }

        return CompletableFuture.allOf(futures);
    }

    public void delegateItemToBlockModel(@NotNull Block block) {
        addModel(
                ModelLocationUtils.getModelLocation(block.asItem()),
                new DelegatedModel(ModelLocationUtils.getModelLocation(block))
        );
    }

    public void delegateItemModel(
            @NotNull Item item,
            @NotNull ResourceLocation delegateModel
    ) {
        addModel(
                ModelLocationUtils.getModelLocation(item),
                new DelegatedModel(delegateModel)
        );
    }

    private static @NotNull String stripPrefix(
            @NotNull String path,
            @NotNull String prefix
    ) {
        return path.startsWith(prefix) ? path.substring(prefix.length()) : path;
    }

    private static @NotNull ResourceLocation withSubFolder(
            @NotNull ResourceLocation base,
            @NotNull String rootPrefix,
            @NotNull String subFolder
    ) {
        String stripped = stripPrefix(base.getPath(), rootPrefix);
        return base.withPath(rootPrefix + subFolder + "/" + stripped);
    }

    private static @NotNull ResourceLocation itemTexture(@NotNull Item item) {
        return TextureMapping.getItemTexture(item);
    }

    private static @NotNull ResourceLocation itemTexture(
            @NotNull Item item,
            @NotNull String subFolder
    ) {
        return withSubFolder(TextureMapping.getItemTexture(item), ITEM_PREFIX, subFolder);
    }

    private static @NotNull ResourceLocation blockTexture(@NotNull Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    private static @NotNull ResourceLocation blockTexture(
            @NotNull Block block,
            @NotNull String subFolder
    ) {
        return withSubFolder(TextureMapping.getBlockTexture(block), BLOCK_PREFIX, subFolder);
    }

    private void createFlatItemModel(
            @NotNull Item item,
            @NotNull ResourceLocation texture
    ) {
        ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(texture),
                this::addModel
        );
    }

    private void createHandheldItemModel(
            @NotNull Item item,
            @NotNull ResourceLocation texture
    ) {
        ModelTemplates.FLAT_HANDHELD_ITEM.create(
                ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(texture),
                this::addModel
        );
    }

    public void flatItem(@NotNull Item item) {
        createFlatItemModel(item, itemTexture(item));
    }

    public void flatItem(
            @NotNull Item item,
            @NotNull String subFolder
    ) {
        createFlatItemModel(item, itemTexture(item, subFolder));
    }

    public void flatItem(
            @NotNull Item item,
            @NotNull Item textureSource,
            @NotNull String subFolder
    ) {
        createFlatItemModel(item, itemTexture(textureSource, subFolder));
    }

    public void flatBlockItem(@NotNull Block block) {
        createFlatItemModel(block.asItem(), blockTexture(block));
    }

    public void handheldItem(@NotNull Item item) {
        createHandheldItemModel(item, itemTexture(item));
    }

    public void handheldItem(
            @NotNull Item item,
            @NotNull String subFolder
    ) {
        createHandheldItemModel(item, itemTexture(item, subFolder));
    }

    public void cubeAll(@NotNull Block block) {
        blocks.createTrivialCube(block);
    }

    public void cubeTopBottomWithItem(@NotNull Block block) {
        blocks.createTrivialBlock(block, TexturedModel.CUBE_TOP_BOTTOM);
        delegateItemToBlockModel(block);
    }

    public void cubeAllWithItem(@NotNull Block block) {
        blocks.createTrivialCube(block);
        delegateItemToBlockModel(block);
    }

    public void cubeAllWithItem(
            @NotNull Block block,
            @NotNull String subFolder
    ) {
        ResourceLocation model = ModelTemplates.CUBE_ALL.create(
                block,
                TextureMapping.cube(blockTexture(block, subFolder)),
                this::addModel
        );

        addBlockState(MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, model)
        ));

        delegateItemToBlockModel(block);
    }

    public void rotatedPillarWithHorizontalVariantAndItem(@NotNull Block block) {
        ResourceLocation verticalModel = TexturedModel.COLUMN.create(block, this::addModel);
        ResourceLocation horizontalModel = TexturedModel.COLUMN_HORIZONTAL.create(block, this::addModel);

        addBlockState(
                MultiVariantGenerator.multiVariant(block)
                        .with(
                                PropertyDispatch.property(BlockStateProperties.AXIS)
                                        .select(
                                                net.minecraft.core.Direction.Axis.Y,
                                                Variant.variant().with(VariantProperties.MODEL, verticalModel)
                                        )
                                        .select(
                                                net.minecraft.core.Direction.Axis.Z,
                                                Variant.variant()
                                                        .with(VariantProperties.MODEL, horizontalModel)
                                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        )
                                        .select(
                                                net.minecraft.core.Direction.Axis.X,
                                                Variant.variant()
                                                        .with(VariantProperties.MODEL, horizontalModel)
                                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        )
                        )
        );

        delegateItemToBlockModel(block);
    }

    public @NotNull ResourceLocation createSuffixedVariant(
            @NotNull Block block,
            @NotNull String suffix,
            @NotNull ModelTemplate template,
            @NotNull Function<ResourceLocation, TextureMapping> textureMappingGetter
    ) {
        return template.createWithSuffix(
                block,
                suffix,
                textureMappingGetter.apply(TextureMapping.getBlockTexture(block, suffix)),
                this::addModel
        );
    }

    public static <T extends Comparable<T>> @NotNull PropertyDispatch createEmptyOrFullDispatch(
            @NotNull Property<T> property,
            @NotNull T minimumValueForFullVariant,
            @NotNull ResourceLocation fullModel,
            @NotNull ResourceLocation emptyModel
    ) {
        Variant full = Variant.variant().with(VariantProperties.MODEL, fullModel);
        Variant empty = Variant.variant().with(VariantProperties.MODEL, emptyModel);

        return PropertyDispatch.property(property).generate(value ->
                value.compareTo(minimumValueForFullVariant) >= 0 ? full : empty
        );
    }

    public void createCropBlock(
            @NotNull Block cropBlock,
            @NotNull Property<Integer> ageProperty,
            int... ageToVisualStageMapping
    ) {
        if (ageProperty.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException("Mismatch between age property values and visual stage mapping!");
        }

        Int2ObjectMap<ResourceLocation> cache = new Int2ObjectOpenHashMap<>();

        PropertyDispatch dispatch = PropertyDispatch.property(ageProperty).generate(age -> {
            int stage = ageToVisualStageMapping[age];
            ResourceLocation model = cache.computeIfAbsent(
                    stage,
                    i -> createSuffixedVariant(cropBlock, "_stage" + i, ModelTemplates.CROP, TextureMapping::crop)
            );
            return Variant.variant().with(VariantProperties.MODEL, model);
        });

        flatItem(cropBlock.asItem());
        addBlockState(MultiVariantGenerator.multiVariant(cropBlock).with(dispatch));
    }

    public void createPlantWithDefaultItem(
            @NotNull Block plantBlock,
            @NotNull Block pottedPlantBlock,
            @NotNull ModelTemplate plantTemplate,
            @NotNull ModelTemplate pottedPlantTemplate,
            @NotNull TextureMapping plantTextureMapping,
            @NotNull TextureMapping pottedPlantTextureMapping
    ) {
        flatBlockItem(plantBlock);

        ResourceLocation plantModel = plantTemplate.create(
                plantBlock,
                plantTextureMapping,
                this::addModel
        );
        addBlockState(MultiVariantGenerator.multiVariant(
                plantBlock,
                Variant.variant().with(VariantProperties.MODEL, plantModel)
        ));

        ResourceLocation pottedModel = pottedPlantTemplate.create(
                pottedPlantBlock,
                pottedPlantTextureMapping,
                this::addModel
        );
        addBlockState(MultiVariantGenerator.multiVariant(
                pottedPlantBlock,
                Variant.variant().with(VariantProperties.MODEL, pottedModel)
        ));
    }

    public void createPlantWithDefaultItem(
            @NotNull Block plantBlock,
            @NotNull Block pottedPlantBlock
    ) {
        createPlantWithDefaultItem(
                plantBlock,
                pottedPlantBlock,
                ModelTemplates.CROSS,
                ModelTemplates.FLOWER_POT_CROSS,
                TextureMapping.cross(plantBlock),
                TextureMapping.plant(plantBlock)
        );
    }

    public void manualBlockState(@NotNull Block block) {
        addBlockState(MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(
                        VariantProperties.MODEL,
                        ModelLocationUtils.getModelLocation(block)
                )
        ));
    }

    private static @NotNull JsonObject layeredItemModel(
            @NotNull String parent,
            @NotNull ResourceLocation... textures
    ) {
        if (textures.length == 0) {
            throw new IllegalArgumentException(
                    "A layered item model requires at least one texture"
            );
        }

        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);

        JsonObject textureJson = new JsonObject();
        for (int layer = 0; layer < textures.length; layer++) {
            textureJson.addProperty(
                    "layer" + layer,
                    textures[layer].toString()
            );
        }

        json.add("textures", textureJson);
        return json;
    }

    private static @NotNull ResourceLocation compassFrame(
            @NotNull ResourceLocation base,
            int frame
    ) {
        return base.withSuffix(
                String.format(Locale.ROOT, "_%02d", frame)
        );
    }

    public void layeredItem(
            @NotNull Item item,
            @NotNull ResourceLocation... textures
    ) {
        addModel(
                ModelLocationUtils.getModelLocation(item),
                () -> layeredItemModel(GENERATED_ITEM_PARENT, textures)
        );
    }

    public void layeredHandheldItem(
            @NotNull Item item,
            @NotNull ResourceLocation... textures
    ) {
        addModel(
                ModelLocationUtils.getModelLocation(item),
                () -> layeredItemModel(HANDHELD_ITEM_PARENT, textures)
        );
    }

    public void flatItemWithOverlay(
            @NotNull Item item,
            @NotNull String subFolder
    ) {
        ResourceLocation texture = itemTexture(item, subFolder);

        layeredItem(
                item,
                texture,
                texture.withSuffix("_overlay")
        );
    }

    public void handheldItemWithOverlay(
            @NotNull Item item,
            @NotNull String subFolder
    ) {
        ResourceLocation texture = itemTexture(item, subFolder);

        layeredHandheldItem(
                item,
                texture,
                texture.withSuffix("_overlay")
        );
    }

    public void compassItem(
            @NotNull Item compass,
            @NotNull Item emptyCompass,
            @NotNull Item dial,
            @NotNull ResourceLocation angleProperty,
            @NotNull String subFolder
    ) {
        ResourceLocation model =
                ModelLocationUtils.getModelLocation(compass);

        ResourceLocation bodyTexture =
                itemTexture(emptyCompass, subFolder);
        ResourceLocation bodyOverlayTexture =
                bodyTexture.withSuffix("_overlay");

        ResourceLocation dialTexture =
                itemTexture(dial, subFolder);
        ResourceLocation dialExtraTexture =
                dialTexture.withSuffix("_extra");

        // Vanilla generates all suffixed models except frame 16.
        // The root compass model itself represents frame 16.
        for (int frame = 0; frame < COMPASS_FRAME_COUNT; frame++) {
            if (frame == COMPASS_BASE_FRAME) {
                continue;
            }

            ResourceLocation frameModel =
                    compassFrame(model, frame);
            ResourceLocation frameDialExtra =
                    compassFrame(dialExtraTexture, frame);
            ResourceLocation frameDial =
                    compassFrame(dialTexture, frame);

            addModel(
                    frameModel,
                    () -> layeredItemModel(
                            GENERATED_ITEM_PARENT,
                            bodyTexture,
                            bodyOverlayTexture,
                            frameDialExtra,
                            frameDial
                    )
            );
        }

        addModel(model, () -> {
            JsonObject json = layeredItemModel(
                    GENERATED_ITEM_PARENT,
                    bodyTexture,
                    bodyOverlayTexture,
                    compassFrame(
                            dialExtraTexture,
                            COMPASS_BASE_FRAME
                    ),
                    compassFrame(
                            dialTexture,
                            COMPASS_BASE_FRAME
                    )
            );

            JsonArray overrides = new JsonArray();

            // 33 entries: frame 16 occurs at both ends of the cycle.
            for (int step = 0; step <= COMPASS_FRAME_COUNT; step++) {
                int frame =
                        (COMPASS_BASE_FRAME + step) % COMPASS_FRAME_COUNT;

                float angle = step == 0
                        ? 0.0F
                        : (2 * step - 1)
                        / (2.0F * COMPASS_FRAME_COUNT);

                JsonObject predicate = new JsonObject();
                predicate.addProperty(
                        angleProperty.toString(),
                        angle
                );

                JsonObject override = new JsonObject();
                override.add("predicate", predicate);
                override.addProperty(
                        "model",
                        frame == COMPASS_BASE_FRAME
                                ? model.toString()
                                : compassFrame(model, frame).toString()
                );

                overrides.add(override);
            }

            json.add("overrides", overrides);
            return json;
        });
    }

    public @NotNull PropertyDispatch createColumnWithFacing() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(
                        net.minecraft.core.Direction.DOWN,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                )
                .select(
                        net.minecraft.core.Direction.UP,
                        Variant.variant()
                )
                .select(
                        net.minecraft.core.Direction.NORTH,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                )
                .select(
                        net.minecraft.core.Direction.SOUTH,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                )
                .select(
                        net.minecraft.core.Direction.WEST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                )
                .select(
                        net.minecraft.core.Direction.EAST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                );
    }
}