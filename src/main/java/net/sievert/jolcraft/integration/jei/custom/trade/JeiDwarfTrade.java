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
        return DwarfTrades.getExampleInputA(recipe);
    }

    public @Nullable ItemStack inputBExample() {
        ItemStack b = DwarfTrades.getExampleInputB(recipe);
        return b.isEmpty() ? null : b;
    }

    public ItemStack outputExample(RegistryAccess registryAccess) {
        return DwarfTrades.getExampleOutput(recipe, registryAccess);
    }
}
