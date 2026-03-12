package net.sievert.jolcraft.world.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyData;
import net.sievert.jolcraft.world.item.util.tooltip.TooltipHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBountyTaskItem extends Item implements IItemExtension {

    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F);  // Green (Completed)
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);     // Red (In Progress)

    protected AbstractBountyTaskItem(Properties properties) {
        super(properties);
    }

    // ---------------------------------------------------------------------
    // Hooks (implemented by subclasses)
    // ---------------------------------------------------------------------

    /** Tooltip line shown when the player does not know Dwarvish (e.g. parchment locked vs crate locked). */
    protected abstract String lockedTooltipKey();

    /** If true, Alt tooltip is enabled and uses {@link #altTooltipKey(ItemStack)}. */
    protected boolean supportsAltTooltip(ItemStack stack) {
        return false;
    }

    /** Tooltip line shown on Alt (only used if {@link #supportsAltTooltip(ItemStack)} returns true). */
    protected String altTooltipKey(ItemStack stack) {
        return "";
    }

    /** Whether to show the "Hold Alt" hint line at the bottom. */
    protected boolean showHoldKeyHint(ItemStack stack) {
        return true;
    }

    /**
     * Adds the header lines (type/tier/etc) after language lock passes.
     * IMPORTANT: This is called regardless of whether BOUNTY_DATA exists.
     */
    @OnlyIn(Dist.CLIENT)
    protected abstract void appendHeaderLines(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag);

    /**
     * Adds descriptive line(s) when task data is missing/invalid.
     * NOTE: Subclass should decide whether missing data is actually invalid.
     */
    @OnlyIn(Dist.CLIENT)
    protected abstract void appendInvalidLines(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag);

    // ---------------------------------------------------------------------
    // Component helpers
    // ---------------------------------------------------------------------

    @Nullable
    protected final BountyData getBountyDataOrNull(ItemStack stack) {
        return stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
    }

    protected final int getFill(ItemStack stack) {
        return stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
    }

    protected final boolean isCompleteFlag(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()));
    }

    protected final int getRequiredAmountOrZero(ItemStack stack) {
        BountyData data = getBountyDataOrNull(stack);
        if (data == null) return 0;

        return switch (data.objective()) {
            case BountyData.BountyObjective.ItemObjective(var ignored, int amount) -> amount;
            case BountyData.BountyObjective.EntityObjective(var ignored, int amount) -> amount;
        };
    }

    // ---------------------------------------------------------------------
    // Bar (shared)
    // ---------------------------------------------------------------------

    @Override
    public final boolean isBarVisible(ItemStack stack) {
        int required = getRequiredAmountOrZero(stack);
        if (required <= 0) return false;

        int fill = getFill(stack);
        return fill > 0 || isCompleteFlag(stack);
    }

    @Override
    public final int getBarWidth(ItemStack stack) {
        int required = getRequiredAmountOrZero(stack);
        if (required <= 0) return 0;

        int fill = getFill(stack);
        double progress = Math.min(1.0, (double) fill / (double) required);
        return Math.min(13, (int) (progress * 13.0));
    }

    @Override
    public final int getBarColor(ItemStack stack) {
        int required = getRequiredAmountOrZero(stack);
        if (required <= 0) return BAR_COLOR;

        int fill = getFill(stack);
        return (fill >= required || isCompleteFlag(stack)) ? FULL_BAR_COLOR : BAR_COLOR;
    }

    // ---------------------------------------------------------------------
    // Tooltip (shared)
    // ---------------------------------------------------------------------

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);

        if (!knowsLanguage) {
            tooltip.add(Component.translatable(lockedTooltipKey()).withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, context, tooltip, flag);
            return;
        }

        if (supportsAltTooltip(stack) && JolCraftProxy.access().isAltDown()) {
            String key = altTooltipKey(stack);
            if (!key.isBlank()) {
                tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
                return;
            }
        }

        appendHeaderLines(stack, context, tooltip, flag);

        BountyData data = getBountyDataOrNull(stack);
        if (data == null) {
            appendInvalidLines(stack, context, tooltip, flag);

            if (showHoldKeyHint(stack)) {
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, TooltipHelper.altKey())
                        .withStyle(ChatFormatting.DARK_GRAY));
            }

            super.appendHoverText(stack, context, tooltip, flag);
            return;
        }

        switch (data.objective()) {
            case BountyData.BountyObjective.ItemObjective(Holder<Item> item, int ignored) -> {
                Component itemName = item.value().getDefaultInstance().getHoverName();
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COLLECT, itemName)
                        .withStyle(ChatFormatting.GRAY));
            }
            case BountyData.BountyObjective.EntityObjective(Holder<EntityType<?>> entity, int ignored) -> {
                ResourceLocation id = entity.unwrapKey().map(ResourceKey::location).orElse(null);
                Component name = (id != null)
                        ? Component.translatable(id.toLanguageKey(JolCraftDictionary.ENTITY))
                        : Component.literal(JolCraftDictionary.UNKNOWN);

                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY, name)
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        int required = getRequiredAmountOrZero(stack);
        if (required > 0) {
            int fill = getFill(stack);

            tooltip.add(Component.literal(JolCraftStrings.slashed(String.valueOf(fill), String.valueOf(required)))
                    .withStyle(ChatFormatting.DARK_GRAY));

            if (fill >= required || isCompleteFlag(stack)) {
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_COMPLETE)
                        .withStyle(ChatFormatting.GREEN));
            }
        }

        if (showHoldKeyHint(stack)) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, TooltipHelper.altKey())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}