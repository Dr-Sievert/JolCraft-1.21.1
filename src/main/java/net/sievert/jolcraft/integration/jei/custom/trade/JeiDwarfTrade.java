package net.sievert.jolcraft.integration.jei.custom.trade;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfTrades;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * JEI wrapper for the real DwarfTradeRecipe (data-driven).
 * NOTE:
 * - This is not a "second recipe type".
 * - It exists only to carry the recipe + spawn egg for JEI UI.
 */
public record JeiDwarfTrade(
        DwarfTradeRecipe recipe,
        DeferredItem<Item> spawnEgg
) {

    public DwarfProfession profession() {
        return recipe.profession();
    }

    public int level() {
        return recipe.merchantLevel();
    }

    public ItemStack inputAExample(RegistryAccess registryAccess) {
        RandomSource random = RandomSource.create(0xC0FFEE);
        return normalizeForJei(recipe.rollCostA(registryAccess, random));
    }

    public @Nullable ItemStack inputBExample(RegistryAccess registryAccess) {
        RandomSource random = RandomSource.create(0xBADC0DE);
        Optional<ItemStack> b = recipe.rollCostB(registryAccess, random);
        if (b.isEmpty()) return null;

        ItemStack stack = b.get();
        if (stack.isEmpty()) return null;

        return normalizeForJei(stack);
    }

    /**
     * Tag-aware checks for JEI UI decisions (no item hardcoding).
     *
     * True when:
     *  - ingredient is a TagIngredient AND equals the given tag
     *  - ingredient is an ItemIngredient AND the item is contained in the given tag
     */
    public boolean costAItemIs(TagKey<Item> tag) {
        return ingredientMatchesTag(recipe.costA().ingredient(), tag);
    }

    public boolean costBItemIs(TagKey<Item> tag) {
        return recipe.costB()
                .map(b -> ingredientMatchesTag(b.ingredient(), tag))
                .orElse(false);
    }

    private static boolean ingredientMatchesTag(DwarfTradeRecipe.TradeCostIngredient ing, TagKey<Item> tag) {
        if (ing instanceof DwarfTradeRecipe.TradeCostIngredient.TagIngredient ti) {
            return ti.tag().equals(tag);
        }
        if (ing instanceof DwarfTradeRecipe.TradeCostIngredient.ItemIngredient ii) {
            return ii.item().is(tag);
        }
        return false;
    }

    /**
     * JEI output:
     * - MAP results: filled map with the display name (no structure lookup in JEI).
     * - Other results: rolls base result via RegistryAccess; skips enchant provider (needs DifficultyInstance).
     *   Applies stack modifier + patch for visual correctness.
     */
    public ItemStack outputExample(RegistryAccess registryAccess) {
        if (recipe.result() instanceof DwarfTradeRecipe.TradeResult.MapResult(
                DwarfTradeRecipe.MapTradeData mapData
        )) {
            ItemStack map = new ItemStack(Items.FILLED_MAP);
            map.set(DataComponents.ITEM_NAME, Component.translatable(mapData.mapDisplayNameKey()));

            recipe.stackModifierId().ifPresent(id ->
                    DwarfTrades.StackModifiers.resolve(id).accept(map)
            );
            recipe.resultPatch().ifPresent(map::applyComponents);

            return normalizeForJei(map);
        }

        RandomSource random = RandomSource.create(0xDEADBEEFL);
        ItemStack out = recipe.rollResultBase(registryAccess, random);
        if (out.isEmpty()) return ItemStack.EMPTY;

        recipe.stackModifierId().ifPresent(id ->
                DwarfTrades.StackModifiers.resolve(id).accept(out)
        );
        recipe.resultPatch().ifPresent(out::applyComponents);

        return normalizeForJei(out);
    }

    public DwarfTradeRecipe.TradeAmount inputAmountA() {
        return recipe.costA().amount();
    }

    public @Nullable DwarfTradeRecipe.TradeAmount inputAmountB() {
        return recipe.costB().map(DwarfTradeRecipe.TradeCost::amount).orElse(null);
    }

    public DwarfTradeRecipe.TradeAmount outputAmount() {
        if (recipe.result() instanceof DwarfTradeRecipe.TradeResult.ItemResult ir) {
            return ir.amount();
        }
        return DwarfTradeRecipe.TradeAmount.fixed(1);
    }

    public static RegistryAccess getClientRegistryAccess() {
        var level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
    }

    private static ItemStack normalizeForJei(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }
}