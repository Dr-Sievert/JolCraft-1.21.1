package net.sievert.jolcraft.datagen.client.model;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.client.model.subprovider.*;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public final class JolCraftModelProvider extends AbstractModelProvider {

    public JolCraftModelProvider(PackOutput output) {
        super(output, JolCraft.MOD_ID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        runAll(blockModels, itemModels, subProviders());
    }

    private static @NotNull List<? extends ModelSubProvider> subProviders() {
        return List.of(
                new ArtisanModelSubProvider(),
                new BrewingModelSubProvider(),
                new CropModelSubProvider(),
                new DwarfModelSubProvider(),
                new EggModelSubProvider(),
                new MaterialModelSubProvider(),
                new MiscModelSubProvider(),
                new ScrapperModelSubProvider(),
                new ToolModelSubProvider(),
                new TrimModelSubProvider()
        );
    }

    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.listElements()
                .filter(holder -> {
                    var key = holder.getKey();
                    return key != null && key.location().getNamespace().equals(modId);
                })
                .filter(holder -> holder.value() != JolCraftBlocks.DEEPSLATE_MORTAR.get())
                .filter(holder -> holder.value() != JolCraftBlocks.STRONGBOX.get())
                .filter(holder -> holder.value() != JolCraftBlocks.STRONGBOX_DUMMY.get());
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> {
                    var key = holder.getKey();
                    return key != null && key.location().getNamespace().equals(modId);
                })
                .filter(holder -> holder.value() != JolCraftItems.STRONGBOX_ITEM.get())
                .filter(holder -> holder.value() != JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .filter(holder -> holder.value() != JolCraftItems.DEEPSLATE_COMPASS.get())
                .filter(holder -> holder.value() != JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())
                .filter(holder -> holder.value() != JolCraftItems.DWARVEN_BREW.get());
    }
}