package net.sievert.jolcraft.world.item.custom.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.client.tooltip.util.JolCraftTooltipHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SimpleTooltipBlockItem extends BlockItem {

    protected final String tooltipTranslationKey;
    protected final boolean useItemName;

    public SimpleTooltipBlockItem(Block block, Item.Properties properties, String tooltipTranslationKey) {
        this(block, properties, tooltipTranslationKey, false);
    }

    public SimpleTooltipBlockItem(Block block, Item.Properties properties, String tooltipTranslationKey, boolean useItemName) {
        super(block, properties);
        this.tooltipTranslationKey = tooltipTranslationKey;
        this.useItemName = useItemName;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return useItemName ? super.getOrCreateDescriptionId() : super.getDescriptionId();
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        JolCraftTooltipHelper.addAltTooltip(
                tooltip,
                Component.translatable(tooltipTranslationKey).withStyle(ChatFormatting.GRAY),
                List.of()
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }
}