package net.sievert.jolcraft.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.stat.JolCraftStatIds;

import java.util.function.Supplier;

public final class JolCraftStats {

    private JolCraftStats() {}

    public static final DeferredRegister<ResourceLocation> STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, JolCraft.MOD_ID);

    public static final Supplier<ResourceLocation> STRUCTURES_DISCOVERED =
            STATS.register(
                    JolCraftStatIds.STRUCTURES_DISCOVERED,
                    () -> JolCraft.location(JolCraftStatIds.STRUCTURES_DISCOVERED)
            );

    public static void awardStructureDiscovery(Player player) {
        player.awardStat(Stats.CUSTOM.get(STRUCTURES_DISCOVERED.get()));
    }

    public static void register(IEventBus bus) {
        STATS.register(bus);
    }
}