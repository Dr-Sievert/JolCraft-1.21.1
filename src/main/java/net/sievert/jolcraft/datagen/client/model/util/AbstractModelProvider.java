package net.sievert.jolcraft.datagen.client.model.util;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
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
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractModelProvider extends ModelProvider {

    protected AbstractModelProvider(PackOutput output, String modId) {
        super(output, modId);
    }

    /**
     * Subprovider contract.
     */
    public interface ModelSubProvider {
        void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items);
    }

    protected final void runAll(
            @NotNull BlockModelGenerators blocks,
            @NotNull ItemModelGenerators items,
            @NotNull List<? extends ModelSubProvider> subs
    ) {
        CountingHooks hooks = CountingHooks.install(blocks, items);

        try {
            long beforeTotal = hooks.total();

            for (ModelSubProvider sub : subs) {
                long before = hooks.total();

                sub.addModels(blocks, items);

                long added = hooks.total() - before;
                String name = sub.getClass().getSimpleName();

                JolCraftLogs.debug(
                        JolCraftLogTags.DATAGEN,
                        "Model subprovider {}: +{} outputs",
                        name,
                        added
                );

                if (added == 0) {
                    JolCraftLogs.warn(
                            JolCraftLogTags.DATAGEN,
                            "Model subprovider {} added 0 outputs.",
                            name
                    );
                }
            }

            long totalAdded = hooks.total() - beforeTotal;
            JolCraftLogs.debug(
                    JolCraftLogTags.DATAGEN,
                    "Total models generated: {} ({} subproviders)",
                    totalAdded,
                    subs.size()
            );
        } finally {
            hooks.restore();
        }
    }

    private static final class CountingHooks {

        private long count;

        private final List<Restore> restores = new java.util.ArrayList<>();

        static CountingHooks install(BlockModelGenerators blocks, ItemModelGenerators items) {
            CountingHooks hooks = new CountingHooks();
            hooks.wrap(blocks);
            hooks.wrap(items);
            return hooks;
        }

        long total() {
            return count;
        }

        private void inc() {
            count++;
        }

        void restore() {
            for (int i = restores.size() - 1; i >= 0; i--) {
                Restore r = restores.get(i);
                try {
                    setField(r.instance, r.field, r.original);
                } catch (Throwable ignored) {
                }
            }
            restores.clear();
        }

        private void wrap(Object generator) {
            for (Class<?> c = generator.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    wrapField(generator, field);
                }
            }
        }

        private void wrapField(Object target, Field field) {
            try {
                field.setAccessible(true);
                Object original = field.get(target);
                if (original == null || original instanceof CountingMarker) return;

                Object wrapped = switch (original) {
                    case Consumer<?> consumer -> new CountingConsumer<>(this, consumer);
                    case BiConsumer<?, ?> biConsumer -> new CountingBiConsumer<>(this, biConsumer);
                    case ItemModelOutput itemOut -> new CountingItemModelOutput(this, itemOut);
                    default -> null;
                };
                if (wrapped == null) return;

                restores.add(new Restore(target, field, original));
                setField(target, field, wrapped);
            } catch (Throwable ignored) {
            }
        }

        private record Restore(Object instance, Field field, Object original) {}

        private interface CountingMarker {}

        private record CountingConsumer<T>(CountingHooks hooks, Consumer<T> delegate)
                implements Consumer<T>, CountingMarker {
            @Override public void accept(T t) { hooks.inc(); delegate.accept(t); }
        }

        private record CountingBiConsumer<A, B>(CountingHooks hooks, BiConsumer<A, B> delegate)
                implements BiConsumer<A, B>, CountingMarker {
            @Override public void accept(A a, B b) { hooks.inc(); delegate.accept(a, b); }
        }

        private record CountingItemModelOutput(CountingHooks hooks, ItemModelOutput delegate)
                implements ItemModelOutput, CountingMarker {
            @Override public void accept(@NotNull Item item, ItemModel.@NotNull Unbaked model) {
                hooks.inc();
                delegate.accept(item, model);
            }
            @Override public void copy(@NotNull Item from, @NotNull Item to) {
                hooks.inc();
                delegate.copy(from, to);
            }
        }

        private static void setField(Object instance, Field field, Object value) throws Throwable {
            try {
                field.set(instance, value);
                return;
            } catch (IllegalAccessException ignored) {
            }

            Class<?> owner = field.getDeclaringClass();
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, MethodHandles.lookup());
            VarHandle handle = lookup.findVarHandle(owner, field.getName(), field.getType());
            handle.set(instance, value);
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

    // ---------------------------------------------------------------------
    // ModelLocationUtils helpers
    // ---------------------------------------------------------------------

    private static @NotNull String idPathFromModelLocation(@NotNull ResourceLocation modelLoc, @NotNull String prefix) {
        String path = modelLoc.getPath();
        if (path.startsWith(prefix)) return path.substring(prefix.length());
        return path;
    }

    private static @NotNull String itemIdPath(@NotNull Item item) {
        return idPathFromModelLocation(ModelLocationUtils.getModelLocation(item), "item/");
    }

    private static @NotNull String blockIdPath(@NotNull Block block) {
        return idPathFromModelLocation(ModelLocationUtils.getModelLocation(block), "block/");
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

        String layerZeroName = itemIdPath(layerZeroItem);
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

        String layerZeroName = itemIdPath(layerZeroItem);
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

        String blockName = blockIdPath(block);
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