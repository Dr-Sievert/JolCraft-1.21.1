package net.sievert.jolcraft.world.item.custom.container;

import com.google.common.collect.Iterables;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.datagen.language.subprovider.ContainerLangSubProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class StrongboxItem extends BlockItem {

    public StrongboxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            if (stack.has(JolCraftDataComponents.LOCKED)) {
                tooltip.add(Component.translatable(ContainerLangSubProvider.TOOLTIP_STRONGBOX_LOCKED)
                        .withStyle(ChatFormatting.RED));
            }

            if (stack.has(JolCraftDataComponents.LOOT_TABLE)) {
                tooltip.add(Component.translatable(ContainerLangSubProvider.TOOLTIP_STRONGBOX_LOOT)
                        .withStyle(ChatFormatting.GREEN));
            }

            ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
            if (contents != null && !Iterables.isEmpty(contents.nonEmptyItems())) {
                tooltip.add(Component.translatable(ContainerLangSubProvider.TOOLTIP_STRONGBOX_NOT_EMPTY)
                        .withStyle(ChatFormatting.GRAY));
            }

        super.appendHoverText(stack, ctx, tooltip, flag);
    }
}
