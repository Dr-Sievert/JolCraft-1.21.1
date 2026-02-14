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

    private static Supplier<ResourceLocation> stat(String path) {
        return STATS.register(path, () -> JolCraft.location(path));
    }

    public static final Supplier<ResourceLocation> STRUCTURES_DISCOVERED =
            stat(JolCraftStatIds.DISCOVERED_STRUCTURES);

    public static final Supplier<ResourceLocation> TALK_TO_DWARF =
            stat(JolCraftStatIds.TALK_TO_DWARF);

    public static final Supplier<ResourceLocation> TRADE_WITH_DWARF =
            stat(JolCraftStatIds.TRADE_WITH_DWARF);

    public static void register(IEventBus bus) {
        STATS.register(bus);
    }
}
