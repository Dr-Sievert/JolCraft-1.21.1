package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.lore.util.LoreHelper;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfArtisanTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.ARTISAN;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        sellGem(p, JolCraftItems.AEGISCORE.get());
        sellGem(p, JolCraftItems.ASHFANG.get());
        sellGem(p, JolCraftItems.DEEPMARROW.get());
        sellGem(p, JolCraftItems.EARTHBLOOD.get());
        sellGem(p, JolCraftItems.EMBERGLASS.get());
        sellGem(p, JolCraftItems.FROSTVEIN.get());
        sellGem(p, JolCraftItems.GRIMSTONE.get());
        sellGem(p, JolCraftItems.IRONHEART.get());
        sellGem(p, JolCraftItems.LUMIERE.get());
        sellGem(p, JolCraftItems.MOONSHARD.get());
        sellGem(p, JolCraftItems.RUSTAGATE.get());
        sellGem(p, JolCraftItems.SKYBURROW.get());
        sellGem(p, JolCraftItems.SUNGLEAM.get());
        sellGem(p, JolCraftItems.VERDANITE.get());
        sellGem(p, JolCraftItems.WOECRYSTAL.get());

        mainTrade(
                p,
                NOVICE,
                cost(Items.DIAMOND, 1),
                Optional.empty(),
                coinsResult(5, 9),
                5, 5, 0.05F,
                sell(Items.DIAMOND)
        );

        mainTrade(
                p,
                NOVICE,
                cost(Items.EMERALD, 1),
                Optional.empty(),
                coinsResult(4, 8),
                5, 5, 0.05F,
                sell(Items.EMERALD)
        );

        mainTrade(
                p,
                NOVICE,
                cost(Items.AMETHYST_SHARD, 2, 4),
                Optional.empty(),
                coinsResult(2, 5),
                5, 5, 0.05F,
                sell(Items.AMETHYST_SHARD)
        );

        mainTrade(
                p,
                NOVICE,
                cost(Items.LAPIS_LAZULI, 3, 5),
                Optional.empty(),
                coinsResult(2, 5),
                5, 5, 0.05F,
                sell(Items.LAPIS_LAZULI)
        );

        mainTrade(
                p,
                NOVICE,
                cost(Items.PRISMARINE_SHARD, 3, 5),
                Optional.empty(),
                coinsResult(2, 5),
                5, 5, 0.05F,
                sell(Items.PRISMARINE_SHARD)
        );

        mainTrade(
                p,
                NOVICE,
                cost(Items.QUARTZ, 3, 5),
                Optional.empty(),
                coinsResult(2, 5),
                5, 5, 0.05F,
                sell(Items.QUARTZ)
        );

        mainTrade(
                p,
                APPRENTICE,
                coins(10, 20),
                Optional.empty(),
                itemResult(JolCraftBlocks.LAPIDARY_BENCH.get().asItem(), 1),
                Hooks.EMPTY,
                3, 10, 0.05F,
                buy(JolCraftBlocks.LAPIDARY_BENCH.get().asItem())
        );

        mainTrade(
                p,
                JOURNEYMAN,
                coins(2, 4),
                Optional.empty(),
                itemResult(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get(), 1),
                Hooks.EMPTY,
                3, 10, 0.05F,
                buy(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get())
        );

        mainTrade(
                p,
                EXPERT,
                coins(2, 4),
                Optional.empty(),
                itemResult(JolCraftItems.DEEPSLATE_CHISEL.get(), 1),
                Hooks.EMPTY,
                3, 10, 0.05F,
                buy(JolCraftItems.DEEPSLATE_CHISEL.get())
        );

        mainTrade(
                p,
                MASTER,
                coins(30),
                Optional.of(cost(JolCraftItems.LEGENDARY_PAGE.get(), 20)),
                itemResult(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 1),
                hooksWithPatch(DataComponentPatch.builder()
                        .set(
                                JolCraftDataComponents.LORE_KEY.get(),
                                LoreHelper.toLoreKeyString(DwarfLoreKey.ANCIENT_GEMCRAFT)
                        )
                        .build()
                ),
                1, 0, 0.0F,
                buyFor(JolCraftItems.LEGENDARY_PAGE.get(), JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get())
        );
    }

    private void sellGem(AbstractRecipeProvider p, ItemLike gem) {
        mainTrade(
                p,
                NOVICE,
                cost(gem, 1),
                Optional.empty(),
                coinsResult(8, 15),
                5, 10, 0.05F,
                sell(gem)
        );
    }
}