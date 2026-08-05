package net.sievert.jolcraft.datagen.client.model.subprovider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemPropertyIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelBuilder;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelSubProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.client.property.custom.CoinPouchAmount;
import net.sievert.jolcraft.world.item.client.property.JolCraftItemProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public record MiscModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String SUB_COIN = JolCraftDictionary.COIN;
    private static final String SUB_COMPASS = JolCraftDictionary.COMPASS;
    private static final String SUB_BOUNTY = JolCraftDictionary.BOUNTY;

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.MISC;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.flatItem(JolCraftItems.DEV_KEY.get());

        builder.flatItem(JolCraftItems.GOLD_COIN.get(), SUB_COIN);
        generateCoinPouchModel(builder);

        builder.handheldItem(JolCraftItems.LOCKPICK.get());

        builder.flatItem(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get());
        builder.flatItem(JolCraftItems.INVERIX.get());

        builder.flatItem(JolCraftItems.BOUNTY.get(), SUB_BOUNTY);
        builder.flatItem(JolCraftItems.BOUNTY_CRATE.get(), SUB_BOUNTY);

        createHearth(builder, JolCraftBlocks.HEARTH.get());
        createManagedLight(builder, JolCraftBlocks.MANAGED_LIGHT.get());

        builder.manualBlockState(JolCraftBlocks.DEEPSLATE_MORTAR.get());
        builder.manualBlockState(JolCraftBlocks.STRONGBOX.get());

        builder.flatItemWithOverlay(
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                SUB_COMPASS
        );

        builder.handheldItemWithOverlay(
                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(),
                SUB_COMPASS
        );

        builder.compassItem(
                JolCraftItems.DEEPSLATE_COMPASS.get(),
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(),
                JolCraft.location(JolCraftItemPropertyIds.DEEPSLATE_COMPASS_ANGLE),
                SUB_COMPASS
        );

        builder.instrumentItem(
                JolCraftItems.WAR_HORN.get()
        );
    }

    private static void generateCoinPouchModel(@NotNull JolCraftModelBuilder builder) {
        ResourceLocation empty = JolCraft.location("item/coin/coin_pouch");
        ResourceLocation medium = JolCraft.location("item/coin/coin_pouch_medium");
        ResourceLocation full = JolCraft.location("item/coin/coin_pouch_full");

        ModelTemplates.FLAT_ITEM.create(empty, TextureMapping.layer0(empty), builder::addModel);
        ModelTemplates.FLAT_ITEM.create(medium, TextureMapping.layer0(medium), builder::addModel);
        ModelTemplates.FLAT_ITEM.create(full, TextureMapping.layer0(full), builder::addModel);

        new CoinPouchAmount().bootstrap();

        JsonArray overrides = new JsonArray();
        overrides.add(coinPouchOverride(JolCraftDictionary.EMPTY, empty));
        overrides.add(coinPouchOverride(JolCraftDictionary.MEDIUM, medium));
        overrides.add(coinPouchOverride(JolCraftDictionary.FULL, full));

        builder.addModel(ModelLocationUtils.getModelLocation(JolCraftItems.COIN_POUCH.get()), () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");

            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", empty.toString());
            json.add("textures", textures);

            json.add("overrides", overrides);
            return json;
        });
    }

    private static @NotNull JsonObject coinPouchOverride(
            @NotNull String valueKey,
            @NotNull ResourceLocation modelLocation
    ) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty(
                CoinPouchAmount.KEY.toString(),
                JolCraftItemProperties.value(CoinPouchAmount.KEY, valueKey)
        );

        JsonObject override = new JsonObject();
        override.add("predicate", predicate);
        override.addProperty("model", modelLocation.toString());
        return override;
    }

    public static void createHearth(@NotNull JolCraftModelBuilder builder, @NotNull Block hearthBlock) {
        TextureMapping baseMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(hearthBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(hearthBlock, "_front"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(hearthBlock, "_front"));

        ResourceLocation hearthModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(
                hearthBlock,
                baseMapping,
                builder::addModel
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
                builder::addModel
        );

        ResourceLocation chimney = JolCraft.location("block/hearth_chimney");

        builder.addBlockState(
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
                                            VariantProperties.Rotation yRot = switch (facing) {
                                                case EAST -> VariantProperties.Rotation.R90;
                                                case SOUTH -> VariantProperties.Rotation.R180;
                                                case WEST -> VariantProperties.Rotation.R270;
                                                default -> VariantProperties.Rotation.R0;
                                            };

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

        builder.delegateItemToBlockModel(hearthBlock);
    }

    public static void createManagedLight(@NotNull JolCraftModelBuilder builder, @NotNull Block block) {
        PropertyDispatch.C1<Integer> dispatch = PropertyDispatch.property(BlockStateProperties.LEVEL);

        for (int i = 0; i <= 15; i++) {
            String suffix = String.format(Locale.ROOT, "_%02d", i);
            ResourceLocation particleTex = TextureMapping.getItemTexture(Items.LIGHT, suffix);

            dispatch.select(
                    i,
                    Variant.variant().with(
                            VariantProperties.MODEL,
                            ModelTemplates.PARTICLE_ONLY.createWithSuffix(
                                    block,
                                    suffix,
                                    TextureMapping.particle(particleTex),
                                    builder::addModel
                            )
                    )
            );
        }

        builder.addBlockState(
                MultiVariantGenerator.multiVariant(block).with(dispatch)
        );
    }
}