package net.sievert.jolcraft.datagen.model.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.client.coin.CoinPouchAmountProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MiscModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_COIN = "coin";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        /* ---------------------------- */
        /* Core / misc items            */
        /* ---------------------------- */

        items.generateFlatItem(JolCraftItems.DEV_KEY.get(), ModelTemplates.FLAT_ITEM);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.GOLD_COIN.get(), ModelTemplates.FLAT_ITEM, SUB_COIN);
        generateCoinPouchModel(items);

        items.generateFlatItem(JolCraftItems.LOCKPICK.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        /* ---------------------------- */
        /* Alchemy                      */
        /* ---------------------------- */

        items.generateFlatItem(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.DEEPSLATE_PESTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        items.generateFlatItem(JolCraftItems.MITHRIL_PESTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        items.generateFlatItem(JolCraftItems.INVERIX.get(), ModelTemplates.FLAT_ITEM);

        /* ---------------------------- */
        /* Bounty                        */
        /* ---------------------------- */

        items.generateFlatItem(JolCraftItems.BOUNTY.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.BOUNTY_CRATE.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.RESTOCK_CRATE.get(), ModelTemplates.FLAT_ITEM);
        items.generateFlatItem(JolCraftItems.REROLL_CRATE.get(), ModelTemplates.FLAT_ITEM);

        /* ---------------------------- */
        /* Blocks                        */
        /* ---------------------------- */

        createHearth(blocks, JolCraftBlocks.HEARTH.get());
        createManagedLight(blocks, JolCraftBlocks.MANAGED_LIGHT.get());
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

    public static void createHearth(BlockModelGenerators blockModels, Block hearthBlock) {
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
                                            VariantProperties.Rotation yRot = AbstractModelProvider.rotFromDegrees(AbstractModelProvider.vanillaFacingY(facing));

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

    public static void createManagedLight(BlockModelGenerators blockModels, Block block) {
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
                                    blockModels.modelOutput
                            )
                    )
            );
        }

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(block).with(dispatch)
        );
    }
}