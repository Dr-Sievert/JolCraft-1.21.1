package net.sievert.jolcraft.datagen.model.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
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
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TrimModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    // Armor piece suffixes
    private static final String HELMET = "helmet";
    private static final String CHESTPLATE = "chestplate";
    private static final String LEGGINGS = "leggings";
    private static final String BOOTS = "boots";
    private static final String[] ARMOR_TYPES = {HELMET, CHESTPLATE, LEGGINGS, BOOTS};

    // JolCraft armor bases
    private static final String DEEPSLATE = "deepslate";
    private static final String MITHRIL = "mithril";

    // Vanilla armor bases
    private static final String LEATHER = "leather";
    private static final String CHAINMAIL = "chainmail";
    private static final String IRON = "iron";
    private static final String GOLDEN = "golden";
    private static final String DIAMOND = "diamond";
    private static final String NETHERITE = "netherite";

    // Leather tint constant (match your previous helper)
    private static final Dye LEATHER_DYE_TINT = new Dye(-6265536);

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

        generateJolCraftArmorSet(items, DEEPSLATE, JolCraftEquipmentAssets.DEEPSLATE_KEY);
        generateJolCraftArmorSet(items, MITHRIL, JolCraftEquipmentAssets.MITHRIL_KEY);

        generateVanillaArmorSet(items, LEATHER, true);
        generateVanillaArmorSet(items, CHAINMAIL, false);
        generateVanillaArmorSet(items, IRON, false);
        generateVanillaArmorSet(items, GOLDEN, false);
        generateVanillaArmorSet(items, DIAMOND, false);
        generateVanillaArmorSet(items, NETHERITE, false);
    }

    // -------------------------------------------------------------------------
    // JolCraft armor generation
    // -------------------------------------------------------------------------

    private static void generateJolCraftArmorSet(
            @NotNull ItemModelGenerators itemModels,
            @NotNull String baseName,
            @NotNull ResourceKey<EquipmentAsset> equipmentAssetKey
    ) {
        List<ItemModelGenerators.TrimMaterialData> allTrims = new ArrayList<>(ItemModelGenerators.TRIM_MATERIAL_MODELS);
        allTrims.addAll(JOLCRAFT_TRIMS);

        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            ResourceLocation baseModelLoc = ResourceLocation.fromNamespaceAndPath(
                    equipmentAssetKey.location().getNamespace(),
                    "item/" + fileName
            );
            ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(
                    equipmentAssetKey.location().getNamespace(),
                    "item/" + fileName
            );

            // Variant model JSONs
            for (ItemModelGenerators.TrimMaterialData data : allTrims) {
                ResourceLocation variantModelLoc = baseModelLoc.withSuffix("_" + data.name() + "_trim");
                ResourceLocation trimTexture = trimTextureLocation(type, data, equipmentAssetKey);
                itemModels.generateLayeredItem(variantModelLoc, baseTexture, trimTexture);
            }

            // Base model JSON
            ModelTemplates.FLAT_ITEM.create(
                    baseModelLoc,
                    TextureMapping.layer0(baseTexture),
                    itemModels.modelOutput
            );

            ItemModel.Unbaked defaultModel = ItemModelUtils.plainModel(baseModelLoc);

            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList<>(allTrims.size());
            for (ItemModelGenerators.TrimMaterialData data : allTrims) {
                ResourceLocation variantModelLoc = baseModelLoc.withSuffix("_" + data.name() + "_trim");
                cases.add(ItemModelUtils.when(data.materialKey(), ItemModelUtils.plainModel(variantModelLoc)));
            }

            Item armorItem = armorItem(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), defaultModel, cases)
            );
        }
    }

    /**
     * IMPORTANT:
     * Your working setup expects ALL trim textures to be in the *minecraft* trims folder:
     *   minecraft:textures/trims/items/<piece>_trim_<name>.png
     * That applies to BOTH vanilla trims and your gem trims.
     */
    private static @NotNull ResourceLocation trimTextureLocation(
            @NotNull String armorPieceType,
            @NotNull ItemModelGenerators.TrimMaterialData data,
            @NotNull ResourceKey<EquipmentAsset> equipmentAssetKey
    ) {
        String trimTextureName = data.name();

        // Apply per-armor override names for JolCraft armor materials (deepslate_darker/mithril_darker)
        String overrideName = data.overrideArmorMaterials().get(equipmentAssetKey);
        if (overrideName != null && !overrideName.isEmpty()) {
            trimTextureName = overrideName;
        }

        return ResourceLocation.withDefaultNamespace(
                "trims/items/" + armorPieceType + "_trim_" + trimTextureName
        );
    }

    // -------------------------------------------------------------------------
    // Vanilla armor generation (restore old working behavior)
    // -------------------------------------------------------------------------

    private static void generateVanillaArmorSet(
            @NotNull ItemModelGenerators itemModels,
            @NotNull String baseName,
            boolean dyeable
    ) {
        List<ItemModelGenerators.TrimMaterialData> allTrims = new ArrayList<>(ItemModelGenerators.TRIM_MATERIAL_MODELS);
        allTrims.addAll(JOLCRAFT_TRIMS);

        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            ResourceLocation vanillaBaseModelLoc = ResourceLocation.withDefaultNamespace("item/" + fileName);
            ResourceLocation vanillaBaseTexture = ResourceLocation.withDefaultNamespace("item/" + fileName);
            ResourceLocation vanillaOverlayTexture = ResourceLocation.withDefaultNamespace("item/" + fileName + "_overlay");

            // 1) Generate missing JolCraft trim variant models in minecraft namespace (same place vanilla expects)
            for (ItemModelGenerators.TrimMaterialData data : JOLCRAFT_TRIMS) {
                ResourceLocation variantModelLoc = ResourceLocation.withDefaultNamespace(
                        "item/" + fileName + "_" + data.name() + "_trim"
                );

                // Critical: trim textures are ALSO minecraft namespace in your working setup
                ResourceLocation trimTexture = ResourceLocation.withDefaultNamespace(
                        "trims/items/" + type + "_trim_" + data.name()
                );

                if (dyeable) {
                    itemModels.generateLayeredItem(variantModelLoc, vanillaBaseTexture, vanillaOverlayTexture, trimTexture);
                } else {
                    itemModels.generateLayeredItem(variantModelLoc, vanillaBaseTexture, trimTexture);
                }
            }

            // 2) select() cases (vanilla trim models already exist; JolCraft ones we generated above)
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList<>(allTrims.size());
            for (ItemModelGenerators.TrimMaterialData data : allTrims) {
                ResourceLocation variantModelLoc = ResourceLocation.withDefaultNamespace(
                        "item/" + fileName + "_" + data.name() + "_trim"
                );

                ItemModel.Unbaked baked = dyeable
                        ? ItemModelUtils.tintedModel(variantModelLoc, LEATHER_DYE_TINT)
                        : ItemModelUtils.plainModel(variantModelLoc);

                cases.add(ItemModelUtils.when(data.materialKey(), baked));
            }

            // 3) Default/fallback = vanilla base (tinted for leather)
            ItemModel.Unbaked defaultModel = dyeable
                    ? ItemModelUtils.tintedModel(vanillaBaseModelLoc, LEATHER_DYE_TINT)
                    : ItemModelUtils.plainModel(vanillaBaseModelLoc);

            Item armorItem = vanillaArmorItem(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), defaultModel, cases)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Item mapping helpers
    // -------------------------------------------------------------------------

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
            default -> throw new IllegalStateException("Unknown JolCraft armor base: " + baseName);
        };
    }

    private static @NotNull Item vanillaArmorItem(@NotNull String baseName, @NotNull String type) {
        String itemName = baseName + "_" + type;
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.withDefaultNamespace(itemName));
        return item.orElseThrow(() -> new IllegalStateException("Vanilla armor item not found: " + itemName));
    }
}