package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfAlchemistTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.ALCHEMIST;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(
                p,
                NOVICE,
                coins(4, 7),
                Optional.empty(),
                itemResult(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(), 1, Hooks.EMPTY),
                6, 0, 0.05F,
                buy(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get())
        );

        mainTrade(
                p,
                NOVICE,
                coins(1, 4),
                Optional.empty(),
                itemResult(JolCraftItems.DEEPSLATE_PESTLE.get(), 1, Hooks.EMPTY),
                6, 0, 0.05F,
                buy(JolCraftItems.DEEPSLATE_PESTLE.get())
        );
    }
}