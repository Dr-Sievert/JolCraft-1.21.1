package net.sievert.jolcraft.item.custom.book;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.util.lore.LoreHelper;
import net.minecraft.ChatFormatting;
import net.sievert.jolcraft.data.util.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.data.util.lore.dwarf.DwarfLoreEntry;
import net.sievert.jolcraft.data.util.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.item.custom.tooltip.AncientItemBase;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class AncientDwarvenTomeItem extends AncientItemBase {
    public AncientDwarvenTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean hasAlt() {
        return true;
    }

    @Override
    protected @NotNull List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.dwarven_tome.shift").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.dwarven_tome.locked").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.partial_understanding")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        String text = LoreHelper.getEntryText(stack, DwarfLoreEntries.ALL);
        return List.of(
                (text != null)
                        ? Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        : Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.unlocked").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        );
    }

    @Override
    protected @NotNull List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        String text = LoreHelper.getEntryText(stack, DwarfLoreEntries.ALL);
        return List.of(
                (text != null)
                        ? Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        : Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.unlocked").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        );
    }
}
