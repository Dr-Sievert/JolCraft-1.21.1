package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.custom.tooltip.AncientItemBase;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientEffectHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AncientDwarvenLexiconItem extends AncientItemBase {

    public AncientDwarvenLexiconItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            boolean knowsLang = DwarvenLanguageHelper.knowsDwarvish(serverPlayer);
            boolean hasEffect = AncientEffectHelper.hasAncientMemory(serverPlayer);
            boolean alreadyKnows = AncientDwarvenLanguageHelper.knowsAncientDwarvishBypassCreative(serverPlayer);

            if (!alreadyKnows && knowsLang && hasEffect) {
                AncientDwarvenLanguageHelper.setKnowsAncientDwarvish(serverPlayer, true);
                JolCraftSoundHelper.player(player, SoundEvents.BOOK_PAGE_TURN, 2.0F, 0.7F);
                PlaySound.levelUp(player);
                serverPlayer.displayClientMessage(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE)
                        .withStyle(ChatFormatting.GREEN), true);
            } else {
                if (!knowsLang) {
                    serverPlayer.displayClientMessage(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_READ)
                            .withStyle(ChatFormatting.RED), true);
                    PlaySound.bookPut(player);
                } else if (!hasEffect) {
                    serverPlayer.displayClientMessage(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_USE)
                            .withStyle(ChatFormatting.RED), true);
                    PlaySound.bookPut(player);
                } else {
                    serverPlayer.displayClientMessage(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS_ANCIENT_DWARVEN_LANGUAGE)
                            .withStyle(ChatFormatting.GRAY), true);
                    PlaySound.bookPut(player);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED).withStyle(ChatFormatting.GRAY));
    }
}
