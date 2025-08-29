package net.sievert.jolcraft.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.util.dwarf.DwarvenLoreHelper;
import org.jetbrains.annotations.NotNull;

public class LegendaryAncientUnidentifiedTomeItem extends AncientUnidentifiedTomeItem{
    public LegendaryAncientUnidentifiedTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemStack getRandomIdentifiedItem(ServerPlayer player, ItemStack original) {
        RandomSource rng = player.getRandom();
        String loreKey = DwarvenLoreHelper.getRandomLegendaryKey(rng); // Only legendary!
        if (loreKey.isEmpty()) return ItemStack.EMPTY;

        ItemStack tome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
        tome.set(JolCraftDataComponents.LORE_LINE_ID.get(), loreKey);
        return tome;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        // If the item has a custom name, use it, but always force gold color
        Component customName = stack.getComponents().getOrDefault(DataComponents.ITEM_NAME, null);
        if (!customName.getString().isEmpty()) {
            // .withStyle replaces *only* the color, but preserves other formatting
            return Component.literal(customName.getString()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
        }
        // Otherwise, use the default name, also gold
        return Component.translatable(this.getDescriptionId()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
    }




}
