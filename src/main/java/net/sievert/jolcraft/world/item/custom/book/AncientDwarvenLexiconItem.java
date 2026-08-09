package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.entity.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageType;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.custom.tooltip.AncientItemBase;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }

        boolean knowsDwarvish = LanguageAttachmentHelper.knowsDwarvish(serverPlayer);
        if (!knowsDwarvish) {
            serverPlayer.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_READ)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            PlaySound.bookPut(player);
            return InteractionResultHolder.success(stack);
        }

        if (LanguageAttachmentHelper.knowsAncientDwarvishBypassCreative(serverPlayer)) {
            serverPlayer.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS_ANCIENT_DWARVEN_LANGUAGE)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            PlaySound.bookPut(player);
            return InteractionResultHolder.success(stack);
        }


        LanguageAttachmentHelper.grantAncientDwarvish(serverPlayer);
        JolCraftCriteriaTriggers.KNOWS_LANGUAGE.trigger(serverPlayer, LanguageType.ANCIENT_DWARVEN);

        JolCraftSoundHelper.player(player, SoundEvents.BOOK_PAGE_TURN, 2.0F, 0.7F);
        PlaySound.levelUp(player);
        serverPlayer.displayClientMessage(
                Component.translatable(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE)
                        .withStyle(ChatFormatting.GREEN),
                true
        );

        return InteractionResultHolder.success(stack);
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
