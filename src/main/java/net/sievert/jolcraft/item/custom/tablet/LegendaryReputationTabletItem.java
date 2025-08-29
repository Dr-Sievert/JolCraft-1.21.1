package net.sievert.jolcraft.item.custom.tablet;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LegendaryReputationTabletItem extends ReputationTabletItem {
    public LegendaryReputationTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
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
