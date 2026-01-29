package net.sievert.jolcraft.datagen.model.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.datagen.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.armor.JolCraftEquipmentAssets;
import net.sievert.jolcraft.world.item.trim.JolCraftTrimMaterials;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TrimModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    // Armor piece suffixes
    private static final String HELMET = "helmet";
    private static final String CHESTPLATE = "chestplate";
    private static final String LEGGINGS = "leggings";
    private static final String BOOTS = "boots";
    private static final String[] ARMOR_TYPES = {HELMET, CHESTPLATE, LEGGINGS, BOOTS};

    // Bases
    private static final String DEEPSLATE = "deepslate";
    private static final String MITHRIL = "mithril";

    // JolCraft trims
    private static final List<ItemModelGenerators.TrimMaterialData> JOLCRAFT_TRIMS = List.of(
            // Material-based (override = darker for same-asset armor)
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

            // Gems (no override)
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

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        items.generateFlatItem(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), ModelTemplates.FLAT_ITEM);
        generateArmorSetWithTrims(items, DEEPSLATE, JolCraftEquipmentAssets.DEEPSLATE_KEY);
        generateArmorSetWithTrims(items, MITHRIL, JolCraftEquipmentAssets.MITHRIL_KEY);
    }

    private static void generateArmorSetWithTrims(
            @NotNull ItemModelGenerators itemModels,
            @NotNull String baseName,
            @NotNull ResourceKey<EquipmentAsset> equipmentAssetKey
    ) {
        List<ItemModelGenerators.TrimMaterialData> allTrims = new ArrayList<>(ItemModelGenerators.TRIM_MATERIAL_MODELS);
        allTrims.addAll(JOLCRAFT_TRIMS);

        generateTrimmableArmor(itemModels, baseName, equipmentAssetKey, allTrims);
    }

    private static void generateTrimmableArmor(
            @NotNull ItemModelGenerators itemModels,
            @NotNull String baseName,
            @NotNull ResourceKey<EquipmentAsset> equipmentAssetKey,
            @NotNull List<ItemModelGenerators.TrimMaterialData> trimMaterialList
    ) {
        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            ResourceLocation baseModelLocation = ResourceLocation.fromNamespaceAndPath(
                    equipmentAssetKey.location().getNamespace(),
                    "item/" + fileName
            );
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(
                    equipmentAssetKey.location().getNamespace(),
                    "item/" + fileName
            );

            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases =
                    new ArrayList<>(trimMaterialList.size());

            for (ItemModelGenerators.TrimMaterialData data : trimMaterialList) {
                ResourceLocation trimModelLoc = baseModelLocation.withSuffix("_" + data.name() + "_trim");

                String trimTextureName = data.name();

                String overrideName = data.overrideArmorMaterials().get(equipmentAssetKey);
                if (overrideName != null && !overrideName.isEmpty()) {
                    trimTextureName = overrideName;
                }

                ResourceLocation trimTextureLocation = ResourceLocation.withDefaultNamespace(
                        "trims/items/" + type + "_trim_" + trimTextureName
                );

                itemModels.generateLayeredItem(trimModelLoc, textureLocation, trimTextureLocation);

                ItemModel.Unbaked bakedModel = ItemModelUtils.plainModel(trimModelLoc);
                cases.add(ItemModelUtils.when(data.materialKey(), bakedModel));
            }

            ModelTemplates.FLAT_ITEM.create(
                    baseModelLocation,
                    TextureMapping.layer0(textureLocation),
                    itemModels.modelOutput
            );
            ItemModel.Unbaked defaultModel = ItemModelUtils.plainModel(baseModelLocation);

            Item armorItem = armorItem(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), defaultModel, cases)
            );
        }
    }

    private static @NotNull Item armorItem(@NotNull String baseName, @NotNull String type) {
        return switch (baseName) {
            case DEEPSLATE -> switch (type) {
                case HELMET -> JolCraftItems.DEEPSLATE_HELMET.get();
                case CHESTPLATE -> JolCraftItems.DEEPSLATE_CHESTPLATE.get();
                case LEGGINGS -> JolCraftItems.DEEPSLATE_LEGGINGS.get();
                case BOOTS -> JolCraftItems.DEEPSLATE_BOOTS.get();
                default -> throw new IllegalStateException("Unknown armor type: " + type);
            };
            case MITHRIL -> switch (type) {
                case HELMET -> JolCraftItems.MITHRIL_HELMET.get();
                case CHESTPLATE -> JolCraftItems.MITHRIL_CHESTPLATE.get();
                case LEGGINGS -> JolCraftItems.MITHRIL_LEGGINGS.get();
                case BOOTS -> JolCraftItems.MITHRIL_BOOTS.get();
                default -> throw new IllegalStateException("Unknown armor type: " + type);
            };
            default -> throw new IllegalStateException("Unknown armor base: " + baseName);
        };
    }
}