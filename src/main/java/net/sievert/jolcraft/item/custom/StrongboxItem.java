package net.sievert.jolcraft.item.custom;

import com.google.common.collect.Iterables;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.sievert.jolcraft.data.JolCraftDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class StrongboxItem extends BlockItem {

    public StrongboxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            // Check if it is locked
            if (stack.has(JolCraftDataComponents.LOCKED)) {
                tooltip.add(Component.translatable("tooltip.jolcraft.strongbox.locked")
                        .withStyle(ChatFormatting.RED));
            }

            // Check if the Strongbox has a loot table
            if (stack.has(JolCraftDataComponents.LOOT_TABLE)) {
                tooltip.add(Component.translatable("tooltip.jolcraft.strongbox.loot")
                        .withStyle(ChatFormatting.GREEN));
            }

            // Check if the Strongbox has contents
            ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
            if (contents != null && !Iterables.isEmpty(contents.nonEmptyItems())) {
                tooltip.add(Component.translatable("tooltip.jolcraft.strongbox.not_empty")
                        .withStyle(ChatFormatting.GRAY));
            }

        // Call super to retain any default hover text functionality
        super.appendHoverText(stack, ctx, tooltip, flag);
    }






}
