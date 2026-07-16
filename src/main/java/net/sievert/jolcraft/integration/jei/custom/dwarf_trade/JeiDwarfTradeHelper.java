package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {}

    public static @NotNull List<JeiDwarfTrade>
    getAllDwarfJeiTrades(
            @NotNull DwarfProfession profession
    ) {
        ServerLevel serverLevel =
                getClientServerLevelOrNull();

        if (serverLevel == null) {
            return List.of();
        }

        var recipeType =
                JolCraftRecipes.DWARF_TRADE_TYPE.get();

        Collection<RecipeHolder<?>> recipes =
                serverLevel.getServer()
                        .getRecipeManager()
                        .getRecipes();

        List<RecipeHolder<DwarfTradeRecipe>> matching =
                new ArrayList<>();

        for (RecipeHolder<?> holder : recipes) {
            if (holder.value().getType()
                    != recipeType) {
                continue;
            }

            if (!(holder.value()
                    instanceof DwarfTradeRecipe trade)) {
                continue;
            }

            if (trade.profession()
                    != profession) {
                continue;
            }

            matching.add(
                    new RecipeHolder<>(
                            holder.id(),
                            trade
                    )
            );
        }

        matching.sort(
                Comparator
                        .comparingInt(
                                (
                                        RecipeHolder<DwarfTradeRecipe> holder
                                ) -> groupPriority(
                                        holder.value()
                                )
                        )
                        .thenComparingInt(holder ->
                                levelPriority(
                                        holder.value()
                                )
                        )
                        .thenComparingInt(holder ->
                                holder.value()
                                        .order()
                        )
                        .thenComparing(
                                RecipeHolder::id
                        )
        );

        List<JeiDwarfTrade> result =
                new ArrayList<>(
                        matching.size()
                );

        for (RecipeHolder<DwarfTradeRecipe> holder : matching) {
            result.add(
                    new JeiDwarfTrade(
                            holder.value(),
                            DwarfProfessionHelper.getSpawnEgg(
                                    profession
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private static int groupPriority(
            @NotNull DwarfTradeRecipe recipe
    ) {
        TradeGroup group =
                recipe.pool() != null
                        && recipe.pool().group() != null
                        ? recipe.pool().group()
                        : TradeGroup.MAIN;

        return switch (group) {
            case MAIN -> 0;
            case EXACT_LEVEL_POOL -> 1;
            case CUMULATIVE_POOL -> 2;
            case GLOBAL_POOL -> 3;
        };
    }

    private static int levelPriority(
            @NotNull DwarfTradeRecipe recipe
    ) {
        if (recipe.merchantLevel() == null) {
            return Integer.MAX_VALUE;
        }

        return recipe.merchantLevel()
                .getId();
    }

    private static @Nullable ServerLevel
    getClientServerLevelOrNull() {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.getSingleplayerServer() == null) {
            return null;
        }

        var serverPlayer =
                minecraft.getSingleplayerServer()
                        .getPlayerList()
                        .getPlayer(
                                minecraft.player.getUUID()
                        );

        return serverPlayer != null
                ? serverPlayer.serverLevel()
                : null;
    }
}