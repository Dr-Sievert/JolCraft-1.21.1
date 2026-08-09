package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
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
import net.sievert.jolcraft.world.item.custom.tooltip.AncientUnidentifiedItem;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class UnidentifiedAncientTomeItem extends AncientUnidentifiedItem {

    public UnidentifiedAncientTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean hasAlt() {
        return true;
    }

    @Override
    protected boolean canIdentify(ServerPlayer player) {
        return hasRequiredLanguage(player) && LanguageAttachmentHelper.knowsAncientDwarvish(player);
    }

    @Override
    protected boolean hasRequiredLanguage(ServerPlayer player) {
        return LanguageAttachmentHelper.knowsDwarvish(player);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
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
        DwarfLoreEntry entry = LoreHelper.getRandomLoreEntry(
                rng,
                LoreAge.ANCIENT,
                DwarfLoreEntries.ALL.values()
        );
        if (entry == null) return ItemStack.EMPTY;

        Rarity rarity = entry.getRarity();
        var LEGENDARY = JolCraftEnumExtensions.Rarity.LEGENDARY.getValue();

        ItemStack tome = (rarity == LEGENDARY)
                ? new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get())
                : switch (rarity) {
            case COMMON -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get());
            case UNCOMMON -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get());
            case RARE -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get());
            case EPIC -> new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get());
        };

        LoreHelper.setLoreKey(tome, entry.getKey());
        return tome;
    }

    @Override
    protected @NotNull List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_LOCKED).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    protected @NotNull List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED_ANCIENT_DWARVEN_TOME).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected @NotNull List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED_ANCIENT_DWARVEN_TOME).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected Component getIdentifySuccessMessage(ServerPlayer player, ItemStack identified) {
        return Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS).withStyle(ChatFormatting.GREEN);
    }

    @Override
    protected Component getFailMessageMissingLanguage(ServerPlayer player) {
        return Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL).withStyle(ChatFormatting.RED);
    }

    @Override
    protected Component getFailMessageMissingEffect(ServerPlayer player) {
        return Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING).withStyle(ChatFormatting.RED);
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
