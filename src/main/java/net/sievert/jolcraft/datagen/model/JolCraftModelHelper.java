package net.sievert.jolcraft.datagen.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.crop.FesterlingCropBlock;
import net.sievert.jolcraft.block.custom.crop.HopsCropTopBlock;
import net.sievert.jolcraft.data.custom.lore.LoreRarity;
import net.sievert.jolcraft.data.custom.lore.client.LoreKeyProperty;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.armor.JolCraftEquipmentAssets;
import net.sievert.jolcraft.item.client.coin.CoinPouchAmountProperty;
import net.sievert.jolcraft.item.trim.JolCraftTrimMaterials;

import java.util.*;
import java.util.stream.Collectors;

public class JolCraftModelHelper {

    public static void generateFlatItem(
            ItemModelGenerators itemModels,
            Item item,
            Item layerZeroItem,
            ModelTemplate template,
            String subfolder
    ) {
        if (subfolder == null || subfolder.isEmpty()) {
            throw new IllegalArgumentException("Subfolder must not be null or empty. Use vanilla generateFlatItem for root directory.");
        }

        String layerZeroName = BuiltInRegistries.ITEM.getKey(layerZeroItem).getPath();
        String texturePath = "item/" + subfolder + "/" + layerZeroName;

        ResourceLocation texture = JolCraft.location(texturePath);
        ResourceLocation modelLoc = ModelLocationUtils.getModelLocation(item);

        template.create(
                modelLoc,
                TextureMapping.layer0(texture),
                itemModels.modelOutput
        );

        itemModels.itemModelOutput.accept(
                item,
                ItemModelUtils.plainModel(modelLoc)
        );
    }

    public static void generateFlatItem(
            ItemModelGenerators itemModels,
            Item item,
            ModelTemplate template,
            String subfolder
    ) {
        generateFlatItem(itemModels, item, item, template, subfolder);
    }

    public static void createTrivialCube(
            BlockModelGenerators blockModels,
            Block block,
            String subfolder
    ) {
        if (subfolder == null || subfolder.isEmpty()) {
            throw new IllegalArgumentException("Subfolder must not be null or empty. Use vanilla createTrivialCube for root directory.");
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

        ModelTemplates.CUBE.create(
                modelLoc,
                mapping,
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(VariantProperties.MODEL, modelLoc)
                )
        );
    }

    public static void generateCoinPouchModel(ItemModelGenerators itemModels) {
        Item pouch = JolCraftItems.COIN_POUCH.get();

        ResourceLocation small = JolCraft.location("item/coin/coin_pouch_small");
        ResourceLocation large = JolCraft.location("item/coin/coin_pouch_large");
        ResourceLocation full  = JolCraft.location("item/coin/coin_pouch_full");

        ModelTemplates.FLAT_ITEM.create(small, TextureMapping.layer0(small), itemModels.modelOutput);
        ModelTemplates.FLAT_ITEM.create(large,  TextureMapping.layer0(large),  itemModels.modelOutput);
        ModelTemplates.FLAT_ITEM.create(full,  TextureMapping.layer0(full),  itemModels.modelOutput);

        List<SelectItemModel.SwitchCase<Integer>> cases = List.of(
                ItemModelUtils.when(0,   ItemModelUtils.plainModel(small)),
                ItemModelUtils.when(1,   ItemModelUtils.plainModel(large)),
                ItemModelUtils.when(2,   ItemModelUtils.plainModel(full))
        );

        itemModels.itemModelOutput.accept(
                pouch,
                new SelectItemModel.Unbaked(
                        new SelectItemModel.UnbakedSwitch<>(CoinPouchAmountProperty.INSTANCE, cases),
                        Optional.of(ItemModelUtils.plainModel(small))
                )
        );
    }

