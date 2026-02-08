package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import static net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider.generateFlatItem;

@OnlyIn(Dist.CLIENT)
public class BrewingModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_BREWING = "brewing";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        generateFlatItem(items, JolCraftItems.BARLEY_MALT.get(), ModelTemplates.FLAT_HANDHELD_ITEM, SUB_BREWING);
        generateFlatItem(items, JolCraftItems.YEAST.get(), ModelTemplates.FLAT_ITEM, SUB_BREWING);
        generateFlatItem(items, JolCraftItems.GLASS_MUG.get(), ModelTemplates.FLAT_ITEM, SUB_BREWING);
        fermentingCauldron(blocks);
    }

    private static void fermentingCauldron(BlockModelGenerators blocks) {
        var cauldronModel = ResourceLocation.withDefaultNamespace("block/cauldron");
        blocks.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.FERMENTING_CAULDRON.get())
                        .with(PropertyDispatch.property(LayeredCauldronBlock.LEVEL)
                                .select(1, Variant.variant().with(VariantProperties.MODEL, cauldronModel))
                                .select(2, Variant.variant().with(VariantProperties.MODEL, cauldronModel))
                                .select(3, Variant.variant().with(VariantProperties.MODEL, cauldronModel))
                        )
        );
    }
}
