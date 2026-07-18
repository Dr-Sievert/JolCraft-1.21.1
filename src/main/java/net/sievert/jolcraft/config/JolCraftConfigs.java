package net.sievert.jolcraft.config;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.List;

@SuppressWarnings("removal")
@EventBusSubscriber(
        modid = JolCraft.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class JolCraftConfigs {

    private static final String COMMON_CONFIG_FILE = JolCraftStrings.dashed(JolCraft.MOD_ID, Rarity.COMMON.name().toLowerCase()) + ".toml";

    public static final List<PreparableReloadListener> ALL = List.of(
            DwarfProfessionConfigManager.INSTANCE
    );

    private JolCraftConfigs() {}

    /**
     * Registers the conventional NeoForge TOML configuration files.
     *
     * This must be called from the JolCraft mod constructor.
     */
    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                JolCraftCommonConfig.SPEC,
                COMMON_CONFIG_FILE
        );
    }

    /**
     * Registers JolCraft's data-driven configuration managers as server
     * resource reload listeners.
     */
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        ALL.forEach(event::addListener);
    }
}