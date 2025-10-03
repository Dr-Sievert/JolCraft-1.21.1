package net.sievert.jolcraft.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.custom.lore.LoreAge;
import net.sievert.jolcraft.data.custom.lore.LoreRarity;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreEntry;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import org.jetbrains.annotations.NotNull;

public class LegendaryAncientUnidentifiedTomeItem extends AncientUnidentifiedTomeItem{
    public LegendaryAncientUnidentifiedTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemStack getRandomIdentifiedItem(@NotNull ServerPlayer player, ItemStack original) {
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

    @Override
    public @NotNull Component getName(ItemStack stack) {
        Component customName = stack.getComponents().getOrDefault(DataComponents.ITEM_NAME, null);
        if (!customName.getString().isEmpty()) {
            return Component.literal(customName.getString()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
        }
        return Component.translatable(this.getDescriptionId()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
    }

}
