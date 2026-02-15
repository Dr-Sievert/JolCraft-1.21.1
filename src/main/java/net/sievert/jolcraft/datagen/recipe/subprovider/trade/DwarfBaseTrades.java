package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.recipe.subprovider.trade.util.AbstractDwarfTrades;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfBaseTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.NONE;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(
                p,
                NOVICE,
                coins(1, 4),
                Optional.empty(),
                itemResult(Items.STICK, 2, 8),
                6, 500, 0.05F,
                buy(Items.STICK)
        );

        mainTrade(
                p,
                MASTER,
                coins(30),
                Optional.of(cost(Items.PURPLE_DYE, 1)),
                itemResult(JolCraftItems.GUILD_SIGIL.get(), 1),
                1, 0, 0.05F,
                buyFor(Items.PURPLE_DYE, JolCraftItems.GUILD_SIGIL.get())
        );
    }
}