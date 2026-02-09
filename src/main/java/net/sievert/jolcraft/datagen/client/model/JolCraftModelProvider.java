package net.sievert.jolcraft.datagen.client.model;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.client.model.subprovider.*;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public final class JolCraftModelProvider extends AbstractModelProvider {

    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public JolCraftModelProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, JolCraft.MOD_ID);
        this.lookupProvider = lookupProvider;
    }


    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        HolderLookup.Provider registries = lookupProvider.join();
        runAll(blockModels, itemModels, subProviders(registries));
    }

    private static @NotNull List<? extends ModelSubProvider> subProviders(HolderLookup.Provider registries) {
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
                new TrimModelSubProvider(registries)
        );
    }

    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return JolCraftBlocks.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(block -> block != JolCraftBlocks.DEEPSLATE_MORTAR.get())
                .filter(block -> block != JolCraftBlocks.STRONGBOX.get())
                .filter(block -> block != JolCraftBlocks.STRONGBOX_DUMMY.get())
                .map(Block::builtInRegistryHolder);
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return JolCraftItems.ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(item -> item != JolCraftItems.STRONGBOX_ITEM.get())
                .filter(item -> item != JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .filter(item -> item != JolCraftItems.DEEPSLATE_COMPASS.get())
                .filter(item -> item != JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())
                .filter(item -> item != JolCraftItems.DWARVEN_BREW.get())
                .map(Item::builtInRegistryHolder);
    }
}