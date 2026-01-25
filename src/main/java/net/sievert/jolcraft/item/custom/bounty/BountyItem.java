package net.sievert.jolcraft.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.custom.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyTier;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.item.util.tooltip.TooltipHelper;
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

        if (JolCraftProxy.access().isAltDown()) {
            if (type == BountyType.UNKNOWN) {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty.no_type")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty." + type)
                        .withStyle(ChatFormatting.GRAY));
            }
        } else {
            if (knowsLanguage) {
                if (type == BountyType.UNKNOWN) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty.type.invalid")
                            .withStyle(ChatFormatting.RED));
                } else {
                    tooltip.add(
                            Component.translatable("tooltip.jolcraft.bounty.type")
                                    .append(Component.translatable("entity.jolcraft.dwarf_" + type.getId()))
                                    .withStyle(ChatFormatting.GRAY)
                    );
                }

                BountyTier tier = BountyHelper.getBountyTier(stack);
                if (tier == BountyTier.UNKNOWN) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty.tier.invalid")
                            .withStyle(ChatFormatting.RED));
                } else {
                    tooltip.add(
                            Component.translatable("tooltip.jolcraft.bounty.tier", tier.getDisplayName())
                                    .withStyle(ChatFormatting.GRAY)
                    );
                }
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty.locked")
                        .withStyle(ChatFormatting.GRAY));
            }

            tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", TooltipHelper.altKey())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
