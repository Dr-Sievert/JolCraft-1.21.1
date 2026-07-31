package net.sievert.jolcraft.world.loot.custom.reward.client;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class RewardLootTableClientCache {

    private static Map<ResourceKey<LootTable>, LootTable> tables = Map.of();

    private RewardLootTableClientCache() {}

    public static void replace(
            @NotNull Map<ResourceKey<LootTable>, LootTable> updated
    ) {
        tables = Map.copyOf(updated);
    }

    public static @NotNull Optional<LootTable> get(
            @NotNull ResourceKey<LootTable> key
    ) {
        return Optional.ofNullable(tables.get(key));
    }

    public static void clear() {
        tables = Map.of();
    }
}
