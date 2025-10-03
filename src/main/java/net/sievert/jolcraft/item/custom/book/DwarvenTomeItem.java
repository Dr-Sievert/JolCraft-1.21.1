package net.sievert.jolcraft.item.custom.book;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.util.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreEntries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarvenTomeItem extends Item {
    public DwarvenTomeItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (context.level() != null && Objects.requireNonNull(context.level()).isClientSide()) {
            boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvishClient();

            if (Screen.hasAltDown()) {
                if (knowsLanguage) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.shift").withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.locked").withStyle(ChatFormatting.GRAY));
                }
            } else {
                if (knowsLanguage) {
                    String text = LoreHelper.getEntryText(stack, DwarfLoreEntries.ALL);
                    if (text != null) {
                        tooltip.add(Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    } else {
                        tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.unlocked")
                                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    }
                }
                else {
                    tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.locked").withStyle(ChatFormatting.GRAY));
                }
                Component altKey = InputConstants.getKey(InputConstants.KEY_LALT, -1)
                        .getDisplayName().copy().withStyle(ChatFormatting.BLUE);
                tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", altKey)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
