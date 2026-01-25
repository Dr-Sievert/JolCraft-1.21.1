package net.sievert.jolcraft.world.item.custom.book;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientEffectHelper;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;

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
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(serverPlayer);
            boolean hasAncientMemory = AncientEffectHelper.hasAncientMemory(serverPlayer);

            if (!(knowsLanguage && hasAncientMemory)) {
                playIdentifyFailSound(level, player);
                if (!knowsLanguage) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.dwarven_tome.identify_fail").withStyle(ChatFormatting.RED), true
                    );
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.ancient_dwarven_tome.partial_understanding").withStyle(ChatFormatting.RED), true
                    );
                    playIdentifyFailSound(level, player);
                }
                return InteractionResult.SUCCESS;
            }

            ItemStack stack = player.getItemInHand(hand);
            DwarfLoreKey key = LoreHelper.getLoreKey(stack, DwarfLoreKey.class);

            switch (key) {
                case FORGOTTEN_BREW_FORMULAS -> {
                    if (DwarfLoreUnlockHelper.hasUnlockBypassCreative(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                        showEmptyUnlockMessage(player);
                        playIdentifyFailSound(level, player);
                    } else {
                        DwarfLoreUnlockHelper.grantUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS);
                        player.displayClientMessage(
                                Component.translatable("tooltip.jolcraft.tome_unlock.brew").withStyle(ChatFormatting.GREEN), true
                        );
                        playUnlockSounds(level, player);
                    }
                }
                case ANCIENT_GEMCRAFT -> {
                    if (DwarfLoreUnlockHelper.hasUnlockBypassCreative(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                        showEmptyUnlockMessage(player);
                        playIdentifyFailSound(level, player);
                    } else {
                        DwarfLoreUnlockHelper.grantUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT);
                        player.displayClientMessage(
                                Component.translatable("tooltip.jolcraft.tome_unlock.gems").withStyle(ChatFormatting.GREEN), true
                        );
                        playUnlockSounds(level, player);
                    }
                }
                default -> showEmptyUnlockMessage(player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    public static void showEmptyUnlockMessage(Player player) {
        player.displayClientMessage(
                Component.translatable("tooltip.jolcraft.tome_unlock.empty").withStyle(ChatFormatting.GRAY),
                true
        );
    }

    public static void playUnlockSounds(Level level, Player player) {
        BlockPos pos = player.blockPosition();
        level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, pos, JolCraftSounds.LEVEL_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    protected void playIdentifyFailSound(Level level, Player player) {
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS, 1.2f, 0.7f);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component customName = stack.getComponents().get(DataComponents.ITEM_NAME);
        assert customName != null;
        if (!customName.getString().isEmpty()) {
            return Component.literal(customName.getString()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
        }
        return Component.translatable(this.getDescriptionId()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
    }

    @Override
    protected List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        var dataComponentType = JolCraftDataComponents.LORE_KEY.get();
        String loreKey = stack.get(dataComponentType);
        if(loreKey != null){
            return List.of(Component.translatable("tooltip.jolcraft.legendary_ancient_dwarven_tome.shift").withStyle(ChatFormatting.GRAY));
        }
        return List.of(Component.translatable("tooltip.jolcraft.tome_unlock.empty").withStyle(ChatFormatting.GRAY));
    }


}

