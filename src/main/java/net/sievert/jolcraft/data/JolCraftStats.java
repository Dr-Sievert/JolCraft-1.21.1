package net.sievert.jolcraft.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;

import java.util.function.Supplier;

public class JolCraftStats {

    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, JolCraft.MOD_ID);

    public static final Supplier<ResourceLocation> STRUCTURES_DISCOVERED_ID =
            CUSTOM_STATS.register("structures_discovered", () -> JolCraft.location("structures_discovered"));

    public static Stat<ResourceLocation> STRUCTURES_DISCOVERED;

    public static void awardStructureDiscovery(Player player) {
        if (STRUCTURES_DISCOVERED != null) {
            player.awardStat(STRUCTURES_DISCOVERED, 1);
        }
    }

    public static void initializeStats() {
        STRUCTURES_DISCOVERED = Stats.CUSTOM.get(STRUCTURES_DISCOVERED_ID.get());
    }

    public static void register(IEventBus bus) {
        CUSTOM_STATS.register(bus);
    }
}
