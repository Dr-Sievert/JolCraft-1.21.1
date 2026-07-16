package net.sievert.jolcraft.world.recipe.output;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public final class ItemOutputs {

    private ItemOutputs() {}

    public static ItemOutput item(
            LootPoolEntryContainer.Builder<?> entry
    ) {
        return pool(
                LootPool.lootPool()
                        .add(entry)
        );
    }

    public static ItemOutput pool(
            LootPool.Builder pool
    ) {
        return ItemOutput.of(pool);
    }

    public static ItemOutput pool(
            NumberProvider rolls,
            LootPoolEntryContainer.Builder<?>... entries
    ) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(rolls);

        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }

        return ItemOutput.of(pool);
    }
}