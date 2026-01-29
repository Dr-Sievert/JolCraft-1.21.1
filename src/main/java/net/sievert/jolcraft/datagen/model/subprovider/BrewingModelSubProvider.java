package net.sievert.jolcraft.datagen.model.subprovider;

import com.google.gson.JsonObject;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.Block;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import static net.sievert.jolcraft.datagen.model.util.AbstractModelProvider.generateFlatItem;

public class BrewingModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_BREWING = "brewing";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        generateFlatItem(items, JolCraftItems.BARLEY_MALT.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "brewing");
        generateFlatItem(items, JolCraftItems.YEAST.get(), ModelTemplates.FLAT_ITEM, "brewing");
        generateFlatItem(items, JolCraftItems.GLASS_MUG.get(), ModelTemplates.FLAT_ITEM, "brewing");

        blocks.blockStateOutput.accept(new BlockStateGenerator() {
            @Override
            public JsonObject get() {
                JsonObject root = new JsonObject();
                JsonObject variants = new JsonObject();
                variants.add("level=1", modelObj("block/fermenting_cauldron_level1"));
                variants.add("level=2", modelObj("block/fermenting_cauldron_level2"));
                variants.add("level=3", modelObj("block/fermenting_cauldron_full"));
                root.add("variants", variants);
                return root;
            }

            @Override
            public @NotNull Block getBlock() {
                return JolCraftBlocks.FERMENTING_CAULDRON.get();
            }
        });
    }

    private static JsonObject modelObj(String path) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", JolCraft.MOD_ID + ":" + path);
        return obj;
    }

}
