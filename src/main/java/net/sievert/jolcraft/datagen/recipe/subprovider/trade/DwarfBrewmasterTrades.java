package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfBrewmasterTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.BREWMASTER;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        // NOVICE
        mainTrade(
                p,
                NOVICE,
                cost(JolCraftItems.GLASS_MUG.get(), 1, 2),
                Optional.empty(),
                coinsResult(1, 3),
                5, 2, 0.05F,
                sell(JolCraftItems.GLASS_MUG.get())
        );

        mainTrade(
                p,
                NOVICE,
                coins(1, 2),
                Optional.empty(),
                itemResult(Items.SUGAR, 1, 2, Hooks.EMPTY),
                10, 1, 0.05F,
                buy(Items.SUGAR)
        );

        // APPRENTICE
        mainTrade(
                p,
                APPRENTICE,
                coins(7, 12),
                Optional.empty(),
                itemResult(Items.CAULDRON, 1, Hooks.EMPTY),
                9, 10, 0.05F,
                buy(Items.CAULDRON)
        );

        mainTrade(
                p,
                APPRENTICE,
                cost(JolCraftItems.BARLEY_MALT.get(), 12, 22),
                Optional.empty(),
                coinsResult(1, 3),
                5, 1, 0.05F,
                sell(JolCraftItems.BARLEY_MALT.get())
        );

        // JOURNEYMAN
        sellHops(p, JolCraftItems.ASGARNIAN_HOPS.get());
        sellHops(p, JolCraftItems.DUSKHOLD_HOPS.get());
        sellHops(p, JolCraftItems.KRANDONIAN_HOPS.get());
        sellHops(p, JolCraftItems.YANILLIAN_HOPS.get());

        // EXPERT
        mainTrade(
                p,
                EXPERT,
                cost(JolCraftItems.DWARVEN_BREW.get(), 1, 5),
                Optional.empty(),
                coinsResult(6),
                5, 3, 0.05F,
                sell(JolCraftItems.DWARVEN_BREW.get())
        );

        mainTrade(
                p,
                EXPERT,
                coins(1, 2),
                Optional.empty(),
                itemResult(JolCraftItems.YEAST.get(), 3, 5, Hooks.EMPTY),
                5, 10, 0.05F,
                buy(JolCraftItems.YEAST.get())
        );

        // MASTER
        mainTrade(
                p,
                MASTER,
                coins(30),
                Optional.of(cost(JolCraftItems.LEGENDARY_PAGE.get(), 20)),
                itemResult(
                        JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(),
                        1,
                        hooksWithPatch(
                                DataComponentPatch.builder()
                                        .set(
                                                JolCraftDataComponents.LORE_KEY.get(),
                                                LoreHelper.toLoreKeyString(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)
                                        )
                                        .build()
                        )
                ),
                1, 0, 0.0F,
                buyFor(JolCraftItems.LEGENDARY_PAGE.get(), JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(20, 40),
                Optional.of(cost(JolCraftItems.EMBERGLASS_CUT.get(), 2)),
                itemResult(JolCraftBlocks.HEARTH.get().asItem(), 1, Hooks.EMPTY),
                1, 0, 0.0F,
                buyFor(JolCraftItems.EMBERGLASS_CUT.get(), JolCraftBlocks.HEARTH.get().asItem())
        );
    }

    private void sellHops(AbstractRecipeProvider p, net.minecraft.world.level.ItemLike hops) {
        mainTrade(
                p,
                JOURNEYMAN,
                cost(hops, 10, 20),
                Optional.empty(),
                coinsResult(1, 3),
                5, 1, 0.05F,
                sell(hops)
        );
    }
}