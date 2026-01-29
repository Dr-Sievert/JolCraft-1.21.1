package net.sievert.jolcraft.datagen.model.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.sievert.jolcraft.datagen.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.sievert.jolcraft.datagen.model.util.AbstractModelProvider.generateFlatItem;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ArtisanModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_GEM_UNCUT = "material/gem/uncut";
    private static final String SUB_GEM_CUT = "material/gem/cut";
    private static final String SUB_GEM_DUST = "material/gem/dust";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        blocks.createTrivialBlock(JolCraftBlocks.LAPIDARY_BENCH.get(), TexturedModel.CUBE_TOP_BOTTOM);

        generateFlatItem(items, JolCraftItems.AEGISCORE.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.ASHFANG.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.DEEPMARROW.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.EARTHBLOOD.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.EMBERGLASS.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.FROSTVEIN.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.GRIMSTONE.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.IRONHEART.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.LUMIERE.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.MOONSHARD.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.RUSTAGATE.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.SKYBURROW.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.SUNGLEAM.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.VERDANITE.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);
        generateFlatItem(items, JolCraftItems.WOECRYSTAL.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_UNCUT);

        generateFlatItem(items, JolCraftItems.AEGISCORE_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.ASHFANG_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.DEEPMARROW_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.EARTHBLOOD_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.EMBERGLASS_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.FROSTVEIN_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.GRIMSTONE_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.IRONHEART_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.LUMIERE_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.MOONSHARD_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.RUSTAGATE_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.SKYBURROW_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.SUNGLEAM_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.VERDANITE_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);
        generateFlatItem(items, JolCraftItems.WOECRYSTAL_CUT.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_CUT);

        generateFlatItem(items, JolCraftItems.AEGISCORE_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.ASHFANG_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.DEEPMARROW_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.EARTHBLOOD_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.EMBERGLASS_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.FROSTVEIN_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.GRIMSTONE_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.IRONHEART_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.LUMIERE_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.MOONSHARD_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.RUSTAGATE_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.SKYBURROW_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.SUNGLEAM_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.VERDANITE_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
        generateFlatItem(items, JolCraftItems.WOECRYSTAL_DUST.get(), ModelTemplates.FLAT_ITEM, SUB_GEM_DUST);
    }
}
