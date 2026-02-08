package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public final class MaterialModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_PAPER      = "material/paper";
    private static final String SUB_ENTITY     = "material/entity";
    private static final String SUB_GEODE      = "material/geode";
    private static final String SUB_DEEPSLATE  = "material/deepslate";
    private static final String SUB_MITHRIL    = "material/mithril";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        /* ---------------------------- */
        /* Paper / writing materials    */
        /* ---------------------------- */

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.PARCHMENT.get(), ModelTemplates.FLAT_ITEM, SUB_PAPER);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.QUILL_EMPTY.get(), ModelTemplates.FLAT_ITEM, SUB_PAPER);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.QUILL_SMALL.get(), ModelTemplates.FLAT_ITEM, SUB_PAPER);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.QUILL_HALF.get(), ModelTemplates.FLAT_ITEM, SUB_PAPER);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.QUILL_FULL.get(), ModelTemplates.FLAT_ITEM, SUB_PAPER);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.LEGENDARY_PAGE.get(), ModelTemplates.FLAT_ITEM, SUB_PAPER);

        /* ---------------------------- */
        /* Entity-derived materials     */
        /* ---------------------------- */

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.MUFFHORN_MILK_BUCKET.get(), ModelTemplates.FLAT_HANDHELD_ITEM, SUB_ENTITY);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.MUFFHORN_FUR.get(), ModelTemplates.FLAT_HANDHELD_ITEM, SUB_ENTITY);

        AbstractModelProvider.createTrivialCube(blocks, JolCraftBlocks.MUFFHORN_FUR_BLOCK.get(), SUB_ENTITY);

        /* ---------------------------- */
        /* Geodes                       */
        /* ---------------------------- */

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.GEODE_SMALL.get(), ModelTemplates.FLAT_ITEM, SUB_GEODE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.GEODE_MEDIUM.get(), ModelTemplates.FLAT_ITEM, SUB_GEODE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.GEODE_LARGE.get(), ModelTemplates.FLAT_ITEM, SUB_GEODE);

        AbstractModelProvider.createTrivialCube(blocks, JolCraftBlocks.GEODE_BLOCK.get(), SUB_GEODE);

        /* ---------------------------- */
        /* Deepslate materials          */
        /* ---------------------------- */

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DEEPSLATE_PLATE.get(), ModelTemplates.FLAT_ITEM, SUB_DEEPSLATE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DEEPSLATE_ROD.get(), ModelTemplates.FLAT_ITEM, SUB_DEEPSLATE);

        AbstractModelProvider.createTrivialCube(blocks, JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get(), SUB_DEEPSLATE);

        /* ---------------------------- */
        /* Mithril materials            */
        /* ---------------------------- */

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.IMPURE_MITHRIL.get(), ModelTemplates.FLAT_ITEM, SUB_MITHRIL);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.PURE_MITHRIL.get(), ModelTemplates.FLAT_ITEM, SUB_MITHRIL);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.MITHRIL_INGOT.get(), ModelTemplates.FLAT_ITEM, SUB_MITHRIL);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.MITHRIL_NUGGET.get(), ModelTemplates.FLAT_ITEM, SUB_MITHRIL);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.MITHRIL_CHAINWEAVE.get(), ModelTemplates.FLAT_ITEM, SUB_MITHRIL);

        blocks.createRotatedPillarWithHorizontalVariant(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(), TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        AbstractModelProvider.createTrivialCube(blocks, JolCraftBlocks.PURE_MITHRIL_BLOCK.get(), SUB_MITHRIL);
        AbstractModelProvider.createTrivialCube(blocks, JolCraftBlocks.MITHRIL_BLOCK.get(), SUB_MITHRIL);
    }
}