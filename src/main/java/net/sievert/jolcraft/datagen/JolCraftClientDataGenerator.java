package net.sievert.jolcraft.datagen;

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

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class JolCraftClientDataGenerator {

    private JolCraftClientDataGenerator() {}

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        addClientProviders(event.getGenerator());
        JolCraftServerDataGenerator.addServerProviders(event.getGenerator(), event.getLookupProvider());
    }

    private static void addClientProviders(DataGenerator generator) {
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(true, new JolCraftModelProvider(packOutput));
        generator.addProvider(true, new JolCraftLanguageProvider(packOutput));
        generator.addProvider(true, new JolCraftEquipmentProvider(packOutput));
        generator.addProvider(true, new JolCraftAtlasProvider(packOutput));
    }
}