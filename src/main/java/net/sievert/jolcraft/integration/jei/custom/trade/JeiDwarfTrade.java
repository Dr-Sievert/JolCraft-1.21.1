package net.sievert.jolcraft.integration.jei.custom.trade;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfTrades;

import javax.annotation.Nullable;

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

    public ItemStack inputAExample() {
        return normalizeForJei(DwarfTrades.getExampleInputA(recipe));
    }

    public @Nullable ItemStack inputBExample() {
        ItemStack b = DwarfTrades.getExampleInputB(recipe);
        if (b.isEmpty()) return null;
        return normalizeForJei(b);
    }

    public ItemStack outputExample(RegistryAccess registryAccess) {
        return normalizeForJei(DwarfTrades.getExampleOutput(recipe, registryAccess));
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

    private static ItemStack normalizeForJei(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }
}