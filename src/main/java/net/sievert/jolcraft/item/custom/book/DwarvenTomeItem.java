package net.sievert.jolcraft.item.custom.book;

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
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.item.util.tooltip.TooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

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
            Player player = JolCraftProxy.access().getLocalPlayer();
            boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);

            if (JolCraftProxy.access().isAltDown()) {
                if (knowsLanguage) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.shift")
                            .withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.locked")
                            .withStyle(ChatFormatting.GRAY));
                }
            } else {
                if (knowsLanguage) {
                    String translationKey = LoreHelper.getEntryTranslationKey(stack, DwarfLoreKey.class);
                    tooltip.add(Component.translatable(
                                    translationKey != null ? translationKey : "tooltip.jolcraft.dwarven_tome.unlocked")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                } else {
                    tooltip.add(Component.translatable("tooltip.jolcraft.dwarven_tome.locked")
                            .withStyle(ChatFormatting.GRAY));
                }

                tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", TooltipHelper.altKey())
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
