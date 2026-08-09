package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.entity.player.JolCraftStats;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.LoreAge;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreEntry;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.tooltip.UnidentifiedItem;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;

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
        return LanguageAttachmentHelper.knowsDwarvish(player);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);

        if (result.getResult() == InteractionResult.SUCCESS) {
            if (!level.isClientSide) {
                player.awardStat(JolCraftStats.DWARVEN_TOMES_IDENTIFIED.get());
            }
        }

        return result;
    }

    @Override
    protected ItemStack getRandomIdentifiedItem(ServerPlayer player, ItemStack original) {
        RandomSource rng = player.getRandom();
        Set<Rarity> allowed = Set.of(
                Rarity.COMMON,
                Rarity.UNCOMMON,
                Rarity.RARE,
                Rarity.EPIC
        );
        DwarfLoreEntry entry = LoreHelper.getRandomLoreEntry(
                rng,
                LoreAge.MODERN,
                DwarfLoreEntries.ALL.values(),
                allowed
        );
        if (entry == null) return ItemStack.EMPTY;
        Rarity rarity = entry.getRarity();
        ItemStack tome = switch (rarity) {
            case COMMON -> new ItemStack(JolCraftItems.DWARVEN_TOME_COMMON.get());
            case UNCOMMON -> new ItemStack(JolCraftItems.DWARVEN_TOME_UNCOMMON.get());
            case RARE -> new ItemStack(JolCraftItems.DWARVEN_TOME_RARE.get());
            case EPIC -> new ItemStack(JolCraftItems.DWARVEN_TOME_EPIC.get());
        };

        LoreHelper.setLoreKey(tome, entry.getKey());
        return tome;
    }

    @Override
    protected List<Component> getAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        boolean knowsLanguage = LanguageAttachmentHelper.knowsDwarvish(player);
        return List.of(knowsLanguage
                        ? Component.translatable(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED).withStyle(ChatFormatting.GRAY)
                        : Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_LOCKED).withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    protected List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        boolean knowsLanguage = LanguageAttachmentHelper.knowsDwarvish(player);
        return List.of(knowsLanguage
                        ? Component.translatable(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED_DWARVEN_TOME).withStyle(ChatFormatting.GRAY)
                        : Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_LOCKED).withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    protected Component getIdentifySuccessMessage(ServerPlayer player, ItemStack identified) {
        return Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS).withStyle(ChatFormatting.GREEN);
    }

    @Override
    protected Component getIdentifyFailMessage(ServerPlayer player) {
        return Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL).withStyle(ChatFormatting.RED);
    }

    @Override
    protected void playIdentifySuccessSound(Level level, Player player) {
        PlaySound.bookPageTurn(player);
    }

    @Override
    protected void playIdentifyFailSound(Level level, Player player) {
        PlaySound.bookPut(player);
    }
}