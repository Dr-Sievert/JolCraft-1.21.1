package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.lore.LoreAge;
import net.sievert.jolcraft.data.lore.LoreRarity;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreEntry;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.data.lore.util.LoreHelper;
import org.jetbrains.annotations.NotNull;

public class UnidentifiedLegendaryAncientTomeItem extends UnidentifiedAncientTomeItem {
    public UnidentifiedLegendaryAncientTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemStack getRandomIdentifiedItem(@NotNull ServerPlayer player, @NotNull ItemStack original) {
        RandomSource rng = player.getRandom();
        DwarfLoreEntry entry = LoreHelper.getRandomLoreEntry(
                rng,
                LoreAge.ANCIENT,
                DwarfLoreEntries.ALL.values(),
                LoreRarity.LEGENDARY
        );
        if (entry == null) return ItemStack.EMPTY;

        ItemStack tome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
        LoreHelper.setLoreKey(tome, entry.getKey());
        return tome;
    }
}
