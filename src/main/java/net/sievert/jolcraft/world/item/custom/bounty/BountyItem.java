package net.sievert.jolcraft.world.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.datagen.language.subprovider.BountyLangSubProvider;
import net.sievert.jolcraft.datagen.language.subprovider.DwarfLangSubProvider;
import net.sievert.jolcraft.datagen.language.subprovider.MiscLangSubProvider;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyTier;
import net.sievert.jolcraft.world.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.world.item.util.tooltip.TooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BountyItem extends Item {

    public BountyItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);
        BountyType type = BountyHelper.getBountyType(stack);
        BountyTier tier = BountyHelper.getBountyTier(stack);

        if (JolCraftProxy.access().isAltDown() && type != BountyType.UNKNOWN) {
            tooltip.add(Component.translatable("tooltip.jolcraft.bounty." + type)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            if (knowsLanguage) {
                if (type == BountyType.UNKNOWN || tier == BountyTier.UNKNOWN) {
                    tooltip.add(Component.translatable(BountyLangSubProvider.TOOLTIP_BOUNTY_INVALID)
                            .withStyle(ChatFormatting.RED));
                } else {
                    tooltip.add(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_BOUNTY_TYPE)
                                    .append(Component.translatable("entity.jolcraft.dwarf_" + type.getId()))
                                    .withStyle(ChatFormatting.GRAY)
                    );
                    tooltip.add(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_BOUNTY_TIER, tier.getDisplayName())
                                    .withStyle(ChatFormatting.GRAY)
                    );
                }
            } else {
                tooltip.add(Component.translatable(DwarfLangSubProvider.TOOLTIP_PARCHMENT_LOCKED)
                        .withStyle(ChatFormatting.GRAY));
            }
            if (type != BountyType.UNKNOWN) {
                tooltip.add(Component.translatable(MiscLangSubProvider.TOOLTIP_HOLD_KEY, TooltipHelper.altKey())
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
