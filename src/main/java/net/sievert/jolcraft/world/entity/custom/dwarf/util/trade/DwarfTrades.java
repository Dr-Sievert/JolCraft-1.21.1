package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.data.recipe.util.RecipeStackModifiers;
import net.sievert.jolcraft.data.recipe.util.RecipeStackTransformations;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class DwarfTrades {

    private DwarfTrades() {}

    // -------------------------------------------------------------------------
    // Recipe -> Offer (ONLY source of truth)
    // -------------------------------------------------------------------------

    public static final class RecipeListing {
        private final DwarfTradeRecipe recipe;

        public RecipeListing(DwarfTradeRecipe recipe) {
            this.recipe = recipe;
        }

        @Nullable
        public DwarfMerchantOffer getOffer(Entity trader, RandomSource random) {
            if (recipe.result().type() == DwarfTradeRecipe.TradeResult.Type.MAP) {
                return createTreasureMapOffer(trader, random);
            }

            Level level = trader.level();
            var registries = level.registryAccess();

            ItemStack costAStack = recipe.rollCostA(registries, random);
            Optional<ItemStack> costBStack = recipe.rollCostB(registries, random);

            ItemStack out = recipe.rollResultBase(registries, random);
            if (out.isEmpty()) return null;

            RecipeStackTransformations.applyWithResolver(
                    out,
                    level,
                    trader,
                    random,
                    recipe.enchantmentProvider(),
                    recipe.stackModifierId(),
                    recipe.resultPatch(),
                    RecipeStackModifiers::resolve
            );

            DwarfItemCost costA = new DwarfItemCost(costAStack.getItem(), costAStack.getCount());
            Optional<DwarfItemCost> costB = costBStack.map(s -> new DwarfItemCost(s.getItem(), s.getCount()));

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    out,
                    0,
                    recipe.maxUses(),
                    recipe.dwarfXp(),
                    recipe.priceMultiplier()
            );
        }

        @Nullable
        private DwarfMerchantOffer createTreasureMapOffer(Entity trader, RandomSource random) {
            if (!(trader.level() instanceof ServerLevel serverLevel)) return null;

            if (!(recipe.result() instanceof DwarfTradeRecipe.TradeResult.MapResult(
                    DwarfTradeRecipe.MapTradeData mapData
            ))) return null;

            BlockPos targetPos;
            try {
                targetPos = serverLevel.findNearestMapStructure(
                        mapData.destinationStructureTag(),
                        trader.blockPosition(),
                        100,
                        true
                );
            } catch (Exception e) {
                return null;
            }
            if (targetPos == null) return null;

            Holder<MapDecorationType> destinationType;
            try {
                var lookup = serverLevel.registryAccess().lookupOrThrow(Registries.MAP_DECORATION_TYPE);
                ResourceKey<MapDecorationType> key = ResourceKey.create(Registries.MAP_DECORATION_TYPE, mapData.mapDecorationTypeId());
                destinationType = lookup.getOrThrow(key);
            } catch (Exception e) {
                return null;
            }

            ItemStack map = MapItem.create(serverLevel, targetPos.getX(), targetPos.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(serverLevel, map);
            MapItemSavedData.addTargetDecoration(map, targetPos, "+", destinationType);
            map.set(DataComponents.ITEM_NAME, Component.translatable(mapData.mapDisplayNameKey()));

            RecipeStackTransformations.applyWithResolver(
                    map,
                    serverLevel,
                    trader,
                    random,
                    recipe.enchantmentProvider(),
                    recipe.stackModifierId(),
                    recipe.resultPatch(),
                    RecipeStackModifiers::resolve
            );

            var registries = serverLevel.registryAccess();

            ItemStack costAStack = recipe.rollCostA(registries, random);
            Optional<ItemStack> costBStack = recipe.rollCostB(registries, random);

            DwarfItemCost costA = new DwarfItemCost(costAStack.getItem(), costAStack.getCount());
            Optional<DwarfItemCost> costB = costBStack.map(s -> new DwarfItemCost(s.getItem(), s.getCount()));

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    map,
                    0,
                    recipe.maxUses(),
                    recipe.dwarfXp(),
                    recipe.priceMultiplier()
            );
        }
    }

    // -------------------------------------------------------------------------
    // Canonical recipe queries (NO caching)
    // -------------------------------------------------------------------------

    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesAtLevel(
            Level level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int merchantLevel
    ) {
        return findTradeRecipesAtLevel(level, profession, pool, merchantLevel);
    }

    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesUpToLevel(
            Level level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int merchantLevel
    ) {
        return findTradeRecipesUpToLevel(level, profession, pool, merchantLevel);
    }

    // -------------------------------------------------------------------------
    // Internal recipe queries
    // -------------------------------------------------------------------------

    private static List<RecipeHolder<DwarfTradeRecipe>> findTradeRecipesAtLevel(
            Level level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int merchantLevel
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> all = getAllTradeRecipes(level);
        List<RecipeHolder<DwarfTradeRecipe>> filtered = new ArrayList<>();

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe r = holder.value();

            if (r.profession() != profession) continue;
            if (r.pool() != pool) continue;
            if (r.merchantLevel() != merchantLevel) continue;

            filtered.add(holder);
        }

        sortWithOptionalOrder(filtered);
        return List.copyOf(filtered);
    }

    private static List<RecipeHolder<DwarfTradeRecipe>> findTradeRecipesUpToLevel(
            Level level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int merchantLevel
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> all = getAllTradeRecipes(level);
        List<RecipeHolder<DwarfTradeRecipe>> filtered = new ArrayList<>();

        for (RecipeHolder<DwarfTradeRecipe> holder : all) {
            DwarfTradeRecipe r = holder.value();

            if (r.profession() != profession) continue;
            if (r.pool() != pool) continue;
            if (r.merchantLevel() > merchantLevel) continue;

            filtered.add(holder);
        }

        sortWithOptionalOrder(filtered);
        return List.copyOf(filtered);
    }

    private static void sortWithOptionalOrder(List<RecipeHolder<DwarfTradeRecipe>> recipes) {
        recipes.sort((a, b) -> {
            DwarfTradeRecipe ra = a.value();
            DwarfTradeRecipe rb = b.value();

            boolean oa = ra.order().isPresent();
            boolean ob = rb.order().isPresent();

            if (oa && ob) {
                return Integer.compare(ra.order().getAsInt(), rb.order().getAsInt());
            }
            if (oa) return -1;
            if (ob) return 1;
            return 0;
        });
    }

    @SuppressWarnings("unchecked")
    private static List<RecipeHolder<DwarfTradeRecipe>> getAllTradeRecipes(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        Collection<RecipeHolder<?>> all = serverLevel.getServer().getRecipeManager().getRecipes();
        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : all) {
            if (!(holder.value() instanceof DwarfTradeRecipe trade)) continue;
            if (trade.getType() != JolCraftRecipes.DWARF_TRADE_TYPE.get()) continue;

            RecipeHolder<DwarfTradeRecipe> cast = (RecipeHolder<DwarfTradeRecipe>) holder;
            out.add(cast);
        }

        return List.copyOf(out);
    }
}