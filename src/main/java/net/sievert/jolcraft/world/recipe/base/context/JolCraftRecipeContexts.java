package net.sievert.jolcraft.world.recipe.base.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.Optional;
import java.util.function.Consumer;

public final class JolCraftRecipeContexts {

    private JolCraftRecipeContexts() {}

    public static LootContext create(
            ServerLevel level,
            LootContextParamSet paramSet,
            Consumer<LootParams.Builder> parameters
    ) {
        LootParams.Builder builder = new LootParams.Builder(level);
        parameters.accept(builder);

        return new LootContext.Builder(builder.create(paramSet))
                .create(Optional.empty());
    }

    public static LootContext create(
            ServerLevel level,
            RandomSource random,
            LootContextParamSet paramSet,
            Consumer<LootParams.Builder> parameters
    ) {
        LootParams.Builder builder = new LootParams.Builder(level);
        parameters.accept(builder);

        return new LootContext.Builder(builder.create(paramSet))
                .withOptionalRandomSource(random)
                .create(Optional.empty());
    }
}