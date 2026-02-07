package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfHistorianTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.HISTORIAN;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(p, NOVICE, cost(JolCraftItems.DWARVEN_TOME_COMMON.get(), 1), Optional.empty(), coinsResult(3), 10, 5, 0.05F, sell(JolCraftItems.DWARVEN_TOME_COMMON.get()));
        mainTrade(p, NOVICE, cost(JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), 1), Optional.empty(), coinsResult(6), 10, 35, 0.05F, sell(JolCraftItems.DWARVEN_TOME_UNCOMMON.get()));
        mainTrade(p, NOVICE, cost(JolCraftItems.DWARVEN_TOME_RARE.get(), 1), Optional.empty(), coinsResult(10), 10, 75, 0.05F, sell(JolCraftItems.DWARVEN_TOME_RARE.get()));
        mainTrade(p, NOVICE, cost(JolCraftItems.DWARVEN_TOME_EPIC.get(), 1), Optional.empty(), coinsResult(22), 10, 125, 0.05F, sell(JolCraftItems.DWARVEN_TOME_EPIC.get()));

        mainTrade(p, NOVICE, coins(1, 2), Optional.empty(), itemResult(JolCraftItems.PARCHMENT.get(), 1, 3, Hooks.EMPTY), 6, 1, 0.05F, buy(JolCraftItems.PARCHMENT.get()));

        mainTrade(p, APPRENTICE, cost(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), 1), Optional.empty(), coinsResult(6), 10, 5, 0.05F, sell(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get()));
        mainTrade(p, APPRENTICE, cost(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), 1), Optional.empty(), coinsResult(8), 10, 35, 0.05F, sell(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get()));
        mainTrade(p, APPRENTICE, cost(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), 1), Optional.empty(), coinsResult(14), 10, 75, 0.05F, sell(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get()));
        mainTrade(p, APPRENTICE, cost(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), 1), Optional.empty(), coinsResult(28), 10, 125, 0.05F, sell(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get()));
        mainTrade(p, APPRENTICE, cost(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 1), Optional.empty(), coinsResult(35), 10, 250, 0.05F, sell(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get()));

        mainTrade(p, APPRENTICE, coins(2, 4), Optional.empty(), itemResult(JolCraftItems.CONTRACT_BLANK.get(), 1, 2, Hooks.EMPTY), 5, 1, 0.05F, buy(JolCraftItems.CONTRACT_BLANK.get()));

        mainTrade(p, JOURNEYMAN, coins(8), Optional.empty(), itemResult(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get(), 1, Hooks.EMPTY), 3, 1, 0.05F, buy(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get()));
        mainTrade(p, JOURNEYMAN, coins(1, 3), Optional.empty(), itemResult(JolCraftItems.QUILL_EMPTY.get(), 1, 2, Hooks.EMPTY), 6, 1, 0.05F, buy(JolCraftItems.QUILL_EMPTY.get()));

        mainTrade(p, EXPERT, coins(13), Optional.empty(), itemResult(JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), 1, Hooks.EMPTY), 3, 1, 0.05F, buy(JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get()));
        mainTrade(p, EXPERT, coins(3, 6), Optional.empty(), itemResult(Items.INK_SAC, 1, 2, Hooks.EMPTY), 6, 1, 0.05F, buy(Items.INK_SAC));

        buyLegendaryPages(p, MASTER, JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), 1);
        buyLegendaryPages(p, MASTER, JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), 2);
        buyLegendaryPages(p, MASTER, JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), 3);
        buyLegendaryPages(p, MASTER, JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), 4);
        buyLegendaryPages(p, MASTER, JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 5);

        mainTrade(
                p,
                MASTER,
                coins(15),
                Optional.of(cost(JolCraftItems.LEGENDARY_PAGE.get(), 10)),
                itemResult(JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), 1, Hooks.EMPTY),
                10, 0, 0.05F,
                buyFor(JolCraftItems.LEGENDARY_PAGE.get(), JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get())
        );

        buyLegendaryLoreTome(p, MASTER, 30, 20, DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE);
        buyLegendaryLoreTome(p, MASTER, 30, 20, DwarfLoreKey.COIN_PRESS_MANUAL);
    }

    private void buyLegendaryPages(AbstractRecipeProvider p, Level level, ItemLike ancientTome, int pages) {
        mainTrade(
                p,
                level,
                coins(pages),
                Optional.of(cost(ancientTome, 1)),
                itemResult(JolCraftItems.LEGENDARY_PAGE.get(), pages, Hooks.EMPTY),
                100, 0, 0.05F,
                buyFor(ancientTome, JolCraftItems.LEGENDARY_PAGE.get())
        );
    }

    private void buyLegendaryLoreTome(AbstractRecipeProvider p, Level level, int coinCount, int pageCount, DwarfLoreKey loreKey) {
        String lore = LoreHelper.toLoreKeyString(loreKey);

        mainTrade(
                p,
                level,
                coins(coinCount),
                Optional.of(cost(JolCraftItems.LEGENDARY_PAGE.get(), pageCount)),
                itemResult(
                        JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(),
                        1,
                        hooksWithPatch(
                                DataComponentPatch.builder()
                                        .set(JolCraftDataComponents.LORE_KEY.get(), lore)
                                        .build()
                        )
                ),
                1, 1, 0.0F,
                levelId(level) + "_buy_" + lore + "_tome"
        );
    }
}