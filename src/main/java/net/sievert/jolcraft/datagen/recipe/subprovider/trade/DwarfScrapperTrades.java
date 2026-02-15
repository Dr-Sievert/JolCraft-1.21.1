package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.datagen.recipe.subprovider.trade.util.AbstractDwarfTrades;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfScrapperTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.SCRAPPER;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        // =========================================================
        // MAIN TRADES (baseline, always present)
        // =========================================================

        mainTrade(
                p,
                NOVICE,
                coins(8, 15),
                Optional.empty(),
                itemResult(JolCraftItems.COPPER_SPANNER.get(), 1),
                3, 10, 0.05F,
                buy(JolCraftItems.COPPER_SPANNER.get())
        );

        mainTrade(
                p,
                APPRENTICE,
                cost(JolCraftItems.SCRAP.get(), 1),
                Optional.empty(),
                coinsResult(1),
                256, 5, 0.05F,
                sell(JolCraftItems.SCRAP.get())
        );

        mainTrade(
                p,
                JOURNEYMAN,
                coins(24, 32),
                Optional.empty(),
                itemResult(JolCraftItems.IRON_SPANNER.get(), 1),
                3, 40, 0.05F,
                buy(JolCraftItems.IRON_SPANNER.get())
        );

        mainTrade(
                p,
                EXPERT,
                cost(JolCraftItems.SCRAP_HEAP.get(), 1),
                Optional.empty(),
                coinsResult(4, 7),
                50, 4, 0.05F,
                sell(JolCraftItems.SCRAP_HEAP.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(1, 15),
                Optional.of(cost(JolCraftItems.SCRAP_HEAP.get(), 1)),
                itemResult(JolCraftItems.RUSTAGATE.get(), 1),
                3, 0, 0.05F,
                buyFor(JolCraftItems.SCRAP_HEAP.get(), JolCraftItems.RUSTAGATE.get())
        );

        // =========================================================
        // SALVAGE POOL (random offers)
        // =========================================================

        // NOVICE
        pooledSalvage(p, NOVICE, JolCraftItems.EXPIRED_POTION.get(), 1, 3, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.OLD_FABRIC.get(), 1, 3, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.BROKEN_PICKAXE.get(), 1, 4, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.BROKEN_AMULET.get(), 1, 4, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.BROKEN_BELT.get(), 1, 4, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.BROKEN_COINS.get(), 1, 4, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.RUSTY_TONGS.get(), 1, 4, 1);
        pooledSalvage(p, NOVICE, JolCraftItems.INGOT_MOULD.get(), 1, 4, 1);

        // APPRENTICE
        pooledSalvage(p, APPRENTICE, JolCraftItems.DEEPSLATE_MUG.get(), 3, 5, 3);
        pooledSalvage(p, APPRENTICE, JolCraftItems.BROKEN_TABLET.get(), 3, 5, 3);

        // JOURNEYMAN
        pooledSalvage(p, JOURNEYMAN, JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), 3, 5, 3);
        pooledSalvage(p, JOURNEYMAN, JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), 3, 5, 3);
        pooledSalvage(p, JOURNEYMAN, JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), 3, 5, 3);

        // MASTER (mithril gate)
        pooledSalvage(p, MASTER, JolCraftItems.MITHRIL_SCRAP.get(), 5, 10, 5);
        pooledSalvage(p, MASTER, JolCraftItems.BROKEN_MITHRIL_PLATE.get(), 5, 10, 5);
        pooledSalvage(p, MASTER, JolCraftItems.BROKEN_MITHRIL_SWORD.get(), 5, 10, 5);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void pooledSalvage(
            AbstractRecipeProvider p,
            Level level,
            ItemLike item,
            int minGold,
            int maxGold,
            int villagerXp
    ) {
        pooledTrade(
                p,
                level,
                DwarfTradeRecipe.TradePool.POOL,
                1,
                cost(item, 1),
                Optional.empty(),
                coinsResult(minGold, maxGold),
                5,
                villagerXp,
                0.05F,
                sell(item),
                Optional.empty()
        );
    }
}