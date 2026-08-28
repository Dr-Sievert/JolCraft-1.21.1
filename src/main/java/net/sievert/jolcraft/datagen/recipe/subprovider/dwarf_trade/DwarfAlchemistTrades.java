package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import org.jetbrains.annotations.NotNull;

public record DwarfAlchemistTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.ALCHEMIST;

    public DwarfAlchemistTrades(
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
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costA(
                                DwarfTradeRecipeBuilder.cost(
                                        DataComponentIngredient.of(
                                                false,
                                                essenceStack(EssenceType.INFUSED)
                                        ),
                                        1,
                                        1
                                )
                        )
                        .costAName("infused_essence")
                        .noCostB()
                        .coinsResult(1, 2)
                        .maxUses(64)
                        .dwarfXp(2)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costA(
                                DwarfTradeRecipeBuilder.cost(
                                        DataComponentIngredient.of(
                                                false,
                                                essenceStack(EssenceType.REFINED)
                                        ),
                                        1,
                                        1
                                )
                        )
                        .costAName("refined_essence")
                        .noCostB()
                        .coinsResult(2, 4)
                        .maxUses(64)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costA(
                                DwarfTradeRecipeBuilder.cost(
                                        DataComponentIngredient.of(
                                                false,
                                                essenceStack(EssenceType.EXALTED)
                                        ),
                                        1,
                                        1
                                )
                        )
                        .costAName("exalted_essence")
                        .noCostB()
                        .coinsResult(3, 6)
                        .maxUses(64)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );


        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costA(
                                DwarfTradeRecipeBuilder.cost(
                                        DataComponentIngredient.of(
                                                false,
                                                essenceStack(EssenceType.CORRUPTED)
                                        ),
                                        1,
                                        1
                                )
                        )
                        .costAName("corrupted_essence")
                        .noCostB()
                        .coinsResult(4, 7)
                        .maxUses(64)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.JOURNEYMAN
                        )
                        .costACoins(
                                3,
                                5
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_PESTLE
                        )
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(
                                12,
                                25
                        )
                        .noCostB()
                        .rewardCrateResult(RewardCrateType.ALCHEMY_SUPPLIES)
                        .maxUses(1)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        PROFESSION,
                        DwarfMerchantData.Level.MASTER,
                        DwarfLoreKey.ALCHEMY_RECIPES,
                        25,
                        35,
                        80,
                        120
                )
        );
    }

    private static ItemStack essenceStack(
            EssenceType type
    ) {
        return JolCraftItems.ESSENCE.get().createStack(type);
    }
}