package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.integration.jei.util.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {
    }

    public static @NotNull List<JeiDwarfTrade>
    getAllDwarfJeiTrades(
            @NotNull DwarfProfession profession
    ) {
        ClientLevel clientLevel =
                Minecraft.getInstance().level;

        if (clientLevel == null) {
            return List.of();
        }

        List<RecipeHolder<DwarfTradeRecipe>> matching =
                new ArrayList<>();

        for (
                RecipeHolder<DwarfTradeRecipe> holder :
                clientLevel.getRecipeManager()
                        .getAllRecipesFor(
                                JolCraftRecipes
                                        .DWARF_TRADE_TYPE
                                        .get()
                        )
        ) {
            if (holder.value().profession() != profession) {
                continue;
            }

            matching.add(holder);
        }

        matching.sort(
                Comparator
                        .<RecipeHolder<DwarfTradeRecipe>>comparingInt(
                                holder ->
                                        groupPriority(
                                                holder.value()
                                        )
                        )
                        .thenComparingInt(
                                holder ->
                                        levelPriority(
                                                holder.value()
                                        )
                        )
                        .thenComparingInt(
                                holder ->
                                        holder.value().order()
                        )
                        .thenComparing(
                                RecipeHolder::id
                        )
        );

        DeferredItem<Item> spawnEgg =
                DwarfProfessionHelper.getSpawnEgg(
                        profession
                );

        List<JeiDwarfTrade> result =
                new ArrayList<>();

        for (
                RecipeHolder<DwarfTradeRecipe> holder :
                matching
        ) {
            DwarfTradeRecipe recipe =
                    holder.value();

            List<JeiItemOutcome> outcomes =
                    ItemOutputJeiTranslator.translate(
                            recipe.result()
                    );

            for (JeiItemOutcome outcome : outcomes) {
                result.add(
                        new JeiDwarfTrade(
                                recipe,
                                spawnEgg,
                                outcome
                        )
                );
            }
        }

        return List.copyOf(result);
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
        return recipe.merchantLevel() == null
                ? Integer.MAX_VALUE
                : recipe.merchantLevel().getId();
    }
}