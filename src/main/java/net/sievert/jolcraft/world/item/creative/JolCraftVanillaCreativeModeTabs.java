package net.sievert.jolcraft.world.item.creative;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.JolCraftItems;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class JolCraftVanillaCreativeModeTabs {

    @SubscribeEvent
    public static void onBuildVanillaTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(JolCraftItems.DWARF_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_MERCHANT_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_GUARD_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_KEEPER_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_ARTISAN_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_EXPLORER_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_MINER_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_ARCANIST_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_PRIEST_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_BLACKSMITH_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_CHAMPION_SPAWN_EGG);
            event.accept(JolCraftItems.DWARF_SMELTER_SPAWN_EGG);
            event.accept(JolCraftItems.MUFFHORN_SPAWN_EGG);
        }
    }
}
