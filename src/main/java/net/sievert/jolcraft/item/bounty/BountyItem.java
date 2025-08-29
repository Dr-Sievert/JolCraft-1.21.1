package net.sievert.jolcraft.item.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.util.dwarf.bounty.BountyHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BountyItem extends Item {
    public BountyItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvishClient(); // ✅ use helper
        String type = BountyHelper.getBountyType(stack);

        if (Screen.hasShiftDown()) {
            if (type.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty.no_type").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty." + type).withStyle(ChatFormatting.GRAY));
            }
        }
        else{
            if (knowsLanguage) {
                if (type.isEmpty()) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty.type.invalid").withStyle(ChatFormatting.RED));
                } else {
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty.type").append(Component.translatable("entity.jolcraft.dwarf_" + type)).withStyle(ChatFormatting.GRAY));
                }
                int tier = BountyHelper.getBountyTier(stack);
                if (tier <= 0) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty.tier.invalid").withStyle(ChatFormatting.RED));
                } else {
                    String tierName = switch (tier) {
                        case 1 -> "Novice";
                        case 2 -> "Apprentice";
                        case 3 -> "Journeyman";
                        case 4 -> "Expert";
                        case 5 -> "Master";
                        default -> "Unknown";
                    };
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty.tier", tierName).withStyle(ChatFormatting.GRAY));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty.locked").withStyle(ChatFormatting.GRAY));
            }
            Component shiftKey = Component.literal("Shift").withStyle(ChatFormatting.BLUE);
            tooltip.add(Component.translatable("tooltip.jolcraft.shift", shiftKey)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

}
