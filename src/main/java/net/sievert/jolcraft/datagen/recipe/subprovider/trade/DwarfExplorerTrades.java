package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.client.compass.DialItemColor;
import net.sievert.jolcraft.world.item.util.compass.DeepslateCompassHelper;
import net.sievert.jolcraft.world.item.util.compass.StructureGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfExplorerTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.EXPLORER;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        mainTrade(
                p,
                NOVICE,
                coins(5, 10),
                Optional.empty(),
                itemResult(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(), 1),
                3, 0, 0.0F,
                buy(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
        );

        addDialTrade(p, NOVICE, StructureGroup.DWARVEN);
        addDialTrade(p, APPRENTICE, StructureGroup.ANCIENT);
    }

    private void addDialTrade(AbstractRecipeProvider p, Level level, StructureGroup group) {
        int color = DeepslateCompassHelper.getColor(group);

        mainTrade(
                p,
                level,
                coins(5),
                Optional.of(cost(Items.REDSTONE, 1)),
                itemResult(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(), 1),
                hooksWithPatch(
                        DataComponentPatch.builder()
                                .set(JolCraftDataComponents.STRUCTURE_GROUP.get(), group.id())
                                .set(JolCraftDataComponents.DIAL_COLOR.get(), new DialItemColor(color))
                                .build()
                ),
                3, 0, 0.0F,
                buyFor(Items.REDSTONE, JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())
        );
    }
}