package net.sievert.jolcraft.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.custom.tooltip.AncientUnidentifiedItem;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.util.attachment.AncientEffectHelper;
import net.sievert.jolcraft.util.dwarf.DwarvenLoreHelper;

import java.util.List;

public class AncientUnidentifiedTomeItem extends AncientUnidentifiedItem {
    public AncientUnidentifiedTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean hasAlt() {
        return true;
    }

    @Override
    protected boolean canIdentify(ServerPlayer player) {
        return hasRequiredLanguage(player)
                && AncientEffectHelper.hasAncientMemory(player);
    }

    @Override
    protected boolean hasRequiredLanguage(ServerPlayer player) {
        return DwarvenLanguageHelper.knowsDwarvish(player);
    }

    @Override
    protected ItemStack getRandomIdentifiedItem(ServerPlayer player, ItemStack original) {
        RandomSource rng = player.getRandom();
        String loreKey = DwarvenLoreHelper.getRandomKeyWeighted(rng, true); // ancient pool, excludes legendary
        if (loreKey.isEmpty()) return ItemStack.EMPTY;

        DwarvenLoreHelper.LoreRarity rarity = DwarvenLoreHelper.getRarity(loreKey, true);
        ItemStack tome = switch (rarity) {
            case COMMON -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get());
            case UNCOMMON -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get());
            case RARE -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get());
            case EPIC -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get());
            // Should never happen, but safe fallback:
            case LEGENDARY -> ItemStack.EMPTY;
        };
        tome.set(JolCraftDataComponents.LORE_LINE_ID.get(), loreKey);
        return tome;
    }

    @Override
    protected List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.unidentified").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.dwarven_tome.locked").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.partial_understanding")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    protected List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.unidentified").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.unidentified").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected Component getIdentifySuccessMessage(ServerPlayer player, ItemStack identified) {
        return Component.translatable("tooltip.jolcraft.dwarven_tome.identify_success").withStyle(ChatFormatting.GREEN);
    }

    @Override
    protected Component getFailMessageMissingLanguage(ServerPlayer player) {
        return Component.translatable("tooltip.jolcraft.dwarven_tome.identify_fail").withStyle(ChatFormatting.RED);
    }

    @Override
    protected Component getFailMessageMissingEffect(ServerPlayer player) {
        return Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.partial_understanding").withStyle(ChatFormatting.RED);
    }

    @Override
    protected void playIdentifySuccessSound(Level level, Player player) {
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.2f, 0.7f);
    }

    @Override
    protected void playIdentifyFailSound(Level level, Player player) {
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 1.2f, 0.7f);
    }
}
