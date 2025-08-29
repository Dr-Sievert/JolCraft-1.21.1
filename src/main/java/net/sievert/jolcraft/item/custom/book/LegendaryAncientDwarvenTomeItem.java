package net.sievert.jolcraft.item.custom.book;

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
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.util.attachment.AncientEffectHelper;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.util.attachment.TomeUnlockHelper;

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
            // === GATE: Language + Ancient Memory ===
            boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(serverPlayer);
            boolean hasAncientMemory = AncientEffectHelper.hasAncientMemory(serverPlayer);

            if (!(knowsLanguage && hasAncientMemory)) {
                // Gate failed: Play fail sound and display message, just like AncientUnidentifiedTomeItem
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

            // === PASSED: Now run Legendary unlock logic ===
            ItemStack stack = player.getItemInHand(hand);
            String loreLineId = stack.getComponents().getOrDefault(JolCraftDataComponents.LORE_LINE_ID.get(), null);

            switch (loreLineId) {
                case "forgotten_brew_formulas" -> {
                    if (TomeUnlockHelper.hasUnlockBypassCreative(player, TomeUnlockHelper.BREW_MULTIPLE_HOPS)) {
                        showEmptyUnlockMessage(player);
                        playIdentifyFailSound(level, player);
                    } else {
                        TomeUnlockHelper.grantUnlock(player, TomeUnlockHelper.BREW_MULTIPLE_HOPS);
                        player.displayClientMessage(
                                Component.translatable("tooltip.jolcraft.tome_unlock.brew").withStyle(ChatFormatting.GREEN), true
                        );
                        playUnlockSounds(level, player);
                    }
                }
                case "ancient_gemcraft" -> {
                    if (TomeUnlockHelper.hasUnlockBypassCreative(player, TomeUnlockHelper.CUTTING_GEMS)) {
                        showEmptyUnlockMessage(player);
                        playIdentifyFailSound(level, player);
                    } else {
                        TomeUnlockHelper.grantUnlock(player, TomeUnlockHelper.CUTTING_GEMS);
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
        // If the item has a custom name, use it, but always force gold color
        Component customName = stack.getComponents().getOrDefault(DataComponents.ITEM_NAME, null);
        if (!customName.getString().isEmpty()) {
            // .withStyle replaces *only* the color, but preserves other formatting
            return Component.literal(customName.getString()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
        }
        // Otherwise, use the default name, also gold
        return Component.translatable(this.getDescriptionId()).withStyle(style -> style.withColor(ChatFormatting.GOLD));
    }

    @Override
    protected List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        var dataComponentType = JolCraftDataComponents.LORE_LINE_ID.get();
        String loreKey = stack.get(dataComponentType);
        if(loreKey != null){
            return List.of(Component.translatable("tooltip.jolcraft.legendary_ancient_dwarven_tome.shift").withStyle(ChatFormatting.GRAY));
        }
        return List.of(Component.translatable("tooltip.jolcraft.tome_unlock.empty").withStyle(ChatFormatting.GRAY));
    }


}

