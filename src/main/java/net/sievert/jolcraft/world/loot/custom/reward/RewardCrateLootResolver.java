package net.sievert.jolcraft.world.loot.custom.reward;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolves and rolls deferred reward-crate loot.
 */
public final class RewardCrateLootResolver {

    private RewardCrateLootResolver() {}

    /**
     * Empty means the source could not be resolved. A present empty list is a
     * valid roll which generated no items.
     */
    public static @NotNull Optional<List<ItemStack>> generate(
            @NotNull ServerPlayer player,
            @NotNull RewardCrateSource source
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(source, "source");

        ServerLevel level = player.serverLevel();

        LootTable table =
                resolve(level, source)
                        .orElse(null);

        if (table == null) {
            return Optional.empty();
        }

        LootParams params =
                new LootParams.Builder(level)
                        .withParameter(
                                LootContextParams.ORIGIN,
                                player.position()
                        )
                        .withParameter(
                                LootContextParams.THIS_ENTITY,
                                player
                        )
                        .withLuck(player.getLuck())
                        .create(LootContextParamSets.CHEST);

        return Optional.of(
                table.getRandomItems(params)
                        .stream()
                        .filter(stack -> stack != null && !stack.isEmpty())
                        .map(ItemStack::copy)
                        .toList()
        );
    }

    public static @NotNull Optional<LootTable> resolve(
            @NotNull ServerLevel level,
            @NotNull RewardCrateSource source
    ) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(source, "source");

        if (source instanceof RewardCrateSource.LootTableSource direct) {
            LootTable table =
                    level.getServer()
                            .reloadableRegistries()
                            .getLootTable(direct.lootTable());

            return table == LootTable.EMPTY
                    ? Optional.empty()
                    : Optional.of(table);
        }

        RewardCrateSource.RecipeSource recipeSource =
                (RewardCrateSource.RecipeSource) source;

        RecipeHolder<?> holder =
                level.getRecipeManager()
                        .byKey(recipeSource.recipeId())
                        .orElse(null);

        if (holder == null
                || !(holder.value() instanceof BountyRewardRecipe recipe)) {
            return Optional.empty();
        }

        return Optional.of(recipe.createLootTable());
    }
}
