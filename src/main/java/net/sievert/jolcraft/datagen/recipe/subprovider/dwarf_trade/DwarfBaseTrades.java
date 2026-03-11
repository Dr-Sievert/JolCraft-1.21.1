package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class DwarfBaseTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.NONE;

    @Override
    public @NotNull String folder() {
        return JolCraftParameterIds.BASE;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        executor.emit(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costACoins(1, 4)
                        .noCostB()
                        .result(Items.STICK, 2, 8)
                        .maxUses(6)
                        .dwarfXp(500)
                        .priceMultiplier(0.05F)
                        .buildValidated()
        );

        executor.emit(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(30, 30)
                        .costB(Items.PURPLE_DYE, 1)
                        .result(JolCraftItems.GUILD_SIGIL.get().asItem(), 1)
                        .buildValidated()
        );
    }
}