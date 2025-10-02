package net.sievert.jolcraft.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.sievert.jolcraft.item.custom.tooltip.UnidentifiedItem;
import net.sievert.jolcraft.data.util.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.entity.util.dwarf.DwarvenLoreHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class UnidentifiedDwarvenTomeItem extends UnidentifiedItem {
    public UnidentifiedDwarvenTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean hasAlt() {
        return true;
    }

    @Override
    protected boolean canIdentify(ServerPlayer player) {
        return DwarvenLanguageHelper.knowsDwarvish(player);
    }

    @Override
    protected ItemStack getRandomIdentifiedItem(ServerPlayer player, ItemStack original) {
        RandomSource rng = player.getRandom();
        String loreKey = DwarvenLoreHelper.getRandomKeyWeighted(rng, false);
        if (loreKey.isEmpty()) return ItemStack.EMPTY;

        DwarvenLoreHelper.LoreRarity rarity = DwarvenLoreHelper.getRarity(loreKey, false);
        ItemStack tome = switch (rarity) {
            case COMMON -> new ItemStack(JolCraftItems.DWARVEN_TOME_COMMON.get());
            case UNCOMMON -> new ItemStack(JolCraftItems.DWARVEN_TOME_UNCOMMON.get());
            case RARE -> new ItemStack(JolCraftItems.DWARVEN_TOME_RARE.get());
            case EPIC -> new ItemStack(JolCraftItems.DWARVEN_TOME_EPIC.get());
            case LEGENDARY -> ItemStack.EMPTY;
        };

        tome.set(JolCraftDataComponents.LORE_LINE_ID.get(), loreKey);
        return tome;
    }

    @Override
    protected List<Component> getAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvishClient();
        return List.of(
                knowsLanguage
                        ? Component.translatable("tooltip.jolcraft.unidentified").withStyle(ChatFormatting.GRAY)
                        : Component.translatable("tooltip.jolcraft.dwarven_tome.locked").withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    protected List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvishClient();
        return List.of(
                knowsLanguage
                        ? Component.translatable("tooltip.jolcraft.unidentified_dwarven_tome").withStyle(ChatFormatting.GRAY)
                        : Component.translatable("tooltip.jolcraft.dwarven_tome.locked").withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    protected Component getIdentifySuccessMessage(ServerPlayer player, ItemStack identified) {
        return Component.translatable("tooltip.jolcraft.dwarven_tome.identify_success").withStyle(ChatFormatting.GREEN);
    }

    @Override
    protected Component getIdentifyFailMessage(ServerPlayer player) {
        return Component.translatable("tooltip.jolcraft.dwarven_tome.identify_fail").withStyle(ChatFormatting.RED);
    }

    @Override
    protected void playIdentifySuccessSound(Level level, Player player) {
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.2f, 0.8f);
    }

    @Override
    protected void playIdentifyFailSound(Level level, Player player) {
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 1.2f, 0.8f);
    }
}
