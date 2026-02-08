package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class DwarfTrades {

    // -------------------------------------------------------------------------
    // Trade recipe cache
    // -------------------------------------------------------------------------

    private record TradeKey(
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int merchantLevel
    ) {}

    private static final Map<Object, Map<TradeKey, List<RecipeHolder<DwarfTradeRecipe>>>> TRADE_AT_LEVEL_CACHE =
            new IdentityHashMap<>();

    private static final Map<Object, Map<TradeKey, List<RecipeHolder<DwarfTradeRecipe>>>> TRADE_UP_TO_LEVEL_CACHE =
            new IdentityHashMap<>();

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

            ItemStack costAStack = recipe.rollCostA(random);
            Optional<ItemStack> costBStack = recipe.rollCostB(random);

            ItemStack out = recipe.rollResultBase(trader.level().registryAccess(), random);
            if (out.isEmpty()) return null;

            // Enchantments require a real Level + DifficultyInstance, so this is server-authoritative only.
            if (recipe.enchantmentProvider().isPresent()) {
                Level level = trader.level();
                EnchantmentHelper.enchantItemFromProvider(
                        out,
                        level.registryAccess(),
                        recipe.enchantmentProvider().get(),
                        level.getCurrentDifficultyAt(trader.blockPosition()),
                        random
                );
            }

            recipe.stackModifierId().ifPresent(id ->
                    DwarfTrades.StackModifiers.resolve(id).accept(out)
            );

            recipe.resultPatch().ifPresent(out::applyComponents);

            DwarfItemCost costA = new DwarfItemCost(costAStack.getItem(), costAStack.getCount());
            Optional<DwarfItemCost> costB = costBStack.map(s -> new DwarfItemCost(s.getItem(), s.getCount()));

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    out,
                    0,
                    recipe.maxUses(),
                    recipe.villagerXp(),
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

            // Enchantments require a real Level + DifficultyInstance, so this is server-authoritative only.
            recipe.enchantmentProvider().ifPresent(providerKey -> EnchantmentHelper.enchantItemFromProvider(
                    map,
                    serverLevel.registryAccess(),
                    providerKey,
                    serverLevel.getCurrentDifficultyAt(trader.blockPosition()),
                    random
            ));

            recipe.stackModifierId().ifPresent(id ->
                    DwarfTrades.StackModifiers.resolve(id).accept(map)
            );

            recipe.resultPatch().ifPresent(map::applyComponents);

            ItemStack costAStack = recipe.rollCostA(random);
            Optional<ItemStack> costBStack = recipe.rollCostB(random);

            DwarfItemCost costA = new DwarfItemCost(costAStack.getItem(), costAStack.getCount());
            Optional<DwarfItemCost> costB = costBStack.map(s -> new DwarfItemCost(s.getItem(), s.getCount()));

            return new DwarfMerchantOffer(
                    costA,
                    costB,
                    map,
                    0,
                    recipe.maxUses(),
                    recipe.villagerXp(),
                    recipe.priceMultiplier()
            );
        }
    }

    // -------------------------------------------------------------------------
    // Canonical recipe queries (ONLY query API)
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesAtLevel(
            ServerLevel level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int merchantLevel
    ) {
        Object access = level.recipeAccess();

        Map<TradeKey, List<RecipeHolder<DwarfTradeRecipe>>> byKey =
                TRADE_AT_LEVEL_CACHE.computeIfAbsent(access, a -> new IdentityHashMap<>());

        TradeKey key = new TradeKey(profession, pool, merchantLevel);
        List<RecipeHolder<DwarfTradeRecipe>> cached = byKey.get(key);
        if (cached != null) {
            return cached;
        }

        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof DwarfTradeRecipe r)) continue;
            if (r.profession() != profession) continue;
            if (r.pool() != pool) continue;
            if (r.merchantLevel() != merchantLevel) continue;

            out.add((RecipeHolder<DwarfTradeRecipe>) holder);
        }

        out.sort(Comparator.comparing(h -> h.id().location()));
        List<RecipeHolder<DwarfTradeRecipe>> frozen = List.copyOf(out);

        byKey.put(key, frozen);
        return frozen;
    }

    @SuppressWarnings("unchecked")
    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesUpToLevel(
            ServerLevel level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int maxMerchantLevel
    ) {
        Object access = level.recipeAccess();

        Map<TradeKey, List<RecipeHolder<DwarfTradeRecipe>>> byKey =
                TRADE_UP_TO_LEVEL_CACHE.computeIfAbsent(access, a -> new IdentityHashMap<>());

        TradeKey key = new TradeKey(profession, pool, maxMerchantLevel);
        List<RecipeHolder<DwarfTradeRecipe>> cached = byKey.get(key);
        if (cached != null) {
            return cached;
        }

        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof DwarfTradeRecipe r)) continue;
            if (r.profession() != profession) continue;
            if (r.pool() != pool) continue;
            if (r.merchantLevel() > maxMerchantLevel) continue;

            out.add((RecipeHolder<DwarfTradeRecipe>) holder);
        }

        out.sort(Comparator.comparing(h -> h.id().location()));
        List<RecipeHolder<DwarfTradeRecipe>> frozen = List.copyOf(out);

        byKey.put(key, frozen);
        return frozen;
    }

    // -------------------------------------------------------------------------
    // Stack modifier registry (used by recipes)
    // -------------------------------------------------------------------------

    public static final class StackModifiers {

        private static final Map<ResourceLocation, Consumer<ItemStack>> REGISTRY = new HashMap<>();

        private StackModifiers() {}

        public static void register(ResourceLocation id, Consumer<ItemStack> modifier) {
            REGISTRY.put(id, modifier);
        }

        public static Consumer<ItemStack> resolve(String id) {
            if (id == null || id.isBlank()) return s -> {};
            ResourceLocation rl = ResourceLocation.tryParse(id.trim());
            if (rl == null) return s -> {};
            return REGISTRY.getOrDefault(rl, s -> {});
        }
    }

    // -------------------------------------------------------------------------
    // JEI examples (RECIPE-BASED, no legacy listings)
    // -------------------------------------------------------------------------

    /**
     * JEI example input A (deterministic).
     * Uses a fixed-seed RNG so ranges produce stable examples.
     */
    public static ItemStack getExampleInputA(DwarfTradeRecipe recipe) {
        RandomSource random = RandomSource.create(0xC0FFEE);
        ItemStack a = recipe.rollCostA(random);
        return a.isEmpty() ? ItemStack.EMPTY : a.copyWithCount(1);
    }

    public static ItemStack getExampleInputB(DwarfTradeRecipe recipe) {
        RandomSource random = RandomSource.create(0xBADC0DE);
        Optional<ItemStack> b = recipe.rollCostB(random);
        return b.map(s -> s.copyWithCount(1)).orElse(ItemStack.EMPTY);
    }

    /**
     * JEI example output.
     * - MAP results: returns a filled map with the display name (no structure lookup in JEI).
     * - Other results: rolls base result using registry access only (no enchant provider here because it requires DifficultyInstance).
     *   Applies stack modifier + patch for visual correctness. Enchant provider is intentionally skipped in JEI.
     */
    public static ItemStack getExampleOutput(DwarfTradeRecipe recipe, RegistryAccess registryAccess) {
        if (recipe.result() instanceof DwarfTradeRecipe.TradeResult.MapResult(
                DwarfTradeRecipe.MapTradeData mapData
        )) {
            ItemStack map = new ItemStack(Items.FILLED_MAP);
            map.set(DataComponents.ITEM_NAME, Component.translatable(mapData.mapDisplayNameKey()));
            recipe.stackModifierId().ifPresent(id -> DwarfTrades.StackModifiers.resolve(id).accept(map));
            recipe.resultPatch().ifPresent(map::applyComponents);
            return map;
        }

        RandomSource random = RandomSource.create(0xDEADBEEFL);
        ItemStack out = recipe.rollResultBase(registryAccess, random);
        if (out.isEmpty()) return ItemStack.EMPTY;

        recipe.stackModifierId().ifPresent(id -> DwarfTrades.StackModifiers.resolve(id).accept(out));
        recipe.resultPatch().ifPresent(out::applyComponents);

        return out;
    }
}