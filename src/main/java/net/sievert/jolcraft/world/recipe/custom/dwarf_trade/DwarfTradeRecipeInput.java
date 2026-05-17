package net.sievert.jolcraft.world.recipe.custom.dwarf_trade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.base.ContextInput;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record DwarfTradeRecipeInput(
        @NotNull WorldContext ctx,
        @NotNull DwarfProfession profession,
        @NotNull DwarfMerchantData.Level merchantLevel,
        @NotNull ItemStack costA,
        @NotNull ItemStack costB
) implements RecipeInput, ContextInput, ItemTransformSourceResolver {

    public DwarfTradeRecipeInput {
        Objects.requireNonNull(ctx, JolCraftDictionary.CONTEXT);
        Objects.requireNonNull(profession, JolCraftDictionary.PROFESSION);
        Objects.requireNonNull(merchantLevel, JolCraftStrings.underscored(JolCraftDictionary.MERCHANT, JolCraftDictionary.LEVEL));
        Objects.requireNonNull(costA, JolCraftStrings.underscored(JolCraftDictionary.COST, "a"));
        Objects.requireNonNull(costB, JolCraftStrings.underscored(JolCraftDictionary.COST, "b"));
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> costA;
            case 1 -> costB;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public @NotNull ItemStack resolveItemTransformSource(@NotNull String source) {
        if (DwarfTradeRecipe.SOURCE_COST_A.equals(source)) {
            return costA;
        }

        if (DwarfTradeRecipe.SOURCE_COST_B.equals(source)) {
            return costB;
        }

        return ItemStack.EMPTY;
    }
}