package net.sievert.jolcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.advancement.JolCraftAdvancementProvider;
import net.sievert.jolcraft.datagen.biome.JolCraftBiomeTagProvider;
import net.sievert.jolcraft.datagen.block.JolCraftBlockLootTableProvider;
import net.sievert.jolcraft.datagen.block.JolCraftBlockTagProvider;
import net.sievert.jolcraft.datagen.config.JolCraftConfigProvider;
import net.sievert.jolcraft.datagen.item.JolCraftItemTagProvider;
import net.sievert.jolcraft.datagen.loot.JolCraftEntityLootTableProvider;
import net.sievert.jolcraft.datagen.loot.JolCraftGlobalLootModifierProvider;
import net.sievert.jolcraft.datagen.recipe.JolCraftRecipeProvider;
import net.sievert.jolcraft.datagen.structure.JolCraftStructureTagProvider;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class JolCraftServerDataGenerator {

    private JolCraftServerDataGenerator() {}

    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {

        JolCraftLogs.info(
                JolCraftLogTags.DATAGEN,
                "GatherDataEvent.Server: registering server providers."
        );

        addServerProviders(event.getGenerator(), event.getLookupProvider());

        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "Server providers: {}, {}, {}, {}, {}, {}, {}, {}",
                JolCraftBlockTagProvider.class.getSimpleName(),
                JolCraftDataMapProvider.class.getSimpleName(),
                JolCraftRecipeProvider.Runner.class.getSimpleName(),
                JolCraftGlobalLootModifierProvider.class.getSimpleName(),
                JolCraftItemTagProvider.class.getSimpleName(),
                JolCraftBiomeTagProvider.class.getSimpleName(),
                JolCraftStructureTagProvider.class.getSimpleName(),
                JolCraftDatapackProvider.class.getSimpleName(),
                JolCraftConfigProvider.class.getSimpleName()
        );
    }

    public static void addServerProviders(
            DataGenerator generator,
            CompletableFuture<HolderLookup.Provider> lookup
    ) {
        PackOutput packOutput = generator.getPackOutput();

        BlockTagsProvider blockTagsProvider = new JolCraftBlockTagProvider(packOutput, lookup);
        generator.addProvider(true, blockTagsProvider);

        generator.addProvider(true, new JolCraftDataMapProvider(packOutput, lookup));
        generator.addProvider(true, new JolCraftRecipeProvider.Runner(packOutput, lookup));

        generator.addProvider(true, new LootTableProvider(
                packOutput,
                Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(JolCraftBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(JolCraftEntityLootTableProvider::new, LootContextParamSets.ENTITY)
                ),
                lookup
        ));

        generator.addProvider(true, new JolCraftGlobalLootModifierProvider(packOutput, lookup));
        generator.addProvider(true, new JolCraftItemTagProvider(packOutput, lookup, blockTagsProvider.contentsGetter()));
        generator.addProvider(true, new JolCraftBiomeTagProvider(packOutput, lookup));
        generator.addProvider(true, new JolCraftStructureTagProvider(packOutput, lookup));

        generator.addProvider(true, new AdvancementProvider(
                packOutput, lookup, List.of(new JolCraftAdvancementProvider())
        ));

        generator.addProvider(true, new JolCraftDatapackProvider(packOutput, lookup));
        generator.addProvider(true, new JolCraftConfigProvider(packOutput));
    }
}