package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.player.attachment.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.player.attachment.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LegendaryAncientDwarvenTomeItem extends AncientDwarvenTomeItem {
    public LegendaryAncientDwarvenTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }

        boolean knowsLanguage = LanguageAttachmentHelper.knowsDwarvish(serverPlayer);
        boolean knowsAncientLanguage = LanguageAttachmentHelper.knowsAncientDwarvish(serverPlayer);

        if (!knowsLanguage) {
            playIdentifyFailSound(player);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL).withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.success(stack);
        }

        if (!knowsAncientLanguage) {
            playIdentifyFailSound(player);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING).withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.success(stack);
        }

        DwarfLoreKey key = LoreHelper.getLoreKey(stack, DwarfLoreKey.class);

        if (key == null) {
            showEmptyUnlockMessage(player);
            playIdentifyFailSound(player);
            return InteractionResultHolder.success(stack);
        }

        switch (key) {
            case FORGOTTEN_BREW_FORMULAS -> {
                if (DwarfLoreAttachmentHelper.hasUnlockBypassCreative(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                    showEmptyUnlockMessage(player);
                    playIdentifyFailSound(player);
                } else {
                    DwarfLoreAttachmentHelper.addUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS);
                    player.displayClientMessage(
                            Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_BREW).withStyle(ChatFormatting.GREEN),
                            true
                    );
                    playUnlockSounds(player);
                }
            }
            case ANCIENT_GEMCRAFT -> {
                if (DwarfLoreAttachmentHelper.hasUnlockBypassCreative(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                    showEmptyUnlockMessage(player);
                    playIdentifyFailSound(player);
                } else {
                    DwarfLoreAttachmentHelper.addUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT);
                    player.displayClientMessage(
                            Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_GEMS).withStyle(ChatFormatting.GREEN),
                            true
                    );
                    playUnlockSounds(player);
                }
            }
            default -> {
                showEmptyUnlockMessage(player);
                playIdentifyFailSound(player);
            }
        }

        return InteractionResultHolder.success(stack);
    }

    public static void showEmptyUnlockMessage(Player player) {
        player.displayClientMessage(
                Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_EMPTY).withStyle(ChatFormatting.GRAY),
                true
        );
    }

    public static void playUnlockSounds(Player player) {
        PlaySound.bookPageTurn(player);
        PlaySound.levelUp(player);
    }

    protected void playIdentifyFailSound(Player player) {
        PlaySound.bookPut(player);
    }

    @Override
    protected List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        var dataComponentType = JolCraftDataComponents.DWARF_LORE_KEY.get();
        String loreKey = stack.get(dataComponentType);
        if(loreKey != null){
            return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT).withStyle(ChatFormatting.GRAY));
        }
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_EMPTY).withStyle(ChatFormatting.GRAY));
    }
}