    public static void generateLegendaryTomeModels(ItemModelGenerators itemModels) {
        Item tomeItem = JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get();
        ResourceLocation baseModelLoc = ModelLocationUtils.getModelLocation(tomeItem);
        ResourceLocation fallbackTexture = TextureMapping.getItemTexture(tomeItem);

        ModelTemplates.FLAT_ITEM.create(baseModelLoc, TextureMapping.layer0(fallbackTexture), itemModels.modelOutput);
        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(baseModelLoc);

        Set<DwarfLoreKey> legendaryLoreKeys = DwarfLoreEntries.ALL.entrySet().stream()
                .filter(e -> e.getValue().rarity() == LoreRarity.LEGENDARY)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();

        for (DwarfLoreKey loreKey : legendaryLoreKeys) {
            String keyString = loreKey.name().toLowerCase(Locale.ROOT);
            String modelName = "item/book/tome/ancient_dwarven_tome_legendary_" + keyString;
            ResourceLocation modelLoc = JolCraft.location(modelName);

            ModelTemplates.FLAT_ITEM.create(modelLoc, TextureMapping.layer0(modelLoc), itemModels.modelOutput);
            ItemModel.Unbaked model = ItemModelUtils.plainModel(modelLoc);

            switchCases.add(ItemModelUtils.when(keyString, model));
        }

        itemModels.itemModelOutput.accept(
                tomeItem,
                new SelectItemModel.Unbaked(
                        new SelectItemModel.UnbakedSwitch<>(LoreKeyProperty.INSTANCE, switchCases),
                        Optional.of(fallbackModel)
                )
        );
    }

