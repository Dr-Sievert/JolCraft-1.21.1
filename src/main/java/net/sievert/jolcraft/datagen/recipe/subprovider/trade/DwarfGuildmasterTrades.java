package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfGuildmasterTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.GUILDMASTER;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(
                p,
                NOVICE,
                coins(15),
                Optional.empty(),
                itemResult(JolCraftItems.REPUTATION_TABLET_0.get(), 1),
                5, 0, 0.0F,
                buy(JolCraftItems.REPUTATION_TABLET_0.get())
        );

        addContractTrade(p, NOVICE, JolCraftItems.CONTRACT_HISTORIAN.get());
        addContractTrade(p, NOVICE, JolCraftItems.CONTRACT_MERCHANT.get());
        addContractTrade(p, NOVICE, JolCraftItems.CONTRACT_SCRAPPER.get());

        addContractTrade(p, APPRENTICE, JolCraftItems.CONTRACT_BREWMASTER.get());
        addContractTrade(p, APPRENTICE, JolCraftItems.CONTRACT_GUARD.get());
        addContractTrade(p, APPRENTICE, JolCraftItems.CONTRACT_KEEPER.get());

        addContractTrade(p, JOURNEYMAN, JolCraftItems.CONTRACT_ARTISAN.get());
        addContractTrade(p, JOURNEYMAN, JolCraftItems.CONTRACT_EXPLORER.get());
        addContractTrade(p, JOURNEYMAN, JolCraftItems.CONTRACT_MINER.get());

        addContractTrade(p, EXPERT, JolCraftItems.CONTRACT_ARCANIST.get());
        addContractTrade(p, EXPERT, JolCraftItems.CONTRACT_ALCHEMIST.get());
        addContractTrade(p, EXPERT, JolCraftItems.CONTRACT_PRIEST.get());

        addContractTrade(p, MASTER, JolCraftItems.CONTRACT_BLACKSMITH.get());
        addContractTrade(p, MASTER, JolCraftItems.CONTRACT_CHAMPION.get());
        addContractTrade(p, MASTER, JolCraftItems.CONTRACT_SMELTER.get());
    }

    private void addContractTrade(AbstractRecipeProvider p, Level level, ItemLike contract) {
        mainTrade(
                p,
                level,
                coins(30),
                Optional.of(cost(JolCraftItems.CONTRACT_SIGNED.get(), 1)),
                itemResult(contract, 1),
                Hooks.EMPTY,
                1, 0, 0.05F,
                buyFor(JolCraftItems.CONTRACT_SIGNED.get(), contract)
        );
    }
}