package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.entity.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageType;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarvenLexiconItem extends Item {

    public DwarvenLexiconItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!LanguageAttachmentHelper.knowsDwarvishBypassCreative(serverPlayer)) {
                LanguageAttachmentHelper.grantDwarvish(serverPlayer);
                JolCraftCriteriaTriggers.KNOWS_LANGUAGE.trigger(serverPlayer, LanguageType.DWARVEN);
                PlaySound.bookPageTurn(player);
                PlaySound.levelUp(player);
                serverPlayer.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_USE)
                                .withStyle(ChatFormatting.GREEN),
                        true
                );
            } else {
                serverPlayer.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_KNOWS_DWARVEN_LANGUAGE)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                PlaySound.bookPut(player);
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        boolean knows = LanguageAttachmentHelper.knowsDwarvish(player);

        if (knows) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_UNLOCKED)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_LOCKED)
                    .withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}