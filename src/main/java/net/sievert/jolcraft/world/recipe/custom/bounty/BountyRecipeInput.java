package net.sievert.jolcraft.world.recipe.custom.bounty;

import com.mojang.serialization.DataResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.base.ContextInput;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record BountyRecipeInput(
        @NotNull WorldContext ctx,
        @NotNull ItemStack redeemStack,
        @NotNull DwarfProfession type,
        @NotNull DwarfMerchantData.Level tier
) implements RecipeInput, ContextInput {

    public BountyRecipeInput {
        Objects.requireNonNull(ctx, JolCraftDictionary.CONTEXT);
        Objects.requireNonNull(
                redeemStack,
                JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftDictionary.STACK)
        );
        Objects.requireNonNull(type, JolCraftDictionary.TYPE);
        Objects.requireNonNull(tier, JolCraftDictionary.TIER);
    }

    public static @NotNull DataResult<BountyRecipeInput> of(
            @NotNull WorldContext ctx,
            @NotNull ItemStack stack
    ) {
        Objects.requireNonNull(ctx, JolCraftDictionary.CONTEXT);
        Objects.requireNonNull(
                stack,
                JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftDictionary.STACK)
        );

        return BountyRecipe.readInfo(stack)
                .map(info -> new BountyRecipeInput(ctx, stack, info.type(), info.tier()));
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return index == 0 ? redeemStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}