    public static void createTopCropBlock(BlockModelGenerators blockModels, Block block, int... ageToVisualStageMapping) {
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

    public static void createFesterlingCrop(BlockModelGenerators blockModels) {
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

    public static void createVerdantFarmland(BlockModelGenerators blockModels) {
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


    public static void createHearth(Block hearthBlock, BlockModelGenerators blockModels) {
        TextureMapping baseMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(hearthBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(hearthBlock, "_front"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(hearthBlock, "_front"));

        ResourceLocation hearthModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(
                hearthBlock,
                baseMapping,
                blockModels.modelOutput
        );

        TextureMapping litMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(hearthBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(hearthBlock, "_front_on"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(hearthBlock, "_front_on"));

        ResourceLocation hearthOnModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(
                hearthBlock,
                "_on",
                litMapping,
                blockModels.modelOutput
        );

        ResourceLocation chimney = JolCraft.location("block/hearth_chimney");

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(hearthBlock)
                        .with(
                                PropertyDispatch
                                        .properties(
                                                BlockStateProperties.DOUBLE_BLOCK_HALF,
                                                BlockStateProperties.LIT,
                                                BlockStateProperties.HORIZONTAL_FACING
                                        )
                                        .generate((half, lit, facing) -> {
                                            VariantProperties.Rotation xRot = VariantProperties.Rotation.R0;
                                            VariantProperties.Rotation yRot = rotFromDegrees(vanillaFacingY(facing));

                                            if (half == DoubleBlockHalf.LOWER) {
                                                return Variant.variant()
                                                        .with(VariantProperties.MODEL, lit ? hearthOnModel : hearthModel)
                                                        .with(VariantProperties.X_ROT, xRot)
                                                        .with(VariantProperties.Y_ROT, yRot);
                                            } else {
                                                return Variant.variant()
                                                        .with(VariantProperties.MODEL, chimney)
                                                        .with(VariantProperties.X_ROT, xRot)
                                                        .with(VariantProperties.Y_ROT, yRot);
                                            }
                                        })
                        )
        );
    }

    public static void generateSpawnEgg(
            ItemModelGenerators itemModels,
            Item eggItem,
            String primaryHex,
            String secondaryHex
    ) {
        int primaryColor = eggColorPrimary(primaryHex);
        int secondaryColor = eggColorSecondary(secondaryHex);
        itemModels.generateSpawnEgg(eggItem, primaryColor, secondaryColor);
    }

    private static int eggColor(String hex, int mask) {
        String s = hex.trim();
        if (s.startsWith("#")) s = s.substring(1);
        else if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
        int rgb = Integer.parseInt(s, 16) & 0xFFFFFF;
        int r = Math.min(255, (((rgb >> 16) & 0xFF) * 255 + mask) / mask);
        int g = Math.min(255, (((rgb >> 8)  & 0xFF) * 255 + mask) / mask);
        int b = Math.min(255, (( rgb        & 0xFF) * 255 + mask) / mask);
        return (int)(0xFF000000L | (r << 16) | (g << 8) | b);
    }

    private static int eggColorPrimary(String hex) {
        return eggColor(hex, 232);
    }

    private static int eggColorSecondary(String hex) {
        return eggColor(hex, 222);
    }

    private static final String[] ARMOR_TYPES = {"helmet", "chestplate", "leggings", "boots"};

    public static final List<ItemModelGenerators.TrimMaterialData> JOLCRAFT_TRIMS = List.of(
            //Armor = override
            new ItemModelGenerators.TrimMaterialData(
                    "deepslate",
                    JolCraftTrimMaterials.DEEPSLATE,
                    Map.of(JolCraftEquipmentAssets.DEEPSLATE_KEY, "deepslate_darker")
            ),
            new ItemModelGenerators.TrimMaterialData(
                    "mithril",
                    JolCraftTrimMaterials.MITHRIL,
                    Map.of(JolCraftEquipmentAssets.MITHRIL_KEY, "mithril_darker")
            ),
            // Gems (no override, use Map.of())
            new ItemModelGenerators.TrimMaterialData("aegiscore", JolCraftTrimMaterials.AEGISCORE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("ashfang", JolCraftTrimMaterials.ASHFANG, Map.of()),
            new ItemModelGenerators.TrimMaterialData("deepmarrow", JolCraftTrimMaterials.DEEPMARROW, Map.of()),
            new ItemModelGenerators.TrimMaterialData("earthblood", JolCraftTrimMaterials.EARTHBLOOD, Map.of()),
            new ItemModelGenerators.TrimMaterialData("emberglass", JolCraftTrimMaterials.EMBERGLASS, Map.of()),
            new ItemModelGenerators.TrimMaterialData("frostvein", JolCraftTrimMaterials.FROSTVEIN, Map.of()),
            new ItemModelGenerators.TrimMaterialData("grimstone", JolCraftTrimMaterials.GRIMSTONE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("ironheart", JolCraftTrimMaterials.IRONHEART, Map.of()),
            new ItemModelGenerators.TrimMaterialData("lumiere", JolCraftTrimMaterials.LUMIERE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("moonshard", JolCraftTrimMaterials.MOONSHARD, Map.of()),
            new ItemModelGenerators.TrimMaterialData("rustagate", JolCraftTrimMaterials.RUSTAGATE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("skyburrow", JolCraftTrimMaterials.SKYBURROW, Map.of()),
            new ItemModelGenerators.TrimMaterialData("sungleam", JolCraftTrimMaterials.SUNGLEAM, Map.of()),
            new ItemModelGenerators.TrimMaterialData("verdanite", JolCraftTrimMaterials.VERDANITE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("woecrystal", JolCraftTrimMaterials.WOECRYSTAL, Map.of())
    );

    public static final Map<String, ResourceKey<TrimMaterial>> VANILLA_TRIMS = Map.of(
            "quartz",   TrimMaterials.QUARTZ,
            "iron",     TrimMaterials.IRON,
            "netherite",TrimMaterials.NETHERITE,
            "redstone", TrimMaterials.REDSTONE,
            "copper",   TrimMaterials.COPPER,
            "gold",     TrimMaterials.GOLD,
            "emerald",  TrimMaterials.EMERALD,
            "diamond",  TrimMaterials.DIAMOND,
            "lapis",    TrimMaterials.LAPIS,
            "amethyst", TrimMaterials.AMETHYST
    );

    public static void generateTrimmableItemWithCustomList(
            ItemModelGenerators itemModels,
            String baseName,
            ResourceKey<EquipmentAsset> key,
            boolean dyeable,
            List<ItemModelGenerators.TrimMaterialData> trimMaterialList) {

        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            ResourceLocation baseModelLocation = ResourceLocation.fromNamespaceAndPath(key.location().getNamespace(), "item/" + fileName);
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(key.location().getNamespace(), "item/" + fileName);
            ResourceLocation overlayTexture = ResourceLocation.fromNamespaceAndPath(key.location().getNamespace(), "item/" + fileName + "_overlay");

            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> list = new ArrayList<>(trimMaterialList.size());

            for (ItemModelGenerators.TrimMaterialData data : trimMaterialList) {
                ResourceLocation trimModelLoc = baseModelLocation.withSuffix("_" + data.name() + "_trim");

                String trimTextureName = data.name();
                if (baseName.equals(data.name())) {
                    trimTextureName += "_darker";
                }
                ResourceLocation trimTextureLocation = ResourceLocation.withDefaultNamespace("trims/items/" + type + "_trim_" + trimTextureName);

                ItemModel.Unbaked bakedModel;
                if (dyeable) {
                    itemModels.generateLayeredItem(
                            trimModelLoc,
                            textureLocation,
                            overlayTexture,
                            trimTextureLocation
                    );
                    bakedModel = ItemModelUtils.tintedModel(trimModelLoc, new Dye(-6265536));
                } else {
                    itemModels.generateLayeredItem(
                            trimModelLoc,
                            textureLocation,
                            trimTextureLocation
                    );
                    bakedModel = ItemModelUtils.plainModel(trimModelLoc);
                }
                list.add(ItemModelUtils.when(data.materialKey(), bakedModel));
            }

            ItemModel.Unbaked defaultModel;
            if (dyeable) {
                ModelTemplates.TWO_LAYERED_ITEM.create(baseModelLocation, TextureMapping.layered(textureLocation, overlayTexture), itemModels.modelOutput);
                defaultModel = ItemModelUtils.tintedModel(baseModelLocation, new Dye(-6265536));
            } else {
                ModelTemplates.FLAT_ITEM.create(baseModelLocation, TextureMapping.layer0(textureLocation), itemModels.modelOutput);
                defaultModel = ItemModelUtils.plainModel(baseModelLocation);
            }

            Item armorItem = getItemFromBaseName(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), defaultModel, list)
            );
        }
    }

    public static void generateTrimmableArmorSetWithCustom(
            ItemModelGenerators itemModels,
            String baseName,
            ResourceKey<EquipmentAsset> key,
            boolean dyeable
    ) {
        List<ItemModelGenerators.TrimMaterialData> allTrims = new ArrayList<>(ItemModelGenerators.TRIM_MATERIAL_MODELS);
        allTrims.addAll(JOLCRAFT_TRIMS);
        generateTrimmableItemWithCustomList(itemModels, baseName, key, dyeable, allTrims);
    }

    public static void generateArmorWithTrim(
            ItemModelGenerators itemModels,
            String baseName,
            ResourceKey<EquipmentAsset> key,
            boolean dyeable) {

        List<ItemModelGenerators.TrimMaterialData> allTrimMaterials = new ArrayList<>();

        for (Map.Entry<String, ResourceKey<TrimMaterial>> entry : VANILLA_TRIMS.entrySet()) {
            allTrimMaterials.add(new ItemModelGenerators.TrimMaterialData(entry.getKey(), entry.getValue(), Map.of()));
        }

        allTrimMaterials.addAll(JOLCRAFT_TRIMS);

        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> selectCases = new ArrayList<>();

            for (ItemModelGenerators.TrimMaterialData trim : allTrimMaterials) {
                boolean isCustom = trim.materialKey().location().getNamespace().equals("jolcraft");
                boolean isVanillaArmor = baseName.equals("diamond") || baseName.equals("netherite") || baseName.equals("leather")
                        || baseName.equals("iron") || baseName.equals("golden") || baseName.equals("chainmail");
                String trimName = trim.name();

                ResourceLocation caseModelLoc;

                if (!isVanillaArmor && isCustom) {
                    caseModelLoc = JolCraft.location("item/" + fileName + "_" + trimName + "_trim");

                    ResourceLocation texture = JolCraft.location("item/" + fileName);
                    ResourceLocation overlay = JolCraft.location("item/" + fileName + "_overlay");
                    ResourceLocation trimTexture = JolCraft.location("trims/items/" + type + "_trim_" + trimName);

                    addTrimModelToList(
                            itemModels,
                            caseModelLoc,
                            texture,
                            overlay,
                            trim,
                            trimTexture,
                            selectCases,
                            dyeable
                    );
                } else if (isCustom) {
                    caseModelLoc = JolCraft.location("item/" + fileName);

                    ResourceLocation texture = ResourceLocation.withDefaultNamespace("item/" + fileName);
                    ResourceLocation overlay = ResourceLocation.withDefaultNamespace("item/" + fileName + "_overlay");
                    ResourceLocation trimTexture = ResourceLocation.withDefaultNamespace("trims/items/" + type + "_trim_" + trimName);

                    addTrimModelToList(
                            itemModels,
                            caseModelLoc,
                            texture,
                            overlay,
                            trim,
                            trimTexture,
                            selectCases,
                            dyeable
                    );
                } else {
                    caseModelLoc = ResourceLocation.withDefaultNamespace("item/" + fileName + "_" + trimName + "_trim");
                    ItemModel.Unbaked dummyModel = ItemModelUtils.plainModel(caseModelLoc);
                    selectCases.add(ItemModelUtils.when(trim.materialKey(), dummyModel));
                }
            }

            ResourceLocation fallbackModelLoc = (baseName.equals("diamond") || baseName.equals("netherite") || baseName.equals("leather")
                    || baseName.equals("iron") || baseName.equals("golden") || baseName.equals("chainmail"))
                    ? ResourceLocation.withDefaultNamespace("item/" + fileName)
                    : JolCraft.location("item/" + fileName);

            ItemModel.Unbaked fallbackModel = dyeable
                    ? ItemModelUtils.tintedModel(fallbackModelLoc, new Dye(-6265536))
                    : ItemModelUtils.plainModel(fallbackModelLoc);

            Item armorItem = getItemFromBaseName(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), fallbackModel, selectCases)
            );
        }
    }

    private static void addTrimModelToList(
            ItemModelGenerators itemModels,
            ResourceLocation baseModelLocation,
            ResourceLocation textureLocation,
            ResourceLocation overlayTexture,
            ItemModelGenerators.TrimMaterialData trim,
            ResourceLocation trimTextureLocation,
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> list,
            boolean dyeable) {

        ItemModel.Unbaked bakedModel;
        if (dyeable) {
            itemModels.generateLayeredItem(baseModelLocation.withSuffix("_" + trim.name() + "_trim"), textureLocation, overlayTexture, trimTextureLocation);
            bakedModel = ItemModelUtils.tintedModel(baseModelLocation.withSuffix("_" + trim.name() + "_trim"), new Dye(-6265536)); // Example color
        } else {
            itemModels.generateLayeredItem(baseModelLocation.withSuffix("_" + trim.name() + "_trim"), textureLocation, trimTextureLocation);
            bakedModel = ItemModelUtils.plainModel(baseModelLocation.withSuffix("_" + trim.name() + "_trim"));
        }

        list.add(ItemModelUtils.when(trim.materialKey(), bakedModel));
    }

    private static Item getItemFromBaseName(String baseName, String type) {
        String itemName = baseName + "_" + type;

        ResourceLocation jolcraftLocation = JolCraft.location(itemName);
        Optional<Item> itemOptional = BuiltInRegistries.ITEM.getOptional(jolcraftLocation);

        if (itemOptional.isEmpty()) {
            ResourceLocation minecraftLocation = ResourceLocation.withDefaultNamespace(itemName);
            itemOptional = BuiltInRegistries.ITEM.getOptional(minecraftLocation);
        }

        return itemOptional.orElseThrow(() -> new IllegalStateException("Item not found: " + itemName));
    }

    private static VariantProperties.Rotation rotFromDegrees(int degrees) {
        return switch (degrees) {
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }

    private static int vanillaFacingY(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST  -> 90;
            case SOUTH -> 180;
            case WEST  -> 270;
            default    -> 0;
        };
    }


}
