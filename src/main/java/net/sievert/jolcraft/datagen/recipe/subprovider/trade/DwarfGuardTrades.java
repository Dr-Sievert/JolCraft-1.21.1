package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfGuardTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.GUARD;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(
                p,
                MASTER,
                cost(JolCraftItems.AEGISCORE.get(), 1),
                Optional.empty(),
                coinsResult(30),
                1, 0, 0.05F,
                sell(JolCraftItems.AEGISCORE.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(30),
                Optional.of(cost(JolCraftItems.AEGISCORE.get(), 1)),
                itemResult(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), 1, Hooks.EMPTY),
                1, 0, 0.05F,
                buyFor(JolCraftItems.AEGISCORE.get(), JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get())
        );
    }
}