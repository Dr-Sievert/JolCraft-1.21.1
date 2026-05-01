package net.sievert.jolcraft.config;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;

import java.util.List;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftConfigs {

    private JolCraftConfigs() {}

    public static final List<PreparableReloadListener> ALL = List.of(
            DwarfProfessionConfigManager.INSTANCE
    );

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        for (PreparableReloadListener listener : ALL) {
            event.addListener(listener);
        }
    }
}