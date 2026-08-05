package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record DwarfKeeperTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.KEEPER;

    public DwarfKeeperTrades(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

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
        addBountyTrades(
                output,
                tracking,
                PROFESSION
        );

        emitOrdered(
                output,
                tracking,
                seedTrade(
                        DwarfMerchantData.Level.APPRENTICE,
                        JolCraftItems.DUSKHOLD_SEEDS.get()
                )
        );

        emitOrdered(
                output,
                tracking,
                seedTrade(
                        DwarfMerchantData.Level.JOURNEYMAN,
                        JolCraftItems.YANILLIAN_SEEDS.get()
                )
        );

        emitOrdered(
                output,
                tracking,
                seedTrade(
                        DwarfMerchantData.Level.EXPERT,
                        JolCraftItems.KRANDONIAN_SEEDS.get()
                )
        );

        emitOrdered(
                output,
                tracking,
                seedTrade(
                        DwarfMerchantData.Level.MASTER,
                        JolCraftItems.ASGARNIAN_SEEDS.get()
                )
        );
    }

    private static @NotNull DwarfTradeRecipeBuilder seedTrade(
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike seed
    ) {
        return DwarfTradeRecipeBuilder.create()
                .profession(PROFESSION)
                .merchantLevel(level)
                .costACoins(10)
                .noCostB()
                .result(seed)
                .maxUses(5)
                .dwarfXp(1)
                .priceMultiplier(0.05F);
    }
}