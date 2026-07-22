package net.sievert.jolcraft.datagen.client.model.subprovider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelBuilder;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.equipment.JolCraftArmorItemSet;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.item.registry.JolCraftArmorItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public record TrimModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private record VanillaArmorSpec(
            @NotNull String baseName,
            @NotNull Holder<ArmorMaterial> material,
            boolean dyeable
    ) {
    }

    private static final Comparator<ItemModelGenerators.TrimModelData> TRIM_ORDER =
            Comparator.comparing(ItemModelGenerators.TrimModelData::itemModelIndex);

    private static final List<ItemModelGenerators.TrimModelData> CUSTOM_TRIMS = buildCustomTrims();
    private static final List<ItemModelGenerators.TrimModelData> ALL_TRIMS = buildAllTrims();
    private static final List<VanillaArmorSpec> VANILLA_ARMOR_SETS = buildVanillaArmorSets();

    private static final Map<JolCraftMaterials.Material, JolCraftArmorItemSet> ARMOR_BY_MATERIAL = buildArmorByMaterial();

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.TRIM;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.flatItem(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get());

        for (Map.Entry<JolCraftMaterials.Material, JolCraftArmorItemSet> entry : ARMOR_BY_MATERIAL.entrySet()) {
            generateJolCraftArmorSet(builder, entry.getKey(), entry.getValue());
        }

        for (VanillaArmorSpec spec : VANILLA_ARMOR_SETS) {
            generateVanillaArmorSet(builder, spec);
        }
    }

    private void generateJolCraftArmorSet(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftMaterials.Material material,
            @NotNull JolCraftArmorItemSet set
    ) {
        Holder<ArmorMaterial> armorMaterial = JolCraftArmorMaterials.armorMaterial(material);

        for (ArmorItem.Type type : JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES) {
            Item armorItem = set.get(type).get();

            ResourceLocation baseModelLoc = ModelLocationUtils.getModelLocation(armorItem);
            ResourceLocation baseTexture = TextureMapping.getItemTexture(armorItem);

            for (ItemModelGenerators.TrimModelData data : ALL_TRIMS) {
                String trimName = data.name(armorMaterial);
                ResourceLocation variantModelLoc = baseModelLoc.withSuffix("_" + trimName + "_" + JolCraftDictionary.TRIM);
                ResourceLocation trimTexture = trimTextureLocation(type, trimName);

                ModelTemplates.TWO_LAYERED_ITEM.create(
                        variantModelLoc,
                        TextureMapping.layered(baseTexture, trimTexture),
                        builder::addModel
                );
            }

            ModelTemplates.FLAT_ITEM.create(
                    baseModelLoc,
                    TextureMapping.layer0(baseTexture),
                    builder::addModel,
                    (location, textures) -> generateBaseArmorTrimTemplate(location, textures, armorMaterial)
            );
        }
    }

    private void generateVanillaArmorSet(
            @NotNull JolCraftModelBuilder builder,
            @NotNull VanillaArmorSpec spec
    ) {
        for (ArmorItem.Type type : JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES) {
            Item armorItem = vanillaArmorItem(spec.baseName(), type);

            ResourceLocation baseModelLoc = ModelLocationUtils.getModelLocation(armorItem);
            ResourceLocation baseTexture = TextureMapping.getItemTexture(armorItem);
            ResourceLocation overlayTexture = TextureMapping.getItemTexture(armorItem, "_" + JolCraftDictionary.OVERLAY);

            for (ItemModelGenerators.TrimModelData data : CUSTOM_TRIMS) {
                String trimName = data.name(spec.material());
                ResourceLocation variantModelLoc = baseModelLoc.withSuffix("_" + trimName + "_" + JolCraftDictionary.TRIM);
                ResourceLocation trimTexture = trimTextureLocation(type, trimName);

                if (spec.dyeable()) {
                    ModelTemplates.THREE_LAYERED_ITEM.create(
                            variantModelLoc,
                            TextureMapping.layered(baseTexture, overlayTexture, trimTexture),
                            builder::addModel
                    );
                } else {
                    ModelTemplates.TWO_LAYERED_ITEM.create(
                            variantModelLoc,
                            TextureMapping.layered(baseTexture, trimTexture),
                            builder::addModel
                    );
                }
            }

            if (spec.dyeable()) {
                ModelTemplates.TWO_LAYERED_ITEM.create(
                        baseModelLoc,
                        TextureMapping.layered(baseTexture, overlayTexture),
                        builder::addModel,
                        (location, textures) -> generateBaseArmorTrimTemplate(location, textures, spec.material())
                );
            } else {
                ModelTemplates.FLAT_ITEM.create(
                        baseModelLoc,
                        TextureMapping.layer0(baseTexture),
                        builder::addModel,
                        (location, textures) -> generateBaseArmorTrimTemplate(location, textures, spec.material())
                );
            }
        }
    }

    private @NotNull JsonObject generateBaseArmorTrimTemplate(
            @NotNull ResourceLocation modelLocation,
            @NotNull Map<TextureSlot, ResourceLocation> textures,
            @NotNull Holder<ArmorMaterial> armorMaterial
    ) {
        JsonObject json = textures.containsKey(TextureSlot.LAYER1)
                ? ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(modelLocation, textures)
                : ModelTemplates.FLAT_ITEM.createBaseTemplate(modelLocation, textures);

        JsonArray overrides = new JsonArray();

        for (ItemModelGenerators.TrimModelData data : ALL_TRIMS) {
            JsonObject predicate = new JsonObject();
            predicate.addProperty(
                    ItemModelGenerators.TRIM_TYPE_PREDICATE_ID.getPath(),
                    data.itemModelIndex()
            );

            JsonObject override = new JsonObject();
            override.add("predicate", predicate);
            override.addProperty(
                    "model",
                    modelLocation.withSuffix("_" + data.name(armorMaterial) + "_" + JolCraftDictionary.TRIM).toString()
            );

            overrides.add(override);
        }

        json.add("overrides", overrides);
        return json;
    }

    private static @NotNull ResourceLocation trimTextureLocation(
            @NotNull ArmorItem.Type type,
            @NotNull String trimName
    ) {
        return ResourceLocation.withDefaultNamespace(
                JolCraftStrings.slashed(
                        JolCraftStrings.plural(JolCraftDictionary.TRIM),
                        JolCraftStrings.plural(JolCraftDictionary.ITEM)
                ) + "/" + type.getName() + "_" + JolCraftDictionary.TRIM + "_" + trimName
        );
    }

    private @NotNull Item vanillaArmorItem(
            @NotNull String baseName,
            @NotNull ArmorItem.Type type
    ) {
        ResourceLocation id = ResourceLocation.withDefaultNamespace(baseName + "_" + type.getName());
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);

        return parent.lookups().items()
                .get(key)
                .orElseThrow(() -> new IllegalStateException("Vanilla armor item not found: " + id))
                .value();
    }

    private static @NotNull Map<JolCraftMaterials.Material, JolCraftArmorItemSet> buildArmorByMaterial() {
        EnumMap<JolCraftMaterials.Material, JolCraftArmorItemSet> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        out.put(JolCraftMaterials.Material.DEEPSLATE, JolCraftArmorItems.DEEPSLATE);
        out.put(JolCraftMaterials.Material.MITHRIL, JolCraftArmorItems.MITHRIL);

        return Map.copyOf(out);
    }

    private static @NotNull List<ItemModelGenerators.TrimModelData> buildCustomTrims() {
        List<ItemModelGenerators.TrimModelData> out = new ArrayList<>();

        Map<JolCraftMaterials.Material, JolCraftArmorItemSet> armorByMaterial = buildArmorByMaterial();
        for (JolCraftMaterials.Material material : armorByMaterial.keySet()) {
            Holder<ArmorMaterial> armorMaterial = JolCraftArmorMaterials.armorMaterial(material);
            String darkerTrimName = material.darkerTrimName();

            out.add(new ItemModelGenerators.TrimModelData(
                    material.getId(),
                    JolCraftTrimMaterials.vanillaItemModelIndex(material),
                    Map.of(armorMaterial, darkerTrimName)
            ));
        }

        for (JolCraftTrimMaterials.Attribute attribute : JolCraftTrimMaterials.Attribute.values()) {
            out.add(new ItemModelGenerators.TrimModelData(
                    attribute.getId(),
                    attribute.itemModelIndex(),
                    Map.of()
            ));
        }

        out.sort(TRIM_ORDER);
        return List.copyOf(out);
    }

    private static @NotNull List<ItemModelGenerators.TrimModelData> buildAllTrims() {
        List<ItemModelGenerators.TrimModelData> out = new ArrayList<>(ItemModelGenerators.GENERATED_TRIM_MODELS);
        out.addAll(CUSTOM_TRIMS);
        out.sort(TRIM_ORDER);
        return List.copyOf(out);
    }

    private static @NotNull List<VanillaArmorSpec> buildVanillaArmorSets() {
        return List.of(
                new VanillaArmorSpec(JolCraftDictionary.LEATHER, ArmorMaterials.LEATHER, true),
                new VanillaArmorSpec(JolCraftDictionary.CHAINMAIL, ArmorMaterials.CHAIN, false),
                new VanillaArmorSpec(JolCraftDictionary.IRON, ArmorMaterials.IRON, false),
                new VanillaArmorSpec(JolCraftDictionary.GOLD + "en", ArmorMaterials.GOLD, false),
                new VanillaArmorSpec(JolCraftDictionary.DIAMOND, ArmorMaterials.DIAMOND, false),
                new VanillaArmorSpec(JolCraftDictionary.NETHERITE, ArmorMaterials.NETHERITE, false)
        );
    }
}