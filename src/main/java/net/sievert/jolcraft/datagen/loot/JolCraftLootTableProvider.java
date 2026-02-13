package net.sievert.jolcraft.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.sievert.jolcraft.datagen.loot.subprovider.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * JolCraft loot table provider.
 */
public final class JolCraftLootTableProvider extends LootTableProvider {

    public JolCraftLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(
                output,
                Set.of(),
                List.of(
                        new SubProviderEntry(JolCraftArchaeologyLootTableProvider::new, LootContextParamSets.ARCHAEOLOGY),
                        new SubProviderEntry(JolCraftBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                        new SubProviderEntry(JolCraftChestLootTableProvider::new, LootContextParamSets.CHEST),
                        new SubProviderEntry(JolCraftEntityLootTableProvider::new, LootContextParamSets.ENTITY),
                        new SubProviderEntry(JolCraftStrongboxLootTableProvider::new, LootContextParamSets.CHEST)
                ),
                registries
        );
    }

    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new JolCraftLootTableProvider(output, registries);
    }

    @Override
    protected void validate(
            @NotNull WritableRegistry<LootTable> registry,
            @NotNull ValidationContext context,
            ProblemReporter.@NotNull Collector problems
    ) {
        super.validate(registry, context, problems);
    }
}
