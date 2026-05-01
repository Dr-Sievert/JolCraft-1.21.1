package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public record DwarfGuardTrades(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfGuardTrades(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    private static final DwarfProfession PROFESSION = DwarfProfession.GUARD;

    @Override
    public @NotNull String id() {
        return folder();
    }

    @Override
    public @NotNull String folder() {
        return PROFESSION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costA(JolCraftItems.AEGISCORE.get().asItem(), 1)
                        .result(JolCraftItems.GOLD_COIN.get().asItem(), 30)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costA(JolCraftItems.GOLD_COIN.get().asItem(), 30)
                        .costB(JolCraftItems.AEGISCORE.get().asItem(), 1)
                        .result(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get().asItem(), 1)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );
    }
}