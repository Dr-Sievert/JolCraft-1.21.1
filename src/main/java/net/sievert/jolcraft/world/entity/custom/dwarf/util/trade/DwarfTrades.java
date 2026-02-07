package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
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
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

            ItemStack costAStack = recipe.rollCostA(random);
            Optional<ItemStack> costBStack = recipe.rollCostB(random);

            ItemStack out = recipe.rollResult(trader.level().registryAccess(), random);
            if (out.isEmpty()) return null;

            var result = recipe.result();

            // Enchantments require a real Level + DifficultyInstance, so this is server-authoritative only.
            if (result.enchantmentProvider().isPresent()) {
                Level level = trader.level();
                EnchantmentHelper.enchantItemFromProvider(
                        out,
                        level.registryAccess(),
                        result.enchantmentProvider().get(),
                        level.getCurrentDifficultyAt(trader.blockPosition()),
                        random
                );
            }

            result.stackModifierId().ifPresent(id ->
                    DwarfTrades.StackModifiers.resolve(id).accept(out)
            );

            result.resultPatch().ifPresent(out::applyComponents);

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
                    DwarfTradeRecipe.MapTradeData mapData,
                    Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
                    Optional<String> stackModifierId,
                    Optional<DataComponentPatch> resultPatch
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

            resultPatch.ifPresent(map::applyComponents);

            enchantmentProvider.ifPresent(providerKey -> EnchantmentHelper.enchantItemFromProvider(
                    map,
                    serverLevel.registryAccess(),
                    providerKey,
                    serverLevel.getCurrentDifficultyAt(trader.blockPosition()),
                    random
            ));

            stackModifierId.ifPresent(id ->
                    DwarfTrades.StackModifiers.resolve(id).accept(map)
            );

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
        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof DwarfTradeRecipe r)) continue;
            if (r.profession() != profession) continue;
            if (r.pool() != pool) continue;
            if (r.merchantLevel() != merchantLevel) continue;

            out.add((RecipeHolder<DwarfTradeRecipe>) holder);
        }

        out.sort(Comparator.comparing(h -> h.id().location()));
        return out;
    }

    @SuppressWarnings("unchecked")
    public static List<RecipeHolder<DwarfTradeRecipe>> getTradeRecipesUpToLevel(
            ServerLevel level,
            DwarfProfession profession,
            DwarfTradeRecipe.TradePool pool,
            int maxMerchantLevel
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>();

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof DwarfTradeRecipe r)) continue;
            if (r.profession() != profession) continue;
            if (r.pool() != pool) continue;
            if (r.merchantLevel() > maxMerchantLevel) continue;

            out.add((RecipeHolder<DwarfTradeRecipe>) holder);
        }

        out.sort(Comparator.comparing(h -> h.id().location()));
        return out;
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
            return REGISTRY.getOrDefault(ResourceLocation.parse(id), s -> {});
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
     * - Other results: rolls result using registry access only (no enchant provider here because it requires DifficultyInstance).
     */
    public static ItemStack getExampleOutput(DwarfTradeRecipe recipe, net.minecraft.core.RegistryAccess registryAccess) {
        if (recipe.result() instanceof DwarfTradeRecipe.TradeResult.MapResult(
                DwarfTradeRecipe.MapTradeData mapData,
                Optional<ResourceKey<EnchantmentProvider>> ignoredEnchant,
                Optional<String> stackModifierId,
                Optional<DataComponentPatch> resultPatch
        )) {
            ItemStack map = new ItemStack(Items.FILLED_MAP);
            map.set(DataComponents.ITEM_NAME, Component.translatable(mapData.mapDisplayNameKey()));
            resultPatch.ifPresent(map::applyComponents);
            stackModifierId.ifPresent(id -> DwarfTrades.StackModifiers.resolve(id).accept(map));
            return map;
        }

        RandomSource random = RandomSource.create(0xDEADBEEFL);
        ItemStack out = recipe.rollResult(registryAccess, random);
        if (out.isEmpty()) return ItemStack.EMPTY;

        recipe.result().resultPatch().ifPresent(out::applyComponents);
        recipe.result().stackModifierId().ifPresent(id -> DwarfTrades.StackModifiers.resolve(id).accept(out));

        return out;
    }
}