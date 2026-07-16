package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public record DwarfBaseTrades(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.NONE;

    public DwarfBaseTrades(
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
        return JolCraftDictionary.BASE;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        emit(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costACoins(
                                1,
                                4
                        )
                        .noCostB()
                        .result(
                                Items.STICK,
                                2,
                                8
                        )
                        .maxUses(6)
                        .dwarfXp(500)
                        .priceMultiplier(0.05F)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.MASTER
                        )
                        .costACoins(
                                30,
                                30
                        )
                        .costB(
                                Items.PURPLE_DYE,
                                1
                        )
                        .result(
                                JolCraftItems.GUILD_SIGIL.get(),
                                1
                        )
                        .buildValidated()
        );
    }
}