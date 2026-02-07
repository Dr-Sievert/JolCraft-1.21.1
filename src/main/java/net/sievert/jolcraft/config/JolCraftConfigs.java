package net.sievert.jolcraft.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.dwarf.DwarfProfessionConfigs;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftConfigs {

    private JolCraftConfigs() {}

    public record Entry(ResourceLocation id, Supplier<? extends PreparableReloadListener> factory) {}

    public static final List<Entry> ALL = List.of(
            new Entry(DwarfProfessionConfigs.RELOAD_LISTENER_ID, DwarfProfessionConfigs::new)
    );

    @SubscribeEvent
    public static void onAddServerReloadListeners(AddServerReloadListenersEvent event) {
        for (Entry entry : ALL) {
            event.addListener(entry.id(), entry.factory().get());
        }
    }
}