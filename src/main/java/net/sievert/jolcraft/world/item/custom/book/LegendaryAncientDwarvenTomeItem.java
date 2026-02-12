package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.lore.util.LoreHelper;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientEffectHelper;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(serverPlayer);
        boolean hasAncientMemory = AncientEffectHelper.hasAncientMemory(serverPlayer);

        if (!knowsLanguage) {
            playIdentifyFailSound(player);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL).withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (!hasAncientMemory) {
            playIdentifyFailSound(player);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING).withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = player.getItemInHand(hand);
        DwarfLoreKey key = LoreHelper.getLoreKey(stack, DwarfLoreKey.class);

        if (key == null) {
            showEmptyUnlockMessage(player);
            playIdentifyFailSound(player);
            return InteractionResult.SUCCESS;
        }

        switch (key) {
            case FORGOTTEN_BREW_FORMULAS -> {
                if (DwarfLoreUnlockHelper.hasUnlockBypassCreative(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                    showEmptyUnlockMessage(player);
                    playIdentifyFailSound(player);
                } else {
                    DwarfLoreUnlockHelper.addUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS);
                    player.displayClientMessage(
                            Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_BREW).withStyle(ChatFormatting.GREEN),
                            true
                    );
                    playUnlockSounds(player);
                }
            }
            case ANCIENT_GEMCRAFT -> {
                if (DwarfLoreUnlockHelper.hasUnlockBypassCreative(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                    showEmptyUnlockMessage(player);
                    playIdentifyFailSound(player);
                } else {
                    DwarfLoreUnlockHelper.addUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT);
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

        return InteractionResult.SUCCESS;
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
        var dataComponentType = JolCraftDataComponents.LORE_KEY.get();
        String loreKey = stack.get(dataComponentType);
        if(loreKey != null){
            return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT).withStyle(ChatFormatting.GRAY));
        }
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_EMPTY).withStyle(ChatFormatting.GRAY));
    }
}

