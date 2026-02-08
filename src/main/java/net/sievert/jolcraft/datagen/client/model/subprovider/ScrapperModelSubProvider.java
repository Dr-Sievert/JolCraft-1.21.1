package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class ScrapperModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_SCRAP = "material/scrap";
    private static final String SUB_SALVAGE = "material/salvage";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.SCRAP.get(), ModelTemplates.FLAT_ITEM, SUB_SCRAP);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.SCRAP_HEAP.get(), ModelTemplates.FLAT_ITEM, SUB_SCRAP);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_AMULET.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_BELT.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_COINS.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DEEPSLATE_MUG.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_TABLET.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.EXPIRED_POTION.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.INGOT_MOULD.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.OLD_FABRIC.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.RUSTY_TONGS.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.MITHRIL_SALVAGE.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_MITHRIL_PLATE.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_MITHRIL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM, SUB_SALVAGE);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), ModelTemplates.FLAT_ITEM, SUB_SALVAGE);
    }
}