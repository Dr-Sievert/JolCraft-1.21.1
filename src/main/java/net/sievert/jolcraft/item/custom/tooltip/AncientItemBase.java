package net.sievert.jolcraft.item.custom.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.util.attachment.AncientEffectHelper;
import net.sievert.jolcraft.item.util.TooltipHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AncientItemBase extends Item {
    public AncientItemBase(Properties properties) {
        super(properties);
    }

    protected boolean hasAlt() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public final void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (context.level() != null && Objects.requireNonNull(context.level()).isClientSide()) {
            Player player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {
                if (net.minecraft.client.gui.screens.Screen.hasAltDown() && hasAlt()) {
                    if (AncientEffectHelper.hasAncientMemoryClient()) {
                        if (DwarvenLanguageHelper.knowsDwarvishClient()) {
                            tooltip.addAll(getFullyReadableTooltip(stack, player, tooltip, flag));
                        } else {
                            tooltip.addAll(getLockedTooltip(stack, player, tooltip, flag));
                        }
                    } else if (DwarvenLanguageHelper.knowsDwarvishClient()) {
                        tooltip.addAll(getPartialUnderstandingTooltip(stack, player, tooltip, flag));
                    } else {
                        tooltip.addAll(AncientEffectHelper.getAncientText(player,
                                getUnreadableTooltipSGA(stack, player, tooltip, flag)));
                    }

                    if (!AncientEffectHelper.hasAncientMemoryClient()) {
                        tooltip.add(Component.translatable("tooltip.jolcraft.need_ancient")
                                .withStyle(ChatFormatting.RED));
                    }
                    if(!DwarvenLanguageHelper.knowsDwarvishClient()) {
                        tooltip.add(Component.translatable("tooltip.jolcraft.need_lang")
                                .withStyle(ChatFormatting.RED));
                        tooltip.add(Component.translatable("tooltip.jolcraft.ancient_memory")
                                .withStyle(ChatFormatting.GRAY));
                    }
                } else {
                    if (AncientEffectHelper.hasAncientMemoryClient()) {
                        if (DwarvenLanguageHelper.knowsDwarvishClient()) {
                            tooltip.addAll(getNoAltTooltip(stack, player, tooltip, flag));
                        } else {
                            tooltip.addAll(getLockedTooltip(stack, player, tooltip, flag));
                        }
                    } else if (DwarvenLanguageHelper.knowsDwarvishClient()) {
                        tooltip.addAll(getPartialUnderstandingTooltip(stack, player, tooltip, flag));
                    } else {
                        tooltip.addAll(AncientEffectHelper.getAncientText(player,
                                getUnreadableTooltipSGA(stack, player, tooltip, flag)));
                    }
                    if(hasAlt()){
                        Component altKey = TooltipHelper.ALT_KEY;
                        tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", altKey)
                                .withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    /**
     * Subclass provides the fully readable tooltip for this item.
     */
    protected List<Component> getFullyReadableTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag) {
        return null;
    }

    /** Subclass provides lines shown when NOT holding Alt (before the "Hold Alt" line). */
    protected abstract List<Component> getNoAltTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag);

    /** Subclass provides the "locked" tooltip for this item (has memory, but not language). */
    protected abstract List<Component> getLockedTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag);

    /** Subclass provides the partial tooltip for this item (knows Dwarvish only). */
    protected abstract List<Component> getPartialUnderstandingTooltip(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag);

    /** Subclass provides the SGA-wrapped unreadable tooltip (knows nothing). */
    protected abstract List<Component> getUnreadableTooltipSGA(ItemStack stack, Player player, List<Component> tooltip, TooltipFlag flag);
}
