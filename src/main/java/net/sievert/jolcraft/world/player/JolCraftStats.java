package net.sievert.jolcraft.world.player;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.stat.JolCraftStatIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

import java.util.function.Supplier;

public final class JolCraftStats {

    private JolCraftStats() {}

    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Registries.CUSTOM_STAT, JolCraft.MOD_ID);

    private static Supplier<ResourceLocation> stat(String path) {
        return STATS.register(path, () -> JolCraft.location(path));
    }

    public static final Supplier<ResourceLocation> STRUCTURES_DISCOVERED =
            stat(JolCraftStatIds.DISCOVERED_STRUCTURES);

    public static final Supplier<ResourceLocation> TALK_TO_DWARF =
            stat(JolCraftStatIds.TALK_TO_DWARF);

    public static final Supplier<ResourceLocation> TRADE_WITH_DWARF =
            stat(JolCraftStatIds.TRADE_WITH_DWARF);

    public static final Supplier<ResourceLocation> COINS_SPENT =
            stat(JolCraftStatIds.COINS_SPENT);

    public static final Supplier<ResourceLocation> DWARVEN_TOMES_IDENTIFIED =
            stat(JolCraftStatIds.DWARVEN_TOMES_IDENTIFIED);

    public static final Supplier<ResourceLocation> DWARVEN_BOUNTIES_COMPLETED =
            stat(JolCraftStatIds.DWARVEN_BOUNTIES_COMPLETED);

    public static final Supplier<ResourceLocation> DWARVEN_BREWS_CREATED =
            stat(JolCraftStatIds.DWARVEN_BREWS_CREATED);

    public static final Supplier<ResourceLocation> GEODES_CRACKED =
            stat(JolCraftStatIds.GEODES_CRACKED);

    public static final Supplier<ResourceLocation> GEMS_CRUSHED =
            stat(JolCraftStatIds.GEMS_CRUSHED);

    public static final Supplier<ResourceLocation> GEMS_CUT =
            stat(JolCraftStatIds.GEMS_CUT);

    public static void register(IEventBus bus) {
        STATS.register(bus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} stats",
                STATS.getEntries().size()
        );
    }
}
