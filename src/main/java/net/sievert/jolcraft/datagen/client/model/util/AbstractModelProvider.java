package net.sievert.jolcraft.datagen.client.model.util;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractModelProvider extends ModelProvider {

    protected AbstractModelProvider(PackOutput output, String modId) {
        super(output, modId);
    }

    public interface ModelSubProvider {
        void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items);
    }

    protected final void runAll(
            @NotNull BlockModelGenerators blocks,
            @NotNull ItemModelGenerators items,
            @NotNull List<? extends ModelSubProvider> subs
    ) {
        for (ModelSubProvider sub : subs) {
            sub.addModels(blocks, items);
        }
    }

    public static @NotNull String subFolder(@NotNull String root, @NotNull String... segments) {
        if (root.isEmpty()) throw new IllegalArgumentException("Root segment must not be empty.");

        StringBuilder sb = new StringBuilder(root);
        for (String seg : segments) {
            if (seg.isEmpty()) throw new IllegalArgumentException("Subfolder segment must not be empty.");
            sb.append('/').append(seg);
        }
        return sb.toString();
    }

    public static void generateFlatItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull Item layerZeroItem,
            @NotNull ModelTemplate template,
            @NotNull String subfolder
    ) {
        if (subfolder.isEmpty()) {
            throw new IllegalArgumentException("Subfolder must not be empty. Use vanilla generateFlatItem for root directory.");
        }

        String layerZeroName = BuiltInRegistries.ITEM.getKey(layerZeroItem).getPath();
        ResourceLocation texture = JolCraft.location("item/" + subfolder + "/" + layerZeroName);
        ResourceLocation modelLoc = ModelLocationUtils.getModelLocation(item);

        template.create(modelLoc, TextureMapping.layer0(texture), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLoc));
    }

    public static void generateFlatItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull ModelTemplate template,
            @NotNull String subfolder
    ) {
        generateFlatItem(itemModels, item, item, template, subfolder);
    }

    public static void generateFlatItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull ModelTemplate template,
            @NotNull String root,
            @NotNull String... segments
    ) {
        generateFlatItem(itemModels, item, item, template, subFolder(root, segments));
    }

    public static void generateHandheldItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull Item layerZeroItem,
            @NotNull String subfolder
    ) {
        if (subfolder.isEmpty()) {
            throw new IllegalArgumentException("Subfolder must not be empty. Use vanilla handheld template for root directory.");
        }

        String layerZeroName = BuiltInRegistries.ITEM.getKey(layerZeroItem).getPath();
        ResourceLocation texture = JolCraft.location("item/" + subfolder + "/" + layerZeroName);
        ResourceLocation modelLoc = ModelLocationUtils.getModelLocation(item);

        ModelTemplates.FLAT_HANDHELD_ITEM.create(modelLoc, TextureMapping.layer0(texture), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLoc));
    }

    public static void generateHandheldItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull String subfolder
    ) {
        generateHandheldItem(itemModels, item, item, subfolder);
    }

    public static void generateHandheldItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull String root,
            @NotNull String... segments
    ) {
        generateHandheldItem(itemModels, item, item, subFolder(root, segments));
    }

    public static void generateTwoLayerItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull ResourceLocation layer0Texture,
            @NotNull ResourceLocation layer1Texture
    ) {
        ResourceLocation modelLoc = ModelLocationUtils.getModelLocation(item);

        ModelTemplates.TWO_LAYERED_ITEM.create(
                modelLoc,
                TextureMapping.layered(layer0Texture, layer1Texture),
                itemModels.modelOutput
        );

        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLoc));
    }

    public static void generateTwoLayerItem(
            @NotNull ItemModelGenerators itemModels,
            @NotNull Item item,
            @NotNull String layer0TexturePath,
            @NotNull String layer1TexturePath
    ) {
        generateTwoLayerItem(
                itemModels,
                item,
                JolCraft.location("item/" + layer0TexturePath),
                JolCraft.location("item/" + layer1TexturePath)
        );
    }

    public static void createTrivialCube(
            @NotNull BlockModelGenerators blockModels,
            @NotNull Block block,
            @NotNull String subfolder
    ) {
        if (subfolder.isEmpty()) {
            throw new IllegalArgumentException("Subfolder must not be empty. Use vanilla createTrivialCube for root directory.");
        }

        String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ResourceLocation texture = JolCraft.location("block/" + subfolder + "/" + blockName);

        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.UP, texture)
                .put(TextureSlot.DOWN, texture)
                .put(TextureSlot.NORTH, texture)
                .put(TextureSlot.SOUTH, texture)
                .put(TextureSlot.EAST, texture)
                .put(TextureSlot.WEST, texture)
                .put(TextureSlot.PARTICLE, texture);

        ResourceLocation modelLoc = ModelLocationUtils.getModelLocation(block);

        ModelTemplates.CUBE.create(modelLoc, mapping, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(VariantProperties.MODEL, modelLoc)
                )
        );
    }

    public static VariantProperties.Rotation rotFromDegrees(int degrees) {
        return switch (degrees) {
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }

    public static int vanillaFacingY(@NotNull Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}