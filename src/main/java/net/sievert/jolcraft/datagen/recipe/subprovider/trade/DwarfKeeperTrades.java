package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfKeeperTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.KEEPER;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(
                p,
                NOVICE,
                coins(1, 2),
                Optional.empty(),
                itemResult(JolCraftItems.BARLEY_SEEDS.get(), 1, 3, Hooks.EMPTY),
                10, 1, 0.05F,
                buy(JolCraftItems.BARLEY_SEEDS.get())
        );

        mainTrade(
                p,
                APPRENTICE,
                cost(JolCraftItems.BARLEY.get(), 15, 22),
                Optional.empty(),
                coinsResult(1, 2),
                10, 25, 0.05F,
                sell(JolCraftItems.BARLEY.get())
        );

        mainTrade(
                p,
                JOURNEYMAN,
                cost(JolCraftItems.MUFFHORN_FUR.get(), 1),
                Optional.empty(),
                coinsResult(2, 4),
                15, 5, 0.05F,
                sell(JolCraftItems.MUFFHORN_FUR.get())
        );

        mainTrade(
                p,
                JOURNEYMAN,
                cost(JolCraftItems.MUFFHORN_MILK_BUCKET.get(), 1),
                Optional.empty(),
                coinsResult(5),
                10, 30, 0.05F,
                sell(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
        );

        mainTrade(
                p,
                EXPERT,
                cost(JolCraftItems.DEEPSLATE_BULBS.get(), 1, 2),
                Optional.empty(),
                coinsResult(3, 5),
                10, 30, 0.05F,
                sell(JolCraftItems.DEEPSLATE_BULBS.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(2, 5),
                Optional.empty(),
                itemResult(Items.BONE_MEAL, 3, 5, Hooks.EMPTY),
                5, 1, 0.05F,
                buy(Items.BONE_MEAL)
        );

        mainTrade(
                p,
                MASTER,
                coins(5, 9),
                Optional.empty(),
                itemResult(JolCraftItems.DEEPSLATE_BULBS.get(), 1, 5, Hooks.EMPTY),
                3, 0, 0.05F,
                buy(JolCraftItems.DEEPSLATE_BULBS.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(5),
                Optional.empty(),
                itemResult(JolCraftItems.ASGARNIAN_SEEDS.get(), 1, Hooks.EMPTY),
                3, 0, 0.05F,
                buy(JolCraftItems.ASGARNIAN_SEEDS.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(5),
                Optional.empty(),
                itemResult(JolCraftItems.DUSKHOLD_SEEDS.get(), 1, Hooks.EMPTY),
                3, 0, 0.05F,
                buy(JolCraftItems.DUSKHOLD_SEEDS.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(5),
                Optional.empty(),
                itemResult(JolCraftItems.KRANDONIAN_SEEDS.get(), 1, Hooks.EMPTY),
                3, 0, 0.05F,
                buy(JolCraftItems.KRANDONIAN_SEEDS.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(5),
                Optional.empty(),
                itemResult(JolCraftItems.YANILLIAN_SEEDS.get(), 1, Hooks.EMPTY),
                3, 0, 0.05F,
                buy(JolCraftItems.YANILLIAN_SEEDS.get())
        );
    }
}