package net.sievert.jolcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.advancement.JolCraftAdvancementProvider;
import net.sievert.jolcraft.datagen.client.atlas.JolCraftAtlasProvider;
import net.sievert.jolcraft.datagen.client.language.JolCraftLanguageProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.sound.JolCraftSoundProvider;
import net.sievert.jolcraft.datagen.config.JolCraftConfigProvider;
import net.sievert.jolcraft.datagen.loot.glm.JolCraftGlobalLootModifierProvider;
import net.sievert.jolcraft.datagen.loot.table.JolCraftMainLootTableProvider;
import net.sievert.jolcraft.datagen.recipe.JolCraftRecipeProvider;
import net.sievert.jolcraft.datagen.tag.provider.*;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class JolCraftDataGenerator {

    private JolCraftDataGenerator() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        JolCraftLogs.info(
                JolCraftLogTags.DATAGEN,
                "Registering datagen providers (server={}, client={})",
                event.includeServer(),
                event.includeClient()
        );

        if (event.includeServer()) {



            generator.addProvider(true, new JolCraftDataMapProvider(packOutput, lookupProvider));
            generator.addProvider(true, new JolCraftRecipeProvider(packOutput, lookupProvider));
            generator.addProvider(true, new JolCraftGlobalLootModifierProvider(packOutput, lookupProvider));
            generator.addProvider(true, new JolCraftMainLootTableProvider(packOutput, lookupProvider));

            generator.addProvider(true, new JolCraftBiomeTagProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper
            ));

            JolCraftBlockTagProvider blockTagsProvider = new JolCraftBlockTagProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper
            );

            generator.addProvider(true, blockTagsProvider);

            generator.addProvider(true, new JolCraftDamageTypeTagProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper
            ));

            generator.addProvider(true, new JolCraftEntityTypeTagProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper
            ));

            generator.addProvider(true, new JolCraftInstrumentTagProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper
            ));

            generator.addProvider(true, new JolCraftItemTagProvider(
                    packOutput,
                    lookupProvider,
                    blockTagsProvider.contentsGetter(),
                    existingFileHelper
            ));


            generator.addProvider(true, new JolCraftStructureTagProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper
            ));

            generator.addProvider(true, new AdvancementProvider(
                    packOutput,
                    lookupProvider,
                    existingFileHelper,
                    List.of((registries, consumer, fileHelper) ->
                            new JolCraftAdvancementProvider().generate(registries, consumer))
            ));

            generator.addProvider(true, new JolCraftDatapackProvider(packOutput, lookupProvider));
            generator.addProvider(true, new JolCraftConfigProvider(packOutput));

            JolCraftLogs.debug(
                    JolCraftLogTags.DATAGEN,
                    "Server providers: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                    JolCraftBlockTagProvider.class.getSimpleName(),
                    JolCraftDataMapProvider.class.getSimpleName(),
                    JolCraftRecipeProvider.class.getSimpleName(),
                    JolCraftGlobalLootModifierProvider.class.getSimpleName(),
                    JolCraftMainLootTableProvider.class.getSimpleName(),
                    JolCraftItemTagProvider.class.getSimpleName(),
                    JolCraftBiomeTagProvider.class.getSimpleName(),
                    JolCraftStructureTagProvider.class.getSimpleName(),
                    JolCraftDatapackProvider.class.getSimpleName(),
                    JolCraftConfigProvider.class.getSimpleName()
            );
        }

        if (event.includeClient()) {
            generator.addProvider(true, new JolCraftModelProvider(packOutput, lookupProvider, existingFileHelper));
            generator.addProvider(true, new JolCraftLanguageProvider(packOutput));
            generator.addProvider(true, new JolCraftAtlasProvider(packOutput));
            generator.addProvider(true, new JolCraftSoundProvider(packOutput, existingFileHelper));

            JolCraftLogs.debug(
                    JolCraftLogTags.DATAGEN,
                    "Client providers: {}, {}, {}, {}",
                    JolCraftModelProvider.class.getSimpleName(),
                    JolCraftLanguageProvider.class.getSimpleName(),
                    JolCraftAtlasProvider.class.getSimpleName(),
                    JolCraftSoundProvider.class.getSimpleName()
            );
        }
    }
}