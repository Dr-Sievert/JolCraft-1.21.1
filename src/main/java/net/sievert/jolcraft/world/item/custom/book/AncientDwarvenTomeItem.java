package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.lore.util.LoreHelper;
import net.minecraft.ChatFormatting;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.custom.tooltip.AncientItemBase;
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
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_SHIFT).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_LOCKED).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        String translationKey = LoreHelper.getEntryTranslationKey(stack, DwarfLoreKey.class);
        return List.of((translationKey != null)
                        ? Component.translatable(translationKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        : Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        );
    }

    @Override
    protected @NotNull List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        String translationKey = LoreHelper.getEntryTranslationKey(stack, DwarfLoreKey.class);
        return List.of((translationKey != null)
                        ? Component.translatable(translationKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                        : Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        );
    }

}
