package net.sievert.jolcraft.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.item.custom.tooltip.AncientItemBase;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.ClientboundAncientLanguagePacket;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.util.attachment.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.util.attachment.AncientEffectHelper;

import java.util.List;

public class AncientDwarvenLexiconItem extends AncientItemBase {

    public AncientDwarvenLexiconItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            boolean knowsLang = DwarvenLanguageHelper.knowsDwarvish(serverPlayer);
            boolean hasEffect = AncientEffectHelper.hasAncientMemoryServer(serverPlayer);
            boolean alreadyKnows = AncientDwarvenLanguageHelper.knowsAncientDwarvishServerBypassCreative(serverPlayer);

            if (!alreadyKnows && knowsLang && hasEffect) {
                AncientDwarvenLanguageHelper.setKnowsAncientDwarvishServer(serverPlayer, true);
                JolCraftNetworking.sendToClient(serverPlayer, new ClientboundAncientLanguagePacket(true));
                level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 2.0f, 0.7f);
                level.playSound(null, player.blockPosition(), JolCraftSounds.LEVEL_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                serverPlayer.displayClientMessage(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.use")
                        .withStyle(ChatFormatting.GREEN), true);
            } else {
                // ---- Precedence: language before effect ----
                if (!knowsLang) {
                    serverPlayer.displayClientMessage(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.cant_read")
                            .withStyle(ChatFormatting.RED), true);
                    level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 1.2f, 0.8f);
                } else if (!hasEffect) {
                    serverPlayer.displayClientMessage(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.cant_use")
                            .withStyle(ChatFormatting.RED), true);
                    level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 1.2f, 0.8f);
                } else {
                    serverPlayer.displayClientMessage(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.knows")
                            .withStyle(ChatFormatting.GRAY), true);
                    level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 1.2f, 0.8f);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    // ===== Tooltip Providers =====

    @Override
    protected List<Component> getNoShiftTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.unlocked").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.locked").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.partial_understanding")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        // This just returns the same as fully readable, the base will SGA-ify it if needed.
        return List.of(Component.translatable("tooltip.jolcraft.ancient_dwarven_lexicon.unlocked").withStyle(ChatFormatting.GRAY));
    }
}
