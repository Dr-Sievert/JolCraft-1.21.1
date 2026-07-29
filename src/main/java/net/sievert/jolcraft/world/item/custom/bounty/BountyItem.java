package net.sievert.jolcraft.world.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.player.attachment.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.world.item.component.custom.BountyData;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class BountyItem extends AbstractBountyTaskItem {

    public BountyItem(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull String lockedTooltipKey() {
        return JolCraftLanguageKeys.TOOLTIP_PARCHMENT_LOCKED;
    }

    @Override
    protected boolean supportsAltTooltip(ItemStack stack) {
        BountyData data = getBountyDataOrNull(stack);
        return data != null;
    }

    @Override
    protected @NotNull String altTooltipKey(ItemStack stack) {
        BountyData data = getBountyDataOrNull(stack);

        if (data != null && data.objective() instanceof BountyData.BountyObjective.EntityObjective) {
            return JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY_ALT;
        }

        return JolCraftLanguageKeys.TOOLTIP_BOUNTY_DWARF_PROFESSION;
    }

    @Override
    protected boolean showHoldKeyHint(ItemStack stack) {
        return supportsAltTooltip(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void appendHeaderLines(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        DwarfProfession type = BountyRecipe.getType(stack);
        DwarfMerchantData.Level tier = BountyRecipe.getTier(stack);

        if (type != null) {
            tooltip.add(
                    Component.translatable(
                                    JolCraftLanguageKeys.TOOLTIP_BOUNTY_TYPE,
                                    Component.translatable(AbstractLanguageKeys.entity(type.getId()))
                            )
                            .withStyle(ChatFormatting.GRAY)
            );
        }

        if (tier != null) {
            tooltip.add(
                    Component.translatable(
                                    JolCraftLanguageKeys.TOOLTIP_BOUNTY_TIER,
                                    Component.translatable(tier.langKey())
                            )
                            .withStyle(ChatFormatting.GRAY)
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        if (!LanguageAttachmentHelper.knowsDwarvish(player)) {
            super.appendHoverText(stack, context, tooltip, flag);
            return;
        }

        if (JolCraftProxy.access().isAltDown()) {
            BountyData data = getBountyDataOrNull(stack);

            if (data != null && data.objective() instanceof BountyData.BountyObjective.EntityObjective) {
                tooltip.add(Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY_ALT
                ).withStyle(ChatFormatting.GRAY));
                return;
            }

            DwarfProfession type = BountyRecipe.getType(stack);
            if (type != null) {
                tooltip.add(Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_BOUNTY_DWARF_PROFESSION,
                        Component.translatable(AbstractLanguageKeys.entity(type.getId()))
                ).withStyle(ChatFormatting.GRAY));
            }

            return;
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void appendInvalidLines(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (BountyRecipe.getType(stack) == null && BountyRecipe.getTier(stack) == null) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID)
                    .withStyle(ChatFormatting.RED));
        }
    }
}