package net.sievert.jolcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class JolCraftDataMapProvider extends DataMapProvider {
    protected JolCraftDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(JolCraftItems.BARLEY_SEEDS.getId(), new Compostable(0.25f), false)
                .add(JolCraftItems.BARLEY.getId(), new Compostable(0.45f), false)
                .add(JolCraftItems.ASGARNIAN_SEEDS.getId(), new Compostable(0.25f), false)
                .add(JolCraftItems.ASGARNIAN_HOPS.getId(), new Compostable(0.45f), false)
                .add(JolCraftItems.DUSKHOLD_SEEDS.getId(), new Compostable(0.25f), false)
                .add(JolCraftItems.DUSKHOLD_HOPS.getId(), new Compostable(0.45f), false)
                .add(JolCraftItems.KRANDONIAN_SEEDS.getId(), new Compostable(0.25f), false)
                .add(JolCraftItems.KRANDONIAN_HOPS.getId(), new Compostable(0.45f), false)
                .add(JolCraftItems.YANILLIAN_SEEDS.getId(), new Compostable(0.25f), false)
                .add(JolCraftItems.YANILLIAN_HOPS.getId(), new Compostable(0.45f), false)
                .add(JolCraftBlocks.DUSKCAP.getId(), new Compostable(0.65f), false)
                .add(JolCraftBlocks.FESTERLING.getId(), new Compostable(0.65f), false);

    }
}