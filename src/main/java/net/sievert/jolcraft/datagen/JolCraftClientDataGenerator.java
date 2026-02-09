package net.sievert.jolcraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.client.atlas.JolCraftAtlasProvider;
import net.sievert.jolcraft.datagen.client.equipment.JolCraftEquipmentProvider;
import net.sievert.jolcraft.datagen.client.language.JolCraftLanguageProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class JolCraftClientDataGenerator {

    private JolCraftClientDataGenerator() {}

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {

        JolCraftLogs.info(JolCraftLogTags.DATAGEN, "Client gather: registering client providers.");

        addClientProviders(event.getGenerator(), event.getLookupProvider());

        JolCraftLogs.debug(JolCraftLogTags.DATAGEN, "Client providers: {}, {}, {}, {}",
                JolCraftModelProvider.class.getSimpleName(),
                JolCraftLanguageProvider.class.getSimpleName(),
                JolCraftEquipmentProvider.class.getSimpleName(),
                JolCraftAtlasProvider.class.getSimpleName()
        );

        JolCraftServerDataGenerator.addServerProviders(event.getGenerator(), event.getLookupProvider());
    }

    private static void addClientProviders(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(true, new JolCraftModelProvider(packOutput, lookupProvider));
        generator.addProvider(true, new JolCraftLanguageProvider(packOutput));
        generator.addProvider(true, new JolCraftEquipmentProvider(packOutput));
        generator.addProvider(true, new JolCraftAtlasProvider(packOutput));
    }
}